/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.compare.IComparator;
import com.helger.commons.compare.NaturalNumericOrderComparator;
import com.helger.diver.api.version.VESID;
import com.helger.html.request.IHCRequestField;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.peppol.legacy.PeppolValidation3_10_0;
import com.helger.phive.xml.source.IValidationSourceXML;
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

  @Nonnull
  @Nonempty
  public static ICommonsList <IValidationExecutorSet <IValidationSourceXML>> getAllSortedCorrect (@Nonnull final Locale aDisplayLocale)
  {
    final ICommonsList <IValidationExecutorSet <IValidationSourceXML>> aAll = new CommonsArrayList <> ();
    for (final IValidationExecutorSet <IValidationSourceXML> aEntry : ExtValidationKeyRegistry.getAll ())
      if (!LEGACY_IDS.contains (aEntry.getID ()))
        aAll.add (aEntry);

    final NaturalNumericOrderComparator aCS = new NaturalNumericOrderComparator (IComparator.getComparatorCollating (aDisplayLocale));
    return aAll.getSortedInline ( (x, y) -> {
      if (false)
        return x.getID ().compareTo (y.getID ());
      return aCS.compare (x.getDisplayName (), y.getDisplayName ());
    });
  }

  public ExtValidationKeySelect (@Nonnull final IHCRequestField aRF, @Nonnull final Locale aDisplayLocale)
  {
    super (aRF);

    for (final IValidationExecutorSet <IValidationSourceXML> aEntry : getAllSortedCorrect (aDisplayLocale))
      addOption (aEntry.getID ().getAsSingleID (),
                 aEntry.getDisplayName () + (aEntry.isDeprecated () ? " (deprecated!)" : ""));
    addOptionPleaseSelect (aDisplayLocale);
  }
}
