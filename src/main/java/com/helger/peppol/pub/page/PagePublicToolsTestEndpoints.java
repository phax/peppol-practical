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
package com.helger.peppol.pub.page;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.OverrideOnDemand;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.tabular.IHCCell;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.peppol.app.AppHelper;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.identifier.peppol.issuingagency.EPredefinedIdentifierIssuingAgency;
import com.helger.peppol.pub.CMenuPublic;
import com.helger.peppol.pub.testendpoint.TestEndpoint;
import com.helger.peppol.pub.testendpoint.TestEndpointManager;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.smp.ESMPTransportProfile;
import com.helger.peppol.ui.AppCommonUI;
import com.helger.peppol.ui.IdentifierIssuingAgencySelect;
import com.helger.peppol.ui.SMLSelect;
import com.helger.peppol.ui.SMPTransportProfileSelect;
import com.helger.peppol.ui.page.AbstractAppWebPageForm;
import com.helger.photon.bootstrap3.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap3.alert.BootstrapInfoBox;
import com.helger.photon.bootstrap3.alert.BootstrapQuestionBox;
import com.helger.photon.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap3.button.BootstrapButton;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.form.BootstrapForm;
import com.helger.photon.bootstrap3.form.BootstrapFormGroup;
import com.helger.photon.bootstrap3.form.BootstrapViewForm;
import com.helger.photon.bootstrap3.label.BootstrapLabel;
import com.helger.photon.bootstrap3.label.EBootstrapLabelType;
import com.helger.photon.bootstrap3.pages.handler.AbstractBootstrapWebPageActionHandlerDelete;
import com.helger.photon.bootstrap3.uictrls.datatables.BootstrapDTColAction;
import com.helger.photon.bootstrap3.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.security.login.LoggedInUserManager;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.page.EWebPageFormAction;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.DataTables;
import com.helger.photon.uictrls.datatables.column.DTCol;

public class PagePublicToolsTestEndpoints extends AbstractAppWebPageForm <TestEndpoint>
{
  private static final String FIELD_COMPANY_NAME = "companyname";
  private static final String FIELD_CONTACT_PERSON = "contactperson";
  private static final String FIELD_PARTICIPANT_ID_ISO6523 = "participantidscheme";
  private static final String FIELD_PARTICIPANT_ID_VALUE = "participantidvalue";
  private static final String FIELD_TRANSPORT_PROFILE = "transportprofile";
  private static final String FIELD_SML = "sml";

  public PagePublicToolsTestEndpoints (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Test endpoints");
    setDeleteHandler (new AbstractBootstrapWebPageActionHandlerDelete <TestEndpoint, WebPageExecutionContext> ()
    {
      @Override
      protected void showDeleteQuery (@Nonnull final WebPageExecutionContext aWPEC,
                                      @Nonnull final BootstrapForm aForm,
                                      @Nonnull final TestEndpoint aSelectedObject)
      {
        aForm.addChild (new BootstrapQuestionBox ().addChild ("Are you sure you want to delete the test endpoint '" +
                                                              aSelectedObject.getParticipantIDValue () +
                                                              "' for transport profile '" +
                                                              AppHelper.getSMPTransportProfileShortName (aSelectedObject.getTransportProfile ()) +
                                                              "'?"));
      }

      @Override
      @OverrideOnDemand
      protected void performDelete (@Nonnull final WebPageExecutionContext aWPEC,
                                    @Nonnull final TestEndpoint aSelectedObject)
      {
        final TestEndpointManager aTestEndpointMgr = PPMetaManager.getTestEndpointMgr ();
        if (aTestEndpointMgr.deleteTestEndpoint (aSelectedObject.getID ()).isChanged ())
          aWPEC.postRedirectGetInternal (new BootstrapSuccessBox ().addChild ("The test endpoint was successfully deleted!"));
        else
          aWPEC.postRedirectGetInternal (new BootstrapErrorBox ().addChild ("Error deleting the test endpoint!"));
      }
    });
  }

  @Nonnull
  private static SimpleURL _createParticipantInfoURL (@Nonnull final WebPageExecutionContext aWPEC,
                                                      @Nonnull final TestEndpoint aTestEndpoint)
  {
    return aWPEC.getLinkToMenuItem (CMenuPublic.MENU_TOOLS_PARTICIPANT_INFO)
                .add (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM)
                .add (PagePublicToolsParticipantInformation.FIELD_ID_ISO6523, aTestEndpoint.getParticipantIDScheme ())
                .add (PagePublicToolsParticipantInformation.FIELD_ID_VALUE, aTestEndpoint.getParticipantIDValue ())
                .add (PagePublicToolsParticipantInformation.FIELD_SML, aTestEndpoint.getSML ().getID ());
  }

  @Override
  protected TestEndpoint getSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, @Nullable final String sID)
  {
    final TestEndpointManager aTestEndpointMgr = PPMetaManager.getTestEndpointMgr ();
    return aTestEndpointMgr.getTestEndpointOfID (sID);
  }

  @Override
  protected boolean isActionAllowed (@Nonnull final WebPageExecutionContext aWPEC,
                                     @Nonnull final EWebPageFormAction eFormAction,
                                     @Nullable final TestEndpoint aSelectedObject)
  {
    if (eFormAction.isReadonly ())
      return true;

    if (eFormAction.isEdit ())
      return aSelectedObject.getCreationUserID ().equals (LoggedInUserManager.getInstance ().getCurrentUserID ());

    if (eFormAction.isDelete ())
      return !aSelectedObject.isDeleted () &&
             aSelectedObject.getCreationUserID ().equals (LoggedInUserManager.getInstance ().getCurrentUserID ());

    // Only logged in users can modify something
    return LoggedInUserManager.getInstance ().isUserLoggedInInCurrentSession ();
  }

  @Override
  protected void modifyViewToolbar (@Nonnull final WebPageExecutionContext aWPEC,
                                    @Nonnull final TestEndpoint aSelectedObject,
                                    @Nonnull final BootstrapButtonToolbar aToolbar)
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

    final BootstrapViewForm aForm = aNodeList.addAndReturnChild (new BootstrapViewForm ());
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Creation")
                                                 .setCtrl (AppCommonUI.getDTAndUser (aWPEC,
                                                                                     aSelectedObject.getCreationDateTime (),
                                                                                     aSelectedObject.getCreationUserID ())));
    if (aSelectedObject.getLastModificationDateTime () != null)
    {
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Last modification")
                                                   .setCtrl (AppCommonUI.getDTAndUser (aWPEC,
                                                                                       aSelectedObject.getLastModificationDateTime (),
                                                                                       aSelectedObject.getLastModificationUserID ())));
    }
    if (aSelectedObject.getLastModificationDateTime () != null)
    {
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Deletion")
                                                   .setCtrl (AppCommonUI.getDTAndUser (aWPEC,
                                                                                       aSelectedObject.getDeletionDateTime (),
                                                                                       aSelectedObject.getDeletionUserID ())));
    }
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Company name")
                                                 .setCtrl (aSelectedObject.getCompanyName ()));
    if (StringHelper.hasText (aSelectedObject.getContactPerson ()))
    {
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Contact person")
                                                   .setCtrl (aSelectedObject.getContactPerson ()));
    }
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Participant information")
                                                 .setCtrl (aSelectedObject.getParticipantIDScheme () +
                                                           ":" +
                                                           aSelectedObject.getParticipantIDValue ()));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Transport profile")
                                                 .setCtrl (AppHelper.getSMPTransportProfileShortName (aSelectedObject.getTransportProfile ()) +
                                                           " (" +
                                                           aSelectedObject.getTransportProfile ().getID () +
                                                           ")"));
  }

  @Override
  protected void showInputForm (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nullable final TestEndpoint aSelectedObject,
                                @Nonnull final BootstrapForm aForm,
                                @Nonnull final EWebPageFormAction eFormAction,
                                @Nonnull final FormErrorList aFormErrors)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final BootstrapForm aRealForm = aForm;
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
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML")
                                                     .setCtrl (new SMLSelect (new RequestField (FIELD_SML,
                                                                                                aSelectedObject == null ? ESML.DIGIT_PRODUCTION
                                                                                                                        : aSelectedObject.getSML ())))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SML)));
  }

  @Override
  protected void validateAndSaveInputParameters (@Nonnull final WebPageExecutionContext aWPEC,
                                                 @Nullable final TestEndpoint aSelectedObject,
                                                 @Nonnull final FormErrorList aFormErrors,
                                                 @Nonnull final EWebPageFormAction eFormAction)
  {
    final TestEndpointManager aTestEndpointMgr = PPMetaManager.getTestEndpointMgr ();

    final String sCompanyName = aWPEC.getAttributeAsString (FIELD_COMPANY_NAME);
    final String sContactPerson = aWPEC.getAttributeAsString (FIELD_CONTACT_PERSON);
    final String sParticipantIDISO6523 = aWPEC.getAttributeAsString (FIELD_PARTICIPANT_ID_ISO6523);
    final EPredefinedIdentifierIssuingAgency eAgency = AppHelper.getIdentifierIssuingAgencyOfID (sParticipantIDISO6523);
    final String sParticipantIDValue = aWPEC.getAttributeAsString (FIELD_PARTICIPANT_ID_VALUE);
    final String sTransportProfile = aWPEC.getAttributeAsString (FIELD_TRANSPORT_PROFILE);
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.getFromIDOrNull (sTransportProfile);
    final String sTransportProfileName = AppHelper.getSMPTransportProfileShortName (eTransportProfile);
    final String sSML = aWPEC.getAttributeAsString (FIELD_SML);
    final ISMLInfo aSML = ESML.getFromIDOrNull (sSML);

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

    if (aSML == null)
      aFormErrors.addFieldError (FIELD_SML, "Please select an SML where the participant is registered");

    if (aFormErrors.isEmpty ())
    {
      // Check if participant ID and transport profile are already registered
      final TestEndpoint aSameIDTestEndpoint = aTestEndpointMgr.getTestEndpoint (sParticipantIDISO6523,
                                                                                 sParticipantIDValue,
                                                                                 eTransportProfile);
      if (aSameIDTestEndpoint != null && !aSameIDTestEndpoint.equals (aSelectedObject))
        aFormErrors.addFieldError (FIELD_TRANSPORT_PROFILE,
                                   "Another test endpoint for " +
                                                            sParticipantIDISO6523 +
                                                            ":" +
                                                            sParticipantIDValue +
                                                            " and transport profile " +
                                                            sTransportProfileName +
                                                            " is already registered!");
    }

    if (aFormErrors.isEmpty ())
    {
      if (eFormAction.isEdit ())
      {
        aTestEndpointMgr.updateTestEndpoint (aSelectedObject.getID (),
                                             sCompanyName,
                                             sContactPerson,
                                             sParticipantIDISO6523,
                                             sParticipantIDValue,
                                             eTransportProfile,
                                             aSML);
        aWPEC.postRedirectGetInternal (new BootstrapSuccessBox ().addChild ("Successfully edited the test endpoint for " +
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
                                             eTransportProfile,
                                             aSML);
        aWPEC.postRedirectGetInternal (new BootstrapSuccessBox ().addChild ("Successfully added the new test endpoint for " +
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
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final TestEndpointManager aTestEndpointMgr = PPMetaManager.getTestEndpointMgr ();
    final boolean bUserIsLoggedIn = LoggedInUserManager.getInstance ().isUserLoggedInInCurrentSession ();

    // Toolbar on top
    final BootstrapButtonToolbar aToolbar = aNodeList.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
    if (bUserIsLoggedIn)
      aToolbar.addButtonNew ("Create new test endpoint", createCreateURL (aWPEC));
    else
      aToolbar.addChild (new BootstrapLabel (EBootstrapLabelType.INFO).addChild ("You need to be logged in to create test endpoints."));

    aNodeList.addChild (new BootstrapInfoBox ().addChild ("Test endpoints are special PEPPOL participant identifiers whose sole purpose is the usage for testing. So if you are an PEPPOL AccessPoint provider and want to test your implementation you may use the below listed participant identifiers as test recipients."));

    // List existing
    final HCTable aTable = new HCTable (new DTCol ("Participant ID"),
                                        new DTCol ("Company").setInitialSorting (ESortOrder.ASCENDING),
                                        new DTCol ("Transport profile"),
                                        new DTCol ("SML"),
                                        new BootstrapDTColAction (aDisplayLocale)).setID (getID ());

    for (final TestEndpoint aCurObject : aTestEndpointMgr.getAllActiveTestEndpoints ())
    {
      final ISimpleURL aViewLink = createViewURL (aWPEC, aCurObject);

      final HCRow aRow = aTable.addBodyRow ();
      aRow.addCell (new HCA (aViewLink).addChild (aCurObject.getDisplayName ()));
      aRow.addCell (aCurObject.getCompanyName ());
      aRow.addCell (AppHelper.getSMPTransportProfileShortName (aCurObject.getTransportProfile ()));
      aRow.addCell (aCurObject.getSML ().getDisplayName ());

      final IHCCell <?> aActionCell = aRow.addCell ();
      if (isActionAllowed (aWPEC, EWebPageFormAction.EDIT, aCurObject))
        aActionCell.addChild (createEditLink (aWPEC, aCurObject));
      else
        aActionCell.addChild (createEmptyAction ());
      aActionCell.addChild (new HCTextNode (" "));

      if (isActionAllowed (aWPEC, EWebPageFormAction.DELETE, aCurObject))
        aActionCell.addChild (createDeleteLink (aWPEC, aCurObject));
      else
        aActionCell.addChild (createEmptyAction ());
      aActionCell.addChild (new HCTextNode (" "));

      if (bUserIsLoggedIn)
        aActionCell.addChild (createCopyLink (aWPEC, aCurObject));
      else
        aActionCell.addChild (createEmptyAction ());
      aActionCell.addChild (new HCTextNode (" "));

      // Visible for all
      aActionCell.addChild (new HCA (_createParticipantInfoURL (aWPEC,
                                                                aCurObject)).setTitle ("Show participant information")
                                                                            .addChild (EDefaultIcon.MAGNIFIER.getAsNode ()));
    }
    aNodeList.addChild (aTable);

    final DataTables aDataTables = BootstrapDataTables.createDefaultDataTables (aWPEC, aTable);
    aNodeList.addChild (aDataTables);
  }
}
