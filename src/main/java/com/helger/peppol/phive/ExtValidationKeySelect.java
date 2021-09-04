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
package com.helger.peppol.phive;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.html.request.IHCRequestField;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.api.executorset.VESID;
import com.helger.phive.engine.source.IValidationSourceXML;
import com.helger.phive.peppol.legacy.PeppolValidation3_10_0;
import com.helger.photon.uicore.html.select.HCExtSelect;

@SuppressWarnings ("deprecation")
public final class ExtValidationKeySelect extends HCExtSelect
{
  // Explicit exclude certain artefacts (because the same artefacts are
  // registered with a different name - e.g. "order" instead of "t01")
  // Later versions only register the "order" version
  private static final ICommonsSet <VESID> LEGACY_IDS = new CommonsHashSet <> (PeppolValidation3_10_0.VID_OPENPEPPOL_T01_V3,
                                                                               PeppolValidation3_10_0.VID_OPENPEPPOL_T16_V3,
                                                                               PeppolValidation3_10_0.VID_OPENPEPPOL_T19_V3,
                                                                               PeppolValidation3_10_0.VID_OPENPEPPOL_T58_V3,
                                                                               PeppolValidation3_10_0.VID_OPENPEPPOL_T71_V3,
                                                                               PeppolValidation3_10_0.VID_OPENPEPPOL_T76_V3,
                                                                               PeppolValidation3_10_0.VID_OPENPEPPOL_T77_V3,
                                                                               PeppolValidation3_10_0.VID_OPENPEPPOL_T110_V3,
                                                                               PeppolValidation3_10_0.VID_OPENPEPPOL_T111_V3);

  public ExtValidationKeySelect (@Nonnull final IHCRequestField aRF, @Nonnull final Locale aDisplayLocale)
  {
    super (aRF);
    for (final Map.Entry <VESID, IValidationExecutorSet <IValidationSourceXML>> aEntry : ExtValidationKeyRegistry.getAllSortedByDisplayName (aDisplayLocale)
                                                                                                                 .entrySet ())
      if (!LEGACY_IDS.contains (aEntry.getKey ()))
        addOption (aEntry.getKey ().getAsSingleID (),
                   aEntry.getValue ().getDisplayName () + (aEntry.getValue ().isDeprecated () ? " (deprecated!)" : ""));
    addOptionPleaseSelect (aDisplayLocale);
  }
}
