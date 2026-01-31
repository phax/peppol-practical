/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.peppol.secure;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.compare.ESortOrder;
import com.helger.base.email.EmailAddressHelper;
import com.helger.base.string.StringHelper;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.crm.CRMGroupManager;
import com.helger.peppol.crm.CRMSubscriberManager;
import com.helger.peppol.crm.ICRMGroup;
import com.helger.peppol.sharedui.page.AbstractAppWebPageForm;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.form.BootstrapViewForm;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDTColAction;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.page.EWebPageFormAction;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.DataTables;
import com.helger.photon.uictrls.datatables.column.DTCol;
import com.helger.url.ISimpleURL;


public final class PageSecureCRMGroup extends AbstractAppWebPageForm <ICRMGroup>
{
  private static final String FIELD_NAME = "name";
  private static final String FIELD_SENDER_EMAIL_ADDRESS = "senderemailaddress";

  public PageSecureCRMGroup (@NonNull @Nonempty final String sID)
  {
    super (sID, "CRM groups");
  }

  @Override
  @Nullable
  protected ICRMGroup getSelectedObject (@NonNull final WebPageExecutionContext aWPEC, @Nullable final String sID)
  {
    final CRMGroupManager aCRMGroupMgr = PPMetaManager.getCRMGroupMgr ();
    return aCRMGroupMgr.getCRMGroupOfID (sID);
  }

  @Override
  protected void showSelectedObject (@NonNull final WebPageExecutionContext aWPEC, final ICRMGroup aSelectedObject)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CRMSubscriberManager aCRMSubscriberMgr = PPMetaManager.getCRMSubscriberMgr ();

    final BootstrapViewForm aForm = aNodeList.addAndReturnChild (new BootstrapViewForm ());
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Name").setCtrl (aSelectedObject.getDisplayName ()));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Sender email address")
                                                 .setCtrl (aSelectedObject.getSenderEmailAddress ()));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Assigned participants")
                                                 .setCtrl (Long.toString (aCRMSubscriberMgr.getCRMSubscriberCountOfGroup (aSelectedObject))));
  }

  @Override
  protected void validateAndSaveInputParameters (@NonNull final WebPageExecutionContext aWPEC,
                                                 @Nullable final ICRMGroup aSelectedObject,
                                                 @NonNull final FormErrorList aFormErrors,
                                                 @NonNull final EWebPageFormAction eFormAction)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CRMGroupManager aCRMGroupMgr = PPMetaManager.getCRMGroupMgr ();

    final String sName = aWPEC.params ().getAsString (FIELD_NAME);
    final String sSenderEmailAddress = aWPEC.params ().getAsString (FIELD_SENDER_EMAIL_ADDRESS);

    if (StringHelper.isEmpty (sName))
      aFormErrors.addFieldError (FIELD_NAME, "A name for the CRM group must be provided!");

    if (StringHelper.isEmpty (sSenderEmailAddress))
      aFormErrors.addFieldError (FIELD_SENDER_EMAIL_ADDRESS, "A sender email address must be provided!");
    else
      if (!EmailAddressHelper.isValid (sSenderEmailAddress))
        aFormErrors.addFieldError (FIELD_SENDER_EMAIL_ADDRESS, "The provided sender email address is invalid!");

    if (aFormErrors.isEmpty ())
    {
      // All fields are valid -> save
      if (eFormAction.isEdit ())
      {
        // We're editing an existing object
        aCRMGroupMgr.updateCRMGroup (aSelectedObject.getID (), sName, sSenderEmailAddress);
        aNodeList.addChild (success ("The CRM group was successfully edited!"));
      }
      else
      {
        // We're creating a new object
        aCRMGroupMgr.createCRMGroup (sName, sSenderEmailAddress);
        aNodeList.addChild (success ("The new CRM group was successfully created!"));
      }
    }
  }

  @Override
  protected void showInputForm (@NonNull final WebPageExecutionContext aWPEC,
                                @Nullable final ICRMGroup aSelectedObject,
                                @NonNull final BootstrapForm aForm,
                                final boolean bFormSubmitted,
                                @NonNull final EWebPageFormAction eFormAction,
                                @NonNull final FormErrorList aFormErrors)
  {
    aForm.addChild (getUIHandler ().createActionHeader (eFormAction.isEdit () ? "Edit CRM group"
                                                                              : "Create new CRM group"));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Name")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_NAME,
                                                                                         aSelectedObject == null ? null
                                                                                                                 : aSelectedObject.getDisplayName ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_NAME)));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Sender email address")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_SENDER_EMAIL_ADDRESS,
                                                                                         aSelectedObject == null ? null
                                                                                                                 : aSelectedObject.getSenderEmailAddress ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_SENDER_EMAIL_ADDRESS)));
  }

  @Override
  protected void showListOfExistingObjects (@NonNull final WebPageExecutionContext aWPEC)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CRMGroupManager aCRMGroupMgr = PPMetaManager.getCRMGroupMgr ();

    // Toolbar on top
    final BootstrapButtonToolbar aToolbar = aNodeList.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
    aToolbar.addButtonNew ("Create new CRM group", createCreateURL (aWPEC));

    // List existing
    final HCTable aTable = new HCTable (new DTCol ("Name").setInitialSorting (ESortOrder.ASCENDING),
                                        new DTCol ("Sender email address"),
                                        new BootstrapDTColAction (aDisplayLocale)).setID (getID ());

    for (final ICRMGroup aCurObject : aCRMGroupMgr.getAll ())
    {
      final ISimpleURL aViewLink = createViewURL (aWPEC, aCurObject);

      final HCRow aRow = aTable.addBodyRow ();
      aRow.addCell (new HCA (aViewLink).addChild (aCurObject.getDisplayName ()));
      aRow.addCell (aCurObject.getSenderEmailAddress ());
      aRow.addCell (createEditLink (aWPEC, aCurObject), new HCTextNode (" "), createCopyLink (aWPEC, aCurObject));
    }
    aNodeList.addChild (aTable);

    final DataTables aDataTables = BootstrapDataTables.createDefaultDataTables (aWPEC, aTable);
    aNodeList.addChild (aDataTables);
  }
}
