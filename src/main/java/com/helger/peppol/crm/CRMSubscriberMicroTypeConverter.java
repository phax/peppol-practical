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
package com.helger.peppol.crm;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.id.ComparatorHasIDString;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroElement;
import com.helger.commons.string.StringHelper;
import com.helger.masterdata.person.ESalutation;
import com.helger.peppol.app.mgr.PPMetaManager;
import com.helger.photon.security.object.AbstractObjectMicroTypeConverter;

public class CRMSubscriberMicroTypeConverter extends AbstractObjectMicroTypeConverter
{
  private static final String ATTR_SALUTATION = "salutation";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_EMAIL_ADDRESS = "emailaddress";
  private static final String ELEMENT_ASSIGNED_GROUP = "assignedgroup";

  @Nullable
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final ICRMSubscriber aValue = (ICRMSubscriber) aObject;
    final MicroElement aElement = new MicroElement (sNamespaceURI, sTagName);
    setObjectFields (aValue, aElement);
    aElement.setAttribute (ATTR_SALUTATION, aValue.getSalutationID ());
    aElement.setAttribute (ATTR_NAME, aValue.getName ());
    aElement.setAttribute (ATTR_EMAIL_ADDRESS, aValue.getEmailAddress ());
    for (final ICRMGroup aGroup : CollectionHelper.getSorted (aValue.getAllAssignedGroups (),
                                                              new ComparatorHasIDString <ICRMGroup> ()))
      aElement.appendElement (sNamespaceURI, ELEMENT_ASSIGNED_GROUP).setAttribute (ATTR_ID, aGroup.getID ());
    return aElement;
  }

  @Nullable
  public CRMSubscriber convertToNative (@Nonnull final IMicroElement aElement)
  {
    final CRMGroupManager aCRMGroupMgr = PPMetaManager.getCRMGroupMgr ();

    final String sSalutationID = aElement.getAttributeValue (ATTR_SALUTATION);
    final ESalutation eSalutation = ESalutation.getFromIDOrNull (sSalutationID);
    if (eSalutation == null && StringHelper.hasText (sSalutationID))
      throw new IllegalStateException ("Failed to resolve salutation ID '" + sSalutationID + "'");

    final String sName = aElement.getAttributeValue (ATTR_NAME);
    final String sEmailAddress = aElement.getAttributeValue (ATTR_EMAIL_ADDRESS);

    final Set <ICRMGroup> aGroups = new HashSet <ICRMGroup> ();
    for (final IMicroElement eGroup : aElement.getAllChildElements (ELEMENT_ASSIGNED_GROUP))
    {
      final String sCRMGroupID = eGroup.getAttributeValue (ATTR_ID);
      final ICRMGroup aCRMGroup = aCRMGroupMgr.getCRMGroupOfID (sCRMGroupID);
      if (aCRMGroup == null)
        throw new IllegalStateException ("Failed to resolve CRM group with ID '" + sCRMGroupID + "'");
      aGroups.add (aCRMGroup);
    }

    return new CRMSubscriber (getStubObject (aElement), eSalutation, sName, sEmailAddress, aGroups);
  }
}
