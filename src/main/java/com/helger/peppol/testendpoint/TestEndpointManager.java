/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.peppol.testendpoint;

import java.util.function.Predicate;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.CommonsArrayList;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsMap;
import com.helger.collection.helper.CollectionSort;
import com.helger.dao.DAOException;
import com.helger.peppol.smp.ISMPTransportProfile;
import com.helger.peppol.ui.types.smlconfig.ISMLConfiguration;
import com.helger.photon.audit.AuditHelper;
import com.helger.photon.io.dao.AbstractPhotonSimpleDAO;
import com.helger.photon.security.object.BusinessObjectHelper;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.convert.MicroTypeConverter;

import jakarta.annotation.Nonnull;

public final class TestEndpointManager extends AbstractPhotonSimpleDAO
{
  private static final String ELEMENT_ROOT = "testendpoints";
  private static final String ELEMENT_ITEM = "testendpoint";

  private final ICommonsMap <String, TestEndpoint> m_aMap = new CommonsHashMap <> ();

  public TestEndpointManager (@NonNull @Nonempty final String sFilename) throws DAOException
  {
    super (sFilename);
    initialRead ();
  }

  private void _addTestEndpoint (@NonNull final TestEndpoint aTestEndpoint)
  {
    ValueEnforcer.notNull (aTestEndpoint, "TestEndpoint");

    final String sTestEndpointID = aTestEndpoint.getID ();
    if (m_aMap.containsKey (sTestEndpointID))
      throw new IllegalArgumentException ("TestEndpoint ID '" + sTestEndpointID + "' is already in use!");
    m_aMap.put (sTestEndpointID, aTestEndpoint);
  }

  @Override
  @NonNull
  protected EChange onRead (@NonNull final IMicroDocument aDoc)
  {
    for (final IMicroElement eTestEndpoint : aDoc.getDocumentElement ().getAllChildElements (ELEMENT_ITEM))
    {
      _addTestEndpoint (MicroTypeConverter.convertToNative (eTestEndpoint, TestEndpoint.class));
    }
    return EChange.UNCHANGED;
  }

  @Override
  @NonNull
  protected IMicroDocument createWriteData ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.addElement (ELEMENT_ROOT);
    for (final TestEndpoint aTestEndpoint : CollectionSort.getSortedByKey (m_aMap).values ())
      eRoot.addChild (MicroTypeConverter.convertToMicroElement (aTestEndpoint, ELEMENT_ITEM));
    return aDoc;
  }

  @NonNull
  public TestEndpoint createTestEndpoint (@NonNull @Nonempty final String sCompanyName,
                                          @Nullable final String sContactPerson,
                                          @NonNull @Nonempty final String sParticipantIDScheme,
                                          @NonNull @Nonempty final String sParticipantIDValue,
                                          @NonNull final ISMPTransportProfile aTransportProfile,
                                          @NonNull final ISMLConfiguration aSML)
  {
    final TestEndpoint aTestEndpoint = new TestEndpoint (sCompanyName,
                                                         sContactPerson,
                                                         sParticipantIDScheme,
                                                         sParticipantIDValue,
                                                         aTransportProfile,
                                                         aSML);

    m_aRWLock.writeLock ().lock ();
    try
    {
      _addTestEndpoint (aTestEndpoint);
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditCreateSuccess (TestEndpoint.OT,
                                      aTestEndpoint.getID (),
                                      sCompanyName,
                                      sContactPerson,
                                      sParticipantIDScheme,
                                      sParticipantIDValue,
                                      aTransportProfile,
                                      aSML);
    return aTestEndpoint;
  }

  @NonNull
  public EChange updateTestEndpoint (@Nullable final String sTestEndpointID,
                                     @NonNull @Nonempty final String sCompanyName,
                                     @Nullable final String sContactPerson,
                                     @NonNull @Nonempty final String sParticipantIDScheme,
                                     @NonNull @Nonempty final String sParticipantIDValue,
                                     @NonNull final ISMPTransportProfile aTransportProfile,
                                     @NonNull final ISMLConfiguration aSML)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      final TestEndpoint aTestEndpoint = m_aMap.get (sTestEndpointID);
      if (aTestEndpoint == null)
      {
        AuditHelper.onAuditModifyFailure (TestEndpoint.OT, sTestEndpointID, "no-such-id");
        return EChange.UNCHANGED;
      }

      EChange eChange = EChange.UNCHANGED;
      // client ID cannot be changed!
      eChange = eChange.or (aTestEndpoint.setCompanyName (sCompanyName));
      eChange = eChange.or (aTestEndpoint.setContactPerson (sContactPerson));
      eChange = eChange.or (aTestEndpoint.setParticipantIDIssuer (sParticipantIDScheme));
      eChange = eChange.or (aTestEndpoint.setParticipantIDValue (sParticipantIDValue));
      eChange = eChange.or (aTestEndpoint.setTransportProfile (aTransportProfile));
      eChange = eChange.or (aTestEndpoint.setSML (aSML));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;

      BusinessObjectHelper.setLastModificationNow (aTestEndpoint);
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditModifySuccess (TestEndpoint.OT,
                                      sTestEndpointID,
                                      sCompanyName,
                                      sContactPerson,
                                      sParticipantIDScheme,
                                      sParticipantIDValue,
                                      aTransportProfile,
                                      aSML);
    return EChange.CHANGED;
  }

  @NonNull
  public EChange deleteTestEndpoint (@Nullable final String sTestEndpointID)
  {
    final TestEndpoint aTestEndpoint = getTestEndpointOfID (sTestEndpointID);
    if (aTestEndpoint == null)
    {
      AuditHelper.onAuditDeleteFailure (TestEndpoint.OT, "no-such-id", sTestEndpointID);
      return EChange.UNCHANGED;
    }

    m_aRWLock.writeLock ().lock ();
    try
    {
      if (BusinessObjectHelper.setDeletionNow (aTestEndpoint).isUnchanged ())
      {
        AuditHelper.onAuditDeleteFailure (TestEndpoint.OT, "already-deleted", aTestEndpoint.getID ());
        return EChange.UNCHANGED;
      }
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditDeleteSuccess (TestEndpoint.OT, aTestEndpoint.getID ());
    return EChange.CHANGED;
  }

  @NonNull
  public EChange undeleteTestEndpoint (@Nullable final String sTestEndpointID)
  {
    final TestEndpoint aTestEndpoint = getTestEndpointOfID (sTestEndpointID);
    if (aTestEndpoint == null)
    {
      AuditHelper.onAuditUndeleteFailure (TestEndpoint.OT, "no-such-id", sTestEndpointID);
      return EChange.UNCHANGED;
    }
    if (!aTestEndpoint.isDeleted ())
    {
      AuditHelper.onAuditUndeleteFailure (TestEndpoint.OT, "not-deleted", sTestEndpointID);
      return EChange.UNCHANGED;
    }

    m_aRWLock.writeLock ().lock ();
    try
    {
      if (BusinessObjectHelper.setUndeletionNow (aTestEndpoint).isUnchanged ())
      {
        AuditHelper.onAuditUndeleteFailure (TestEndpoint.OT, "already-undeleted", aTestEndpoint.getID ());
        return EChange.UNCHANGED;
      }
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditUndeleteSuccess (TestEndpoint.OT, aTestEndpoint.getID ());
    return EChange.CHANGED;
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <TestEndpoint> getAllTestEndpoints (@Nonnull final Predicate <? super TestEndpoint> aFilter)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      final ICommonsList <TestEndpoint> ret = new CommonsArrayList <> ();
      for (final TestEndpoint aItem : m_aMap.values ())
        if (aFilter.test (aItem))
          ret.add (aItem);
      return ret;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <TestEndpoint> getAllTestEndpoints ()
  {
    return getAllTestEndpoints (x -> true);
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <TestEndpoint> getAllActiveTestEndpoints ()
  {
    return getAllTestEndpoints (TestEndpoint::isNotDeleted);
  }

  @Nullable
  public TestEndpoint getTestEndpointOfID (@Nullable final String sID)
  {
    if (StringHelper.isEmpty (sID))
      return null;

    return m_aRWLock.readLockedGet ( () -> m_aMap.get (sID));
  }

  public boolean containsTestEndpointWithID (@Nullable final String sID)
  {
    if (StringHelper.isEmpty (sID))
      return false;

    return m_aRWLock.readLockedBoolean ( () -> m_aMap.containsKey (sID));
  }

  @Nullable
  public TestEndpoint getTestEndpoint (@Nullable final String sParticipantIDIssuer,
                                       @Nullable final String sParticipantIDValue,
                                       @Nullable final ISMPTransportProfile aTransportProfile)
  {
    if (StringHelper.isNotEmpty (sParticipantIDIssuer) &&
        StringHelper.isNotEmpty (sParticipantIDValue) &&
        aTransportProfile != null)
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        for (final TestEndpoint aTestEndpoint : m_aMap.values ())
          if (aTestEndpoint.hasSameIdentifier (sParticipantIDIssuer, sParticipantIDValue, aTransportProfile))
            return aTestEndpoint;
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }
    return null;
  }

  public boolean containsTestEndpoint (@Nullable final String sParticipantIDIssuer,
                                       @Nullable final String sParticipantIDValue,
                                       @Nullable final ISMPTransportProfile aTransportProfile)
  {
    return getTestEndpoint (sParticipantIDIssuer, sParticipantIDValue, aTransportProfile) != null;
  }
}
