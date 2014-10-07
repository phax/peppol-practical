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

import java.util.Collection;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.bootstrap3.form.BootstrapForm;
import com.helger.bootstrap3.form.BootstrapFormGroup;
import com.helger.bootstrap3.form.EBootstrapFormType;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.name.ComparatorHasDisplayName;
import com.helger.html.hc.html.HCCheckBox;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCHiddenField;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.crm.CRMGroupManager;
import com.helger.peppol.crm.CRMSubscriberManager;
import com.helger.peppol.crm.ICRMGroup;
import com.helger.peppol.mgr.MetaManager;
import com.helger.validation.error.FormErrors;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webbasics.form.RequestFieldBoolean;
import com.helger.webctrls.masterdata.HCSalutationSelect;
import com.helger.webpages.AbstractWebPageExt;

public final class PagePublicNewsletterSubscribe extends AbstractWebPageExt <WebPageExecutionContext>
{
  private static final String FIELD_SALUTATION = "salutation";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_EMAIL_ADDRESS = "emailaddress";
  private static final String FIELD_GROUP = "group";

  public PagePublicNewsletterSubscribe (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Newsletter subscription");
  }

  @Override
  protected void fillContent (final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final CRMGroupManager aCRMGroupMgr = MetaManager.getCRMGroupMgr ();
    final CRMSubscriberManager aCRMSubscriberMgr = MetaManager.getCRMSubscriberMgr ();
    final FormErrors aFormErrors = new FormErrors ();

    final BootstrapForm aForm = new BootstrapForm (aWPEC.getSelfHref (), EBootstrapFormType.HORIZONTAL);
    aForm.setLeft (3);
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Your salutation")
                                                 .setCtrl (new HCSalutationSelect (new RequestField (FIELD_SALUTATION),
                                                                                   aDisplayLocale))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_SALUTATION)));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Your name")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_NAME)))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_NAME)));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Your email address")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_EMAIL_ADDRESS)))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_EMAIL_ADDRESS)));
    {
      final Collection <? extends ICRMGroup> aAllCRMGroups = aCRMGroupMgr.getAllCRMGroups ();
      if (false && aAllCRMGroups.size () == 1)
      {
        // No need for selection
        aForm.addChild (new HCHiddenField (FIELD_GROUP, ContainerHelper.getFirstElement (aAllCRMGroups).getID ()));
      }
      else
      {
        final HCNodeList aGroupCtrl = new HCNodeList ();
        for (final ICRMGroup aCRMGroup : ContainerHelper.getSorted (aAllCRMGroups,
                                                                    new ComparatorHasDisplayName <ICRMGroup> (aDisplayLocale)))
        {
          final RequestFieldBoolean aRFB = new RequestFieldBoolean (FIELD_GROUP, false);
          aGroupCtrl.addChild (new HCDiv ().addChild (new HCCheckBox (aRFB).setValue (aCRMGroup.getID ()))
                                           .addChild (" " + aCRMGroup.getDisplayName ()));
        }
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("The groups you want to subscribe to")
                                                     .setCtrl (aGroupCtrl)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_GROUP)));
      }
    }
    aNodeList.addChild (aForm);
  }
}
