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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.appbasics.security.login.LoggedInUserManager;
import com.helger.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.bootstrap3.button.BootstrapButton;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.form.BootstrapForm;
import com.helger.bootstrap3.form.BootstrapFormGroup;
import com.helger.bootstrap3.label.BootstrapLabel;
import com.helger.bootstrap3.label.EBootstrapLabelType;
import com.helger.bootstrap3.table.BootstrapTable;
import com.helger.bootstrap3.table.BootstrapTableFormView;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.state.EContinue;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.CHCParam;
import com.helger.html.hc.IHCCell;
import com.helger.html.hc.html.AbstractHCForm;
import com.helger.html.hc.html.HCA;
import com.helger.html.hc.html.HCCol;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCRow;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.peppol.app.menu.CMenuPublic;
import com.helger.peppol.app.ui.AppCommonUI;
import com.helger.peppol.mgr.MetaManager;
import com.helger.peppol.page.AbstractAppFormPage;
import com.helger.peppol.page.ui.IdentifierIssuingAgencySelect;
import com.helger.peppol.page.ui.SMPTransportProfileSelect;
import com.helger.peppol.testep.domain.TestEndpoint;
import com.helger.peppol.testep.domain.TestEndpointManager;
import com.helger.validation.error.FormErrors;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webctrls.custom.EDefaultIcon;
import com.helger.webctrls.custom.toolbar.IButtonToolbar;
import com.helger.webctrls.datatables.DataTables;
import com.helger.webctrls.page.EWebPageFormAction;

import eu.europa.ec.cipa.peppol.identifier.issuingagency.EPredefinedIdentifierIssuingAgency;
import eu.europa.ec.cipa.smp.client.ESMPTransportProfile;

public class PagePublicToolsTestEndpoints extends AbstractAppFormPage <TestEndpoint>
{
  private static final String FIELD_COMPANY_NAME = "companyname";
  private static final String FIELD_CONTACT_PERSON = "contactperson";
  private static final String FIELD_PARTICIPANT_ID_ISO6523 = "participantidscheme";
  private static final String FIELD_PARTICIPANT_ID_VALUE = "participantidvalue";
  private static final String FIELD_TRANSPORT_PROFILE = "transportprofile";

  public PagePublicToolsTestEndpoints (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Test endpoints");
  }

  @Nonnull
  private static SimpleURL _createParticipantInfoURL (@Nonnull final WebPageExecutionContext aWPEC,
                                                      @Nonnull final TestEndpoint aTestEndpoint)
  {
    return aWPEC.getLinkToMenuItem (CMenuPublic.MENU_TOOLS_PARTICIPANT_INFO)
                .add (CHCParam.PARAM_ACTION, ACTION_PERFORM)
                .add (PagePublicToolsParticipantInformation.FIELD_ID_ISO6523, aTestEndpoint.getParticipantIDScheme ())
                .add (PagePublicToolsParticipantInformation.FIELD_ID_VALUE, aTestEndpoint.getParticipantIDValue ());
  }

  @Override
  protected TestEndpoint getSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, @Nullable final String sID)
  {
    final TestEndpointManager aTestEndpointMgr = MetaManager.getTestEndpointMgr ();
    return aTestEndpointMgr.getTestEndpointOfID (sID);
  }

  @Override
  protected void modifyViewToolbar (@Nonnull final WebPageExecutionContext aWPEC,
                                    @Nonnull final TestEndpoint aSelectedObject,
                                    @Nonnull final IButtonToolbar <?> aToolbar)
  {
    aToolbar.addChild (new BootstrapButton ().setOnClick (_createParticipantInfoURL (aWPEC, aSelectedObject))
                                             .addChild ("Show participant information")
                                             .setIcon (EDefaultIcon.MAGNIFIER));
  }

  @Override
  protected void showSelectedObject (@Nonnull final WebPageExecutionContext aWPEC,
                                     @Nonnull final TestEndpoint aSelectedObject)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    final BootstrapTableFormView aTable = aNodeList.addAndReturnChild (new BootstrapTableFormView (new HCCol (170),
                                                                                                   HCCol.star ()));
    aTable.createItemRow ()
          .setLabel ("Creation")
          .setCtrl (AppCommonUI.getDTAndUser (aWPEC,
                                              aSelectedObject.getCreationDateTime (),
                                              aSelectedObject.getCreationUserID ()));
    if (aSelectedObject.getLastModificationDateTime () != null)
    {
      aTable.createItemRow ()
            .setLabel ("Last modification")
            .setCtrl (AppCommonUI.getDTAndUser (aWPEC,
                                                aSelectedObject.getLastModificationDateTime (),
                                                aSelectedObject.getLastModificationUserID ()));
    }
    if (aSelectedObject.getLastModificationDateTime () != null)
    {
      aTable.createItemRow ()
            .setLabel ("Deletion")
            .setCtrl (AppCommonUI.getDTAndUser (aWPEC,
                                                aSelectedObject.getDeletionDateTime (),
                                                aSelectedObject.getDeletionUserID ()));
    }
    aTable.createItemRow ().setLabel ("Company name").setCtrl (aSelectedObject.getCompanyName ());
    if (StringHelper.hasText (aSelectedObject.getContactPerson ()))
    {
      aTable.createItemRow ().setLabel ("Contact person").setCtrl (aSelectedObject.getContactPerson ());
    }
    aTable.createItemRow ()
          .setLabel ("Participant information")
          .setCtrl (aSelectedObject.getParticipantIDScheme () + ":" + aSelectedObject.getParticipantIDValue ());
    aTable.createItemRow ()
          .setLabel ("Transport profile")
          .setCtrl (SMPTransportProfileSelect.getShortName (aSelectedObject.getTransportProfile ()) +
                    " (" +
                    aSelectedObject.getTransportProfile ().getID () +
                    ")");
  }

  @Override
  @Nonnull
  protected EContinue beforeProcessing (@Nonnull final WebPageExecutionContext aWPEC,
                                        @Nullable final TestEndpoint aSelectedObject,
                                        @Nullable final EWebPageFormAction eFormAction)
  {
    // Only logged in users can modify something
    // TODO use !eFormAction.isReadOnly() in ph-webctrls > 2.5.1
    if (eFormAction != null &&
        !eFormAction.equals (EWebPageFormAction.VIEW) &&
        !LoggedInUserManager.getInstance ().isUserLoggedInInCurrentSession ())
      return EContinue.BREAK;

    return super.beforeProcessing (aWPEC, aSelectedObject, eFormAction);
  }

  @Override
  protected boolean isEditAllowed (@Nonnull final WebPageExecutionContext aWPEC,
                                   @Nullable final TestEndpoint aSelectedObject)
  {
    if (aSelectedObject == null)
      return false;

    // Only owner can edit his object
    if (!aSelectedObject.getCreationUserID ().equals (LoggedInUserManager.getInstance ().getCurrentUserID ()))
      return false;

    return super.isEditAllowed (aWPEC, aSelectedObject);
  }

  @Override
  protected void showInputForm (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nullable final TestEndpoint aSelectedObject,
                                @Nonnull final AbstractHCForm <?> aForm,
                                final boolean bEdit,
                                final boolean bCopy,
                                @Nonnull final FormErrors aFormErrors)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final BootstrapForm aRealForm = (BootstrapForm) aForm;
    aRealForm.setLeft (3);
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Company name")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_COMPANY_NAME,
                                                                                             aSelectedObject == null ? null
                                                                                                                    : aSelectedObject.getCompanyName ())))
                                                     .setHelpText ("The name of the company operating the test AccessPoint")
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_COMPANY_NAME)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Contact person")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_CONTACT_PERSON,
                                                                                             aSelectedObject == null ? null
                                                                                                                    : aSelectedObject.getContactPerson ())))
                                                     .setHelpText ("The contact person being in charge of the test endpoint. This field is free text and may contain an optional email address.")
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_CONTACT_PERSON)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier scheme")
                                                     .setCtrl (new IdentifierIssuingAgencySelect (new RequestField (FIELD_PARTICIPANT_ID_ISO6523,
                                                                                                                    aSelectedObject == null ? null
                                                                                                                                           : aSelectedObject.getParticipantIDScheme ()),
                                                                                                  aDisplayLocale))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PARTICIPANT_ID_ISO6523)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier value")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_PARTICIPANT_ID_VALUE,
                                                                                             aSelectedObject == null ? null
                                                                                                                    : aSelectedObject.getParticipantIDValue ())))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PARTICIPANT_ID_VALUE)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Transport profile")
                                                     .setCtrl (new SMPTransportProfileSelect (new RequestField (FIELD_TRANSPORT_PROFILE,
                                                                                                                aSelectedObject == null ? null
                                                                                                                                       : aSelectedObject.getTransportProfile ()
                                                                                                                                                        .getID ()),
                                                                                              aDisplayLocale))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_TRANSPORT_PROFILE)));
  }

  @Nullable
  private static EPredefinedIdentifierIssuingAgency _getIIA (@Nullable final String sSchemeID)
  {
    if (StringHelper.hasText (sSchemeID))
      for (final EPredefinedIdentifierIssuingAgency eAgency : EPredefinedIdentifierIssuingAgency.values ())
        if (eAgency.getISO6523Code ().equals (sSchemeID) || eAgency.getSchemeID ().equals (sSchemeID))
          return eAgency;
    return null;
  }

  @Override
  protected void validateAndSaveInputParameters (@Nonnull final WebPageExecutionContext aWPEC,
                                                 @Nullable final TestEndpoint aSelectedObject,
                                                 @Nonnull final FormErrors aFormErrors,
                                                 final boolean bEdit)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final TestEndpointManager aTestEndpointMgr = MetaManager.getTestEndpointMgr ();

    final String sCompanyName = aWPEC.getAttributeAsString (FIELD_COMPANY_NAME);
    final String sContactPerson = aWPEC.getAttributeAsString (FIELD_CONTACT_PERSON);
    final String sParticipantIDISO6523 = aWPEC.getAttributeAsString (FIELD_PARTICIPANT_ID_ISO6523);
    final EPredefinedIdentifierIssuingAgency eAgency = _getIIA (sParticipantIDISO6523);
    final String sParticipantIDValue = aWPEC.getAttributeAsString (FIELD_PARTICIPANT_ID_VALUE);
    final String sTransportProfile = aWPEC.getAttributeAsString (FIELD_TRANSPORT_PROFILE);
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.getFromIDOrNull (sTransportProfile);
    final String sTransportProfileName = SMPTransportProfileSelect.getShortName (eTransportProfile);

    if (StringHelper.hasNoText (sCompanyName))
      aFormErrors.addFieldError (FIELD_COMPANY_NAME, "Please provide the company name");

    if (StringHelper.hasNoText (sParticipantIDISO6523))
      aFormErrors.addFieldError (FIELD_PARTICIPANT_ID_ISO6523, "Please select a participant identifier scheme");
    else
      if (eAgency == null)
        aFormErrors.addFieldError (FIELD_PARTICIPANT_ID_ISO6523, "Please select a valid participant identifier scheme");

    if (StringHelper.hasNoText (sParticipantIDValue))
      aFormErrors.addFieldError (FIELD_PARTICIPANT_ID_VALUE, "Please provide a participant identifier value");

    if (eTransportProfile == null)
      aFormErrors.addFieldError (FIELD_TRANSPORT_PROFILE, "Please select a transport profile");

    if (aFormErrors.isEmpty ())
    {
      // Check if participant ID and transport profile are already registered
      if (aTestEndpointMgr.containsTestEndpoint (sParticipantIDISO6523, sParticipantIDValue, eTransportProfile))
        aFormErrors.addFieldError (FIELD_TRANSPORT_PROFILE, "A test endpoint for " +
                                                            sParticipantIDISO6523 +
                                                            ":" +
                                                            sParticipantIDValue +
                                                            " and transport profile " +
                                                            sTransportProfileName +
                                                            " is already registered!");
    }

    if (aFormErrors.isEmpty ())
    {
      if (bEdit)
      {
        aTestEndpointMgr.updateTestEndpoint (aSelectedObject.getID (),
                                             sCompanyName,
                                             sContactPerson,
                                             sParticipantIDISO6523,
                                             sParticipantIDValue,
                                             eTransportProfile);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Successfully edited the test endpoint for " +
                                                                 sParticipantIDISO6523 +
                                                                 ":" +
                                                                 sParticipantIDValue +
                                                                 " with transport profile " +
                                                                 sTransportProfileName));
      }
      else
      {
        aTestEndpointMgr.createTestEndpoint (sCompanyName,
                                             sContactPerson,
                                             sParticipantIDISO6523,
                                             sParticipantIDValue,
                                             eTransportProfile);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Successfully added the new test endpoint for " +
                                                                 sParticipantIDISO6523 +
                                                                 ":" +
                                                                 sParticipantIDValue +
                                                                 " with transport profile " +
                                                                 sTransportProfileName));
      }
    }
  }

  @Override
  protected void showListOfExistingObjects (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final TestEndpointManager aTestEndpointMgr = MetaManager.getTestEndpointMgr ();
    final boolean bUserIsLoggedIn = LoggedInUserManager.getInstance ().isUserLoggedInInCurrentSession ();

    // Toolbar on top
    final BootstrapButtonToolbar aToolbar = aNodeList.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
    if (bUserIsLoggedIn)
      aToolbar.addButtonNew ("Create new test endpoint", createCreateURL (aWPEC));
    else
      aToolbar.addChild (new BootstrapLabel (EBootstrapLabelType.INFO).addChild ("You need to be logged in to create test endpoints."));

    // List existing
    final BootstrapTable aTable = new BootstrapTable (HCCol.star (), HCCol.star (), HCCol.star (), createActionCol (2)).setID (getID ());
    aTable.addHeaderRow ().addCells ("Participant ID", "Company", "Transport profile", "Actions");

    for (final TestEndpoint aCurObject : aTestEndpointMgr.getAllTestEndpoints ())
      if (!aCurObject.isDeleted ())
      {
        final ISimpleURL aViewLink = createViewURL (aWPEC, aCurObject);

        final HCRow aRow = aTable.addBodyRow ();
        aRow.addCell (new HCA (aViewLink).addChild (aCurObject.getDisplayName ()));
        aRow.addCell (aCurObject.getCompanyName ());
        aRow.addCell (SMPTransportProfileSelect.getShortName (aCurObject.getTransportProfile ()));

        final IHCCell <?> aActionCell = aRow.addCell ();
        if (isEditAllowed (aWPEC, aCurObject))
          aActionCell.addChildren (createEditLink (aWPEC, aCurObject), new HCTextNode (" "));
        else
          aActionCell.addChildren (createEmptyAction (), new HCTextNode (" "));
        if (bUserIsLoggedIn)
          aActionCell.addChildren (new HCTextNode (" "), createCopyLink (aWPEC, aCurObject));
        else
          aActionCell.addChildren (createEmptyAction (), new HCTextNode (" "));
        // Visible for all
        aActionCell.addChildren (new HCTextNode (" "),
                                 new HCA (_createParticipantInfoURL (aWPEC, aCurObject)).setTitle ("Show participant information")
                                                                                        .addChild (EDefaultIcon.MAGNIFIER.getAsNode ()));
      }
    aNodeList.addChild (aTable);

    final DataTables aDataTables = getStyler ().createDefaultDataTables (aWPEC, aTable);
    aDataTables.getOrCreateColumnOfTarget (3).addClass (CSS_CLASS_ACTION_COL).setSortable (false);
    aDataTables.setInitialSorting (1, ESortOrder.ASCENDING);
    aNodeList.addChild (aDataTables);
  }
}
