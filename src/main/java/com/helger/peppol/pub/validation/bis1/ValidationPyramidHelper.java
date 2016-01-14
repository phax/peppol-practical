/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.peppol.pub.validation.bis1;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.transform.dom.DOMSource;

import org.joda.time.DateTime;
import org.w3c.dom.Node;

import com.helger.commons.error.IResourceError;
import com.helger.commons.error.IResourceLocation;
import com.helger.commons.lang.StackTraceHelper;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroDocument;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.IMutableStatisticsHandlerTimer;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.StringHelper;
import com.helger.commons.timing.StopWatch;
import com.helger.photon.basic.audit.AuditHelper;
import com.helger.schematron.svrl.SVRLResourceError;
import com.helger.web.datetime.PDTWebDateHelper;

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;
import eu.europa.ec.cipa.validation.pyramid.ValidationPyramid2;
import eu.europa.ec.cipa.validation.pyramid.ValidationPyramidResult;
import eu.europa.ec.cipa.validation.pyramid.ValidationPyramidResultLayer;
import eu.europa.ec.cipa.validation.rules.EValidationArtefact;
import eu.europa.ec.cipa.validation.rules.EValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.EValidationLevel;
import eu.europa.ec.cipa.validation.rules.EValidationSyntaxBinding;
import eu.europa.ec.cipa.validation.rules.ValidationTransaction;

@Immutable
public final class ValidationPyramidHelper
{
  private static final IMutableStatisticsHandlerCounter s_aStatsCount = StatisticsManager.getCounterHandler (ValidationPyramidHelper.class.getName () +
                                                                                                             "$count");
  private static final IMutableStatisticsHandlerTimer s_aStatsDuration = StatisticsManager.getTimerHandler (ValidationPyramidHelper.class.getName () +
                                                                                                            "$duration");

  private ValidationPyramidHelper ()
  {}

  @Nonnull
  public static ValidationPyramidResult executeValidationPyramid (@Nonnull final EValidationSyntaxBinding eSyntaxBinding,
                                                                  @Nonnull final EValidationDocumentType eDocType,
                                                                  @Nonnull final ETransaction eTransaction,
                                                                  @Nullable final Locale aCountry,
                                                                  final boolean bIndustrySpecificRules,
                                                                  @Nonnull final Node aXMLNode)
  {
    s_aStatsCount.increment ();

    final StopWatch aSW = StopWatch.createdStarted ();
    final ValidationPyramid2 aPyramid = ValidationPyramid2.createDefault (eDocType,
                                                                          new ValidationTransaction (eSyntaxBinding,
                                                                                                     eTransaction),
                                                                          aCountry);
    if (bIndustrySpecificRules)
      for (final EValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (EValidationLevel.INDUSTRY_SPECIFIC,
                                                                                              eDocType,
                                                                                              aCountry))
      {
        aPyramid.addValidationLayer (eArtefact);
      }

    // Don't use a StringReader here, because it is closed while processing the
    // stuff
    final ValidationPyramidResult aResult = aPyramid.applyValidation (new DOMSource (aXMLNode));
    final long nMillis = aSW.stopAndGetMillis ();
    s_aStatsDuration.addTime (nMillis);
    AuditHelper.onAuditExecuteSuccess ("validation-pyramid",
                                       eSyntaxBinding.getID (),
                                       eDocType.getID (),
                                       eTransaction.getID (),
                                       aCountry,
                                       Boolean.valueOf (bIndustrySpecificRules),
                                       Integer.valueOf (aResult.getAggregatedResults ().getFailureCount ()),
                                       Integer.valueOf (aResult.getAggregatedResults ().getErrorCount ()));
    return aResult;
  }

  @Nonnull
  public static IMicroDocument getAsXML (@Nonnull final String sRequestor,
                                         @Nonnull final DateTime aDT,
                                         final long nDurationMillis,
                                         @Nonnull final ValidationPyramidResult aResult,
                                         @Nonnull final Locale aDisplayLocale,
                                         @Nonnull final ERequestSource eRequestSource)
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement ("results");
    // Header
    final IMicroElement eHeader = eRoot.appendElement ("header");
    eHeader.appendElement ("requestor").appendText (sRequestor);
    eHeader.appendElement ("datetime").appendText (PDTWebDateHelper.getAsStringW3C (aDT));
    eHeader.appendElement ("duration").appendText (Long.toString (nDurationMillis));
    eHeader.appendElement ("requestsource").appendText (eRequestSource.getID ());
    // Result
    final IMicroElement eResult = eRoot.appendElement ("result");
    eResult.setAttribute ("syntax", aResult.getValidationTransaction ().getSyntaxBinding ().getID ());
    eResult.setAttribute ("doctype", aResult.getValidationDocumentType ().getID ());
    if (!aResult.isValidationCountryIndependent ())
      eResult.setAttribute ("country", aResult.getValidationCountry ().getCountry ());
    eResult.setAttribute ("transaction", aResult.getValidationTransaction ().getTransaction ().getID ());
    eResult.setAttribute ("interrupted", Boolean.valueOf (aResult.isValidationInterrupted ()).toString ());

    // Result layers
    for (final ValidationPyramidResultLayer aResultLayer : aResult.getAllValidationResultLayers ())
    {
      final IMicroElement eLayer = eResult.appendElement ("layer");
      eLayer.setAttribute ("level", aResultLayer.getValidationLevel ().getID ());
      eLayer.setAttribute ("type", aResultLayer.getXMLValidationType ().getID ());
      for (final IResourceError aError : aResultLayer.getValidationErrors ())
      {
        final IMicroElement eError = eLayer.appendElement ("error");
        // error location
        final IMicroElement eLocation = eError.appendElement ("location");
        if (StringHelper.hasText (aError.getLocation ().getResourceID ()))
          eLocation.setAttribute ("resourceid", aError.getLocation ().getResourceID ());
        if (aError.getLocation ().getLineNumber () != IResourceLocation.ILLEGAL_NUMBER)
          eLocation.setAttribute ("linenum", aError.getLocation ().getLineNumber ());
        if (aError.getLocation ().getColumnNumber () != IResourceLocation.ILLEGAL_NUMBER)
          eLocation.setAttribute ("colnum", aError.getLocation ().getColumnNumber ());
        if (StringHelper.hasText (aError.getLocation ().getField ()))
          eLocation.setAttribute ("field", aError.getLocation ().getField ());
        // error other fields
        eError.appendElement ("level").appendText (aError.getErrorLevel ().getID ());
        eError.appendElement ("text").appendText (aError.getDisplayText (aDisplayLocale));
        if (aError.getLinkedException () != null)
        {
          final IMicroElement eException = eError.appendElement ("exception");
          eException.appendElement ("message", aError.getLinkedException ().getMessage ());
          eException.appendElement ("stacktrace", StackTraceHelper.getStackAsString (aError.getLinkedException ()));
        }
        if (aError instanceof SVRLResourceError)
          eError.appendElement ("test").appendText (((SVRLResourceError) aError).getTest ());
      }
    }

    return aDoc;
  }
}
