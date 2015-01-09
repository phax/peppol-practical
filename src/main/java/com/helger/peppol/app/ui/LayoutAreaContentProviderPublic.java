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
package com.helger.peppol.app.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.appbasics.app.menu.ApplicationMenuTree;
import com.helger.appbasics.app.menu.IMenuItemExternal;
import com.helger.appbasics.app.menu.IMenuItemPage;
import com.helger.appbasics.app.menu.IMenuObject;
import com.helger.appbasics.app.menu.IMenuSeparator;
import com.helger.appbasics.app.menu.IMenuTree;
import com.helger.appbasics.app.menu.MenuItemDeterminatorCallback;
import com.helger.appbasics.security.AccessManager;
import com.helger.appbasics.security.login.LoggedInUserManager;
import com.helger.appbasics.security.user.IUser;
import com.helger.appbasics.security.util.SecurityUtils;
import com.helger.bootstrap3.CBootstrapCSS;
import com.helger.bootstrap3.alert.BootstrapErrorBox;
import com.helger.bootstrap3.base.BootstrapContainer;
import com.helger.bootstrap3.base.BootstrapContainerFluid;
import com.helger.bootstrap3.breadcrumbs.BootstrapBreadcrumbs;
import com.helger.bootstrap3.breadcrumbs.BootstrapBreadcrumbsProvider;
import com.helger.bootstrap3.button.BootstrapButton;
import com.helger.bootstrap3.button.EBootstrapButtonType;
import com.helger.bootstrap3.dropdown.BootstrapDropdownMenu;
import com.helger.bootstrap3.ext.BootstrapMenuItemRenderer;
import com.helger.bootstrap3.ext.BootstrapMenuItemRendererHorz;
import com.helger.bootstrap3.grid.BootstrapRow;
import com.helger.bootstrap3.nav.BootstrapNav;
import com.helger.bootstrap3.navbar.BootstrapNavbar;
import com.helger.bootstrap3.navbar.EBootstrapNavbarPosition;
import com.helger.bootstrap3.navbar.EBootstrapNavbarType;
import com.helger.bootstrap3.pageheader.BootstrapPageHeader;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.math.MathHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.css.property.CCSSProperties;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.css.ICSSClassProvider;
import com.helger.html.hc.IHCElement;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.api.EHCInputType;
import com.helger.html.hc.html.HCA;
import com.helger.html.hc.html.HCCol;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCForm;
import com.helger.html.hc.html.HCH1;
import com.helger.html.hc.html.HCHead;
import com.helger.html.hc.html.HCHiddenField;
import com.helger.html.hc.html.HCImg;
import com.helger.html.hc.html.HCInput;
import com.helger.html.hc.html.HCP;
import com.helger.html.hc.html.HCRow;
import com.helger.html.hc.html.HCSpan;
import com.helger.html.hc.html.HCStrong;
import com.helger.html.hc.html.HCTable;
import com.helger.html.hc.html.HC_Target;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.AppUtils;
import com.helger.peppol.app.CApp;
import com.helger.peppol.app.menu.CMenuPublic;
import com.helger.webbasics.EWebBasicsText;
import com.helger.webbasics.app.LinkUtils;
import com.helger.webbasics.app.layout.CLayout;
import com.helger.webbasics.app.layout.ILayoutAreaContentProvider;
import com.helger.webbasics.app.layout.ILayoutExecutionContext;
import com.helger.webbasics.app.layout.LayoutExecutionContext;
import com.helger.webbasics.app.page.IWebPage;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.servlet.AbstractSecureApplicationServlet;
import com.helger.webbasics.servlet.LogoutServlet;
import com.helger.webctrls.google.HCUniversalAnalytics;
import com.helger.webscopes.domain.IRequestWebScopeWithoutResponse;

/**
 * The viewport renderer (menu + content area)
 *
 * @author Philip Helger
 */
public final class LayoutAreaContentProviderPublic implements ILayoutAreaContentProvider <LayoutExecutionContext>
{
  private static final String PARAM_HTTP_ERROR = "httpError";
  private static final String VALUE_HTTP_ERROR = "true";
  private static final String PARAM_HTTP_STATUS_CODE = "httpStatusCode";
  private static final String PARAM_HTTP_STATUS_MESSAGE = "httpStatusMessage";
  private static final String PARAM_HTTP_REQUEST_URI = "httpRequestUri";

  private static final ICSSClassProvider CSS_CLASS_PAYPAL = DefaultCSSClassProvider.create ("paypal");
  private static final ICSSClassProvider CSS_CLASS_FOOTER_LINKS = DefaultCSSClassProvider.create ("footer-links");

  private final List <IMenuObject> m_aFooterObjectsCol1 = new ArrayList <IMenuObject> ();
  private final List <IMenuObject> m_aFooterObjectsCol2 = new ArrayList <IMenuObject> ();
  private final List <IMenuObject> m_aFooterObjectsCol3 = new ArrayList <IMenuObject> ();
  private final int m_nFooterRowCount;

  public LayoutAreaContentProviderPublic ()
  {
    ApplicationMenuTree.getTree ().iterateAllMenuObjects ( (aCurrentObject) -> {
      if (aCurrentObject.containsAttribute (CMenuPublic.FLAG_FOOTER_COL1))
        m_aFooterObjectsCol1.add (aCurrentObject);
      if (aCurrentObject.containsAttribute (CMenuPublic.FLAG_FOOTER_COL2))
        m_aFooterObjectsCol2.add (aCurrentObject);
      if (aCurrentObject.containsAttribute (CMenuPublic.FLAG_FOOTER_COL3))
        m_aFooterObjectsCol3.add (aCurrentObject);
    });
    m_nFooterRowCount = MathHelper.getMaxInt (m_aFooterObjectsCol1.size (),
                                              m_aFooterObjectsCol2.size (),
                                              m_aFooterObjectsCol3.size ());
  }

  private static void _addNavbarLoginLogout (@Nonnull final LayoutExecutionContext aLEC,
                                             @Nonnull final BootstrapNavbar aNavbar)
  {
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();
    final IUser aUser = LoggedInUserManager.getInstance ().getCurrentUser ();

    if (aUser != null)
    {
      // aNavbar.addNav (EBootstrapNavbarPosition.COLLAPSIBLE_RIGHT, aNav);

      final BootstrapNav aNav = new BootstrapNav ();
      aNav.addItem (new HCSpan ().addClass (CBootstrapCSS.NAVBAR_TEXT)
                                 .addChild ("Logged in as ")
                                 .addChild (new HCStrong ().addChild (SecurityUtils.getUserDisplayName (aUser,
                                                                                                        aDisplayLocale))));
      if (AccessManager.getInstance ().hasUserRole (aUser.getID (), CApp.ROLE_CONFIG_ID))
      {
        final HCForm aForm = new HCForm ().addClass (CBootstrapCSS.NAVBAR_FORM);
        aForm.addChild (new BootstrapButton ().setOnClick (LinkUtils.getURLWithContext (AbstractSecureApplicationServlet.SERVLET_DEFAULT_PATH))
                                              .addChild ("Administration"));
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
    }
    else
    {
      // show login in Navbar
      final BootstrapNav aNav = new BootstrapNav ();

      final HCForm aForm = new HCForm ().addClass (CBootstrapCSS.NAVBAR_FORM);
      aForm.addChild (new BootstrapButton (EBootstrapButtonType.SUCCESS).addChild (EWebBasicsText.MSG_BUTTON_SIGN_UP.getDisplayText (aDisplayLocale))
                                                                        .setOnClick (aLEC.getLinkToMenuItem (CMenuPublic.MENU_SIGN_UP)));
      aNav.addItem (aForm);

      final BootstrapDropdownMenu aDropDown = aNav.addDropdownMenu ("Login");
      {
        // 300px would lead to a messy layout - so 250px is fine
        final HCDiv aDiv = new HCDiv ().addStyle (CCSSProperties.PADDING.newValue ("10px"))
                                       .addStyle (CCSSProperties.WIDTH.newValue ("250px"));
        aDiv.addChild (AppCommonUI.createViewLoginForm (aLEC, null, false, false).addClass (CBootstrapCSS.NAVBAR_FORM));
        aDropDown.addItem (aDiv);
      }

      aNavbar.addNav (EBootstrapNavbarPosition.COLLAPSIBLE_RIGHT, aNav);
    }
  }

  @Nonnull
  private static BootstrapNavbar _getNavbar (final LayoutExecutionContext aLEC)
  {
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final ISimpleURL aLinkToStartPage = aLEC.getLinkToMenuItem (aLEC.getMenuTree ().getDefaultMenuItemID ());

    final BootstrapNavbar aNavbar = new BootstrapNavbar (EBootstrapNavbarType.STATIC_TOP, true, aDisplayLocale);
    aNavbar.addBrand (new HCSpan ().addClass (CAppCSS.CSS_CLASS_LOGO1).addChild (AppUtils.getApplicationTitle ()),
                      aLinkToStartPage);

    _addNavbarLoginLogout (aLEC, aNavbar);
    return aNavbar;
  }

  @Nonnull
  public static IHCNode getMenuContent (@Nonnull final LayoutExecutionContext aLEC)
  {
    // Main menu
    final IMenuTree aMenuTree = aLEC.getMenuTree ();
    final MenuItemDeterminatorCallback aCallback = new MenuItemDeterminatorCallback (aMenuTree,
                                                                                     aLEC.getSelectedMenuItemID ())
    {
      @Override
      protected boolean isMenuItemValidToBeDisplayed (@Nonnull final IMenuObject aMenuObj)
      {
        // Don't show items that belong to the footer
        if (aMenuObj.containsAttribute (CMenuPublic.FLAG_FOOTER_COL1) ||
            aMenuObj.containsAttribute (CMenuPublic.FLAG_FOOTER_COL2) ||
            aMenuObj.containsAttribute (CMenuPublic.FLAG_FOOTER_COL3))
          return false;

        // Use default code
        return super.isMenuItemValidToBeDisplayed (aMenuObj);
      }
    };
    final IHCElement <?> aMenu = BootstrapMenuItemRenderer.createSideBarMenu (aLEC, aCallback);

    // Add PayPal
    HCForm aPayPal;
    {
      aPayPal = new HCForm ("https://www.paypal.com/cgi-bin/webscr").setTarget (HC_Target.TOP)
                                                                    .addClass (CSS_CLASS_PAYPAL);
      aPayPal.addChild (new HCHiddenField ("cmd", "_s-xclick"));
      aPayPal.addChild (new HCHiddenField ("encrypted",
                                           "-----BEGIN PKCS7-----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-----END PKCS7-----\r\n"));
      aPayPal.addChild (new HCInput ().setType (EHCInputType.IMAGE)
                                      .setSrc ("https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif")
                                      .setName ("submit")
                                      .setAlt ("PayPal - The safer, easier way to pay online!"));
      aPayPal.addChild (new HCImg ().setAlt ("")
                                    .setSrc ("https://www.paypalobjects.com/de_DE/i/scr/pixel.gif")
                                    .setExtent (1, 1));
    }

    return HCNodeList.create (aMenu, aPayPal);
  }

  @SuppressWarnings ("unchecked")
  @Nonnull
  static IHCNode _getMainContent (@Nonnull final LayoutExecutionContext aLEC, @Nonnull final HCHead aHead)
  {
    final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();

    // Get the requested menu item
    final IMenuItemPage aSelectedMenuItem = aLEC.getSelectedMenuItem ();

    // Resolve the page of the selected menu item (if found)
    IWebPage <WebPageExecutionContext> aDisplayPage;
    if (aSelectedMenuItem.matchesDisplayFilter ())
    {
      // Only if we have display rights!
      aDisplayPage = (IWebPage <WebPageExecutionContext>) aSelectedMenuItem.getPage ();
    }
    else
    {
      // No rights -> goto start page
      aDisplayPage = (IWebPage <WebPageExecutionContext>) aLEC.getMenuTree ().getDefaultMenuItem ().getPage ();
    }

    final WebPageExecutionContext aWPEC = new WebPageExecutionContext (aLEC, aDisplayPage);

    // Build page content: header + content
    final HCNodeList aPageContainer = new HCNodeList ();

    // Handle 404 case here (see error404.jsp)
    if (VALUE_HTTP_ERROR.equals (aRequestScope.getAttributeAsString (PARAM_HTTP_ERROR)))
    {
      final String sHttpStatusCode = aRequestScope.getAttributeAsString (PARAM_HTTP_STATUS_CODE);
      final String sHttpStatusMessage = aRequestScope.getAttributeAsString (PARAM_HTTP_STATUS_MESSAGE);
      final String sHttpRequestURI = aRequestScope.getAttributeAsString (PARAM_HTTP_REQUEST_URI);
      aPageContainer.addChild (new BootstrapErrorBox ().addChild ("HTTP error " +
                                                                  sHttpStatusCode +
                                                                  " (" +
                                                                  sHttpStatusMessage +
                                                                  ")" +
                                                                  (StringHelper.hasText (sHttpRequestURI) ? " for request URI " +
                                                                                                            sHttpRequestURI
                                                                                                         : "")));
    }

    final String sHeaderText = aDisplayPage.getHeaderText (aWPEC);
    if (StringHelper.hasText (sHeaderText))
      aPageContainer.addChild (new BootstrapPageHeader ().addChild (new HCH1 ().addChild (sHeaderText)));
    // Main fill content
    aDisplayPage.getContent (aWPEC);
    // Add result
    aPageContainer.addChild (aWPEC.getNodeList ());
    // Add all meta elements
    aHead.getMetaElementList ().addMetaElements (aDisplayPage.getMetaElements ());

    // Add Google Analytics
    aPageContainer.addChild (new HCUniversalAnalytics ("UA-55419519-1", true, true, false));

    return aPageContainer;
  }

  @Nullable
  private static IHCNode _getRenderedFooterMenuObj (@Nonnull final ILayoutExecutionContext aLEC,
                                                    @Nonnull final BootstrapMenuItemRendererHorz aRenderer,
                                                    @Nullable final IMenuObject aMenuObj)
  {
    if (aMenuObj == null)
      return null;

    if (aMenuObj instanceof IMenuSeparator)
      return aRenderer.renderSeparator (aLEC, (IMenuSeparator) aMenuObj);

    if (aMenuObj instanceof IMenuItemPage)
      return aRenderer.renderMenuItemPage (aLEC, (IMenuItemPage) aMenuObj, false, false, false);

    if (aMenuObj instanceof IMenuItemExternal)
      return aRenderer.renderMenuItemExternal (aLEC, (IMenuItemExternal) aMenuObj, false, false, false);

    throw new IllegalStateException ("Unsupported menu object type: " + aMenuObj);
  }

  @Nonnull
  public IHCNode getContent (@Nonnull final LayoutExecutionContext aLEC, @Nonnull final HCHead aHead)
  {
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
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
      aCol1.addChild (new HCSpan ().setID (CLayout.LAYOUT_AREAID_MENU)
                                   .addClass (CBootstrapCSS.HIDDEN_PRINT)
                                   .addChild (getMenuContent (aLEC)));
      aCol1.addChild (new HCDiv ().setID (CLayout.LAYOUT_AREAID_SPECIAL));

      // content
      aCol2.addChild (_getMainContent (aLEC, aHead));
    }

    // Footer
    {
      final BootstrapContainerFluid aDiv = new BootstrapContainerFluid ().setID (CLayout.LAYOUT_AREAID_FOOTER);

      aDiv.addChild (new HCP ().addChild ("PEPPOL practical - created by Philip Helger")
                               .addChild (" - GitHub: ")
                               .addChild (new HCA ("https://github.com/phax").addChild ("phax"))
                               .addChild (" - Twitter: ")
                               .addChild (new HCA ("https://twitter.com/philiphelger").addChild ("@philiphelger")));

      if (m_nFooterRowCount > 0)
      {
        final BootstrapMenuItemRendererHorz aRenderer = new BootstrapMenuItemRendererHorz (aDisplayLocale);
        final HCTable aTable = new HCTable (HCCol.star (), HCCol.star (), HCCol.star ());
        aTable.addClass (CSS_CLASS_FOOTER_LINKS);
        for (int i = 0; i < m_nFooterRowCount; ++i)
        {
          final HCRow aRow = aTable.addBodyRow ();
          aRow.addCell (_getRenderedFooterMenuObj (aLEC, aRenderer, ContainerHelper.getSafe (m_aFooterObjectsCol1, i)));
          aRow.addCell (_getRenderedFooterMenuObj (aLEC, aRenderer, ContainerHelper.getSafe (m_aFooterObjectsCol2, i)));
          aRow.addCell (_getRenderedFooterMenuObj (aLEC, aRenderer, ContainerHelper.getSafe (m_aFooterObjectsCol3, i)));
        }
        aDiv.addChild (aTable);
      }

      ret.addChild (aDiv);
    }

    return ret;
  }
}
