/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.peppol.crm;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.photon.security.object.AbstractBusinessObjectMicroTypeConverter;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;


public class CRMGroupMicroTypeConverter extends AbstractBusinessObjectMicroTypeConverter <CRMGroup>
{
  private static final String ATTR_DISPLAY_NAME = "displayname";
  private static final String ATTR_SENDER_EMAIL_ADDRESS = "senderemailaddress";

  @Nullable
  public IMicroElement convertToMicroElement (@NonNull final CRMGroup aValue,
                                              @Nullable final String sNamespaceURI,
                                              @NonNull final String sTagName)
  {
    final MicroElement aElement = new MicroElement (sNamespaceURI, sTagName);
    setObjectFields (aValue, aElement);
    aElement.setAttribute (ATTR_DISPLAY_NAME, aValue.getDisplayName ());
    aElement.setAttribute (ATTR_SENDER_EMAIL_ADDRESS, aValue.getSenderEmailAddress ());
    return aElement;
  }

  @Nullable
  public CRMGroup convertToNative (@NonNull final IMicroElement aElement)
  {
    final String sDisplayName = aElement.getAttributeValue (ATTR_DISPLAY_NAME);
    final String sSenderEmailAddress = aElement.getAttributeValue (ATTR_SENDER_EMAIL_ADDRESS);
    return new CRMGroup (getStubObject (aElement), sDisplayName, sSenderEmailAddress);
  }
}
