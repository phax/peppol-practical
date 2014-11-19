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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTime;

import com.helger.appbasics.datetime.IHasCreationInfo;
import com.helger.appbasics.security.login.LoggedInUserManager;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.id.IHasID;
import com.helger.commons.idfactory.GlobalIDFactory;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.type.ObjectType;
import com.helger.datetime.PDTFactory;

import eu.europa.ec.cipa.peppol.identifier.participant.IPeppolReadonlyParticipantIdentifier;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.smp.client.ESMPTransportProfile;

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
  private final String m_sParticipantIDScheme;
  private final String m_sParticipantIDValue;
  private final ESMPTransportProfile m_eTransportProfile;
  // status
  private final SimpleParticipantIdentifier m_aParticipantID;

  /**
   * Constructor.
   *
   * @param sCompanyName
   *        User provided company name.
   * @param sContactPerson
   *        Optional contact person.
   * @param sParticipantIDScheme
   *        Test endpoint participant ID scheme (e.g. 9915).
   * @param sParticipantIDValue
   *        Test endpoint participant ID value (e.g. 123456).
   * @param eTransportProfile
   *        Transport profile. May not be <code>null</code>.
   */
  public TestEndpoint (@Nonnull @Nonempty final String sCompanyName,
                       @Nullable final String sContactPerson,
                       @Nonnull @Nonempty final String sParticipantIDScheme,
                       @Nonnull @Nonempty final String sParticipantIDValue,
                       @Nonnull final ESMPTransportProfile eTransportProfile)
  {
    this (GlobalIDFactory.getNewPersistentStringID (),
          PDTFactory.getCurrentDateTime (),
          LoggedInUserManager.getInstance ().getCurrentUserID (),
          sCompanyName,
          sContactPerson,
          sParticipantIDScheme,
          sParticipantIDValue,
          eTransportProfile);
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
   * @param sParticipantIDScheme
   *        Test endpoint participant ID scheme (e.g. 9915).
   * @param sParticipantIDValue
   *        Test endpoint participant ID value (e.g. 123456).
   * @param eTransportProfile
   *        Transport profile.
   */
  TestEndpoint (@Nonnull @Nonempty final String sID,
                @Nonnull final DateTime aCreationDT,
                @Nonnull @Nonempty final String sCreationUserID,
                @Nonnull @Nonempty final String sCompanyName,
                @Nullable final String sContactPerson,
                @Nonnull @Nonempty final String sParticipantIDScheme,
                @Nonnull @Nonempty final String sParticipantIDValue,
                @Nonnull final ESMPTransportProfile eTransportProfile)
  {
    m_sID = ValueEnforcer.notEmpty (sID, "ID");
    m_aCreationDT = ValueEnforcer.notNull (aCreationDT, "CreationDT");
    m_sCreationUserID = ValueEnforcer.notEmpty (sCreationUserID, "CreationUserID");
    m_sCompanyName = ValueEnforcer.notEmpty (sCompanyName, "CompanyName");
    m_sContactPerson = sContactPerson;
    m_sParticipantIDScheme = ValueEnforcer.notEmpty (sParticipantIDScheme, "ParticipantIDScheme");
    m_sParticipantIDValue = ValueEnforcer.notEmpty (sParticipantIDValue, "ParticipantIDValue");
    m_eTransportProfile = ValueEnforcer.notNull (eTransportProfile, "TransportProfile");
    m_aParticipantID = SimpleParticipantIdentifier.createWithDefaultScheme (sParticipantIDScheme +
                                                                            ":" +
                                                                            sParticipantIDValue);
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

  /**
   * @return The test endpoint participant ID scheme (e.g. 9915)
   */
  @Nonnull
  @Nonempty
  public String getParticipantIDScheme ()
  {
    return m_sParticipantIDScheme;
  }

  /**
   * @return The test endpoint participant ID value (e.g. 9915)
   */
  @Nonnull
  @Nonempty
  public String getParticipantIDValue ()
  {
    return m_sParticipantIDValue;
  }

  /**
   * @return The complete participant identifier. Never <code>null</code>.
   */
  @Nonnull
  public IPeppolReadonlyParticipantIdentifier getParticipantIdentifier ()
  {
    return m_aParticipantID;
  }

  @Nonnull
  public ESMPTransportProfile getTransportProfile ()
  {
    return m_eTransportProfile;
  }

  public boolean hasSameIdentifier (@Nullable final String sParticipantIDScheme,
                                    @Nullable final String sParticipantIDValue,
                                    @Nullable final ESMPTransportProfile eTransportProfile)
  {
    if (sParticipantIDScheme == null || sParticipantIDValue == null || eTransportProfile == null)
      return false;
    return m_sParticipantIDScheme.equals (sParticipantIDScheme) &&
           m_sParticipantIDValue.equals (sParticipantIDValue) &&
           m_eTransportProfile.equals (eTransportProfile);
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
                                       .append ("ParticipantIDScheme", m_sParticipantIDScheme)
                                       .append ("ParticipantIDValue", m_sParticipantIDValue)
                                       .append ("TransportProfile", m_eTransportProfile)
                                       .toString ();
  }
}
