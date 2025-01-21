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
package com.helger.peppol.secure;

import java.net.URL;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.commons.url.URLHelper;
import com.helger.html.hc.html.forms.HCCheckBox;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.tabular.HCTable;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.html.textlevel.HCCode;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.peppol.app.mgr.ISMLConfigurationManager;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.domain.ISMLConfiguration;
import com.helger.peppol.sml.CSMLDefault;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.ui.select.SMPAPITypeSelect;
import com.helger.peppol.ui.select.SMPIdentifierTypeSelect;
import com.helger.peppolid.factory.ESMPIdentifierType;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.form.BootstrapViewForm;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPageForm;
import com.helger.photon.bootstrap4.pages.handler.AbstractBootstrapWebPageActionHandlerDelete;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDTColAction;
import com.helger.photon.bootstrap4.uictrls.datatables.BootstrapDataTables;
import com.helger.photon.core.EPhotonCoreText;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.form.RequestFieldBoolean;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.page.EWebPageFormAction;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.datatables.DataTables;
import com.helger.photon.uictrls.datatables.column.DTCol;

public class PageSecureSMLConfiguration extends AbstractBootstrapWebPageForm <ISMLConfiguration, WebPageExecutionContext>
{
  private static final String FIELD_ID = "id";
  private static final String FIELD_DISPLAY_NAME = "displayname";
  private static final String FIELD_DNS_ZONE = "dnszone";
  private static final String FIELD_MANAGEMENT_ADDRESS_URL = "mgmtaddrurl";
  private static final String FIELD_CLIENT_CERTIFICATE_REQUIRED = "clientcert";
  private static final String FIELD_SMP_API_TYPE = "smpapitype";
  private static final String FIELD_SMP_ID_TYPE = "smpidype";
  private static final String FIELD_PRODUCTION = "production";

  private static final boolean DEFAULT_CLIENT_CERTIFICATE_REQUIRED = true;

  public PageSecureSMLConfiguration (@Nonnull @Nonempty final String sID)
  {
    super (sID, "SML configuration");
    setDeleteHandler (new AbstractBootstrapWebPageActionHandlerDelete <ISMLConfiguration, WebPageExecutionContext> ()
    {
      @Override
      protected void showQuery (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nonnull final BootstrapForm aForm,
                                @Nonnull final ISMLConfiguration aSelectedObject)
      {
        aForm.addChild (question ("Are you sure you want to delete the SML configuration '" +
                                  aSelectedObject.getDisplayName () +
                                  "' with ID '" +
                                  aSelectedObject.getID () +
                                  "'?"));
      }

      @Override
      protected void performAction (@Nonnull final WebPageExecutionContext aWPEC, @Nonnull final ISMLConfiguration aSelectedObject)
      {
        final ISMLConfigurationManager aSMLConfigurationMgr = PPMetaManager.getSMLConfigurationMgr ();
        if (aSMLConfigurationMgr.removeSMLInfo (aSelectedObject.getID ()).isChanged ())
          aWPEC.postRedirectGetInternal (success ("The SML configuration '" +
                                                  aSelectedObject.getDisplayName () +
                                                  "' with ID '" +
                                                  aSelectedObject.getID () +
                                                  "' was successfully deleted!"));
        else
          aWPEC.postRedirectGetInternal (error ("The SML configuration '" + aSelectedObject.getDisplayName () + "' could not be deleted!"));
      }
    });
  }

  @Override
  protected ISMLConfiguration getSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, @Nullable final String sID)
  {
    final ISMLConfigurationManager aSMLConfigurationMgr = PPMetaManager.getSMLConfigurationMgr ();
    return aSMLConfigurationMgr.getSMLInfoOfID (sID);
  }

  @Override
  protected boolean isActionAllowed (@Nonnull final WebPageExecutionContext aWPEC,
                                     @Nonnull final EWebPageFormAction eFormAction,
                                     @Nullable final ISMLConfiguration aSelectedObject)
  {
    return super.isActionAllowed (aWPEC, eFormAction, aSelectedObject);
  }

  @Override
  protected void showSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, @Nonnull final ISMLConfiguration aSelectedObject)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    aNodeList.addChild (getUIHandler ().createActionHeader ("Show details of SML configuration '" +
                                                            aSelectedObject.getDisplayName () +
                                                            "'"));

    final BootstrapViewForm aForm = new BootstrapViewForm ();
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("ID").setCtrl (aSelectedObject.getID ()));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Name").setCtrl (aSelectedObject.getDisplayName ()));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("DNS Zone").setCtrl (aSelectedObject.getDNSZone ()));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Publisher DNS Zone").setCtrl (aSelectedObject.getPublisherDNSZone ()));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Management Service URL")
                                                 .setCtrl (HCA.createLinkedWebsite (aSelectedObject.getManagementServiceURL ())));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Manage Service Metadata Endpoint")
                                                 .setCtrl (HCA.createLinkedWebsite (aSelectedObject.getManageServiceMetaDataEndpointAddress ()
                                                                                                   .toExternalForm ())));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Manage Participant Identifier Endpoint")
                                                 .setCtrl (HCA.createLinkedWebsite (aSelectedObject.getManageParticipantIdentifierEndpointAddress ()
                                                                                                   .toExternalForm ())));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Client certificate required?")
                                                 .setCtrl (EPhotonCoreText.getYesOrNo (aSelectedObject.isClientCertificateRequired (),
                                                                                       aDisplayLocale)));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("SMP API type").setCtrl (aSelectedObject.getSMPAPIType ().getDisplayName ()));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("SMP identifier type")
                                                 .setCtrl (aSelectedObject.getSMPIdentifierType ().getDisplayName ()));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Production SML?")
                                                 .setCtrl (EPhotonCoreText.getYesOrNo (aSelectedObject.isProduction (), aDisplayLocale)));

    aNodeList.addChild (aForm);
  }

  @Override
  protected void showInputForm (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nullable final ISMLConfiguration aSelectedObject,
                                @Nonnull final BootstrapForm aForm,
                                final boolean bFormSubmitted,
                                @Nonnull final EWebPageFormAction eFormAction,
                                @Nonnull final FormErrorList aFormErrors)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final boolean bEdit = eFormAction.isEdit ();

    aForm.addChild (getUIHandler ().createActionHeader (bEdit ? "Edit SML configuration '" + aSelectedObject.getDisplayName () + "'"
                                                              : "Create new SML configuration"));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("ID")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_ID,
                                                                                         aSelectedObject != null ? aSelectedObject.getID ()
                                                                                                                 : null)).setReadOnly (bEdit))
                                                 .setHelpText ("The internal ID of the SML configuration. This value cannot be edited.")
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_ID)));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Name")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_DISPLAY_NAME,
                                                                                         aSelectedObject != null ? aSelectedObject.getDisplayName ()
                                                                                                                 : null)))
                                                 .setHelpText ("The name of the SML configuration. This is for informational purposes only and has no effect on the functionality.")
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_DISPLAY_NAME)));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("DNS Zone")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_DNS_ZONE,
                                                                                         aSelectedObject != null ? aSelectedObject.getDNSZone ()
                                                                                                                 : null)))
                                                 .setHelpText (new HCTextNode ("The name of the DNS Zone that this SML is working upon (e.g. "),
                                                               new HCCode ().addChild ("sml.peppolcentral.org"),
                                                               new HCTextNode ("). The value will automatically converted to all-lowercase!"))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_DNS_ZONE)));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Management Service URL")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_MANAGEMENT_ADDRESS_URL,
                                                                                         aSelectedObject != null ? aSelectedObject.getManagementServiceURL ()
                                                                                                                 : null)))
                                                 .setHelpText ("The service URL where the SML management application is running on including the host name. It may not contain the '" +
                                                               CSMLDefault.MANAGEMENT_SERVICE_METADATA +
                                                               "' or '" +
                                                               CSMLDefault.MANAGEMENT_SERVICE_PARTICIPANTIDENTIFIER +
                                                               "' path elements!")
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_MANAGEMENT_ADDRESS_URL)));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Client Certificate required?")
                                                 .setCtrl (new HCCheckBox (new RequestFieldBoolean (FIELD_CLIENT_CERTIFICATE_REQUIRED,
                                                                                                    aSelectedObject != null ? aSelectedObject.isClientCertificateRequired ()
                                                                                                                            : DEFAULT_CLIENT_CERTIFICATE_REQUIRED)))
                                                 .setHelpText ("Check this if this SML requires a client certificate for access. Both Peppol production SML and SMK require a client certificate. Only a locally running SML software may not require a client certificate.")
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_CLIENT_CERTIFICATE_REQUIRED)));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP API type")
                                                 .setCtrl (new SMPAPITypeSelect (new RequestField (FIELD_SMP_API_TYPE,
                                                                                                   aSelectedObject != null ? aSelectedObject.getSMPAPIType ()
                                                                                                                                            .getID ()
                                                                                                                           : null),
                                                                                 aDisplayLocale))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_SMP_API_TYPE)));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP identifier type")
                                                 .setCtrl (new SMPIdentifierTypeSelect (new RequestField (FIELD_SMP_ID_TYPE,
                                                                                                          aSelectedObject != null ? aSelectedObject.getSMPIdentifierType ()
                                                                                                                                                   .getID ()
                                                                                                                                  : null),
                                                                                        aDisplayLocale))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_SMP_ID_TYPE)));

    aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Production SML?")
                                                 .setCtrl (new HCCheckBox (new RequestFieldBoolean (FIELD_PRODUCTION,
                                                                                                    aSelectedObject != null ? aSelectedObject.isProduction ()
                                                                                                                            : true)))
                                                 .setHelpText ("Check this if this SML is a production SML. Don't check e.g. for SMK.")
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_PRODUCTION)));
  }

  @Override
  protected void validateAndSaveInputParameters (@Nonnull final WebPageExecutionContext aWPEC,
                                                 @Nullable final ISMLConfiguration aSelectedObject,
                                                 @Nonnull final FormErrorList aFormErrors,
                                                 @Nonnull final EWebPageFormAction eFormAction)
  {
    final boolean bEdit = eFormAction.isEdit ();
    final ISMLConfigurationManager aSMLConfigurationMgr = PPMetaManager.getSMLConfigurationMgr ();

    final String sID = aWPEC.params ().getAsString (FIELD_ID);
    final String sDisplayName = aWPEC.params ().getAsString (FIELD_DISPLAY_NAME);
    final String sDNSZone = aWPEC.params ().getAsString (FIELD_DNS_ZONE);
    final String sManagementAddressURL = aWPEC.params ().getAsString (FIELD_MANAGEMENT_ADDRESS_URL);
    final boolean bClientCertificateRequired = aWPEC.params ()
                                                    .isCheckBoxChecked (FIELD_CLIENT_CERTIFICATE_REQUIRED,
                                                                        DEFAULT_CLIENT_CERTIFICATE_REQUIRED);
    final String sSMPAPIType = aWPEC.params ().getAsString (FIELD_SMP_API_TYPE);
    final ESMPAPIType eSMPAPIType = ESMPAPIType.getFromIDOrNull (sSMPAPIType);
    final String sSMPIdentifierType = aWPEC.params ().getAsString (FIELD_SMP_ID_TYPE);
    final ESMPIdentifierType eSMPIdentifierType = ESMPIdentifierType.getFromIDOrNull (sSMPIdentifierType);
    final boolean bProduction = aWPEC.params ().isCheckBoxChecked (FIELD_PRODUCTION, false);

    // validations
    if (StringHelper.hasNoText (sID))
      aFormErrors.addFieldError (FIELD_ID, "The SML configuration ID must not be empty!");
    else
      if (ISMLConfigurationManager.ID_AUTO_DETECT.equals (sID))
        aFormErrors.addFieldError (FIELD_ID, "This SML configuration ID is reserved!");
      else
      {
        final ISMLConfiguration aExisting = aSMLConfigurationMgr.getSMLInfoOfID (sID);
        if (bEdit)
        {
          // Expect aExistring == aSelectedObject
          if (aExisting == null)
            aFormErrors.addFieldError (FIELD_ID, "Invalid SML configuration ID provided!");
          else
            if (aExisting != aSelectedObject)
              aFormErrors.addFieldError (FIELD_ID, "Another SML configuration with the same ID already exists!");
        }
        else
        {
          if (aExisting != null)
            aFormErrors.addFieldError (FIELD_ID, "Another SML configuration with the same ID already exists!");
        }
      }

    if (StringHelper.hasNoText (sDisplayName))
      aFormErrors.addFieldError (FIELD_DISPLAY_NAME, "The SML configuration name must not be empty!");

    if (StringHelper.hasNoText (sDNSZone))
      aFormErrors.addFieldError (FIELD_DNS_ZONE, "The DNS Zone must not be empty!");

    if (StringHelper.hasNoText (sManagementAddressURL))
      aFormErrors.addFieldError (FIELD_MANAGEMENT_ADDRESS_URL, "The Management Address URL must not be empty!");
    else
    {
      final URL aURL = URLHelper.getAsURL (sManagementAddressURL);
      if (aURL == null)
        aFormErrors.addFieldError (FIELD_MANAGEMENT_ADDRESS_URL, "The Management Address URL is not a valid URL!");
      else
        if (!"https".equals (aURL.getProtocol ()) && !"http".equals (aURL.getProtocol ()))
          aFormErrors.addFieldError (FIELD_MANAGEMENT_ADDRESS_URL,
                                     "The Management Address URL should only be use the 'http' or the 'https' protocol!");
    }

    if (StringHelper.hasNoText (sSMPAPIType))
      aFormErrors.addFieldError (FIELD_SMP_API_TYPE, "An SMP API type must be selected!");
    else
      if (eSMPAPIType == null)
        aFormErrors.addFieldError (FIELD_SMP_API_TYPE, "A valid SMP API type must be selected!");

    if (StringHelper.hasNoText (sSMPIdentifierType))
      aFormErrors.addFieldError (FIELD_SMP_ID_TYPE, "An SMP identifier type must be selected!");
    else
      if (eSMPIdentifierType == null)
        aFormErrors.addFieldError (FIELD_SMP_ID_TYPE, "A valid SMP identifier type must be selected!");

    if (aFormErrors.isEmpty ())
    {
      // Lowercase with the US locale - not display locale specific
      final String sDNSZoneLC = sDNSZone.toLowerCase (Locale.US);

      if (bEdit)
      {
        aSMLConfigurationMgr.updateSMLInfo (aSelectedObject.getID (),
                                            sDisplayName,
                                            sDNSZoneLC,
                                            sManagementAddressURL,
                                            bClientCertificateRequired,
                                            eSMPAPIType,
                                            eSMPIdentifierType,
                                            bProduction);
        aWPEC.postRedirectGetInternal (success ("The SML configuration '" + sDisplayName + "' was successfully edited."));
      }
      else
      {
        aSMLConfigurationMgr.createSMLInfo (sID,
                                            sDisplayName,
                                            sDNSZoneLC,
                                            sManagementAddressURL,
                                            bClientCertificateRequired,
                                            eSMPAPIType,
                                            eSMPIdentifierType,
                                            bProduction);
        aWPEC.postRedirectGetInternal (success ("The new SML configuration '" + sDisplayName + "' was successfully created."));
      }
    }
  }

  @Override
  protected void showListOfExistingObjects (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final ISMLConfigurationManager aSMLConfigurationMgr = PPMetaManager.getSMLConfigurationMgr ();

    aNodeList.addChild (info ("This page lets you create custom SML configurations that can be used for registration."));

    final BootstrapButtonToolbar aToolbar = new BootstrapButtonToolbar (aWPEC);
    aToolbar.addButton ("Create new SML configuration", createCreateURL (aWPEC), EDefaultIcon.NEW);
    aNodeList.addChild (aToolbar);

    final HCTable aTable = new HCTable (new DTCol ("ID"),
                                        new DTCol ("Name").setInitialSorting (ESortOrder.ASCENDING),
                                        new DTCol ("DNS Zone"),
                                        new DTCol ("Management Service URL"),
                                        new DTCol ("Client Cert?"),
                                        new DTCol ("SMP API type"),
                                        new DTCol ("SMP ID type"),
                                        new DTCol ("Production?"),
                                        new BootstrapDTColAction (aDisplayLocale)).setID (getID ());
    for (final ISMLConfiguration aCurObject : aSMLConfigurationMgr.getAll ())
    {
      final ISimpleURL aViewLink = createViewURL (aWPEC, aCurObject);

      final HCRow aRow = aTable.addBodyRow ();
      aRow.addCell (aCurObject.getID ());
      aRow.addCell (new HCA (aViewLink).addChild (aCurObject.getDisplayName ()));
      aRow.addCell (aCurObject.getDNSZone ());
      aRow.addCell (aCurObject.getManagementServiceURL ());
      aRow.addCell (EPhotonCoreText.getYesOrNo (aCurObject.isClientCertificateRequired (), aDisplayLocale));
      aRow.addCell (aCurObject.getSMPAPIType ().getDisplayName ());
      aRow.addCell (aCurObject.getSMPIdentifierType ().getDisplayName ());
      aRow.addCell (EPhotonCoreText.getYesOrNo (aCurObject.isProduction (), aDisplayLocale));

      aRow.addCell (createEditLink (aWPEC, aCurObject, "Edit " + aCurObject.getID ()),
                    new HCTextNode (" "),
                    createCopyLink (aWPEC, aCurObject, "Copy " + aCurObject.getID ()),
                    new HCTextNode (" "),
                    isActionAllowed (aWPEC,
                                     EWebPageFormAction.DELETE,
                                     aCurObject) ? createDeleteLink (aWPEC, aCurObject, "Delete " + aCurObject.getDisplayName ())
                                                 : createEmptyAction ());
    }

    final DataTables aDataTables = BootstrapDataTables.createDefaultDataTables (aWPEC, aTable);
    aNodeList.addChild (aTable).addChild (aDataTables);
  }
}
