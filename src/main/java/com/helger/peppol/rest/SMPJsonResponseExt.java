/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.json.IJson;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.peppol.domain.NiceNameEntry;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.ui.AppCommonUI;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.smpclient.json.SMPJsonResponse;

@Immutable
public final class SMPJsonResponseExt
{
  private static final String JSON_NICE_NAME = "niceName";
  private static final String JSON_IS_DEPRECATED = "isDeprecated";

  private SMPJsonResponseExt ()
  {}

  @Nonnull
  public static IJsonObject convert (@Nonnull final ESMPAPIType eSMPAPIType,
                                     @Nonnull final IParticipantIdentifier aParticipantID,
                                     @Nonnull final Map <String, String> aSGHrefs,
                                     @Nonnull final IIdentifierFactory aIF)
  {
    final IJsonObject aJson = SMPJsonResponse.convert (eSMPAPIType, aParticipantID, aSGHrefs, aIF);
    final IJsonArray aURLsArray = aJson.getAsArray (SMPJsonResponse.JSON_URLS);
    if (aURLsArray != null)
      for (final IJson aEntry : aURLsArray)
        if (aEntry.isObject ())
        {
          final IJsonObject aUrlEntry = aEntry.getAsObject ();
          final String sDocType = aUrlEntry.getAsString (SMPJsonResponse.JSON_DOCUMENT_TYPE_ID);
          if (sDocType != null)
          {
            final NiceNameEntry aNN = AppCommonUI.getDocTypeNames ().get (sDocType);
            if (aNN != null)
            {
              aUrlEntry.add (JSON_NICE_NAME, aNN.getName ());
              aUrlEntry.add (JSON_IS_DEPRECATED, aNN.isDeprecated ());
            }
          }
        }
    return aJson;
  }
}
