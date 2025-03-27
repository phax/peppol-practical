/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.mutable.MutableInt;
import com.helger.commons.string.StringHelper;
import com.helger.ddd.model.DDDValueProviderList;
import com.helger.ddd.model.DDDValueProviderPerSyntax;
import com.helger.ddd.model.DDDValueProviderPerSyntax.ISelectorCallback;
import com.helger.ddd.model.EDDDDeterminedField;
import com.helger.ddd.model.VPSourceValue;
import com.helger.diver.api.coord.DVRCoordinate;
import com.helger.peppol.sharedui.validate.VESRegistry;

public class MainCheckDDDConsistency
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCheckDDDConsistency.class);

  public static void main (final String [] args)
  {
    final MutableInt aChecks = new MutableInt (0);

    // For each value provider of each syntax
    final DDDValueProviderList aVPL = DDDValueProviderList.getDefaultValueProviderList ();
    for (final Map.Entry <String, DDDValueProviderPerSyntax> e1 : aVPL.valueProvidersPerSyntaxes ()
                                                                      .getSortedByKey (Comparator.naturalOrder ())
                                                                      .entrySet ())
    {
      LOGGER.info ("Syntax " + e1.getKey ());

      // Check each VESID selector
      e1.getValue ().forEachSelector (new ISelectorCallback ()
      {
        public void acceptFlag (final ICommonsList <VPSourceValue> aSourceValues, final String sFlag)
        {}

        public void acceptDeterminedValue (@Nonnull @Nonempty final ICommonsList <VPSourceValue> aSourceValues,
                                           @Nonnull final EDDDDeterminedField eDeterminedField,
                                           @Nonnull final String sDeterminedValue)
        {
          final String sSrcString = StringHelper.imploder ()
                                                .source (aSourceValues,
                                                         x -> '[' +
                                                              x.getSourceField ().name () +
                                                              '=' +
                                                              x.getSourceValue () +
                                                              ']')
                                                .separator ("; ")
                                                .build ();
          // We only care about the VESID
          if (eDeterminedField == EDDDDeterminedField.VESID)
          {
            aChecks.inc ();
            final String sVESID = sDeterminedValue;
            LOGGER.info ("  " + sSrcString + " -- " + sVESID);
            if (VESRegistry.getFromIDOrNull (DVRCoordinate.parseOrNull (sVESID)) == null)
              throw new IllegalStateException ("VES ID '" + sVESID + "' is unknown");
          }
        }
      });
    }
    LOGGER.info (aChecks.intValue () + " checks done");
  }
}
