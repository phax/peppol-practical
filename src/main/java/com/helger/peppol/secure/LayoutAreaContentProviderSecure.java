/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
package com.helger.peppol.secure;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.commons.url.ISimpleURL;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.IHCElement;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.textlevel.HCSpan;
import com.helger.html.hc.html.textlevel.HCStrong;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.AppHelper;
import com.helger.peppol.pub.LayoutAreaContentProviderPublic;
import com.helger.peppol.ui.AppCommonUI;
import com.helger.photon.app.url.LinkHelper;
import com.helger.photon.bootstrap4.CBootstrapCSS;
import com.helger.photon.bootstrap4.breadcrumb.BootstrapBreadcrumb;
import com.helger.photon.bootstrap4.breadcrumb.BootstrapBreadcrumbProvider;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.layout.BootstrapContainer;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbar;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbarToggleable;
import com.helger.photon.bootstrap4.uictrls.ext.BootstrapMenuItemRenderer;
import com.helger.photon.core.EPhotonCoreText;
import com.helger.photon.core.execcontext.LayoutExecutionContext;
import com.helger.photon.core.html.CLayout;
import com.helger.photon.core.servlet.AbstractPublicApplicationServlet;
import com.helger.photon.core.servlet.LogoutServlet;
import com.helger.photon.security.login.LoggedInUserManager;
import com.helger.photon.security.user.IUser;
import com.helger.photon.security.util.SecurityHelper;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

/**
 * The viewport renderer (menu + content area)
 *
 * @author Philip Helger
 */
public final class LayoutAreaContentProviderSecure
{
  private LayoutAreaContentProviderSecure ()
  {}

  @Nonnull
  private static IHCNode _getNavbar (@Nonnull final LayoutExecutionContext aLEC)
  {
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();

    final ISimpleURL aLinkToStartPage = aLEC.getLinkToMenuItem (aLEC.getMenuTree ().getDefaultMenuItemID ());

    final BootstrapNavbar aNavbar = new BootstrapNavbar ();
    aNavbar.addBrand (new HCNodeList ().addChild (new HCSpan ().addClass (AppCommonUI.CSS_CLASS_LOGO1)
                                                               .addChild (AppHelper.getApplicationTitle ()))
                                       .addChild (new HCSpan ().addClass (AppCommonUI.CSS_CLASS_LOGO2).addChild (" Administration")),
                      aLinkToStartPage);

    final BootstrapNavbarToggleable aToggleable = aNavbar.addAndReturnToggleable ();

    final IUser aUser = LoggedInUserManager.getInstance ().getCurrentUser ();
    aToggleable.addAndReturnText ()
               .addClass (CBootstrapCSS.ML_AUTO)
               .addClass (CBootstrapCSS.MX_2)
               .addChild ("Welcome ")
               .addChild (new HCStrong ().addChild (SecurityHelper.getUserDisplayName (aUser, aDisplayLocale)));

    aToggleable.addChild (new BootstrapButton ().setOnClick (LinkHelper.getURLWithContext (AbstractPublicApplicationServlet.SERVLET_DEFAULT_PATH))
                                                .addChild ("Public area")
                                                .addClass (CBootstrapCSS.MX_2));

    aToggleable.addChild (new BootstrapButton ().setOnClick (LinkHelper.getURLWithContext (aRequestScope,
                                                                                           LogoutServlet.SERVLET_DEFAULT_PATH))
                                                .addChild (EPhotonCoreText.LOGIN_LOGOUT.getDisplayText (aDisplayLocale))
                                                .addClass (CBootstrapCSS.MX_2));

    return aNavbar;
  }

  @Nonnull
  public static IHCElement <?> getMenuContent (@Nonnull final LayoutExecutionContext aLEC)
  {
    final IHCElement <?> ret = BootstrapMenuItemRenderer.createSideBarMenu (aLEC);
    return ret;
  }

  @Nonnull
  public static IHCNode getContent (@Nonnull final LayoutExecutionContext aLEC)
  {
    final HCNodeList ret = new HCNodeList ();

    // Header
    ret.addChild (_getNavbar (aLEC));

    final BootstrapContainer aOuterContainer = ret.addAndReturnChild (new BootstrapContainer ().setFluid (true));

    // Breadcrumbs
    {
      final BootstrapBreadcrumb aBreadcrumbs = BootstrapBreadcrumbProvider.createBreadcrumb (aLEC);
      aBreadcrumbs.addClasses (CBootstrapCSS.D_NONE, CBootstrapCSS.D_SM_BLOCK);
      aOuterContainer.addChild (aBreadcrumbs);
    }

    // Content
    {
      final HCDiv aRow = aOuterContainer.addAndReturnChild (new HCDiv ().addClass (CBootstrapCSS.D_MD_FLEX));
      final HCDiv aCol1 = aRow.addAndReturnChild (new HCDiv ().addClass (CBootstrapCSS.D_MD_FLEX).addClass (CBootstrapCSS.MR_2));
      final HCDiv aCol2 = aRow.addAndReturnChild (new HCDiv ().addClass (CBootstrapCSS.FLEX_FILL));

      // left
      // We need a wrapper span for easy AJAX content replacement
      aCol1.addClass (CBootstrapCSS.D_PRINT_NONE)
           .addChild (new HCSpan ().setID (CLayout.LAYOUT_AREAID_MENU).addChild (getMenuContent (aLEC)));
      aCol1.addChild (new HCDiv ().setID (CLayout.LAYOUT_AREAID_SPECIAL));

      // content - determine is exactly same as for view
      aCol2.addChild (LayoutAreaContentProviderPublic.getPageContent (aLEC));
    }

    return ret;
  }
}
