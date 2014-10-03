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
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.ext.BootstrapDataTables;
import com.helger.bootstrap3.form.BootstrapForm;
import com.helger.bootstrap3.form.BootstrapFormGroup;
import com.helger.bootstrap3.form.EBootstrapFormType;
import com.helger.bootstrap3.styler.BootstrapWebPageStyler;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.idfactory.GlobalIDFactory;
import com.helger.css.property.CCSSProperties;
import com.helger.html.EHTMLVersion;
import com.helger.html.hc.IHCTable;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCEditPassword;
import com.helger.html.js.builder.JSAssocArray;
import com.helger.html.js.builder.JSPackage;
import com.helger.html.js.builder.jquery.JQuery;
import com.helger.peppol.app.action.CActionPublic;
import com.helger.peppol.app.ajax.CAjaxPublic;
import com.helger.webbasics.EWebBasicsText;
import com.helger.webbasics.app.html.WebHTMLCreator;
import com.helger.webbasics.app.layout.LayoutExecutionContext;
import com.helger.webbasics.app.page.IWebPageExecutionContext;
import com.helger.webbasics.login.CLogin;
import com.helger.webctrls.datatables.DataTablesLengthMenuList;
import com.helger.webctrls.datatables.EDataTablesFilterType;
import com.helger.webctrls.datatables.ajax.ActionExecutorDataTablesI18N;
import com.helger.webctrls.datatables.ajax.AjaxExecutorDataTables;
import com.helger.webctrls.styler.WebPageStylerManager;
import com.helger.webscopes.domain.IRequestWebScopeWithoutResponse;

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
    WebPageStylerManager.getInstance ().setStyler (new BootstrapWebPageStyler ()
    {
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
                               ActionExecutorDataTablesI18N.LANGUAGE_ID);
        return ret;
      }
    });

    WebHTMLCreator.setHTMLVersion (EHTMLVersion.HTML5);
  }

  @Nonnull
  public static BootstrapForm createViewLoginForm (@Nonnull final LayoutExecutionContext aLEC,
                                                   @Nullable final String sPreselectedUserName,
                                                   final boolean bFullUI)
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

    // User name field
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel (EWebBasicsText.EMAIL_ADDRESS.getDisplayText (aDisplayLocale))
                                                 .setCtrl (new HCEdit (CLogin.REQUEST_ATTR_USERID, sPreselectedUserName).setID (sIDUserName)));

    // Password field
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel (EWebBasicsText.LOGIN_FIELD_PASSWORD.getDisplayText (aDisplayLocale))
                                                 .setCtrl (new HCEditPassword (CLogin.REQUEST_ATTR_PASSWORD).setID (sIDPassword)));

    // Placeholder for error message
    aForm.addChild (new HCDiv ().setID (sIDErrorField).addStyle (CCSSProperties.MARGIN.newValue ("4px 0")));

    // Login button
    final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aLEC));
    final JSPackage aOnClick = new JSPackage ();
    aOnClick.add (CAppJS.viewLogin ()
                        .arg (CAjaxPublic.LOGIN.getInvocationURI (aRequestScope))
                        .arg (new JSAssocArray ().add (CLogin.REQUEST_ATTR_USERID, JQuery.idRef (sIDUserName).val ())
                                                 .add (CLogin.REQUEST_ATTR_PASSWORD, JQuery.idRef (sIDPassword).val ()))
                        .arg (sIDErrorField));
    aOnClick._return (false);
    aToolbar.addSubmitButton (EWebBasicsText.LOGIN_BUTTON_SUBMIT.getDisplayText (aDisplayLocale), aOnClick);
    return aForm;
  }
}
