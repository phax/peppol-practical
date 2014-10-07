/**
 * Copyright (C) 2012-2014 WineNet - www.winenet.at
 * All Rights Reserved
 *
 * This file is part of the WineNet software.
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is
 * strictly prohibited.
 */
package com.helger.peppol.mgr;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.appbasics.app.dao.impl.DAOException;
import com.helger.commons.annotations.UsedViaReflection;
import com.helger.commons.exceptions.InitializationException;
import com.helger.peppol.crm.CRMGroupManager;
import com.helger.peppol.crm.CRMSubscriberManager;
import com.helger.scopes.singleton.GlobalSingleton;

/**
 * Central manager for all sub managers
 *
 * @author Philip Helger
 */
public final class MetaManager extends GlobalSingleton
{
  public static final String CRMGROUP_XML = "crm/group.xml";
  public static final String CRMSUBSCRIBER_XML = "crm/subscriber.xml";

  private static final Logger s_aLogger = LoggerFactory.getLogger (MetaManager.class);

  private CRMGroupManager m_aCRMGroupMgr;
  private CRMSubscriberManager m_aCRMSubscriberMgr;

  @Deprecated
  @UsedViaReflection
  public MetaManager ()
  {}

  @Override
  protected void onAfterInstantiation ()
  {
    try
    {
      m_aCRMGroupMgr = new CRMGroupManager (CRMGROUP_XML);
      m_aCRMSubscriberMgr = new CRMSubscriberManager (CRMSUBSCRIBER_XML);

      s_aLogger.info ("MetaManager was initialized");
    }
    catch (final DAOException ex)
    {
      throw new InitializationException ("Failed to init MetaManager", ex);
    }
  }

  @Nonnull
  public static MetaManager getInstance ()
  {
    return getGlobalSingleton (MetaManager.class);
  }

  @Nonnull
  public static CRMGroupManager getCRMGroupMgr ()
  {
    return getInstance ().m_aCRMGroupMgr;
  }

  @Nonnull
  public static CRMSubscriberManager getCRMSubscriberMgr ()
  {
    return getInstance ().m_aCRMSubscriberMgr;
  }
}
