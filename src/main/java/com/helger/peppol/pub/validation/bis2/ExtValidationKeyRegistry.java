/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.peppol.pub.validation.bis2;

import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.bdve.ValidationArtefactKey;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsLinkedHashMap;
import com.helger.commons.collection.ext.ICommonsOrderedMap;
import com.helger.peppol.validation.EPeppolStandardValidationSchematronArtefact;
import com.helger.peppol.validation.EPeppolThirdPartyValidationSchematronArtefact;

@Immutable
public final class ExtValidationKeyRegistry
{
  private static ICommonsOrderedMap <String, ValidationArtefactKey> s_aKeys;

  static
  {
    final ICommonsOrderedMap <String, ValidationArtefactKey> aKeys = new CommonsLinkedHashMap<> ();
    for (final ValidationArtefactKey aKey : EPeppolStandardValidationSchematronArtefact.getTotalValidationKeys ())
    {
      final String sID = aKey.getID ();
      if (aKeys.containsKey (sID))
        throw new IllegalStateException ("Key '" + sID + "' is already contained!");
      aKeys.put (sID, aKey);
    }
    for (final ValidationArtefactKey aKey : EPeppolThirdPartyValidationSchematronArtefact.getTotalValidationKeys ())
    {
      final String sID = aKey.getID ();
      if (aKeys.containsKey (sID))
        throw new IllegalStateException ("Key '" + sID + "' is already contained!");
      aKeys.put (sID, aKey);
    }

    // Sort only once
    s_aKeys = aKeys.getSortedByValue (Comparator.naturalOrder ());
  }

  private ExtValidationKeyRegistry ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <String, ValidationArtefactKey> getAllSorted ()
  {
    return s_aKeys.getClone ();
  }

  @Nullable
  public static ValidationArtefactKey getFromIDOrNull (@Nullable final String sID)
  {
    return s_aKeys.get (sID);
  }

  @Nonnull
  @Nonempty
  public static String getDisplayText (@Nonnull final ValidationArtefactKey aVK, @Nonnull final Locale aDisplayLocale)
  {
    String ret = aVK.getBusinessSpecification ().getDisplayName () +
                 "; transaction " +
                 aVK.getTransaction ().getName ();
    if (aVK.isCountrySpecific ())
      ret += "; Country " + aVK.getCountryLocale ().getDisplayCountry (aDisplayLocale);
    if (aVK.isSectorSpecific ())
      ret += "; Sector: " + aVK.getSectorKey ().getDisplayName ();
    return ret;
  }
}
