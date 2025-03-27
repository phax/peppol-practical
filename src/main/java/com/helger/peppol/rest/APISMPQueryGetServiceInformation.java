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
package com.helger.peppol.rest;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.hc.client5.http.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.http.CHttp;
import com.helger.commons.mime.CMimeType;
import com.helger.commons.timing.StopWatch;
import com.helger.json.IJsonObject;
import com.helger.json.serialize.JsonWriter;
import com.helger.json.serialize.JsonWriterSettings;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.sharedui.domain.ISMLConfiguration;
import com.helger.peppol.sharedui.domain.SMPQueryParams;
import com.helger.peppol.sharedui.mgr.ISMLConfigurationManager;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.photon.api.IAPIDescriptor;
import com.helger.servlet.response.UnifiedResponse;
import com.helger.smpclient.bdxr1.BDXRClientReadOnly;
import com.helger.smpclient.bdxr2.BDXR2ClientReadOnly;
import com.helger.smpclient.json.SMPJsonResponse;
import com.helger.smpclient.peppol.PeppolWildcardSelector.EMode;
import com.helger.smpclient.peppol.Pfuoi420;
import com.helger.smpclient.peppol.SMPClientReadOnly;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

public final class APISMPQueryGetServiceInformation extends AbstractRateLimitingAPIExecutor
{
  public static final String PARAM_VERIFY_SIGNATURE = "verifySignature";
  public static final String PARAM_XML_SCHEMA_VALIDATION = "xmlSchemaValidation";

  private static final Logger LOGGER = LoggerFactory.getLogger (APISMPQueryGetServiceInformation.class);

  @Override
  protected void rateLimitedInvokeAPI (@Nonnull final IAPIDescriptor aAPIDescriptor,
                                       @Nonnull @Nonempty final String sPath,
                                       @Nonnull final Map <String, String> aPathVariables,
                                       @Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                                       @Nonnull final UnifiedResponse aUnifiedResponse) throws Exception
  {
    final ISMLConfigurationManager aSMLConfigurationMgr = PPMetaManager.getSMLConfigurationMgr ();
    final String sSMLID = aPathVariables.get (PPAPI.PARAM_SML_ID);
    final boolean bSMLAutoDetect = ISMLConfigurationManager.ID_AUTO_DETECT.equals (sSMLID);
    ISMLConfiguration aSML = aSMLConfigurationMgr.getSMLInfoOfID (sSMLID);
    if (aSML == null && !bSMLAutoDetect)
      throw new APIParamException ("Unsupported SML ID '" + sSMLID + "' provided.");

    final String sParticipantID = aPathVariables.get (PPAPI.PARAM_PARTICIPANT_ID);
    final IParticipantIdentifier aPID = SimpleIdentifierFactory.INSTANCE.parseParticipantIdentifier (sParticipantID);
    if (aPID == null)
      throw new APIParamException ("Invalid participant ID '" + sParticipantID + "' provided.");

    final String sDocTypeID = aPathVariables.get (PPAPI.PARAM_DOCTYPE_ID);
    final IDocumentTypeIdentifier aDTID = SimpleIdentifierFactory.INSTANCE.parseDocumentTypeIdentifier (sDocTypeID);
    if (aDTID == null)
      throw new APIParamException ("Invalid document type ID '" + sDocTypeID + "' provided.");

    final boolean bXMLSchemaValidation = aRequestScope.params ().getAsBoolean (PARAM_XML_SCHEMA_VALIDATION, true);
    final boolean bVerifySignature = aRequestScope.params ().getAsBoolean (PARAM_VERIFY_SIGNATURE, true);

    final ZonedDateTime aQueryDT = PDTFactory.getCurrentZonedDateTimeUTC ();
    final StopWatch aSW = StopWatch.createdStarted ();

    SMPQueryParams aSMPQueryParams = null;
    if (bSMLAutoDetect)
    {
      for (final ISMLConfiguration aCurSML : aSMLConfigurationMgr.getAllSorted ())
      {
        aSMPQueryParams = SMPQueryParams.createForSMLOrNull (aCurSML, aPID.getScheme (), aPID.getValue (), false);
        if (aSMPQueryParams != null && aSMPQueryParams.isSMPRegisteredInDNS ())
        {
          // Found it
          aSML = aCurSML;
          break;
        }
      }

      // Ensure to go into the exception handler
      if (aSML == null)
        throw new HttpResponseException (CHttp.HTTP_NOT_FOUND,
                                         "The participant identifier '" +
                                                               sParticipantID +
                                                               "' could not be found in any SML.");
    }
    else
    {
      aSMPQueryParams = SMPQueryParams.createForSMLOrNull (aSML, aPID.getScheme (), aPID.getValue (), true);
    }
    if (aSMPQueryParams == null)
      throw new APIParamException ("Failed to resolve participant ID '" +
                                   sParticipantID +
                                   "' for the provided SML '" +
                                   aSML.getID () +
                                   "'");

    final IParticipantIdentifier aParticipantID = aSMPQueryParams.getParticipantID ();
    final IDocumentTypeIdentifier aDocTypeID = aSMPQueryParams.getIF ()
                                                              .createDocumentTypeIdentifier (aDTID.getScheme (),
                                                                                             aDTID.getValue ());
    if (aDocTypeID == null)
      throw new APIParamException ("Invalid document type ID '" + sDocTypeID + "' provided.");

    LOGGER.info ("[API] Participant information of '" +
                 aParticipantID.getURIEncoded () +
                 "' is queried using SMP API '" +
                 aSMPQueryParams.getSMPAPIType () +
                 "' from '" +
                 aSMPQueryParams.getSMPHostURI () +
                 "' using SML '" +
                 aSML.getID () +
                 "' for document type '" +
                 aDocTypeID.getURIEncoded () +
                 "'; XSD validation=" +
                 bXMLSchemaValidation +
                 "; signature verification=" +
                 bVerifySignature);

    IJsonObject aJson = null;
    switch (aSMPQueryParams.getSMPAPIType ())
    {
      case PEPPOL:
      {
        final SMPClientReadOnly aSMPClient = new SMPClientReadOnly (aSMPQueryParams.getSMPHostURI ());
        aSMPClient.setSecureValidation (false);
        aSMPClient.withHttpClientSettings (SMP_HCS_MODIFIER);
        aSMPClient.setXMLSchemaValidation (bXMLSchemaValidation);
        aSMPClient.setVerifySignature (bVerifySignature);

        @Pfuoi420
        final com.helger.xsds.peppol.smp1.SignedServiceMetadataType aSSM;
        if (PeppolIdentifierHelper.DOCUMENT_TYPE_SCHEME_PEPPOL_DOCTYPE_WILDCARD.equals (aDocTypeID.getScheme ()))
          aSSM = aSMPClient.getWildcardServiceMetadataOrNull (aParticipantID, aDocTypeID, EMode.BUSDOX_THEN_WILDCARD);
        else
          aSSM = aSMPClient.getServiceMetadataOrNull (aParticipantID, aDocTypeID);
        if (aSSM != null)
        {
          final com.helger.xsds.peppol.smp1.ServiceMetadataType aSM = aSSM.getServiceMetadata ();
          aJson = SMPJsonResponse.convert (aParticipantID, aDocTypeID, aSM);
        }
        break;
      }
      case OASIS_BDXR_V1:
      {
        final BDXRClientReadOnly aBDXR1Client = new BDXRClientReadOnly (aSMPQueryParams.getSMPHostURI ());
        aBDXR1Client.setSecureValidation (false);
        aBDXR1Client.withHttpClientSettings (SMP_HCS_MODIFIER);
        aBDXR1Client.setXMLSchemaValidation (bXMLSchemaValidation);
        aBDXR1Client.setVerifySignature (bVerifySignature);

        final com.helger.xsds.bdxr.smp1.SignedServiceMetadataType aSSM = aBDXR1Client.getServiceMetadataOrNull (aParticipantID,
                                                                                                                aDocTypeID);
        if (aSSM != null)
        {
          final com.helger.xsds.bdxr.smp1.ServiceMetadataType aSM = aSSM.getServiceMetadata ();
          aJson = SMPJsonResponse.convert (aParticipantID, aDocTypeID, aSM);
        }
        break;
      }
      case OASIS_BDXR_V2:
      {
        final BDXR2ClientReadOnly aBDXR2Client = new BDXR2ClientReadOnly (aSMPQueryParams.getSMPHostURI ());
        aBDXR2Client.setSecureValidation (false);
        aBDXR2Client.withHttpClientSettings (SMP_HCS_MODIFIER);
        aBDXR2Client.setXMLSchemaValidation (bXMLSchemaValidation);
        aBDXR2Client.setVerifySignature (bVerifySignature);

        final com.helger.xsds.bdxr.smp2.ServiceMetadataType aSM = aBDXR2Client.getServiceMetadataOrNull (aParticipantID,
                                                                                                         aDocTypeID);
        if (aSM != null)
        {
          aJson = SMPJsonResponse.convert (aParticipantID, aDocTypeID, aSM);
        }
        break;
      }
    }

    aSW.stop ();

    if (aJson == null)
    {
      LOGGER.error ("[API] Failed to perform the SMP lookup");
      aUnifiedResponse.setStatus (CHttp.HTTP_NOT_FOUND);
    }
    else
    {
      LOGGER.info ("[API] Succesfully finished lookup lookup after " + aSW.getMillis () + " milliseconds");

      aJson.add ("queryDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format (aQueryDT));
      aJson.add ("queryDurationMillis", aSW.getMillis ());

      final String sRet = new JsonWriter (JsonWriterSettings.DEFAULT_SETTINGS_FORMATTED).writeAsString (aJson);
      aUnifiedResponse.setContentAndCharset (sRet, StandardCharsets.UTF_8)
                      .setMimeType (CMimeType.APPLICATION_JSON)
                      .enableCaching (3 * CGlobal.SECONDS_PER_HOUR);
    }
  }
}
