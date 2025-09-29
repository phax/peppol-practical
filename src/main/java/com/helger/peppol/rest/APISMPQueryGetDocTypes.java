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
import java.security.GeneralSecurityException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.apache.hc.client5.http.HttpResponseException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.base.CGlobal;
import com.helger.base.timing.StopWatch;
import com.helger.collection.commons.CommonsTreeMap;
import com.helger.collection.commons.ICommonsSortedMap;
import com.helger.datetime.helper.PDTFactory;
import com.helger.http.CHttp;
import com.helger.httpclient.HttpClientManager;
import com.helger.httpclient.response.ResponseHandlerByteArray;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import com.helger.json.serialize.JsonWriter;
import com.helger.json.serialize.JsonWriterSettings;
import com.helger.mime.CMimeType;
import com.helger.peppol.businesscard.generic.PDBusinessCard;
import com.helger.peppol.businesscard.helper.PDBusinessCardHelper;
import com.helger.peppol.sharedui.CSharedUI;
import com.helger.peppol.sharedui.api.APIParamException;
import com.helger.peppol.sharedui.api.SMPJsonResponseExt;
import com.helger.peppol.sharedui.domain.ISMLConfiguration;
import com.helger.peppol.sharedui.domain.SMPQueryParams;
import com.helger.peppol.sharedui.mgr.ISMLConfigurationManager;
import com.helger.peppol.sharedui.mgr.SharedUIMetaManager;
import com.helger.peppol.sharedui.page.pub.PagePublicToolsParticipantInformation;
import com.helger.peppolid.CIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.photon.api.IAPIDescriptor;
import com.helger.servlet.response.UnifiedResponse;
import com.helger.smpclient.bdxr1.BDXRClientReadOnly;
import com.helger.smpclient.httpclient.SMPHttpClientSettings;
import com.helger.smpclient.peppol.SMPClientReadOnly;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import jakarta.annotation.Nonnull;

public final class APISMPQueryGetDocTypes extends AbstractPPAPIExecutor
{
  public static final String PARAM_VERIFY_SIGNATURE = "verifySignature";
  public static final String PARAM_XML_SCHEMA_VALIDATION = "xmlSchemaValidation";
  public static final String PARAM_BUSINESS_CARD = "businessCard";

  private static final Logger LOGGER = LoggerFactory.getLogger (APISMPQueryGetDocTypes.class);

  @Override
  protected void rateLimitedInvokeAPI (@Nonnull final IAPIDescriptor aAPIDescriptor,
                                       @Nonnull @Nonempty final String sPath,
                                       @Nonnull final Map <String, String> aPathVariables,
                                       @Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                                       @Nonnull final UnifiedResponse aUnifiedResponse) throws Exception
  {
    final ISMLConfigurationManager aSMLConfigurationMgr = SharedUIMetaManager.getSMLConfigurationMgr ();
    final String sSMLID = aPathVariables.get (PPAPI.PARAM_SML_ID);
    final boolean bSMLAutoDetect = ISMLConfigurationManager.ID_AUTO_DETECT.equals (sSMLID);
    ISMLConfiguration aSML = aSMLConfigurationMgr.getSMLInfoOfID (sSMLID);
    if (aSML == null && !bSMLAutoDetect)
      throw new APIParamException ("Unsupported SML ID '" + sSMLID + "' provided.");

    final String sParticipantID = aPathVariables.get (PPAPI.PARAM_PARTICIPANT_ID);
    final IParticipantIdentifier aPID = SimpleIdentifierFactory.INSTANCE.parseParticipantIdentifier (sParticipantID);
    if (aPID == null)
      throw new APIParamException ("Invalid participant ID '" + sParticipantID + "' provided.");

    final boolean bQueryBusinessCard = aRequestScope.params ().getAsBoolean (PARAM_BUSINESS_CARD, false);
    final boolean bXMLSchemaValidation = aRequestScope.params ().getAsBoolean (PARAM_XML_SCHEMA_VALIDATION, true);
    final boolean bVerifySignature = aRequestScope.params ().getAsBoolean (PARAM_VERIFY_SIGNATURE, true);

    final ZonedDateTime aQueryDT = PDTFactory.getCurrentZonedDateTimeUTC ();
    final StopWatch aSW = StopWatch.createdStarted ();

    SMPQueryParams aSMPQueryParams = null;
    if (bSMLAutoDetect)
    {
      for (final ISMLConfiguration aCurSML : aSMLConfigurationMgr.getAllSorted ())
      {
        aSMPQueryParams = SMPQueryParams.createForSMLOrNull (aCurSML,
                                                             aPID.getScheme (),
                                                             aPID.getValue (),
                                                             PagePublicToolsParticipantInformation.DEFAULT_CNAME_LOOKUP,
                                                             false);
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
      aSMPQueryParams = SMPQueryParams.createForSMLOrNull (aSML,
                                                           aPID.getScheme (),
                                                           aPID.getValue (),
                                                           PagePublicToolsParticipantInformation.DEFAULT_CNAME_LOOKUP,
                                                           true);
    }
    if (aSMPQueryParams == null)
      throw new APIParamException ("Failed to resolve participant ID '" +
                                   sParticipantID +
                                   "' for the provided SML '" +
                                   aSML.getID () +
                                   "'");

    final IParticipantIdentifier aParticipantID = aSMPQueryParams.getParticipantID ();

    LOGGER.info ("[API] Document types of '" +
                 aParticipantID.getURIEncoded () +
                 "' are queried using SMP API '" +
                 aSMPQueryParams.getSMPAPIType () +
                 "' from '" +
                 aSMPQueryParams.getSMPHostURI () +
                 "' using SML '" +
                 aSML.getID () +
                 "'; XSD validation=" +
                 bXMLSchemaValidation +
                 "; signature verification=" +
                 bVerifySignature);

    // Defaulting to true per 11.8.2025
    final boolean bUseSMPSecureValidation = CSharedUI.DEFAULT_SMP_USE_SECURE_VALIDATION;

    ICommonsSortedMap <String, String> aSGHrefs = null;
    switch (aSMPQueryParams.getSMPAPIType ())
    {
      case PEPPOL:
      {
        final SMPClientReadOnly aSMPClient = new SMPClientReadOnly (aSMPQueryParams.getSMPHostURI ());
        aSMPClient.setSecureValidation (bUseSMPSecureValidation);
        aSMPClient.withHttpClientSettings (m_aHCSModifier);
        aSMPClient.setXMLSchemaValidation (bXMLSchemaValidation);
        aSMPClient.setVerifySignature (bVerifySignature);
        if (aSMPQueryParams.isTrustAllCertificates ())
          try
          {
            aSMPClient.httpClientSettings ().setSSLContextTrustAll ();
          }
          catch (final GeneralSecurityException ex)
          {
            // Ignore
          }

        // Get all HRefs and sort them by decoded URL
        final com.helger.xsds.peppol.smp1.ServiceGroupType aSG = aSMPClient.getServiceGroupOrNull (aParticipantID);
        // Map from cleaned URL to original URL
        if (aSG != null && aSG.getServiceMetadataReferenceCollection () != null)
        {
          aSGHrefs = new CommonsTreeMap <> ();
          for (final com.helger.xsds.peppol.smp1.ServiceMetadataReferenceType aSMR : aSG.getServiceMetadataReferenceCollection ()
                                                                                        .getServiceMetadataReference ())
          {
            // Decoded href is important for unification
            final String sHref = CIdentifier.createPercentDecoded (aSMR.getHref ());
            if (aSGHrefs.put (sHref, aSMR.getHref ()) != null)
              LOGGER.warn ("[API] The ServiceGroup list contains the duplicate URL '" + sHref + "'");
          }
        }
        break;
      }
      case OASIS_BDXR_V1:
      {
        aSGHrefs = new CommonsTreeMap <> ();
        final BDXRClientReadOnly aBDXR1Client = new BDXRClientReadOnly (aSMPQueryParams.getSMPHostURI ());
        aBDXR1Client.setSecureValidation (bUseSMPSecureValidation);
        aBDXR1Client.withHttpClientSettings (m_aHCSModifier);
        aBDXR1Client.setXMLSchemaValidation (bXMLSchemaValidation);
        aBDXR1Client.setVerifySignature (bVerifySignature);
        if (aSMPQueryParams.isTrustAllCertificates ())
          try
          {
            aBDXR1Client.httpClientSettings ().setSSLContextTrustAll ();
          }
          catch (final GeneralSecurityException ex)
          {
            // Ignore
          }

        // Get all HRefs and sort them by decoded URL
        final com.helger.xsds.bdxr.smp1.ServiceGroupType aSG = aBDXR1Client.getServiceGroupOrNull (aParticipantID);
        // Map from cleaned URL to original URL
        if (aSG != null && aSG.getServiceMetadataReferenceCollection () != null)
        {
          aSGHrefs = new CommonsTreeMap <> ();
          for (final com.helger.xsds.bdxr.smp1.ServiceMetadataReferenceType aSMR : aSG.getServiceMetadataReferenceCollection ()
                                                                                      .getServiceMetadataReference ())
          {
            // Decoded href is important for unification
            final String sHref = CIdentifier.createPercentDecoded (aSMR.getHref ());
            if (aSGHrefs.put (sHref, aSMR.getHref ()) != null)
              LOGGER.warn ("[API] The ServiceGroup list contains the duplicate URL '" + sHref + "'");
          }
        }
        break;
      }
    }

    IJsonObject aJson = null;
    if (aSGHrefs != null)
    {
      aJson = SMPJsonResponseExt.convert (aSMPQueryParams.getSMPAPIType (),
                                          aParticipantID,
                                          aSGHrefs,
                                          aSMPQueryParams.getIF ());
    }

    if (bQueryBusinessCard)
    {
      final SMPHttpClientSettings aHCS = new SMPHttpClientSettings ();
      m_aHCSModifier.accept (aHCS);

      final String sBCURL = aSMPQueryParams.getSMPHostURI ().toString () +
                            "/businesscard/" +
                            aParticipantID.getURIEncoded ();
      LOGGER.info ("[API] Querying BC from '" + sBCURL + "'");
      byte [] aData;
      try (HttpClientManager aHttpClientMgr = HttpClientManager.create (aHCS))
      {
        final HttpGet aGet = new HttpGet (sBCURL);
        aData = aHttpClientMgr.execute (aGet, new ResponseHandlerByteArray ());
      }
      catch (final Exception ex)
      {
        aData = null;
      }

      if (aData == null)
        LOGGER.warn ("[API] No Business Card is available for that participant.");
      else
      {
        final PDBusinessCard aBC = PDBusinessCardHelper.parseBusinessCard (aData, StandardCharsets.UTF_8);
        if (aBC == null)
        {
          LOGGER.error ("[API] Failed to parse BC:\n" + new String (aData));
        }
        else
        {
          // Business Card found
          if (aJson == null)
            aJson = new JsonObject ();
          aJson.add (PARAM_BUSINESS_CARD, aBC.getAsJson ());
        }
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
