/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.peppol.domain;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.sml.SMLInfo;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.IMicroQName;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.MicroQName;
import com.helger.xml.microdom.convert.IMicroTypeConverter;
import com.helger.xml.microdom.convert.MicroTypeConverter;

public final class ExtendedSMLInfoMicroTypeConverter implements IMicroTypeConverter <ExtendedSMLInfo>
{
  private static final String ELEMENT_SML_INFO = "smlinfo";
  private static final IMicroQName ATTR_SMP_API_TYPE = new MicroQName ("smpapitype");

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final ExtendedSMLInfo aObj,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IMicroElement aElement = new MicroElement (sNamespaceURI, sTagName);
    aElement.appendChild (MicroTypeConverter.convertToMicroElement (aObj.getSMLInfo (),
                                                                    sNamespaceURI,
                                                                    ELEMENT_SML_INFO));
    aElement.setAttribute (ATTR_SMP_API_TYPE, aObj.getSMPAPIType ().getID ());
    return aElement;
  }

  @Nonnull
  public ExtendedSMLInfo convertToNative (@Nonnull final IMicroElement aElement)
  {
    final IMicroElement eSMLInfo = aElement.getFirstChildElement (ELEMENT_SML_INFO);
    final SMLInfo aSMLInfo;
    final ESMPAPIType eSMPAPIType;
    if (eSMLInfo != null)
    {
      aSMLInfo = MicroTypeConverter.convertToNative (eSMLInfo, SMLInfo.class);
      eSMPAPIType = ESMPAPIType.getFromIDOrNull (aElement.getAttributeValue (ATTR_SMP_API_TYPE));
    }
    else
    {
      // Assume we read legacy data
      aSMLInfo = MicroTypeConverter.convertToNative (aElement, SMLInfo.class);
      eSMPAPIType = ESMPAPIType.PEPPOL;
    }
    return new ExtendedSMLInfo (aSMLInfo, eSMPAPIType);
  }
}
