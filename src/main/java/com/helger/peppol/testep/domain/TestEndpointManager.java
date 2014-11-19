/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.appbasics.app.dao.impl.AbstractSimpleDAO;
import com.helger.appbasics.app.dao.impl.DAOException;
import com.helger.appbasics.security.audit.AuditUtils;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.impl.MicroDocument;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;

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
    for (final TestEndpoint aTestEndpoint : ContainerHelper.getSortedByKey (m_aMap).values ())
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
                                          @Nonnull @Nonempty final String sParticipantIDValue,
                                          @Nonnull final List <TestEndpointData> aDatas,
                                          @Nullable final String sContactPerson)
  {
    final TestEndpoint aTestEndpoint = new TestEndpoint (sCompanyName, sContactPerson, sParticipantIDValue, aDatas);

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
                                     sParticipantIDValue,
                                     aDatas,
                                     sContactPerson);
    return aTestEndpoint;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <? extends TestEndpoint> getAllTestEndpoints ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newList (m_aMap.values ());
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

  public boolean containsTestEndpoint (@Nonnull @Nonempty final String sParticipantIDValue,
                                       @Nullable final TestEndpointData aData)
  {
    if (StringHelper.hasText (sParticipantIDValue) && aData != null)
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        for (final TestEndpoint aTestEndpoint : m_aMap.values ())
          if (aTestEndpoint.hasSameIdentifier (sParticipantIDValue, aData))
            return true;
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }
    return false;
  }
}
