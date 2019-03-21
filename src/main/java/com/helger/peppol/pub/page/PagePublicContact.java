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
package com.helger.peppol.pub.page;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.email.EmailAddress;
import com.helger.commons.email.EmailAddressHelper;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCHiddenField;
import com.helger.html.hc.html.grouping.HCP;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.photon.bootstrap4.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap4.button.BootstrapSubmitButton;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.interror.InternalErrorSettings;
import com.helger.photon.icon.fontawesome.EFontAwesome4Icon;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.html.select.HCExtSelect;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.autosize.HCTextAreaAutosize;
import com.helger.smtp.data.EEmailType;
import com.helger.smtp.data.EmailData;
import com.helger.smtp.scope.ScopedMailAPI;

public final class PagePublicContact extends AbstractAppWebPage
{
  private static final String FIELD_NAME = "name";
  private static final String FIELD_EMAIL = "email";
  private static final String FIELD_TOPIC = "reason";
  private static final String FIELD_TEXT = "topic";

  public PagePublicContact (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Contact form");
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    aNodeList.addChild (new HCP ().addChild ("If you have general questions concerning PEPPOL technology, you may contact me using the form below. Please be aware, that I run this page on a voluntary basis and that the answers you may receive are my personal answers and not official OpenPEPPOL answers."));

    boolean bShowForm = true;
    final FormErrorList aFormErrors = new FormErrorList ();
    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      final String sName = StringHelper.trim (aWPEC.params ().getAsString (FIELD_NAME));
      final String sEmail = StringHelper.trim (aWPEC.params ().getAsString (FIELD_EMAIL));
      final String sTopic = aWPEC.params ().getAsString (FIELD_TOPIC);
      final String sText = StringHelper.trim (aWPEC.params ().getAsString (FIELD_TEXT));

      if (StringHelper.hasNoText (sName))
        aFormErrors.addFieldError (FIELD_NAME, "Your name must be provided.");
      if (StringHelper.hasNoText (sEmail))
        aFormErrors.addFieldError (FIELD_EMAIL, "Your email address must be provided.");
      else
        if (!EmailAddressHelper.isValid (sEmail))
          aFormErrors.addFieldError (FIELD_EMAIL, "The provided email address is invalid.");
      if (StringHelper.hasNoText (sText))
        aFormErrors.addFieldError (FIELD_TEXT, "A message text must be provided.");

      if (aFormErrors.isEmpty ())
      {
        final EmailData aEmailData = new EmailData (EEmailType.TEXT);
        aEmailData.setFrom (new EmailAddress ("peppol-practical@helger.com"));
        aEmailData.setTo (new EmailAddress ("ph@helger.com"));
        aEmailData.setReplyTo (new EmailAddress (sEmail, sName));
        aEmailData.setSubject ("[PEPPOL Practical] Contact Form - " + sName);

        final StringBuilder aSB = new StringBuilder ();
        aSB.append ("Contact form from PEPPOL practical was filled out.\n\n");
        aSB.append ("Name: ").append (sName).append ("\n");
        aSB.append ("Email: ").append (sEmail).append ("\n");
        aSB.append ("Topic: ").append (sTopic).append ("\n");
        aSB.append ("Text:\n").append (sText).append ("\n");
        aEmailData.setBody (aSB.toString ());

        ScopedMailAPI.getInstance ().queueMail (InternalErrorSettings.getSMTPSettings (), aEmailData);

        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Thank you for your message. Please note that I run this page on a voluntary basis on my expenses - you may consider a donation :)"));
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
      aSelect.addOption ("AccessPoint (AP)");
      aSelect.addOption ("AS2");
      aSelect.addOption ("AS4");
      aSelect.addOption ("Dictionary");
      aSelect.addOption ("CEF");
      aSelect.addOption ("General question");
      aSelect.addOptionPleaseSelect (aDisplayLocale);
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Topic")
                                                   .setCtrl (aSelect)
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_TOPIC)));

      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Your message")
                                                   .setCtrl (new HCTextAreaAutosize (new RequestField (FIELD_TEXT)).setRows (5))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_TEXT)));

      aForm.addChild (new HCHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM));
      aForm.addChild (new BootstrapSubmitButton ().addChild ("Send message").setIcon (EFontAwesome4Icon.PAPER_PLANE));
    }
  }
}
