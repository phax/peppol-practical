/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashSet;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.collection.impl.ICommonsSortedMap;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.datetime.PDTToString;
import com.helger.commons.email.EmailAddressHelper;
import com.helger.commons.locale.country.CountryCache;
import com.helger.commons.locale.language.LanguageCache;
import com.helger.commons.state.ETriState;
import com.helger.commons.string.StringHelper;
import com.helger.commons.timing.StopWatch;
import com.helger.commons.url.SimpleURL;
import com.helger.css.property.CCSSProperties;
import com.helger.css.propertyvalue.CCSSValue;
import com.helger.dns.ip.IPV4Addr;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.ext.HCA_MailTo;
import com.helger.html.hc.ext.HCExtHelper;
import com.helger.html.hc.html.forms.EHCFormMethod;
import com.helger.html.hc.html.forms.HCCheckBox;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCTextArea;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.grouping.HCHR;
import com.helger.html.hc.html.grouping.HCLI;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.html.grouping.IHCLI;
import com.helger.html.hc.html.sections.HCH3;
import com.helger.html.hc.html.sections.HCH4;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.httpclient.HttpClientManager;
import com.helger.httpclient.response.ResponseHandlerByteArray;
import com.helger.pd.businesscard.generic.PDBusinessCard;
import com.helger.pd.businesscard.generic.PDBusinessEntity;
import com.helger.pd.businesscard.generic.PDContact;
import com.helger.pd.businesscard.generic.PDIdentifier;
import com.helger.pd.businesscard.generic.PDName;
import com.helger.pd.businesscard.helper.PDBusinessCardHelper;
import com.helger.peppol.app.mgr.ISMLConfigurationManager;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.domain.ISMLConfiguration;
import com.helger.peppol.domain.SMPQueryParams;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.smp.ESMPTransportProfile;
import com.helger.peppol.ui.AppCommonUI;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.peppol.ui.select.SMLConfigurationSelect;
import com.helger.peppol.utils.EPeppolCertificateCheckResult;
import com.helger.peppol.utils.PeppolCertificateChecker;
import com.helger.peppolid.CIdentifier;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.peppolid.simple.process.SimpleProcessIdentifier;
import com.helger.photon.audit.AuditHelper;
import com.helger.photon.bootstrap4.button.BootstrapLinkButton;
import com.helger.photon.bootstrap4.button.EBootstrapButtonSize;
import com.helger.photon.bootstrap4.button.EBootstrapButtonType;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.form.BootstrapFormHelper;
import com.helger.photon.bootstrap4.table.BootstrapTable;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.form.RequestFieldBoolean;
import com.helger.photon.core.interror.InternalErrorBuilder;
import com.helger.photon.icon.fontawesome.EFontAwesome4Icon;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.famfam.EFamFamFlagIcon;
import com.helger.photon.uictrls.prism.EPrismLanguage;
import com.helger.photon.uictrls.prism.HCPrismJS;
import com.helger.security.certificate.CertificateHelper;
import com.helger.smpclient.bdxr1.BDXRClientReadOnly;
import com.helger.smpclient.exception.SMPClientBadResponseException;
import com.helger.smpclient.exception.SMPClientException;
import com.helger.smpclient.peppol.SMPClientReadOnly;
import com.helger.smpclient.peppol.utils.W3CEndpointReferenceHelper;
import com.helger.smpclient.url.SMPDNSResolutionException;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

public class PagePublicToolsParticipantInformation extends AbstractAppWebPage
{
  public static final String DEFAULT_ID_SCHEME = PeppolIdentifierHelper.DEFAULT_PARTICIPANT_SCHEME;
  public static final String FIELD_ID_SCHEME = "scheme";
  public static final String FIELD_ID_VALUE = "value";
  public static final String FIELD_SML = "sml";
  public static final String PARAM_QUERY_BUSINESS_CARD = "querybc";
  public static final String PARAM_SHOW_TIME = "showtime";
  public static final String PARAM_XSD_VALIDATION = "xsdvalidation";
  public static final String PARAM_VERIFY_SIGNATURES = "verifysignatures";

  private static final boolean DEFAULT_QUERY_BUSINESS_CARD = true;
  private static final boolean DEFAULT_SHOW_TIME = false;
  private static final boolean DEFAULT_XSD_VALIDATION = true;
  private static final boolean DEFAULT_VERIFY_SIGNATURES = true;
  private static final Logger LOGGER = LoggerFactory.getLogger (PagePublicToolsParticipantInformation.class);

  public PagePublicToolsParticipantInformation (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Participant information");
  }

  @Nonnull
  private static BootstrapLinkButton _createOpenInBrowser (@Nonnull final String sURL)
  {
    return _createOpenInBrowser (sURL, "Open in browser");
  }

  @Nonnull
  private static BootstrapLinkButton _createOpenInBrowser (@Nonnull final String sURL, @Nonnull final String sLabel)
  {
    return new BootstrapLinkButton (EBootstrapButtonSize.SMALL).setButtonType (EBootstrapButtonType.OUTLINE_INFO)
                                                               .setHref (new SimpleURL (sURL))
                                                               .addChild (sLabel)
                                                               .setTargetBlank ();
  }

  @Nonnull
  private IHCNode _createTimingNode (final long nMillis)
  {
    return badgeInfo ("took " + nMillis + " milliseconds");
  }

  private void _printEndpointURL (@Nonnull final IHCLI <?> aLIEndpoint, final String sEndpointRef)
  {
    aLIEndpoint.addChild (div ("Endpoint URL: ").addChild (StringHelper.hasNoText (sEndpointRef) ? em ("none") : code (sEndpointRef)));
  }

  private void _printActivationDate (@Nonnull final IHCLI <?> aLIEndpoint,
                                     final LocalDateTime aServiceActivationDate,
                                     final Locale aDisplayLocale)
  {
    if (aServiceActivationDate != null)
    {
      final LocalDate aNowDate = PDTFactory.getCurrentLocalDate ();
      final LocalDate aValidFrom = aServiceActivationDate.toLocalDate ();
      aLIEndpoint.addChild (div ("Valid from: " + PDTToString.getAsString (aValidFrom, aDisplayLocale)));
      if (aValidFrom.isAfter (aNowDate))
        aLIEndpoint.addChild (error ("This endpoint is not yet valid!"));
    }
  }

  private void _printExpirationDate (@Nonnull final IHCLI <?> aLIEndpoint,
                                     final LocalDateTime aServiceExpirationDate,
                                     final Locale aDisplayLocale)
  {
    if (aServiceExpirationDate != null)
    {
      final LocalDate aNowDate = PDTFactory.getCurrentLocalDate ();
      final LocalDate aValidTo = PDTFactory.createLocalDate (aServiceExpirationDate);
      aLIEndpoint.addChild (div ("Valid to: " + PDTToString.getAsString (aValidTo, aDisplayLocale)));
      if (aValidTo.isBefore (aNowDate))
        aLIEndpoint.addChild (error ("This endpoint is no longer valid!"));
    }
  }

  private void _printTransportProfile (@Nonnull final IHCLI <?> aLIEndpoint, @Nullable final String sTransportProfile)
  {
    final HCDiv aDiv = div ("Transport profile: ");
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.getFromIDOrNull (sTransportProfile);
    if (eTransportProfile != null)
    {
      // Known transport profile
      aDiv.addChild (badgeSuccess (eTransportProfile.getName ()))
          .addChild (small (" (").addChild (code (sTransportProfile)).addChild (")"));
    }
    else
    {
      aDiv.addChild (code (sTransportProfile)).addChild (" ").addChild (badgeWarn ("non-standard"));
    }
    aLIEndpoint.addChild (aDiv);
  }

  private void _printTecInfo (@Nonnull final IHCLI <?> aLIEndpoint, final String sTecInfo)
  {
    if (StringHelper.hasText (sTecInfo))
    {
      final boolean bIsEmail = EmailAddressHelper.isValid (sTecInfo);
      aLIEndpoint.addChild (div ("Technical info: ").addChild (bIsEmail ? HCA_MailTo.createLinkedEmail (sTecInfo)
                                                                        : new HCTextNode (sTecInfo)));
    }
  }

  private void _queryParticipant (@Nonnull final WebPageExecutionContext aWPEC,
                                  final String sParticipantIDScheme,
                                  final String sParticipantIDValue,
                                  final ISMLConfiguration aSMLConfiguration,
                                  final boolean bSMLAutoDetect,
                                  final boolean bQueryBusinessCard,
                                  final boolean bShowTime,
                                  final boolean bXSDValidation,
                                  final boolean bVerifySignatures)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aWPEC.getRequestScope ();
    final ISMLConfigurationManager aSMLConfigurationMgr = PPMetaManager.getSMLConfigurationMgr ();

    final String sParticipantIDUriEncoded = CIdentifier.getURIEncoded (sParticipantIDScheme, sParticipantIDValue);

    LOGGER.info ("Start querying the Participant information of '" + sParticipantIDUriEncoded + "'");

    // Try to print the basic information before an error occurs
    aNodeList.addChild (div ("Querying the following SMP for ").addChild (code (sParticipantIDUriEncoded)).addChild (":"));

    try
    {
      SMPQueryParams aQueryParams = null;
      ISMLConfiguration aRealSMLConfiguration = aSMLConfiguration;
      if (bSMLAutoDetect)
      {
        for (final ISMLConfiguration aCurSML : aSMLConfigurationMgr.getAllSorted ())
        {
          aQueryParams = SMPQueryParams.createForSML (aCurSML, sParticipantIDScheme, sParticipantIDValue);
          if (aQueryParams == null)
            continue;
          try
          {
            InetAddress.getByName (aQueryParams.getSMPHostURI ().getHost ());
            // Found it
            aRealSMLConfiguration = aCurSML;
            break;
          }
          catch (final UnknownHostException ex)
          {
            // continue
          }
        }

        // Ensure to go into the exception handler
        if (aRealSMLConfiguration == null)
          throw new UnknownHostException ("");
      }
      else
      {
        // SML configuration is not null
        aQueryParams = SMPQueryParams.createForSML (aRealSMLConfiguration, sParticipantIDScheme, sParticipantIDValue);
      }

      if (aQueryParams == null)
        throw new SMPDNSResolutionException ("Failed to resolve participant ID '" +
                                             sParticipantIDUriEncoded +
                                             "' for the provided SML '" +
                                             aRealSMLConfiguration.getID () +
                                             "'");

      final IParticipantIdentifier aParticipantID = aQueryParams.getParticipantID ();

      LOGGER.info ("Participant information of '" +
                   sParticipantIDUriEncoded +
                   "' is queried using SMP API '" +
                   aQueryParams.getSMPAPIType () +
                   "' from '" +
                   aQueryParams.getSMPHostURI () +
                   "' using SML '" +
                   aRealSMLConfiguration +
                   "'; XSD validation=" +
                   bXSDValidation +
                   "; verify signatures=" +
                   bVerifySignatures);
      final URL aSMPHost = aQueryParams.getSMPHostURI ().toURL ();

      {
        final HCUL aUL = new HCUL ();
        aUL.addItem (div ("SML used: ").addChild (code (aRealSMLConfiguration.getDisplayName () +
                                                        " / " +
                                                        aRealSMLConfiguration.getDNSZone ()))
                                       .addChild (" ")
                                       .addChild (aRealSMLConfiguration.isProduction () ? badgeSuccess ("production SML")
                                                                                        : badgeWarn ("test SML")));

        final String sURL1 = aSMPHost.toExternalForm ();
        aUL.addItem (div ("Peppol name: ").addChild (code (sURL1)), div (_createOpenInBrowser (sURL1)));

        final InetAddress [] aInetAddresses = InetAddress.getAllByName (aSMPHost.getHost ());
        for (final InetAddress aInetAddress : aInetAddresses)
        {
          final String sURL2 = new IPV4Addr (aInetAddress).getAsString ();
          final InetAddress aNice = InetAddress.getByAddress (aInetAddress.getAddress ());
          final String sURL3 = aNice.getCanonicalHostName ();

          aUL.addItem (div ("IP address: ").addChild (code (sURL2)).addChild (" - reverse lookup: ").addChild (code (sURL3)),
                       div (_createOpenInBrowser ("http://" + sURL2,
                                                  "Open IP in browser")).addChild (" ")
                                                                        .addChild (_createOpenInBrowser ("http://" + sURL3,
                                                                                                         "Open name in browser")));
        }

        final String sURL4 = sURL1 + "/" + sParticipantIDUriEncoded;
        aUL.addItem (div ("Query base URL: ").addChild (code (sURL4)), div (_createOpenInBrowser (sURL4)));

        if (!bXSDValidation)
          aUL.addItem (badgeWarn ("XML Schema validation of SMP responses is disabled."));
        if (!bVerifySignatures)
          aUL.addItem (badgeDanger ("Signature verification of SMP responses is disabled."));

        aNodeList.addChild (aUL);
      }

      // Determine all document types
      final ICommonsList <IDocumentTypeIdentifier> aDocTypeIDs = new CommonsArrayList <> ();
      SMPClientReadOnly aSMPClient = null;
      BDXRClientReadOnly aBDXR1Client = null;

      {
        final StopWatch aSWGetDocTypes = StopWatch.createdStarted ();
        final HCUL aUL = new HCUL ();
        final ICommonsSortedMap <String, String> aSGHrefs = new CommonsTreeMap <> ();

        switch (aQueryParams.getSMPAPIType ())
        {
          case PEPPOL:
          {
            aSMPClient = new SMPClientReadOnly (aQueryParams.getSMPHostURI ());
            aSMPClient.setXMLSchemaValidation (bXSDValidation);
            aSMPClient.setVerifySignature (bVerifySignatures);

            // Get all HRefs and sort them by decoded URL
            final com.helger.smpclient.peppol.jaxb.ServiceGroupType aSG = aSMPClient.getServiceGroupOrNull (aParticipantID);
            // Map from cleaned URL to original URL
            if (aSG != null && aSG.getServiceMetadataReferenceCollection () != null)
              for (final com.helger.smpclient.peppol.jaxb.ServiceMetadataReferenceType aSMR : aSG.getServiceMetadataReferenceCollection ()
                                                                                                 .getServiceMetadataReference ())
              {
                // Decoded href is important for unification
                final String sHref = CIdentifier.createPercentDecoded (aSMR.getHref ());
                if (aSGHrefs.put (sHref, aSMR.getHref ()) != null)
                  aUL.addItem (warn ("The ServiceGroup list contains the duplicate URL ").addChild (code (sHref)));
              }
            break;
          }
          case OASIS_BDXR_V1:
          {
            aBDXR1Client = new BDXRClientReadOnly (aQueryParams.getSMPHostURI ());
            aBDXR1Client.setXMLSchemaValidation (bXSDValidation);
            aBDXR1Client.setVerifySignature (bVerifySignatures);

            // Get all HRefs and sort them by decoded URL
            final com.helger.xsds.bdxr.smp1.ServiceGroupType aSG = aBDXR1Client.getServiceGroupOrNull (aParticipantID);
            // Map from cleaned URL to original URL
            if (aSG != null && aSG.getServiceMetadataReferenceCollection () != null)
              for (final com.helger.xsds.bdxr.smp1.ServiceMetadataReferenceType aSMR : aSG.getServiceMetadataReferenceCollection ()
                                                                                          .getServiceMetadataReference ())
              {
                // Decoded href is important for unification
                final String sHref = CIdentifier.createPercentDecoded (aSMR.getHref ());
                if (aSGHrefs.put (sHref, aSMR.getHref ()) != null)
                  aUL.addItem (warn ("The ServiceGroup list contains the duplicate URL ").addChild (code (sHref)));
              }
            break;
          }
        }

        aSWGetDocTypes.stop ();

        LOGGER.info ("Participant information of '" + aParticipantID.getURIEncoded () + "' returned " + aSGHrefs.size () + " entries");

        final HCH3 aH3 = h3 ("ServiceGroup contents");
        if (bShowTime)
          aH3.addChild (" ").addChild (_createTimingNode (aSWGetDocTypes.getMillis ()));
        aNodeList.addChild (aH3);
        final String sPathStart = "/" + aParticipantID.getURIEncoded () + "/services/";

        // Show all ServiceGroup hrefs
        for (final Map.Entry <String, String> aEntry : aSGHrefs.entrySet ())
        {
          final String sHref = aEntry.getKey ();
          final String sOriginalHref = aEntry.getValue ();

          final IHCLI <?> aLI = aUL.addAndReturnItem (div (code (sHref)));
          // Should be case insensitive "indexOf" here
          final int nPathStart = sHref.toLowerCase (Locale.US).indexOf (sPathStart.toLowerCase (Locale.US));
          if (nPathStart >= 0)
          {
            final String sDocType = sHref.substring (nPathStart + sPathStart.length ());
            final IDocumentTypeIdentifier aDocType = aQueryParams.getIF ().parseDocumentTypeIdentifier (sDocType);
            if (aDocType != null)
            {
              aDocTypeIDs.add (aDocType);
              aLI.addChild (div (EFontAwesome4Icon.ARROW_RIGHT.getAsNode ()).addChild (" ")
                                                                            .addChild (AppCommonUI.createDocTypeID (aDocType, false)));
              aLI.addChild (div (EFontAwesome4Icon.ARROW_RIGHT.getAsNode ()).addChild (" ")
                                                                            .addChild (_createOpenInBrowser (sOriginalHref)));
            }
            else
            {
              aLI.addChild (error ("The document type ").addChild (code (sDocType))
                                                        .addChild (" could not be interpreted as a structured document type!"));
            }
          }
          else
          {
            aLI.addChild (error ().addChildren (div ("Contained href does not match the rules!"),
                                                div ("Found href: ").addChild (code (sHref)),
                                                div ("Expected path part: ").addChild (code (sPathStart))));
          }
        }
        if (!aUL.hasChildren ())
          aUL.addItem (warn ("No service group entries were found for " + aParticipantID.getURIEncoded ()));
        aNodeList.addChild (aUL);
      }

      // List document type details
      if (aDocTypeIDs.isNotEmpty ())
      {
        final LocalDateTime aNowDateTime = PDTFactory.getCurrentLocalDateTime ();
        final ICommonsOrderedSet <X509Certificate> aAllUsedEndpointCertifiactes = new CommonsLinkedHashSet <> ();
        long nTotalDurationMillis = 0;

        aNodeList.addChild (h3 ("Document type details"));
        final HCUL aULDocTypeIDs = new HCUL ();
        for (final IDocumentTypeIdentifier aDocTypeID : aDocTypeIDs.getSortedInline (IDocumentTypeIdentifier.comparator ()))
        {
          final HCDiv aDocTypeDiv = div (AppCommonUI.createDocTypeID (aDocTypeID, true));
          final IHCLI <?> aLIDocTypeID = aULDocTypeIDs.addAndReturnItem (aDocTypeDiv);

          LOGGER.info ("Now SMP querying '" + aParticipantID.getURIEncoded () + "' / '" + aDocTypeID.getURIEncoded () + "'");

          final StopWatch aSWGetDetails = StopWatch.createdStarted ();
          switch (aQueryParams.getSMPAPIType ())
          {
            case PEPPOL:
            {
              final com.helger.smpclient.peppol.jaxb.SignedServiceMetadataType aSSM = aSMPClient.getServiceMetadataOrNull (aParticipantID,
                                                                                                                           aDocTypeID);
              aSWGetDetails.stop ();
              if (aSSM != null)
              {
                final com.helger.smpclient.peppol.jaxb.ServiceMetadataType aSM = aSSM.getServiceMetadata ();
                if (aSM.getRedirect () != null)
                  aLIDocTypeID.addChild (div ("Redirect to " + aSM.getRedirect ().getHref ()));
                else
                {
                  // For all processes
                  final HCUL aULProcessID = new HCUL ();
                  for (final com.helger.smpclient.peppol.jaxb.ProcessType aProcess : aSM.getServiceInformation ()
                                                                                        .getProcessList ()
                                                                                        .getProcess ())
                    if (aProcess.getProcessIdentifier () != null)
                    {
                      final IHCLI <?> aLIProcessID = aULProcessID.addItem ();
                      aLIProcessID.addChild (div ("Process ID: ").addChild (AppCommonUI.createProcessID (aDocTypeID,
                                                                                                         SimpleProcessIdentifier.wrap (aProcess.getProcessIdentifier ()))));
                      final HCUL aULEndpoint = new HCUL ();
                      // For all endpoints of the process
                      for (final com.helger.smpclient.peppol.jaxb.EndpointType aEndpoint : aProcess.getServiceEndpointList ()
                                                                                                   .getEndpoint ())
                      {
                        final IHCLI <?> aLIEndpoint = aULEndpoint.addItem ();

                        // Endpoint URL
                        final String sEndpointRef = aEndpoint.getEndpointReference () == null ? null
                                                                                              : W3CEndpointReferenceHelper.getAddress (aEndpoint.getEndpointReference ());
                        _printEndpointURL (aLIEndpoint, sEndpointRef);

                        // Valid from
                        _printActivationDate (aLIEndpoint, aEndpoint.getServiceActivationDate (), aDisplayLocale);

                        // Valid to
                        _printExpirationDate (aLIEndpoint, aEndpoint.getServiceExpirationDate (), aDisplayLocale);

                        // Transport profile
                        _printTransportProfile (aLIEndpoint, aEndpoint.getTransportProfile ());

                        // Technical infos
                        _printTecInfo (aLIEndpoint,
                                       StringHelper.getImplodedNonEmpty (" / ",
                                                                         aEndpoint.getTechnicalInformationUrl (),
                                                                         aEndpoint.getTechnicalContactUrl ()));

                        // Certificate (also add null values)
                        final X509Certificate aCert = CertificateHelper.convertStringToCertficateOrNull (aEndpoint.getCertificate ());
                        aAllUsedEndpointCertifiactes.add (aCert);
                      }
                      aLIProcessID.addChild (aULEndpoint);
                    }
                  aLIDocTypeID.addChild (aULProcessID);
                }
              }
              else
              {
                aLIDocTypeID.addChild (error ("Failed to read service metadata from SMP (not found)"));
              }
              break;
            }
            case OASIS_BDXR_V1:
            {
              final com.helger.xsds.bdxr.smp1.SignedServiceMetadataType aSSM = aBDXR1Client.getServiceMetadataOrNull (aParticipantID,
                                                                                                                      aDocTypeID);
              aSWGetDetails.stop ();
              if (aSSM != null)
              {
                final com.helger.xsds.bdxr.smp1.ServiceMetadataType aSM = aSSM.getServiceMetadata ();
                if (aSM.getRedirect () != null)
                  aLIDocTypeID.addChild (div ("Redirect to " + aSM.getRedirect ().getHref ()));
                else
                {
                  // For all processes
                  final HCUL aULProcessID = new HCUL ();
                  for (final com.helger.xsds.bdxr.smp1.ProcessType aProcess : aSM.getServiceInformation ().getProcessList ().getProcess ())
                    if (aProcess.getProcessIdentifier () != null)
                    {
                      final IHCLI <?> aLIProcessID = aULProcessID.addItem ();
                      aLIProcessID.addChild (div ("Process ID: ").addChild (AppCommonUI.createProcessID (aDocTypeID,
                                                                                                         SimpleProcessIdentifier.wrap (aProcess.getProcessIdentifier ()))));
                      final HCUL aULEndpoint = new HCUL ();
                      // For all endpoints of the process
                      for (final com.helger.xsds.bdxr.smp1.EndpointType aEndpoint : aProcess.getServiceEndpointList ().getEndpoint ())
                      {
                        final IHCLI <?> aLIEndpoint = aULEndpoint.addItem ();

                        // Endpoint URL
                        _printEndpointURL (aLIEndpoint, aEndpoint.getEndpointURI ());

                        // Valid from
                        _printActivationDate (aLIEndpoint, aEndpoint.getServiceActivationDate (), aDisplayLocale);

                        // Valid to
                        _printExpirationDate (aLIEndpoint, aEndpoint.getServiceExpirationDate (), aDisplayLocale);

                        // Transport profile
                        _printTransportProfile (aLIEndpoint, aEndpoint.getTransportProfile ());

                        // Technical infos
                        _printTecInfo (aLIEndpoint,
                                       StringHelper.getImplodedNonEmpty (" / ",
                                                                         aEndpoint.getTechnicalInformationUrl (),
                                                                         aEndpoint.getTechnicalContactUrl ()));

                        // Certificate (also add null values)
                        final X509Certificate aCert = CertificateHelper.convertByteArrayToCertficateDirect (aEndpoint.getCertificate ());
                        aAllUsedEndpointCertifiactes.add (aCert);
                      }
                      aLIProcessID.addChild (aULEndpoint);
                    }
                  aLIDocTypeID.addChild (aULProcessID);
                }
              }
              else
              {
                aLIDocTypeID.addChild (error ("Failed to read service metadata from SMP (not found)"));
              }
              break;
            }
          }
          if (bShowTime)
            aDocTypeDiv.addChild (" ").addChild (_createTimingNode (aSWGetDetails.getMillis ()));
          nTotalDurationMillis += aSWGetDetails.getMillis ();
        }
        aNodeList.addChild (aULDocTypeIDs);

        if (bShowTime)
          aNodeList.addChild (div ("Overall time: ").addChild (_createTimingNode (nTotalDurationMillis)));

        aNodeList.addChild (h3 ("Endpoint Certificate details"));
        if (aAllUsedEndpointCertifiactes.isEmpty ())
        {
          aNodeList.addChild (warn ("No Endpoint Certificate information was found."));
        }
        else
        {
          final HCUL aULCerts = new HCUL ();
          for (final X509Certificate aEndpointCert : aAllUsedEndpointCertifiactes)
          {
            final IHCLI <?> aLICert = aULCerts.addItem ();
            if (aEndpointCert != null)
            {
              aLICert.addChild (div ("Subject: " + aEndpointCert.getSubjectX500Principal ().getName ()));
              aLICert.addChild (div ("Issuer: " + aEndpointCert.getIssuerX500Principal ().getName ()));
              final LocalDateTime aNotBefore = PDTFactory.createLocalDateTime (aEndpointCert.getNotBefore ());
              aLICert.addChild (div ("Not before: " + PDTToString.getAsString (aNotBefore, aDisplayLocale)));
              if (aNotBefore.isAfter (aNowDateTime))
                aLICert.addChild (error ("This Endpoint Certificate is not yet valid!"));
              final LocalDateTime aNotAfter = PDTFactory.createLocalDateTime (aEndpointCert.getNotAfter ());
              aLICert.addChild (div ("Not after: " + PDTToString.getAsString (aNotAfter, aDisplayLocale)));
              if (aNotAfter.isBefore (aNowDateTime))
                aLICert.addChild (error ("This Endpoint Certificate is no longer valid!"));
              aLICert.addChild (div ("Serial number: " +
                                     aEndpointCert.getSerialNumber ().toString () +
                                     " / 0x" +
                                     aEndpointCert.getSerialNumber ().toString (16)));

              if (aQueryParams.getSMPAPIType () == ESMPAPIType.PEPPOL)
              {
                // Check Peppol certificate status
                final EPeppolCertificateCheckResult eCertStatus = PeppolCertificateChecker.checkPeppolAPCertificate (aEndpointCert,
                                                                                                                     aNowDateTime,
                                                                                                                     ETriState.FALSE,
                                                                                                                     ETriState.UNDEFINED);
                if (eCertStatus.isValid ())
                  aLICert.addChild (success ("The Endpoint Certificate appears to be a valid Peppol certificate."));
                else
                  aLICert.addChild (error ("The Endpoint Certificate appears to be an invalid Peppol certificate. Reason: " +
                                           eCertStatus.getReason ()));
              }

              final HCTextArea aTextArea = new HCTextArea ().setReadOnly (true)
                                                            .setRows (4)
                                                            .setValue (CertificateHelper.getPEMEncodedCertificate (aEndpointCert))
                                                            .addStyle (CCSSProperties.FONT_FAMILY.newValue (CCSSValue.FONT_MONOSPACE));
              BootstrapFormHelper.markAsFormControl (aTextArea);
              aLICert.addChild (div (aTextArea));
            }
            else
            {
              aLICert.addChild (error ("Failed to interpret the data as a X509 certificate"));
            }
          }
          aNodeList.addChild (aULCerts);
        }
      }

      if (bQueryBusinessCard)
      {
        final StopWatch aSWGetBC = StopWatch.createdStarted ();
        aNodeList.addChild (h3 ("Business Card details"));

        EFamFamFlagIcon.registerResourcesForThisRequest ();
        final String sBCURL = aSMPHost.toExternalForm () + "/businesscard/" + aParticipantID.getURIEncoded ();
        LOGGER.info ("Querying BC from '" + sBCURL + "'");
        byte [] aData;
        try (HttpClientManager aHttpClientMgr = new HttpClientManager ())
        {
          final HttpGet aGet = new HttpGet (sBCURL);
          aData = aHttpClientMgr.execute (aGet, new ResponseHandlerByteArray ());
        }
        catch (final Exception ex)
        {
          aData = null;
        }
        aSWGetBC.stop ();

        if (aData == null)
          aNodeList.addChild (warn ("No Business Card is available for that participant."));
        else
        {
          final PDBusinessCard aBC = PDBusinessCardHelper.parseBusinessCard (aData, null);
          if (aBC == null)
          {
            aNodeList.addChild (error ("Failed to parse the response data as a Business Card."));

            final String sBC = new String (aData, StandardCharsets.UTF_8);
            if (StringHelper.hasText (sBC))
              aNodeList.addChild (new HCPrismJS (EPrismLanguage.MARKUP).addChild (sBC));
            LOGGER.error ("Failed to parse BC:\n" + sBC);
          }
          else
          {
            final HCH4 aH4 = h4 ("Business Card contains " +
                                 (aBC.businessEntities ().size () == 1 ? "1 entity" : aBC.businessEntities ().size () + " entities"));
            if (bShowTime)
              aH4.addChild (" ").addChild (_createTimingNode (aSWGetBC.getMillis ()));
            aNodeList.addChild (aH4);
            aNodeList.addChild (div (_createOpenInBrowser (sBCURL)));

            final HCUL aUL = new HCUL ();
            for (final PDBusinessEntity aEntity : aBC.businessEntities ())
            {
              final HCLI aLI = aUL.addItem ();

              // Name
              for (final PDName aName : aEntity.names ())
              {
                final Locale aLanguage = LanguageCache.getInstance ().getLanguage (aName.getLanguageCode ());
                final String sLanguageName = aLanguage == null ? "" : " (" + aLanguage.getDisplayLanguage (aDisplayLocale) + ")";
                aLI.addChild (div (aName.getName () + sLanguageName));
              }

              // Country
              {
                final String sCountryCode = aEntity.getCountryCode ();
                final Locale aCountryCode = CountryCache.getInstance ().getCountry (sCountryCode);
                final String sCountryName = aCountryCode == null ? sCountryCode
                                                                 : aCountryCode.getDisplayCountry (aDisplayLocale) +
                                                                   " (" +
                                                                   sCountryCode +
                                                                   ")";
                final EFamFamFlagIcon eIcon = EFamFamFlagIcon.getFromIDOrNull (sCountryCode);
                aLI.addChild (div ("Country: " + sCountryName + " ").addChild (eIcon == null ? null : eIcon.getAsNode ()));
              }

              // Geo info
              if (aEntity.hasGeoInfo ())
              {
                aLI.addChild (div ("Geographical information: ").addChildren (HCExtHelper.nl2brList (aEntity.getGeoInfo ())));
              }
              // Additional IDs
              if (aEntity.identifiers ().isNotEmpty ())
              {
                final BootstrapTable aIDTab = new BootstrapTable ().setCondensed (true);
                aIDTab.addHeaderRow ().addCells ("Scheme", "Value");
                for (final PDIdentifier aItem : aEntity.identifiers ())
                  aIDTab.addBodyRow ().addCells (aItem.getScheme (), aItem.getValue ());
                aLI.addChild (div ("Additional identifiers: ").addChild (aIDTab));
              }
              // Website URLs
              if (aEntity.websiteURIs ().isNotEmpty ())
              {
                final HCNodeList aWebsites = new HCNodeList ();
                for (final String sItem : aEntity.websiteURIs ())
                  aWebsites.addChild (div (HCA.createLinkedWebsite (sItem)));
                aLI.addChild (div ("Website URLs: ").addChild (aWebsites));
              }
              // Contacts
              if (aEntity.contacts ().isNotEmpty ())
              {
                final BootstrapTable aContactTab = new BootstrapTable ().setCondensed (true);
                aContactTab.addHeaderRow ().addCells ("Type", "Name", "Phone", "Email");
                for (final PDContact aItem : aEntity.contacts ())
                  aContactTab.addBodyRow ()
                             .addCell (aItem.getType ())
                             .addCell (aItem.getName ())
                             .addCell (aItem.getPhoneNumber ())
                             .addCell (HCA_MailTo.createLinkedEmail (aItem.getEmail ()));
                aLI.addChild (div ("Contact points: ").addChild (aContactTab));
              }
              if (aEntity.hasAdditionalInfo ())
              {
                aLI.addChild (div ("Additional information: ").addChildren (HCExtHelper.nl2brList (aEntity.getAdditionalInfo ())));
              }
              if (aEntity.hasRegistrationDate ())
              {
                aLI.addChild (div ("Registration date: ").addChild (PDTToString.getAsString (aEntity.getRegistrationDate (),
                                                                                             aDisplayLocale)));
              }
            }
            aNodeList.addChild (aUL);
          }
        }
      }

      // Audit success
      AuditHelper.onAuditExecuteSuccess ("participant-information", aParticipantID.getURIEncoded ());
    }
    catch (final UnknownHostException ex)
    {
      aNodeList.addChild (error (div ("Seems like the participant ID " +
                                      sParticipantIDUriEncoded +
                                      " is not registered to the Peppol network.")).addChild (AppCommonUI.getTechnicalDetailsUI (ex, false))
                                                                                   .addChild (bSMLAutoDetect ? null
                                                                                                             : div ("Try selecting a different SML - maybe this helps")));

      // Audit failure
      AuditHelper.onAuditExecuteFailure ("participant-information", sParticipantIDUriEncoded, "unknown-host", ex.getMessage ());
    }
    catch (final SMPDNSResolutionException ex)
    {
      aNodeList.addChild (error (div ("Seems like the participant ID " +
                                      sParticipantIDUriEncoded +
                                      " is not registered to the Peppol network.")).addChild (AppCommonUI.getTechnicalDetailsUI (ex, false))
                                                                                   .addChild (bSMLAutoDetect ? null
                                                                                                             : div ("Try selecting a different SML - maybe this helps")));

      // Audit failure
      AuditHelper.onAuditExecuteFailure ("participant-information", sParticipantIDUriEncoded, "dns-resolution-failed", ex.getMessage ());
    }
    catch (final SMPClientBadResponseException ex)
    {
      aNodeList.addChild (error (div ("Error querying SMP. Try disabling 'XML Schema validation'.")).addChild (AppCommonUI.getTechnicalDetailsUI (ex,
                                                                                                                                                  false)));

      // Audit failure
      AuditHelper.onAuditExecuteFailure ("participant-information", sParticipantIDUriEncoded, ex.getClass (), ex.getMessage ());
    }
    catch (final Exception ex)
    {
      // Don't spam me
      final boolean bInterestingException = !(ex instanceof SMPClientException);
      if (bInterestingException)
      {
        new InternalErrorBuilder ().setRequestScope (aRequestScope).setDisplayLocale (aDisplayLocale).setThrowable (ex).handle ();
      }
      aNodeList.addChild (error (div ("Error querying SMP.")).addChild (AppCommonUI.getTechnicalDetailsUI (ex, bInterestingException)));

      // Audit failure
      AuditHelper.onAuditExecuteFailure ("participant-information", sParticipantIDUriEncoded, ex.getClass (), ex.getMessage ());
    }

    aNodeList.addChild (new HCHR ());
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final ISMLConfigurationManager aSMLConfigurationMgr = PPMetaManager.getSMLConfigurationMgr ();
    final FormErrorList aFormErrors = new FormErrorList ();
    final boolean bShowInput = true;

    String sParticipantIDScheme = DEFAULT_ID_SCHEME;
    String sParticipantIDValue = null;
    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      // Validate fields
      sParticipantIDScheme = StringHelper.trim (aWPEC.params ().getAsString (FIELD_ID_SCHEME));
      sParticipantIDValue = StringHelper.trim (aWPEC.params ().getAsString (FIELD_ID_VALUE));
      final String sSMLID = StringHelper.trim (aWPEC.params ().getAsString (FIELD_SML));
      final ISMLConfiguration aSMLConfiguration = aSMLConfigurationMgr.getSMLInfoOfID (sSMLID);
      final boolean bSMLAutoDetect = SMLConfigurationSelect.FIELD_AUTO_SELECT.equals (sSMLID);
      final boolean bQueryBusinessCard = aWPEC.params ().isCheckBoxChecked (PARAM_QUERY_BUSINESS_CARD, DEFAULT_QUERY_BUSINESS_CARD);
      final boolean bShowTime = aWPEC.params ().isCheckBoxChecked (PARAM_SHOW_TIME, DEFAULT_SHOW_TIME);
      final boolean bXSDValidation = aWPEC.params ().isCheckBoxChecked (PARAM_XSD_VALIDATION, DEFAULT_XSD_VALIDATION);
      final boolean bVerifySignatures = aWPEC.params ().isCheckBoxChecked (PARAM_VERIFY_SIGNATURES, DEFAULT_VERIFY_SIGNATURES);

      // Legacy URL params?
      if (aWPEC.params ().containsKey ("idscheme") && aWPEC.params ().containsKey ("idvalue"))
      {
        sParticipantIDScheme = DEFAULT_ID_SCHEME;
        sParticipantIDValue = StringHelper.trim (aWPEC.params ().getAsString ("idscheme")) +
                              ":" +
                              StringHelper.trim (aWPEC.params ().getAsString ("idvalue"));
      }

      if (StringHelper.hasNoText (sParticipantIDScheme))
        aFormErrors.addFieldError (FIELD_ID_SCHEME, "Please provide an identifier scheme");
      else
        if (!PeppolIdentifierFactory.INSTANCE.isParticipantIdentifierSchemeValid (sParticipantIDScheme))
          aFormErrors.addFieldError (FIELD_ID_SCHEME, "The participant identifier scheme '" + sParticipantIDScheme + "' is not valid!");

      if (StringHelper.hasNoText (sParticipantIDValue))
        aFormErrors.addFieldError (FIELD_ID_VALUE, "Please provide an identifier value");
      else
        if (!PeppolIdentifierFactory.INSTANCE.isParticipantIdentifierValueValid (sParticipantIDValue))
          aFormErrors.addFieldError (FIELD_ID_VALUE, "The participant identifier value '" + sParticipantIDValue + "' is not valid!");

      if (aSMLConfiguration == null && !bSMLAutoDetect)
        aFormErrors.addFieldError (FIELD_SML, "A valid SML must be selected!");

      if (aFormErrors.isEmpty ())
      {
        _queryParticipant (aWPEC,
                           sParticipantIDScheme,
                           sParticipantIDValue,
                           aSMLConfiguration,
                           bSMLAutoDetect,
                           bQueryBusinessCard,
                           bShowTime,
                           bXSDValidation,
                           bVerifySignatures);
      }
    }

    if (bShowInput)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (getUIHandler ().createFormSelf (aWPEC)
                                                                              .setMethod (EHCFormMethod.GET)
                                                                              .setLeft (3));
      aForm.addChild (info ().addChildren (div ("Show all processes, document types and endpoints of a participant."),
                                           div ("You may want to try scheme ").addChild (code (DEFAULT_ID_SCHEME))
                                                                              .addChild (" and value ")
                                                                              .addChild (code ("9915:test"))
                                                                              .addChild (" on ")
                                                                              .addChild (code ("SMK"))
                                                                              .addChild (" as an example.")));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier scheme")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_ID_SCHEME,
                                                                                           sParticipantIDScheme)).setMaxLength (PeppolIdentifierHelper.MAX_IDENTIFIER_SCHEME_LENGTH)
                                                                                                                 .setPlaceholder ("Identifier scheme"))
                                                   .setHelpText (div ("The most common identifier scheme is ").addChild (code (DEFAULT_ID_SCHEME)))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_ID_SCHEME)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier value")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_ID_VALUE,
                                                                                           sParticipantIDValue)).setMaxLength (PeppolIdentifierHelper.MAX_PARTICIPANT_VALUE_LENGTH)
                                                                                                                .setPlaceholder ("Identifier value"))
                                                   .setHelpText (div ("The identifier value must look like ").addChild (code ("9915:test")))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_ID_VALUE)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML to use")
                                                   .setCtrl (new SMLConfigurationSelect (new RequestField (FIELD_SML,
                                                                                                           SMLConfigurationSelect.FIELD_AUTO_SELECT),
                                                                                         true))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_SML)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Query Business Card?")
                                                   .setCtrl (new HCCheckBox (new RequestFieldBoolean (PARAM_QUERY_BUSINESS_CARD,
                                                                                                      DEFAULT_QUERY_BUSINESS_CARD)))
                                                   .setErrorList (aFormErrors.getListOfField (PARAM_QUERY_BUSINESS_CARD)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Show query duration?")
                                                   .setCtrl (new HCCheckBox (new RequestFieldBoolean (PARAM_SHOW_TIME, DEFAULT_SHOW_TIME)))
                                                   .setErrorList (aFormErrors.getListOfField (PARAM_SHOW_TIME)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Enable XML Schema validation of responses?")
                                                   .setCtrl (new HCCheckBox (new RequestFieldBoolean (PARAM_XSD_VALIDATION,
                                                                                                      DEFAULT_XSD_VALIDATION)))
                                                   .setErrorList (aFormErrors.getListOfField (PARAM_XSD_VALIDATION)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Verify signatures of SMP responses?")
                                                   .setCtrl (new HCCheckBox (new RequestFieldBoolean (PARAM_VERIFY_SIGNATURES,
                                                                                                      DEFAULT_VERIFY_SIGNATURES)))
                                                   .setErrorList (aFormErrors.getListOfField (PARAM_VERIFY_SIGNATURES)));

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
      aToolbar.addSubmitButton ("Show details");
    }
  }
}
