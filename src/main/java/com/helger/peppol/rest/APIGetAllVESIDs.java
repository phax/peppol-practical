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

import java.util.Map;

import com.helger.annotation.Nonempty;
import com.helger.base.CGlobal;
import com.helger.json.IJsonArray;
import com.helger.peppol.ui.validate.VESRegistry;
import com.helger.photon.api.IAPIDescriptor;
import com.helger.photon.app.PhotonUnifiedResponse;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

import jakarta.annotation.Nonnull;

public final class APIGetAllVESIDs extends AbstractPPAPIExecutor
{
  @Override
  protected void rateLimitedInvokeAPI (@Nonnull @Nonempty final String sLogPrefix,
                                       @Nonnull final IAPIDescriptor aAPIDescriptor,
                                       @Nonnull @Nonempty final String sPath,
                                       @Nonnull final Map <String, String> aPathVariables,
                                       @Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                                       @Nonnull final PhotonUnifiedResponse aUnifiedResponse) throws Exception
  {
    final IJsonArray aJson = VESRegistry.getAllAsJson ();

    aUnifiedResponse.json (aJson).enableCaching (1 * CGlobal.SECONDS_PER_HOUR);
  }
}
