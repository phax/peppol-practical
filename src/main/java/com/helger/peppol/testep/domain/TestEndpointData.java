package com.helger.peppol.testep.domain;

import javax.annotation.Nonnull;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.hash.HashCodeGenerator;

import eu.europa.ec.cipa.smp.client.ESMPTransportProfile;

public class TestEndpointData
{
  private final String m_sDocumentTypeID;
  private final String m_sProcessID;
  private final ESMPTransportProfile m_eTransportProfile;

  public TestEndpointData (@Nonnull @Nonempty final String sDocumentTypeID,
                           @Nonnull @Nonempty final String sProcessID,
                           @Nonnull final ESMPTransportProfile eTransportProfile)
  {
    m_sDocumentTypeID = ValueEnforcer.notEmpty (sDocumentTypeID, "DocumentTypeID");
    m_sProcessID = ValueEnforcer.notEmpty (sProcessID, "ProcessID");
    m_eTransportProfile = ValueEnforcer.notNull (eTransportProfile, "TransportProfile");
  }

  @Nonnull
  @Nonempty
  public String getDocumentTypeID ()
  {
    return m_sDocumentTypeID;
  }

  @Nonnull
  @Nonempty
  public String getProcessID ()
  {
    return m_sProcessID;
  }

  @Nonnull
  public ESMPTransportProfile getTransportProfile ()
  {
    return m_eTransportProfile;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final TestEndpointData rhs = (TestEndpointData) o;
    return m_sDocumentTypeID.equals (rhs.m_sDocumentTypeID) &&
           m_sProcessID.equals (rhs.m_sProcessID) &&
           m_eTransportProfile.equals (rhs.m_eTransportProfile);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sDocumentTypeID)
                                       .append (m_sProcessID)
                                       .append (m_eTransportProfile)
                                       .getHashCode ();
  }
}
