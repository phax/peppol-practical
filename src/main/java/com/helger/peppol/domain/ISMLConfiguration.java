/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.peppol.domain;

import java.io.Serializable;
import java.net.URL;

import javax.annotation.Nonnull;

import com.helger.commons.id.IHasID;
import com.helger.commons.name.IHasDisplayName;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppolid.factory.ESMPIdentifierType;

public interface ISMLConfiguration extends IHasID <String>, IHasDisplayName, Serializable
{
  @Nonnull
  ISMLInfo getSMLInfo ();

  default String getID ()
  {
    return getSMLInfo ().getID ();
  }

  default String getDisplayName ()
  {
    return getSMLInfo ().getDisplayName ();
  }

  default String getDNSZone ()
  {
    return getSMLInfo ().getDNSZone ();
  }

  default String getPublisherDNSZone ()
  {
    return getSMLInfo ().getPublisherDNSZone ();
  }

  default String getManagementServiceURL ()
  {
    return getSMLInfo ().getManagementServiceURL ();
  }

  default URL getManageServiceMetaDataEndpointAddress ()
  {
    return getSMLInfo ().getManageServiceMetaDataEndpointAddress ();
  }

  default URL getManageParticipantIdentifierEndpointAddress ()
  {
    return getSMLInfo ().getManageParticipantIdentifierEndpointAddress ();
  }

  default boolean isClientCertificateRequired ()
  {
    return getSMLInfo ().isClientCertificateRequired ();
  }

  @Nonnull
  ESMPAPIType getSMPAPIType ();

  @Nonnull
  ESMPIdentifierType getSMPIdentifierType ();

  boolean isProduction ();
}
