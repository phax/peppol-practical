/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.peppol.pub.page;

import javax.annotation.Nonnull;
import javax.net.ssl.SSLSocketFactory;

import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.smlclient.ManageServiceMetadataServiceCaller;

/**
 * A special {@link ManageServiceMetadataServiceCaller} subclass that sets the
 * socket factory on a per web service client call basis.
 *
 * @author Philip Helger
 */
public class SecuredSMPSMLClient extends ManageServiceMetadataServiceCaller
{
  public SecuredSMPSMLClient (@Nonnull final ISMLInfo aSMLInfo, @Nonnull final SSLSocketFactory aSocketFactory)
  {
    super (aSMLInfo);
    setSSLSocketFactory (aSocketFactory);
  }
}
