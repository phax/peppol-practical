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
package com.helger.peppol.phive;

import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.CommonsHashSet;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.compare.IComparator;
import com.helger.commons.name.IHasDisplayName;
import com.helger.html.request.IHCRequestField;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.api.executorset.VESID;
import com.helger.phive.engine.source.IValidationSourceXML;
import com.helger.phive.peppol.legacy.PeppolValidation3_10_0;
import com.helger.photon.uicore.html.select.HCExtSelect;

@SuppressWarnings ("deprecation")
public final class ExtValidationKeySelect extends HCExtSelect
{
  public static class NaturalOrderComparator implements Comparator <String>
  {
    private final Comparator <? super String> m_aOtherComp;

    public NaturalOrderComparator (@Nonnull final Comparator <String> aOtherComp)
    {
      m_aOtherComp = aOtherComp;
    }

    private static boolean _isDigit (final char c)
    {
      return Character.isDigit (c) || c == '.' || c == ',';
    }

    private static char _charAt (final String s, final int i)
    {
      return i >= s.length () ? 0 : s.charAt (i);
    }

    private int _compareRight (final String a, final String b)
    {
      int bias = 0;
      int ia = 0, ib = 0;

      // The longest run of digits wins. That aside, the greatest
      // value wins, but we can't know that it will until we've scanned
      // both numbers to know that they have the same magnitude, so we
      // remember it in BIAS.
      for (;; ia++, ib++)
      {
        final char ca = _charAt (a, ia);
        final char cb = _charAt (b, ib);

        if (!_isDigit (ca) && !_isDigit (cb))
          return bias;
        if (!_isDigit (ca))
          return -1;
        if (!_isDigit (cb))
          return +1;
        if (ca == 0 && cb == 0)
          return bias;

        if (bias == 0)
        {
          if (ca < cb)
            bias = -1;
          else
            if (ca > cb)
              bias = +1;
        }
      }
    }

    private int _compareEqual (final String a, final String b, final int nza, final int nzb)
    {
      if (nza - nzb != 0)
        return nza - nzb;

      if (a.length () == b.length ())
        return m_aOtherComp.compare (a, b);

      return a.length () - b.length ();
    }

    public int compare (@Nonnull final String a, @Nonnull final String b)
    {
      int ia = 0;
      int ib = 0;
      int nZerosA = 0;
      int nZeroB = 0;
      char ca;
      char cb;

      while (true)
      {
        // Only count the number of zeroes leading the last number compared
        nZerosA = nZeroB = 0;

        ca = _charAt (a, ia);
        cb = _charAt (b, ib);

        // skip over leading spaces or zeros
        while (Character.isSpaceChar (ca) || ca == '0')
        {
          if (ca == '0')
            nZerosA++;
          else
          {
            // Only count consecutive zeroes
            nZerosA = 0;
          }

          ca = _charAt (a, ++ia);
        }

        while (Character.isSpaceChar (cb) || cb == '0')
        {
          if (cb == '0')
            nZeroB++;
          else
          {
            // Only count consecutive zeroes
            nZeroB = 0;
          }

          cb = _charAt (b, ++ib);
        }

        // Process run of digits
        if (Character.isDigit (ca) && Character.isDigit (cb))
        {
          final int bias = _compareRight (a.substring (ia), b.substring (ib));
          if (bias != 0)
            return bias;
        }

        if (ca == 0 && cb == 0)
        {
          // The strings compare the same. Perhaps the caller
          // will want to call strcmp to break the tie.
          return _compareEqual (a, b, nZerosA, nZeroB);
        }
        if (ca < cb)
          return -1;
        if (ca > cb)
          return +1;

        ++ia;
        ++ib;
      }
    }
  }

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
    final ICommonsList <IValidationExecutorSet <IValidationSourceXML>> aAll = ExtValidationKeyRegistry.getAll ();
    final ICommonsMap <String, IValidationExecutorSet <IValidationSourceXML>> aMap = new CommonsHashMap <> (aAll,
                                                                                                            IHasDisplayName::getDisplayName,
                                                                                                            x -> x);
    return aMap.getSortedByKey (new NaturalOrderComparator (IComparator.getComparatorCollating (aDisplayLocale))).copyOfValues ();
  }

  public ExtValidationKeySelect (@Nonnull final IHCRequestField aRF, @Nonnull final Locale aDisplayLocale)
  {
    super (aRF);

    for (final IValidationExecutorSet <IValidationSourceXML> aEntry : getAllSortedCorrect (aDisplayLocale))
      if (!LEGACY_IDS.contains (aEntry.getID ()))
        addOption (aEntry.getID ().getAsSingleID (), aEntry.getDisplayName () + (aEntry.isDeprecated () ? " (deprecated!)" : ""));
    addOptionPleaseSelect (aDisplayLocale);
  }
}
