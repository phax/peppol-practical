/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.peppol.ws;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.io.EAppend;
import com.helger.base.rt.StackTraceHelper;
import com.helger.base.string.StringHelper;
import com.helger.base.timing.StopWatch;
import com.helger.csv.CSVWriter;
import com.helger.datetime.helper.PDTFactory;
import com.helger.diagnostics.error.IError;
import com.helger.diagnostics.error.level.EErrorLevel;
import com.helger.diagnostics.error.level.IErrorLevel;
import com.helger.diver.api.coord.DVRCoordinate;
import com.helger.http.CHttp;
import com.helger.http.CHttpHeader;
import com.helger.io.file.FileHelper;
import com.helger.peppol.api.rest.AbstractAPIExecutor;
import com.helger.peppol.app.CPPApp;
import com.helger.peppol.validate.VESRegistry;
import com.helger.peppol.validate.config.SharedValidationConfig;
import com.helger.peppol.wsclient2.ErrorLevelType;
import com.helger.peppol.wsclient2.ItemType;
import com.helger.peppol.wsclient2.RequestType;
import com.helger.peppol.wsclient2.ResponseType;
import com.helger.peppol.wsclient2.TriStateType;
import com.helger.peppol.wsclient2.ValidateFaultError;
import com.helger.peppol.wsclient2.ValidationResultType;
import com.helger.peppol.wsclient2.WSDVSPort;
import com.helger.phive.api.execute.ValidationExecutionManager;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.api.result.ValidationResult;
import com.helger.phive.api.result.ValidationResultList;
import com.helger.phive.api.validity.EExtendedValidity;
import com.helger.phive.api.validity.IValidityDeterminator;
import com.helger.phive.xml.source.IValidationSourceXML;
import com.helger.phive.xml.source.ValidationSourceXML;
import com.helger.photon.io.WebFileIO;
import com.helger.schematron.svrl.SVRLResourceError;
import com.helger.servlet.request.RequestHelper;
import com.helger.statistics.api.IMutableStatisticsHandlerCounter;
import com.helger.statistics.api.IMutableStatisticsHandlerTimer;
import com.helger.statistics.impl.StatisticsManager;
import com.helger.text.locale.LocaleCache;
import com.helger.web.scope.mgr.WebScoped;
import com.helger.xml.serialize.read.DOMReader;

import es.moki.ratelimitj.core.limiter.request.RequestLimitRule;
import es.moki.ratelimitj.inmemory.request.InMemorySlidingWindowRequestRateLimiter;
import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

@WebService (endpointInterface = "com.helger.peppol.wsclient2.WSDVSPort")
public class WSDVS implements WSDVSPort
{
  private static final Logger LOGGER = LoggerFactory.getLogger (WSDVS.class);
  // Re-created for every restart; in combination with s_aInvocationCounter it
  // is unique
  private static final String SESSION_ID = UUID.randomUUID ().toString ();
  private static final AtomicInteger INVOCATION_COUNTER = new AtomicInteger ();
  private static final IMutableStatisticsHandlerCounter STATS_COUNTER_TOTOAL = StatisticsManager.getCounterHandler (WSDVS.class.getName () +
                                                                                                                    "$total");
  private static final IMutableStatisticsHandlerCounter STATS_COUNTER_API_SUCCESS = StatisticsManager.getCounterHandler (WSDVS.class.getName () +
                                                                                                                         "$api-success");
  private static final IMutableStatisticsHandlerCounter STATS_COUNTER_API_ERROR = StatisticsManager.getCounterHandler (WSDVS.class.getName () +
                                                                                                                       "$api-error");
  private static final IMutableStatisticsHandlerCounter STATS_COUNTER_VALIDATION_SUCCESS = StatisticsManager.getCounterHandler (WSDVS.class.getName () +
                                                                                                                                "$validation-success");
  private static final IMutableStatisticsHandlerCounter STATS_COUNTER_VALIDATION_ERROR = StatisticsManager.getCounterHandler (WSDVS.class.getName () +
                                                                                                                              "$validation-error");
  private static final IMutableStatisticsHandlerTimer STATS_TIMER = StatisticsManager.getTimerHandler (WSDVS.class);
  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();

  @Resource
  private WebServiceContext m_aWSContext;

  private final InMemorySlidingWindowRequestRateLimiter m_aRequestRateLimiter;

  public WSDVS ()
  {
    final long nDurationSecs = SharedValidationConfig.getValidationAPILimitDurationSeconds ();
    final long nRequestsInDuration = SharedValidationConfig.getValidationAPILimitRequestsInDuration ();
    if (nDurationSecs >= 2 && nRequestsInDuration > 0)
    {
      // 2 request per second, per key
      // Note: duration must be > 1 second
      m_aRequestRateLimiter = new InMemorySlidingWindowRequestRateLimiter (RequestLimitRule.of (Duration.ofSeconds (nDurationSecs),
                                                                                                nRequestsInDuration));
      LOGGER.info ("Installed validation API rate limiter with a maximum of " +
                   nRequestsInDuration +
                   " requests per " +
                   nDurationSecs +
                   " seconds");
    }
    else
    {
      m_aRequestRateLimiter = null;
      LOGGER.info ("Validation API runs without limit (" + nDurationSecs + " / " + nRequestsInDuration + ")");
    }
  }

  private static void _throw (@NonNull final String s) throws ValidateFaultError
  {
    LOGGER.error ("Error in SOAP WS validation: " + s);

    throw new ValidateFaultError (s, s);
  }

  @NonNull
  private static ErrorLevelType _convert (@NonNull final IErrorLevel aEL)
  {
    if (aEL.isGE (EErrorLevel.ERROR))
      return ErrorLevelType.ERROR;
    if (aEL.isGE (EErrorLevel.WARN))
      return ErrorLevelType.WARN;
    return ErrorLevelType.SUCCESS;
  }

  @NonNull
  public ResponseType validate (@NonNull final RequestType aValidationRequest) throws ValidateFaultError
  {
    final HttpServletRequest aHttpRequest = (HttpServletRequest) m_aWSContext.getMessageContext ()
                                                                             .get (MessageContext.SERVLET_REQUEST);
    final HttpServletResponse aHttpResponse = (HttpServletResponse) m_aWSContext.getMessageContext ()
                                                                                .get (MessageContext.SERVLET_RESPONSE);

    final String sRateLimitKey = "ip:" + aHttpRequest.getRemoteAddr ();
    final boolean bOverRateLimit = m_aRequestRateLimiter != null ? m_aRequestRateLimiter.overLimitWhenIncremented (
                                                                                                                   sRateLimitKey)
                                                                 : false;

    final String sInvocationUniqueID = Integer.toString (INVOCATION_COUNTER.incrementAndGet ());

    RW_LOCK.writeLocked ( () -> {
      // Just append to file
      try (final CSVWriter w = new CSVWriter (FileHelper.getPrintWriter (WebFileIO.getDataIO ()
                                                                                  .getFile ("wsdvs-logs.csv"),
                                                                         EAppend.APPEND,
                                                                         StandardCharsets.ISO_8859_1)))
      {
        w.setSeparatorChar (';');
        w.writeNext (SESSION_ID,
                     sInvocationUniqueID,
                     PDTFactory.getCurrentLocalDateTime ().toString (),
                     aHttpRequest.getRemoteAddr (),
                     Boolean.toString (bOverRateLimit),
                     aValidationRequest.getVESID (),
                     Integer.toString (StringHelper.getLength (aValidationRequest.getXML ())),
                     RequestHelper.getHttpUserAgentStringFromRequest (aHttpRequest));
      }
      catch (final IOException ex)
      {
        LOGGER.error ("Error writing CSV: " + ex.getMessage ());
      }
    });

    LOGGER.info ("Start validating business document with SOAP WS; source [" +
                 aHttpRequest.getRemoteAddr () +
                 ":" +
                 aHttpRequest.getRemotePort () +
                 "]; VESID '" +
                 aValidationRequest.getVESID () +
                 "'; Payload: " +
                 StringHelper.getLength (aValidationRequest.getXML ()) +
                 " bytes;" +
                 (bOverRateLimit ? " RATE LIMIT EXCEEDED" : ""));

    // Start request scope
    try (final WebScoped aWebScoped = new WebScoped (aHttpRequest, aHttpResponse))
    {
      // Track total invocation
      STATS_COUNTER_TOTOAL.increment ();

      if (bOverRateLimit)
      {
        // Too Many Requests
        if (LOGGER.isDebugEnabled ())
          LOGGER.debug ("REST search rate limit exceeded for " + sRateLimitKey);

        final HttpServletResponse aResponse = (HttpServletResponse) m_aWSContext.getMessageContext ()
                                                                                .get (MessageContext.SERVLET_RESPONSE);
        try
        {
          aResponse.addIntHeader (CHttpHeader.RETRY_AFTER, AbstractAPIExecutor.DEFAULT_RETRY_AFTER_SECONDS);
          aResponse.sendError (CHttp.HTTP_TOO_MANY_REQUESTS);
        }
        catch (final IOException ex)
        {
          throw new UncheckedIOException (ex);
        }
        return null;
      }

      // Interpret parameters
      final String sVESID = aValidationRequest.getVESID ();
      final DVRCoordinate aVESID = DVRCoordinate.parseOrNull (sVESID);
      if (aVESID == null)
        _throw ("Syntactically invalid VESID '" + sVESID + "' provided!");
      final IValidationExecutorSet <IValidationSourceXML> aVES = VESRegistry.getFromIDOrNull (aVESID);
      if (aVES == null)
        _throw ("Unsupported VESID " + aVESID.getAsSingleID () + " provided!");

      if (aVES.getStatus ().isDeprecated ())
        LOGGER.warn ("  VESID '" + aVESID.getAsSingleID () + "' is deprecated");

      Document aXMLDoc = null;
      try
      {
        aXMLDoc = DOMReader.readXMLDOM (aValidationRequest.getXML ());
      }
      catch (final Exception ex)
      {
        // fall-through
      }
      if (aXMLDoc == null)
        _throw ("Invalid XML provided!");

      final String sDisplayLocale = aValidationRequest.getDisplayLocale ();
      final Locale aDisplayLocale = StringHelper.isNotEmpty (sDisplayLocale) ? LocaleCache.getInstance ()
                                                                                          .getLocale (sDisplayLocale)
                                                                             : CPPApp.DEFAULT_LOCALE;
      if (aDisplayLocale == null)
        _throw ("Invalid display locale '" + sDisplayLocale + "' provided!");

      // All input parameters are valid!
      LOGGER.info ("Validating by SOAP WS using " + aVESID.getAsSingleID ());

      final StopWatch aSW = StopWatch.createdStarted ();

      // Start validating
      final ValidationResultList aVRL = ValidationExecutionManager.executeValidation (IValidityDeterminator.createDefault (),
                                                                                      aVES,
                                                                                      ValidationSourceXML.create ("uploaded-file",
                                                                                                                  aXMLDoc),
                                                                                      aDisplayLocale);

      // Result object
      final ResponseType ret = new ResponseType ();

      int nWarnings = 0;
      int nErrors = 0;
      boolean bValidationInterrupted = false;
      IErrorLevel aMostSevere = EErrorLevel.LOWEST;
      for (final ValidationResult aVR : aVRL)
      {
        final ValidationResultType aVRT = new ValidationResultType ();
        if (aVR.isSkipped ())
        {
          bValidationInterrupted = true;
          aVRT.setSuccess (TriStateType.UNDEFINED);
        }
        else
        {
          final EExtendedValidity eValidity = IValidityDeterminator.createDefault ()
                                                                   .getValidity (null, aVR.getErrorList ());
          aVRT.setSuccess (eValidity.isValid () ? TriStateType.TRUE : TriStateType.FALSE);
        }
        aVRT.setArtifactType (aVR.getValidationArtefact ().getValidationType ().getID ());
        aVRT.setArtifactPath (aVR.getValidationArtefact ().getRuleResource ().getPath ());

        for (final IError aError : aVR.getErrorList ())
        {
          if (aError.getErrorLevel ().isGT (aMostSevere))
            aMostSevere = aError.getErrorLevel ();

          if (aError.getErrorLevel ().isGE (EErrorLevel.ERROR))
            nErrors++;
          else
            if (aError.getErrorLevel ().isGE (EErrorLevel.WARN))
              nWarnings++;

          final ItemType aItem = new ItemType ();
          aItem.setErrorLevel (_convert (aError.getErrorLevel ()));
          if (aError.hasErrorID ())
            aItem.setErrorID (aError.getErrorID ());
          if (aError.hasErrorFieldName ())
            aItem.setErrorFieldName (aError.getErrorFieldName ());
          if (aError.hasErrorLocation ())
            aItem.setErrorLocation (aError.getErrorLocation ().getAsString ());
          aItem.setErrorText (aError.getErrorText (aDisplayLocale));
          if (aError.hasLinkedException ())
            aItem.setException (StackTraceHelper.getStackAsString (aError.getLinkedException ()));
          if (aError instanceof SVRLResourceError)
          {
            final String sTest = ((SVRLResourceError) aError).getTest ();
            aItem.setTest (sTest);
          }
          aVRT.addItem (aItem);
        }
        ret.addResult (aVRT);
      }
      // Success if the worst that happened is a warning
      ret.setSuccess (aMostSevere.isLE (EErrorLevel.WARN));
      ret.setInterrupted (bValidationInterrupted);
      ret.setMostSevereErrorLevel (_convert (aMostSevere));

      aSW.stop ();

      LOGGER.info ("Finished validation after " +
                   aSW.getMillis () +
                   "ms; " +
                   nWarnings +
                   " warns; " +
                   nErrors +
                   " errors");
      STATS_TIMER.addTime (aSW.getMillis ());

      // Track validation result
      if (ret.getMostSevereErrorLevel ().equals (ErrorLevelType.ERROR))
        STATS_COUNTER_VALIDATION_ERROR.increment ();
      else
        STATS_COUNTER_VALIDATION_SUCCESS.increment ();

      // Track API result
      if (ret.isSuccess ())
        STATS_COUNTER_API_SUCCESS.increment ();
      else
        STATS_COUNTER_API_ERROR.increment ();

      final int nFinalWarnings = nWarnings;
      final int nFinalErrors = nErrors;
      RW_LOCK.writeLocked ( () -> {
        // Just append to file
        try (final CSVWriter w = new CSVWriter (FileHelper.getPrintWriter (WebFileIO.getDataIO ()
                                                                                    .getFile ("wsdvs-results.csv"),
                                                                           EAppend.APPEND,
                                                                           StandardCharsets.ISO_8859_1)))
        {
          w.setSeparatorChar (';');
          w.writeNext (SESSION_ID,
                       sInvocationUniqueID,
                       PDTFactory.getCurrentLocalDateTime ().toString (),
                       Long.toString (aSW.getMillis ()),
                       Integer.toString (nFinalWarnings),
                       Integer.toString (nFinalErrors));
        }
        catch (final IOException ex)
        {
          LOGGER.error ("Error writing CSV2: " + ex.getMessage ());
        }
      });

      return ret;
    }
    finally
    {
      LOGGER.info ("Finished validating business document with SOAP WS");
    }
  }
}
