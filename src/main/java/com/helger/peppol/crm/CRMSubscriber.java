/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.state.EChange;
import com.helger.base.string.StringHelper;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.base.type.ObjectType;
import com.helger.collection.commons.CommonsHashSet;
import com.helger.collection.commons.ICommonsSet;
import com.helger.masterdata.person.ESalutation;
import com.helger.photon.security.object.StubObject;
import com.helger.tenancy.AbstractBusinessObject;


public class CRMSubscriber extends AbstractBusinessObject implements ICRMSubscriber
{
  public static final ObjectType OT_CRM_SUBSCRIBER = new ObjectType ("crm-subscriber");

  private ESalutation m_eSalutation;
  private String m_sName;
  private String m_sEmailAddress;
  private ICommonsSet <ICRMGroup> m_aAssignedGroups;

  public CRMSubscriber (@Nullable final ESalutation eSalutation,
                        @NonNull @Nonempty final String sName,
                        @NonNull @Nonempty final String sEmailAddress,
                        @Nullable final Set <ICRMGroup> aAssignedGroups)
  {
    this (StubObject.createForCurrentUser (), eSalutation, sName, sEmailAddress, aAssignedGroups);
  }

  CRMSubscriber (@NonNull @Nonempty final StubObject aStubObject,
                 @Nullable final ESalutation eSalutation,
                 @NonNull @Nonempty final String sName,
                 @NonNull @Nonempty final String sEmailAddress,
                 @Nullable final Set <ICRMGroup> aAssignedGroups)
  {
    super (aStubObject);
    setSalutation (eSalutation);
    setName (sName);
    setEmailAddress (sEmailAddress);
    setAssignedGroups (aAssignedGroups);
  }

  @NonNull
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
  public String getSalutationDisplayName (@NonNull final Locale aContentLocale)
  {
    return m_eSalutation == null ? null : m_eSalutation.getDisplayText (aContentLocale);
  }

  @NonNull
  public EChange setSalutation (@Nullable final ESalutation eSalutation)
  {
    if (EqualsHelper.equals (eSalutation, m_eSalutation))
      return EChange.UNCHANGED;
    m_eSalutation = eSalutation;
    return EChange.CHANGED;
  }

  @NonNull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  @NonNull
  public EChange setName (@NonNull @Nonempty final String sName)
  {
    ValueEnforcer.notEmpty (sName, "Name");
    if (sName.equals (m_sName))
      return EChange.UNCHANGED;
    m_sName = sName;
    return EChange.CHANGED;
  }

  @NonNull
  @Nonempty
  public String getEmailAddress ()
  {
    return m_sEmailAddress;
  }

  @NonNull
  public EChange setEmailAddress (@NonNull @Nonempty final String sEmailAddress)
  {
    ValueEnforcer.notEmpty (sEmailAddress, "EmailAddress");
    final String sRealEmailAddress = ICRMSubscriber.getUnifiedEmailAddress (sEmailAddress);

    if (sRealEmailAddress.equals (m_sEmailAddress))
      return EChange.UNCHANGED;
    m_sEmailAddress = sRealEmailAddress;
    return EChange.CHANGED;
  }

  @NonNull
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

  @NonNull
  public EChange setAssignedGroups (@Nullable final Set <ICRMGroup> aAssignedGroups)
  {
    // Ensure same implementation type and non-null
    final ICommonsSet <ICRMGroup> aRealAssignedGroups = new CommonsHashSet <> (aAssignedGroups);
    if (aRealAssignedGroups.equals (m_aAssignedGroups))
      return EChange.UNCHANGED;
    m_aAssignedGroups = aRealAssignedGroups;
    return EChange.CHANGED;
  }

  @Nullable
  public String getDisplayText (@NonNull final Locale aContentLocale)
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
                            .getToString ();
  }
}
