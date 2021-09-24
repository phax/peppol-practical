/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.http.CHttp;
import com.helger.commons.mime.CMimeType;
import com.helger.commons.timing.StopWatch;
import com.helger.httpclient.HttpClientManager;
import com.helger.httpclient.response.ResponseHandlerByteArray;
import com.helger.json.IJsonObject;
import com.helger.json.serialize.JsonWriter;
import com.helger.json.serialize.JsonWriterSettings;
import com.helger.pd.businesscard.generic.PDBusinessCard;
import com.helger.pd.businesscard.helper.PDBusinessCardHelper;
import com.helger.peppol.app.mgr.ISMLConfigurationManager;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.domain.ISMLConfiguration;
import com.helger.peppol.domain.SMPQueryParams;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.photon.api.IAPIDescriptor;
import com.helger.servlet.response.UnifiedResponse;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

public final class APISMPQueryGetBusinessCard extends AbstractAPIExecutor
{
  private static final Logger LOGGER = LoggerFactory.getLogger (APISMPQueryGetBusinessCard.class);

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

    final ZonedDateTime aQueryDT = PDTFactory.getCurrentZonedDateTimeUTC ();
    final StopWatch aSW = StopWatch.createdStarted ();

    SMPQueryParams aQueryParams = null;
    if (bSMLAutoDetect)
    {
      for (final ISMLConfiguration aCurSML : aSMLConfigurationMgr.getAllSorted ())
      {
        aQueryParams = SMPQueryParams.createForSML (aCurSML, aPID.getScheme (), aPID.getValue (), false);
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
        throw new HttpResponseException (CHttp.HTTP_NOT_FOUND,
                                         "The participant identifier '" + sParticipantID + "' could not be found in any SML.");
    }
    else
    {
      aQueryParams = SMPQueryParams.createForSML (aSML, aPID.getScheme (), aPID.getValue (), true);
    }
    if (aQueryParams == null)
      throw new APIParamException ("Failed to resolve participant ID '" +
                                   sParticipantID +
                                   "' for the provided SML '" +
                                   aSML.getID () +
                                   "'");

    final IParticipantIdentifier aParticipantID = aQueryParams.getParticipantID ();
    final String sLogPrefix = "[API] ";

    LOGGER.info (sLogPrefix +
                 "BusinessCard of '" +
                 aParticipantID.getURIEncoded () +
                 "' is queried using SMP API '" +
                 aQueryParams.getSMPAPIType () +
                 "' from '" +
                 aQueryParams.getSMPHostURI () +
                 "' using SML '" +
                 aSML +
                 "'");

    IJsonObject aJson = null;

    final String sBCURL = aQueryParams.getSMPHostURI ().toString () + "/businesscard/" + aParticipantID.getURIEncoded ();
    LOGGER.info (sLogPrefix + "Querying BC from '" + sBCURL + "'");
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

    if (aData == null)
      LOGGER.warn (sLogPrefix + "No Business Card is available for that participant.");
    else
    {
      final PDBusinessCard aBC = PDBusinessCardHelper.parseBusinessCard (aData, StandardCharsets.UTF_8);
      if (aBC == null)
      {
        LOGGER.error (sLogPrefix + "Failed to parse BC:\n" + new String (aData));
      }
      else
      {
        // Business Card found
        aJson = aBC.getAsJson ();
      }
    }

    aSW.stop ();

    if (aJson == null)
    {
      LOGGER.error ("[API] Failed to perform the BusinessCard SMP lookup");
      aUnifiedResponse.setStatus (CHttp.HTTP_NOT_FOUND);
    }
    else
    {
      LOGGER.info ("[API] Succesfully finished BusinessCard lookup lookup after " + aSW.getMillis () + " milliseconds");

      aJson.add ("queryDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format (aQueryDT));
      aJson.add ("queryDurationMillis", aSW.getMillis ());

      final String sRet = new JsonWriter (JsonWriterSettings.DEFAULT_SETTINGS_FORMATTED).writeAsString (aJson);
      aUnifiedResponse.setContentAndCharset (sRet, StandardCharsets.UTF_8)
                      .setMimeType (CMimeType.APPLICATION_JSON)
                      .enableCaching (3 * CGlobal.SECONDS_PER_HOUR);
    }
  }
}
