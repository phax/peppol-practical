/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsHashSet;
import com.helger.commons.collection.ext.ICommonsSet;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.type.ObjectType;
import com.helger.masterdata.person.ESalutation;
import com.helger.photon.basic.object.AbstractObject;
import com.helger.photon.security.object.StubObject;

public class CRMSubscriber extends AbstractObject implements ICRMSubscriber
{
  public static final ObjectType OT_CRM_SUBSCRIBER = new ObjectType ("crm-subscriber");

  private ESalutation m_eSalutation;
  private String m_sName;
  private String m_sEmailAddress;
  private ICommonsSet <ICRMGroup> m_aAssignedGroups;

  public CRMSubscriber (@Nullable final ESalutation eSalutation,
                        @Nonnull @Nonempty final String sName,
                        @Nonnull @Nonempty final String sEmailAddress,
                        @Nullable final Set <ICRMGroup> aAssignedGroups)
  {
    this (StubObject.createForCurrentUser (), eSalutation, sName, sEmailAddress, aAssignedGroups);
  }

  CRMSubscriber (@Nonnull @Nonempty final StubObject aStubObject,
                 @Nullable final ESalutation eSalutation,
                 @Nonnull @Nonempty final String sName,
                 @Nonnull @Nonempty final String sEmailAddress,
                 @Nullable final Set <ICRMGroup> aAssignedGroups)
  {
    super (aStubObject);
    setSalutation (eSalutation);
    setName (sName);
    setEmailAddress (sEmailAddress);
    setAssignedGroups (aAssignedGroups);
  }

  @Nonnull
  public ObjectType getObjectType ()
  {
    return OT_CRM_SUBSCRIBER;
  }

  @Nullable
  public ESalutation getSalutation ()
  {
    return m_eSalutation;
  }

  @Nullable
  public String getSalutationID ()
  {
    return m_eSalutation == null ? null : m_eSalutation.getID ();
  }

  @Nullable
  public String getSalutationDisplayName (@Nonnull final Locale aContentLocale)
  {
    return m_eSalutation == null ? null : m_eSalutation.getDisplayText (aContentLocale);
  }

  @Nonnull
  public EChange setSalutation (@Nullable final ESalutation eSalutation)
  {
    if (EqualsHelper.equals (eSalutation, m_eSalutation))
      return EChange.UNCHANGED;
    m_eSalutation = eSalutation;
    return EChange.CHANGED;
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  @Nonnull
  public EChange setName (@Nonnull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    if (sName.equals (m_sName))
      return EChange.UNCHANGED;
    m_sName = sName;
    return EChange.CHANGED;
  }

  @Nonnull
  @Nonempty
  public String getEmailAddress ()
  {
    return m_sEmailAddress;
  }

  @Nonnull
  public EChange setEmailAddress (@Nonnull @Nonempty final String sEmailAddress)
  {
    ValueEnforcer.notEmpty (sEmailAddress, "EmailAddress");
    final String sRealEmailAddress = ICRMSubscriber.getUnifiedEmailAddress (sEmailAddress);

    if (sRealEmailAddress.equals (m_sEmailAddress))
      return EChange.UNCHANGED;
    m_sEmailAddress = sRealEmailAddress;
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <ICRMGroup> getAllAssignedGroups ()
  {
    return m_aAssignedGroups.getClone ();
  }

  @Nonnegative
  public int getAssignedGroupCount ()
  {
    return m_aAssignedGroups.size ();
  }

  public boolean isAssignedToGroup (@Nullable final ICRMGroup aCRMGroup)
  {
    return aCRMGroup != null && m_aAssignedGroups.contains (aCRMGroup);
  }

  @Nonnull
  public EChange setAssignedGroups (@Nullable final Set <ICRMGroup> aAssignedGroups)
  {
    // Ensure same implementation type and non-null
    final ICommonsSet <ICRMGroup> aRealAssignedGroups = new CommonsHashSet<> (aAssignedGroups);
    if (aRealAssignedGroups.equals (m_aAssignedGroups))
      return EChange.UNCHANGED;
    m_aAssignedGroups = aRealAssignedGroups;
    return EChange.CHANGED;
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return StringHelper.getConcatenatedOnDemand (getSalutationDisplayName (aContentLocale), " ", m_sName);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .appendIfNotNull ("Salutation", m_eSalutation)
                            .append ("Name", m_sName)
                            .append ("EmailAddress", m_sEmailAddress)
                            .append ("AssignedGroups", m_aAssignedGroups)
                            .toString ();
  }
}
