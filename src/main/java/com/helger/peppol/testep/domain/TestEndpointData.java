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
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

import eu.europa.ec.cipa.smp.client.ESMPTransportProfile;

@Immutable
public final class TestEndpointData
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

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("DocumentTypeID", m_sDocumentTypeID)
                                       .append ("ProcessID", m_sProcessID)
                                       .append ("TransportProfile", m_eTransportProfile)
                                       .toString ();
  }
}
