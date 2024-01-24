/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.peppol.domain;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.sml.SMLInfo;
import com.helger.peppolid.factory.ESMPIdentifierType;

public final class SMLConfiguration implements ISMLConfiguration
{
  private SMLInfo m_aSMLInfo;
  private ESMPAPIType m_eSMPAPIType;
  private ESMPIdentifierType m_eSMPIdentifierType;
  private boolean m_bProduction;

  public SMLConfiguration (@Nonnull final SMLInfo aSMLInfo,
                           @Nonnull final ESMPAPIType eSMPAPIType,
                           @Nonnull final ESMPIdentifierType eSMPIdentifierType,
                           final boolean bProduction)
  {
    setSMLInfo (aSMLInfo);
    setSMPAPIType (eSMPAPIType);
    setSMPIdentifierType (eSMPIdentifierType);
    setProduction (bProduction);
  }

  @Nonnull
  public SMLInfo getSMLInfo ()
  {
    return m_aSMLInfo;
  }

  @Nonnull
  public EChange setSMLInfo (@Nonnull final SMLInfo aSMLInfo)
  {
    ValueEnforcer.notNull (aSMLInfo, "SMLInfo");
    if (aSMLInfo.equals (m_aSMLInfo))
      return EChange.UNCHANGED;
    m_aSMLInfo = aSMLInfo;
    return EChange.CHANGED;
  }

  @Nonnull
  public ESMPAPIType getSMPAPIType ()
  {
    return m_eSMPAPIType;
  }

  @Nonnull
  public EChange setSMPAPIType (@Nonnull final ESMPAPIType eSMPAPIType)
  {
    ValueEnforcer.notNull (eSMPAPIType, "SMPAPIType");
    if (eSMPAPIType.equals (m_eSMPAPIType))
      return EChange.UNCHANGED;
    m_eSMPAPIType = eSMPAPIType;
    return EChange.CHANGED;
  }

  @Nonnull
  public ESMPIdentifierType getSMPIdentifierType ()
  {
    return m_eSMPIdentifierType;
  }

  @Nonnull
  public EChange setSMPIdentifierType (@Nonnull final ESMPIdentifierType eSMPIdentifierType)
  {
    ValueEnforcer.notNull (eSMPIdentifierType, "SMPIdentifierType");
    if (eSMPIdentifierType.equals (m_eSMPIdentifierType))
      return EChange.UNCHANGED;
    m_eSMPIdentifierType = eSMPIdentifierType;
    return EChange.CHANGED;
  }

  public boolean isProduction ()
  {
    return m_bProduction;
  }

  @Nonnull
  public EChange setProduction (final boolean bProduction)
  {
    if (bProduction == m_bProduction)
      return EChange.UNCHANGED;
    m_bProduction = bProduction;
    return EChange.CHANGED;
  }

  @Nonnull
  public static SMLConfiguration create (@Nonnull final ESML eSML)
  {
    return new SMLConfiguration (new SMLInfo (eSML), ESMPAPIType.PEPPOL, ESMPIdentifierType.PEPPOL, eSML == ESML.DIGIT_PRODUCTION);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final SMLConfiguration rhs = (SMLConfiguration) o;
    return getID ().equals (rhs.getID ());
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (getID ()).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("SMLInfo", m_aSMLInfo)
                                       .append ("APIType", m_eSMPAPIType)
                                       .append ("IdentifierType", m_eSMPIdentifierType)
                                       .append ("Production", m_bProduction)
                                       .getToString ();
  }
}
