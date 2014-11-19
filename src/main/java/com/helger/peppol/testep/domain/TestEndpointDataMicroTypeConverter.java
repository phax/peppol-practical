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

import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.IMicroTypeConverter;
import com.helger.commons.microdom.impl.MicroElement;

import eu.europa.ec.cipa.smp.client.ESMPTransportProfile;

@Immutable
public final class TestEndpointDataMicroTypeConverter implements IMicroTypeConverter
{
  private static final String ATTR_DOCUMENT_TYPE_ID = "doctypid";
  private static final String ATTR_PROCESS_ID = "processid";
  private static final String ATTR_TRANSPORT_PROFILE = "transportprofile";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final TestEndpointData aValue = (TestEndpointData) aObject;

    final IMicroElement eValue = new MicroElement (sNamespaceURI, sTagName);
    eValue.setAttribute (ATTR_DOCUMENT_TYPE_ID, aValue.getDocumentTypeID ());
    eValue.setAttribute (ATTR_PROCESS_ID, aValue.getProcessID ());
    eValue.setAttribute (ATTR_TRANSPORT_PROFILE, aValue.getTransportProfile ().getID ());
    return eValue;
  }

  @Nonnull
  public TestEndpointData convertToNative (@Nonnull final IMicroElement eValue)
  {
    final String sDocumentTypeID = eValue.getAttributeValue (ATTR_DOCUMENT_TYPE_ID);
    final String sProcessID = eValue.getAttributeValue (ATTR_PROCESS_ID);
    final String sTransportProfile = eValue.getAttributeValue (ATTR_TRANSPORT_PROFILE);
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.getFromIDOrNull (sTransportProfile);

    // Create object
    return new TestEndpointData (sDocumentTypeID, sProcessID, eTransportProfile);
  }
}
