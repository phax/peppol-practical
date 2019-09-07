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
package com.helger.peppol.app;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.StringHelper;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.smp.ISMPTransportProfile;
import com.helger.peppol.url.BDXLURLProvider;
import com.helger.peppol.url.IPeppolURLProvider;
import com.helger.peppol.url.PeppolURLProvider;
import com.helger.peppolid.factory.BDXR1IdentifierFactory;
import com.helger.peppolid.factory.BDXR2IdentifierFactory;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.peppolid.peppol.pidscheme.EPredefinedParticipantIdentifierScheme;

/**
 * Misc utility methods
 *
 * @author Philip Helger
 */
@Immutable
public final class AppHelper
{
  private AppHelper ()
  {}

  @Nonnull
  @Nonempty
  public static String getApplicationTitle ()
  {
    return "PEPPOL practical" + (AppSettings.isTestVersion () ? " [TEST]" : "");
  }

  @Nullable
  public static EPredefinedParticipantIdentifierScheme getParticipantIdentifierSchemeOfID (@Nullable final String sSchemeID)
  {
    if (StringHelper.hasText (sSchemeID))
      for (final EPredefinedParticipantIdentifierScheme eAgency : EPredefinedParticipantIdentifierScheme.values ())
        if (eAgency.getISO6523Code ().equals (sSchemeID) || eAgency.getSchemeID ().equals (sSchemeID))
          return eAgency;
    return null;
  }

  @Nullable
  public static String getSMPTransportProfileShortName (@Nullable final ISMPTransportProfile aTransportProfile)
  {
    return aTransportProfile == null ? null : aTransportProfile.getName ();
  }

  public static boolean isSMKToop (@Nonnull final ISMLInfo aSML)
  {
    // TODO make configurable
    return "SMK TOOP".equals (aSML.getDisplayName ());
  }

  @Nonnull
  public static ESMPAPIType findSMPAPIType (@Nonnull final ISMLInfo aSML)
  {
    return isSMKToop (aSML) ? ESMPAPIType.OASIS_BDXR_V1 : ESMPAPIType.PEPPOL;
  }

  @Nonnull
  public static IIdentifierFactory getIdentifierFactory (@Nonnull final ISMLInfo aSML, @Nonnull final ESMPAPIType eSMP)
  {
    if (isSMKToop (aSML))
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
  public static IPeppolURLProvider getURLProvider (@Nonnull final ESMPAPIType eAPIType)
  {
    return eAPIType == ESMPAPIType.PEPPOL ? PeppolURLProvider.INSTANCE : BDXLURLProvider.INSTANCE;
  }
}
