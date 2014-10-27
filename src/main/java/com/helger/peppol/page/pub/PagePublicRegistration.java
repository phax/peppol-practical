package com.helger.peppol.page.pub;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.appbasics.security.AccessManager;
import com.helger.appbasics.security.password.GlobalPasswordSettings;
import com.helger.appbasics.security.user.IUser;
import com.helger.bootstrap3.alert.BootstrapErrorBox;
import com.helger.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.ext.BootstrapSecurityUI;
import com.helger.bootstrap3.table.BootstrapTableForm;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.email.EmailAddressUtils;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.SMap;
import com.helger.html.hc.CHCParam;
import com.helger.html.hc.html.AbstractHCForm;
import com.helger.html.hc.html.HCCol;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCEditPassword;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.CApp;
import com.helger.peppol.app.ui.AppCommonUI;
import com.helger.peppol.page.AbstractAppWebPageExt;
import com.helger.validation.error.FormErrors;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.form.RequestField;

public final class PagePublicRegistration extends AbstractAppWebPageExt
{
  private static final String FIELD_FIRSTNAME = "firstname";
  private static final String FIELD_LASTNAME = "lastname";
  private static final String FIELD_EMAIL1 = "email";
  private static final String FIELD_EMAIL2 = "emailconfirm";
  private static final String FIELD_PASSWORD = "password";
  private static final String FIELD_PASSWORD_CONFIRM = "passwordconfirm";

  public PagePublicRegistration (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Registration");
  }

  protected void validateAndSaveInputParameters (@Nonnull final WebPageExecutionContext aWPEC,
                                                 @Nonnull final FormErrors aFormErrors)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    final String sFirstName = aWPEC.getAttributeAsString (FIELD_FIRSTNAME);
    final String sLastName = aWPEC.getAttributeAsString (FIELD_LASTNAME);
    final String sEmailAddress = aWPEC.getAttributeAsString (FIELD_EMAIL1);
    final String sEmailAddressConfirm = aWPEC.getAttributeAsString (FIELD_EMAIL2);
    final String sPlainTextPassword = aWPEC.getAttributeAsString (FIELD_PASSWORD);
    final String sPlainTextPasswordConfirm = aWPEC.getAttributeAsString (FIELD_PASSWORD_CONFIRM);

    if (StringHelper.hasNoText (sFirstName))
      aFormErrors.addFieldError (FIELD_FIRSTNAME, "A first name must be provded!!");

    if (StringHelper.hasNoText (sLastName))
      aFormErrors.addFieldError (FIELD_LASTNAME, "A last name must be provded!!");

    if (StringHelper.hasNoText (sEmailAddress))
      aFormErrors.addFieldError (FIELD_EMAIL1, "An email address must be provded!!");
    else
      if (!EmailAddressUtils.isValid (sEmailAddress))
        aFormErrors.addFieldError (FIELD_EMAIL1, "The provided email address is not valid!");
      else
        if (!sEmailAddress.equals (sEmailAddressConfirm))
        {
          aFormErrors.addFieldError (FIELD_EMAIL2, "The two provided email addresses don't match!");
        }
        else
        {
          IUser aUser = AccessManager.getInstance ().getUserOfLoginName (sEmailAddress);
          if (aUser == null)
            aUser = AccessManager.getInstance ().getUserOfEmailAddress (sEmailAddress);
          if (aUser != null)
            aFormErrors.addFieldError (FIELD_EMAIL1, "Another user with the same email address is already registered!");
        }

    final List <String> aPasswordErrors = GlobalPasswordSettings.getPasswordConstraintList ()
                                                                .getInvalidPasswordDescriptions (sPlainTextPassword,
                                                                                                 aDisplayLocale);
    for (final String sPasswordError : aPasswordErrors)
      aFormErrors.addFieldError (FIELD_PASSWORD, sPasswordError);
    if (!EqualsUtils.equals (sPlainTextPassword, sPlainTextPasswordConfirm))
      aFormErrors.addFieldError (FIELD_PASSWORD_CONFIRM, "The two provided passwords don't match!");

    if (aFormErrors.isEmpty ())
    {
      // Create new user
      final IUser aCreatedUser = AccessManager.getInstance ().createNewUser (sEmailAddress,
                                                                             sEmailAddress,
                                                                             sPlainTextPassword,
                                                                             sFirstName,
                                                                             sLastName,
                                                                             aDisplayLocale,
                                                                             new SMap (),
                                                                             false);
      if (aCreatedUser == null)
        aNodeList.addChild (BootstrapErrorBox.create ("Error creating the new user!"));
      else
      {
        // Assign new user to user group
        if (AccessManager.getInstance ()
                         .assignUserToUserGroup (CApp.USERGROUP_VIEW_ID, aCreatedUser.getID ())
                         .isUnchanged ())
          aNodeList.addChild (BootstrapErrorBox.create ("Error assigning the user to the user group!"));
        else
        {
          aNodeList.addChild (BootstrapSuccessBox.create ("You have been registered successfully! You may now login with your email address '" +
                                                          sEmailAddress +
                                                          "' and the selected password."));
          // Show login form
          aNodeList.addChild (AppCommonUI.createViewLoginForm (aWPEC, sEmailAddress, true, false));
        }
      }
    }
  }

  protected void showInputForm (final Locale aDisplayLocale,
                                final AbstractHCForm <?> aForm,
                                final FormErrors aFormErrors)
  {
    final BootstrapTableForm aTable = aForm.addAndReturnChild (new BootstrapTableForm (new HCCol (230),
                                                                                       HCCol.star (),
                                                                                       new HCCol (30)));

    // User data
    aTable.createItemRow ()
          .setLabelMandatory ("First name")
          .setCtrl (new HCEdit (new RequestField (FIELD_FIRSTNAME)))
          .setErrorList (aFormErrors.getListOfField (FIELD_FIRSTNAME));
    aTable.createItemRow ()
          .setLabelMandatory ("Last name")
          .setCtrl (new HCEdit (new RequestField (FIELD_LASTNAME)))
          .setErrorList (aFormErrors.getListOfField (FIELD_LASTNAME));
    aTable.createItemRow ()
          .setLabelMandatory ("Email address")
          .setCtrl (new HCEdit (new RequestField (FIELD_EMAIL1)))
          .setErrorList (aFormErrors.getListOfField (FIELD_EMAIL1));
    aTable.createItemRow ()
          .setLabelMandatory ("Email address (confirmation)")
          .setCtrl (new HCEdit (new RequestField (FIELD_EMAIL2)))
          .setErrorList (aFormErrors.getListOfField (FIELD_EMAIL2));
    aTable.createItemRow ()
          .setLabelMandatory ("Password")
          .setCtrl (new HCEditPassword (FIELD_PASSWORD))
          .setNote (BootstrapSecurityUI.createPasswordConstraintTip (aDisplayLocale))
          .setErrorList (aFormErrors.getListOfField (FIELD_PASSWORD));
    aTable.createItemRow ()
          .setLabelMandatory ("Password (confirmation)")
          .setCtrl (new HCEditPassword (FIELD_PASSWORD_CONFIRM))
          .setNote (BootstrapSecurityUI.createPasswordConstraintTip (aDisplayLocale))
          .setErrorList (aFormErrors.getListOfField (FIELD_PASSWORD_CONFIRM));
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final FormErrors aFormErrors = new FormErrors ();
    boolean bShowForm = true;

    if (aWPEC.hasSubAction (CHCParam.ACTION_SAVE))
    {
      // try to save
      validateAndSaveInputParameters (aWPEC, aFormErrors);
      if (aFormErrors.isEmpty ())
      {
        // Save successful
        bShowForm = false;
      }
      else
        aWPEC.getNodeList ().addChild (getStyler ().createIncorrectInputBox (aWPEC));
    }

    if (bShowForm)
    {
      final AbstractHCForm <?> aForm = aWPEC.getNodeList ().addAndReturnChild (createFormSelf (aWPEC));
      showInputForm (aWPEC.getDisplayLocale (), aForm, aFormErrors);

      final BootstrapButtonToolbar aToolbar = new BootstrapButtonToolbar (aWPEC);
      aToolbar.addHiddenField (CHCParam.PARAM_ACTION, ACTION_CREATE);
      aToolbar.addHiddenField (CHCParam.PARAM_SUBACTION, ACTION_SAVE);
      // Save button
      aToolbar.addSubmitButtonSave (aWPEC.getDisplayLocale ());
      // Cancel button
      aToolbar.addButtonCancel (aWPEC.getDisplayLocale ());
      aForm.addChild (aToolbar);
    }
  }
}
