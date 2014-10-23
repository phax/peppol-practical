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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;

import com.helger.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.form.BootstrapCheckBox;
import com.helger.bootstrap3.form.BootstrapForm;
import com.helger.bootstrap3.form.BootstrapFormGroup;
import com.helger.bootstrap3.form.EBootstrapFormType;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.email.EmailAddressUtils;
import com.helger.commons.name.ComparatorHasDisplayName;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.CHCParam;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCHiddenField;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.masterdata.person.ESalutation;
import com.helger.peppol.crm.CRMGroupManager;
import com.helger.peppol.crm.CRMSubscriberManager;
import com.helger.peppol.crm.ICRMGroup;
import com.helger.peppol.crm.ICRMSubscriber;
import com.helger.peppol.mgr.MetaManager;
import com.helger.validation.error.FormErrors;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webbasics.form.RequestFieldBooleanMultiValue;
import com.helger.webctrls.custom.EDefaultIcon;
import com.helger.webctrls.masterdata.HCSalutationSelect;
import com.helger.webctrls.page.AbstractWebPageExt;

public final class PagePublicNewsletterSubscribe extends AbstractWebPageExt <WebPageExecutionContext>
{
  private static final String FIELD_SALUTATION = "salutation";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_EMAIL_ADDRESS = "emailaddress";
  private static final String FIELD_GROUP = "group";

  public PagePublicNewsletterSubscribe (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Subscribe to newsletter");
  }

  @Override
  protected void fillContent (final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final CRMGroupManager aCRMGroupMgr = MetaManager.getCRMGroupMgr ();
    final CRMSubscriberManager aCRMSubscriberMgr = MetaManager.getCRMSubscriberMgr ();
    final FormErrors aFormErrors = new FormErrors ();
    List <String> aSelectedCRMGroupIDs = null;

    if (aWPEC.hasAction (ACTION_SAVE))
    {
      final String sSalutationID = aWPEC.getAttributeAsString (FIELD_SALUTATION);
      final ESalutation eSalutation = ESalutation.getFromIDOrNull (sSalutationID);
      final String sName = aWPEC.getAttributeAsString (FIELD_NAME);
      final String sEmailAddress = aWPEC.getAttributeAsString (FIELD_EMAIL_ADDRESS);
      aSelectedCRMGroupIDs = aWPEC.getAttributeValues (FIELD_GROUP);
      final Set <ICRMGroup> aSelectedCRMGroups = new HashSet <ICRMGroup> ();
      ICRMSubscriber aSameEmailAddressSubscriber = null;

      if (StringHelper.hasNoText (sName))
        aFormErrors.addFieldError (FIELD_NAME, "You must provide your name!");

      if (StringHelper.hasNoText (sEmailAddress))
        aFormErrors.addFieldError (FIELD_EMAIL_ADDRESS, "You must provide your email address!");
      else
        if (!EmailAddressUtils.isValid (sEmailAddress))
          aFormErrors.addFieldError (FIELD_EMAIL_ADDRESS, "The provided email address is invalid!");
        else
        {
          // Check if the email address is already in use
          aSameEmailAddressSubscriber = aCRMSubscriberMgr.getCRMSubscriberOfEmailAddress (sEmailAddress);
        }

      if (aSelectedCRMGroupIDs != null)
        for (final String sCRMGroupID : aSelectedCRMGroupIDs)
        {
          final ICRMGroup aGroup = aCRMGroupMgr.getCRMGroupOfID (sCRMGroupID);
          if (aGroup == null)
            aFormErrors.addFieldError (FIELD_GROUP, "The selected mailing list is not existing!");
          else
            aSelectedCRMGroups.add (aGroup);
        }
      if (aSelectedCRMGroups.isEmpty ())
        aFormErrors.addFieldError (FIELD_GROUP, "At least one mailing list must be selected!");
      else
      {
        if (aSameEmailAddressSubscriber != null)
        {
          // Merge with existing subscriber
          aSelectedCRMGroups.addAll (aSameEmailAddressSubscriber.getAllAssignedGroups ());
        }
      }

      if (aFormErrors.isEmpty ())
      {
        // Save
        if (aSameEmailAddressSubscriber == null)
        {
          // Create a new one
          aCRMSubscriberMgr.createCRMSubscriber (eSalutation, sName, sEmailAddress, aSelectedCRMGroups);
        }
        else
        {
          // Update an existing one
          aCRMSubscriberMgr.updateCRMSubscriber (aSameEmailAddressSubscriber.getID (),
                                                 eSalutation,
                                                 sName,
                                                 sEmailAddress,
                                                 aSelectedCRMGroups);
        }
        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Successfully subscribed '" +
                                                                 sEmailAddress +
                                                                 "' to mailing lists"));
      }
      else
      {
        aNodeList.addChild (getStyler ().createIncorrectInputBox (aWPEC));
      }
    }

    final BootstrapForm aForm = new BootstrapForm (aWPEC.getSelfHref (), EBootstrapFormType.HORIZONTAL);
    aForm.setLeft (4);
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
      if (aAllCRMGroups.size () == 1)
      {
        // No need for selection - use hidden field
        aForm.addChild (new HCHiddenField (FIELD_GROUP, ContainerHelper.getFirstElement (aAllCRMGroups).getID ()));
      }
      else
      {
        // Show selection
        final HCNodeList aGroups = new HCNodeList ();
        for (final ICRMGroup aCRMGroup : ContainerHelper.getSorted (aAllCRMGroups,
                                                                    new ComparatorHasDisplayName <ICRMGroup> (aDisplayLocale)))
        {
          final String sCRMGroupID = aCRMGroup.getID ();
          final RequestFieldBooleanMultiValue aRFB = new RequestFieldBooleanMultiValue (FIELD_GROUP, sCRMGroupID, false);
          aGroups.addChild (new HCDiv ().addChild (new BootstrapCheckBox (aRFB).setInline (true))
                                        .addChild (" " + aCRMGroup.getDisplayName ()));
        }
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Mailing lists to subscribe to")
                                                     .setCtrl (aGroups)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_GROUP)));
      }
    }

    // Toolbar
    final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
    aToolbar.addHiddenField (CHCParam.PARAM_ACTION, ACTION_SAVE);
    aToolbar.addSubmitButton ("Subscribe", EDefaultIcon.YES);

    aNodeList.addChild (aForm);
  }
}
