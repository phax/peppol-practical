package com.helger.peppol.pub.validation.bis2;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import com.helger.html.request.IHCRequestField;
import com.helger.photon.uicore.html.select.HCExtSelect;

public final class ExtValidationKeySelect extends HCExtSelect
{
  public ExtValidationKeySelect (@Nonnull final IHCRequestField aRF, @Nonnull final Locale aDisplayLocale)
  {
    super (aRF);
    for (final Map.Entry <String, ExtValidationKey> aEntry : ExtValidationKeyRegistry.getAllSorted ().entrySet ())
      addOption (aEntry.getKey (), aEntry.getValue ().getDisplayName (aDisplayLocale));
    addOptionPleaseSelect (aDisplayLocale);
  }
}