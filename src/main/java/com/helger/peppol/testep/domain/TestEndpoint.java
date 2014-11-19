/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.peppol.testep.domain;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTime;

import com.helger.appbasics.datetime.IHasCreationInfo;
import com.helger.appbasics.security.login.LoggedInUserManager;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.id.IHasID;
import com.helger.commons.idfactory.GlobalIDFactory;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.type.ObjectType;
import com.helger.datetime.PDTFactory;

/**
 * Represents a single test endpoint.
 *
 * @author Philip Helger
 */
@Immutable
public class TestEndpoint implements IHasID <String>, IHasCreationInfo
{
  public static final ObjectType TYPE_TEST_ENDPOINT = new ObjectType ("test-endpoint");

  private final String m_sID;
  private final DateTime m_aCreationDT;
  private final String m_sCreationUserID;
  private final String m_sCompanyName;
  private final String m_sContactPerson;
  private final String m_sParticipantID;
  private final List <TestEndpointData> m_aDatas;

  /**
   * Constructor.
   *
   * @param sCompanyName
   *        User provided company name.
   * @param sContactPerson
   *        Optional contact person.
   * @param sParticipantID
   *        User provided participant ID value.
   * @param aDatas
   *        Endpoint data. Usually created
   */
  public TestEndpoint (@Nonnull @Nonempty final String sCompanyName,
                       @Nullable final String sContactPerson,
                       @Nonnull @Nonempty final String sParticipantID,
                       @Nonnull final List <TestEndpointData> aDatas)
  {
    this (GlobalIDFactory.getNewPersistentStringID (),
          PDTFactory.getCurrentDateTime (),
          LoggedInUserManager.getInstance ().getCurrentUserID (),
          sCompanyName,
          sContactPerson,
          sParticipantID,
          aDatas);
  }

  /**
   * Constructor.
   *
   * @param sID
   *        Object ID
   * @param aCreationDT
   *        The creation date time.
   * @param sCreationUserID
   *        The user ID who created this test endpoint.
   * @param sCompanyName
   *        User provided company name.
   * @param sContactPerson
   *        Optional contact person.
   * @param sParticipantID
   *        User provided participant ID value (no iso6523-actorid-upis).
   * @param aDatas
   *        Endpoint data.
   */
  TestEndpoint (@Nonnull @Nonempty final String sID,
                @Nonnull final DateTime aCreationDT,
                @Nonnull @Nonempty final String sCreationUserID,
                @Nonnull @Nonempty final String sCompanyName,
                @Nullable final String sContactPerson,
                @Nonnull @Nonempty final String sParticipantID,
                @Nonnull final List <TestEndpointData> aDatas)
  {
    m_sID = ValueEnforcer.notEmpty (sID, "ID");
    m_aCreationDT = ValueEnforcer.notNull (aCreationDT, "CreationDT");
    m_sCreationUserID = ValueEnforcer.notEmpty (sCreationUserID, "CreationUserID");
    m_sCompanyName = ValueEnforcer.notEmpty (sCompanyName, "CompanyName");
    m_sContactPerson = sContactPerson;
    m_sParticipantID = ValueEnforcer.notEmpty (sParticipantID, "ParticipantID");
    m_aDatas = ValueEnforcer.notNull (aDatas, "Data");
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  public DateTime getCreationDateTime ()
  {
    return m_aCreationDT;
  }

  @Nonnull
  @Nonempty
  public String getCreationUserID ()
  {
    return m_sCreationUserID;
  }

  @Nonnull
  @Nonempty
  public String getCompanyName ()
  {
    return m_sCompanyName;
  }

  @Nullable
  public String getContactPerson ()
  {
    return m_sContactPerson;
  }

  @Nonnull
  @Nonempty
  public String getParticipantIDValue ()
  {
    return m_sParticipantID;
  }

  @Nonnull
  public List <TestEndpointData> getAllDatas ()
  {
    return ContainerHelper.newList (m_aDatas);
  }

  public boolean hasSameIdentifier (@Nullable final String sParticipantIDValue, @Nullable final TestEndpointData aData)
  {
    if (sParticipantIDValue == null || aData == null)
      return false;
    return m_sParticipantID.equals (sParticipantIDValue) && m_aDatas.contains (aData);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final TestEndpoint rhs = (TestEndpoint) o;
    return m_sID.equals (rhs.m_sID);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sID).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("ID", m_sID)
                                       .append ("CreationDT", m_aCreationDT)
                                       .append ("CreationUserID", m_sCreationUserID)
                                       .append ("CompanyName", m_sCompanyName)
                                       .append ("ContactPerson", m_sContactPerson)
                                       .append ("ParticipantID", m_sParticipantID)
                                       .append ("Datas", m_aDatas)
                                       .toString ();
  }
}
