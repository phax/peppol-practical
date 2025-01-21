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
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.name.IHasDisplayName;
import com.helger.diver.api.coord.DVRCoordinate;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.api.executorset.ValidationExecutorSetRegistry;
import com.helger.phive.cii.CIIValidation;
import com.helger.phive.ciuspt.CIUS_PTValidation;
import com.helger.phive.ciusro.CIUS_ROValidation;
import com.helger.phive.ebinterface.EbInterfaceValidation;
import com.helger.phive.ehf.EHFValidation;
import com.helger.phive.en16931.EN16931Validation;
import com.helger.phive.energieefactuur.EnergieEFactuurValidation;
import com.helger.phive.facturae.FacturaeValidation;
import com.helger.phive.fatturapa.FatturaPAValidation;
import com.helger.phive.finvoice.FinvoiceValidation;
import com.helger.phive.isdoc.ISDOCValidation;
import com.helger.phive.oioubl.OIOUBLValidation;
import com.helger.phive.peppol.PeppolValidation;
import com.helger.phive.peppol.italy.PeppolItalyValidation;
import com.helger.phive.peppol.legacy.PeppolLegacyValidationBisAUNZ;
import com.helger.phive.peppol.legacy.PeppolLegacyValidationBisEurope;
import com.helger.phive.peppol.legacy.PeppolLegacyValidationSG;
import com.helger.phive.setu.SETUValidation;
import com.helger.phive.simplerinvoicing.SimplerInvoicingValidation;
import com.helger.phive.svefaktura.SvefakturaValidation;
import com.helger.phive.teapps.TEAPPSValidation;
import com.helger.phive.ubl.UBLValidation;
import com.helger.phive.ublbe.UBLBEValidation;
import com.helger.phive.xml.source.IValidationSourceXML;
import com.helger.phive.xrechnung.XRechnungValidation;
import com.helger.phive.zugferd.ZugferdValidation;

@SuppressWarnings ("deprecation")
@Immutable
public final class ExtValidationKeyRegistry
{
  public static final ValidationExecutorSetRegistry <IValidationSourceXML> VES_REGISTRY = new ValidationExecutorSetRegistry <> ();
  static
  {
    PeppolValidation.initStandard (VES_REGISTRY);
    PeppolLegacyValidationBisEurope.init (VES_REGISTRY);
    PeppolLegacyValidationBisAUNZ.init (VES_REGISTRY);
    PeppolLegacyValidationSG.init (VES_REGISTRY);
    SimplerInvoicingValidation.initSimplerInvoicing (VES_REGISTRY);
    EN16931Validation.initEN16931 (VES_REGISTRY);
    EHFValidation.initEHF (VES_REGISTRY);
    UBLValidation.initUBLAllVersions (VES_REGISTRY);
    CIIValidation.initCII (VES_REGISTRY);
    EnergieEFactuurValidation.initEnergieEFactuur (VES_REGISTRY);
    OIOUBLValidation.initOIOUBL (VES_REGISTRY);
    EbInterfaceValidation.initEbInterface (VES_REGISTRY);
    TEAPPSValidation.initTEAPPS (VES_REGISTRY);
    UBLBEValidation.initUBLBE (VES_REGISTRY);
    XRechnungValidation.initXRechnung (VES_REGISTRY);
    FatturaPAValidation.initFatturaPA (VES_REGISTRY);
    FinvoiceValidation.initFinvoice (VES_REGISTRY);
    SvefakturaValidation.initSvefaktura (VES_REGISTRY);
    FacturaeValidation.initFacturae (VES_REGISTRY);
    CIUS_PTValidation.initCIUS_PT (VES_REGISTRY);
    ISDOCValidation.initISDOC (VES_REGISTRY);
    PeppolItalyValidation.init (VES_REGISTRY);
    CIUS_ROValidation.initCIUS_RO (VES_REGISTRY);
    SETUValidation.initSETU (VES_REGISTRY);
    ZugferdValidation.initZugferd (VES_REGISTRY);
  }

  private ExtValidationKeyRegistry ()
  {}

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <DVRCoordinate, IValidationExecutorSet <IValidationSourceXML>> getAllSortedByDisplayName (@Nonnull final Locale aDisplayLocale)
  {
    final ICommonsMap <DVRCoordinate, IValidationExecutorSet <IValidationSourceXML>> aMap = new CommonsHashMap <> (VES_REGISTRY.getAll (),
                                                                                                                   IValidationExecutorSet::getID,
                                                                                                                   x -> x);
    return aMap.getSortedByValue (IHasDisplayName.getComparatorCollating (aDisplayLocale));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsOrderedMap <DVRCoordinate, IValidationExecutorSet <IValidationSourceXML>> getAllSortedByID ()
  {
    final ICommonsMap <DVRCoordinate, IValidationExecutorSet <IValidationSourceXML>> aMap = new CommonsHashMap <> (VES_REGISTRY.getAll (),
                                                                                                                   IValidationExecutorSet::getID,
                                                                                                                   x -> x);
    return aMap.getSortedByKey (Comparator.naturalOrder ());
  }

  @Nullable
  public static IValidationExecutorSet <IValidationSourceXML> getFromIDOrNull (@Nullable final DVRCoordinate aID)
  {
    return VES_REGISTRY.getOfID (aID);
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <IValidationExecutorSet <IValidationSourceXML>> getAll ()
  {
    return VES_REGISTRY.getAll ();
  }

  public static void cleanupOnShutdown ()
  {
    VES_REGISTRY.removeAll ();
  }
}
