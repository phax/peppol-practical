/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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
package com.helger.peppol.pub.rest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nonnull;
import javax.xml.datatype.XMLGregorianCalendar;

import com.helger.datetime.util.PDTXMLConverter;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.peppol.bdxr.smp1.BDXR1ExtensionConverter;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.smp.SMPExtensionConverter;
import com.helger.peppol.utils.W3CEndpointReferenceHelper;
import com.helger.peppolid.CIdentifier;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;

public class SMPJsonResponse
{
  private static final String JSON_SMPTYPE = "smptype";
  private static final String JSON_PARTICIPANT_ID = "participantID";
  private static final String JSON_DOCUMENT_TYPE_ID = "documentTypeID";
  private static final String JSON_HREF = "href";
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
  public static IJsonObject convert (@Nonnull final IParticipantIdentifier aParticipantID,
                                     @Nonnull final IDocumentTypeIdentifier aDocTypeID,
                                     @Nonnull final com.helger.peppol.smp.ServiceMetadataType aSM)
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (JSON_SMPTYPE, ESMPAPIType.PEPPOL.getID ());
    ret.add (JSON_PARTICIPANT_ID, aParticipantID.getURIEncoded ());
    ret.add (JSON_DOCUMENT_TYPE_ID, aDocTypeID.getURIEncoded ());

    final com.helger.peppol.smp.RedirectType aRedirect = aSM.getRedirect ();
    if (aRedirect != null)
    {
      final IJsonObject aJsonRedirect = new JsonObject ().add (JSON_HREF, aRedirect.getHref ())
                                                         .add (JSON_CERTIFICATE_UID, aRedirect.getCertificateUID ())
                                                         .add (JSON_EXTENSION,
                                                               SMPExtensionConverter.convertToString (aRedirect.getExtension ()));
      ret.add (JSON_REDIRECT, aJsonRedirect);
    }
    else
    {
      final com.helger.peppol.smp.ServiceInformationType aSI = aSM.getServiceInformation ();
      final IJsonObject aJsonSI = new JsonObject ();
      {
        final IJsonArray aJsonProcs = new JsonArray ();
        // For all processes
        if (aSI.getProcessList () != null)
          for (final com.helger.peppol.smp.ProcessType aProcess : aSI.getProcessList ().getProcess ())
            if (aProcess.getProcessIdentifier () != null)
            {
              final IJsonObject aJsonProc = new JsonObject ().add (JSON_PROCESS_ID,
                                                                   CIdentifier.getURIEncoded (aProcess.getProcessIdentifier ()));
              final IJsonArray aJsonEPs = new JsonArray ();
              // For all endpoints
              if (aProcess.getServiceEndpointList () != null)
                for (final com.helger.peppol.smp.EndpointType aEndpoint : aProcess.getServiceEndpointList ()
                                                                                  .getEndpoint ())
                {
                  final String sEndpointRef = aEndpoint.getEndpointReference () == null ? null
                                                                                        : W3CEndpointReferenceHelper.getAddress (aEndpoint.getEndpointReference ());
                  final IJsonObject aJsonEP = new JsonObject ().add (JSON_TRANSPORT_PROFILE,
                                                                     aEndpoint.getTransportProfile ())
                                                               .add (JSON_ENDPOINT_REFERENCE, sEndpointRef)
                                                               .add (JSON_REQUIRE_BUSINESS_LEVEL_SIGNATURE,
                                                                     aEndpoint.isRequireBusinessLevelSignature ())
                                                               .add (JSON_MINIMUM_AUTHENTICATION_LEVEL,
                                                                     aEndpoint.getMinimumAuthenticationLevel ());

                  final LocalDateTime aServiceActivationDate = aEndpoint.getServiceActivationDate ();
                  if (aServiceActivationDate != null)
                    aJsonEP.add (JSON_SERVICE_ACTIVATION_DATE,
                                 DateTimeFormatter.ISO_LOCAL_DATE_TIME.format (aServiceActivationDate));

                  final LocalDateTime aServiceExpirationDate = aEndpoint.getServiceExpirationDate ();
                  if (aServiceExpirationDate != null)
                    aJsonEP.add (JSON_SERVICE_EXPIRATION_DATE,
                                 DateTimeFormatter.ISO_LOCAL_DATE_TIME.format (aServiceExpirationDate));

                  aJsonEP.add (JSON_CERTIFICATE, aEndpoint.getCertificate ())
                         .add (JSON_SERVICE_DESCRIPTION, aEndpoint.getServiceDescription ())
                         .add (JSON_TECHNICAL_CONTACT_URL, aEndpoint.getTechnicalContactUrl ())
                         .add (JSON_TECHNICAL_INFORMATION_URL, aEndpoint.getTechnicalInformationUrl ())
                         .add (JSON_EXTENSION, SMPExtensionConverter.convertToString (aEndpoint.getExtension ()));

                  aJsonEPs.add (aJsonEP);
                }
              aJsonProc.add (JSON_ENDPOINTS, aJsonEPs)
                       .add (JSON_EXTENSION, SMPExtensionConverter.convertToString (aProcess.getExtension ()));
              aJsonProcs.add (aJsonProc);
            }
        aJsonSI.add (JSON_PROCESSES, aJsonProcs)
               .add (JSON_EXTENSION, SMPExtensionConverter.convertToString (aSI.getExtension ()));
      }
      ret.add (JSON_SERVICEINFO, aJsonSI);
    }
    return ret;
  }

  @Nonnull
  public static IJsonObject convert (@Nonnull final IParticipantIdentifier aParticipantID,
                                     @Nonnull final IDocumentTypeIdentifier aDocTypeID,
                                     @Nonnull final com.helger.xsds.bdxr.smp1.ServiceMetadataType aSM)
  {
    final IJsonObject ret = new JsonObject ();
    ret.add (JSON_SMPTYPE, ESMPAPIType.OASIS_BDXR_V1.getID ());
    ret.add (JSON_PARTICIPANT_ID, aParticipantID.getURIEncoded ());
    ret.add (JSON_DOCUMENT_TYPE_ID, aDocTypeID.getURIEncoded ());

    final com.helger.xsds.bdxr.smp1.RedirectType aRedirect = aSM.getRedirect ();
    if (aRedirect != null)
    {
      final IJsonObject aJsonRedirect = new JsonObject ().add (JSON_HREF, aRedirect.getHref ())
                                                         .add (JSON_CERTIFICATE_UID, aRedirect.getCertificateUID ())
                                                         .add (JSON_EXTENSION,
                                                               BDXR1ExtensionConverter.convertToJson (aRedirect.getExtension ()));
      ret.add (JSON_REDIRECT, aJsonRedirect);
    }
    else
    {
      final com.helger.xsds.bdxr.smp1.ServiceInformationType aSI = aSM.getServiceInformation ();
      final IJsonObject aJsonSI = new JsonObject ();
      {
        final IJsonArray aJsonProcs = new JsonArray ();
        // For all processes
        if (aSI.getProcessList () != null)
          for (final com.helger.xsds.bdxr.smp1.ProcessType aProcess : aSI.getProcessList ().getProcess ())
            if (aProcess.getProcessIdentifier () != null)
            {
              final IJsonObject aJsonProc = new JsonObject ().add (JSON_PROCESS_ID,
                                                                   CIdentifier.getURIEncoded (aProcess.getProcessIdentifier ()));
              final IJsonArray aJsonEPs = new JsonArray ();
              // For all endpoints
              if (aProcess.getServiceEndpointList () != null)
                for (final com.helger.xsds.bdxr.smp1.EndpointType aEndpoint : aProcess.getServiceEndpointList ()
                                                                                      .getEndpoint ())
                {
                  final IJsonObject aJsonEP = new JsonObject ().add (JSON_TRANSPORT_PROFILE,
                                                                     aEndpoint.getTransportProfile ())
                                                               .add (JSON_ENDPOINT_REFERENCE,
                                                                     aEndpoint.getEndpointURI ())
                                                               .add (JSON_REQUIRE_BUSINESS_LEVEL_SIGNATURE,
                                                                     aEndpoint.isRequireBusinessLevelSignature ())
                                                               .add (JSON_MINIMUM_AUTHENTICATION_LEVEL,
                                                                     aEndpoint.getMinimumAuthenticationLevel ());

                  final XMLGregorianCalendar aServiceActivationDate = aEndpoint.getServiceActivationDate ();
                  if (aServiceActivationDate != null)
                    aJsonEP.add (JSON_SERVICE_ACTIVATION_DATE,
                                 DateTimeFormatter.ISO_LOCAL_DATE_TIME.format (PDTXMLConverter.getLocalDateTime (aServiceActivationDate)));

                  final XMLGregorianCalendar aServiceExpirationDate = aEndpoint.getServiceExpirationDate ();
                  if (aServiceExpirationDate != null)
                    aJsonEP.add (JSON_SERVICE_EXPIRATION_DATE,
                                 DateTimeFormatter.ISO_LOCAL_DATE_TIME.format (PDTXMLConverter.getLocalDateTime (aServiceExpirationDate)));

                  aJsonEP.add (JSON_CERTIFICATE, aEndpoint.getCertificate ())
                         .add (JSON_SERVICE_DESCRIPTION, aEndpoint.getServiceDescription ())
                         .add (JSON_TECHNICAL_CONTACT_URL, aEndpoint.getTechnicalContactUrl ())
                         .add (JSON_TECHNICAL_INFORMATION_URL, aEndpoint.getTechnicalInformationUrl ())
                         .add (JSON_EXTENSION, BDXR1ExtensionConverter.convertToJson (aEndpoint.getExtension ()));

                  aJsonEPs.add (aJsonEP);
                }
              aJsonProc.add (JSON_ENDPOINTS, aJsonEPs)
                       .add (JSON_EXTENSION, BDXR1ExtensionConverter.convertToJson (aProcess.getExtension ()));
              aJsonProcs.add (aJsonProc);
            }
        aJsonSI.add (JSON_PROCESSES, aJsonProcs)
               .add (JSON_EXTENSION, BDXR1ExtensionConverter.convertToJson (aSI.getExtension ()));
      }
      ret.add (JSON_SERVICEINFO, aJsonSI);
    }
    return ret;
  }
}
