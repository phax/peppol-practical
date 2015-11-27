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
package com.helger.peppol.pub.validation.bis2;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.peppol.validation.artefact.peppol.EPeppolStandardValidationSchematronArtefact;
import com.helger.peppol.validation.artefact.peppol.EPeppolThirdPartyValidationSchematronArtefact;
import com.helger.peppol.validation.domain.ValidationKey;

@Immutable
public final class ExtValidationKeyRegistry
{
  private static Map <String, ExtValidationKey> m_aKeys;

  static
  {
    final Map <String, ExtValidationKey> aKeys = new LinkedHashMap <> ();
    for (final ValidationKey aKey : EPeppolStandardValidationSchematronArtefact.getAllValidationKeys ())
    {
      final ExtValidationKey aItem = new ExtValidationKey (aKey);
      aKeys.put (aItem.getID (), aItem);
    }
    for (final ValidationKey aKey : EPeppolThirdPartyValidationSchematronArtefact.getAllValidationKeys ())
    {
      final ExtValidationKey aItem = new ExtValidationKey (aKey);
      aKeys.put (aItem.getID (), aItem);
    }

    // Sort only once
    m_aKeys = CollectionHelper.getSortedByValue (aKeys);
  }

  private ExtValidationKeyRegistry ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static Map <String, ExtValidationKey> getAllSorted ()
  {
    return CollectionHelper.newOrderedMap (m_aKeys);
  }

  @Nullable
  public static ExtValidationKey getFromID (@Nullable final String sID)
  {
    return m_aKeys.get (sID);
  }
}
