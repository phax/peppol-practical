/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.peppol.servlet;

import java.security.Security;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.system.SystemProperties;
import com.helger.commons.vendor.VendorInfo;
import com.helger.peppol.app.AppInternalErrorHandler;
import com.helger.peppol.app.AppSecurity;
import com.helger.peppol.app.AppSettings;
import com.helger.peppol.app.CPPApp;
import com.helger.peppol.app.ajax.CAjax;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.pub.InitializerPublic;
import com.helger.peppol.secure.InitializerSecure;
import com.helger.peppol.ui.AppCommonUI;
import com.helger.photon.basic.app.appid.CApplicationID;
import com.helger.photon.basic.app.locale.ILocaleManager;
import com.helger.photon.bootstrap3.servlet.AbstractWebAppListenerMultiAppBootstrap;
import com.helger.photon.core.ajax.IAjaxInvoker;
import com.helger.photon.core.app.context.LayoutExecutionContext;
import com.helger.photon.core.app.init.IApplicationInitializer;

/**
 * This listener is invoked during the servlet initiailization. This is
 * basically a ServletContextListener.
 *
 * @author Philip Helger
 */
public final class AppWebAppListener extends AbstractWebAppListenerMultiAppBootstrap <LayoutExecutionContext>
{
  @Override
  protected String getInitParameterDebug (@Nonnull final ServletContext aSC)
  {
    return AppSettings.getGlobalDebug ();
  }

  @Override
  protected String getInitParameterProduction (@Nonnull final ServletContext aSC)
  {
    return AppSettings.getGlobalProduction ();
  }

  @Override
  protected String getDataPath (@Nonnull final ServletContext aSC)
  {
    return AppSettings.getDataPath ();
  }

  @Override
  protected boolean shouldCheckFileAccess (@Nonnull final ServletContext aSC)
  {
    return AppSettings.isCheckFileAccess ();
  }

  @Override
  @Nonnull
  @Nonempty
  protected ICommonsMap <String, IApplicationInitializer <LayoutExecutionContext>> getAllInitializers ()
  {
    final ICommonsMap <String, IApplicationInitializer <LayoutExecutionContext>> ret = new CommonsHashMap <> ();
    ret.put (CApplicationID.APP_ID_SECURE, new InitializerSecure ());
    ret.put (CApplicationID.APP_ID_PUBLIC, new InitializerPublic ());
    return ret;
  }

  public static void setDNSCacheTime (final int nSeconds)
  {
    final String sValue = Integer.toString (nSeconds);
    Security.setProperty ("networkaddress.cache.ttl", sValue);
    Security.setProperty ("networkaddress.cache.negative.ttl", sValue);
    SystemProperties.setPropertyValue ("disableWSAddressCaching", nSeconds == 0);
  }

  @Override
  protected void initGlobalSettings ()
  {
    // Disable DNS caching
    setDNSCacheTime (0);

    // JUL to SLF4J
    SLF4JBridgeHandler.removeHandlersForRootLogger ();
    SLF4JBridgeHandler.install ();

    VendorInfo.setVendorName ("Philip Helger");
    VendorInfo.setVendorURL ("http://www.helger.com");
    VendorInfo.setVendorEmail ("philip@helger.com");
    VendorInfo.setVendorLocation ("Vienna, Austria");
    VendorInfo.setInceptionYear (2014);

    super.initGlobalSettings ();

    // UI stuff
    AppCommonUI.init ();

    // Set all security related stuff
    AppSecurity.init ();

    // Load managers
    PPMetaManager.getInstance ();

    // Setup error handler
    AppInternalErrorHandler.doSetup ();
  }

  @Override
  public void initLocales (@Nonnull final ILocaleManager aLocaleMgr)
  {
    aLocaleMgr.registerLocale (CPPApp.LOCALE_DE);
    aLocaleMgr.registerLocale (CPPApp.LOCALE_EN);
    aLocaleMgr.setDefaultLocale (CPPApp.DEFAULT_LOCALE);
  }

  @Override
  public void initAjax (@Nonnull final IAjaxInvoker aAjaxInvoker)
  {
    aAjaxInvoker.registerFunction (CAjax.DATATABLES);
    aAjaxInvoker.registerFunction (CAjax.DATATABLES_I18N);
    aAjaxInvoker.registerFunction (CAjax.LOGIN);
    aAjaxInvoker.registerFunction (CAjax.UPDATE_MENU_VIEW_PUB);
    aAjaxInvoker.registerFunction (CAjax.UPDATE_MENU_VIEW_SEC);
    aAjaxInvoker.registerFunction (CAjax.COMMENT_ADD);
    aAjaxInvoker.registerFunction (CAjax.COMMENT_CREATE_THREAD);
    aAjaxInvoker.registerFunction (CAjax.COMMENT_DELETE);
    aAjaxInvoker.registerFunction (CAjax.COMMENT_SHOW_INPUT);
  }
}
