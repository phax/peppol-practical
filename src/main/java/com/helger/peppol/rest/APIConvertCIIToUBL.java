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
package com.helger.peppol.rest;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.cii.d16b.CIID16BReader;
import com.helger.commons.CGlobal;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.error.IError;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.mime.CMimeType;
import com.helger.commons.timing.StopWatch;
import com.helger.en16931.cii2ubl.CIIToUBL21Converter;
import com.helger.jaxb.validation.WrappedCollectingValidationEventHandler;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.json.serialize.JsonWriter;
import com.helger.json.serialize.JsonWriterSettings;
import com.helger.peppol.app.CPPApp;
import com.helger.photon.api.IAPIDescriptor;
import com.helger.servlet.response.UnifiedResponse;
import com.helger.ubl21.UBL21Writer;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import oasis.names.specification.ubl.schema.xsd.creditnote_21.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import un.unece.uncefact.data.standard.crossindustryinvoice._100.CrossIndustryInvoiceType;

/**
 * Convert CII to UBL via API
 * 
 * @author Philip Helger
 */
public final class APIConvertCIIToUBL extends AbstractJsonBasedAPIExecutor
{
  private static final Logger LOGGER = LoggerFactory.getLogger (APIConvertCIIToUBL.class);

  public void invokeAPI (@Nonnull final IAPIDescriptor aAPIDescriptor,
                         @Nonnull @Nonempty final String sPath,
                         @Nonnull final Map <String, String> aPathVariables,
                         @Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                         @Nonnull final UnifiedResponse aUnifiedResponse) throws Exception
  {
    final ErrorList aErrorList = new ErrorList ();
    final Locale aDisplayLocale = CPPApp.DEFAULT_LOCALE;

    final ZonedDateTime aQueryDT = PDTFactory.getCurrentZonedDateTimeUTC ();
    final StopWatch aSW = StopWatch.createdStarted ();

    final String sLogPrefix = "[API] ";

    final IJsonObject aJson = new JsonObject ();
    aJson.add ("conversionDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format (aQueryDT));

    // Parse XML
    LOGGER.info (sLogPrefix + "Parsing CII");
    final CrossIndustryInvoiceType aCIIInvoice = CIID16BReader.crossIndustryInvoice ()
                                                              .setValidationEventHandler (new WrappedCollectingValidationEventHandler (aErrorList))
                                                              .read (aRequestScope.getRequest ().getInputStream ());
    final long nParsingMillis = aSW.stopAndGetMillis ();
    aJson.add ("parsingDuractionMillis", nParsingMillis);

    {
      final IJsonArray aParseErrors = new JsonArray ();
      for (final IError aError : aErrorList.getAllFailures ())
        aParseErrors.add (createItem (aError, aDisplayLocale));
      aJson.add ("parsingErrors", aParseErrors);
    }

    if (aCIIInvoice != null)
    {
      // Convert to domain model
      LOGGER.info (sLogPrefix + "Converting CII to UBL");

      aSW.restart ();
      aErrorList.clear ();

      final Serializable aUBL = new CIIToUBL21Converter ().convertCIItoUBL (aCIIInvoice, aErrorList);
      final long nConversionMillis = aSW.stopAndGetMillis ();
      aJson.add ("conversionDuractionMillis", nConversionMillis);

      {
        final IJsonArray aConversionErrors = new JsonArray ();
        for (final IError aError : aErrorList.getAllFailures ())
          aConversionErrors.add (createItem (aError, aDisplayLocale));
        aJson.add ("coversionErrors", aConversionErrors);
      }

      if (aUBL != null)
      {
        final String sUBL;
        if (aUBL instanceof InvoiceType)
          sUBL = UBL21Writer.invoice ().getAsString ((InvoiceType) aUBL);
        else
          if (aUBL instanceof CreditNoteType)
            sUBL = UBL21Writer.creditNote ().getAsString ((CreditNoteType) aUBL);
          else
            throw new IllegalStateException ();

        aJson.add ("ubl", sUBL);
      }
    }

    final String sRet = new JsonWriter (new JsonWriterSettings ().setIndentEnabled (true)).writeAsString (aJson);
    aUnifiedResponse.setContentAndCharset (sRet, StandardCharsets.UTF_8)
                    .setMimeType (CMimeType.APPLICATION_JSON)
                    .enableCaching (3 * CGlobal.SECONDS_PER_HOUR);
  }
}
