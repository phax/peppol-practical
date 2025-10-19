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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.base.CGlobal;
import com.helger.base.timing.StopWatch;
import com.helger.datetime.helper.PDTFactory;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import com.helger.peppol.api.rest.APIParamException;
import com.helger.peppol.photon.mgr.PhotonPeppolMetaManager;
import com.helger.peppol.photon.smlconfig.ISMLConfiguration;
import com.helger.peppol.photon.smlconfig.ISMLConfigurationManager;
import com.helger.peppol.photon.smp.SMPQueryParams;
import com.helger.peppol.sharedui.page.pub.PagePublicToolsParticipantInformation;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.photon.api.IAPIDescriptor;
import com.helger.photon.app.PhotonUnifiedResponse;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import jakarta.annotation.Nonnull;

public final class APIQueryParticipantExistence extends AbstractPPAPIExecutor
{
  private static final Logger LOGGER = LoggerFactory.getLogger (APIQueryParticipantExistence.class);

  @Override
  protected void invokeAPI (@Nonnull @Nonempty final String sLogPrefix,
                            @Nonnull final IAPIDescriptor aAPIDescriptor,
                            @Nonnull @Nonempty final String sPath,
                            @Nonnull final Map <String, String> aPathVariables,
                            @Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                            @Nonnull final PhotonUnifiedResponse aUnifiedResponse) throws Exception
  {
    final ISMLConfigurationManager aSMLConfigurationMgr = PhotonPeppolMetaManager.getSMLConfigurationMgr ();
    final String sSMLID = aPathVariables.get (PPAPI.PARAM_SML_ID);
    final ISMLConfiguration aSML = aSMLConfigurationMgr.getSMLInfoOfID (sSMLID);
    if (aSML == null)
      throw new APIParamException ("Unsupported SML ID '" + sSMLID + "' provided.");

    final String sParticipantID = aPathVariables.get (PPAPI.PARAM_PARTICIPANT_ID);
    final IParticipantIdentifier aPID = SimpleIdentifierFactory.INSTANCE.parseParticipantIdentifier (sParticipantID);
    if (aPID == null)
      throw new APIParamException ("Invalid participant ID '" + sParticipantID + "' provided.");

    final ZonedDateTime aQueryDT = PDTFactory.getCurrentZonedDateTimeUTC ();
    final StopWatch aSW = StopWatch.createdStarted ();

    // No auto detect here, because this is what we want to check
    final SMPQueryParams aSMPQueryParams = SMPQueryParams.createForSMLOrNull (aSML,
                                                                              aPID.getScheme (),
                                                                              aPID.getValue (),
                                                                              PagePublicToolsParticipantInformation.DEFAULT_CNAME_LOOKUP,
                                                                              true);
    if (aSMPQueryParams == null)
      throw new APIParamException ("Failed to resolve participant ID '" +
                                   sParticipantID +
                                   "' for the provided SML '" +
                                   aSML.getID () +
                                   "'");

    final IParticipantIdentifier aParticipantID = aSMPQueryParams.getParticipantID ();

    LOGGER.info (sLogPrefix +
                 "Checking existance of '" +
                 aParticipantID.getURIEncoded () +
                 "' from '" +
                 aSMPQueryParams.getSMPHostURI () +
                 "' using SML '" +
                 aSML.getID () +
                 "'");

    final IJsonObject aJson = new JsonObject ();
    aJson.add ("participantID", aParticipantID.getURIEncoded ());
    aJson.add ("sml", aSML.getID ());
    aJson.add ("smpHostURI", aSMPQueryParams.getSMPHostURI ());
    // This is the main check
    aJson.add ("exists", aSMPQueryParams.isSMPRegisteredInDNS ());
    aSW.stop ();

    LOGGER.info (sLogPrefix + "Succesfully finished lookup lookup after " + aSW.getMillis () + " milliseconds");

    aJson.add ("queryDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format (aQueryDT));
    aJson.add ("queryDurationMillis", aSW.getMillis ());

    aUnifiedResponse.json (aJson).enableCaching (1 * CGlobal.SECONDS_PER_HOUR);
  }
}
