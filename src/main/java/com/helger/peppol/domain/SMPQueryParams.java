/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.peppol.domain;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.smpclient.url.BDXLURLProvider;
import com.helger.smpclient.url.ISMPURLProvider;
import com.helger.smpclient.url.PeppolURLProvider;
import com.helger.smpclient.url.SMPDNSResolutionException;

/**
 * Extracted because used some times
 *
 * @author Philip Helger
 */
public final class SMPQueryParams
{
  private ESMPAPIType m_eSMPAPIType;
  private IIdentifierFactory m_aIF;
  private IParticipantIdentifier m_aParticipantID;
  private URI m_aSMPHostURI;

  private SMPQueryParams ()
  {}

  @Nonnull
  public ESMPAPIType getSMPAPIType ()
  {
    return m_eSMPAPIType;
  }

  @Nonnull
  public IIdentifierFactory getIF ()
  {
    return m_aIF;
  }

  @Nonnull
  public IParticipantIdentifier getParticipantID ()
  {
    return m_aParticipantID;
  }

  @Nonnull
  public URI getSMPHostURI ()
  {
    return m_aSMPHostURI;
  }

  @Nonnull
  private static ISMPURLProvider _getURLProvider (@Nonnull final ESMPAPIType eAPIType)
  {
    return eAPIType == ESMPAPIType.PEPPOL ? PeppolURLProvider.INSTANCE : BDXLURLProvider.INSTANCE;
  }

  @Nullable
  public static SMPQueryParams createForSML (@Nonnull final ISMLConfiguration aCurSML,
                                             @Nullable final String sParticipantIDScheme,
                                             @Nullable final String sParticipantIDValue)
  {
    final SMPQueryParams ret = new SMPQueryParams ();
    ret.m_eSMPAPIType = aCurSML.getSMPAPIType ();
    ret.m_aIF = aCurSML.getSMPIdentifierType ().getIdentifierFactory ();
    ret.m_aParticipantID = ret.m_aIF.createParticipantIdentifier (sParticipantIDScheme, sParticipantIDValue);
    if (ret.m_aParticipantID == null)
    {
      // Participant ID is invalid for this scheme
      return null;
    }
    try
    {
      ret.m_aSMPHostURI = _getURLProvider (ret.m_eSMPAPIType).getSMPURIOfParticipant (ret.m_aParticipantID, aCurSML.getDNSZone ());
    }
    catch (final SMPDNSResolutionException ex)
    {
      // For BDXL lookup -> no such participant
      return null;
    }
    return ret;
  }
}
