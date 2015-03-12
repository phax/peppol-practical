/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.peppol.testep.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.appbasics.app.dao.impl.AbstractSimpleDAO;
import com.helger.appbasics.app.dao.impl.DAOException;
import com.helger.appbasics.security.audit.AuditUtils;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.CollectionHelper;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.impl.MicroDocument;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;

import eu.europa.ec.cipa.smp.client.ESMPTransportProfile;

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
                                          @Nonnull final ESMPTransportProfile eTransportProfile)
  {
    final TestEndpoint aTestEndpoint = new TestEndpoint (sCompanyName,
                                                         sContactPerson,
                                                         sParticipantIDScheme,
                                                         sParticipantIDValue,
                                                         eTransportProfile);

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
    AuditUtils.onAuditCreateSuccess (TestEndpoint.TYPE_TEST_ENDPOINT,
                                     aTestEndpoint.getID (),
                                     sCompanyName,
                                     sContactPerson,
                                     sParticipantIDScheme,
                                     sParticipantIDValue,
                                     eTransportProfile);
    return aTestEndpoint;
  }

  @Nonnull
  public EChange updateTestEndpoint (@Nullable final String sTestEndpointID,
                                     @Nonnull @Nonempty final String sCompanyName,
                                     @Nullable final String sContactPerson,
                                     @Nonnull @Nonempty final String sParticipantIDScheme,
                                     @Nonnull @Nonempty final String sParticipantIDValue,
                                     @Nonnull final ESMPTransportProfile eTransportProfile)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      final TestEndpoint aTestEndpoint = m_aMap.get (sTestEndpointID);
      if (aTestEndpoint == null)
      {
        AuditUtils.onAuditModifyFailure (TestEndpoint.TYPE_TEST_ENDPOINT, sTestEndpointID, "no-such-id");
        return EChange.UNCHANGED;
      }

      EChange eChange = EChange.UNCHANGED;
      // client ID cannot be changed!
      eChange = eChange.or (aTestEndpoint.setCompanyName (sCompanyName));
      eChange = eChange.or (aTestEndpoint.setContactPerson (sContactPerson));
      eChange = eChange.or (aTestEndpoint.setParticipantIDScheme (sParticipantIDScheme));
      eChange = eChange.or (aTestEndpoint.setParticipantIDValue (sParticipantIDValue));
      eChange = eChange.or (aTestEndpoint.setTransportProfile (eTransportProfile));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;

      aTestEndpoint.setLastModificationNow ();
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditUtils.onAuditModifySuccess (TestEndpoint.TYPE_TEST_ENDPOINT,
                                     sTestEndpointID,
                                     sCompanyName,
                                     sContactPerson,
                                     sParticipantIDScheme,
                                     sParticipantIDValue,
                                     eTransportProfile);
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <? extends TestEndpoint> getAllTestEndpoints ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return CollectionHelper.newList (m_aMap.values ());
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
                                       @Nullable final ESMPTransportProfile eTransportProfile)
  {
    if (StringHelper.hasText (sParticipantIDScheme) &&
        StringHelper.hasText (sParticipantIDValue) &&
        eTransportProfile != null)
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        for (final TestEndpoint aTestEndpoint : m_aMap.values ())
          if (aTestEndpoint.hasSameIdentifier (sParticipantIDScheme, sParticipantIDValue, eTransportProfile))
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
                                       @Nullable final ESMPTransportProfile eTransportProfile)
  {
    return getTestEndpoint (sParticipantIDScheme, sParticipantIDValue, eTransportProfile) != null;
  }
}
