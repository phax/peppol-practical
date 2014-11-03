package com.helger.peppol.page.pub;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;

import org.busdox.servicemetadata.manageservicemetadataservice._1.ManageServiceMetadataServiceSoap;

import com.helger.commons.ValueEnforcer;

import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.sml.client.ManageServiceMetadataServiceCaller;

public class SecuredSMPSMLClient extends ManageServiceMetadataServiceCaller
{
  private final SSLSocketFactory m_aSocketFactory;

  public SecuredSMPSMLClient (@Nonnull final ISMLInfo aSMLInfo, @Nonnull final SSLSocketFactory aSocketFactory)
  {
    super (aSMLInfo);
    m_aSocketFactory = ValueEnforcer.notNull (aSocketFactory, "SocketFactory");
  }

  @Override
  protected ManageServiceMetadataServiceSoap createWSPort ()
  {
    final ManageServiceMetadataServiceSoap aPort = super.createWSPort ();
    // Use the specific socket factory
    ((BindingProvider) aPort).getRequestContext ().put (com.sun.xml.ws.developer.JAXWSProperties.SSL_SOCKET_FACTORY,
                                                        m_aSocketFactory);
    return aPort;
  }
}