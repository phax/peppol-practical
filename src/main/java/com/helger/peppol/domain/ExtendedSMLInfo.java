package com.helger.peppol.domain;

import javax.annotation.Nonnull;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.sml.SMLInfo;

public final class ExtendedSMLInfo implements IExtendedSMLInfo
{
  private SMLInfo m_aSMLInfo;

  public ExtendedSMLInfo (@Nonnull final SMLInfo aSMLInfo)
  {
    setSMLInfo (aSMLInfo);
  }

  @Nonnull
  public SMLInfo getSMLInfo ()
  {
    return m_aSMLInfo;
  }

  @Nonnull
  public EChange setSMLInfo (@Nonnull final SMLInfo aSMLInfo)
  {
    if (EqualsHelper.equals (aSMLInfo, m_aSMLInfo))
      return EChange.UNCHANGED;
    m_aSMLInfo = aSMLInfo;
    return EChange.CHANGED;
  }

  @Nonnull
  public static ExtendedSMLInfo create (@Nonnull final ESML eSML)
  {
    return new ExtendedSMLInfo (new SMLInfo (eSML));
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
