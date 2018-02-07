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

import java.util.Comparator;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.bdve.cii.CIIValidation;
import com.helger.bdve.ehf.EHFValidation;
import com.helger.bdve.en16931.EN16931Validation;
import com.helger.bdve.energieefactuur.EnergieEFactuurValidation;
import com.helger.bdve.executorset.IValidationExecutorSet;
import com.helger.bdve.executorset.VESID;
import com.helger.bdve.executorset.ValidationExecutorSetRegistry;
import com.helger.bdve.oioubl.OIOUBLValidation;
import com.helger.bdve.peppol.PeppolValidation;
import com.helger.bdve.simplerinvoicing.SimplerInvoicingValidation;
import com.helger.bdve.ubl.UBLValidation;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.name.IHasDisplayName;

@Immutable
public final class ExtValidationKeyRegistry
{
  public static final ValidationExecutorSetRegistry VES_REGISTRY = new ValidationExecutorSetRegistry ();
  static
  {
    PeppolValidation.initStandard (VES_REGISTRY);
    PeppolValidation.initThirdParty (VES_REGISTRY);
    SimplerInvoicingValidation.initSimplerInvoicing (VES_REGISTRY);
    EN16931Validation.initEN16931 (VES_REGISTRY);
    EHFValidation.initEHF (VES_REGISTRY);
    UBLValidation.initUBL21 (VES_REGISTRY);
    CIIValidation.initCIID16B (VES_REGISTRY);
    EnergieEFactuurValidation.initEnergieEFactuur (VES_REGISTRY);
    OIOUBLValidation.initOIOUBL (VES_REGISTRY);
  }

  private ExtValidationKeyRegistry ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <VESID, IValidationExecutorSet> getAllSortedByDisplayName (@Nonnull final Locale aDisplayLocale)
  {
    final ICommonsMap <VESID, IValidationExecutorSet> aMap = new CommonsHashMap <> (VES_REGISTRY.getAll (),
                                                                                    x -> x.getID (),
                                                                                    x -> x);
    return aMap.getSortedByValue (IHasDisplayName.getComparatorCollating (aDisplayLocale));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <VESID, IValidationExecutorSet> getAllSortedByID ()
  {
    final ICommonsMap <VESID, IValidationExecutorSet> aMap = new CommonsHashMap <> (VES_REGISTRY.getAll (),
                                                                                    x -> x.getID (),
                                                                                    x -> x);
    return aMap.getSortedByKey (Comparator.naturalOrder ());
  }

  @Nullable
  public static IValidationExecutorSet getFromIDOrNull (@Nullable final VESID aID)
  {
    return VES_REGISTRY.getOfID (aID);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <IValidationExecutorSet> getAll ()
  {
    return VES_REGISTRY.getAll ();
  }

  public static void cleanupOnShutdown ()
  {
    VES_REGISTRY.removeAll ();
  }
}
