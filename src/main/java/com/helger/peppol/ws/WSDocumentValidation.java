/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.w3c.dom.Document;

import com.helger.commons.error.IResourceError;
import com.helger.commons.error.IResourceErrorGroup;
import com.helger.commons.locale.LocaleCache;
import com.helger.commons.locale.country.CountryCache;
import com.helger.commons.stats.IStatisticsHandlerCounter;
import com.helger.commons.stats.StatisticsManager;
import com.helger.commons.string.StringHelper;
import com.helger.commons.text.impl.TextProvider;
import com.helger.commons.xml.serialize.DOMReader;
import com.helger.peppol.model.ValidationPyramidHelper;
import com.helger.peppol.ws.types.EValidationServiceReturnCode;
import com.helger.peppol.ws.types.ValidationServiceResult;
import com.helger.peppol.ws.types.ValidationServiceResultItem;
import com.helger.schematron.svrl.SVRLResourceError;
import com.helger.webscopes.mgr.WebScopeManager;

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;
import eu.europa.ec.cipa.validation.pyramid.ValidationPyramidResult;
import eu.europa.ec.cipa.validation.pyramid.ValidationPyramidResultLayer;
import eu.europa.ec.cipa.validation.rules.EValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.EValidationLevel;
import eu.europa.ec.cipa.validation.rules.EValidationSyntaxBinding;

@WebService (endpointInterface = "com.helger.peppol.ws.IDocumentValidation")
public class WSDocumentValidation implements IDocumentValidation
{
  private static final IStatisticsHandlerCounter s_aCounterTotal = StatisticsManager.getCounterHandler (WSDocumentValidation.class.getName () +
                                                                                                        "$total");
  private static final IStatisticsHandlerCounter s_aCounterAPISuccess = StatisticsManager.getCounterHandler (WSDocumentValidation.class.getName () +
                                                                                                             "$api-success");
  private static final IStatisticsHandlerCounter s_aCounterAPIError = StatisticsManager.getCounterHandler (WSDocumentValidation.class.getName () +
                                                                                                           "$api-error");
  private static final IStatisticsHandlerCounter s_aCounterValidationSuccess = StatisticsManager.getCounterHandler (WSDocumentValidation.class.getName () +
                                                                                                                    "$validation-success");
  private static final IStatisticsHandlerCounter s_aCounterValidationError = StatisticsManager.getCounterHandler (WSDocumentValidation.class.getName () +
                                                                                                                  "$validation-error");

  @Resource
  private WebServiceContext m_aWSContext;

  public ValidationServiceResult executeValidationPyramid (@Nonnull final EValidationSyntaxBinding eSyntaxBinding,
                                                           @Nonnull final EValidationDocumentType eDocType,
                                                           @Nonnull final ETransaction eTransaction,
                                                           @Nullable final String sCountry,
                                                           final boolean bIndustrySpecificRules,
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

      final ValidationServiceResult ret = new ValidationServiceResult ();

      // Interprete parameters
      final Locale aCountry = StringHelper.hasText (sCountry) ? CountryCache.getCountry (sCountry) : null;
      final Locale aDisplayLocale = StringHelper.hasText (sDisplayLocale) ? LocaleCache.getLocale (sDisplayLocale)
                                                                         : TextProvider.EN;
      Document aXMLDoc = null;
      try
      {
        aXMLDoc = DOMReader.readXMLDOM (sXMLDoc);
      }
      catch (final Exception ex)
      {
        // fall-through
      }

      // Check input parameters
      if (eSyntaxBinding == null)
        ret.setReturnCode (EValidationServiceReturnCode.INVALID_SYNTAXBINDING);
      else
        if (eDocType == null)
          ret.setReturnCode (EValidationServiceReturnCode.INVALID_DOCTYPE);
        else
          if (eTransaction == null)
            ret.setReturnCode (EValidationServiceReturnCode.INVALID_TRANSACTION);
          else
            if (StringHelper.hasText (sCountry) && aCountry == null)
              ret.setReturnCode (EValidationServiceReturnCode.INVALID_COUNTRY);
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
                  final ValidationPyramidResult res = ValidationPyramidHelper.executeValidationPyramid (eSyntaxBinding,
                                                                                                        eDocType,
                                                                                                        eTransaction,
                                                                                                        aCountry,
                                                                                                        bIndustrySpecificRules,
                                                                                                        aXMLDoc);
                  final IResourceErrorGroup aAggResults = res.getAggregatedResults ();

                  ret.setValidationInterrupted (res.isValidationInterrupted ());
                  ret.setMostSevereErrorLevel (aAggResults.getMostSevereErrorLevel ());

                  // For all layers
                  for (final ValidationPyramidResultLayer aResLayer : res.getAllValidationResultLayers ())
                  {
                    // For all errors in current layer
                    for (final IResourceError aError : aResLayer.getValidationErrors ())
                    {
                      final ValidationServiceResultItem aItem = new ValidationServiceResultItem ();
                      aItem.setValidationLevel ((EValidationLevel) aResLayer.getValidationLevel ());
                      aItem.setValidationType (aResLayer.getXMLValidationType ());
                      aItem.setErrorLevel (aError.getErrorLevel ());
                      aItem.setLocation (aError.getLocation ().getAsString ());
                      aItem.setErrorText (aError.getDisplayText (aDisplayLocale));
                      if (aError.getLinkedException () != null)
                        aItem.setExceptionMessage (aError.getLinkedException ().getMessage ());
                      if (aError instanceof SVRLResourceError)
                        aItem.setSVRLTest (((SVRLResourceError) aError).getTest ());
                      ret.getItems ().add (aItem);
                    }
                  }

                  // Track validation result
                  if (ret.getMostSevereErrorLevel ().isError ())
                    s_aCounterValidationError.increment ();
                  else
                    s_aCounterValidationSuccess.increment ();
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
