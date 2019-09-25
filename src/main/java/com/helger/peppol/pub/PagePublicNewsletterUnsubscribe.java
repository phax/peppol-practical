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

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.email.EmailAddressHelper;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.crm.CRMSubscriberManager;
import com.helger.peppol.crm.ICRMSubscriber;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.photon.bootstrap4.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.page.WebPageExecutionContext;

public final class PagePublicNewsletterUnsubscribe extends AbstractAppWebPage
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
    final CRMSubscriberManager aCRMSubscriberMgr = PPMetaManager.getCRMSubscriberMgr ();
    final FormErrorList aFormErrors = new FormErrorList ();

    if (aWPEC.hasAction (CPageParam.ACTION_SAVE))
    {
      final String sEmailAddress = aWPEC.params ().getAsString (FIELD_EMAIL_ADDRESS);
      ICRMSubscriber aCRMSubscriber = null;

      if (StringHelper.hasNoText (sEmailAddress))
        aFormErrors.addFieldError (FIELD_EMAIL_ADDRESS, "You must provide your email address!");
      else
        if (!EmailAddressHelper.isValid (sEmailAddress))
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
        aCRMSubscriberMgr.updateCRMSubscriberGroupAssignments (aCRMSubscriber.getID (), null);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Successfully unsubscribed '" +
                                                                 sEmailAddress +
                                                                 "' from all mailing lists"));
      }
      else
      {
        aNodeList.addChild (getUIHandler ().createIncorrectInputBox (aWPEC));
      }
    }

    final BootstrapForm aForm = getUIHandler ().createFormSelf (aWPEC);
    aForm.setLeft (3);
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Your email address")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_EMAIL_ADDRESS)))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_EMAIL_ADDRESS)));

    // Toolbar
    final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
    aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_SAVE);
    aToolbar.addSubmitButton ("Unsubscribe", EDefaultIcon.YES);

    aNodeList.addChild (aForm);
  }
}
