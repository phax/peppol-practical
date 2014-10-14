package com.helger.peppol.page;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.webbasics.form.RequestField;
import com.helger.webctrls.custom.HCExtSelect;

import eu.europa.ec.cipa.peppol.identifier.issuingagency.EPredefinedIdentifierIssuingAgency;

public class IdentifierIssuingAgencySelect extends HCExtSelect
{
  public IdentifierIssuingAgencySelect (@Nonnull final RequestField aRF, @Nonnull final Locale aDisplayLocale)
  {
    super (aRF);
    addOptionPleaseSelect (aDisplayLocale);
    for (final EPredefinedIdentifierIssuingAgency eIIA : EPredefinedIdentifierIssuingAgency.values ())
      if (!eIIA.isDeprecated ())
        addOption (eIIA.getSchemeID (), eIIA.getISO6523Code () + " - " + eIIA.getSchemeID ());
  }
}
