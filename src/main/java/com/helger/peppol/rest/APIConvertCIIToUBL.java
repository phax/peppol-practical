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

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.annotation.Nonempty;
import com.helger.base.timing.StopWatch;
import com.helger.cii.d16b.CIID16BCrossIndustryInvoiceTypeMarshaller;
import com.helger.datetime.helper.PDTFactory;
import com.helger.diagnostics.error.IError;
import com.helger.diagnostics.error.list.ErrorList;
import com.helger.en16931.cii2ubl.CIIToUBL21Converter;
import com.helger.en16931.cii2ubl.CIIToUBLVersion;
import com.helger.en16931.cii2ubl.EUBLCreationMode;
import com.helger.jaxb.validation.WrappedCollectingValidationEventHandler;
import com.helger.json.IJsonArray;
import com.helger.json.IJsonObject;
import com.helger.json.JsonArray;
import com.helger.json.JsonObject;
import com.helger.mime.CMimeType;
import com.helger.peppol.app.CPPApp;
import com.helger.photon.api.IAPIDescriptor;
import com.helger.photon.app.PhotonUnifiedResponse;
import com.helger.ubl21.UBL21Marshaller;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import jakarta.annotation.Nonnull;
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
  public static final String PARAM_SIMPLE_RESPONSE = "simple-response";
  public static final boolean DEFAULT_SIMPLE_RESPONSE = false;
  public static final String PARAM_XML_BEAUTIFY = "xml-beautify";
  public static final boolean DEFAULT_XML_BEAUTIFY = true;

  private static final Logger LOGGER = LoggerFactory.getLogger (APIConvertCIIToUBL.class);

  @Override
  protected void invokeAPI (@Nonnull @Nonempty final String sLogPrefix,
                            @Nonnull final IAPIDescriptor aAPIDescriptor,
                            @Nonnull @Nonempty final String sPath,
                            @Nonnull final Map <String, String> aPathVariables,
                            @Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                            @Nonnull final PhotonUnifiedResponse aUnifiedResponse) throws Exception
  {
    final ErrorList aErrorList = new ErrorList ();
    final Locale aDisplayLocale = CPPApp.DEFAULT_LOCALE;

    final ZonedDateTime aQueryDT = PDTFactory.getCurrentZonedDateTimeUTC ();
    final StopWatch aSW = StopWatch.createdStarted ();

    // Params
    final boolean bSimpleResponse = aRequestScope.params ()
                                                 .getAsBoolean (PARAM_SIMPLE_RESPONSE, DEFAULT_SIMPLE_RESPONSE);
    final boolean bXMLBeautify = aRequestScope.params ().getAsBoolean (PARAM_XML_BEAUTIFY, DEFAULT_XML_BEAUTIFY);

    final IJsonObject aJson = new JsonObject ();
    aJson.add ("conversionDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format (aQueryDT));

    // Parse XML
    LOGGER.info (sLogPrefix + "Parsing CII");
    final CrossIndustryInvoiceType aCIIInvoice = new CIID16BCrossIndustryInvoiceTypeMarshaller ().setValidationEventHandler (new WrappedCollectingValidationEventHandler (aErrorList))
                                                                                                 .read (aRequestScope.getRequest ()
                                                                                                                     .getInputStream ());
    final long nParsingMillis = aSW.stopAndGetMillis ();
    aJson.add ("parsingDuractionMillis", nParsingMillis);

    {
      final IJsonArray aParseErrors = new JsonArray ();
      for (final IError aError : aErrorList.getAllFailures ())
        aParseErrors.add (createItem (aError, aDisplayLocale));
      aJson.add ("parsingErrors", aParseErrors);
    }
    String sUBL = null;
    boolean bSuccess = false;
    if (aCIIInvoice != null)
    {
      // Convert to domain model
      LOGGER.info (sLogPrefix +
                   "Converting CII to UBL with v" +
                   CIIToUBLVersion.BUILD_VERSION +
                   " (from " +
                   CIIToUBLVersion.BUILD_TIMESTAMP +
                   ")");

      aSW.restart ();
      aErrorList.clear ();

      // Added since v1.3.0
      aJson.add ("conversionVersion", CIIToUBLVersion.BUILD_VERSION);
      aJson.add ("conversionBuildTimestamp", CIIToUBLVersion.BUILD_TIMESTAMP);

      final Serializable aUBL = new CIIToUBL21Converter ().setUBLCreationMode (EUBLCreationMode.AUTOMATIC)
                                                          .convertCIItoUBL (aCIIInvoice, aErrorList);
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
        if (aUBL instanceof InvoiceType)
          sUBL = UBL21Marshaller.invoice ().setFormattedOutput (bXMLBeautify).getAsString ((InvoiceType) aUBL);
        else
          if (aUBL instanceof CreditNoteType)
            sUBL = UBL21Marshaller.creditNote ().setFormattedOutput (bXMLBeautify).getAsString ((CreditNoteType) aUBL);
          else
            throw new IllegalStateException ("Whaaaatttt");

        aJson.add ("ubl", sUBL);
        bSuccess = true;
      }
    }
    if (bSuccess)
      LOGGER.info (sLogPrefix + "Successfully finished conversion");
    else
      LOGGER.info (sLogPrefix + "Finished conversion with errors");

    if (bSimpleResponse && bSuccess)
    {
      aUnifiedResponse.setContentAndCharset (sUBL, StandardCharsets.UTF_8).setMimeType (CMimeType.APPLICATION_XML);
    }
    else
    {
      if (!bSuccess)
        aUnifiedResponse.createBadRequest ();

      aUnifiedResponse.json (aJson);
    }
  }
}
