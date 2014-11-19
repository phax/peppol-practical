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

import com.helger.appbasics.object.AbstractBaseObject;
import com.helger.appbasics.object.StubObject;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.name.IHasDisplayName;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.type.ObjectType;

import eu.europa.ec.cipa.smp.client.ESMPTransportProfile;

/**
 * Represents a single test endpoint.
 *
 * @author Philip Helger
 */
@Immutable
public class TestEndpoint extends AbstractBaseObject implements IHasDisplayName
{
  public static final ObjectType TYPE_TEST_ENDPOINT = new ObjectType ("test-endpoint");

  private String m_sCompanyName;
  private String m_sContactPerson;
  private String m_sParticipantIDScheme;
  private String m_sParticipantIDValue;
  private ESMPTransportProfile m_eTransportProfile;

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
    this (StubObject.createForCurrentUser (),
          sCompanyName,
          sContactPerson,
          sParticipantIDScheme,
          sParticipantIDValue,
          eTransportProfile);
  }

  /**
   * Constructor.
   *
   * @param aObject
   *        Object with default info
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
  TestEndpoint (@Nonnull final StubObject aObject,
                @Nonnull @Nonempty final String sCompanyName,
                @Nullable final String sContactPerson,
                @Nonnull @Nonempty final String sParticipantIDScheme,
                @Nonnull @Nonempty final String sParticipantIDValue,
                @Nonnull final ESMPTransportProfile eTransportProfile)
  {
    super (aObject);
    setCompanyName (sCompanyName);
    setContactPerson (sContactPerson);
    setParticipantIDScheme (sParticipantIDScheme);
    setParticipantIDValue (sParticipantIDValue);
    setTransportProfile (eTransportProfile);
  }

  @Nonnull
  public ObjectType getTypeID ()
  {
    return TYPE_TEST_ENDPOINT;
  }

  @Nonnull
  @Nonempty
  public String getCompanyName ()
  {
    return m_sCompanyName;
  }

  @Nonnull
  public EChange setCompanyName (@Nonnull @Nonempty final String sCompanyName)
  {
    ValueEnforcer.notEmpty (sCompanyName, "CompanyName");
    if (sCompanyName.equals (m_sCompanyName))
      return EChange.UNCHANGED;

    m_sCompanyName = sCompanyName;
    return EChange.CHANGED;
  }

  @Nullable
  public String getContactPerson ()
  {
    return m_sContactPerson;
  }

  @Nonnull
  public EChange setContactPerson (@Nullable final String sContactPerson)
  {
    if (EqualsUtils.equals (sContactPerson, m_sContactPerson))
      return EChange.UNCHANGED;

    m_sContactPerson = sContactPerson;
    return EChange.CHANGED;
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

  @Nonnull
  public EChange setParticipantIDScheme (@Nonnull @Nonempty final String sParticipantIDScheme)
  {
    ValueEnforcer.notEmpty (sParticipantIDScheme, "ParticipantIDScheme");
    if (sParticipantIDScheme.equals (m_sParticipantIDScheme))
      return EChange.UNCHANGED;

    m_sParticipantIDScheme = sParticipantIDScheme;
    return EChange.CHANGED;
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

  @Nonnull
  public EChange setParticipantIDValue (@Nonnull @Nonempty final String sParticipantIDValue)
  {
    ValueEnforcer.notEmpty (sParticipantIDValue, "ParticipantIDValue");
    if (sParticipantIDValue.equals (m_sParticipantIDValue))
      return EChange.UNCHANGED;

    m_sParticipantIDValue = sParticipantIDValue;
    return EChange.CHANGED;
  }

  @Nonnull
  public ESMPTransportProfile getTransportProfile ()
  {
    return m_eTransportProfile;
  }

  @Nonnull
  public EChange setTransportProfile (@Nonnull final ESMPTransportProfile eTransportProfile)
  {
    ValueEnforcer.notNull (eTransportProfile, "ParticipantIDValue");
    if (eTransportProfile.equals (m_eTransportProfile))
      return EChange.UNCHANGED;

    m_eTransportProfile = eTransportProfile;
    return EChange.CHANGED;
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

  @Nonnull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sParticipantIDScheme + ":" + m_sParticipantIDValue;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("CompanyName", m_sCompanyName)
                            .appendIfNotNull ("ContactPerson", m_sContactPerson)
                            .append ("ParticipantIDScheme", m_sParticipantIDScheme)
                            .append ("ParticipantIDValue", m_sParticipantIDValue)
                            .append ("TransportProfile", m_eTransportProfile)
                            .toString ();
  }
}
