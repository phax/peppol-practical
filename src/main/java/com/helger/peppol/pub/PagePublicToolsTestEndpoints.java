/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import com.helger.annotation.Nonempty;
import com.helger.annotation.style.OverrideOnDemand;
import com.helger.base.compare.ESortOrder;
import com.helger.base.string.StringHelper;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.tabular.IHCCell;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.sharedui.SharedUIHelper;
import com.helger.peppol.sharedui.domain.ISMLConfiguration;
import com.helger.peppol.sharedui.mgr.ISMLConfigurationManager;
import com.helger.peppol.sharedui.mgr.SharedUIMetaManager;
import com.helger.peppol.sharedui.page.AbstractAppWebPageForm;
import com.helger.peppol.sharedui.page.pub.CSharedUIMenuPublic;
import com.helger.peppol.sharedui.page.pub.PagePublicToolsParticipantInformation;
import com.helger.peppol.sharedui.ui.select.ParticipantIdentifierSchemeSelect;
import com.helger.peppol.sharedui.ui.select.SMLConfigurationSelect;
import com.helger.peppol.sharedui.ui.select.SMPTransportProfileSelect;
import com.helger.peppol.smp.ESMPTransportProfile;
import com.helger.peppol.testendpoint.TestEndpoint;
import com.helger.peppol.testendpoint.TestEndpointManager;
import com.helger.peppol.ui.AppCommonUI;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.peppolid.peppol.pidscheme.EPredefinedParticipantIdentifierScheme;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.form.BootstrapViewForm;
import com.helger.photon.bootstrap4.pages.handler.AbstractBootstrapWebPageActionHandlerDelete;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDTColAction;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.security.login.LoggedInUserManager;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.page.EWebPageFormAction;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.DataTables;
import com.helger.photon.uictrls.datatables.column.DTCol;
import com.helger.url.ISimpleURL;
import com.helger.url.SimpleURL;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class PagePublicToolsTestEndpoints extends AbstractAppWebPageForm <TestEndpoint>
{
  private static final String FIELD_COMPANY_NAME = "companyname";
  private static final String FIELD_CONTACT_PERSON = "contactperson";
  private static final String FIELD_PARTICIPANT_ID_ISSUER = "participantidissuer";
  private static final String FIELD_PARTICIPANT_ID_VALUE = "participantidvalue";
  private static final String FIELD_TRANSPORT_PROFILE = "transportprofile";
  private static final String FIELD_SML = "sml";

  public PagePublicToolsTestEndpoints (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Test Endpoints");
    setDeleteHandler (new AbstractBootstrapWebPageActionHandlerDelete <TestEndpoint, WebPageExecutionContext> ()
    {
      @Override
      protected void showQuery (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nonnull final BootstrapForm aForm,
                                @Nonnull final TestEndpoint aSelectedObject)
      {
        aForm.addChild (question ("Are you sure you want to delete the Test Endpoint '" +
                                  aSelectedObject.getParticipantIDValue () +
                                  "' for Transport Profile '" +
                                  SharedUIHelper.getSMPTransportProfileShortName (aSelectedObject.getTransportProfile ()) +
                                  "'?"));
      }

      @Override
      @OverrideOnDemand
      protected void performAction (@Nonnull final WebPageExecutionContext aWPEC,
                                    @Nonnull final TestEndpoint aSelectedObject)
      {
        final TestEndpointManager aTestEndpointMgr = PPMetaManager.getTestEndpointMgr ();
        if (aTestEndpointMgr.deleteTestEndpoint (aSelectedObject.getID ()).isChanged ())
          aWPEC.postRedirectGetInternal (success ("The Test Endpoint was successfully deleted!"));
        else
          aWPEC.postRedirectGetInternal (error ("Error deleting the Test Endpoint!"));
      }
    });
  }

  @Nonnull
  private static SimpleURL _createParticipantInfoURL (@Nonnull final WebPageExecutionContext aWPEC,
                                                      @Nonnull final TestEndpoint aTestEndpoint)
  {
    return aWPEC.getLinkToMenuItem (CSharedUIMenuPublic.MENU_TOOLS_PARTICIPANT_INFO)
                .add (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM)
                .add (PagePublicToolsParticipantInformation.FIELD_ID_SCHEME,
                      PeppolIdentifierHelper.DEFAULT_PARTICIPANT_SCHEME)
                .add (PagePublicToolsParticipantInformation.FIELD_ID_VALUE, aTestEndpoint.getParticipantID ())
                .add (PagePublicToolsParticipantInformation.FIELD_SML, aTestEndpoint.getSML ().getID ())
                .add (PagePublicToolsParticipantInformation.PARAM_QUERY_BUSINESS_CARD, true)
                .add (PagePublicToolsParticipantInformation.PARAM_VERIFY_SIGNATURES, true)
                .add (PagePublicToolsParticipantInformation.PARAM_XSD_VALIDATION, true);
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

    final LoggedInUserManager aLIUM = LoggedInUserManager.getInstance ();

    if (eFormAction.isEdit ())
      return !aSelectedObject.isDeleted () &&
             (aSelectedObject.getCreationUserID ().equals (aLIUM.getCurrentUserID ()) ||
              aLIUM.isCurrentUserAdministrator ());

    if (eFormAction.isDelete ())
      return !aSelectedObject.isDeleted () &&
             (aSelectedObject.getCreationUserID ().equals (aLIUM.getCurrentUserID ()) ||
              aLIUM.isCurrentUserAdministrator ());

    if (eFormAction.isUndelete ())
      return aSelectedObject.isDeleted () && aLIUM.isCurrentUserAdministrator ();

    if (eFormAction.isCopy ())
      return false;

    // Only logged in users can create something
    return aLIUM.isUserLoggedInInCurrentSession ();
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
    if (aSelectedObject.getDeletionDateTime () != null)
    {
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Deletion")
                                                   .setCtrl (AppCommonUI.getDTAndUser (aWPEC,
                                                                                       aSelectedObject.getDeletionDateTime (),
                                                                                       aSelectedObject.getDeletionUserID ())));
    }
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Company name")
                                                 .setCtrl (aSelectedObject.getCompanyName ()));
    if (StringHelper.isNotEmpty (aSelectedObject.getContactPerson ()))
    {
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Contact person")
                                                   .setCtrl (aSelectedObject.getContactPerson ()));
    }
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Participant information")
                                                 .setCtrl (aSelectedObject.getParticipantIDIssuer () +
                                                           ":" +
                                                           aSelectedObject.getParticipantIDValue ()));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Transport profile")
                                                 .setCtrl (SharedUIHelper.getSMPTransportProfileShortName (aSelectedObject.getTransportProfile ()) +
                                                           " (" +
                                                           aSelectedObject.getTransportProfile ().getID () +
                                                           ")"));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("SML")
                                                 .setCtrl (aSelectedObject.getSML ().getDisplayName ()));
  }

  @Override
  protected void showInputForm (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nullable final TestEndpoint aSelectedObject,
                                @Nonnull final BootstrapForm aForm,
                                final boolean bFormSubmitted,
                                @Nonnull final EWebPageFormAction eFormAction,
                                @Nonnull final FormErrorList aFormErrors)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final BootstrapForm aRealForm = aForm;
    aRealForm.setLeft (3);
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Company name")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_COMPANY_NAME,
                                                                                             aSelectedObject == null
                                                                                                                     ? null
                                                                                                                     : aSelectedObject.getCompanyName ())))
                                                     .setHelpText ("The name of the company operating the test AccessPoint")
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_COMPANY_NAME)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Contact person")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_CONTACT_PERSON,
                                                                                             aSelectedObject == null
                                                                                                                     ? null
                                                                                                                     : aSelectedObject.getContactPerson ())))
                                                     .setHelpText ("The contact person being in charge of the test endpoint. This field is free text and may contain an optional email address.")
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_CONTACT_PERSON)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier issuing agency")
                                                     .setCtrl (new ParticipantIdentifierSchemeSelect (new RequestField (FIELD_PARTICIPANT_ID_ISSUER,
                                                                                                                        aSelectedObject ==
                                                                                                                                                     null ? null
                                                                                                                                                          : aSelectedObject.getParticipantIDIssuer ()),
                                                                                                      aDisplayLocale))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PARTICIPANT_ID_ISSUER)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier value")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_PARTICIPANT_ID_VALUE,
                                                                                             aSelectedObject == null
                                                                                                                     ? null
                                                                                                                     : aSelectedObject.getParticipantIDValue ())))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PARTICIPANT_ID_VALUE)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Transport profile")
                                                     .setCtrl (new SMPTransportProfileSelect (new RequestField (FIELD_TRANSPORT_PROFILE,
                                                                                                                aSelectedObject ==
                                                                                                                                         null ? null
                                                                                                                                              : aSelectedObject.getTransportProfile ()
                                                                                                                                                               .getID ()),
                                                                                              aDisplayLocale))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_TRANSPORT_PROFILE)));
    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML")
                                                     .setCtrl (new SMLConfigurationSelect (new RequestField (FIELD_SML,
                                                                                                             aSelectedObject ==
                                                                                                                        null ? null
                                                                                                                             : aSelectedObject.getSML ()
                                                                                                                                              .getID ()),
                                                                                           false))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SML)));
  }

  @Override
  protected void validateAndSaveInputParameters (@Nonnull final WebPageExecutionContext aWPEC,
                                                 @Nullable final TestEndpoint aSelectedObject,
                                                 @Nonnull final FormErrorList aFormErrors,
                                                 @Nonnull final EWebPageFormAction eFormAction)
  {
    final TestEndpointManager aTestEndpointMgr = PPMetaManager.getTestEndpointMgr ();
    final ISMLConfigurationManager aSMLConfigurationMgr = SharedUIMetaManager.getSMLConfigurationMgr ();

    final String sCompanyName = aWPEC.params ().getAsString (FIELD_COMPANY_NAME);
    final String sContactPerson = aWPEC.params ().getAsString (FIELD_CONTACT_PERSON);
    final String sParticipantIDIssuer = aWPEC.params ().getAsString (FIELD_PARTICIPANT_ID_ISSUER);
    final EPredefinedParticipantIdentifierScheme eScheme = SharedUIHelper.getParticipantIdentifierSchemeOfID (sParticipantIDIssuer);
    final String sParticipantIDValue = aWPEC.params ().getAsString (FIELD_PARTICIPANT_ID_VALUE);
    final String sTransportProfile = aWPEC.params ().getAsString (FIELD_TRANSPORT_PROFILE);
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.getFromIDOrNull (sTransportProfile);
    final String sTransportProfileName = SharedUIHelper.getSMPTransportProfileShortName (eTransportProfile);
    final String sSMLID = aWPEC.params ().getAsString (FIELD_SML);
    final ISMLConfiguration aSML = aSMLConfigurationMgr.getSMLInfoOfID (sSMLID);

    if (StringHelper.isEmpty (sCompanyName))
      aFormErrors.addFieldError (FIELD_COMPANY_NAME, "Please provide the company name");

    if (StringHelper.isEmpty (sParticipantIDIssuer))
      aFormErrors.addFieldError (FIELD_PARTICIPANT_ID_ISSUER, "Please select a participant identifier issuing agency");
    else
      if (eScheme == null)
        aFormErrors.addFieldError (FIELD_PARTICIPANT_ID_ISSUER,
                                   "Please select a valid participant identifier issuing agency");

    if (StringHelper.isEmpty (sParticipantIDValue))
      aFormErrors.addFieldError (FIELD_PARTICIPANT_ID_VALUE, "Please provide a participant identifier value");

    if (eTransportProfile == null)
      aFormErrors.addFieldError (FIELD_TRANSPORT_PROFILE, "Please select a transport profile");

    if (aSML == null)
      aFormErrors.addFieldError (FIELD_SML, "Please select an SML where the participant is registered");

    if (aFormErrors.isEmpty ())
    {
      // Check if participant ID and transport profile are already registered
      final TestEndpoint aSameIDTestEndpoint = aTestEndpointMgr.getTestEndpoint (sParticipantIDIssuer,
                                                                                 sParticipantIDValue,
                                                                                 eTransportProfile);
      if (aSameIDTestEndpoint != null && !aSameIDTestEndpoint.equals (aSelectedObject))
        aFormErrors.addFieldError (FIELD_TRANSPORT_PROFILE,
                                   "Another test endpoint for " +
                                                            sParticipantIDIssuer +
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
                                             sParticipantIDIssuer,
                                             sParticipantIDValue,
                                             eTransportProfile,
                                             aSML);
        aWPEC.postRedirectGetInternal (success ("Successfully edited the test endpoint for " +
                                                sParticipantIDIssuer +
                                                ":" +
                                                sParticipantIDValue +
                                                " with transport profile " +
                                                sTransportProfileName));
      }
      else
      {
        aTestEndpointMgr.createTestEndpoint (sCompanyName,
                                             sContactPerson,
                                             sParticipantIDIssuer,
                                             sParticipantIDValue,
                                             eTransportProfile,
                                             aSML);
        aWPEC.postRedirectGetInternal (success ("Successfully added the new test endpoint for " +
                                                sParticipantIDIssuer +
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
      aToolbar.addButtonNew ("List your existing Test Endpoints", createCreateURL (aWPEC));
    else
      aToolbar.addChild (badgeInfo ("You need to be logged in to register Test Endpoints."));

    aNodeList.addChild (info ("Test Endpoints are special Peppol Participant Identifiers whose sole purpose is the usage for testing." +
                              " They must already exist and are NOT created by this tool." +
                              " So if you are a Peppol AccessPoint provider and want to test your implementation you may use the below listed participant identifiers as test recipients." +
                              " Please note that there is very little quality assurance on this list. Everybody can list its own Participant ID."));

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
      aRow.addCell (SharedUIHelper.getSMPTransportProfileShortName (aCurObject.getTransportProfile ()));
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
      aActionCell.addChild (new HCA (_createParticipantInfoURL (aWPEC, aCurObject)).setTitle (
                                                                                              "Show participant information")
                                                                                   .addChild (EDefaultIcon.MAGNIFIER.getAsNode ()));
    }
    aNodeList.addChild (aTable);

    final DataTables aDataTables = BootstrapDataTables.createDefaultDataTables (aWPEC, aTable);
    aNodeList.addChild (aDataTables);
  }
}
