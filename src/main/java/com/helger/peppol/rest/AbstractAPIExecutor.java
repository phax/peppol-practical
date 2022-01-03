/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import java.time.Duration;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.http.CHttp;
import com.helger.peppol.app.AppConfig;
import com.helger.photon.api.IAPIDescriptor;
import com.helger.photon.api.IAPIExecutor;
import com.helger.servlet.response.UnifiedResponse;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import es.moki.ratelimitj.core.limiter.request.RequestLimitRule;
import es.moki.ratelimitj.inmemory.request.InMemorySlidingWindowRequestRateLimiter;

public abstract class AbstractAPIExecutor implements IAPIExecutor
{
  private static final Logger LOGGER = LoggerFactory.getLogger (AbstractAPIExecutor.class);

  protected final InMemorySlidingWindowRequestRateLimiter m_aRequestRateLimiter;

  protected AbstractAPIExecutor ()
  {
    final long nRequestsPerSec = AppConfig.getRESTAPIMaxRequestsPerSecond ();
    if (nRequestsPerSec > 0)
    {
      // 2 request per second, per key
      // Note: duration must be > 1 second
      m_aRequestRateLimiter = new InMemorySlidingWindowRequestRateLimiter (RequestLimitRule.of (Duration.ofSeconds (2),
                                                                                                nRequestsPerSec * 2));
      LOGGER.info ("Installed REST API rate limiter with a maximum of " +
                   nRequestsPerSec +
                   " requests per second for class " +
                   getClass ().getSimpleName ());
    }
    else
    {
      m_aRequestRateLimiter = null;
      LOGGER.info ("REST API runs without limit");
    }
  }

  protected abstract void rateLimitedInvokeAPI (@Nonnull IAPIDescriptor aAPIDescriptor,
                                                @Nonnull @Nonempty String sPath,
                                                @Nonnull Map <String, String> aPathVariables,
                                                @Nonnull IRequestWebScopeWithoutResponse aRequestScope,
                                                @Nonnull UnifiedResponse aUnifiedResponse) throws Exception;

  public final void invokeAPI (@Nonnull final IAPIDescriptor aAPIDescriptor,
                               @Nonnull @Nonempty final String sPath,
                               @Nonnull final Map <String, String> aPathVariables,
                               @Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                               @Nonnull final UnifiedResponse aUnifiedResponse) throws Exception
  {
    final String sRateLimitKey = "ip:" + aRequestScope.getRemoteAddr ();
    final boolean bOverRateLimit = m_aRequestRateLimiter != null ? m_aRequestRateLimiter.overLimitWhenIncremented (sRateLimitKey) : false;

    if (bOverRateLimit)
    {
      // Too Many Requests
      if (LOGGER.isDebugEnabled ())
        LOGGER.debug ("REST search rate limit exceeded for " + sRateLimitKey);

      aUnifiedResponse.setStatus (CHttp.HTTP_TOO_MANY_REQUESTS);
    }
    else
    {
      rateLimitedInvokeAPI (aAPIDescriptor, sPath, aPathVariables, aRequestScope, aUnifiedResponse);
    }
  }
}
