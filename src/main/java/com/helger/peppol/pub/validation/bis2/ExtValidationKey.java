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

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.peppol.validation.api.ValidationKey;

public final class ExtValidationKey implements Serializable, Comparable <ExtValidationKey>
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
    String ret = m_aValidationKey.getBusinessSpecification ().getID () +
                 "-" +
                 m_aValidationKey.getTransaction ().getTransactionKey ();
    if (m_aValidationKey.isCountrySpecific ())
      ret += "-" + m_aValidationKey.getCountryCode ();
    if (m_aValidationKey.isSectorSpecific ())
      ret += "-" + m_aValidationKey.getSectorKey ().getID ();
    return ret;
  }

  @Nonnull
  @Nonempty
  public String getDisplayName (@Nonnull final Locale aDisplayLocale)
  {
    String ret = m_aValidationKey.getBusinessSpecification ().getDisplayName () +
                 "; transaction T" +
                 m_aValidationKey.getTransaction ().getNumber () +
                 " " +
                 m_aValidationKey.getTransaction ().getName ();
    if (m_aValidationKey.isCountrySpecific ())
      ret += "; Country " + m_aValidationKey.getCountryLocale ().getDisplayCountry (aDisplayLocale);
    if (m_aValidationKey.isSectorSpecific ())
      ret += "; Sector: " + m_aValidationKey.getSectorKey ().getDisplayName ();
    return ret;
  }

  public int compareTo (@Nonnull final ExtValidationKey aOther)
  {
    return m_aValidationKey.compareTo (aOther.m_aValidationKey);
  }
}
