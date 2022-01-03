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
package com.helger.peppol.ui.select;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.peppolid.factory.ESMPIdentifierType;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.html.select.HCExtSelect;

/**
 * UI select for SMP API types
 *
 * @author Philip Helger
 */
public class SMPIdentifierTypeSelect extends HCExtSelect
{
  public SMPIdentifierTypeSelect (@Nonnull final RequestField aRF, @Nonnull final Locale aDisplayLocale)
  {
    super (aRF);
    addOptionPleaseSelect (aDisplayLocale);
    for (final ESMPIdentifierType e : ESMPIdentifierType.values ())
      addOption (e.getID (), e.getDisplayName ());
  }
}
