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
package com.helger.peppol.page.pub;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;

import org.busdox.servicemetadata.publishing._1.EndpointType;
import org.busdox.servicemetadata.publishing._1.ProcessType;
import org.busdox.servicemetadata.publishing._1.ServiceGroupType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataReferenceType;
import org.busdox.servicemetadata.publishing._1.ServiceMetadataType;
import org.busdox.servicemetadata.publishing._1.SignedServiceMetadataType;
import org.joda.time.LocalDate;

import com.helger.appbasics.security.audit.AuditUtils;
import com.helger.bootstrap3.EBootstrapIcon;
import com.helger.bootstrap3.alert.BootstrapErrorBox;
import com.helger.bootstrap3.alert.BootstrapInfoBox;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.form.BootstrapCheckBox;
import com.helger.bootstrap3.form.BootstrapForm;
import com.helger.bootstrap3.form.BootstrapFormGroup;
import com.helger.bootstrap3.form.EBootstrapFormType;
import com.helger.bootstrap3.grid.BootstrapRow;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.string.StringHelper;
import com.helger.css.property.CCSSProperties;
import com.helger.css.propertyvalue.CCSSValue;
import com.helger.datetime.PDTFactory;
import com.helger.datetime.format.PDTToString;
import com.helger.html.hc.CHCParam;
import com.helger.html.hc.api.EHCFormMethod;
import com.helger.html.hc.html.HCA;
import com.helger.html.hc.html.HCCode;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCH3;
import com.helger.html.hc.html.HCLI;
import com.helger.html.hc.html.HCStrong;
import com.helger.html.hc.html.HCTextArea;
import com.helger.html.hc.html.HCUL;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.js.EJSEvent;
import com.helger.html.js.builder.jquery.JQuery;
import com.helger.peppol.identifier.CIdentifier;
import com.helger.peppol.identifier.IdentifierUtils;
import com.helger.peppol.identifier.doctype.SimpleDocumentTypeIdentifier;
import com.helger.peppol.identifier.participant.SimpleParticipantIdentifier;
import com.helger.peppol.page.ui.IdentifierIssuingAgencySelect;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.smp.ESMPTransportProfile;
import com.helger.peppol.smpclient.SMPClientReadonly;
import com.helger.peppol.utils.BusdoxURLUtils;
import com.helger.peppol.utils.CertificateUtils;
import com.helger.peppol.utils.W3CEndpointReferenceUtils;
import com.helger.validation.error.FormErrors;
import com.helger.web.dns.IPV4Addr;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webbasics.form.RequestFieldBoolean;
import com.helger.webctrls.page.AbstractWebPageExt;

public class PagePublicToolsParticipantInformation extends AbstractWebPageExt <WebPageExecutionContext>
{
  public static final String FIELD_ID_ISO6523_PREDEF = "idschemepredef";
  public static final String FIELD_ID_ISO6523 = "idscheme";
  public static final String FIELD_ID_VALUE = "idvalue";
  public static final String FIELD_USE_SML = "usesml";

  public static final boolean DEFAULT_USE_SML = true;

  public PagePublicToolsParticipantInformation (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Participant information");
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final FormErrors aFormErrors = new FormErrors ();
    final boolean bShowInput = true;

    if (aWPEC.hasAction (ACTION_PERFORM))
    {
      // Validate fields
      final String sParticipantIDISO6523 = aWPEC.getAttributeAsString (FIELD_ID_ISO6523);
      final String sParticipantIDValue = aWPEC.getAttributeAsString (FIELD_ID_VALUE);
      final boolean bUseSML = aWPEC.getCheckBoxAttr (FIELD_USE_SML, DEFAULT_USE_SML);

      if (StringHelper.hasNoText (sParticipantIDISO6523))
        aFormErrors.addFieldError (FIELD_ID_ISO6523, "Please select an identifier scheme");

      if (StringHelper.hasNoText (sParticipantIDValue))
        aFormErrors.addFieldError (FIELD_ID_VALUE, "Please provide an identifier value");

      String sParticipantIdentifierValue = null;
      if (aFormErrors.isEmpty ())
      {
        sParticipantIdentifierValue = sParticipantIDISO6523 + ":" + sParticipantIDValue;
        if (!IdentifierUtils.isValidParticipantIdentifierValue (sParticipantIdentifierValue))
          aFormErrors.addFieldError (FIELD_ID_VALUE, "The resulting participant identifier value '" +
                                                     sParticipantIdentifierValue +
                                                     "' is not valid!");
      }

      if (aFormErrors.isEmpty ())
      {
        final SimpleParticipantIdentifier aParticipantID = SimpleParticipantIdentifier.createWithDefaultScheme (sParticipantIdentifierValue);
        final SMPClientReadonly aSMPClient = new SMPClientReadonly (aParticipantID, bUseSML ? ESML.PRODUCTION
                                                                                           : ESML.TEST);
        try
        {
          final URL aSMPHost = new URL (aSMPClient.getSMPHostURI ());
          final InetAddress aInetAddress = InetAddress.getByName (aSMPHost.getHost ());
          final InetAddress aNice = InetAddress.getByAddress (aInetAddress.getAddress ());
          aNodeList.addChild (new HCDiv ().addChild ("Querying the following SMP for ")
                                          .addChild (new HCCode ().addChild (aParticipantID.getURIEncoded ()))
                                          .addChild (":"));
          aNodeList.addChild (new HCDiv ().addChild ("PEPPOL name: ")
                                          .addChild (new HCCode ().addChild (aSMPHost.toExternalForm ())));
          aNodeList.addChild (new HCDiv ().addChild ("Nice name: ")
                                          .addChild (new HCCode ().addChild (aNice.getCanonicalHostName ())));
          aNodeList.addChild (new HCDiv ().addChild ("IP address: ")
                                          .addChild (new HCCode ().addChild (new IPV4Addr (aInetAddress).getAsString ())));

          final List <SimpleDocumentTypeIdentifier> aDocTypeIDs = new ArrayList <> ();
          {
            aNodeList.addChild (new HCH3 ().addChild ("ServiceGroup contents"));
            // Check with lowercase host name as well (e.g. 9908:983974724)!
            // The generated hostname contains "B-" whereas the returned
            // hostname contains "b-"
            final String sCommonPrefix = (aSMPHost.toExternalForm () + aParticipantID.getURIEncoded () + "/services/").toLowerCase (Locale.US);

            final ServiceGroupType aSG = aSMPClient.getServiceGroupOrNull (aParticipantID);
            final HCUL aUL = new HCUL ();
            for (final ServiceMetadataReferenceType aSMR : aSG.getServiceMetadataReferenceCollection ()
                                                              .getServiceMetadataReference ())
            {
              final String sHref = BusdoxURLUtils.createPercentDecodedURL (aSMR.getHref ());
              final HCLI aLI = aUL.addAndReturnItem (new HCDiv ().addChild (new HCCode ().addChild (sHref)));
              if (sHref.toLowerCase (Locale.US).startsWith (sCommonPrefix))
              {
                final String sDocType = sHref.substring (sCommonPrefix.length ());
                try
                {
                  final SimpleDocumentTypeIdentifier aDocType = SimpleDocumentTypeIdentifier.createFromURIPart (sDocType);
                  aDocTypeIDs.add (aDocType);
                  aLI.addChild (new HCDiv ().addChild (EBootstrapIcon.ARROW_RIGHT.getAsNode ())
                                            .addChild (" " + aDocType.getURIEncoded ()));
                  aLI.addChild (new HCDiv ().addChild (EBootstrapIcon.ARROW_RIGHT.getAsNode ())
                                            .addChild (new HCA (aSMR.getHref ()).addChild ("Open in browser")
                                                                                .setTargetBlank ()));
                }
                catch (final IllegalArgumentException ex)
                {
                  aLI.addChild (new BootstrapErrorBox ().addChild ("The document type ")
                                                        .addChild (new HCCode ().addChild (sDocType))
                                                        .addChild (" could not be interpreted as a PEPPOL document type!"));
                }
              }
              else
                aLI.addChild (new BootstrapErrorBox ().addChildren (new HCDiv ().addChild ("Contained href does not match the rules!"),
                                                                    new HCDiv ().addChild ("Found href: ")
                                                                                .addChild (new HCCode ().addChild (sHref)),
                                                                    new HCDiv ().addChild ("Expected prefix: ")
                                                                                .addChild (new HCCode ().addChild (sCommonPrefix))));
            }
            aNodeList.addChild (aUL);
          }

          // List document type details
          if (!aDocTypeIDs.isEmpty ())
          {
            final LocalDate aNowDate = PDTFactory.getCurrentLocalDate ();
            final Set <String> aAllUsedCertifiactes = new LinkedHashSet <String> ();

            aNodeList.addChild (new HCH3 ().addChild ("Document type details"));
            final HCUL aULDocTypeIDs = new HCUL ();
            for (final SimpleDocumentTypeIdentifier aDocTypeID : aDocTypeIDs)
            {
              final HCLI aLIDocTypeID = aULDocTypeIDs.addAndReturnItem (new HCDiv ().addChild (new HCCode ().addChild (aDocTypeID.getURIEncoded ())));
              final SignedServiceMetadataType aSSM = aSMPClient.getServiceRegistrationOrNull (aParticipantID,
                                                                                              aDocTypeID);
              if (aSSM != null)
              {
                final ServiceMetadataType aSM = aSSM.getServiceMetadata ();
                if (aSM.getRedirect () != null)
                  aLIDocTypeID.addChild (new HCDiv ().addChild ("Redirect to " + aSM.getRedirect ().getHref ()));
                else
                {
                  // For all processes
                  final HCUL aULProcessID = new HCUL ();
                  for (final ProcessType aProcess : aSM.getServiceInformation ().getProcessList ().getProcess ())
                  {
                    final HCLI aLIProcessID = aULProcessID.addAndReturnItem (new HCDiv ().addChild ("Process ID: ")
                                                                                         .addChild (new HCCode ().addChild (IdentifierUtils.getIdentifierURIEncoded (aProcess.getProcessIdentifier ()))));
                    final HCUL aULEndpoint = new HCUL ();
                    // For all endpoints of the process
                    for (final EndpointType aEndpoint : aProcess.getServiceEndpointList ().getEndpoint ())
                    {
                      // Endpoint URL
                      final HCLI aLIEndpoint = aULEndpoint.addAndReturnItem (new HCDiv ().addChild ("Endpoint URL: ")
                                                                                         .addChild (new HCCode ().addChild (W3CEndpointReferenceUtils.getAddress (aEndpoint.getEndpointReference ()))));

                      // Valid from
                      if (aEndpoint.getServiceActivationDate () != null)
                      {
                        final LocalDate aValidFrom = PDTFactory.createLocalDate (aEndpoint.getServiceActivationDate ());
                        aLIEndpoint.addChild (new HCDiv ().addChild ("Valid from: " +
                                                                     PDTToString.getAsString (aValidFrom,
                                                                                              aDisplayLocale)));
                        if (aValidFrom.isAfter (aNowDate))
                          aLIEndpoint.addChild (new BootstrapErrorBox ().addChild ("This endpoint is not yet valid!"));
                      }

                      // Valid to
                      if (aEndpoint.getServiceExpirationDate () != null)
                      {
                        final LocalDate aValidTo = PDTFactory.createLocalDate (aEndpoint.getServiceExpirationDate ());
                        aLIEndpoint.addChild (new HCDiv ().addChild ("Valid to: " +
                                                                     PDTToString.getAsString (aValidTo, aDisplayLocale)));
                        if (aValidTo.isBefore (aNowDate))
                          aLIEndpoint.addChild (new BootstrapErrorBox ().addChild ("This endpoint is no longer valid!"));
                      }

                      // Transport profile
                      final String sTransportProfile = aEndpoint.getTransportProfile ();
                      final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.getFromIDOrNull (sTransportProfile);
                      String sShortName = "unknown";
                      if (eTransportProfile == ESMPTransportProfile.TRANSPORT_PROFILE_START)
                        sShortName = "START";
                      else
                        if (eTransportProfile == ESMPTransportProfile.TRANSPORT_PROFILE_AS2)
                          sShortName = "AS2";
                      aLIEndpoint.addChild (new HCDiv ().addChild ("Transport profile: " + sTransportProfile + " (")
                                                        .addChild (new HCStrong ().addChild (sShortName))
                                                        .addChild (")"));

                      // Technical infos
                      final String sTecInfo = StringHelper.getImplodedNonEmpty (" / ",
                                                                                aEndpoint.getTechnicalInformationUrl (),
                                                                                aEndpoint.getTechnicalContactUrl ());
                      if (StringHelper.hasText (sTecInfo))
                        aLIEndpoint.addChild (new HCDiv ().addChild ("Technical info: " + sTecInfo));

                      // Certificate
                      aAllUsedCertifiactes.add (aEndpoint.getCertificate ());
                    }
                    aLIProcessID.addChild (aULEndpoint);
                  }
                  aLIDocTypeID.addChild (aULProcessID);
                }
              }
              else
              {
                aLIDocTypeID.addChild (new BootstrapErrorBox ().addChild ("Failed to get service registration"));
              }
            }
            aNodeList.addChild (aULDocTypeIDs);

            aNodeList.addChild (new HCH3 ().addChild ("Certificate details"));
            final HCUL aULCerts = new HCUL ();
            for (final String sCertificate : aAllUsedCertifiactes)
            {
              final HCLI aLICert = aULCerts.addItem ();
              final X509Certificate aCert = CertificateUtils.convertStringToCertficate (sCertificate);
              if (aCert != null)
              {
                aLICert.addChild (new HCDiv ().addChild ("Issuer: " + aCert.getIssuerDN ().toString ()));
                aLICert.addChild (new HCDiv ().addChild ("Subject: " + aCert.getSubjectDN ().toString ()));
                final LocalDate aNotBefore = PDTFactory.createLocalDate (aCert.getNotBefore ());
                aLICert.addChild (new HCDiv ().addChild ("Not before: " +
                                                         PDTToString.getAsString (aNotBefore, aDisplayLocale)));
                if (aNotBefore.isAfter (aNowDate))
                  aLICert.addChild (new BootstrapErrorBox ().addChild ("This certificate is not yet valid!"));
                final LocalDate aNotAfter = PDTFactory.createLocalDate (aCert.getNotAfter ());
                aLICert.addChild (new HCDiv ().addChild ("Not after: " +
                                                         PDTToString.getAsString (aNotAfter, aDisplayLocale)));
                if (aNotAfter.isBefore (aNowDate))
                  aLICert.addChild (new BootstrapErrorBox ().addChild ("This certificate is no longer valid!"));
              }
              else
                aLICert.addChild (new BootstrapErrorBox ().addChild ("Failed to interpret the data as a X509 certificate"));
              aLICert.addChild (new HCDiv ().addChild (new HCTextArea ().setReadonly (true)
                                                                        .setRows (3)
                                                                        .setValue (sCertificate)
                                                                        .addStyle (CCSSProperties.FONT_FAMILY.newValue (CCSSValue.FONT_MONOSPACE))));
            }
            aNodeList.addChild (aULCerts);
          }

          // Audit success
          AuditUtils.onAuditExecuteSuccess ("participate-information", aParticipantID.getURIEncoded ());
        }
        catch (final UnknownHostException ex)
        {
          aNodeList.addChild (new BootstrapErrorBox ().addChild (new HCDiv ().addChild ("Seems like the participant ID " +
                                                                                        aParticipantID.getURIEncoded () +
                                                                                        " is not registered to the PEPPOL network."))
                                                      .addChild (new HCDiv ().addChild ("Technical details: unknown host " +
                                                                                        ex.getMessage ())));

          // Audit failure
          AuditUtils.onAuditExecuteFailure ("participate-information",
                                            aParticipantID.getURIEncoded (),
                                            "unknown-host",
                                            ex.getMessage ());
        }
        catch (final Exception ex)
        {
          aNodeList.addChild (new BootstrapErrorBox ().addChild (new HCDiv ().addChild ("Error querying SMP."))
                                                      .addChild (new HCDiv ().addChild ("Technical details: " +
                                                                                        ex.getMessage ())));

          // Audit failure
          AuditUtils.onAuditExecuteFailure ("participate-information",
                                            aParticipantID.getURIEncoded (),
                                            ex.getClass (),
                                            ex.getMessage ());
        }
      }
    }

    if (bShowInput)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (new BootstrapForm (EBootstrapFormType.HORIZONTAL).setAction (aWPEC.getSelfHref ())
                                                                                                                .setMethod (EHCFormMethod.GET)
                                                                                                                .setLeft (3));
      aForm.addChild (new BootstrapInfoBox ().addChildren (new HCDiv ().addChild ("Show all processes, document types and endpoints of a participant."),
                                                           new HCDiv ().addChild ("You may want to try scheme ")
                                                                       .addChild (new HCCode ().addChild ("9915"))
                                                                       .addChild ("and value ")
                                                                       .addChild (new HCCode ().addChild ("test"))
                                                                       .addChild (" as an example.")));
      {
        final IdentifierIssuingAgencySelect aSelect = new IdentifierIssuingAgencySelect (new RequestField (FIELD_ID_ISO6523_PREDEF),
                                                                                         aDisplayLocale);
        final HCEdit aEdit = new HCEdit (new RequestField (FIELD_ID_ISO6523)).setMaxLength (CIdentifier.MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH)
                                                                             .setPlaceholder ("Identifier value");
        // In case something is selected, put it in the edit
        aSelect.setEventHandler (EJSEvent.ONCHANGE, JQuery.idRef (aEdit).val (JQuery.jQueryThis ().val ()));

        final BootstrapRow aRow = new BootstrapRow ();
        aRow.createColumn (6).addChild (aSelect);
        aRow.createColumn (6).addChild (aEdit);

        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier scheme")
                                                     .setCtrl (aRow)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_ID_ISO6523)));
      }
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier value")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_ID_VALUE)).setMaxLength (CIdentifier.MAX_PARTICIPANT_IDENTIFIER_VALUE_LENGTH)
                                                                                                           .setPlaceholder ("Identifier value"))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_ID_VALUE)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Use the SML?")
                                                   .setCtrl (new BootstrapCheckBox (new RequestFieldBoolean (FIELD_USE_SML,
                                                                                                             DEFAULT_USE_SML)))
                                                   .setHelpText ("Use the SML or alternatively the SMK?")
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_ID_VALUE)));

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CHCParam.PARAM_ACTION, ACTION_PERFORM);
      aToolbar.addSubmitButton ("Show details");
    }
  }
}
