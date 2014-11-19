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
package com.helger.peppol.app.ui;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.appbasics.security.login.LoggedInUserManager;
import com.helger.appbasics.security.user.IUser;
import com.helger.appbasics.security.util.SecurityUtils;
import com.helger.bootstrap3.CBootstrapCSS;
import com.helger.bootstrap3.base.BootstrapContainer;
import com.helger.bootstrap3.breadcrumbs.BootstrapBreadcrumbs;
import com.helger.bootstrap3.breadcrumbs.BootstrapBreadcrumbsProvider;
import com.helger.bootstrap3.button.BootstrapButton;
import com.helger.bootstrap3.ext.BootstrapMenuItemRenderer;
import com.helger.bootstrap3.grid.BootstrapRow;
import com.helger.bootstrap3.nav.BootstrapNav;
import com.helger.bootstrap3.navbar.BootstrapNavbar;
import com.helger.bootstrap3.navbar.EBootstrapNavbarPosition;
import com.helger.bootstrap3.navbar.EBootstrapNavbarType;
import com.helger.commons.url.ISimpleURL;
import com.helger.html.hc.IHCElement;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCForm;
import com.helger.html.hc.html.HCHead;
import com.helger.html.hc.html.HCSpan;
import com.helger.html.hc.html.HCStrong;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.CApp;
import com.helger.webbasics.EWebBasicsText;
import com.helger.webbasics.app.LinkUtils;
import com.helger.webbasics.app.layout.CLayout;
import com.helger.webbasics.app.layout.ILayoutAreaContentProvider;
import com.helger.webbasics.app.layout.LayoutExecutionContext;
import com.helger.webbasics.servlet.AbstractPublicApplicationServlet;
import com.helger.webbasics.servlet.LogoutServlet;
import com.helger.webscopes.domain.IRequestWebScopeWithoutResponse;

/**
 * The viewport renderer (menu + content area)
 *
 * @author Philip Helger
 */
public final class LayoutAreaContentProviderSecure implements ILayoutAreaContentProvider <LayoutExecutionContext>
{
  @Nonnull
  private static IHCNode _getNavbar (@Nonnull final LayoutExecutionContext aLEC)
  {
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();

    final ISimpleURL aLinkToStartPage = aLEC.getLinkToMenuItem (aLEC.getMenuTree ().getDefaultMenuItemID ());

    final BootstrapNavbar aNavbar = new BootstrapNavbar (EBootstrapNavbarType.STATIC_TOP, true, aDisplayLocale);
    aNavbar.addBrand (new HCNodeList ().addChild (new HCSpan ().addClass (CAppCSS.CSS_CLASS_LOGO1)
                                                               .addChild (CApp.getApplicationTitle ()))
                                       .addChild (new HCSpan ().addClass (CAppCSS.CSS_CLASS_LOGO2)
                                                               .addChild (" Administration")),
                      aLinkToStartPage);

    final BootstrapNav aNav = new BootstrapNav ();
    final IUser aUser = LoggedInUserManager.getInstance ().getCurrentUser ();
    aNav.addItem (new HCSpan ().addChild ("Logged in as ")
                               .addClass (CBootstrapCSS.NAVBAR_TEXT)
                               .addChild (new HCStrong ().addChild (SecurityUtils.getUserDisplayName (aUser,
                                                                                                      aDisplayLocale))));
    {
      final HCForm aForm = new HCForm ().addClass (CBootstrapCSS.NAVBAR_FORM);
      aForm.addChild (new BootstrapButton ().setOnClick (LinkUtils.getURLWithContext (AbstractPublicApplicationServlet.SERVLET_DEFAULT_PATH))
                                            .addChild ("Public area"));
      aNav.addItem (aForm);
    }
    {
      final HCForm aForm = new HCForm ().addClass (CBootstrapCSS.NAVBAR_FORM);
      aForm.addChild (new BootstrapButton ().setOnClick (LinkUtils.getURLWithContext (aRequestScope,
                                                                                      LogoutServlet.SERVLET_DEFAULT_PATH))
                                            .addChild (EWebBasicsText.LOGIN_LOGOUT.getDisplayText (aDisplayLocale)));
      aNav.addItem (aForm);
    }
    aNavbar.addNav (EBootstrapNavbarPosition.COLLAPSIBLE_RIGHT, aNav);

    return aNavbar;
  }

  @Nonnull
  public static IHCElement <?> getMenuContent (@Nonnull final LayoutExecutionContext aLEC)
  {
    final IHCElement <?> ret = BootstrapMenuItemRenderer.createSideBarMenu (aLEC);
    return ret;
  }

  @Nonnull
  public IHCNode getContent (@Nonnull final LayoutExecutionContext aLEC, @Nonnull final HCHead aHead)
  {
    final HCNodeList ret = new HCNodeList ();

    // Header
    ret.addChild (_getNavbar (aLEC));

    final BootstrapContainer aOuterContainer = ret.addAndReturnChild (new BootstrapContainer ());

    // Breadcrumbs
    {
      final BootstrapBreadcrumbs aBreadcrumbs = BootstrapBreadcrumbsProvider.createBreadcrumbs (aLEC);
      aBreadcrumbs.addClass (CBootstrapCSS.HIDDEN_XS);
      aOuterContainer.addChild (aBreadcrumbs);
    }

    // Content
    {
      final BootstrapRow aRow = aOuterContainer.addAndReturnChild (new BootstrapRow ());
      final HCDiv aCol1 = aRow.createColumn (12, 4, 4, 3);
      final HCDiv aCol2 = aRow.createColumn (12, 8, 8, 9);

      // left
      // We need a wrapper span for easy AJAX content replacement
      aCol1.addChild (new HCSpan ().setID (CLayout.LAYOUT_AREAID_MENU).addChild (getMenuContent (aLEC)));
      aCol1.addChild (new HCDiv ().setID (CLayout.LAYOUT_AREAID_SPECIAL));

      // content - determine is exactly same as for view
      aCol2.addChild (LayoutAreaContentProviderPublic._getMainContent (aLEC, aHead));
    }

    return ret;
  }
}
