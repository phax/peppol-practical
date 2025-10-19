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
package com.helger.peppol.ui;

import java.time.LocalDateTime;
import java.util.Locale;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.id.factory.GlobalIDFactory;
import com.helger.base.type.ITypedObject;
import com.helger.css.property.CCSSProperties;
import com.helger.datetime.format.PDTToString;
import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.css.ICSSClassProvider;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCEditPassword;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.html.jquery.JQuery;
import com.helger.html.jquery.JQueryAjaxBuilder;
import com.helger.html.jscode.JSAnonymousFunction;
import com.helger.html.jscode.JSAssocArray;
import com.helger.html.jscode.JSPackage;
import com.helger.html.jscode.JSParam;
import com.helger.html.jscode.html.JSHtml;
import com.helger.http.EHttpMethod;
import com.helger.peppol.app.CPPApp;
import com.helger.peppol.app.ajax.AjaxExecutorPublicLogin;
import com.helger.peppol.app.ajax.CAjax;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.pub.CMenuPublic;
import com.helger.peppol.sharedui.ui.SharedCommonUI;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.button.EBootstrapButtonType;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.form.EBootstrapFormType;
import com.helger.photon.bootstrap4.pages.BootstrapPagesMenuConfigurator;
import com.helger.photon.core.EPhotonCoreText;
import com.helger.photon.core.execcontext.LayoutExecutionContext;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.login.CLogin;
import com.helger.photon.core.menu.IMenuObject;
import com.helger.photon.security.mgr.PhotonSecurityManager;
import com.helger.photon.security.role.IRole;
import com.helger.photon.security.user.IUser;
import com.helger.photon.security.usergroup.IUserGroup;
import com.helger.photon.security.util.SecurityHelper;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.page.IWebPageExecutionContext;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Immutable
public final class AppCommonUI
{
  public static final ICSSClassProvider CSS_CLASS_LOGO1 = DefaultCSSClassProvider.create ("logo1");
  public static final ICSSClassProvider CSS_CLASS_LOGO2 = DefaultCSSClassProvider.create ("logo2");

  private AppCommonUI ()
  {}

  public static void init ()
  {
    SharedCommonUI.init ();

    // Register comment handlers
    CommentThreadManager.getInstance ().registerObjectType (CPPApp.OT_PAGE);
  }

  @Nonnull
  public static BootstrapForm createViewLoginForm (@Nonnull final LayoutExecutionContext aLEC,
                                                   @Nullable final String sPreselectedUserName,
                                                   final boolean bShowRegistration)
  {
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();

    // Use new IDs for both fields, in case the login stuff is displayed more
    // than once!
    final String sIDUserName = GlobalIDFactory.getNewStringID ();
    final String sIDPassword = GlobalIDFactory.getNewStringID ();
    final String sIDErrorField = GlobalIDFactory.getNewStringID ();

    final BootstrapForm aForm = new BootstrapForm (aLEC).setAction (aLEC.getSelfHref ())
                                                        .setFormType (EBootstrapFormType.DEFAULT);
    aForm.setLeft (3);

    // Placeholder for error message
    aForm.addChild (new HCDiv ().setID (sIDErrorField).addStyle (CCSSProperties.MARGIN.newValue ("4px 0")));

    // User name field
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory (EPhotonCoreText.EMAIL_ADDRESS.getDisplayText (aDisplayLocale))
                                                 .setCtrl (new HCEdit (new RequestField (CLogin.REQUEST_ATTR_USERID,
                                                                                         sPreselectedUserName)).setPlaceholder (EPhotonCoreText.EMAIL_ADDRESS.getDisplayText (aDisplayLocale))
                                                                                                               .setID (sIDUserName)));

    // Password field
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory (EPhotonCoreText.LOGIN_FIELD_PASSWORD.getDisplayText (aDisplayLocale))
                                                 .setCtrl (new HCEditPassword (CLogin.REQUEST_ATTR_PASSWORD).setPlaceholder (EPhotonCoreText.LOGIN_FIELD_PASSWORD.getDisplayText (aDisplayLocale))
                                                                                                            .setID (sIDPassword)));

    // Login button
    final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aLEC));
    {
      final JSPackage aOnClick = new JSPackage ();
      final JSAnonymousFunction aJSSuccess = new JSAnonymousFunction ();
      final JSParam aJSData = aJSSuccess.param ("data");
      aJSSuccess.body ()
                ._if (aJSData.ref (AjaxExecutorPublicLogin.JSON_LOGGEDIN),
                      JSHtml.windowLocationReload (),
                      JQuery.idRef (sIDErrorField).empty ().append (aJSData.ref (AjaxExecutorPublicLogin.JSON_HTML)));

      aOnClick.add (new JQueryAjaxBuilder ().url (CAjax.LOGIN.getInvocationURI (aRequestScope))
                                            .method (EHttpMethod.POST)
                                            .data (new JSAssocArray ().add (CLogin.REQUEST_ATTR_USERID,
                                                                            JQuery.idRef (sIDUserName).val ())
                                                                      .add (CLogin.REQUEST_ATTR_PASSWORD,
                                                                            JQuery.idRef (sIDPassword).val ()))
                                            .success (aJSSuccess)
                                            .build ());
      aOnClick._return (false);
      aToolbar.addSubmitButton (EPhotonCoreText.LOGIN_BUTTON_SUBMIT.getDisplayText (aDisplayLocale),
                                aOnClick,
                                EDefaultIcon.YES.getIcon ());
    }
    if (bShowRegistration)
    {
      aToolbar.addChild (new BootstrapButton (EBootstrapButtonType.SUCCESS).addChild (EPhotonCoreText.BUTTON_SIGN_UP.getDisplayText (aDisplayLocale))
                                                                           .setOnClick (aLEC.getLinkToMenuItem (CMenuPublic.MENU_SIGN_UP)));
    }
    return aForm;
  }

  @Nullable
  public static IHCNode getDTAndUser (@Nonnull final IWebPageExecutionContext aWPEC,
                                      @Nullable final LocalDateTime aDateTime,
                                      @Nullable final String sUserID)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    String sDateTime = null;
    if (aDateTime != null)
      sDateTime = PDTToString.getAsString (aDateTime, aDisplayLocale);
    IHCNode aUserName = null;
    if (sUserID != null)
    {
      final IUser aUser = PhotonSecurityManager.getUserMgr ().getUserOfID (sUserID);
      aUserName = createViewLink (aWPEC, aUser);
    }

    if (sDateTime != null)
    {
      if (aUserName != null)
      {
        // Date and user
        return new HCNodeList ().addChildren (new HCTextNode ("on " + sDateTime + " by "), aUserName);
      }

      // Date only
      return new HCTextNode ("on  " + sDateTime);
    }

    if (aUserName != null)
    {
      // User only
      return new HCNodeList ().addChildren (new HCTextNode ("by "), aUserName);
    }

    // Neither nor
    return null;
  }

  @Nonnull
  public static IHCNode createViewLink (@Nonnull final IWebPageExecutionContext aWPEC,
                                        @Nullable final ITypedObject <String> aObject)
  {
    return createViewLink (aWPEC, aObject, null);
  }

  @Nonnull
  public static IHCNode createViewLink (@Nonnull final IWebPageExecutionContext aWPEC,
                                        @Nullable final ITypedObject <String> aObject,
                                        @Nullable final String sDisplayName)
  {
    if (aObject == null)
      return HCTextNode.createOnDemand (sDisplayName);

    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    if (aObject instanceof final IRole aTypedObj)
    {
      final String sRealDisplayName = sDisplayName != null ? sDisplayName : aTypedObj.getName ();
      final String sMenuItemID = BootstrapPagesMenuConfigurator.MENU_ADMIN_SECURITY_ROLE;
      final IMenuObject aObj = aWPEC.getMenuTree ().getItemDataWithID (sMenuItemID);
      if (aObj != null && aObj.matchesDisplayFilter ())
        return new HCA (PeppolUI.getViewLink (aWPEC, sMenuItemID, aTypedObj)).addChild (sRealDisplayName)
                                                                             .setTitle ("Show details of role '" +
                                                                                        sRealDisplayName +
                                                                                        "'");
      return new HCTextNode (sRealDisplayName);
    }

    if (aObject instanceof final IUser aTypedObj)
    {
      final String sRealDisplayName = sDisplayName != null ? sDisplayName : SecurityHelper.getUserDisplayName (
                                                                                                               aTypedObj,
                                                                                                               aDisplayLocale);
      final String sMenuItemID = BootstrapPagesMenuConfigurator.MENU_ADMIN_SECURITY_USER;
      final IMenuObject aObj = aWPEC.getMenuTree ().getItemDataWithID (sMenuItemID);
      if (aObj != null && aObj.matchesDisplayFilter ())
        return new HCA (PeppolUI.getViewLink (aWPEC, sMenuItemID, aTypedObj)).addChild (sRealDisplayName)
                                                                             .setTitle ("Show details of user '" +
                                                                                        sRealDisplayName +
                                                                                        "'");
      return new HCTextNode (sRealDisplayName);
    }
    if (aObject instanceof final IUserGroup aTypedObj)
    {
      final String sRealDisplayName = sDisplayName != null ? sDisplayName : aTypedObj.getName ();
      final String sMenuItemID = BootstrapPagesMenuConfigurator.MENU_ADMIN_SECURITY_USER_GROUP;
      final IMenuObject aObj = aWPEC.getMenuTree ().getItemDataWithID (sMenuItemID);
      if (aObj != null && aObj.matchesDisplayFilter ())
        return new HCA (PeppolUI.getViewLink (aWPEC, sMenuItemID, aTypedObj)).addChild (sRealDisplayName)
                                                                             .setTitle ("Show details of user group '" +
                                                                                        sRealDisplayName +
                                                                                        "'");
      return new HCTextNode (sRealDisplayName);
    }

    // add other types as desired
    throw new IllegalArgumentException ("Unsupported object: " + aObject);
  }
}
