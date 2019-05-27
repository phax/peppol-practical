/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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
package com.helger.peppol.pub;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.math.MathHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.commons.url.SimpleURL;
import com.helger.css.property.CCSSProperties;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.css.ICSSClassProvider;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.HC_Target;
import com.helger.html.hc.html.IHCElement;
import com.helger.html.hc.html.embedded.HCImg;
import com.helger.html.hc.html.forms.EHCInputType;
import com.helger.html.hc.html.forms.HCForm;
import com.helger.html.hc.html.forms.HCHiddenField;
import com.helger.html.hc.html.forms.HCInput;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.grouping.HCP;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.html.textlevel.HCSpan;
import com.helger.html.hc.html.textlevel.HCStrong;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.AppHelper;
import com.helger.peppol.app.CPPApp;
import com.helger.peppol.ui.AppCommonUI;
import com.helger.peppol.ui.CAppCSS;
import com.helger.photon.app.url.LinkHelper;
import com.helger.photon.bootstrap4.CBootstrapCSS;
import com.helger.photon.bootstrap4.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap4.breadcrumb.BootstrapBreadcrumb;
import com.helger.photon.bootstrap4.breadcrumb.BootstrapBreadcrumbProvider;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.button.EBootstrapButtonType;
import com.helger.photon.bootstrap4.dropdown.BootstrapDropdownMenu;
import com.helger.photon.bootstrap4.ext.BootstrapSystemMessage;
import com.helger.photon.bootstrap4.grid.BootstrapRow;
import com.helger.photon.bootstrap4.layout.BootstrapContainer;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbar;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbarNav;
import com.helger.photon.bootstrap4.navbar.BootstrapNavbarToggleable;
import com.helger.photon.bootstrap4.pages.BootstrapWebPageUIHandler;
import com.helger.photon.bootstrap4.uictrls.ext.BootstrapMenuItemRenderer;
import com.helger.photon.bootstrap4.uictrls.ext.BootstrapMenuItemRendererHorz;
import com.helger.photon.core.EPhotonCoreText;
import com.helger.photon.core.appid.CApplicationID;
import com.helger.photon.core.appid.PhotonGlobalState;
import com.helger.photon.core.execcontext.ILayoutExecutionContext;
import com.helger.photon.core.execcontext.LayoutExecutionContext;
import com.helger.photon.core.html.CLayout;
import com.helger.photon.core.menu.IMenuItemExternal;
import com.helger.photon.core.menu.IMenuItemPage;
import com.helger.photon.core.menu.IMenuObject;
import com.helger.photon.core.menu.IMenuSeparator;
import com.helger.photon.core.menu.IMenuTree;
import com.helger.photon.core.menu.MenuItemDeterminatorCallback;
import com.helger.photon.core.servlet.AbstractSecureApplicationServlet;
import com.helger.photon.core.servlet.LogoutServlet;
import com.helger.photon.security.login.LoggedInUserManager;
import com.helger.photon.security.user.IUser;
import com.helger.photon.security.util.SecurityHelper;
import com.helger.photon.uicore.html.google.HCUniversalAnalytics;
import com.helger.photon.uicore.html.twitter.HCTweet;
import com.helger.photon.uicore.page.IWebPage;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;
import com.helger.xservlet.forcedredirect.ForcedRedirectManager;

/**
 * The viewport renderer (menu + content area)
 *
 * @author Philip Helger
 */
public final class LayoutAreaContentProviderPublic
{
  private static final String PARAM_HTTP_ERROR = "httpError";
  private static final String VALUE_HTTP_ERROR = "true";
  private static final String PARAM_HTTP_STATUS_CODE = "httpStatusCode";
  private static final String PARAM_HTTP_STATUS_MESSAGE = "httpStatusMessage";
  private static final String PARAM_HTTP_REQUEST_URI = "httpRequestUri";

  private static final ICSSClassProvider CSS_CLASS_PAYPAL = DefaultCSSClassProvider.create ("paypal");
  private static final ICSSClassProvider CSS_CLASS_FOOTER_LINKS = DefaultCSSClassProvider.create ("footer-links");

  private static final ICommonsList <IMenuObject> s_aFooterObjectsCol1 = new CommonsArrayList <> ();
  private static final ICommonsList <IMenuObject> s_aFooterObjectsCol2 = new CommonsArrayList <> ();
  private static final ICommonsList <IMenuObject> s_aFooterObjectsCol3 = new CommonsArrayList <> ();
  private static final int s_nFooterRowCount;

  static
  {
    PhotonGlobalState.state (CApplicationID.APP_ID_PUBLIC).getMenuTree ().iterateAllMenuObjects ( (aCurrentObject) -> {
      if (aCurrentObject.attrs ().containsKey (CMenuPublic.FLAG_FOOTER_COL1))
        s_aFooterObjectsCol1.add (aCurrentObject);
      if (aCurrentObject.attrs ().containsKey (CMenuPublic.FLAG_FOOTER_COL2))
        s_aFooterObjectsCol2.add (aCurrentObject);
      if (aCurrentObject.attrs ().containsKey (CMenuPublic.FLAG_FOOTER_COL3))
        s_aFooterObjectsCol3.add (aCurrentObject);
    });
    s_nFooterRowCount = MathHelper.getMaxInt (s_aFooterObjectsCol1.size (),
                                              s_aFooterObjectsCol2.size (),
                                              s_aFooterObjectsCol3.size ());
  }

  private LayoutAreaContentProviderPublic ()
  {}

  @Nonnull
  private static BootstrapNavbar _getNavbar (final LayoutExecutionContext aLEC)
  {
    final ISimpleURL aLinkToStartPage = aLEC.getLinkToMenuItem (aLEC.getMenuTree ().getDefaultMenuItemID ());
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();
    final IUser aUser = LoggedInUserManager.getInstance ().getCurrentUser ();

    final BootstrapNavbar aNavbar = new BootstrapNavbar ();
    aNavbar.addBrand (new HCSpan ().addClass (CAppCSS.CSS_CLASS_LOGO1).addChild (AppHelper.getApplicationTitle ()),
                      aLinkToStartPage);

    final BootstrapNavbarToggleable aToggleable = aNavbar.addAndReturnToggleable ();

    if (aUser != null)
    {
      aToggleable.addAndReturnText ()
                 .addClass (CBootstrapCSS.ML_AUTO)
                 .addClass (CBootstrapCSS.MX_2)
                 .addChild ("Welcome ")
                 .addChild (new HCStrong ().addChild (SecurityHelper.getUserDisplayName (aUser, aDisplayLocale)));
      if (SecurityHelper.hasUserRole (aUser.getID (), CPPApp.ROLE_CONFIG_ID))
      {
        aToggleable.addChild (new BootstrapButton ().setOnClick (LinkHelper.getURLWithContext (AbstractSecureApplicationServlet.SERVLET_DEFAULT_PATH))
                                                    .addChild ("Administration")
                                                    .addClass (CBootstrapCSS.MX_2));
      }

      aToggleable.addChild (new BootstrapButton ().setOnClick (LinkHelper.getURLWithContext (aRequestScope,
                                                                                             LogoutServlet.SERVLET_DEFAULT_PATH))
                                                  .addChild (EPhotonCoreText.LOGIN_LOGOUT.getDisplayText (aDisplayLocale))
                                                  .addClass (CBootstrapCSS.MX_2));
    }
    else
    {
      // show login in Navbar
      final BootstrapNavbarNav aNav = aToggleable.addAndReturnNav ();
      final BootstrapDropdownMenu aDropDown = new BootstrapDropdownMenu ();
      {
        final HCDiv aDiv = new HCDiv ().addClass (CBootstrapCSS.P_2)
                                       .addStyle (CCSSProperties.MIN_WIDTH.newValue ("400px"));
        aDiv.addChild (AppCommonUI.createViewLoginForm (aLEC, null, false));
        aDropDown.addChild (aDiv);
      }
      aNav.addItem ().addNavDropDown ("Login", aDropDown);

      aToggleable.addChild (new BootstrapButton (EBootstrapButtonType.SUCCESS).addChild (EPhotonCoreText.BUTTON_SIGN_UP.getDisplayText (aDisplayLocale))
                                                                              .setOnClick (aLEC.getLinkToMenuItem (CMenuPublic.MENU_SIGN_UP))
                                                                              .addClass (CBootstrapCSS.ML_AUTO)
                                                                              .addClass (CBootstrapCSS.MX_2));
    }
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
        if (aMenuObj.attrs ().containsKey (CMenuPublic.FLAG_FOOTER_COL1) ||
            aMenuObj.attrs ().containsKey (CMenuPublic.FLAG_FOOTER_COL2) ||
            aMenuObj.attrs ().containsKey (CMenuPublic.FLAG_FOOTER_COL3))
          return false;

        // Use default code
        return super.isMenuItemValidToBeDisplayed (aMenuObj);
      }
    };
    final IHCElement <?> aMenu = BootstrapMenuItemRenderer.createSideBarMenu (aLEC, aCallback);

    // Add PayPal
    HCForm aPayPal = null;
    if (!GlobalDebug.isDebugMode ())
    {
      aPayPal = new HCForm (new SimpleURL ("https://www.paypal.com/cgi-bin/webscr")).setTarget (HC_Target.TOP)
                                                                                    .addClass (CSS_CLASS_PAYPAL)
                                                                                    .addClass (CBootstrapCSS.MT_2);
      aPayPal.addChild (new HCHiddenField ("cmd", "_s-xclick"));
      aPayPal.addChild (new HCHiddenField ("encrypted",
                                           "-----BEGIN PKCS7-----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-----END PKCS7-----\r\n"));
      aPayPal.addChild (new HCInput ().setType (EHCInputType.IMAGE)
                                      .setSrc (new SimpleURL ("https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif"))
                                      .setName ("submit")
                                      .setAlt ("PayPal - The safer, easier way to pay online!"));
      aPayPal.addChild (new HCImg ().setAlt ("")
                                    .setSrc (new SimpleURL ("https://www.paypalobjects.com/de_DE/i/scr/pixel.gif"))
                                    .setExtent (1, 1));
    }

    return new HCNodeList ().addChild (aMenu).addChild (aPayPal);
  }

  @SuppressWarnings ("unchecked")
  @Nonnull
  public static IHCNode getPageContent (@Nonnull final LayoutExecutionContext aLEC)
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

    // System message always
    aPageContainer.addChild (BootstrapSystemMessage.createDefault ());

    // Handle 404 case here (see error404.jsp)
    if (VALUE_HTTP_ERROR.equals (aRequestScope.params ().getAsString (PARAM_HTTP_ERROR)))
    {
      final String sHttpStatusCode = aRequestScope.params ().getAsString (PARAM_HTTP_STATUS_CODE);
      final String sHttpStatusMessage = aRequestScope.params ().getAsString (PARAM_HTTP_STATUS_MESSAGE);
      final String sHttpRequestURI = aRequestScope.params ().getAsString (PARAM_HTTP_REQUEST_URI);
      aPageContainer.addChild (new BootstrapErrorBox ().addChild ("HTTP error " +
                                                                  sHttpStatusCode +
                                                                  " (" +
                                                                  sHttpStatusMessage +
                                                                  ")" +
                                                                  (StringHelper.hasText (sHttpRequestURI) ? " for request URI " +
                                                                                                            sHttpRequestURI
                                                                                                          : "")));
    }
    else
    {
      // Add the forced redirect content here
      if (aWPEC.params ().containsKey (ForcedRedirectManager.REQUEST_PARAMETER_PRG_ACTIVE))
        aPageContainer.addChild ((IHCNode) ForcedRedirectManager.getLastForcedRedirectContent (aDisplayPage.getID ()));
    }

    final String sHeaderText = aDisplayPage.getHeaderText (aWPEC);
    {
      final BootstrapRow aRow = new BootstrapRow ();
      aRow.createColumn (10).addChild (BootstrapWebPageUIHandler.INSTANCE.createPageHeader (sHeaderText));
      aRow.createColumn (2).addChild (HCTweet.createShareButton ());
      aPageContainer.addChild (aRow);
    }

    // Main fill content
    aDisplayPage.getContent (aWPEC);
    // Add result
    aPageContainer.addChild (aWPEC.getNodeList ());

    if (GlobalDebug.isProductionMode ())
    {
      // Add Google Analytics
      aPageContainer.addChild (new HCUniversalAnalytics ("UA-55419519-1", true, true, false, true));
    }

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
  public static IHCNode getContent (@Nonnull final LayoutExecutionContext aLEC)
  {
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
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
      final HCDiv aCol1 = aRow.addAndReturnChild (new HCDiv ().addClass (CBootstrapCSS.D_MD_FLEX));
      final HCDiv aCol2 = aRow.addAndReturnChild (new HCDiv ().addClass (CBootstrapCSS.ML_3)
                                                              .addClass (CBootstrapCSS.FLEX_FILL));

      // left
      // We need a wrapper span for easy AJAX content replacement
      aCol1.addClass (CBootstrapCSS.D_PRINT_NONE)
           .addChild (new HCSpan ().setID (CLayout.LAYOUT_AREAID_MENU).addChild (getMenuContent (aLEC)));
      aCol1.addChild (new HCDiv ().setID (CLayout.LAYOUT_AREAID_SPECIAL));

      // content
      aCol2.addChild (getPageContent (aLEC));
    }

    // Footer
    {
      final BootstrapContainer aFooter = new BootstrapContainer ().setFluid (true).setID (CLayout.LAYOUT_AREAID_FOOTER);

      aFooter.addChild (new HCP ().addChild ("PEPPOL practical - created by Philip Helger")
                                  .addChild (" - GitHub: ")
                                  .addChild (new HCA (new SimpleURL ("https://github.com/phax")).addChild ("phax"))
                                  .addChild (" - Twitter: ")
                                  .addChild (new HCA (new SimpleURL ("https://twitter.com/philiphelger")).addChild ("@philiphelger")));
      aFooter.addChild (new HCP ().addChild ("Disclaimer: This page is a personal project of Philip Helger." +
                                             " The content is neither provided nor approved by OpenPEPPOL AISBL." +
                                             " The official PEPPOL website is ")
                                  .addChild (new HCA (new SimpleURL ("https://www.peppol.eu")).addChild ("www.peppol.eu")
                                                                                              .setTargetBlank ()));

      if (s_nFooterRowCount > 0)
      {
        final BootstrapMenuItemRendererHorz aRenderer = new BootstrapMenuItemRendererHorz (aDisplayLocale);
        final HCDiv aTable = new HCDiv ();
        aTable.addClass (CSS_CLASS_FOOTER_LINKS);
        for (int i = 0; i < s_nFooterRowCount; ++i)
        {
          final BootstrapRow aRow = aTable.addAndReturnChild (new BootstrapRow ());
          aRow.createColumn (4)
              .addChild (_getRenderedFooterMenuObj (aLEC, aRenderer, s_aFooterObjectsCol1.getAtIndex (i)));
          aRow.createColumn (4)
              .addChild (_getRenderedFooterMenuObj (aLEC, aRenderer, s_aFooterObjectsCol2.getAtIndex (i)));
          aRow.createColumn (4)
              .addChild (_getRenderedFooterMenuObj (aLEC, aRenderer, s_aFooterObjectsCol3.getAtIndex (i)));
        }
        aFooter.addChild (aTable);
      }

      ret.addChild (aFooter);
    }

    return ret;
  }
}
