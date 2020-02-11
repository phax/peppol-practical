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
import com.helger.datetime.util.PDTXMLConverter;
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
import com.helger.html.hc.html.textlevel.HCCode;
import com.helger.html.hc.html.textlevel.HCEM;
import com.helger.html.hc.html.textlevel.HCSmall;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.httpclient.HttpClientManager;
import com.helger.httpclient.response.ResponseHandlerByteArray;
import com.helger.network.dns.IPV4Addr;
import com.helger.pd.businesscard.generic.PDBusinessCard;
import com.helger.pd.businesscard.generic.PDBusinessEntity;
import com.helger.pd.businesscard.generic.PDContact;
import com.helger.pd.businesscard.generic.PDIdentifier;
import com.helger.pd.businesscard.generic.PDName;
import com.helger.pd.businesscard.helper.PDBusinessCardHelper;
import com.helger.peppol.app.mgr.ISMLInfoManager;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.domain.SMPQueryParams;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.smp.ESMPTransportProfile;
import com.helger.peppol.ui.AppCommonUI;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.peppol.ui.select.SMLSelect;
import com.helger.peppol.utils.EPeppolCertificateCheckResult;
import com.helger.peppol.utils.PeppolCertificateChecker;
import com.helger.peppolid.CIdentifier;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.peppolid.simple.process.SimpleProcessIdentifier;
import com.helger.photon.audit.AuditHelper;
import com.helger.photon.bootstrap4.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap4.alert.BootstrapInfoBox;
import com.helger.photon.bootstrap4.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap4.alert.BootstrapWarnBox;
import com.helger.photon.bootstrap4.badge.BootstrapBadge;
import com.helger.photon.bootstrap4.badge.EBootstrapBadgeType;
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
import com.helger.smpclient.exception.SMPClientException;
import com.helger.smpclient.peppol.SMPClientReadOnly;
import com.helger.smpclient.peppol.utils.W3CEndpointReferenceHelper;
import com.helger.smpclient.url.PeppolDNSResolutionException;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

public class PagePublicToolsParticipantInformation extends AbstractAppWebPage
{
  public static final String DEFAULT_ID_SCHEME = PeppolIdentifierHelper.DEFAULT_PARTICIPANT_SCHEME;
  public static final String FIELD_ID_SCHEME = "scheme";
  public static final String FIELD_ID_VALUE = "value";
  public static final String FIELD_SML = "sml";
  public static final String PARAM_QUERY_BUSINESS_CARD = "querybc";
  public static final String PARAM_SHOW_TIME = "showtime";

  private static final boolean DEFAULT_QUERY_BUSINESS_CARD = true;
  private static final boolean DEFAULT_SHOW_TIME = false;
  private static final Logger LOGGER = LoggerFactory.getLogger (PagePublicToolsParticipantInformation.class);

  public PagePublicToolsParticipantInformation (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Participant information");
  }

  @Nonnull
  private static BootstrapLinkButton _createOpenInBrowser (@Nonnull final String sURL)
  {
    return new BootstrapLinkButton (EBootstrapButtonSize.SMALL).setButtonType (EBootstrapButtonType.OUTLINE_INFO)
                                                               .setHref (new SimpleURL (sURL))
                                                               .addChild ("Open in browser")
                                                               .setTargetBlank ();
  }

  @Nonnull
  private static IHCNode _createTimingNode (final long nMillis)
  {
    return new BootstrapBadge (EBootstrapBadgeType.INFO).addChild ("took " + nMillis + " milliseconds");
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aWPEC.getRequestScope ();
    final ISMLInfoManager aSMLInfoMgr = PPMetaManager.getSMLInfoMgr ();
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
      ISMLInfo aSML = aSMLInfoMgr.getSMLInfoOfID (sSMLID);
      final boolean bSMLAutoDetect = SMLSelect.FIELD_AUTO_SELECT.equals (sSMLID);
      final boolean bQueryBusinessCard = aWPEC.params ()
                                              .isCheckBoxChecked (PARAM_QUERY_BUSINESS_CARD,
                                                                  DEFAULT_QUERY_BUSINESS_CARD);
      final boolean bShowTime = aWPEC.params ().isCheckBoxChecked (PARAM_SHOW_TIME, DEFAULT_SHOW_TIME);

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
          aFormErrors.addFieldError (FIELD_ID_SCHEME,
                                     "The participant identifier scheme '" + sParticipantIDScheme + "' is not valid!");

      if (StringHelper.hasNoText (sParticipantIDValue))
        aFormErrors.addFieldError (FIELD_ID_VALUE, "Please provide an identifier value");
      else
        if (!PeppolIdentifierFactory.INSTANCE.isParticipantIdentifierValueValid (sParticipantIDValue))
          aFormErrors.addFieldError (FIELD_ID_VALUE,
                                     "The participant identifier value '" + sParticipantIDValue + "' is not valid!");

      if (aSML == null && !bSMLAutoDetect)
        aFormErrors.addFieldError (FIELD_SML, "A valid SML must be selected!");

      if (aFormErrors.isEmpty ())
      {
        // Try to print the basic information before an error occurs
        final String sParticipantIDUriEncoded = CIdentifier.getURIEncoded (sParticipantIDScheme, sParticipantIDValue);
        aNodeList.addChild (new HCDiv ().addChild ("Querying the following SMP for ")
                                        .addChild (new HCCode ().addChild (sParticipantIDUriEncoded))
                                        .addChild (":"));

        try
        {
          SMPQueryParams aQueryParams = null;
          if (bSMLAutoDetect)
          {
            for (final ISMLInfo aCurSML : aSMLInfoMgr.getAllSorted ())
            {
              aQueryParams = SMPQueryParams.createForSML (aCurSML, sParticipantIDScheme, sParticipantIDValue);
              if (aQueryParams == null)
                continue;
              try
              {
                InetAddress.getByName (aQueryParams.getSMPHostURI ().getHost ());
                // Found it
                aSML = aCurSML;
                break;
              }
              catch (final UnknownHostException ex)
              {
                // continue
              }
            }

            // Ensure to go into the exception handler
            if (aSML == null)
              throw new UnknownHostException ("");
          }
          else
          {
            aQueryParams = SMPQueryParams.createForSML (aSML, sParticipantIDScheme, sParticipantIDValue);
          }

          if (aQueryParams == null)
            throw new PeppolDNSResolutionException ("Failed to resolve participant ID '" +
                                                    sParticipantIDUriEncoded +
                                                    "' for the provided SML '" +
                                                    aSML.getID () +
                                                    "'");

          final IParticipantIdentifier aParticipantID = aQueryParams.getParticipantID ();

          LOGGER.info ("Participant information of '" +
                       sParticipantIDUriEncoded +
                       "' is queried using SMP API '" +
                       aQueryParams.getSMPAPIType () +
                       "' from '" +
                       aQueryParams.getSMPHostURI () +
                       "' using SML '" +
                       aSML +
                       "'");
          final URL aSMPHost = aQueryParams.getSMPHostURI ().toURL ();

          {
            final HCUL aUL = new HCUL ();
            aUL.addItem (new HCDiv ().addChild ("SML used: ")
                                     .addChild (new HCCode ().addChild (aSML.getDisplayName () +
                                                                        " / " +
                                                                        aSML.getDNSZone ())));

            final String sURL1 = aSMPHost.toExternalForm ();
            aUL.addItem (new HCDiv ().addChild ("Peppol name: ").addChild (new HCCode ().addChild (sURL1)),
                         new HCDiv ().addChild (_createOpenInBrowser (sURL1)));

            final InetAddress aInetAddress = InetAddress.getByName (aSMPHost.getHost ());
            final String sURL2 = new IPV4Addr (aInetAddress).getAsString ();
            aUL.addItem (new HCDiv ().addChild ("IP address: ").addChild (new HCCode ().addChild (sURL2)),
                         new HCDiv ().addChild (_createOpenInBrowser ("http://" + sURL2)));

            final InetAddress aNice = InetAddress.getByAddress (aInetAddress.getAddress ());
            final String sURL3 = aNice.getCanonicalHostName ();
            aUL.addItem (new HCDiv ().addChild ("Nice name: ")
                                     .addChild (new HCCode ().addChild (sURL3))
                                     .addChild (" (determined by reverse name lookup - this is potentially not the URL you registered your SMP for!)"),
                         new HCDiv ().addChild (_createOpenInBrowser ("http://" + sURL3)));

            final String sURL4 = sURL1 + "/" + sParticipantIDUriEncoded;
            aUL.addItem (new HCDiv ().addChild ("Query base URL: ").addChild (new HCCode ().addChild (sURL4)),
                         new HCDiv ().addChild (_createOpenInBrowser (sURL4)));

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
                      aUL.addItem (new BootstrapWarnBox ().addChild ("The ServiceGroup list contains the duplicate URL ")
                                                          .addChild (new HCCode ().addChild (sHref)));
                  }
                break;
              }
              case OASIS_BDXR_V1:
              {
                aBDXR1Client = new BDXRClientReadOnly (aQueryParams.getSMPHostURI ());

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
                      aUL.addItem (new BootstrapWarnBox ().addChild ("The ServiceGroup list contains the duplicate URL ")
                                                          .addChild (new HCCode ().addChild (sHref)));
                  }
                break;
              }
            }

            aSWGetDocTypes.stop ();

            LOGGER.info ("Participant information of '" +
                         aParticipantID.getURIEncoded () +
                         "' returned " +
                         aSGHrefs.size () +
                         " entries");

            final HCH3 aH3 = new HCH3 ().addChild ("ServiceGroup contents");
            if (bShowTime)
              aH3.addChild (" ").addChild (_createTimingNode (aSWGetDocTypes.getMillis ()));
            aNodeList.addChild (aH3);
            final String sPathStart = "/" + aParticipantID.getURIEncoded () + "/services/";

            // Show all ServiceGroup hrefs
            for (final Map.Entry <String, String> aEntry : aSGHrefs.entrySet ())
            {
              final String sHref = aEntry.getKey ();
              final String sOriginalHref = aEntry.getValue ();

              final IHCLI <?> aLI = aUL.addAndReturnItem (new HCDiv ().addChild (new HCCode ().addChild (sHref)));
              // Should be case insensitive "indexOf" here
              final int nPathStart = sHref.toLowerCase (Locale.US).indexOf (sPathStart.toLowerCase (Locale.US));
              if (nPathStart >= 0)
              {
                final String sDocType = sHref.substring (nPathStart + sPathStart.length ());
                final IDocumentTypeIdentifier aDocType = aQueryParams.getIF ().parseDocumentTypeIdentifier (sDocType);
                if (aDocType != null)
                {
                  aDocTypeIDs.add (aDocType);
                  aLI.addChild (new HCDiv ().addChild (EFontAwesome4Icon.ARROW_RIGHT.getAsNode ())
                                            .addChild (" ")
                                            .addChild (AppCommonUI.createDocTypeID (aDocType, false)));
                  aLI.addChild (new HCDiv ().addChild (EFontAwesome4Icon.ARROW_RIGHT.getAsNode ())
                                            .addChild (" ")
                                            .addChild (_createOpenInBrowser (sOriginalHref)));
                }
                else
                {
                  aLI.addChild (new BootstrapErrorBox ().addChild ("The document type ")
                                                        .addChild (new HCCode ().addChild (sDocType))
                                                        .addChild (" could not be interpreted as a structured document type!"));
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
              aUL.addItem (new BootstrapWarnBox ().addChild ("No service group entries were found for " +
                                                             aParticipantID.getURIEncoded ()));
            aNodeList.addChild (aUL);
          }

          // List document type details
          if (aDocTypeIDs.isNotEmpty ())
          {
            final LocalDateTime aNowDateTime = PDTFactory.getCurrentLocalDateTime ();
            final LocalDate aNowDate = aNowDateTime.toLocalDate ();
            final ICommonsOrderedSet <X509Certificate> aAllUsedEndpointCertifiactes = new CommonsLinkedHashSet <> ();
            long nTotalDurationMillis = 0;

            aNodeList.addChild (new HCH3 ().addChild ("Document type details"));
            final HCUL aULDocTypeIDs = new HCUL ();
            for (final IDocumentTypeIdentifier aDocTypeID : aDocTypeIDs.getSortedInline (IDocumentTypeIdentifier.comparator ()))
            {
              final HCDiv aDocTypeDiv = new HCDiv ().addChild (AppCommonUI.createDocTypeID (aDocTypeID, true));
              final IHCLI <?> aLIDocTypeID = aULDocTypeIDs.addAndReturnItem (aDocTypeDiv);

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
                      aLIDocTypeID.addChild (new HCDiv ().addChild ("Redirect to " + aSM.getRedirect ().getHref ()));
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
                          aLIProcessID.addChild (new HCDiv ().addChild ("Process ID: ")
                                                             .addChild (AppCommonUI.createProcessID (aDocTypeID,
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
                            aAllUsedEndpointCertifiactes.add (CertificateHelper.convertStringToCertficateOrNull (aEndpoint.getCertificate ()));
                          }
                          aLIProcessID.addChild (aULEndpoint);
                        }
                      aLIDocTypeID.addChild (aULProcessID);
                    }
                  }
                  else
                  {
                    aLIDocTypeID.addChild (new BootstrapErrorBox ().addChild ("Failed to read service metadata from SMP"));
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
                      aLIDocTypeID.addChild (new HCDiv ().addChild ("Redirect to " + aSM.getRedirect ().getHref ()));
                    else
                    {
                      // For all processes
                      final HCUL aULProcessID = new HCUL ();
                      for (final com.helger.xsds.bdxr.smp1.ProcessType aProcess : aSM.getServiceInformation ()
                                                                                     .getProcessList ()
                                                                                     .getProcess ())
                        if (aProcess.getProcessIdentifier () != null)
                        {
                          final IHCLI <?> aLIProcessID = aULProcessID.addItem ();
                          aLIProcessID.addChild (new HCDiv ().addChild ("Process ID: ")
                                                             .addChild (AppCommonUI.createProcessID (aDocTypeID,
                                                                                                     SimpleProcessIdentifier.wrap (aProcess.getProcessIdentifier ()))));
                          final HCUL aULEndpoint = new HCUL ();
                          // For all endpoints of the process
                          for (final com.helger.xsds.bdxr.smp1.EndpointType aEndpoint : aProcess.getServiceEndpointList ()
                                                                                                .getEndpoint ())
                          {
                            final IHCLI <?> aLIEndpoint = aULEndpoint.addItem ();

                            // Endpoint URL
                            _printEndpointURL (aLIEndpoint, aEndpoint.getEndpointURI ());

                            // Valid from
                            _printActivationDate (aLIEndpoint,
                                                  PDTXMLConverter.getLocalDateTime (aEndpoint.getServiceActivationDate ()),
                                                  aDisplayLocale);

                            // Valid to
                            _printExpirationDate (aLIEndpoint,
                                                  PDTXMLConverter.getLocalDateTime (aEndpoint.getServiceExpirationDate ()),
                                                  aDisplayLocale);

                            // Transport profile
                            _printTransportProfile (aLIEndpoint, aEndpoint.getTransportProfile ());

                            // Technical infos
                            _printTecInfo (aLIEndpoint,
                                           StringHelper.getImplodedNonEmpty (" / ",
                                                                             aEndpoint.getTechnicalInformationUrl (),
                                                                             aEndpoint.getTechnicalContactUrl ()));

                            // Certificate (also add null values)
                            aAllUsedEndpointCertifiactes.add (CertificateHelper.convertByteArrayToCertficateDirect (aEndpoint.getCertificate ()));
                          }
                          aLIProcessID.addChild (aULEndpoint);
                        }
                      aLIDocTypeID.addChild (aULProcessID);
                    }
                  }
                  else
                  {
                    aLIDocTypeID.addChild (new BootstrapErrorBox ().addChild ("Failed to read service metadata from SMP"));
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
              aNodeList.addChild (new HCDiv ().addChild ("Overall time: ")
                                              .addChild (_createTimingNode (nTotalDurationMillis)));

            aNodeList.addChild (new HCH3 ().addChild ("Endpoint Certificate details"));
            if (aAllUsedEndpointCertifiactes.isEmpty ())
            {
              aNodeList.addChild (new BootstrapWarnBox ().addChild ("No Endpoint Certificate information was found."));
            }
            else
            {
              final HCUL aULCerts = new HCUL ();
              for (final X509Certificate aEndpointCert : aAllUsedEndpointCertifiactes)
              {
                final IHCLI <?> aLICert = aULCerts.addItem ();
                if (aEndpointCert != null)
                {
                  if (aEndpointCert.getSubjectDN () != null)
                    aLICert.addChild (new HCDiv ().addChild ("Subject: " + aEndpointCert.getSubjectDN ().toString ()));
                  if (aEndpointCert.getIssuerDN () != null)
                    aLICert.addChild (new HCDiv ().addChild ("Issuer: " + aEndpointCert.getIssuerDN ().toString ()));
                  final LocalDate aNotBefore = PDTFactory.createLocalDate (aEndpointCert.getNotBefore ());
                  aLICert.addChild (new HCDiv ().addChild ("Not before: " +
                                                           PDTToString.getAsString (aNotBefore, aDisplayLocale)));
                  if (aNotBefore.isAfter (aNowDate))
                    aLICert.addChild (new BootstrapErrorBox ().addChild ("This Endpoint Certificate is not yet valid!"));
                  final LocalDate aNotAfter = PDTFactory.createLocalDate (aEndpointCert.getNotAfter ());
                  aLICert.addChild (new HCDiv ().addChild ("Not after: " +
                                                           PDTToString.getAsString (aNotAfter, aDisplayLocale)));
                  if (aNotAfter.isBefore (aNowDate))
                    aLICert.addChild (new BootstrapErrorBox ().addChild ("This Endpoint Certificate is no longer valid!"));
                  aLICert.addChild (new HCDiv ().addChild ("Serial number: " +
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
                      aLICert.addChild (new BootstrapSuccessBox ().addChild ("The Endpoint Certificate appears to be a valid Peppol certificate."));
                    else
                      aLICert.addChild (new BootstrapErrorBox ().addChild ("The Endpoint Certificate appears to be an invalid Peppol certificate. Reason: " +
                                                                           eCertStatus.getReason ()));
                  }

                  final HCTextArea aTextArea = new HCTextArea ().setReadOnly (true)
                                                                .setRows (4)
                                                                .setValue (CertificateHelper.getPEMEncodedCertificate (aEndpointCert))
                                                                .addStyle (CCSSProperties.FONT_FAMILY.newValue (CCSSValue.FONT_MONOSPACE));
                  BootstrapFormHelper.markAsFormControl (aTextArea);
                  aLICert.addChild (new HCDiv ().addChild (aTextArea));
                }
                else
                {
                  aLICert.addChild (new BootstrapErrorBox ().addChild ("Failed to interpret the data as a X509 certificate"));
                }
              }
              aNodeList.addChild (aULCerts);
            }
          }

          if (bQueryBusinessCard)
          {
            final StopWatch aSWGetBC = StopWatch.createdStarted ();
            aNodeList.addChild (new HCH3 ().addChild ("Business Card details"));

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
              aNodeList.addChild (new BootstrapWarnBox ().addChild ("No Business Card is available for that participant."));
            else
            {
              final PDBusinessCard aBC = PDBusinessCardHelper.parseBusinessCard (aData, null);
              if (aBC == null)
              {
                aNodeList.addChild (new BootstrapErrorBox ().addChild ("Failed to parse the response data as a Business Card."));

                final String sBC = new String (aData, StandardCharsets.UTF_8);
                if (StringHelper.hasText (sBC))
                  aNodeList.addChild (new HCPrismJS (EPrismLanguage.MARKUP).addChild (sBC));
                LOGGER.error ("Failed to parse BC:\n" + sBC);
              }
              else
              {
                final HCH4 aH4 = new HCH4 ().addChild ("Business Card contains " +
                                                       (aBC.businessEntities ().size () == 1 ? "1 entity"
                                                                                             : aBC.businessEntities ()
                                                                                                  .size () +
                                                                                               " entities"));
                if (bShowTime)
                  aH4.addChild (" ").addChild (_createTimingNode (aSWGetBC.getMillis ()));
                aNodeList.addChild (aH4);
                aNodeList.addChild (new HCDiv ().addChild (_createOpenInBrowser (sBCURL)));

                final HCUL aUL = new HCUL ();
                for (final PDBusinessEntity aEntity : aBC.businessEntities ())
                {
                  final HCLI aLI = aUL.addItem ();

                  // Name
                  for (final PDName aName : aEntity.names ())
                  {
                    final Locale aLanguage = LanguageCache.getInstance ().getLanguage (aName.getLanguageCode ());
                    final String sLanguageName = aLanguage == null ? ""
                                                                   : " (" +
                                                                     aLanguage.getDisplayLanguage (aDisplayLocale) +
                                                                     ")";
                    aLI.addChild (new HCDiv ().addChild (aName.getName () + sLanguageName));
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
                    aLI.addChild (new HCDiv ().addChild ("Country: " + sCountryName + " ")
                                              .addChild (eIcon == null ? null : eIcon.getAsNode ()));
                  }

                  // Geo info
                  if (aEntity.hasGeoInfo ())
                  {
                    aLI.addChild (new HCDiv ().addChild ("Geographical information: ")
                                              .addChildren (HCExtHelper.nl2brList (aEntity.getGeoInfo ())));
                  }
                  // Additional IDs
                  if (aEntity.identifiers ().isNotEmpty ())
                  {
                    final BootstrapTable aIDTab = new BootstrapTable ().setCondensed (true);
                    aIDTab.addHeaderRow ().addCells ("Scheme", "Value");
                    for (final PDIdentifier aItem : aEntity.identifiers ())
                      aIDTab.addBodyRow ().addCells (aItem.getScheme (), aItem.getValue ());
                    aLI.addChild (new HCDiv ().addChild ("Additional identifiers: ").addChild (aIDTab));
                  }
                  // Website URLs
                  if (aEntity.websiteURIs ().isNotEmpty ())
                  {
                    final HCNodeList aWebsites = new HCNodeList ();
                    for (final String sItem : aEntity.websiteURIs ())
                      aWebsites.addChild (new HCDiv ().addChild (HCA.createLinkedWebsite (sItem)));
                    aLI.addChild (new HCDiv ().addChild ("Website URLs: ").addChild (aWebsites));
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
                    aLI.addChild (new HCDiv ().addChild ("Contact points: ").addChild (aContactTab));
                  }
                  if (aEntity.hasAdditionalInfo ())
                  {
                    aLI.addChild (new HCDiv ().addChild ("Additional information: ")
                                              .addChildren (HCExtHelper.nl2brList (aEntity.getAdditionalInfo ())));
                  }
                  if (aEntity.hasRegistrationDate ())
                  {
                    aLI.addChild (new HCDiv ().addChild ("Registration date: ")
                                              .addChild (PDTToString.getAsString (aEntity.getRegistrationDate (),
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
          aNodeList.addChild (new BootstrapErrorBox ().addChild (new HCDiv ().addChild ("Seems like the participant ID " +
                                                                                        sParticipantIDUriEncoded +
                                                                                        " is not registered to the Peppol network."))
                                                      .addChild (AppCommonUI.getTechnicalDetailsUI (ex))
                                                      .addChild (bSMLAutoDetect ? null
                                                                                : new HCDiv ().addChild ("Try selecting a different SML - maybe this helps")));

          // Audit failure
          AuditHelper.onAuditExecuteFailure ("participant-information",
                                             sParticipantIDUriEncoded,
                                             "unknown-host",
                                             ex.getMessage ());
        }
        catch (final PeppolDNSResolutionException ex)
        {
          aNodeList.addChild (new BootstrapErrorBox ().addChild (new HCDiv ().addChild ("Seems like the participant ID " +
                                                                                        sParticipantIDUriEncoded +
                                                                                        " is not registered to the Peppol network."))
                                                      .addChild (AppCommonUI.getTechnicalDetailsUI (ex))
                                                      .addChild (bSMLAutoDetect ? null
                                                                                : new HCDiv ().addChild ("Try selecting a different SML - maybe this helps")));

          // Audit failure
          AuditHelper.onAuditExecuteFailure ("participant-information",
                                             sParticipantIDUriEncoded,
                                             "dns-resolution-failed",
                                             ex.getMessage ());
        }
        catch (final Exception ex)
        {
          // Don't spam me
          if (!(ex instanceof SMPClientException))
          {
            new InternalErrorBuilder ().setRequestScope (aRequestScope)
                                       .setDisplayLocale (aDisplayLocale)
                                       .setThrowable (ex)
                                       .handle ();
          }
          aNodeList.addChild (new BootstrapErrorBox ().addChild (new HCDiv ().addChild ("Error querying SMP."))
                                                      .addChild (AppCommonUI.getTechnicalDetailsUI (ex)));

          // Audit failure
          AuditHelper.onAuditExecuteFailure ("participant-information",
                                             sParticipantIDUriEncoded,
                                             ex.getClass (),
                                             ex.getMessage ());
        }

        aNodeList.addChild (new HCHR ());
      }
    }

    if (bShowInput)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (getUIHandler ().createFormSelf (aWPEC)
                                                                              .setMethod (EHCFormMethod.GET)
                                                                              .setLeft (3));
      aForm.addChild (new BootstrapInfoBox ().addChildren (new HCDiv ().addChild ("Show all processes, document types and endpoints of a participant."),
                                                           new HCDiv ().addChild ("You may want to try scheme ")
                                                                       .addChild (new HCCode ().addChild (DEFAULT_ID_SCHEME))
                                                                       .addChild (" and value ")
                                                                       .addChild (new HCCode ().addChild ("9915:test"))
                                                                       .addChild (" on ")
                                                                       .addChild (new HCCode ().addChild ("SMK"))
                                                                       .addChild (" as an example.")));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier scheme")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_ID_SCHEME,
                                                                                           sParticipantIDScheme)).setMaxLength (PeppolIdentifierHelper.MAX_IDENTIFIER_SCHEME_LENGTH)
                                                                                                                 .setPlaceholder ("Identifier scheme"))
                                                   .setHelpText (new HCDiv ().addChild ("The most common identifier scheme is ")
                                                                             .addChild (new HCCode ().addChild (DEFAULT_ID_SCHEME)))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_ID_SCHEME)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier value")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_ID_VALUE,
                                                                                           sParticipantIDValue)).setMaxLength (PeppolIdentifierHelper.MAX_PARTICIPANT_VALUE_LENGTH)
                                                                                                                .setPlaceholder ("Identifier value"))
                                                   .setHelpText (new HCDiv ().addChild ("The identifier value must look like ")
                                                                             .addChild (new HCCode ().addChild ("9915:test")))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_ID_VALUE)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML to use")
                                                   .setCtrl (new SMLSelect (new RequestField (FIELD_SML,
                                                                                              SMLSelect.FIELD_AUTO_SELECT),
                                                                            true))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_SML)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Query Business Card?")
                                                   .setCtrl (new HCCheckBox (new RequestFieldBoolean (PARAM_QUERY_BUSINESS_CARD,
                                                                                                      DEFAULT_QUERY_BUSINESS_CARD)))
                                                   .setErrorList (aFormErrors.getListOfField (PARAM_QUERY_BUSINESS_CARD)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Show query duration?")
                                                   .setCtrl (new HCCheckBox (new RequestFieldBoolean (PARAM_SHOW_TIME,
                                                                                                      DEFAULT_SHOW_TIME)))
                                                   .setErrorList (aFormErrors.getListOfField (PARAM_SHOW_TIME)));

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
      aToolbar.addSubmitButton ("Show details");
    }
  }

  private static void _printEndpointURL (@Nonnull final IHCLI <?> aLIEndpoint, final String sEndpointRef)
  {
    aLIEndpoint.addChild (new HCDiv ().addChild ("Endpoint URL: ")
                                      .addChild (StringHelper.hasNoText (sEndpointRef) ? new HCEM ().addChild ("none")
                                                                                       : new HCCode ().addChild (sEndpointRef)));
  }

  private static void _printActivationDate (@Nonnull final IHCLI <?> aLIEndpoint,
                                            final LocalDateTime aServiceActivationDate,
                                            final Locale aDisplayLocale)
  {
    if (aServiceActivationDate != null)
    {
      final LocalDate aNowDate = PDTFactory.getCurrentLocalDate ();
      final LocalDate aValidFrom = aServiceActivationDate.toLocalDate ();
      aLIEndpoint.addChild (new HCDiv ().addChild ("Valid from: " +
                                                   PDTToString.getAsString (aValidFrom, aDisplayLocale)));
      if (aValidFrom.isAfter (aNowDate))
        aLIEndpoint.addChild (new BootstrapErrorBox ().addChild ("This endpoint is not yet valid!"));
    }
  }

  private static void _printExpirationDate (@Nonnull final IHCLI <?> aLIEndpoint,
                                            final LocalDateTime aServiceExpirationDate,
                                            final Locale aDisplayLocale)
  {
    if (aServiceExpirationDate != null)
    {
      final LocalDate aNowDate = PDTFactory.getCurrentLocalDate ();
      final LocalDate aValidTo = PDTFactory.createLocalDate (aServiceExpirationDate);
      aLIEndpoint.addChild (new HCDiv ().addChild ("Valid to: " + PDTToString.getAsString (aValidTo, aDisplayLocale)));
      if (aValidTo.isBefore (aNowDate))
        aLIEndpoint.addChild (new BootstrapErrorBox ().addChild ("This endpoint is no longer valid!"));
    }
  }

  private static void _printTransportProfile (@Nonnull final IHCLI <?> aLIEndpoint,
                                              @Nullable final String sTransportProfile)
  {
    final HCDiv aDiv = new HCDiv ().addChild ("Transport profile: ");
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.getFromIDOrNull (sTransportProfile);
    if (eTransportProfile != null)
    {
      // Known transport profile
      aDiv.addChild (new BootstrapBadge (EBootstrapBadgeType.SUCCESS).addChild (eTransportProfile.getName ()))
          .addChild (new HCSmall ().addChild (" (")
                                   .addChild (new HCCode ().addChild (sTransportProfile))
                                   .addChild (")"));
    }
    else
    {
      aDiv.addChild (new HCCode ().addChild (sTransportProfile))
          .addChild (" ")
          .addChild (new BootstrapBadge (EBootstrapBadgeType.WARNING).addChild ("non-standard"));
    }
    aLIEndpoint.addChild (aDiv);
  }

  private static void _printTecInfo (@Nonnull final IHCLI <?> aLIEndpoint, final String sTecInfo)
  {
    if (StringHelper.hasText (sTecInfo))
    {
      final boolean bIsEmail = EmailAddressHelper.isValid (sTecInfo);
      aLIEndpoint.addChild (new HCDiv ().addChild ("Technical info: ")
                                        .addChild (bIsEmail ? HCA_MailTo.createLinkedEmail (sTecInfo)
                                                            : new HCTextNode (sTecInfo)));
    }
  }

}
