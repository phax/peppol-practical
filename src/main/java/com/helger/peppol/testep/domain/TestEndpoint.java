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
import com.helger.commons.type.ObjectType;
import com.helger.datetime.PDTFactory;

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
  private final String m_sParticipantIDValue;
  private final ESMPTransportProfile m_eTransportProfile;
  private final String m_sContactPerson;

  /**
   * Constructor.
   *
   * @param sCompanyName
   *        User provided company name.
   * @param sParticipantIDValue
   *        User provided participant ID value.
   * @param eTransportProfile
   *        Transport profile.
   * @param sContactPerson
   *        Optional contact person.
   */
  public TestEndpoint (@Nonnull @Nonempty final String sCompanyName,
                       @Nonnull @Nonempty final String sParticipantIDValue,
                       @Nonnull final ESMPTransportProfile eTransportProfile,
                       @Nullable final String sContactPerson)
  {
    this (GlobalIDFactory.getNewPersistentStringID (),
          PDTFactory.getCurrentDateTime (),
          LoggedInUserManager.getInstance ().getCurrentUserID (),
          sCompanyName,
          sParticipantIDValue,
          eTransportProfile,
          sContactPerson);
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
   * @param sParticipantIDValue
   *        User provided participant ID value.
   * @param eTransportProfile
   *        Transport profile.
   * @param sContactPerson
   *        Optional contact person.
   */
  TestEndpoint (@Nonnull @Nonempty final String sID,
                @Nonnull final DateTime aCreationDT,
                @Nonnull @Nonempty final String sCreationUserID,
                @Nonnull @Nonempty final String sCompanyName,
                @Nonnull @Nonempty final String sParticipantIDValue,
                @Nonnull final ESMPTransportProfile eTransportProfile,
                @Nullable final String sContactPerson)
  {
    m_sID = ValueEnforcer.notEmpty (sID, "ID");
    m_aCreationDT = ValueEnforcer.notNull (aCreationDT, "CreationDT");
    m_sCreationUserID = ValueEnforcer.notEmpty (sCreationUserID, "CreationUserID");
    m_sCompanyName = ValueEnforcer.notEmpty (sCompanyName, "CompanyName");
    m_sParticipantIDValue = ValueEnforcer.notEmpty (sParticipantIDValue, "ParticipantIDValue");
    m_eTransportProfile = ValueEnforcer.notNull (eTransportProfile, "TransportProfile");
    m_sContactPerson = sContactPerson;
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

  @Nonnull
  @Nonempty
  public String getParticipantIDValue ()
  {
    return m_sParticipantIDValue;
  }

  @Nonnull
  public ESMPTransportProfile getTransportProfile ()
  {
    return m_eTransportProfile;
  }

  @Nullable
  public String getContactPerson ()
  {
    return m_sContactPerson;
  }

  public boolean hasSameIdentifier (@Nullable final String sParticipantIDValue,
                                    @Nullable final ESMPTransportProfile eTransportProfile)
  {
    return m_sParticipantIDValue.equals (sParticipantIDValue) && m_eTransportProfile == eTransportProfile;
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
}
