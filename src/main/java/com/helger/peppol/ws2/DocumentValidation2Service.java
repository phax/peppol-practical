/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.peppol.ws2;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.w3c.dom.Document;

import com.helger.bdve.executorset.IValidationExecutorSet;
import com.helger.bdve.executorset.VESID;
import com.helger.bdve.result.ValidationResult;
import com.helger.bdve.result.ValidationResultList;
import com.helger.bdve.source.ValidationSource;
import com.helger.commons.error.IError;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.StringHelper;
import com.helger.peppol.app.CApp;
import com.helger.peppol.pub.validation.bis2.ExtValidationKeyRegistry;
import com.helger.web.scope.mgr.WebScopeManager;
import com.helger.xml.serialize.read.DOMReader;

@WebService (endpointInterface = "com.helger.peppol.ws2.IDocumentValidation2")
public class DocumentValidation2Service implements IDocumentValidation2
{
  private static final IMutableStatisticsHandlerCounter s_aCounterTotal = StatisticsManager.getCounterHandler (DocumentValidation2Service.class.getName () +
                                                                                                               "$total");
  private static final IMutableStatisticsHandlerCounter s_aCounterAPISuccess = StatisticsManager.getCounterHandler (DocumentValidation2Service.class.getName () +
                                                                                                                    "$api-success");
  private static final IMutableStatisticsHandlerCounter s_aCounterAPIError = StatisticsManager.getCounterHandler (DocumentValidation2Service.class.getName () +
                                                                                                                  "$api-error");
  private static final IMutableStatisticsHandlerCounter s_aCounterValidationSuccess = StatisticsManager.getCounterHandler (DocumentValidation2Service.class.getName () +
                                                                                                                           "$validation-success");
  private static final IMutableStatisticsHandlerCounter s_aCounterValidationError = StatisticsManager.getCounterHandler (DocumentValidation2Service.class.getName () +
                                                                                                                         "$validation-error");

  @Resource
  private WebServiceContext m_aWSContext;

  public DocumentValidation2Result executeValidation (@Nonnull final String sVESID,
                                                      @Nonnull final String sXMLDoc,
                                                      @Nullable final String sDisplayLocale)
  {
    final HttpServletRequest aHttpRequest = (HttpServletRequest) m_aWSContext.getMessageContext ()
                                                                             .get (MessageContext.SERVLET_REQUEST);
    final HttpServletResponse aHttpResponse = (HttpServletResponse) m_aWSContext.getMessageContext ()
                                                                                .get (MessageContext.SERVLET_RESPONSE);

    // Start request scope
    WebScopeManager.onRequestBegin (getClass ().getName (), aHttpRequest, aHttpResponse);
    try
    {
      // Track total invocation
      s_aCounterTotal.increment ();

      // Interpret parameters
      final VESID aVESID = VESID.parseIDOrNull (sVESID);
      Document aXMLDoc = null;
      try
      {
        aXMLDoc = DOMReader.readXMLDOM (sXMLDoc);
      }
      catch (final Exception ex)
      {
        // fall-through
      }
      final Locale aDisplayLocale = StringHelper.hasText (sDisplayLocale) ? LocaleCache.getInstance ()
                                                                                       .getLocale (sDisplayLocale)
                                                                          : CApp.DEFAULT_LOCALE;

      // Result object
      final DocumentValidation2Result ret = new DocumentValidation2Result ();
      ret.setReturnCode (EValidationServiceReturnCode.NO_ERROR);

      // Check input parameters
      if (aVESID == null)
        ret.setReturnCode (EValidationServiceReturnCode.INVALID_VESID);
      else
      {
        final IValidationExecutorSet aVES = ExtValidationKeyRegistry.getFromIDOrNull (aVESID);
        if (aVES == null)
          ret.setReturnCode (EValidationServiceReturnCode.UNSUPPORTED_VESID);
        else
          if (aXMLDoc == null)
            ret.setReturnCode (EValidationServiceReturnCode.INVALID_XML);
          else
            if (aDisplayLocale == null)
              ret.setReturnCode (EValidationServiceReturnCode.INVALID_DISPLAYLOCALE);
            else
            {
              // All input parameters are valid!
              // Start validating
              final ValidationResultList aVRL = aVES.getExecutorManager ()
                                                    .executeValidation (ValidationSource.create ("uploaded-file",
                                                                                                 aXMLDoc));

              boolean bValidationInterrupted = false;
              EErrorLevel aMostSevere = EErrorLevel.LOWEST;
              for (final ValidationResult aVR : aVRL)
              {
                if (aVR.isIgnored ())
                  bValidationInterrupted = true;

                for (final IError aError : aVR.getErrorList ())
                {
                  if (aError.getErrorLevel ().isMoreSevereThan (aMostSevere))
                    aMostSevere = (EErrorLevel) aError.getErrorLevel ();
                }
              }
              ret.setValidationInterrupted (bValidationInterrupted);
              ret.setMostSevereErrorLevel (aMostSevere);

              // Track validation result
              if (ret.getMostSevereErrorLevel ().isError ())
                s_aCounterValidationError.increment ();
              else
                s_aCounterValidationSuccess.increment ();
            }
      }

      // Track API result
      if (ret.getReturnCode ().isSuccess ())
        s_aCounterAPISuccess.increment ();
      else
        s_aCounterAPIError.increment ();

      return ret;
    }
    finally
    {
      WebScopeManager.onRequestEnd ();
    }
  }
}
