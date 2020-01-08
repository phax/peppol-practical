/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.url.BDXLURLProvider;
import com.helger.peppol.url.IPeppolURLProvider;
import com.helger.peppol.url.PeppolDNSResolutionException;
import com.helger.peppol.url.PeppolURLProvider;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.BDXR1IdentifierFactory;
import com.helger.peppolid.factory.BDXR2IdentifierFactory;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.peppolid.factory.SimpleIdentifierFactory;

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

  private static boolean _isSMKToop (@Nonnull final ISMLInfo aSML)
  {
    // TODO make configurable
    return "SMK TOOP".equals (aSML.getDisplayName ());
  }

  @Nonnull
  private static ESMPAPIType _findSMPAPIType (@Nonnull final ISMLInfo aSML)
  {
    return _isSMKToop (aSML) ? ESMPAPIType.OASIS_BDXR_V1 : ESMPAPIType.PEPPOL;
  }

  @Nonnull
  private static IIdentifierFactory _getIdentifierFactory (@Nonnull final ISMLInfo aSML, @Nonnull final ESMPAPIType eSMP)
  {
    if (_isSMKToop (aSML))
      return SimpleIdentifierFactory.INSTANCE;

    switch (eSMP)
    {
      case PEPPOL:
        return PeppolIdentifierFactory.INSTANCE;
      case OASIS_BDXR_V1:
        return BDXR1IdentifierFactory.INSTANCE;
      case OASIS_BDXR_V2:
        return BDXR2IdentifierFactory.INSTANCE;
    }
    throw new IllegalStateException ();
  }

  @Nonnull
  private static IPeppolURLProvider _getURLProvider (@Nonnull final ESMPAPIType eAPIType)
  {
    return eAPIType == ESMPAPIType.PEPPOL ? PeppolURLProvider.INSTANCE : BDXLURLProvider.INSTANCE;
  }

  @Nullable
  public static SMPQueryParams createForSML (@Nonnull final ISMLInfo aCurSML,
                                             @Nullable final String sParticipantIDScheme,
                                             @Nullable final String sParticipantIDValue)
  {
    final SMPQueryParams ret = new SMPQueryParams ();
    ret.m_eSMPAPIType = _findSMPAPIType (aCurSML);
    ret.m_aIF = _getIdentifierFactory (aCurSML, ret.m_eSMPAPIType);
    ret.m_aParticipantID = ret.m_aIF.createParticipantIdentifier (sParticipantIDScheme, sParticipantIDValue);
    if (ret.m_aParticipantID == null)
    {
      // Participant ID is invalid for this scheme
      return null;
    }
    try
    {
      ret.m_aSMPHostURI = _getURLProvider (ret.m_eSMPAPIType).getSMPURIOfParticipant (ret.m_aParticipantID, aCurSML);
    }
    catch (final PeppolDNSResolutionException ex)
    {
      // For BDXL lookup -> no such participant
      return null;
    }
    return ret;
  }
}
