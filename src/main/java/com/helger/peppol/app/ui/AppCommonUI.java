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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTime;

import com.helger.appbasics.app.menu.IMenuObject;
import com.helger.appbasics.app.request.ApplicationRequestManager;
import com.helger.appbasics.security.AccessManager;
import com.helger.appbasics.security.role.IRole;
import com.helger.appbasics.security.user.IUser;
import com.helger.appbasics.security.usergroup.IUserGroup;
import com.helger.appbasics.security.util.SecurityUtils;
import com.helger.bootstrap3.button.BootstrapButton;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.button.EBootstrapButtonType;
import com.helger.bootstrap3.ext.BootstrapDataTables;
import com.helger.bootstrap3.form.BootstrapForm;
import com.helger.bootstrap3.form.BootstrapFormGroup;
import com.helger.bootstrap3.form.EBootstrapFormType;
import com.helger.bootstrap3.styler.BootstrapWebPageStyler;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.idfactory.GlobalIDFactory;
import com.helger.commons.type.ITypedObject;
import com.helger.commons.url.ISimpleURL;
import com.helger.css.property.CCSSProperties;
import com.helger.datetime.format.PDTToString;
import com.helger.html.hc.CHCParam;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.IHCTable;
import com.helger.html.hc.html.HCA;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCEditPassword;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.html.js.builder.JSAssocArray;
import com.helger.html.js.builder.JSPackage;
import com.helger.html.js.builder.jquery.JQuery;
import com.helger.peppol.app.CApp;
import com.helger.peppol.app.action.CActionPublic;
import com.helger.peppol.app.ajax.CAjaxPublic;
import com.helger.peppol.app.menu.CMenuPublic;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.web.scopes.domain.IRequestWebScopeWithoutResponse;
import com.helger.webbasics.EWebBasicsText;
import com.helger.webbasics.app.layout.ILayoutExecutionContext;
import com.helger.webbasics.app.layout.LayoutExecutionContext;
import com.helger.webbasics.app.page.IWebPageExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webbasics.login.CLogin;
import com.helger.webctrls.autonumeric.HCAutoNumeric;
import com.helger.webctrls.custom.EDefaultIcon;
import com.helger.webctrls.datatables.DataTablesLengthMenuList;
import com.helger.webctrls.datatables.EDataTablesFilterType;
import com.helger.webctrls.datatables.ajax.ActionExecutorDataTablesI18N;
import com.helger.webctrls.datatables.ajax.AjaxExecutorDataTables;
import com.helger.webctrls.page.AbstractWebPageExt;
import com.helger.webctrls.page.DefaultMenuConfigurator;
import com.helger.webctrls.styler.WebPageStylerManager;

@Immutable
public final class AppCommonUI
{
  private static final DataTablesLengthMenuList LENGTH_MENU = new DataTablesLengthMenuList ().addItem (25)
                                                                                             .addItem (50)
                                                                                             .addItem (100)
                                                                                             .addItemAll ();

  private AppCommonUI ()
  {}

  public static void init ()
  {
    ApplicationRequestManager.getInstance ().setUsePaths (true);

    WebPageStylerManager.getInstance ().setStyler (new BootstrapWebPageStyler ()
    {
      @Override
      @Nonnull
      public BootstrapForm createForm (@Nonnull final ILayoutExecutionContext aLEC)
      {
        return new BootstrapForm (EBootstrapFormType.HORIZONTAL).setAction (aLEC.getSelfHref ());
      }

      @Override
      @Nonnull
      public BootstrapDataTables createDefaultDataTables (@Nonnull final IWebPageExecutionContext aWPEC,
                                                          @Nonnull final IHCTable <?> aTable)
      {
        final IRequestWebScopeWithoutResponse aRequestScope = aWPEC.getRequestScope ();
        final BootstrapDataTables ret = super.createDefaultDataTables (aWPEC, aTable);
        ret.setAutoWidth (false)
           .setLengthMenu (LENGTH_MENU)
           .setUseJQueryAjax (true)
           .setAjaxSource (CAjaxPublic.DATATABLES.getInvocationURL (aRequestScope))
           .setServerParams (ContainerHelper.newMap (AjaxExecutorDataTables.OBJECT_ID, aTable.getID ()))
           .setServerFilterType (EDataTablesFilterType.ALL_TERMS_PER_ROW)
           .setTextLoadingURL (CActionPublic.DATATABLES_I18N.getInvocationURL (aRequestScope),
                               ActionExecutorDataTablesI18N.LANGUAGE_ID)
           .setUseSearchHighlight (true);
        return ret;
      }
    });

    // Never show a thousand separator
    HCAutoNumeric.setDefaultThousandSeparator ("");

    // Register comment handlers
    CommentThreadManager.getInstance ().registerObjectType (CApp.OT_PAGE);
  }

  @Nonnull
  public static BootstrapForm createViewLoginForm (@Nonnull final LayoutExecutionContext aLEC,
                                                   @Nullable final String sPreselectedUserName,
                                                   final boolean bFullUI,
                                                   final boolean bShowRegistration)
  {
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();

    // Use new IDs for both fields, in case the login stuff is displayed more
    // than once!
    final String sIDUserName = GlobalIDFactory.getNewStringID ();
    final String sIDPassword = GlobalIDFactory.getNewStringID ();
    final String sIDErrorField = GlobalIDFactory.getNewStringID ();

    final BootstrapForm aForm = new BootstrapForm (aLEC.getSelfHref (), bFullUI ? EBootstrapFormType.HORIZONTAL
                                                                               : EBootstrapFormType.DEFAULT);
    aForm.setLeft (3);

    // Placeholder for error message
    aForm.addChild (new HCDiv ().setID (sIDErrorField).addStyle (CCSSProperties.MARGIN.newValue ("4px 0")));

    // User name field
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory (EWebBasicsText.EMAIL_ADDRESS.getDisplayText (aDisplayLocale))
                                                 .setCtrl (new HCEdit (new RequestField (CLogin.REQUEST_ATTR_USERID,
                                                                                         sPreselectedUserName)).setPlaceholder (EWebBasicsText.EMAIL_ADDRESS.getDisplayText (aDisplayLocale))
                                                                                                               .setID (sIDUserName)));

    // Password field
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory (EWebBasicsText.LOGIN_FIELD_PASSWORD.getDisplayText (aDisplayLocale))
                                                 .setCtrl (new HCEditPassword (CLogin.REQUEST_ATTR_PASSWORD).setPlaceholder (EWebBasicsText.LOGIN_FIELD_PASSWORD.getDisplayText (aDisplayLocale))
                                                                                                            .setID (sIDPassword)));

    // Login button
    final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aLEC));
    final JSPackage aOnClick = new JSPackage ();
    aOnClick.add (CAppJS.viewLogin ()
                        .arg (CAjaxPublic.LOGIN.getInvocationURI (aRequestScope))
                        .arg (new JSAssocArray ().add (CLogin.REQUEST_ATTR_USERID, JQuery.idRef (sIDUserName).val ())
                                                 .add (CLogin.REQUEST_ATTR_PASSWORD, JQuery.idRef (sIDPassword).val ()))
                        .arg (sIDErrorField));
    aOnClick._return (false);
    aToolbar.addSubmitButton (EWebBasicsText.LOGIN_BUTTON_SUBMIT.getDisplayText (aDisplayLocale),
                              aOnClick,
                              EDefaultIcon.YES.getIcon ());
    if (bShowRegistration)
    {
      aToolbar.addChild (new BootstrapButton (EBootstrapButtonType.SUCCESS).addChild (EWebBasicsText.MSG_BUTTON_SIGN_UP.getDisplayText (aDisplayLocale))
                                                                           .setOnClick (aLEC.getLinkToMenuItem (CMenuPublic.MENU_SIGN_UP)));
    }
    return aForm;
  }

  @Nullable
  public static IHCNode getDTAndUser (@Nonnull final IWebPageExecutionContext aWPEC,
                                      @Nullable final DateTime aDateTime,
                                      @Nullable final String sUserID)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    String sDateTime = null;
    if (aDateTime != null)
      sDateTime = PDTToString.getAsString (aDateTime, aDisplayLocale);
    IHCNode aUserName = null;
    if (sUserID != null)
    {
      final IUser aUser = AccessManager.getInstance ().getUserOfID (sUserID);
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
  public static ISimpleURL getViewLink (@Nonnull final IWebPageExecutionContext aWPEC,
                                        @Nonnull @Nonempty final String sMenuItemID,
                                        @Nonnull final ITypedObject <String> aObject)
  {
    ValueEnforcer.notNull (aObject, "Object");

    return getViewLink (aWPEC, sMenuItemID, aObject.getID ());
  }

  @Nonnull
  public static ISimpleURL getViewLink (@Nonnull final IWebPageExecutionContext aWPEC,
                                        @Nonnull @Nonempty final String sMenuItemID,
                                        @Nonnull final String sObjectID)
  {
    return aWPEC.getLinkToMenuItem (sMenuItemID)
                .add (CHCParam.PARAM_ACTION, AbstractWebPageExt.ACTION_VIEW)
                .add (CHCParam.PARAM_OBJECT, sObjectID);
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

    if (aObject instanceof IRole)
    {
      final IRole aTypedObj = (IRole) aObject;
      final String sRealDisplayName = sDisplayName != null ? sDisplayName : aTypedObj.getName ();
      final String sMenuItemID = DefaultMenuConfigurator.MENU_ADMIN_SECURITY_ROLE;
      final IMenuObject aObj = aWPEC.getMenuTree ().getItemDataWithID (sMenuItemID);
      if (aObj != null && aObj.matchesDisplayFilter ())
        return new HCA (getViewLink (aWPEC, sMenuItemID, aTypedObj)).addChild (sRealDisplayName)
                                                                    .setTitle ("Show details of role '" +
                                                                               sRealDisplayName +
                                                                               "'");
      return new HCTextNode (sRealDisplayName);
    }

    if (aObject instanceof IUser)
    {
      final IUser aTypedObj = (IUser) aObject;
      final String sRealDisplayName = sDisplayName != null
                                                          ? sDisplayName
                                                          : SecurityUtils.getUserDisplayName (aTypedObj, aDisplayLocale);
      final String sMenuItemID = DefaultMenuConfigurator.MENU_ADMIN_SECURITY_USER;
      final IMenuObject aObj = aWPEC.getMenuTree ().getItemDataWithID (sMenuItemID);
      if (aObj != null && aObj.matchesDisplayFilter ())
        return new HCA (getViewLink (aWPEC, sMenuItemID, aTypedObj)).addChild (sRealDisplayName)
                                                                    .setTitle ("Show details of user '" +
                                                                               sRealDisplayName +
                                                                               "'");
      return new HCTextNode (sRealDisplayName);
    }
    if (aObject instanceof IUserGroup)
    {
      final IUserGroup aTypedObj = (IUserGroup) aObject;
      final String sRealDisplayName = sDisplayName != null ? sDisplayName : aTypedObj.getName ();
      final String sMenuItemID = DefaultMenuConfigurator.MENU_ADMIN_SECURITY_USER_GROUP;
      final IMenuObject aObj = aWPEC.getMenuTree ().getItemDataWithID (sMenuItemID);
      if (aObj != null && aObj.matchesDisplayFilter ())
        return new HCA (getViewLink (aWPEC, sMenuItemID, aTypedObj)).addChild (sRealDisplayName)
                                                                    .setTitle ("Show details of user group '" +
                                                                               sRealDisplayName +
                                                                               "'");
      return new HCTextNode (sRealDisplayName);
    }

    // add other types as desired
    throw new IllegalArgumentException ("Unsupported object: " + aObject);
  }
}
