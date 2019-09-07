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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.http.client.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsTreeMap;
import com.helger.commons.collection.impl.ICommonsSortedMap;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.http.CHttp;
import com.helger.commons.mime.CMimeType;
import com.helger.commons.timing.StopWatch;
import com.helger.json.IJsonObject;
import com.helger.json.serialize.JsonWriter;
import com.helger.json.serialize.JsonWriterSettings;
import com.helger.peppol.app.SMPQueryParams;
import com.helger.peppol.app.mgr.ISMLInfoManager;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.bdxrclient.BDXRClientReadOnly;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.smpclient.SMPClientReadOnly;
import com.helger.peppolid.CIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.photon.api.IAPIDescriptor;
import com.helger.photon.api.IAPIExecutor;
import com.helger.servlet.response.UnifiedResponse;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

public final class APISMPQueryGetDocTypes implements IAPIExecutor
{
  private static final Logger LOGGER = LoggerFactory.getLogger (APISMPQueryGetDocTypes.class);

  public void invokeAPI (@Nonnull final IAPIDescriptor aAPIDescriptor,
                         @Nonnull @Nonempty final String sPath,
                         @Nonnull final Map <String, String> aPathVariables,
                         @Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                         @Nonnull final UnifiedResponse aUnifiedResponse) throws Exception
  {
    final ISMLInfoManager aSMLInfoMgr = PPMetaManager.getSMLInfoMgr ();
    final String sSMLID = aPathVariables.get (PPAPI.PARAM_SML_ID);
    final boolean bSMLAutoDetect = "autodetect".equals (sSMLID);
    ISMLInfo aSML = aSMLInfoMgr.getSMLInfoOfID (sSMLID);
    if (aSML == null && !bSMLAutoDetect)
      throw new APIParamException ("Unsupported SML ID '" + sSMLID + "' provided.");

    final String sParticipantID = aPathVariables.get (PPAPI.PARAM_PARTICIPANT_ID);
    final IParticipantIdentifier aPID = SimpleIdentifierFactory.INSTANCE.parseParticipantIdentifier (sParticipantID);
    if (aPID == null)
      throw new APIParamException ("Invalid participant ID '" + sParticipantID + "' provided.");

    final StopWatch aSW = StopWatch.createdStarted ();

    SMPQueryParams aQueryParams = null;
    if (bSMLAutoDetect)
    {
      for (final ISMLInfo aCurSML : aSMLInfoMgr.getAllSorted ())
      {
        aQueryParams = SMPQueryParams.createForSML (aCurSML, aPID.getScheme (), aPID.getValue ());
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
                                         "The participant identifier '" +
                                                               sParticipantID +
                                                               "' could not be found in any SML.");
    }
    else
    {
      aQueryParams = SMPQueryParams.createForSML (aSML, aPID.getScheme (), aPID.getValue ());
    }
    if (aQueryParams == null)
      throw new APIParamException ("Failed to resolve participant ID '" +
                                   sParticipantID +
                                   "' for the provided SML '" +
                                   aSML.getID () +
                                   "'");

    final IParticipantIdentifier aParticipantID = aQueryParams.getParticipantID ();

    LOGGER.info ("Participant information of '" +
                 aParticipantID.getURIEncoded () +
                 "' is queried using SMP API '" +
                 aQueryParams.getSMPAPIType () +
                 "' from '" +
                 aQueryParams.getSMPHostURI () +
                 "' using SML '" +
                 aSML +
                 "'");

    ICommonsSortedMap <String, String> aSGHrefs = null;
    switch (aQueryParams.getSMPAPIType ())
    {
      case PEPPOL:
      {
        final SMPClientReadOnly aSMPClient = new SMPClientReadOnly (aQueryParams.getSMPHostURI ());

        // Get all HRefs and sort them by decoded URL
        final com.helger.peppol.smp.ServiceGroupType aSG = aSMPClient.getServiceGroupOrNull (aParticipantID);
        // Map from cleaned URL to original URL
        if (aSG != null && aSG.getServiceMetadataReferenceCollection () != null)
        {
          aSGHrefs = new CommonsTreeMap <> ();
          for (final com.helger.peppol.smp.ServiceMetadataReferenceType aSMR : aSG.getServiceMetadataReferenceCollection ()
                                                                                  .getServiceMetadataReference ())
          {
            // Decoded href is important for unification
            final String sHref = CIdentifier.createPercentDecoded (aSMR.getHref ());
            if (aSGHrefs.put (sHref, aSMR.getHref ()) != null)
              LOGGER.warn ("The ServiceGroup list contains the duplicate URL '" + sHref + "'");
          }
        }
        break;
      }
      case OASIS_BDXR_V1:
      {
        aSGHrefs = new CommonsTreeMap <> ();
        final BDXRClientReadOnly aBDXR1Client = new BDXRClientReadOnly (aQueryParams.getSMPHostURI ());

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
              LOGGER.warn ("The ServiceGroup list contains the duplicate URL '" + sHref + "'");
          }
        }
        break;
      }
    }

    IJsonObject aJson = null;
    if (aSGHrefs != null)
      aJson = SMPJsonResponse.convert (aParticipantID, aSGHrefs, aQueryParams.getIF ());

    aSW.stop ();

    if (aJson == null)
    {
      LOGGER.info ("Failed to perform the SMP lookup");
      aUnifiedResponse.setStatus (CHttp.HTTP_NOT_FOUND);
    }
    else
    {
      LOGGER.info ("Succesfully finished lookup lookup after " + aSW.getMillis () + " milliseconds");

      aJson.add ("queryDateTime",
                 DateTimeFormatter.ISO_ZONED_DATE_TIME.format (PDTFactory.getCurrentZonedDateTimeUTC ()));
      aJson.add ("queryDurationMillis", aSW.getMillis ());

      final String sRet = new JsonWriter (new JsonWriterSettings ().setIndentEnabled (true)).writeAsString (aJson);
      aUnifiedResponse.setContentAndCharset (sRet, StandardCharsets.UTF_8)
                      .setMimeType (CMimeType.APPLICATION_JSON)
                      .enableCaching (3 * CGlobal.SECONDS_PER_HOUR);
    }
  }
}
