/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.peppol.secure.page;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.email.EmailAddressHelper;
import com.helger.commons.errorlist.FormErrors;
import com.helger.commons.name.IHasDisplayName;
import com.helger.commons.state.EValidity;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.ext.HCExtHelper;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCTextArea;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.tabular.IHCCell;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.masterdata.person.ESalutation;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.crm.CRMGroupManager;
import com.helger.peppol.crm.CRMSubscriberManager;
import com.helger.peppol.crm.ICRMGroup;
import com.helger.peppol.crm.ICRMSubscriber;
import com.helger.peppol.secure.CMenuSecure;
import com.helger.peppol.ui.page.AbstractAppWebPageForm;
import com.helger.photon.bootstrap3.alert.BootstrapQuestionBox;
import com.helger.photon.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap3.alert.BootstrapWarnBox;
import com.helger.photon.bootstrap3.button.BootstrapButton;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.form.BootstrapCheckBox;
import com.helger.photon.bootstrap3.form.BootstrapForm;
import com.helger.photon.bootstrap3.form.BootstrapFormGroup;
import com.helger.photon.bootstrap3.form.BootstrapViewForm;
import com.helger.photon.bootstrap3.nav.BootstrapTabBox;
import com.helger.photon.bootstrap3.uictrls.datatables.BootstrapDTColAction;
import com.helger.photon.bootstrap3.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.form.RequestFieldBooleanMultiValue;
import com.helger.photon.uicore.html.select.HCSalutationSelect;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.page.EWebPageFormAction;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.DataTables;
import com.helger.photon.uictrls.datatables.column.DTCol;

public final class PageSecureCRMSubscriber extends AbstractAppWebPageForm <ICRMSubscriber>
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
    final CRMGroupManager aCRMGroupMgr = PPMetaManager.getCRMGroupMgr ();

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
  protected boolean isActionAllowed (@Nonnull final WebPageExecutionContext aWPEC,
                                     @Nonnull final EWebPageFormAction eFormAction,
                                     @Nullable final ICRMSubscriber aSelectedObject)
  {
    if (eFormAction == EWebPageFormAction.DELETE)
      return !aSelectedObject.isDeleted ();

    return super.isActionAllowed (aWPEC, eFormAction, aSelectedObject);
  }

  @Override
  @Nullable
  protected ICRMSubscriber getSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, @Nullable final String sID)
  {
    final CRMSubscriberManager aCRMSubscriberMgr = PPMetaManager.getCRMSubscriberMgr ();
    return aCRMSubscriberMgr.getCRMSubscriberOfID (sID);
  }

  @Override
  protected void showSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, final ICRMSubscriber aSelectedObject)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    final BootstrapViewForm aForm = aNodeList.addAndReturnChild (new BootstrapViewForm ());
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Status")
                                                 .setCtrl (aSelectedObject.isDeleted () ? "Deleted" : "Active"));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Salutation")
                                                 .setCtrl (aSelectedObject.getSalutationDisplayName (aDisplayLocale)));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Name").setCtrl (aSelectedObject.getName ()));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Email address")
                                                 .setCtrl (aSelectedObject.getEmailAddress ()));
    {
      final HCNodeList aGroups = new HCNodeList ();
      for (final ICRMGroup aCRMGroup : CollectionHelper.getSorted (aSelectedObject.getAllAssignedGroups (),
                                                                   IHasDisplayName.getComparatorCollating (aDisplayLocale)))
        aGroups.addChild (new HCDiv ().addChild (new HCA (createViewURL (aWPEC,
                                                                         CMenuSecure.MENU_CRM_GROUPS,
                                                                         aCRMGroup)).addChild (aCRMGroup.getDisplayName ())));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Assigned groups").setCtrl (aGroups));
    }
  }

  @Override
  protected void validateAndSaveInputParameters (@Nonnull final WebPageExecutionContext aWPEC,
                                                 @Nullable final ICRMSubscriber aSelectedObject,
                                                 @Nonnull final FormErrors aFormErrors,
                                                 @Nonnull final EWebPageFormAction eFormAction)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CRMGroupManager aCRMGroupMgr = PPMetaManager.getCRMGroupMgr ();
    final CRMSubscriberManager aCRMSubscriberMgr = PPMetaManager.getCRMSubscriberMgr ();

    final String sSalutationID = aWPEC.getAttributeAsString (FIELD_SALUTATION);
    final ESalutation eSalutation = ESalutation.getFromIDOrNull (sSalutationID);
    final String sName = aWPEC.getAttributeAsString (FIELD_NAME);
    final String sEmailAddress = aWPEC.getAttributeAsString (FIELD_EMAIL_ADDRESS);
    final List <String> aSelectedCRMGroupIDs = aWPEC.getAttributeAsList (FIELD_GROUP);
    final Set <ICRMGroup> aSelectedCRMGroups = new HashSet <ICRMGroup> ();

    if (StringHelper.hasNoText (sName))
      aFormErrors.addFieldError (FIELD_NAME, "A name for the CRM subscriber must be provided!");

    if (StringHelper.hasNoText (sEmailAddress))
      aFormErrors.addFieldError (FIELD_EMAIL_ADDRESS, "An email address must be provided!");
    else
      if (!EmailAddressHelper.isValid (sEmailAddress))
        aFormErrors.addFieldError (FIELD_EMAIL_ADDRESS, "The provided email address is invalid!");
      else
      {
        final ICRMSubscriber aSameEmailAddresSubscriber = aCRMSubscriberMgr.getCRMSubscriberOfEmailAddress (sEmailAddress);
        if (aSameEmailAddresSubscriber != null)
        {
          if (!eFormAction.isEdit () || aSameEmailAddresSubscriber != aSelectedObject)
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
      if (eFormAction.isEdit ())
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
                                @Nonnull final BootstrapForm aForm,
                                @Nonnull final EWebPageFormAction eFormAction,
                                @Nonnull final FormErrors aFormErrors)
  {
    final CRMGroupManager aCRMGroupMgr = PPMetaManager.getCRMGroupMgr ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    aForm.addChild (createActionHeader (eFormAction.isEdit () ? "Edit CRM subscriber" : "Create new CRM subscriber"));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Salutation")
                                                 .setCtrl (new HCSalutationSelect (new RequestField (FIELD_SALUTATION,
                                                                                                     aSelectedObject == null ? null
                                                                                                                             : aSelectedObject.getSalutationID ()),
                                                                                   aDisplayLocale))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_SALUTATION)));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Name")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_NAME,
                                                                                         aSelectedObject == null ? null
                                                                                                                 : aSelectedObject.getName ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_NAME)));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Email address")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_EMAIL_ADDRESS,
                                                                                         aSelectedObject == null ? null
                                                                                                                 : aSelectedObject.getEmailAddress ())))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_EMAIL_ADDRESS)));

    {
      final HCNodeList aGroups = new HCNodeList ();
      for (final ICRMGroup aCRMGroup : CollectionHelper.getSorted (aCRMGroupMgr.getAllCRMGroups (),
                                                                   IHasDisplayName.getComparatorCollating (aDisplayLocale)))
      {
        final String sCRMGroupID = aCRMGroup.getID ();
        final RequestFieldBooleanMultiValue aRFB = new RequestFieldBooleanMultiValue (FIELD_GROUP,
                                                                                      sCRMGroupID,
                                                                                      aSelectedObject != null &&
                                                                                                   aSelectedObject.isAssignedToGroup (aCRMGroup));
        aGroups.addChild (new HCDiv ().addChild (new BootstrapCheckBox (aRFB).setInline (true).setValue (sCRMGroupID))
                                      .addChild (" " + aCRMGroup.getDisplayName ()));
      }
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Assigned groups")
                                                   .setCtrl (aGroups)
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_GROUP)));
    }
  }

  @Override
  protected void showDeleteQuery (@Nonnull final WebPageExecutionContext aWPEC,
                                  @Nonnull final BootstrapForm aForm,
                                  @Nonnull final ICRMSubscriber aSelectedObject)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    aForm.addChild (new BootstrapQuestionBox ().addChild ("Should the CRM subscriber '" +
                                                          aSelectedObject.getDisplayText (aDisplayLocale) +
                                                          "' really be deleted?"));
  }

  @Override
  protected void performDelete (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nonnull final ICRMSubscriber aSelectedObject)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final CRMSubscriberManager aCRMSubscriberMgr = PPMetaManager.getCRMSubscriberMgr ();

    if (aCRMSubscriberMgr.deleteCRMSubscriber (aSelectedObject).isChanged ())
      aNodeList.addChild (new BootstrapSuccessBox ().addChild ("The CRM subscriber '" +
                                                               aSelectedObject.getDisplayText (aDisplayLocale) +
                                                               "' was successfully deleted."));
  }

  @Nonnull
  private IHCNode _getList (@Nonnull final WebPageExecutionContext aWPEC,
                            @Nonnull final Collection <? extends ICRMSubscriber> aCRMSubscribers,
                            @Nonnull final String sIDSuffix)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    // List existing
    final HCTable aTable = new HCTable (new DTCol ("Name").setInitialSorting (ESortOrder.ASCENDING),
                                        new DTCol ("Email address"),
                                        new DTCol ("Groups"),
                                        new BootstrapDTColAction (aDisplayLocale)).setID (getID () + sIDSuffix);

    for (final ICRMSubscriber aCurObject : aCRMSubscribers)
    {
      final ISimpleURL aViewLink = createViewURL (aWPEC, aCurObject);

      final HCRow aRow = aTable.addBodyRow ();
      aRow.addCell (new HCA (aViewLink).addChild (StringHelper.getConcatenatedOnDemand (aCurObject.getSalutationDisplayName (aDisplayLocale),
                                                                                        " ",
                                                                                        aCurObject.getName ())));
      aRow.addCell (aCurObject.getEmailAddress ());
      aRow.addCell (HCExtHelper.nl2divList (aCurObject.getAllAssignedGroups ()
                                                      .stream ()
                                                      .map ( (g) -> g.getDisplayName ())
                                                      .collect (Collectors.joining ("\n"))));
      final IHCCell <?> aActionCell = aRow.addCell ();
      aActionCell.addChildren (createEditLink (aWPEC, aCurObject),
                               new HCTextNode (" "),
                               createCopyLink (aWPEC, aCurObject),
                               new HCTextNode (" "));
      aActionCell.addChild (isActionAllowed (aWPEC, EWebPageFormAction.DELETE, aCurObject)
                                                                                           ? createDeleteLink (aWPEC,
                                                                                                               aCurObject)
                                                                                           : createEmptyAction ());
    }

    final DataTables aDataTables = BootstrapDataTables.createDefaultDataTables (aWPEC, aTable);

    return new HCNodeList ().addChild (aTable).addChild (aDataTables);
  }

  @Nonnull
  private static IHCNode _getListForMailing ()
  {
    final StringBuilder aSB = new StringBuilder ();
    final CRMSubscriberManager aCRMSubscriberMgr = PPMetaManager.getCRMSubscriberMgr ();
    int nCount = 0;
    for (final ICRMSubscriber aSubscriber : CollectionHelper.getSorted (aCRMSubscriberMgr.getAllActiveCRMSubscribers (),
                                                                        Comparator.comparing (ICRMSubscriber::getEmailAddress)))
    {
      if (aSB.length () > 0)
        aSB.append ("\n");
      aSB.append (aSubscriber.getEmailAddress ());
      ++nCount;
    }

    return new HCTextArea ().setValue (aSB.toString ()).setRows (Math.min (nCount, 20));
  }

  @Override
  protected void showListOfExistingObjects (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final CRMSubscriberManager aCRMSubscriberMgr = PPMetaManager.getCRMSubscriberMgr ();

    // Toolbar on top
    final BootstrapButtonToolbar aToolbar = aNodeList.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
    aToolbar.addButtonNew ("Create new CRM subscriber", createCreateURL (aWPEC));

    final BootstrapTabBox aTabBox = new BootstrapTabBox ();
    aTabBox.addTab ("active", "Active", _getList (aWPEC, aCRMSubscriberMgr.getAllActiveCRMSubscribers (), "active"));
    aTabBox.addTab ("deleted", "Deleted", _getList (aWPEC, aCRMSubscriberMgr.getAllDeletedCRMSubscribers (), "del"));
    aTabBox.addTab ("mailing", "Mailing", _getListForMailing ());
    aNodeList.addChild (aTabBox);
  }
}
