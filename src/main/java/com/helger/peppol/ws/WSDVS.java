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
package com.helger.peppol.ws;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.bdve.executorset.IValidationExecutorSet;
import com.helger.bdve.executorset.VESID;
import com.helger.bdve.result.ValidationResult;
import com.helger.bdve.result.ValidationResultList;
import com.helger.bdve.source.ValidationSource;
import com.helger.commons.error.IError;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.lang.StackTraceHelper;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.IMutableStatisticsHandlerTimer;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.StringHelper;
import com.helger.commons.timing.StopWatch;
import com.helger.peppol.app.CPPApp;
import com.helger.peppol.bdve.ExtValidationKeyRegistry;
import com.helger.peppol.wsclient2.ErrorLevelType;
import com.helger.peppol.wsclient2.ItemType;
import com.helger.peppol.wsclient2.RequestType;
import com.helger.peppol.wsclient2.ResponseType;
import com.helger.peppol.wsclient2.TriStateType;
import com.helger.peppol.wsclient2.ValidateFaultError;
import com.helger.peppol.wsclient2.ValidationResultType;
import com.helger.peppol.wsclient2.WSDVSPort;
import com.helger.schematron.svrl.SVRLResourceError;
import com.helger.web.scope.mgr.WebScoped;
import com.helger.xml.serialize.read.DOMReader;

@WebService (endpointInterface = "com.helger.peppol.wsclient2.WSDVSPort")
public class WSDVS implements WSDVSPort
{
  private static final Logger LOGGER = LoggerFactory.getLogger (WSDVS.class);
  private static final IMutableStatisticsHandlerCounter s_aCounterTotal = StatisticsManager.getCounterHandler (WSDVS.class.getName () +
                                                                                                               "$total");
  private static final IMutableStatisticsHandlerCounter s_aCounterAPISuccess = StatisticsManager.getCounterHandler (WSDVS.class.getName () +
                                                                                                                    "$api-success");
  private static final IMutableStatisticsHandlerCounter s_aCounterAPIError = StatisticsManager.getCounterHandler (WSDVS.class.getName () +
                                                                                                                  "$api-error");
  private static final IMutableStatisticsHandlerCounter s_aCounterValidationSuccess = StatisticsManager.getCounterHandler (WSDVS.class.getName () +
                                                                                                                           "$validation-success");
  private static final IMutableStatisticsHandlerCounter s_aCounterValidationError = StatisticsManager.getCounterHandler (WSDVS.class.getName () +
                                                                                                                         "$validation-error");
  private static final IMutableStatisticsHandlerTimer s_aTimer = StatisticsManager.getTimerHandler (WSDVS.class);

  @Resource
  private WebServiceContext m_aWSContext;

  private static void _throw (@Nonnull final String s) throws ValidateFaultError
  {
    if (LOGGER.isErrorEnabled ())
      LOGGER.error ("Error in WS validation: " + s);

    throw new ValidateFaultError (s, s);
  }

  @Nonnull
  private static ErrorLevelType _convert (@Nonnull final IErrorLevel aEL)
  {
    if (aEL.isGE (EErrorLevel.ERROR))
      return ErrorLevelType.ERROR;
    if (aEL.isGE (EErrorLevel.WARN))
      return ErrorLevelType.WARN;
    return ErrorLevelType.SUCCESS;
  }

  @Nonnull
  public ResponseType validate (@Nonnull final RequestType aRequest) throws ValidateFaultError
  {
    if (LOGGER.isInfoEnabled ())
      LOGGER.info ("Start validating business document via WS");

    final HttpServletRequest aHttpRequest = (HttpServletRequest) m_aWSContext.getMessageContext ()
                                                                             .get (MessageContext.SERVLET_REQUEST);
    final HttpServletResponse aHttpResponse = (HttpServletResponse) m_aWSContext.getMessageContext ()
                                                                                .get (MessageContext.SERVLET_RESPONSE);

    // Start request scope
    try (final WebScoped aWebScoped = new WebScoped (aHttpRequest, aHttpResponse))
    {
      // Track total invocation
      s_aCounterTotal.increment ();

      // Interpret parameters
      final VESID aVESID = VESID.parseIDOrNull (aRequest.getVESID ());
      if (aVESID == null)
        _throw ("Syntactically invalid VESID provided!");
      final IValidationExecutorSet aVES = ExtValidationKeyRegistry.getFromIDOrNull (aVESID);
      if (aVES == null)
        _throw ("Unsupported VESID provided!");

      Document aXMLDoc = null;
      try
      {
        aXMLDoc = DOMReader.readXMLDOM (aRequest.getXML ());
      }
      catch (final Exception ex)
      {
        // fall-through
      }
      if (aXMLDoc == null)
        _throw ("Invalid XML provided!");

      final String sDisplayLocale = aRequest.getDisplayLocale ();
      final Locale aDisplayLocale = StringHelper.hasText (sDisplayLocale) ? LocaleCache.getInstance ()
                                                                                       .getLocale (sDisplayLocale)
                                                                          : CPPApp.DEFAULT_LOCALE;
      if (aDisplayLocale == null)
        _throw ("Invalid display locale '" + sDisplayLocale + "' provided!");

      // All input parameters are valid!
      if (LOGGER.isInfoEnabled ())
        LOGGER.info ("Validating by VS using " + aVESID.getAsSingleID ());
      final StopWatch aSW = StopWatch.createdStarted ();

      // Start validating
      final ValidationResultList aVRL = aVES.createExecutionManager ()
                                            .executeValidation (ValidationSource.create ("uploaded-file", aXMLDoc),
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
        if (aVR.isIgnored ())
        {
          bValidationInterrupted = true;
          aVRT.setSuccess (TriStateType.UNDEFINED);
        }
        else
        {
          aVRT.setSuccess (aVR.isSuccess () ? TriStateType.TRUE : TriStateType.FALSE);
        }
        aVRT.setArtifactType (aVR.getValidationArtefact ().getValidationArtefactType ().getID ());
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

      if (LOGGER.isInfoEnabled ())
        LOGGER.info ("Finished validation after " +
                     aSW.stopAndGetMillis () +
                     "ms; " +
                     nWarnings +
                     " warns; " +
                     nErrors +
                     " errors");
      s_aTimer.addTime (aSW.getMillis ());

      // Track validation result
      if (ret.getMostSevereErrorLevel ().equals (ErrorLevelType.ERROR))
        s_aCounterValidationError.increment ();
      else
        s_aCounterValidationSuccess.increment ();

      // Track API result
      if (ret.isSuccess ())
        s_aCounterAPISuccess.increment ();
      else
        s_aCounterAPIError.increment ();

      return ret;
    }
    finally
    {
      if (LOGGER.isInfoEnabled ())
        LOGGER.info ("Finished validating business document via WS");
    }
  }
}
