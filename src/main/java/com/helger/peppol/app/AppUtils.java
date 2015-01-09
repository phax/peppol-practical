/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.string.StringHelper;

import eu.europa.ec.cipa.peppol.identifier.issuingagency.EPredefinedIdentifierIssuingAgency;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.smp.client.ESMPTransportProfile;

/**
 * Misc utility methods
 *
 * @author Philip Helger
 */
@Immutable
public final class AppUtils
{
  private AppUtils ()
  {}

  @Nonnull
  @Nonempty
  public static String getApplicationTitle ()
  {
    return "PEPPOL practical" + (AppSettings.isTestVersion () ? " [TEST]" : "");
  }

  @Nullable
  public static ISMLInfo getSMLOfID (@Nullable final String sSML)
  {
    if (StringHelper.hasText (sSML))
      for (final ESML eSML : ESML.values ())
        if (eSML.name ().equals (sSML))
          return eSML;
    return null;
  }

  @Nullable
  public static EPredefinedIdentifierIssuingAgency getIdentifierIssuingAgencyOfID (@Nullable final String sSchemeID)
  {
    if (StringHelper.hasText (sSchemeID))
      for (final EPredefinedIdentifierIssuingAgency eAgency : EPredefinedIdentifierIssuingAgency.values ())
        if (eAgency.getISO6523Code ().equals (sSchemeID) || eAgency.getSchemeID ().equals (sSchemeID))
          return eAgency;
    return null;
  }

  @Nullable
  public static String getSMPTransportProfileShortName (@Nullable final ESMPTransportProfile eTransportProfile)
  {
    if (eTransportProfile != null)
      switch (eTransportProfile)
      {
        case TRANSPORT_PROFILE_AS2:
          return "AS2";
        case TRANSPORT_PROFILE_START:
          return "START";
      }
    return null;
  }
}
