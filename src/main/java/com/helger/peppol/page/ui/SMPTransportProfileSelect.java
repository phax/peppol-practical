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
package com.helger.peppol.page.ui;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.webbasics.form.RequestField;
import com.helger.webctrls.custom.HCExtSelect;

import eu.europa.ec.cipa.smp.client.ESMPTransportProfile;

public class SMPTransportProfileSelect extends HCExtSelect
{
  @Nullable
  public static String getShortName (@Nonnull final ESMPTransportProfile eTransportProfile)
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

  public SMPTransportProfileSelect (@Nonnull final RequestField aRF, @Nonnull final Locale aDisplayLocale)
  {
    super (aRF);
    addOptionPleaseSelect (aDisplayLocale);
    for (final ESMPTransportProfile e : ESMPTransportProfile.values ())
      addOption (e.getID (), getShortName (e) + " (" + e.getID () + ")");
  }
}
