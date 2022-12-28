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

import java.util.Locale;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.email.EmailAddress;
import com.helger.commons.email.EmailAddressHelper;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCHiddenField;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.AppConfig;
import com.helger.peppol.app.AppHelper;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.photon.audit.AuditHelper;
import com.helger.photon.bootstrap4.CBootstrapCSS;
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
  private static final String FIELD_HONEYPOT = "title";
  private static final String FIELD_TEXT = "topic";
  private static final String FIELD_CAPTCHA = "captcha";

  public PagePublicContact (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Contact form");
  }

  // Must only contain lowercase values
  private static final String [] SPAM_KEYS = new String [] { "Get Yours Here:",
                                                             "boostleadgeneration.com",
                                                             "dogloverclub.store",
                                                             "idgod.ch",
                                                             "magicmat.shop",
                                                             "medicopostura.com",
                                                             "oakley sunglasses",
                                                             "ray-ban sunglasses",
                                                             "topfakeid.com",
                                                             "www.untouchableiptv.com",
                                                             "//www.alecpow.com",
                                                             "//bit.ly",
                                                             "//bloggybro.com",
                                                             "//cutt.ly",
                                                             "//digitalsy.org.uk",
                                                             "//earningradar.com",
                                                             "//fixhacksite.com",
                                                             "//geekboy.co",
                                                             "//getcontent.rocks",
                                                             "//jgmbh.de",
                                                             "//magly.space",
                                                             "//screenshot.photos",
                                                             "//seoclerkspro.com",
                                                             "//shipped-order.com",
                                                             "//socialvideoschedule.com ",
                                                             "//speed-seo.net/",
                                                             "//talkwithcustomer.com",
                                                             "//talkwithwebvisitors.com",
                                                             "//thecanadianreport.ca",
                                                             "//yazing.com",
                                                             "//www.ads-that-stay-up-forever.xyz",
                                                             "//www.alecpow.com",
                                                             "//www.biglep.com",
                                                             "//www.electronicdomains.com",
                                                             "//www.follmex.",
                                                             "//www.godlikeproductions.com",
                                                             "//www.interactivewise.com",
                                                             "//www.speed-seo.net",
                                                             "//www.talkwithcustomer.com",
                                                             "//www.talkwithwebvisitors.com",
                                                             "//www.targeted-visitors-4yoursite.xyz",
                                                             "//www.thepricer.org",
                                                             "//www.vidnami.com",
                                                             "//www.zerocost-ad-posting.xyz" };

  private static boolean _isSpamBody (@Nonnull final String sTopic)
  {
    final String sLowerCase = sTopic.toLowerCase (Locale.US);
    for (final String s : SPAM_KEYS)
      if (sLowerCase.contains (s))
        return true;
    return false;
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    final String sRecaptchWebKey = AppConfig.getRecaptchaWebKey ();
    final String sRecaptchSecretKey = AppConfig.getRecaptchaSecretKey ();
    final boolean bRecaptchaEnabled = StringHelper.hasText (sRecaptchWebKey) &&
                                      StringHelper.hasText (sRecaptchSecretKey);

    aNodeList.addChild (p ("If you have general questions concerning Peppol technology, you may contact me using the form below. Please be aware, that I run this page on a voluntary basis and that the answers you may receive are my personal answers and not official OpenPeppol answers."));

    boolean bShowForm = true;
    final FormErrorList aFormErrors = new FormErrorList ();
    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      final String sName = aWPEC.params ().getAsStringTrimmed (FIELD_NAME);
      final String sEmail = aWPEC.params ().getAsStringTrimmed (FIELD_EMAIL);
      final String sTopic = aWPEC.params ().getAsStringTrimmed (FIELD_TOPIC);
      final String sHoneyPot = aWPEC.params ().getAsString (FIELD_HONEYPOT);
      final String sText = aWPEC.params ().getAsStringTrimmed (FIELD_TEXT);
      final String sReCaptchaResponse = aWPEC.params ().getAsStringTrimmed (HCReCaptchaV2.RESPONSE_PARAMETER_NAME);

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
        if (StringHelper.hasText (sHoneyPot))
        {
          LOGGER.warn ("The Honeypot field was filled out - not sending an email from the contact page");
          if (GlobalDebug.isDebugMode ())
            aNodeList.addChild (h2 ("You trapped into the Honeypot"));
          AuditHelper.onAuditExecuteSuccess ("contact-spam-protection", sEmail);
        }
        else
          if (_isSpamBody (sText))
          {
            LOGGER.warn ("Ignoring spam contact form from '" + sEmail + "' with topic '" + sTopic + "'");
            if (GlobalDebug.isDebugMode ())
              aNodeList.addChild (h2 ("You are trying to send Spam"));
            AuditHelper.onAuditExecuteSuccess ("contact-spam-protection", sEmail, sTopic);
          }
          else
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

            LOGGER.info ("Sending contact form from '" +
                         sName +
                         "' and email '" +
                         sEmail +
                         "' with topic '" +
                         sTopic +
                         "'");
            ScopedMailAPI.getInstance ().queueMail (InternalErrorSettings.getSMTPSettings (), aEmailData);
          }

        aNodeList.addChild (success ("Thank you for your message. Please note that I run this page on a voluntary basis on my expenses - you may consider a donation."));
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
      aSelect.addOption ("Validation");
      aSelect.addOption ("Peppol Directory");
      aSelect.addOption ("CEF");
      aSelect.addOption ("General question");
      aSelect.addOptionPleaseSelect (aDisplayLocale);
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Topic")
                                                   .setCtrl (aSelect)
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_TOPIC)));

      {
        // Honeypot field
        // If this field is filled out, the filler is a bot
        final BootstrapFormGroup aBG = new BootstrapFormGroup ().setLabelMandatory ("Message title")
                                                                .setCtrl (new HCEdit (new RequestField (FIELD_HONEYPOT)))
                                                                .setErrorList (aFormErrors.getListOfField (FIELD_HONEYPOT));
        aBG.cssClasses ().addClass (CBootstrapCSS.D_NONE);
        aForm.addFormGroup (aBG);
      }

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
