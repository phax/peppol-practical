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
package com.helger.peppol.crm;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.type.ObjectType;
import com.helger.photon.basic.object.AbstractObject;
import com.helger.photon.security.object.StubObject;

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
  public ObjectType getObjectType ()
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
