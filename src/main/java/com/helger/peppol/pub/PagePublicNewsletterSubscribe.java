/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.email.EmailAddressHelper;
import com.helger.commons.name.IHasDisplayName;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.html.forms.HCCheckBox;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCHiddenField;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.masterdata.person.ESalutation;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.crm.CRMGroupManager;
import com.helger.peppol.crm.CRMSubscriberManager;
import com.helger.peppol.crm.ICRMGroup;
import com.helger.peppol.crm.ICRMSubscriber;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.form.RequestFieldBooleanMultiValue;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.html.select.HCSalutationSelect;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.page.WebPageExecutionContext;

public final class PagePublicNewsletterSubscribe extends AbstractAppWebPage
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
    final CRMGroupManager aCRMGroupMgr = PPMetaManager.getCRMGroupMgr ();
    final CRMSubscriberManager aCRMSubscriberMgr = PPMetaManager.getCRMSubscriberMgr ();
    final FormErrorList aFormErrors = new FormErrorList ();
    ICommonsList <String> aSelectedCRMGroupIDs = null;

    if (aWPEC.hasAction (CPageParam.ACTION_SAVE))
    {
      final String sSalutationID = aWPEC.params ().getAsString (FIELD_SALUTATION);
      final ESalutation eSalutation = ESalutation.getFromIDOrNull (sSalutationID);
      final String sName = aWPEC.params ().getAsString (FIELD_NAME);
      final String sEmailAddress = aWPEC.params ().getAsString (FIELD_EMAIL_ADDRESS);
      aSelectedCRMGroupIDs = aWPEC.params ().getAsStringList (FIELD_GROUP);
      final ICommonsSet <ICRMGroup> aSelectedCRMGroups = new CommonsHashSet <> ();
      ICRMSubscriber aSameEmailAddressSubscriber = null;

      if (StringHelper.hasNoText (sName))
        aFormErrors.addFieldError (FIELD_NAME, "You must provide your name!");

      if (StringHelper.hasNoText (sEmailAddress))
        aFormErrors.addFieldError (FIELD_EMAIL_ADDRESS, "You must provide your email address!");
      else
        if (!EmailAddressHelper.isValid (sEmailAddress))
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
        aNodeList.addChild (success ("Successfully subscribed '" + sEmailAddress + "' to mailing lists"));
      }
      else
      {
        aNodeList.addChild (getUIHandler ().createIncorrectInputBox (aWPEC));
      }
    }

    final BootstrapForm aForm = getUIHandler ().createFormSelf (aWPEC);
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
      final ICommonsList <? extends ICRMGroup> aAllCRMGroups = aCRMGroupMgr.getAll ();
      if (aAllCRMGroups.size () == 1)
      {
        // No need for selection - use hidden field
        aForm.addChild (new HCHiddenField (FIELD_GROUP, aAllCRMGroups.getFirstOrNull ().getID ()));
      }
      else
        if (aAllCRMGroups.isNotEmpty ())
        {
          // Show selection
          final HCNodeList aGroups = new HCNodeList ();
          for (final ICRMGroup aCRMGroup : aAllCRMGroups.getSorted (IHasDisplayName.getComparatorCollating (aDisplayLocale)))
          {
            final String sCRMGroupID = aCRMGroup.getID ();
            final RequestFieldBooleanMultiValue aRFB = new RequestFieldBooleanMultiValue (FIELD_GROUP,
                                                                                          sCRMGroupID,
                                                                                          false);
            aGroups.addChild (new HCDiv ().addChild (new HCCheckBox (aRFB))
                                          .addChild (" " + aCRMGroup.getDisplayName ()));
          }
          aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Mailing lists to subscribe to")
                                                       .setCtrl (aGroups)
                                                       .setErrorList (aFormErrors.getListOfField (FIELD_GROUP)));
        }
    }

    // Toolbar
    final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
    aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_SAVE);
    aToolbar.addSubmitButton ("Subscribe", EDefaultIcon.YES);

    aNodeList.addChild (aForm);
  }
}
