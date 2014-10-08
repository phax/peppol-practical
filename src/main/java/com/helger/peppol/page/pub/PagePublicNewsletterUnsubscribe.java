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
package com.helger.peppol.page.pub;

import java.util.Set;

import javax.annotation.Nonnull;

import com.helger.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.form.BootstrapForm;
import com.helger.bootstrap3.form.BootstrapFormGroup;
import com.helger.bootstrap3.form.EBootstrapFormType;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.email.EmailAddressUtils;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.CHCParam;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.crm.CRMSubscriberManager;
import com.helger.peppol.crm.ICRMGroup;
import com.helger.peppol.crm.ICRMSubscriber;
import com.helger.peppol.mgr.MetaManager;
import com.helger.validation.error.FormErrors;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webctrls.custom.EDefaultIcon;
import com.helger.webpages.AbstractWebPageExt;

public final class PagePublicNewsletterUnsubscribe extends AbstractWebPageExt <WebPageExecutionContext>
{
  private static final String FIELD_EMAIL_ADDRESS = "emailaddress";

  public PagePublicNewsletterUnsubscribe (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Unsubscribe from newsletter");
  }

  @Override
  protected void fillContent (final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CRMSubscriberManager aCRMSubscriberMgr = MetaManager.getCRMSubscriberMgr ();
    final FormErrors aFormErrors = new FormErrors ();

    if (aWPEC.hasAction (ACTION_SAVE))
    {
      final String sEmailAddress = aWPEC.getAttributeAsString (FIELD_EMAIL_ADDRESS);
      ICRMSubscriber aCRMSubscriber = null;

      if (StringHelper.hasNoText (sEmailAddress))
        aFormErrors.addFieldError (FIELD_EMAIL_ADDRESS, "You must provide your email address!");
      else
        if (!EmailAddressUtils.isValid (sEmailAddress))
          aFormErrors.addFieldError (FIELD_EMAIL_ADDRESS, "The provided email address is invalid!");
        else
        {
          aCRMSubscriber = aCRMSubscriberMgr.getCRMSubscriberOfEmailAddress (sEmailAddress);
          if (aCRMSubscriber == null)
            aFormErrors.addFieldError (FIELD_EMAIL_ADDRESS,
                                       "The provided email address is not registered to any mailing list!");
        }

      if (aFormErrors.isEmpty ())
      {
        // Update an existing one
        aCRMSubscriberMgr.updateCRMSubscriberGroupAssignments (aCRMSubscriber.getID (), (Set <ICRMGroup>) null);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Successfully unsubscribed '" +
                                                                 sEmailAddress +
                                                                 "' from all mailing lists"));
      }
      else
      {
        aNodeList.addChild (getStyler ().createIncorrectInputBox (aWPEC));
      }
    }

    final BootstrapForm aForm = new BootstrapForm (aWPEC.getSelfHref (), EBootstrapFormType.HORIZONTAL);
    aForm.setLeft (4);
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Your email address")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_EMAIL_ADDRESS)))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_EMAIL_ADDRESS)));

    // Toolbar
    final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
    aToolbar.addHiddenField (CHCParam.PARAM_ACTION, ACTION_SAVE);
    aToolbar.addSubmitButton ("Unsubscribe", EDefaultIcon.YES);

    aNodeList.addChild (aForm);
  }
}
