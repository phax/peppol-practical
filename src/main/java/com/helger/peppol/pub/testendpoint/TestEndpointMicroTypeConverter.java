/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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

import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.smp.ESMPTransportProfile;
import com.helger.photon.security.object.AbstractBusinessObjectMicroTypeConverter;
import com.helger.photon.security.object.StubObject;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;

@Immutable
public final class TestEndpointMicroTypeConverter extends AbstractBusinessObjectMicroTypeConverter <TestEndpoint>
{
  private static final String ATTR_COMPANY_NAME = "companyname";
  private static final String ATTR_CONTACT_PERSON = "contactperson";
  // Legacy name
  private static final String ATTR_PARTICIPANT_ID_ISSUER = "participantidscheme";
  private static final String ATTR_PARTICIPANT_ID_VALUE = "participantidvalue";
  private static final String ATTR_TRANSPORT_PROFILE = "transportprofile";
  private static final String ATTR_SML = "sml";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final TestEndpoint aValue,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IMicroElement eValue = new MicroElement (sNamespaceURI, sTagName);
    setObjectFields (aValue, eValue);
    eValue.setAttribute (ATTR_COMPANY_NAME, aValue.getCompanyName ());
    eValue.setAttribute (ATTR_CONTACT_PERSON, aValue.getContactPerson ());
    eValue.setAttribute (ATTR_PARTICIPANT_ID_ISSUER, aValue.getParticipantIDIssuer ());
    eValue.setAttribute (ATTR_PARTICIPANT_ID_VALUE, aValue.getParticipantIDValue ());
    eValue.setAttribute (ATTR_TRANSPORT_PROFILE, aValue.getTransportProfile ().getID ());
    eValue.setAttribute (ATTR_SML, aValue.getSML ().getID ());
    return eValue;
  }

  @Nonnull
  public TestEndpoint convertToNative (@Nonnull final IMicroElement eValue)
  {
    final StubObject aStubObject = getStubObject (eValue);

    final String sCompanyName = eValue.getAttributeValue (ATTR_COMPANY_NAME);
    final String sContactPerson = eValue.getAttributeValue (ATTR_CONTACT_PERSON);
    final String sParticipantIDScheme = eValue.getAttributeValue (ATTR_PARTICIPANT_ID_ISSUER);
    final String sParticipantIDValue = eValue.getAttributeValue (ATTR_PARTICIPANT_ID_VALUE);

    final String sTransportProfile = eValue.getAttributeValue (ATTR_TRANSPORT_PROFILE);
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.getFromIDOrNull (sTransportProfile);

    final String sSMLID = eValue.getAttributeValue (ATTR_SML);
    final ISMLInfo aSML = PPMetaManager.getSMLInfoMgr ().getSMLInfoOfID (sSMLID);
    if (aSML == null)
      throw new IllegalStateException ("Failed to resolve SML with ID '" + sSMLID + "'");

    // Create object
    return new TestEndpoint (aStubObject,
                             sCompanyName,
                             sContactPerson,
                             sParticipantIDScheme,
                             sParticipantIDValue,
                             eTransportProfile,
                             aSML);
  }
}
