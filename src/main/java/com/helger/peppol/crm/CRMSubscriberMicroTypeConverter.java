package com.helger.peppol.crm;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.appbasics.object.AbstractObjectMicroTypeConverter;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.id.ComparatorHasIDString;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.impl.MicroElement;
import com.helger.commons.string.StringHelper;
import com.helger.masterdata.person.ESalutation;
import com.helger.peppol.mgr.MetaManager;

public class CRMSubscriberMicroTypeConverter extends AbstractObjectMicroTypeConverter
{
  private static final String ATTR_SALUTATION = "salutation";
  private static final String ATTR_NAME = "name";
  private static final String ATTR_EMAIL_ADDRESS = "emailaddress";
  private static final String ELEMENT_ASSIGNED_GROUP = "assignedgroup";
  private static final String ATTR_ID = "id";

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
    for (final ICRMGroup aGroup : ContainerHelper.getSorted (aValue.getAllAssignedGroups (),
                                                             new ComparatorHasIDString <ICRMGroup> ()))
      aElement.appendElement (sNamespaceURI, ELEMENT_ASSIGNED_GROUP).setAttribute (ATTR_ID, aGroup.getID ());
    return aElement;
  }

  @Nullable
  public CRMSubscriber convertToNative (@Nonnull final IMicroElement aElement)
  {
    final CRMGroupManager aCRMGroupMgr = MetaManager.getCRMGroupMgr ();

    final String sSalutationID = aElement.getAttribute (ATTR_SALUTATION);
    final ESalutation eSalutation = ESalutation.getFromIDOrNull (sSalutationID);
    if (eSalutation == null && StringHelper.hasText (sSalutationID))
      throw new IllegalStateException ("Failed to resolve salutation ID '" + sSalutationID + "'");

    final String sName = aElement.getAttribute (ATTR_NAME);
    final String sEmailAddress = aElement.getAttribute (ATTR_EMAIL_ADDRESS);

    final Set <ICRMGroup> aGroups = new HashSet <ICRMGroup> ();
    for (final IMicroElement eGroup : aElement.getAllChildElements (ELEMENT_ASSIGNED_GROUP))
    {
      final String sCRMGroupID = eGroup.getAttribute (ATTR_ID);
      final ICRMGroup aCRMGroup = aCRMGroupMgr.getCRMGroupOfID (sCRMGroupID);
      if (aCRMGroup == null)
        throw new IllegalStateException ("Failed to resolve CRM group with ID '" + sCRMGroupID + "'");
      aGroups.add (aCRMGroup);
    }

    return new CRMSubscriber (getStubObject (aElement), eSalutation, sName, sEmailAddress, aGroups);
  }
}
