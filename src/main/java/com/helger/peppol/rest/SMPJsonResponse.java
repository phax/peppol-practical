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
package com.helger.peppol.rest;

import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import com.helger.commons.base64.Base64;
import com.helger.commons.datetime.PDTFactory;
import com.helger.datetime.util.PDTXMLConverter;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.peppol.domain.NiceNameEntry;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.ui.AppCommonUI;
import com.helger.peppolid.CIdentifier;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.security.certificate.CertificateHelper;
import com.helger.smpclient.bdxr1.utils.BDXR1ExtensionConverter;
import com.helger.smpclient.peppol.utils.SMPExtensionConverter;
import com.helger.smpclient.peppol.utils.W3CEndpointReferenceHelper;
import com.helger.xsds.bdxr.smp1.EndpointType;
import com.helger.xsds.bdxr.smp1.ProcessType;
import com.helger.xsds.bdxr.smp1.RedirectType;
import com.helger.xsds.bdxr.smp1.ServiceInformationType;
import com.helger.xsds.bdxr.smp1.ServiceMetadataType;

@Immutable
public final class SMPJsonResponse
{
  private static final String JSON_SMPTYPE = "smptype";
  private static final String JSON_PARTICIPANT_ID = "participantID";
  private static final String JSON_HREF = "href";
  private static final String JSON_DOCUMENT_TYPE_ID = "documentTypeID";
  private static final String JSON_NICE_NAME = "niceName";
  private static final String JSON_IS_DEPRECATED = "isDeprecated";
  private static final String JSON_ERROR = "error";
  private static final String JSON_URLS = "urls";

  private static final String JSON_CERTIFICATE_UID = "certificateUID";
  private static final String JSON_REDIRECT = "redirect";
  private static final String JSON_PROCESS_ID = "processID";
  private static final String JSON_TRANSPORT_PROFILE = "transportProfile";
  private static final String JSON_ENDPOINT_REFERENCE = "endpointReference";
  private static final String JSON_REQUIRE_BUSINESS_LEVEL_SIGNATURE = "requireBusinessLevelSignature";
  private static final String JSON_MINIMUM_AUTHENTICATION_LEVEL = "minimumAuthenticationLevel";
  private static final String JSON_SERVICE_ACTIVATION_DATE = "serviceActivationDate";
  private static final String JSON_SERVICE_EXPIRATION_DATE = "serviceExpirationDate";
  private static final String JSON_CERTIFICATE = "certificate";
  private static final String JSON_CERTIFICATE_DETAILS = "certificateDetails";
  private static final String JSON_SERVICE_DESCRIPTION = "serviceDescription";
  private static final String JSON_TECHNICAL_CONTACT_URL = "technicalContactUrl";
  private static final String JSON_TECHNICAL_INFORMATION_URL = "technicalInformationUrl";
  private static final String JSON_ENDPOINTS = "endpoints";
  private static final String JSON_PROCESSES = "processes";
  private static final String JSON_EXTENSION = "extension";
  private static final String JSON_SERVICEINFO = "serviceinfo";

  private SMPJsonResponse ()
  {}

  @Nonnull
  public static IJsonObject convert (@Nonnull final ESMPAPIType eSMPAPIType,
                                     @Nonnull final IParticipantIdentifier aParticipantID,
                                     @Nonnull final Map <String, String> aSGHrefs,
                                     @Nonnull final IIdentifierFactory aIF)
  {
    final IJsonObject aJson = new JsonObject ();
    aJson.add (JSON_SMPTYPE, eSMPAPIType.getID ());
    aJson.add (JSON_PARTICIPANT_ID, aParticipantID.getURIEncoded ());

    final String sPathStart = "/" + aParticipantID.getURIEncoded () + "/services/";
    final IJsonArray aURLsArray = new JsonArray ();
    // Show all ServiceGroup hrefs
    for (final Map.Entry <String, String> aEntry : aSGHrefs.entrySet ())
    {
      final String sHref = aEntry.getKey ();
      final String sOriginalHref = aEntry.getValue ();

      final IJsonObject aUrlEntry = new JsonObject ().add (JSON_HREF, sOriginalHref);
      // Should be case insensitive "indexOf" here
      final int nPathStart = sHref.toLowerCase (Locale.US).indexOf (sPathStart.toLowerCase (Locale.US));
      if (nPathStart >= 0)
      {
        final String sDocType = sHref.substring (nPathStart + sPathStart.length ());
        aUrlEntry.add (JSON_DOCUMENT_TYPE_ID, sDocType);
        final IDocumentTypeIdentifier aDocType = aIF.parseDocumentTypeIdentifier (sDocType);
        if (aDocType != null)
        {
          final NiceNameEntry aNN = AppCommonUI.getDocTypeNames ().get (sDocType);
          if (aNN != null)
          {
            aUrlEntry.add (JSON_NICE_NAME, aNN.getName ());
            aUrlEntry.add (JSON_IS_DEPRECATED, aNN.isDeprecated ());
          }
        }
        else
        {
          aUrlEntry.add (JSON_ERROR, "The document type ID could not be interpreted as a structured document type!");
        }
      }
      else
      {
        aUrlEntry.add (JSON_ERROR, "Contained href does not match the rules. Expected path part: '" + sPathStart + "'");
      }
      aURLsArray.add (aUrlEntry);
    }
    aJson.addJson (JSON_URLS, aURLsArray);
    return aJson;
  }

  @Nonnull
  private static IJsonObject _getJsonPrincipal (@Nonnull final X500Principal aPrincipal)
  {
    final IJsonObject ret = new JsonObject ();
    ret.add ("name", aPrincipal.getName ());
    try
    {
      for (final Rdn aRdn : new LdapName (aPrincipal.getName ()).getRdns ())
        ret.add (aRdn.getType (), aRdn.getValue ());
    }
    catch (final InvalidNameException ex)
    {
      // shit happens
    }
    return ret;
  }

  @Nullable
  private static String _getLDT (@Nullable final LocalDateTime aLDT)
  {
    return aLDT == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format (aLDT);
  }

  private static void _convertCertificate (@Nonnull final IJsonObject aTarget, @Nonnull final String sCert)
  {
    aTarget.add (JSON_CERTIFICATE, sCert);

    final X509Certificate aCert = CertificateHelper.convertStringToCertficateOrNull (sCert);
    final IJsonObject aDetails = new JsonObject ();
    aDetails.add ("parsable", aCert != null);
    if (aCert != null)
    {
      aDetails.addJson ("subject", _getJsonPrincipal (aCert.getSubjectX500Principal ()));
      aDetails.addJson ("issuer", _getJsonPrincipal (aCert.getIssuerX500Principal ()));
      aDetails.add ("serial10", aCert.getSerialNumber ());
      aDetails.add ("serial16", aCert.getSerialNumber ().toString (16));
      aDetails.addIfNotNull ("notBefore", _getLDT (PDTFactory.createLocalDateTime (aCert.getNotBefore ())));
      aDetails.addIfNotNull ("notAfter", _getLDT (PDTFactory.createLocalDateTime (aCert.getNotAfter ())));
      aDetails.add ("validByDate", CertificateHelper.isCertificateValidPerNow (aCert));
      aDetails.add ("sigAlgName", aCert.getSigAlgName ());
    }
    aTarget.addJson (JSON_CERTIFICATE_DETAILS, aDetails);
  }

  @Nonnull
  public static IJsonObject convert (@Nonnull final IParticipantIdentifier aParticipantID,
                                     @Nonnull final IDocumentTypeIdentifier aDocTypeID,
                                     @Nonnull final com.helger.smpclient.peppol.jaxb.ServiceMetadataType aSM)
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (JSON_SMPTYPE, ESMPAPIType.PEPPOL.getID ());
    ret.add (JSON_PARTICIPANT_ID, aParticipantID.getURIEncoded ());
    ret.add (JSON_DOCUMENT_TYPE_ID, aDocTypeID.getURIEncoded ());

    final com.helger.smpclient.peppol.jaxb.RedirectType aRedirect = aSM.getRedirect ();
    if (aRedirect != null)
    {
      final IJsonObject aJsonRedirect = new JsonObject ().add (JSON_HREF, aRedirect.getHref ())
                                                         .add (JSON_CERTIFICATE_UID, aRedirect.getCertificateUID ())
                                                         .add (JSON_EXTENSION,
                                                               SMPExtensionConverter.convertToString (aRedirect.getExtension ()));
      ret.addJson (JSON_REDIRECT, aJsonRedirect);
    }
    else
    {
      final com.helger.smpclient.peppol.jaxb.ServiceInformationType aSI = aSM.getServiceInformation ();
      final IJsonObject aJsonSI = new JsonObject ();
      {
        final IJsonArray aJsonProcs = new JsonArray ();
        // For all processes
        if (aSI.getProcessList () != null)
          for (final com.helger.smpclient.peppol.jaxb.ProcessType aProcess : aSI.getProcessList ().getProcess ())
            if (aProcess.getProcessIdentifier () != null)
            {
              final IJsonObject aJsonProc = new JsonObject ().add (JSON_PROCESS_ID,
                                                                   CIdentifier.getURIEncoded (aProcess.getProcessIdentifier ()));
              final IJsonArray aJsonEPs = new JsonArray ();
              // For all endpoints
              if (aProcess.getServiceEndpointList () != null)
                for (final com.helger.smpclient.peppol.jaxb.EndpointType aEndpoint : aProcess.getServiceEndpointList ().getEndpoint ())
                {
                  final String sEndpointRef = aEndpoint.getEndpointReference () == null ? null
                                                                                        : W3CEndpointReferenceHelper.getAddress (aEndpoint.getEndpointReference ());
                  final IJsonObject aJsonEP = new JsonObject ().add (JSON_TRANSPORT_PROFILE, aEndpoint.getTransportProfile ())
                                                               .add (JSON_ENDPOINT_REFERENCE, sEndpointRef)
                                                               .add (JSON_REQUIRE_BUSINESS_LEVEL_SIGNATURE,
                                                                     aEndpoint.isRequireBusinessLevelSignature ())
                                                               .add (JSON_MINIMUM_AUTHENTICATION_LEVEL,
                                                                     aEndpoint.getMinimumAuthenticationLevel ());

                  aJsonEP.addIfNotNull (JSON_SERVICE_ACTIVATION_DATE, _getLDT (aEndpoint.getServiceActivationDate ()));
                  aJsonEP.addIfNotNull (JSON_SERVICE_EXPIRATION_DATE, _getLDT (aEndpoint.getServiceExpirationDate ()));
                  _convertCertificate (aJsonEP, aEndpoint.getCertificate ());
                  aJsonEP.add (JSON_SERVICE_DESCRIPTION, aEndpoint.getServiceDescription ())
                         .add (JSON_TECHNICAL_CONTACT_URL, aEndpoint.getTechnicalContactUrl ())
                         .add (JSON_TECHNICAL_INFORMATION_URL, aEndpoint.getTechnicalInformationUrl ())
                         .add (JSON_EXTENSION, SMPExtensionConverter.convertToString (aEndpoint.getExtension ()));

                  aJsonEPs.add (aJsonEP);
                }
              aJsonProc.addJson (JSON_ENDPOINTS, aJsonEPs)
                       .add (JSON_EXTENSION, SMPExtensionConverter.convertToString (aProcess.getExtension ()));
              aJsonProcs.add (aJsonProc);
            }
        aJsonSI.addJson (JSON_PROCESSES, aJsonProcs).add (JSON_EXTENSION, SMPExtensionConverter.convertToString (aSI.getExtension ()));
      }
      ret.addJson (JSON_SERVICEINFO, aJsonSI);
    }
    return ret;
  }

  @Nonnull
  public static IJsonObject convert (@Nonnull final IParticipantIdentifier aParticipantID,
                                     @Nonnull final IDocumentTypeIdentifier aDocTypeID,
                                     @Nonnull final ServiceMetadataType aSM)
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (JSON_SMPTYPE, ESMPAPIType.OASIS_BDXR_V1.getID ());
    ret.add (JSON_PARTICIPANT_ID, aParticipantID.getURIEncoded ());
    ret.add (JSON_DOCUMENT_TYPE_ID, aDocTypeID.getURIEncoded ());

    final RedirectType aRedirect = aSM.getRedirect ();
    if (aRedirect != null)
    {
      final IJsonObject aJsonRedirect = new JsonObject ().add (JSON_HREF, aRedirect.getHref ())
                                                         .add (JSON_CERTIFICATE_UID, aRedirect.getCertificateUID ())
                                                         .addIfNotNull (JSON_EXTENSION,
                                                                        BDXR1ExtensionConverter.convertToJson (aRedirect.getExtension ()));
      ret.addJson (JSON_REDIRECT, aJsonRedirect);
    }
    else
    {
      final ServiceInformationType aSI = aSM.getServiceInformation ();
      final IJsonObject aJsonSI = new JsonObject ();
      {
        final IJsonArray aJsonProcs = new JsonArray ();
        // For all processes
        if (aSI.getProcessList () != null)
          for (final ProcessType aProcess : aSI.getProcessList ().getProcess ())
            if (aProcess.getProcessIdentifier () != null)
            {
              final IJsonObject aJsonProc = new JsonObject ().add (JSON_PROCESS_ID,
                                                                   CIdentifier.getURIEncoded (aProcess.getProcessIdentifier ()));
              final IJsonArray aJsonEPs = new JsonArray ();
              // For all endpoints
              if (aProcess.getServiceEndpointList () != null)
                for (final EndpointType aEndpoint : aProcess.getServiceEndpointList ().getEndpoint ())
                {
                  final IJsonObject aJsonEP = new JsonObject ().add (JSON_TRANSPORT_PROFILE, aEndpoint.getTransportProfile ())
                                                               .add (JSON_ENDPOINT_REFERENCE, aEndpoint.getEndpointURI ())
                                                               .add (JSON_REQUIRE_BUSINESS_LEVEL_SIGNATURE,
                                                                     aEndpoint.isRequireBusinessLevelSignature ())
                                                               .add (JSON_MINIMUM_AUTHENTICATION_LEVEL,
                                                                     aEndpoint.getMinimumAuthenticationLevel ());

                  aJsonEP.addIfNotNull (JSON_SERVICE_ACTIVATION_DATE,
                                        _getLDT (PDTXMLConverter.getLocalDateTime (aEndpoint.getServiceActivationDate ())));
                  aJsonEP.addIfNotNull (JSON_SERVICE_EXPIRATION_DATE,
                                        _getLDT (PDTXMLConverter.getLocalDateTime (aEndpoint.getServiceExpirationDate ())));
                  _convertCertificate (aJsonEP, Base64.encodeBytes (aEndpoint.getCertificate ()));
                  aJsonEP.add (JSON_SERVICE_DESCRIPTION, aEndpoint.getServiceDescription ())
                         .add (JSON_TECHNICAL_CONTACT_URL, aEndpoint.getTechnicalContactUrl ())
                         .add (JSON_TECHNICAL_INFORMATION_URL, aEndpoint.getTechnicalInformationUrl ())
                         .addIfNotNull (JSON_EXTENSION, BDXR1ExtensionConverter.convertToJson (aEndpoint.getExtension ()));

                  aJsonEPs.add (aJsonEP);
                }
              aJsonProc.addJson (JSON_ENDPOINTS, aJsonEPs)
                       .addIfNotNull (JSON_EXTENSION, BDXR1ExtensionConverter.convertToJson (aProcess.getExtension ()));
              aJsonProcs.add (aJsonProc);
            }
        aJsonSI.addJson (JSON_PROCESSES, aJsonProcs)
               .addIfNotNull (JSON_EXTENSION, BDXR1ExtensionConverter.convertToJson (aSI.getExtension ()));
      }
      ret.addJson (JSON_SERVICEINFO, aJsonSI);
    }
    return ret;
  }
}
