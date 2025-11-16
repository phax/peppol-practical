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
package com.helger.peppol.app.mgr;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.style.UsedViaReflection;
import com.helger.base.exception.InitializationException;
import com.helger.dao.DAOException;
import com.helger.peppol.crm.CRMGroupManager;
import com.helger.peppol.crm.CRMSubscriberManager;
import com.helger.peppol.testendpoint.TestEndpointManager;
import com.helger.scope.IScope;
import com.helger.scope.singleton.AbstractGlobalSingleton;

/**
 * Central manager for all sub managers
 *
 * @author Philip Helger
 */
public final class PPMetaManager extends AbstractGlobalSingleton
{
  public static final String CRMGROUP_XML = "crm/group.xml";
  public static final String CRMSUBSCRIBER_XML = "crm/subscriber.xml";
  public static final String TEST_ENDPOINT_XML = "test-endpoint.xml";

  private static final Logger LOGGER = LoggerFactory.getLogger (PPMetaManager.class);

  private CRMGroupManager m_aCRMGroupMgr;
  private CRMSubscriberManager m_aCRMSubscriberMgr;
  private TestEndpointManager m_aTestEndpointMgr;

  @Deprecated
  @UsedViaReflection
  public PPMetaManager ()
  {}

  @Override
  protected void onAfterInstantiation (@NonNull final IScope aScope)
  {
    try
    {
      m_aCRMGroupMgr = new CRMGroupManager (CRMGROUP_XML);
      m_aCRMSubscriberMgr = new CRMSubscriberManager (CRMSUBSCRIBER_XML);
      m_aTestEndpointMgr = new TestEndpointManager (TEST_ENDPOINT_XML);

      LOGGER.info (getClass ().getName () + " was initialized");
    }
    catch (final DAOException ex)
    {
      throw new InitializationException ("Failed to init " + getClass ().getName (), ex);
    }
  }

  @NonNull
  public static PPMetaManager getInstance ()
  {
    return getGlobalSingleton (PPMetaManager.class);
  }

  @NonNull
  public static CRMGroupManager getCRMGroupMgr ()
  {
    return getInstance ().m_aCRMGroupMgr;
  }

  @NonNull
  public static CRMSubscriberManager getCRMSubscriberMgr ()
  {
    return getInstance ().m_aCRMSubscriberMgr;
  }

  @NonNull
  public static TestEndpointManager getTestEndpointMgr ()
  {
    return getInstance ().m_aTestEndpointMgr;
  }
}
