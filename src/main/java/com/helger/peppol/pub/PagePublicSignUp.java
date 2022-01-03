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
package com.helger.peppol.pub;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.email.EmailAddressHelper;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.ext.HCExtHelper;
import com.helger.html.hc.html.forms.AbstractHCForm;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCEditPassword;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.AppHelper;
import com.helger.peppol.app.CPPApp;
import com.helger.peppol.ui.AppCommonUI;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.security.mgr.PhotonSecurityManager;
import com.helger.photon.security.password.GlobalPasswordSettings;
import com.helger.photon.security.user.IUser;
import com.helger.photon.security.user.IUserManager;
import com.helger.photon.security.usergroup.IUserGroupManager;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.page.WebPageExecutionContext;

public final class PagePublicSignUp extends AbstractAppWebPage
{
  private static final String FIELD_FIRSTNAME = "firstname";
  private static final String FIELD_LASTNAME = "lastname";
  private static final String FIELD_EMAIL1 = "email";
  private static final String FIELD_EMAIL2 = "emailconfirm";
  private static final String FIELD_PASSWORD = "password";
  private static final String FIELD_PASSWORD_CONFIRM = "passwordconfirm";

  public PagePublicSignUp (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Sign up");
  }

  protected void validateAndSaveInputParameters (@Nonnull final WebPageExecutionContext aWPEC, @Nonnull final FormErrorList aFormErrors)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final IUserManager aUserMgr = PhotonSecurityManager.getUserMgr ();
    final IUserGroupManager aUserGroupMgr = PhotonSecurityManager.getUserGroupMgr ();

    final String sFirstName = aWPEC.params ().getAsString (FIELD_FIRSTNAME);
    final String sLastName = aWPEC.params ().getAsString (FIELD_LASTNAME);
    final String sEmailAddress = aWPEC.params ().getAsString (FIELD_EMAIL1);
    final String sEmailAddressConfirm = aWPEC.params ().getAsString (FIELD_EMAIL2);
    final String sPlainTextPassword = aWPEC.params ().getAsString (FIELD_PASSWORD);
    final String sPlainTextPasswordConfirm = aWPEC.params ().getAsString (FIELD_PASSWORD_CONFIRM);

    if (StringHelper.hasNoText (sFirstName))
      aFormErrors.addFieldError (FIELD_FIRSTNAME, "A first name must be provded!!");

    if (StringHelper.hasNoText (sLastName))
      aFormErrors.addFieldError (FIELD_LASTNAME, "A last name must be provded!!");

    if (StringHelper.hasNoText (sEmailAddress))
      aFormErrors.addFieldError (FIELD_EMAIL1, "An email address must be provded!!");
    else
      if (!EmailAddressHelper.isValid (sEmailAddress))
        aFormErrors.addFieldError (FIELD_EMAIL1, "The provided email address is not valid!");
      else
        if (!sEmailAddress.equals (sEmailAddressConfirm))
        {
          aFormErrors.addFieldError (FIELD_EMAIL2, "The two provided email addresses don't match!");
        }
        else
        {
          IUser aUser = aUserMgr.getUserOfLoginName (sEmailAddress);
          if (aUser == null)
            aUser = aUserMgr.getUserOfEmailAddress (sEmailAddress);
          if (aUser != null)
            aFormErrors.addFieldError (FIELD_EMAIL1, "Another user with the same email address is already registered!");
        }

    final List <String> aPasswordErrors = GlobalPasswordSettings.getPasswordConstraintList ()
                                                                .getInvalidPasswordDescriptions (sPlainTextPassword, aDisplayLocale);
    for (final String sPasswordError : aPasswordErrors)
      aFormErrors.addFieldError (FIELD_PASSWORD, "Error: " + sPasswordError);
    if (!aFormErrors.hasEntryForField (FIELD_PASSWORD) && !EqualsHelper.equals (sPlainTextPassword, sPlainTextPasswordConfirm))
      aFormErrors.addFieldError (FIELD_PASSWORD_CONFIRM, "The two provided passwords don't match!");

    if (aFormErrors.isEmpty ())
    {
      final String sDescription = "User signed up at " +
                                  PDTFactory.getCurrentLocalDateTime ().toString () +
                                  " from " +
                                  aWPEC.getRequestScope ().getRemoteAddr ();

      // Create new user
      final IUser aCreatedUser = aUserMgr.createNewUser (sEmailAddress,
                                                         sEmailAddress,
                                                         sPlainTextPassword,
                                                         sFirstName,
                                                         sLastName,
                                                         sDescription,
                                                         aDisplayLocale,
                                                         (Map <String, String>) null,
                                                         false);
      if (aCreatedUser == null)
        aNodeList.addChild (error ("Error creating the new user!"));
      else
      {
        // Assign new user to user group
        if (aUserGroupMgr.assignUserToUserGroup (CPPApp.USERGROUP_VIEW_ID, aCreatedUser.getID ()).isUnchanged ())
          aNodeList.addChild (error ("Error assigning the user to the user group!"));
        else
        {
          aNodeList.addChild (success ("You have been registered successfully! You may now login with your email address '" +
                                       sEmailAddress +
                                       "' and the selected password."));
          // Show login form
          aNodeList.addChild (AppCommonUI.createViewLoginForm (aWPEC, sEmailAddress, false));
        }
      }
    }
  }

  protected void showInputForm (@Nonnull final Locale aDisplayLocale,
                                @Nonnull final AbstractHCForm <?> aForm,
                                @Nonnull final FormErrorList aFormErrors)
  {
    final List <IHCNode> aPasswordHelpText = HCExtHelper.list2divList (GlobalPasswordSettings.getPasswordConstraintList ()
                                                                                             .getAllPasswordConstraintDescriptions (aDisplayLocale));

    aForm.addChild (info ("Sign up to ").addChild (strong (AppHelper.getApplicationTitle ()))
                                        .addChild (" easily by filling out this form. No further information and no credit card information is needed."));

    final BootstrapForm aRealForm = (BootstrapForm) aForm;
    aRealForm.setLeft (3);
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("First name")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_FIRSTNAME)))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_FIRSTNAME)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Last name")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_LASTNAME)))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_LASTNAME)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Email address")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_EMAIL1)))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_EMAIL1)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Email address (confirmation)")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_EMAIL2)))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_EMAIL2)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Password")
                                                     .setCtrl (new HCEditPassword (FIELD_PASSWORD))
                                                     .setHelpText (aPasswordHelpText)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PASSWORD)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Password (confirmation)")
                                                     .setCtrl (new HCEditPassword (FIELD_PASSWORD_CONFIRM))
                                                     .setHelpText (aPasswordHelpText)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PASSWORD_CONFIRM)));
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final FormErrorList aFormErrors = new FormErrorList ();
    boolean bShowForm = true;

    if (aWPEC.hasSubAction (CPageParam.ACTION_SAVE))
    {
      // try to save
      validateAndSaveInputParameters (aWPEC, aFormErrors);
      if (aFormErrors.isEmpty ())
      {
        // Save successful
        bShowForm = false;
      }
      else
        aNodeList.addChild (getUIHandler ().createIncorrectInputBox (aWPEC));
    }

    if (bShowForm)
    {
      final AbstractHCForm <?> aForm = aNodeList.addAndReturnChild (getUIHandler ().createFormSelf (aWPEC));
      showInputForm (aDisplayLocale, aForm, aFormErrors);

      final BootstrapButtonToolbar aToolbar = new BootstrapButtonToolbar (aWPEC);
      aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_CREATE);
      aToolbar.addHiddenField (CPageParam.PARAM_SUBACTION, CPageParam.ACTION_SAVE);
      // Save button
      aToolbar.addSubmitButton ("Sign up now", EDefaultIcon.YES);
      aForm.addChild (aToolbar);
    }
  }
}
