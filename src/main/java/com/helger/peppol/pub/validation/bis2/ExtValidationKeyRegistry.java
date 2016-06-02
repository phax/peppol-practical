/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ext.CommonsLinkedHashMap;
import com.helger.commons.collection.ext.ICommonsOrderedMap;
import com.helger.peppol.validation.api.ValidationKey;
import com.helger.peppol.validation.engine.peppol.EPeppolStandardValidationSchematronArtefact;
import com.helger.peppol.validation.engine.peppol.EPeppolThirdPartyValidationSchematronArtefact;

@Immutable
public final class ExtValidationKeyRegistry
{
  private static ICommonsOrderedMap <String, ExtValidationKey> s_aKeys;

  static
  {
    final ICommonsOrderedMap <String, ExtValidationKey> aKeys = new CommonsLinkedHashMap <> ();
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
    s_aKeys = CollectionHelper.getSortedByValue (aKeys);
  }

  private ExtValidationKeyRegistry ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <String, ExtValidationKey> getAllSorted ()
  {
    return s_aKeys.getClone ();
  }

  @Nullable
  public static ExtValidationKey getFromID (@Nullable final String sID)
  {
    return s_aKeys.get (sID);
  }
}
