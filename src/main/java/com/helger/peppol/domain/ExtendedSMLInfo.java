package com.helger.peppol.domain;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.sml.SMLInfo;

public final class ExtendedSMLInfo implements IExtendedSMLInfo
{
  private SMLInfo m_aSMLInfo;
  private ESMPAPIType m_eSMPAPIType;

  public ExtendedSMLInfo (@Nonnull final SMLInfo aSMLInfo, @Nonnull final ESMPAPIType eSMPAPIType)
  {
    setSMLInfo (aSMLInfo);
    setSMPAPIType (eSMPAPIType);
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
  public static ExtendedSMLInfo create (@Nonnull final ESML eSML)
  {
    return new ExtendedSMLInfo (new SMLInfo (eSML), ESMPAPIType.PEPPOL);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ExtendedSMLInfo rhs = (ExtendedSMLInfo) o;
    return getID ().equals (rhs.getID ());
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (getID ()).getHashCode ();
  }
}
