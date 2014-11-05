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

import org.joda.time.DateTime;

import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.IMicroTypeConverter;
import com.helger.commons.microdom.impl.MicroElement;

import eu.europa.ec.cipa.smp.client.ESMPTransportProfile;

@Immutable
public final class TestEndpointMicroTypeConverter implements IMicroTypeConverter
{
  private static final String ATTR_ID = "id";
  private static final String ATTR_CREATIONDT = "creationdt";
  private static final String ATTR_USER_ID = "userid";
  private static final String ATTR_COMPANY_NAME = "companyname";
  private static final String ATTR_PARTICIPANT_ID = "participantid";
  private static final String ATTR_TRANSPORT_PROFILE = "transportprofile";
  private static final String ATTR_CONTACT_PERSON = "contactperson";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final TestEndpoint aComment = (TestEndpoint) aObject;

    final IMicroElement eComment = new MicroElement (sNamespaceURI, sTagName);
    eComment.setAttribute (ATTR_ID, aComment.getID ());
    eComment.setAttributeWithConversion (ATTR_CREATIONDT, aComment.getCreationDateTime ());
    eComment.setAttribute (ATTR_USER_ID, aComment.getCreationUserID ());
    eComment.setAttribute (ATTR_COMPANY_NAME, aComment.getCompanyName ());
    eComment.setAttribute (ATTR_PARTICIPANT_ID, aComment.getParticipantIDValue ());
    eComment.setAttribute (ATTR_TRANSPORT_PROFILE, aComment.getTransportProfile ().getID ());
    eComment.setAttribute (ATTR_CONTACT_PERSON, aComment.getContactPerson ());
    return eComment;
  }

  @Nonnull
  public TestEndpoint convertToNative (@Nonnull final IMicroElement eComment)
  {
    final String sCommentID = eComment.getAttributeValue (ATTR_ID);
    final DateTime aCreationDT = eComment.getAttributeWithConversion (ATTR_CREATIONDT, DateTime.class);
    final String sCreationUserID = eComment.getAttributeValue (ATTR_USER_ID);

    final String sCompanyName = eComment.getAttributeValue (ATTR_COMPANY_NAME);
    final String sParticipantIDValue = eComment.getAttributeValue (ATTR_PARTICIPANT_ID);
    final String sTransportProfile = eComment.getAttributeValue (ATTR_TRANSPORT_PROFILE);
    final ESMPTransportProfile eTransportProfile = ESMPTransportProfile.getFromIDOrNull (sTransportProfile);
    final String sContactPerson = eComment.getAttributeValue (ATTR_CONTACT_PERSON);

    // Create object
    return new TestEndpoint (sCommentID,
                             aCreationDT,
                             sCreationUserID,
                             sCompanyName,
                             sParticipantIDValue,
                             eTransportProfile,
                             sContactPerson);
  }
}
