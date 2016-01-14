/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.peppol.pub.testendpoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroDocument;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.smp.ISMPTransportProfile;
import com.helger.photon.basic.app.dao.impl.AbstractSimpleDAO;
import com.helger.photon.basic.app.dao.impl.DAOException;
import com.helger.photon.basic.audit.AuditHelper;
import com.helger.photon.security.object.ObjectHelper;

public final class TestEndpointManager extends AbstractSimpleDAO
{
  private static final String ELEMENT_ROOT = "testendpoints";
  private static final String ELEMENT_ITEM = "testendpoint";

  private final Map <String, TestEndpoint> m_aMap = new HashMap <String, TestEndpoint> ();

  public TestEndpointManager (@Nonnull @Nonempty final String sFilename) throws DAOException
  {
    super (sFilename);
    initialRead ();
  }

  @Override
  @Nonnull
  protected EChange onRead (@Nonnull final IMicroDocument aDoc)
  {
    for (final IMicroElement eTestEndpoint : aDoc.getDocumentElement ().getAllChildElements (ELEMENT_ITEM))
      _addTestEndpoint (MicroTypeConverter.convertToNative (eTestEndpoint, TestEndpoint.class));
    return EChange.UNCHANGED;
  }

  @Override
  @Nonnull
  protected IMicroDocument createWriteData ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement (ELEMENT_ROOT);
    for (final TestEndpoint aTestEndpoint : CollectionHelper.getSortedByKey (m_aMap).values ())
      eRoot.appendChild (MicroTypeConverter.convertToMicroElement (aTestEndpoint, ELEMENT_ITEM));
    return aDoc;
  }

  private void _addTestEndpoint (@Nonnull final TestEndpoint aTestEndpoint)
  {
    ValueEnforcer.notNull (aTestEndpoint, "TestEndpoint");

    final String sTestEndpointID = aTestEndpoint.getID ();
    if (m_aMap.containsKey (sTestEndpointID))
      throw new IllegalArgumentException ("TestEndpoint ID '" + sTestEndpointID + "' is already in use!");
    m_aMap.put (sTestEndpointID, aTestEndpoint);
  }

  @Nonnull
  public TestEndpoint createTestEndpoint (@Nonnull @Nonempty final String sCompanyName,
                                          @Nullable final String sContactPerson,
                                          @Nonnull @Nonempty final String sParticipantIDScheme,
                                          @Nonnull @Nonempty final String sParticipantIDValue,
                                          @Nonnull final ISMPTransportProfile aTransportProfile,
                                          @Nonnull final ESML eSML)
  {
    final TestEndpoint aTestEndpoint = new TestEndpoint (sCompanyName,
                                                         sContactPerson,
                                                         sParticipantIDScheme,
                                                         sParticipantIDValue,
                                                         aTransportProfile,
                                                         eSML);

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
                                      eSML);
    return aTestEndpoint;
  }

  @Nonnull
  public EChange updateTestEndpoint (@Nullable final String sTestEndpointID,
                                     @Nonnull @Nonempty final String sCompanyName,
                                     @Nullable final String sContactPerson,
                                     @Nonnull @Nonempty final String sParticipantIDScheme,
                                     @Nonnull @Nonempty final String sParticipantIDValue,
                                     @Nonnull final ISMPTransportProfile aTransportProfile,
                                     @Nonnull final ESML eSML)
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
      eChange = eChange.or (aTestEndpoint.setParticipantIDScheme (sParticipantIDScheme));
      eChange = eChange.or (aTestEndpoint.setParticipantIDValue (sParticipantIDValue));
      eChange = eChange.or (aTestEndpoint.setTransportProfile (aTransportProfile));
      eChange = eChange.or (aTestEndpoint.setSML (eSML));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;

      ObjectHelper.setLastModificationNow (aTestEndpoint);
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
                                      eSML);
    return EChange.CHANGED;
  }

  @Nonnull
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
      if (ObjectHelper.setDeletionNow (aTestEndpoint).isUnchanged ())
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

  @Nonnull
  @ReturnsMutableCopy
  public Collection <? extends TestEndpoint> getAllActiveTestEndpoints ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      final List <TestEndpoint> ret = new ArrayList <> ();
      for (final TestEndpoint aItem : m_aMap.values ())
        if (!aItem.isDeleted ())
          ret.add (aItem);
      return ret;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public TestEndpoint getTestEndpointOfID (@Nullable final String sID)
  {
    if (StringHelper.hasNoText (sID))
      return null;

    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMap.get (sID);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public boolean containsTestEndpointWithID (@Nullable final String sID)
  {
    if (StringHelper.hasNoText (sID))
      return false;

    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMap.containsKey (sID);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public TestEndpoint getTestEndpoint (@Nullable final String sParticipantIDScheme,
                                       @Nullable final String sParticipantIDValue,
                                       @Nullable final ISMPTransportProfile aTransportProfile)
  {
    if (StringHelper.hasText (sParticipantIDScheme) &&
        StringHelper.hasText (sParticipantIDValue) &&
        aTransportProfile != null)
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        for (final TestEndpoint aTestEndpoint : m_aMap.values ())
          if (aTestEndpoint.hasSameIdentifier (sParticipantIDScheme, sParticipantIDValue, aTransportProfile))
            return aTestEndpoint;
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }
    return null;
  }

  public boolean containsTestEndpoint (@Nullable final String sParticipantIDScheme,
                                       @Nullable final String sParticipantIDValue,
                                       @Nullable final ISMPTransportProfile aTransportProfile)
  {
    return getTestEndpoint (sParticipantIDScheme, sParticipantIDValue, aTransportProfile) != null;
  }
}
