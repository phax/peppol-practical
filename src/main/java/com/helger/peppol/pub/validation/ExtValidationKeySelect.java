/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.peppol.pub.validation;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.bdve.executorset.IValidationExecutorSet;
import com.helger.bdve.executorset.VESID;
import com.helger.html.request.IHCRequestField;
import com.helger.photon.uicore.html.select.HCExtSelect;

public final class ExtValidationKeySelect extends HCExtSelect
{
  public ExtValidationKeySelect (@Nonnull final IHCRequestField aRF, @Nonnull final Locale aDisplayLocale)
  {
    super (aRF);
    for (final Map.Entry <VESID, IValidationExecutorSet> aEntry : ExtValidationKeyRegistry.getAllSorted (aDisplayLocale)
                                                                                          .entrySet ())
      addOption (aEntry.getKey ().getAsSingleID (),
                 aEntry.getValue ().getDisplayName () + (aEntry.getValue ().isDeprecated () ? " (deprecated!)" : ""));
    addOptionPleaseSelect (aDisplayLocale);
  }
}
