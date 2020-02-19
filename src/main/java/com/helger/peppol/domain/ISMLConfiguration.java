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
