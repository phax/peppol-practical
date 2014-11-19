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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTime;

import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.IMicroTypeConverter;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.impl.MicroElement;

@Immutable
public final class TestEndpointMicroTypeConverter implements IMicroTypeConverter
{
  private static final String ATTR_ID = "id";
  private static final String ATTR_CREATIONDT = "creationdt";
  private static final String ATTR_USER_ID = "userid";
  private static final String ATTR_COMPANY_NAME = "companyname";
  private static final String ATTR_CONTACT_PERSON = "contactperson";
  private static final String ATTR_PARTICIPANT_ID = "participantid";
  private static final String ELEMENT_DATA = "data";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final TestEndpoint aValue = (TestEndpoint) aObject;

    final IMicroElement eValue = new MicroElement (sNamespaceURI, sTagName);
    eValue.setAttribute (ATTR_ID, aValue.getID ());
    eValue.setAttributeWithConversion (ATTR_CREATIONDT, aValue.getCreationDateTime ());
    eValue.setAttribute (ATTR_USER_ID, aValue.getCreationUserID ());
    eValue.setAttribute (ATTR_COMPANY_NAME, aValue.getCompanyName ());
    eValue.setAttribute (ATTR_CONTACT_PERSON, aValue.getContactPerson ());
    eValue.setAttribute (ATTR_PARTICIPANT_ID, aValue.getParticipantIDValue ());
    for (final TestEndpointData aData : aValue.getAllDatas ())
      eValue.appendChild (MicroTypeConverter.convertToMicroElement (aData, sNamespaceURI, ELEMENT_DATA));

    return eValue;
  }

  @Nonnull
  public TestEndpoint convertToNative (@Nonnull final IMicroElement eValue)
  {
    final String sID = eValue.getAttributeValue (ATTR_ID);
    final DateTime aCreationDT = eValue.getAttributeWithConversion (ATTR_CREATIONDT, DateTime.class);
    final String sCreationUserID = eValue.getAttributeValue (ATTR_USER_ID);

    final String sCompanyName = eValue.getAttributeValue (ATTR_COMPANY_NAME);
    final String sContactPerson = eValue.getAttributeValue (ATTR_CONTACT_PERSON);
    final String sParticipantIDValue = eValue.getAttributeValue (ATTR_PARTICIPANT_ID);

    final List <TestEndpointData> aDatas = new ArrayList <> ();
    for (final IMicroElement eData : eValue.getAllChildElements (ELEMENT_DATA))
      aDatas.add (MicroTypeConverter.convertToNative (eData, TestEndpointData.class));

    // Create object
    return new TestEndpoint (sID,
                             aCreationDT,
                             sCreationUserID,
                             sCompanyName,
                             sContactPerson,
                             sParticipantIDValue,
                             aDatas);
  }
}
