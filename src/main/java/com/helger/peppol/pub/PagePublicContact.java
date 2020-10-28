/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.email.EmailAddress;
import com.helger.commons.email.EmailAddressHelper;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCHiddenField;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.AppConfig;
import com.helger.peppol.app.AppHelper;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.photon.bootstrap4.button.BootstrapSubmitButton;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.interror.InternalErrorSettings;
import com.helger.photon.icon.fontawesome.EFontAwesome4Icon;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.html.google.CaptchaStateSessionSingleton;
import com.helger.photon.uicore.html.google.HCReCaptchaV2;
import com.helger.photon.uicore.html.google.ReCaptchaServerSideValidator;
import com.helger.photon.uicore.html.select.HCExtSelect;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.autosize.HCTextAreaAutosize;
import com.helger.smtp.data.EEmailType;
import com.helger.smtp.data.EmailData;
import com.helger.smtp.scope.ScopedMailAPI;

public final class PagePublicContact extends AbstractAppWebPage
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PagePublicContact.class);
  private static final String FIELD_NAME = "name";
  private static final String FIELD_EMAIL = "email";
  private static final String FIELD_TOPIC = "reason";
  private static final String FIELD_TEXT = "topic";
  private static final String FIELD_CAPTCHA = "captcha";

  public PagePublicContact (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Contact form");
  }

  private static boolean _isSpamBody (@Nonnull final String sTopic)
  {
    return sTopic.contains ("https://bloggybro.com") || sTopic.contains ("https://seoclerkspro.com/");
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    final String sRecaptchWebKey = AppConfig.getRecaptchaWebKey ();
    final String sRecaptchSecretKey = AppConfig.getRecaptchaSecretKey ();
    final boolean bRecaptchaEnabled = StringHelper.hasText (sRecaptchWebKey) && StringHelper.hasText (sRecaptchSecretKey);

    aNodeList.addChild (p ("If you have general questions concerning Peppol technology, you may contact me using the form below. Please be aware, that I run this page on a voluntary basis and that the answers you may receive are my personal answers and not official OpenPEPPOL answers."));

    boolean bShowForm = true;
    final FormErrorList aFormErrors = new FormErrorList ();
    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      final String sName = StringHelper.trim (aWPEC.params ().getAsString (FIELD_NAME));
      final String sEmail = StringHelper.trim (aWPEC.params ().getAsString (FIELD_EMAIL));
      final String sTopic = aWPEC.params ().getAsString (FIELD_TOPIC);
      final String sText = StringHelper.trim (aWPEC.params ().getAsString (FIELD_TEXT));
      final String sReCaptchaResponse = StringHelper.trim (aWPEC.params ().getAsString (HCReCaptchaV2.RESPONSE_PARAMETER_NAME));

      if (StringHelper.hasNoText (sName))
        aFormErrors.addFieldError (FIELD_NAME, "Your name must be provided.");
      if (StringHelper.hasNoText (sEmail))
        aFormErrors.addFieldError (FIELD_EMAIL, "Your email address must be provided.");
      else
        if (!EmailAddressHelper.isValid (sEmail))
          aFormErrors.addFieldError (FIELD_EMAIL, "The provided email address is invalid.");
      if (StringHelper.hasNoText (sText))
        aFormErrors.addFieldError (FIELD_TEXT, "A message text must be provided.");

      if (bRecaptchaEnabled)
        if (aFormErrors.isEmpty () || StringHelper.hasText (sReCaptchaResponse))
        {
          if (!CaptchaStateSessionSingleton.getInstance ().isChecked ())
          {
            // Check only if no other errors occurred
            if (ReCaptchaServerSideValidator.check (sRecaptchSecretKey, sReCaptchaResponse).isFailure ())
              aFormErrors.addFieldError (FIELD_CAPTCHA, "Please confirm you are not a robot!");
            else
              CaptchaStateSessionSingleton.getInstance ().setChecked ();
          }
        }

      if (aFormErrors.isEmpty ())
      {
        if (!_isSpamBody (sTopic))
        {
          final EmailData aEmailData = new EmailData (EEmailType.TEXT);
          aEmailData.setFrom (new EmailAddress ("peppol-practical@helger.com", "Peppol Practical"));
          aEmailData.to ().add (new EmailAddress ("philip@helger.com"));
          aEmailData.replyTo ().add (new EmailAddress (sEmail, sName));
          aEmailData.setSubject ("[" + AppHelper.getApplicationTitle () + "] Contact Form - " + sName);

          final StringBuilder aSB = new StringBuilder ();
          aSB.append ("Contact form from " + AppHelper.getApplicationTitle () + " was filled out.\n\n");
          aSB.append ("Name: ").append (sName).append ("\n");
          aSB.append ("Email: ").append (sEmail).append ("\n");
          aSB.append ("Topic: ").append (sTopic).append ("\n");
          aSB.append ("Text:\n").append (sText).append ("\n");
          aEmailData.setBody (aSB.toString ());

          ScopedMailAPI.getInstance ().queueMail (InternalErrorSettings.getSMTPSettings (), aEmailData);
        }
        else
          LOGGER.info ("Ignoring spam contact form");

        aNodeList.addChild (success ("Thank you for your message. Please note that I run this page on a voluntary basis on my expenses - you may consider a donation :)"));
        bShowForm = false;
      }
    }

    if (bShowForm)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (getUIHandler ().createFormSelf (aWPEC));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Your name")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_NAME)))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_NAME)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Your email address")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_EMAIL)))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_EMAIL)));

      final HCExtSelect aSelect = new HCExtSelect (new RequestField (FIELD_TOPIC));
      aSelect.addOption ("SML/SMK");
      aSelect.addOption ("SMP");
      aSelect.addOption ("AccessPoint (AP)/AS4");
      aSelect.addOption ("Peppol Directory");
      aSelect.addOption ("Validation");
      aSelect.addOption ("CEF");
      aSelect.addOption ("General question");
      aSelect.addOptionPleaseSelect (aDisplayLocale);
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Topic")
                                                   .setCtrl (aSelect)
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_TOPIC)));

      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Your message")
                                                   .setCtrl (new HCTextAreaAutosize (new RequestField (FIELD_TEXT)).setRows (5))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_TEXT)));

      if (bRecaptchaEnabled)
        if (!CaptchaStateSessionSingleton.getInstance ().isChecked ())
        {
          // Add visible Captcha
          aForm.addFormGroup (new BootstrapFormGroup ().setCtrl (HCReCaptchaV2.create (sRecaptchWebKey, aDisplayLocale))
                                                       .setErrorList (aFormErrors.getListOfField (FIELD_CAPTCHA)));
        }

      aForm.addChild (new HCHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM));
      aForm.addChild (new BootstrapSubmitButton ().addChild ("Send message").setIcon (EFontAwesome4Icon.PAPER_PLANE));
    }
  }
}
