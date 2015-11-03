/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.peppol.pub.testendpoint;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.ContainsSoftMigration;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroElement;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.smp.ESMPTransportProfile;
import com.helger.photon.basic.object.AbstractObjectMicroTypeConverter;
import com.helger.photon.basic.object.StubObject;

@Immutable
public final class TestEndpointMicroTypeConverter extends AbstractObjectMicroTypeConverter
{
  private static final String ATTR_COMPANY_NAME = "companyname";
  private static final String ATTR_CONTACT_PERSON = "contactperson";
  private static final String ATTR_PARTICIPANT_ID_SCHEME = "participantidscheme";
  private static final String ATTR_PARTICIPANT_ID_VALUE = "participantidvalue";
  private static final String ATTR_TRANSPORT_PROFILE = "transportprofile";
  private static final String ATTR_SML = "sml";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final TestEndpoint aValue = (TestEndpoint) aObject;

    final IMicroElement eValue = new MicroElement (sNamespaceURI, sTagName);
    setObjectFields (aValue, eValue);
    eValue.setAttribute (ATTR_COMPANY_NAME, aValue.getCompanyName ());
    eValue.setAttribute (ATTR_CONTACT_PERSON, aValue.getContactPerson ());
    eValue.setAttribute (ATTR_PARTICIPANT_ID_SCHEME, aValue.getParticipantIDScheme ());
    eValue.setAttribute (ATTR_PARTICIPANT_ID_VALUE, aValue.getParticipantIDValue ());
    eValue.setAttribute (ATTR_TRANSPORT_PROFILE, aValue.getTransportProfile ().getID ());
    eValue.setAttribute (ATTR_SML, aValue.getSML ().getID ());

    return eValue;
  }

  @Nonnull
  @ContainsSoftMigration
  public TestEndpoint convertToNative (@Nonnull final IMicroElement eValue)
  {
    final StubObject aStubObject = getStubObject (eValue);

    final String sCompanyName = eValue.getAttributeValue (ATTR_COMPANY_NAME);
    final String sContactPerson = eValue.getAttributeValue (ATTR_CONTACT_PERSON);
    final String sParticipantIDScheme = eValue.getAttributeValue (ATTR_PARTICIPANT_ID_SCHEME);
    final String sParticipantIDValue = eValue.getAttributeValue (ATTR_PARTICIPANT_ID_VALUE);

    final String sTransportProfile = eValue.getAttributeValue (ATTR_TRANSPORT_PROFILE);
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.getFromIDOrNull (sTransportProfile);

    final String sSML = eValue.getAttributeValue (ATTR_SML);
    // Soft migration
    final ESML eSML = sSML == null ? ESML.DIGIT_PRODUCTION : ESML.getFromIDOrNull (sSML);

    // Create object
    return new TestEndpoint (aStubObject,
                             sCompanyName,
                             sContactPerson,
                             sParticipantIDScheme,
                             sParticipantIDValue,
                             eTransportProfile,
                             eSML);
  }
}