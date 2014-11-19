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
package com.helger.peppol.page.secure;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.bootstrap3.alert.BootstrapWarnBox;
import com.helger.bootstrap3.button.BootstrapButton;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.form.BootstrapCheckBox;
import com.helger.bootstrap3.table.BootstrapTable;
import com.helger.bootstrap3.table.BootstrapTableForm;
import com.helger.bootstrap3.table.BootstrapTableFormView;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.email.EmailAddressUtils;
import com.helger.commons.name.ComparatorHasDisplayName;
import com.helger.commons.state.EValidity;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.html.hc.html.AbstractHCForm;
import com.helger.html.hc.html.HCA;
import com.helger.html.hc.html.HCCol;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCRow;
import com.helger.html.hc.htmlext.HCUtils;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.masterdata.person.ESalutation;
import com.helger.peppol.app.menu.CMenuSecure;
import com.helger.peppol.crm.CRMGroupManager;
import com.helger.peppol.crm.CRMSubscriberManager;
import com.helger.peppol.crm.ICRMGroup;
import com.helger.peppol.crm.ICRMSubscriber;
import com.helger.peppol.mgr.MetaManager;
import com.helger.peppol.page.AbstractAppFormPage;
import com.helger.validation.error.FormErrors;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webbasics.form.RequestFieldBooleanMultiValue;
import com.helger.webctrls.custom.EDefaultIcon;
import com.helger.webctrls.datatables.DataTables;
import com.helger.webctrls.masterdata.HCSalutationSelect;

public final class PageSecureCRMSubscriber extends AbstractAppFormPage <ICRMSubscriber>
{
  private static final String FIELD_SALUTATION = "salutation";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_EMAIL_ADDRESS = "emailaddress";
  private static final String FIELD_GROUP = "group";

  public PageSecureCRMSubscriber (@Nonnull @Nonempty final String sID)
  {
    super (sID, "CRM subscribers");
  }

  @Override
  @Nonnull
  protected EValidity isValidToDisplayPage (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CRMGroupManager aCRMGroupMgr = MetaManager.getCRMGroupMgr ();

    if (aCRMGroupMgr.isEmpty ())
    {
      aNodeList.addChild (new BootstrapWarnBox ().addChild ("No CRM groups is present! At least one CRM group must be present to assign a subscriber to"));
      aNodeList.addChild (new BootstrapButton ().addChild ("Create new CRM group")
                                                .setOnClick (createCreateURL (aWPEC, CMenuSecure.MENU_CRM_GROUPS))
                                                .setIcon (EDefaultIcon.YES));
      return EValidity.INVALID;
    }

    return EValidity.VALID;
  }

  @Override
  @Nullable
  protected ICRMSubscriber getSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, @Nullable final String sID)
  {
    final CRMSubscriberManager aCRMSubscriberMgr = MetaManager.getCRMSubscriberMgr ();
    return aCRMSubscriberMgr.getCRMSubscriberOfID (sID);
  }

  @Override
  protected void showSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, final ICRMSubscriber aSelectedObject)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final BootstrapTableFormView aTable = aNodeList.addAndReturnChild (new BootstrapTableFormView (new HCCol (170),
                                                                                                   HCCol.star ()));
    aTable.createItemRow ().setLabel ("Salutation").setCtrl (aSelectedObject.getSalutationDisplayName (aDisplayLocale));
    aTable.createItemRow ().setLabel ("Name").setCtrl (aSelectedObject.getName ());
    aTable.createItemRow ().setLabel ("Email address").setCtrl (aSelectedObject.getEmailAddress ());
    {
      final HCNodeList aGroups = new HCNodeList ();
      for (final ICRMGroup aCRMGroup : ContainerHelper.getSorted (aSelectedObject.getAllAssignedGroups (),
                                                                  new ComparatorHasDisplayName <ICRMGroup> (aDisplayLocale)))
        aGroups.addChild (new HCDiv ().addChild (new HCA (createViewURL (aWPEC, CMenuSecure.MENU_CRM_GROUPS, aCRMGroup)).addChild (aCRMGroup.getDisplayName ())));
      aTable.createItemRow ().setLabel ("Assigned groups").setCtrl (aGroups);
    }
  }

  @Override
  protected void validateAndSaveInputParameters (@Nonnull final WebPageExecutionContext aWPEC,
                                                 @Nullable final ICRMSubscriber aSelectedObject,
                                                 @Nonnull final FormErrors aFormErrors,
                                                 final boolean bEdit)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CRMGroupManager aCRMGroupMgr = MetaManager.getCRMGroupMgr ();
    final CRMSubscriberManager aCRMSubscriberMgr = MetaManager.getCRMSubscriberMgr ();

    final String sSalutationID = aWPEC.getAttributeAsString (FIELD_SALUTATION);
    final ESalutation eSalutation = ESalutation.getFromIDOrNull (sSalutationID);
    final String sName = aWPEC.getAttributeAsString (FIELD_NAME);
    final String sEmailAddress = aWPEC.getAttributeAsString (FIELD_EMAIL_ADDRESS);
    final List <String> aSelectedCRMGroupIDs = aWPEC.getAttributeValues (FIELD_GROUP);
    final Set <ICRMGroup> aSelectedCRMGroups = new HashSet <ICRMGroup> ();

    if (StringHelper.hasNoText (sName))
      aFormErrors.addFieldError (FIELD_NAME, "A name for the CRM subscriber must be provided!");

    if (StringHelper.hasNoText (sEmailAddress))
      aFormErrors.addFieldError (FIELD_EMAIL_ADDRESS, "An email address must be provided!");
    else
      if (!EmailAddressUtils.isValid (sEmailAddress))
        aFormErrors.addFieldError (FIELD_EMAIL_ADDRESS, "The provided email address is invalid!");
      else
      {
        final ICRMSubscriber aSameEmailAddresSubscriber = aCRMSubscriberMgr.getCRMSubscriberOfEmailAddress (sEmailAddress);
        if (aSameEmailAddresSubscriber != null)
        {
          if (!bEdit || aSameEmailAddresSubscriber != aSelectedObject)
            aFormErrors.addFieldError (FIELD_EMAIL_ADDRESS,
                                       "A subscription for the provided email address is already present!");
        }
      }

    if (aSelectedCRMGroupIDs != null)
      for (final String sCRMGroupID : aSelectedCRMGroupIDs)
      {
        final ICRMGroup aGroup = aCRMGroupMgr.getCRMGroupOfID (sCRMGroupID);
        if (aGroup == null)
          aFormErrors.addFieldError (FIELD_GROUP, "The selected group is not existing!");
        else
          aSelectedCRMGroups.add (aGroup);
      }
    if (aSelectedCRMGroups.isEmpty ())
      aFormErrors.addFieldError (FIELD_GROUP, "At least one group must be selected!");

    if (aFormErrors.isEmpty ())
    {
      // All fields are valid -> save
      if (bEdit)
      {
        // We're editing an existing object
        aCRMSubscriberMgr.updateCRMSubscriber (aSelectedObject.getID (),
                                               eSalutation,
                                               sName,
                                               sEmailAddress,
                                               aSelectedCRMGroups);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("The CRM subscriber was successfully edited!"));
      }
      else
      {
        // We're creating a new object
        aCRMSubscriberMgr.createCRMSubscriber (eSalutation, sName, sEmailAddress, aSelectedCRMGroups);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("The new CRM subscriber was successfully created!"));
      }
    }
  }

  @Override
  protected void showInputForm (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nullable final ICRMSubscriber aSelectedObject,
                                @Nonnull final AbstractHCForm <?> aForm,
                                final boolean bEdit,
                                final boolean bCopy,
                                @Nonnull final FormErrors aFormErrors)
  {
    final CRMGroupManager aCRMGroupMgr = MetaManager.getCRMGroupMgr ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    final BootstrapTableForm aTable = aForm.addAndReturnChild (new BootstrapTableForm (new HCCol (170), HCCol.star ()));
    aTable.setSpanningHeaderContent (bEdit ? "Edit CRM subscriber" : "Create new CRM subscriber");

    aTable.createItemRow ()
          .setLabel ("Salutation")
          .setCtrl (new HCSalutationSelect (new RequestField (FIELD_SALUTATION,
                                                              aSelectedObject == null
                                                                                     ? null
                                                                                     : aSelectedObject.getSalutationID ()),
                                            aDisplayLocale))
          .setErrorList (aFormErrors.getListOfField (FIELD_SALUTATION));

    aTable.createItemRow ()
          .setLabelMandatory ("Name")
          .setCtrl (new HCEdit (new RequestField (FIELD_NAME, aSelectedObject == null ? null
                                                                                     : aSelectedObject.getName ())))
          .setErrorList (aFormErrors.getListOfField (FIELD_NAME));

    aTable.createItemRow ()
          .setLabelMandatory ("Email address")
          .setCtrl (new HCEdit (new RequestField (FIELD_EMAIL_ADDRESS,
                                                  aSelectedObject == null ? null : aSelectedObject.getEmailAddress ())))
          .setErrorList (aFormErrors.getListOfField (FIELD_EMAIL_ADDRESS));

    {
      final HCNodeList aGroups = new HCNodeList ();
      for (final ICRMGroup aCRMGroup : ContainerHelper.getSorted (aCRMGroupMgr.getAllCRMGroups (),
                                                                  new ComparatorHasDisplayName <ICRMGroup> (aDisplayLocale)))
      {
        final String sCRMGroupID = aCRMGroup.getID ();
        final RequestFieldBooleanMultiValue aRFB = new RequestFieldBooleanMultiValue (FIELD_GROUP,
                                                                                      sCRMGroupID,
                                                                                      aSelectedObject != null &&
                                                                                          aSelectedObject.isAssignedToGroup (aCRMGroup));
        aGroups.addChild (new HCDiv ().addChild (new BootstrapCheckBox (aRFB).setInline (true).setValue (sCRMGroupID))
                                      .addChild (" " + aCRMGroup.getDisplayName ()));
      }
      aTable.createItemRow ()
            .setLabelMandatory ("Assigned groups")
            .setCtrl (aGroups)
            .setErrorList (aFormErrors.getListOfField (FIELD_GROUP));
    }
  }

  @Override
  protected void showListOfExistingObjects (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CRMSubscriberManager aCRMSubscriberMgr = MetaManager.getCRMSubscriberMgr ();

    // Toolbar on top
    final BootstrapButtonToolbar aToolbar = aNodeList.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
    aToolbar.addButtonNew ("Create new CRM subscriber", createCreateURL (aWPEC));

    // List existing
    final BootstrapTable aTable = new BootstrapTable (HCCol.star (), HCCol.star (), HCCol.star (), createActionCol (2)).setID (getID ());
    aTable.addHeaderRow ().addCells ("Name", "Email address", "Groups", "Actions");

    for (final ICRMSubscriber aCurObject : aCRMSubscriberMgr.getAllCRMSubscribers ())
    {
      final ISimpleURL aViewLink = createViewURL (aWPEC, aCurObject);

      final HCRow aRow = aTable.addBodyRow ();
      aRow.addCell (new HCA (aViewLink).addChild (StringHelper.getConcatenatedOnDemand (aCurObject.getSalutationDisplayName (aDisplayLocale),
                                                                                        " ",
                                                                                        aCurObject.getName ())));
      aRow.addCell (aCurObject.getEmailAddress ());
      aRow.addCell (HCUtils.nl2divList (aCurObject.getAllAssignedGroups ()
                                                  .stream ()
                                                  .map ( (g) -> g.getDisplayName ())
                                                  .collect (Collectors.joining ("\n"))));
      aRow.addCell (createEditLink (aWPEC, aCurObject), new HCTextNode (" "), createCopyLink (aWPEC, aCurObject));
    }
    aNodeList.addChild (aTable);

    final DataTables aDataTables = getStyler ().createDefaultDataTables (aWPEC, aTable);
    aDataTables.getOrCreateColumnOfTarget (2).addClass (CSS_CLASS_ACTION_COL).setSortable (false);
    aDataTables.setInitialSorting (0, ESortOrder.ASCENDING);
    aNodeList.addChild (aDataTables);
  }
}
