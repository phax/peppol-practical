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

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.CommonsLinkedHashSet;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.collection.ext.ICommonsOrderedSet;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.SimpleURL;
import com.helger.css.property.CCSSProperties;
import com.helger.css.propertyvalue.CCSSValue;
import com.helger.datetime.format.PDTToString;
import com.helger.html.hc.html.forms.EHCFormMethod;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCTextArea;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.html.grouping.IHCLI;
import com.helger.html.hc.html.sections.HCH3;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.html.textlevel.HCCode;
import com.helger.html.hc.html.textlevel.HCStrong;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.jquery.JQuery;
import com.helger.html.js.EJSEvent;
import com.helger.network.dns.IPV4Addr;
import com.helger.peppol.identifier.factory.IIdentifierFactory;
import com.helger.peppol.identifier.factory.PeppolIdentifierFactory;
import com.helger.peppol.identifier.generic.doctype.IDocumentTypeIdentifier;
import com.helger.peppol.identifier.generic.participant.IParticipantIdentifier;
import com.helger.peppol.identifier.peppol.PeppolIdentifierHelper;
import com.helger.peppol.identifier.peppol.participant.IPeppolParticipantIdentifier;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.smp.ESMPTransportProfile;
import com.helger.peppol.smp.EndpointType;
import com.helger.peppol.smp.ProcessType;
import com.helger.peppol.smp.ServiceGroupType;
import com.helger.peppol.smp.ServiceMetadataReferenceType;
import com.helger.peppol.smp.ServiceMetadataType;
import com.helger.peppol.smp.SignedServiceMetadataType;
import com.helger.peppol.smpclient.SMPClientReadOnly;
import com.helger.peppol.ui.IdentifierIssuingAgencySelect;
import com.helger.peppol.ui.SMLSelect;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.peppol.url.IPeppolURLProvider;
import com.helger.peppol.url.PeppolURLProvider;
import com.helger.peppol.utils.BusdoxURLHelper;
import com.helger.peppol.utils.W3CEndpointReferenceHelper;
import com.helger.photon.basic.audit.AuditHelper;
import com.helger.photon.bootstrap3.EBootstrapIcon;
import com.helger.photon.bootstrap3.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap3.alert.BootstrapInfoBox;
import com.helger.photon.bootstrap3.alert.BootstrapWarnBox;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.form.BootstrapForm;
import com.helger.photon.bootstrap3.form.BootstrapFormGroup;
import com.helger.photon.bootstrap3.grid.BootstrapRow;
import com.helger.photon.core.app.error.InternalErrorBuilder;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.security.certificate.CertificateHelper;

public class PagePublicToolsParticipantInformation extends AbstractAppWebPage
{
  public static final String FIELD_ID_ISO6523_PREDEF = "idschemepredef";
  public static final String FIELD_ID_ISO6523 = "idscheme";
  public static final String FIELD_ID_VALUE = "idvalue";
  public static final String FIELD_SML = "sml";

  public static final ISMLInfo DEFAULT_SML = ESML.DIGIT_PRODUCTION;
  private static final IPeppolURLProvider URL_PROVIDER = PeppolURLProvider.INSTANCE;

  public PagePublicToolsParticipantInformation (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Participant information");
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final FormErrorList aFormErrors = new FormErrorList ();
    final boolean bShowInput = true;
    final IIdentifierFactory aIF = PeppolIdentifierFactory.INSTANCE;

    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      // Validate fields
      final String sParticipantIDISO6523 = aWPEC.getAttributeAsString (FIELD_ID_ISO6523);
      final String sParticipantIDValue = aWPEC.getAttributeAsString (FIELD_ID_VALUE);
      final String sSML = aWPEC.getAttributeAsString (FIELD_SML);
      final ISMLInfo aSML = ESML.getFromIDOrNull (sSML);

      if (StringHelper.hasNoText (sParticipantIDISO6523))
        aFormErrors.addFieldError (FIELD_ID_ISO6523, "Please select an identifier scheme");

      if (StringHelper.hasNoText (sParticipantIDValue))
        aFormErrors.addFieldError (FIELD_ID_VALUE, "Please provide an identifier value");

      String sParticipantIdentifierValue = null;
      if (aFormErrors.isEmpty ())
      {
        sParticipantIdentifierValue = sParticipantIDISO6523 + ":" + sParticipantIDValue;
        if (!IPeppolParticipantIdentifier.isValidValue (sParticipantIdentifierValue))
          aFormErrors.addFieldError (FIELD_ID_VALUE,
                                     "The resulting participant identifier value '" +
                                                     sParticipantIdentifierValue +
                                                     "' is not valid!");
      }

      if (aSML == null)
        aFormErrors.addFieldError (FIELD_SML, "A valid SML must be selected!");

      if (aFormErrors.isEmpty ())
      {
        final IParticipantIdentifier aParticipantID = aIF.createParticipantIdentifierWithDefaultScheme (sParticipantIdentifierValue);
        final SMPClientReadOnly aSMPClient = new SMPClientReadOnly (URL_PROVIDER, aParticipantID, aSML);
        try
        {
          // URL always with a trailing slash
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

          final ICommonsList <IDocumentTypeIdentifier> aDocTypeIDs = new CommonsArrayList<> ();
          {
            aNodeList.addChild (new HCH3 ().addChild ("ServiceGroup contents"));

            final HCUL aUL = new HCUL ();

            final String sPathStart = "/" + aParticipantID.getURIEncoded () + "/services/";

            // Get all HRefs and sort them by decoded URL
            final ServiceGroupType aSG = aSMPClient.getServiceGroupOrNull (aParticipantID);
            // Map from cleaned URL to original URL
            final Map <String, String> aSGHrefs = new TreeMap<> ();
            if (aSG != null && aSG.getServiceMetadataReferenceCollection () != null)
              for (final ServiceMetadataReferenceType aSMR : aSG.getServiceMetadataReferenceCollection ()
                                                                .getServiceMetadataReference ())
              {
                final String sHref = BusdoxURLHelper.createPercentDecodedURL (aSMR.getHref ());
                if (aSGHrefs.put (sHref, aSMR.getHref ()) != null)
                  aUL.addItem (new BootstrapWarnBox ().addChild ("The ServiceGroup list contains the duplicate URL ")
                                                      .addChild (new HCCode ().addChild (sHref)));
              }

            // Show all ServiceGroup hrefs
            for (final Map.Entry <String, String> aEntry : aSGHrefs.entrySet ())
            {
              final String sHref = aEntry.getKey ();
              final String sOriginalHref = aEntry.getValue ();

              final IHCLI <?> aLI = aUL.addAndReturnItem (new HCDiv ().addChild (new HCCode ().addChild (sHref)));
              final int nPathStart = sHref.indexOf (sPathStart);
              if (nPathStart >= 0)
              {
                final String sDocType = sHref.substring (nPathStart + sPathStart.length ());
                try
                {
                  final IDocumentTypeIdentifier aDocType = aIF.parseDocumentTypeIdentifier (sDocType);
                  aDocTypeIDs.add (aDocType);
                  aLI.addChild (new HCDiv ().addChild (EBootstrapIcon.ARROW_RIGHT.getAsNode ())
                                            .addChild (" " + aDocType.getURIEncoded ()));
                  aLI.addChild (new HCDiv ().addChild (EBootstrapIcon.ARROW_RIGHT.getAsNode ())
                                            .addChild (" ")
                                            .addChild (new HCA (new SimpleURL (sOriginalHref)).addChild ("Open in browser")
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
              {
                aLI.addChild (new BootstrapErrorBox ().addChildren (new HCDiv ().addChild ("Contained href does not match the rules!"),
                                                                    new HCDiv ().addChild ("Found href: ")
                                                                                .addChild (new HCCode ().addChild (sHref)),
                                                                    new HCDiv ().addChild ("Expected path part: ")
                                                                                .addChild (new HCCode ().addChild (sPathStart))));
              }
            }
            if (!aUL.hasChildren ())
              aUL.addItem (new BootstrapWarnBox ().addChild ("No service group entries where found for " +
                                                             aParticipantID.getURIEncoded ()));
            aNodeList.addChild (aUL);
          }

          // List document type details
          if (!aDocTypeIDs.isEmpty ())
          {
            final LocalDate aNowDate = PDTFactory.getCurrentLocalDate ();
            final ICommonsOrderedSet <String> aAllUsedCertifiactes = new CommonsLinkedHashSet<> ();

            aNodeList.addChild (new HCH3 ().addChild ("Document type details"));
            final HCUL aULDocTypeIDs = new HCUL ();
            for (final IDocumentTypeIdentifier aDocTypeID : aDocTypeIDs.getSortedInline (IDocumentTypeIdentifier.comparator ()))
            {
              final IHCLI <?> aLIDocTypeID = aULDocTypeIDs.addAndReturnItem (new HCDiv ().addChild (new HCCode ().addChild (aDocTypeID.getURIEncoded ())));
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
                    final IHCLI <?> aLIProcessID = aULProcessID.addAndReturnItem (new HCDiv ().addChild ("Process ID: ")
                                                                                              .addChild (new HCCode ().addChild (aProcess.getProcessIdentifier ()
                                                                                                                                         .getURIEncoded ())));
                    final HCUL aULEndpoint = new HCUL ();
                    // For all endpoints of the process
                    for (final EndpointType aEndpoint : aProcess.getServiceEndpointList ().getEndpoint ())
                    {
                      // Endpoint URL
                      final IHCLI <?> aLIEndpoint = aULEndpoint.addAndReturnItem (new HCDiv ().addChild ("Endpoint URL: ")
                                                                                              .addChild (new HCCode ().addChild (W3CEndpointReferenceHelper.getAddress (aEndpoint.getEndpointReference ()))));

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
                                                                     PDTToString.getAsString (aValidTo,
                                                                                              aDisplayLocale)));
                        if (aValidTo.isBefore (aNowDate))
                          aLIEndpoint.addChild (new BootstrapErrorBox ().addChild ("This endpoint is no longer valid!"));
                      }

                      // Transport profile
                      final String sTransportProfile = aEndpoint.getTransportProfile ();
                      final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.getFromIDOrNull (sTransportProfile);
                      final String sShortName = eTransportProfile == null ? "unknown" : eTransportProfile.getName ();
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
              final IHCLI <?> aLICert = aULCerts.addItem ();
              final X509Certificate aCert = CertificateHelper.convertStringToCertficate (sCertificate);
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
              aLICert.addChild (new HCDiv ().addChild (new HCTextArea ().setReadOnly (true)
                                                                        .setRows (3)
                                                                        .setValue (sCertificate)
                                                                        .addStyle (CCSSProperties.FONT_FAMILY.newValue (CCSSValue.FONT_MONOSPACE))));
            }
            aNodeList.addChild (aULCerts);
          }

          // Audit success
          AuditHelper.onAuditExecuteSuccess ("participant-information", aParticipantID.getURIEncoded ());
        }
        catch (final UnknownHostException ex)
        {
          aNodeList.addChild (new BootstrapErrorBox ().addChild (new HCDiv ().addChild ("Seems like the participant ID " +
                                                                                        aParticipantID.getURIEncoded () +
                                                                                        " is not registered to the PEPPOL network."))
                                                      .addChild (new HCDiv ().addChild ("Technical details: unknown host " +
                                                                                        ex.getMessage ())));

          // Audit failure
          AuditHelper.onAuditExecuteFailure ("participant-information",
                                             aParticipantID.getURIEncoded (),
                                             "unknown-host",
                                             ex.getMessage ());
        }
        catch (final Exception ex)
        {
          new InternalErrorBuilder ().setRequestScope (aWPEC.getRequestScope ())
                                     .setDisplayLocale (aWPEC.getDisplayLocale ())
                                     .setThrowable (ex)
                                     .handle ();
          aNodeList.addChild (new BootstrapErrorBox ().addChild (new HCDiv ().addChild ("Error querying SMP."))
                                                      .addChild (new HCDiv ().addChild ("Technical details: " +
                                                                                        ex.getMessage ())));

          // Audit failure
          AuditHelper.onAuditExecuteFailure ("participant-information",
                                             aParticipantID.getURIEncoded (),
                                             ex.getClass (),
                                             ex.getMessage ());
        }
      }
    }

    if (bShowInput)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (getUIHandler ().createFormSelf (aWPEC)
                                                                              .setMethod (EHCFormMethod.GET)
                                                                              .setLeft (3));
      aForm.addChild (new BootstrapInfoBox ().addChildren (new HCDiv ().addChild ("Show all processes, document types and endpoints of a participant."),
                                                           new HCDiv ().addChild ("You may want to try scheme ")
                                                                       .addChild (new HCCode ().addChild ("9915"))
                                                                       .addChild ("and value ")
                                                                       .addChild (new HCCode ().addChild ("test"))
                                                                       .addChild (" as an example."),
                                                           new HCDiv ().addChild ("Note: this page currently only works with the ")
                                                                       .addChild (new HCCode ().addChild ("iso6523-actorid-upis"))
                                                                       .addChild (" meta scheme!")));
      {
        final IdentifierIssuingAgencySelect aSelect = new IdentifierIssuingAgencySelect (new RequestField (FIELD_ID_ISO6523_PREDEF),
                                                                                         aDisplayLocale);
        final HCEdit aEdit = new HCEdit (new RequestField (FIELD_ID_ISO6523)).setMaxLength (PeppolIdentifierHelper.MAX_PARTICIPANT_VALUE_LENGTH)
                                                                             .setPlaceholder ("Identifier value");
        // In case something is selected, put it in the edit
        aSelect.addEventHandler (EJSEvent.CHANGE, JQuery.idRef (aEdit).val (JQuery.jQueryThis ().val ()).change ());

        final BootstrapRow aRow = new BootstrapRow ();
        aRow.createColumn (4).addChild (aSelect);
        aRow.createColumn (8).addChild (aEdit);

        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier scheme")
                                                     .setCtrl (aRow)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_ID_ISO6523)));
      }
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier value")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_ID_VALUE)).setMaxLength (PeppolIdentifierHelper.MAX_PARTICIPANT_VALUE_LENGTH)
                                                                                                           .setPlaceholder ("Identifier value"))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_ID_VALUE)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML to use")
                                                   .setCtrl (new SMLSelect (new RequestField (FIELD_SML, DEFAULT_SML)))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_SML)));

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
      aToolbar.addSubmitButton ("Show details");
    }
  }
}
