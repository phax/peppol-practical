package com.helger.peppol.crm;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.appbasics.object.AbstractObjectMicroTypeConverter;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.impl.MicroElement;

public class CRMGroupMicroTypeConverter extends AbstractObjectMicroTypeConverter
{
  private static final String ATTR_DISPLAY_NAME = "displayname";
  private static final String ATTR_SENDER_EMAIL_ADDRESS = "senderemailaddress";

  @Nullable
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final ICRMGroup aValue = (ICRMGroup) aObject;
    final MicroElement aElement = new MicroElement (sNamespaceURI, sTagName);
    setObjectFields (aValue, aElement);
    aElement.setAttribute (ATTR_DISPLAY_NAME, aValue.getDisplayName ());
    aElement.setAttribute (ATTR_SENDER_EMAIL_ADDRESS, aValue.getSenderEmailAddress ());
    return aElement;
  }

  @Nullable
  public CRMGroup convertToNative (@Nonnull final IMicroElement aElement)
  {
    final String sDisplayName = aElement.getAttribute (ATTR_DISPLAY_NAME);
    final String sSenderEmailAddress = aElement.getAttribute (ATTR_SENDER_EMAIL_ADDRESS);
    return new CRMGroup (getStubObject (aElement), sDisplayName, sSenderEmailAddress);
  }
}
