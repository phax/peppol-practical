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
package com.helger.peppol.page.pub;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;

import org.busdox.servicemetadata.manageservicemetadataservice._1.ManageServiceMetadataServiceSoap;

import com.helger.commons.ValueEnforcer;

import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.sml.client.ManageServiceMetadataServiceCaller;

/**
 * A special {@link ManageServiceMetadataServiceCaller} subclass that sets the
 * socket factory on a per web service client call basis.
 * 
 * @author Philip Helger
 */
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
