package com.helger.peppol.crm;

import javax.annotation.Nonnull;

import com.helger.appbasics.object.AbstractObject;
import com.helger.appbasics.object.StubObject;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.type.ObjectType;

public class CRMGroup extends AbstractObject implements ICRMGroup
{
  public static final ObjectType OT_CRM_GROUP = new ObjectType ("crm-group");

  private String m_sDisplayName;
  private String m_sSenderEmailAddress;

  public CRMGroup (@Nonnull @Nonempty final String sDisplayName, @Nonnull @Nonempty final String sSenderEmailAddress)
  {
    this (StubObject.createForCurrentUser (), sDisplayName, sSenderEmailAddress);
  }

  CRMGroup (@Nonnull @Nonempty final StubObject aStubObject,
            @Nonnull @Nonempty final String sDisplayName,
            @Nonnull @Nonempty final String sSenderEmailAddress)
  {
    super (aStubObject);
    setDisplayName (sDisplayName);
    setSenderEmailAddress (sSenderEmailAddress);
  }

  @Nonnull
  public ObjectType getTypeID ()
  {
    return OT_CRM_GROUP;
  }

  @Nonnull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sDisplayName;
  }

  @Nonnull
  public EChange setDisplayName (@Nonnull @Nonempty final String sDisplayName)
  {
    ValueEnforcer.notEmpty (sDisplayName, "DisplayName");
    if (sDisplayName.equals (m_sDisplayName))
      return EChange.UNCHANGED;
    m_sDisplayName = sDisplayName;
    return EChange.CHANGED;
  }

  @Nonnull
  @Nonempty
  public String getSenderEmailAddress ()
  {
    return m_sSenderEmailAddress;
  }

  @Nonnull
  public EChange setSenderEmailAddress (@Nonnull @Nonempty final String sSenderEmailAddress)
  {
    ValueEnforcer.notEmpty (sSenderEmailAddress, "SenderEmailAddress");
    if (sSenderEmailAddress.equals (m_sSenderEmailAddress))
      return EChange.UNCHANGED;
    m_sSenderEmailAddress = sSenderEmailAddress;
    return EChange.CHANGED;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("DisplayName", m_sDisplayName)
                            .append ("SenderEmailAddress", m_sSenderEmailAddress)
                            .toString ();
  }
}
