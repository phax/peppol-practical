/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.peppol.page.secure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.email.EmailAddressUtils;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.html.hc.html.AbstractHCForm;
import com.helger.html.hc.html.HCA;
import com.helger.html.hc.html.HCCol;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCRow;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.peppol.crm.CRMGroupManager;
import com.helger.peppol.crm.CRMSubscriberManager;
import com.helger.peppol.crm.ICRMGroup;
import com.helger.peppol.mgr.MetaManager;
import com.helger.peppol.page.AbstractAppFormPage;
import com.helger.photon.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.table.BootstrapTable;
import com.helger.photon.bootstrap3.table.BootstrapTableForm;
import com.helger.photon.bootstrap3.table.BootstrapTableFormView;
import com.helger.photon.bootstrap3.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.page.EWebPageFormAction;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.DataTables;
import com.helger.validation.error.FormErrors;

public final class PageSecureCRMGroup extends AbstractAppFormPage <ICRMGroup>
{
  private static final String FIELD_NAME = "name";
  private static final String FIELD_SENDER_EMAIL_ADDRESS = "senderemailaddress";

  public PageSecureCRMGroup (@Nonnull @Nonempty final String sID)
  {
    super (sID, "CRM groups");
  }

  @Override
  @Nullable
  protected ICRMGroup getSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, @Nullable final String sID)
  {
    final CRMGroupManager aCRMGroupMgr = MetaManager.getCRMGroupMgr ();
    return aCRMGroupMgr.getCRMGroupOfID (sID);
  }

  @Override
  protected void showSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, final ICRMGroup aSelectedObject)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CRMSubscriberManager aCRMSubscriberMgr = MetaManager.getCRMSubscriberMgr ();

    final BootstrapTableFormView aTable = aNodeList.addAndReturnChild (new BootstrapTableFormView (new HCCol (170),
                                                                                                   HCCol.star ()));
    aTable.createItemRow ().setLabel ("Name").setCtrl (aSelectedObject.getDisplayName ());
    aTable.createItemRow ().setLabel ("Sender email address").setCtrl (aSelectedObject.getSenderEmailAddress ());
    aTable.createItemRow ()
          .setLabel ("Assigned participants")
          .setCtrl (Integer.toString (aCRMSubscriberMgr.getCRMSubscriberCountOfGroup (aSelectedObject)));
  }

  @Override
  protected void validateAndSaveInputParameters (@Nonnull final WebPageExecutionContext aWPEC,
                                                 @Nullable final ICRMGroup aSelectedObject,
                                                 @Nonnull final FormErrors aFormErrors,
                                                 @Nonnull final EWebPageFormAction eFormAction)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CRMGroupManager aCRMGroupMgr = MetaManager.getCRMGroupMgr ();

    final String sName = aWPEC.getAttributeAsString (FIELD_NAME);
    final String sSenderEmailAddress = aWPEC.getAttributeAsString (FIELD_SENDER_EMAIL_ADDRESS);

    if (StringHelper.hasNoText (sName))
      aFormErrors.addFieldError (FIELD_NAME, "A name for the CRM group must be provided!");

    if (StringHelper.hasNoText (sSenderEmailAddress))
      aFormErrors.addFieldError (FIELD_SENDER_EMAIL_ADDRESS, "A sender email address must be provided!");
    else
      if (!EmailAddressUtils.isValid (sSenderEmailAddress))
        aFormErrors.addFieldError (FIELD_SENDER_EMAIL_ADDRESS, "The provided sender email address is invalid!");

    if (aFormErrors.isEmpty ())
    {
      // All fields are valid -> save
      if (eFormAction.isEdit ())
      {
        // We're editing an existing object
        aCRMGroupMgr.updateCRMGroup (aSelectedObject.getID (), sName, sSenderEmailAddress);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("The CRM group was successfully edited!"));
      }
      else
      {
        // We're creating a new object
        aCRMGroupMgr.createCRMGroup (sName, sSenderEmailAddress);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("The new CRM group was successfully created!"));
      }
    }
  }

  @Override
  protected void showInputForm (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nullable final ICRMGroup aSelectedObject,
                                @Nonnull final AbstractHCForm <?> aForm,
                                @Nonnull final EWebPageFormAction eFormAction,
                                @Nonnull final FormErrors aFormErrors)
  {
    final BootstrapTableForm aTable = aForm.addAndReturnChild (new BootstrapTableForm (new HCCol (170), HCCol.star ()));
    aTable.setSpanningHeaderContent (eFormAction.isEdit () ? "Edit CRM group" : "Create new CRM group");
    aTable.createItemRow ()
          .setLabelMandatory ("Name")
          .setCtrl (new HCEdit (new RequestField (FIELD_NAME,
                                                  aSelectedObject == null ? null : aSelectedObject.getDisplayName ())))
          .setErrorList (aFormErrors.getListOfField (FIELD_NAME));
    aTable.createItemRow ()
          .setLabelMandatory ("Sender email address")
          .setCtrl (new HCEdit (new RequestField (FIELD_SENDER_EMAIL_ADDRESS,
                                                  aSelectedObject == null ? null
                                                                         : aSelectedObject.getSenderEmailAddress ())))
          .setErrorList (aFormErrors.getListOfField (FIELD_SENDER_EMAIL_ADDRESS));
  }

  @Override
  protected void showListOfExistingObjects (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CRMGroupManager aCRMGroupMgr = MetaManager.getCRMGroupMgr ();

    // Toolbar on top
    final BootstrapButtonToolbar aToolbar = aNodeList.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
    aToolbar.addButtonNew ("Create new CRM group", createCreateURL (aWPEC));

    // List existing
    final BootstrapTable aTable = new BootstrapTable (HCCol.star (), HCCol.star (), createActionCol (2)).setID (getID ());
    aTable.addHeaderRow ().addCells ("Name", "Sender email address", "Actions");

    for (final ICRMGroup aCurObject : aCRMGroupMgr.getAllCRMGroups ())
    {
      final ISimpleURL aViewLink = createViewURL (aWPEC, aCurObject);

      final HCRow aRow = aTable.addBodyRow ();
      aRow.addCell (new HCA (aViewLink).addChild (aCurObject.getDisplayName ()));
      aRow.addCell (aCurObject.getSenderEmailAddress ());
      aRow.addCell (createEditLink (aWPEC, aCurObject), new HCTextNode (" "), createCopyLink (aWPEC, aCurObject));
    }
    aNodeList.addChild (aTable);

    final DataTables aDataTables = BootstrapDataTables.createDefaultDataTables (aWPEC, aTable);
    aDataTables.getOrCreateColumnOfTarget (2).addClass (CSS_CLASS_ACTION_COL).setSortable (false);
    aDataTables.setInitialSorting (0, ESortOrder.ASCENDING);
    aNodeList.addChild (aDataTables);
  }
}
