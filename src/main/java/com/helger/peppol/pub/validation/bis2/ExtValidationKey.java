package com.helger.peppol.pub.validation.bis2;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.peppol.validation.domain.ValidationKey;

public final class ExtValidationKey
{
  private final ValidationKey m_aValidationKey;

  public ExtValidationKey (@Nonnull final ValidationKey aValidationKey)
  {
    m_aValidationKey = ValueEnforcer.notNull (aValidationKey, "ValidationKey");
  }

  @Nonnull
  public ValidationKey getValidationKey ()
  {
    return m_aValidationKey;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_aValidationKey.getBusinessSpecification ().getID () +
           m_aValidationKey.getTransaction ().getTransactionKey ();
  }

  @Nonnull
  @Nonempty
  public String getDisplayName ()
  {
    return m_aValidationKey.getBusinessSpecification ().getDisplayName () +
           "; transaction T" +
           m_aValidationKey.getTransaction ().getNumber () +
           " " +
           m_aValidationKey.getTransaction ().getName ();
  }
}
