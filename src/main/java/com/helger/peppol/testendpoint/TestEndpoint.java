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
package com.helger.peppol.testendpoint;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.name.IHasDisplayName;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.base.type.ObjectType;
import com.helger.peppol.smp.ISMPTransportProfile;
import com.helger.peppol.ui.types.smlconfig.ISMLConfiguration;
import com.helger.photon.security.object.StubObject;
import com.helger.tenancy.AbstractBusinessObject;


/**
 * Represents a single test endpoint.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class TestEndpoint extends AbstractBusinessObject implements IHasDisplayName
{
  public static final ObjectType OT = new ObjectType ("test-endpoint");

  private String m_sCompanyName;
  private String m_sContactPerson;
  private String m_sParticipantIDIssuer;
  private String m_sParticipantIDValue;
  private ISMPTransportProfile m_aTransportProfile;
  private ISMLConfiguration m_aSMLInfo;

  /**
   * Constructor.
   *
   * @param sCompanyName
   *        User provided company name.
   * @param sContactPerson
   *        Optional contact person.
   * @param sParticipantIDIssuer
   *        Test endpoint participant ID issuing agency (e.g. 9915).
   * @param sParticipantIDValue
   *        Test endpoint participant ID value (e.g. 123456).
   * @param aTransportProfile
   *        Transport profile. May not be <code>null</code>.
   * @param eSML
   *        SML to use. May not be <code>null</code>.
   */
  public TestEndpoint (@NonNull @Nonempty final String sCompanyName,
                       @Nullable final String sContactPerson,
                       @NonNull @Nonempty final String sParticipantIDIssuer,
                       @NonNull @Nonempty final String sParticipantIDValue,
                       @NonNull final ISMPTransportProfile aTransportProfile,
                       @NonNull final ISMLConfiguration eSML)
  {
    this (StubObject.createForCurrentUser (),
          sCompanyName,
          sContactPerson,
          sParticipantIDIssuer,
          sParticipantIDValue,
          aTransportProfile,
          eSML);
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
   * @param sParticipantIDIssuer
   *        Test endpoint participant ID issuing agency (e.g. 9915).
   * @param sParticipantIDValue
   *        Test endpoint participant ID value (e.g. 123456).
   * @param aTransportProfile
   *        Transport profile.May not be <code>null</code>.
   * @param eSML
   *        SML to use. May not be <code>null</code>.
   */
  TestEndpoint (@NonNull final StubObject aObject,
                @NonNull @Nonempty final String sCompanyName,
                @Nullable final String sContactPerson,
                @NonNull @Nonempty final String sParticipantIDIssuer,
                @NonNull @Nonempty final String sParticipantIDValue,
                @NonNull final ISMPTransportProfile aTransportProfile,
                @NonNull final ISMLConfiguration eSML)
  {
    super (aObject);
    setCompanyName (sCompanyName);
    setContactPerson (sContactPerson);
    setParticipantIDIssuer (sParticipantIDIssuer);
    setParticipantIDValue (sParticipantIDValue);
    setTransportProfile (aTransportProfile);
    setSML (eSML);
  }

  @NonNull
  public ObjectType getObjectType ()
  {
    return OT;
  }

  @NonNull
  @Nonempty
  public String getCompanyName ()
  {
    return m_sCompanyName;
  }

  @NonNull
  public EChange setCompanyName (@NonNull @Nonempty final String sCompanyName)
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

  @NonNull
  public EChange setContactPerson (@Nullable final String sContactPerson)
  {
    if (EqualsHelper.equals (sContactPerson, m_sContactPerson))
      return EChange.UNCHANGED;

    m_sContactPerson = sContactPerson;
    return EChange.CHANGED;
  }

  /**
   * @return The test endpoint participant ID type (e.g. 9915)
   */
  @NonNull
  @Nonempty
  public String getParticipantIDIssuer ()
  {
    return m_sParticipantIDIssuer;
  }

  @NonNull
  public EChange setParticipantIDIssuer (@NonNull @Nonempty final String sParticipantIDIssuer)
  {
    ValueEnforcer.notEmpty (sParticipantIDIssuer, "ParticipantIDIssuer");
    if (sParticipantIDIssuer.equals (m_sParticipantIDIssuer))
      return EChange.UNCHANGED;

    m_sParticipantIDIssuer = sParticipantIDIssuer;
    return EChange.CHANGED;
  }

  /**
   * @return The test endpoint participant ID value (e.g. test)
   */
  @NonNull
  @Nonempty
  public String getParticipantIDValue ()
  {
    return m_sParticipantIDValue;
  }

  @NonNull
  public EChange setParticipantIDValue (@NonNull @Nonempty final String sParticipantIDValue)
  {
    ValueEnforcer.notEmpty (sParticipantIDValue, "ParticipantIDValue");
    if (sParticipantIDValue.equals (m_sParticipantIDValue))
      return EChange.UNCHANGED;

    m_sParticipantIDValue = sParticipantIDValue;
    return EChange.CHANGED;
  }

  /**
   * @return The full test endpoint participant ID value (e.g. 9915:test)
   */
  @NonNull
  @Nonempty
  public String getParticipantID ()
  {
    return m_sParticipantIDIssuer + ":" + m_sParticipantIDValue;
  }

  @NonNull
  public ISMPTransportProfile getTransportProfile ()
  {
    return m_aTransportProfile;
  }

  @NonNull
  public EChange setTransportProfile (@NonNull final ISMPTransportProfile aTransportProfile)
  {
    ValueEnforcer.notNull (aTransportProfile, "TransportProfile");
    if (aTransportProfile.equals (m_aTransportProfile))
      return EChange.UNCHANGED;

    m_aTransportProfile = aTransportProfile;
    return EChange.CHANGED;
  }

  @NonNull
  public ISMLConfiguration getSML ()
  {
    return m_aSMLInfo;
  }

  @NonNull
  public EChange setSML (@NonNull final ISMLConfiguration aSML)
  {
    ValueEnforcer.notNull (aSML, "SML");
    if (aSML.equals (m_aSMLInfo))
      return EChange.UNCHANGED;

    m_aSMLInfo = aSML;
    return EChange.CHANGED;
  }

  public boolean hasSameIdentifier (@Nullable final String sParticipantIDIssuer,
                                    @Nullable final String sParticipantIDValue,
                                    @Nullable final ISMPTransportProfile aTransportProfile)
  {
    if (sParticipantIDIssuer == null || sParticipantIDValue == null || aTransportProfile == null)
      return false;
    return m_sParticipantIDIssuer.equals (sParticipantIDIssuer) &&
           m_sParticipantIDValue.equals (sParticipantIDValue) &&
           m_aTransportProfile.equals (aTransportProfile);
  }

  @NonNull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sParticipantIDIssuer + ":" + m_sParticipantIDValue;
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .append ("CompanyName", m_sCompanyName)
                            .appendIfNotNull ("ContactPerson", m_sContactPerson)
                            .append ("ParticipantIDIssuer", m_sParticipantIDIssuer)
                            .append ("ParticipantIDValue", m_sParticipantIDValue)
                            .append ("TransportProfile", m_aTransportProfile)
                            .append ("SML", m_aSMLInfo)
                            .getToString ();
  }
}
