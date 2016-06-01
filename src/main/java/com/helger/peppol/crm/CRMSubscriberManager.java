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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ext.CommonsHashMap;
import com.helger.commons.collection.ext.ICommonsMap;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroDocument;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.masterdata.person.ESalutation;
import com.helger.photon.basic.app.dao.impl.AbstractSimpleDAO;
import com.helger.photon.basic.app.dao.impl.DAOException;
import com.helger.photon.basic.audit.AuditHelper;
import com.helger.photon.security.object.ObjectHelper;

/**
 * Manager for {@link CRMSubscriber} instances.
 *
 * @author Philip Helger
 * @see com.helger.peppol.app.mgr.PPMetaManager
 */
@ThreadSafe
public final class CRMSubscriberManager extends AbstractSimpleDAO
{
  private static final String ELEMENT_ROOT = "crmsubscribers";
  private static final String ELEMENT_ITEM = "crmsubscriber";

  private final ICommonsMap <String, CRMSubscriber> m_aMap = new CommonsHashMap<> ();

  public CRMSubscriberManager (@Nonnull @Nonempty final String sFilename) throws DAOException
  {
    super (sFilename);
    initialRead ();
  }

  @Override
  @Nonnull
  protected EChange onRead (@Nonnull final IMicroDocument aDoc)
  {
    for (final IMicroElement eCRMSubscriber : aDoc.getDocumentElement ().getAllChildElements (ELEMENT_ITEM))
      _addCRMSubscriber (MicroTypeConverter.convertToNative (eCRMSubscriber, CRMSubscriber.class));
    return EChange.UNCHANGED;
  }

  @Override
  @Nonnull
  protected IMicroDocument createWriteData ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement (ELEMENT_ROOT);
    for (final CRMSubscriber aCRMSubscriber : CollectionHelper.getSortedByKey (m_aMap).values ())
      eRoot.appendChild (MicroTypeConverter.convertToMicroElement (aCRMSubscriber, ELEMENT_ITEM));
    return aDoc;
  }

  private void _addCRMSubscriber (@Nonnull final CRMSubscriber aCRMSubscriber)
  {
    ValueEnforcer.notNull (aCRMSubscriber, "CRMSubscriber");

    final String sCRMSubscriberID = aCRMSubscriber.getID ();
    if (m_aMap.containsKey (sCRMSubscriberID))
      throw new IllegalArgumentException ("CRMSubscriber ID '" + sCRMSubscriberID + "' is already in use!");
    m_aMap.put (sCRMSubscriberID, aCRMSubscriber);
  }

  @Nonnull
  public ICRMSubscriber createCRMSubscriber (@Nullable final ESalutation eSalutation,
                                             @Nonnull @Nonempty final String sName,
                                             @Nonnull @Nonempty final String sEmailAddress,
                                             @Nullable final Set <ICRMGroup> aAssignedGroups)
  {
    final CRMSubscriber aCRMSubscriber = new CRMSubscriber (eSalutation, sName, sEmailAddress, aAssignedGroups);

    m_aRWLock.writeLock ().lock ();
    try
    {
      _addCRMSubscriber (aCRMSubscriber);
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditCreateSuccess (CRMSubscriber.OT_CRM_SUBSCRIBER,
                                      aCRMSubscriber.getID (),
                                      eSalutation,
                                      sName,
                                      sEmailAddress,
                                      aAssignedGroups);
    return aCRMSubscriber;
  }

  @Nonnull
  public EChange updateCRMSubscriber (@Nullable final String sCRMSubscriberID,
                                      @Nullable final ESalutation eSalutation,
                                      @Nonnull @Nonempty final String sName,
                                      @Nonnull @Nonempty final String sEmailAddress,
                                      @Nullable final Set <ICRMGroup> aAssignedGroups)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      final CRMSubscriber aCRMSubscriber = m_aMap.get (sCRMSubscriberID);
      if (aCRMSubscriber == null)
      {
        AuditHelper.onAuditModifyFailure (CRMSubscriber.OT_CRM_SUBSCRIBER, sCRMSubscriberID, "no-such-id");
        return EChange.UNCHANGED;
      }

      EChange eChange = EChange.UNCHANGED;
      // ID cannot be changed!
      eChange = eChange.or (aCRMSubscriber.setSalutation (eSalutation));
      eChange = eChange.or (aCRMSubscriber.setName (sName));
      eChange = eChange.or (aCRMSubscriber.setEmailAddress (sEmailAddress));
      eChange = eChange.or (aCRMSubscriber.setAssignedGroups (aAssignedGroups));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;

      ObjectHelper.setLastModificationNow (aCRMSubscriber);
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditModifySuccess (CRMSubscriber.OT_CRM_SUBSCRIBER,
                                      "all",
                                      sCRMSubscriberID,
                                      eSalutation,
                                      sName,
                                      sEmailAddress,
                                      aAssignedGroups);
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange updateCRMSubscriberGroupAssignments (@Nullable final String sCRMSubscriberID,
                                                      @Nullable final Set <ICRMGroup> aAssignedGroups)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      final CRMSubscriber aCRMSubscriber = m_aMap.get (sCRMSubscriberID);
      if (aCRMSubscriber == null)
      {
        AuditHelper.onAuditModifyFailure (CRMSubscriber.OT_CRM_SUBSCRIBER, sCRMSubscriberID, "no-such-id");
        return EChange.UNCHANGED;
      }

      EChange eChange = EChange.UNCHANGED;
      eChange = eChange.or (aCRMSubscriber.setAssignedGroups (aAssignedGroups));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;

      ObjectHelper.setLastModificationNow (aCRMSubscriber);
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditModifySuccess (CRMSubscriber.OT_CRM_SUBSCRIBER,
                                      "assigned-groups",
                                      sCRMSubscriberID,
                                      aAssignedGroups);
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <? extends ICRMSubscriber> getAllCRMSubscribers ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return CollectionHelper.newList (m_aMap.values ());
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <ICRMSubscriber> getAllActiveCRMSubscribers ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMap.values ().stream ().filter (c -> !c.isDeleted ()).collect (Collectors.toList ());
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <ICRMSubscriber> getAllDeletedCRMSubscribers ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMap.values ().stream ().filter (c -> c.isDeleted ()).collect (Collectors.toList ());
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public ICRMSubscriber getCRMSubscriberOfID (@Nullable final String sID)
  {
    if (StringHelper.hasNoText (sID))
      return null;

    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMap.get (sID);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  public boolean containsCRMSubscriberWithID (@Nullable final String sID)
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMap.containsKey (sID);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public ICRMSubscriber getCRMSubscriberOfEmailAddress (@Nullable final String sEmailAddress)
  {
    if (StringHelper.hasNoText (sEmailAddress))
      return null;

    // Unify before checking
    final String sRealEmailAddress = ICRMSubscriber.getUnifiedEmailAddress (sEmailAddress);
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMap.values ()
                   .stream ()
                   .filter (c -> c.getEmailAddress ().equals (sRealEmailAddress))
                   .findAny ()
                   .orElse (null);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnegative
  public long getCRMSubscriberCountOfGroup (@Nonnull final ICRMGroup aGroup)
  {
    ValueEnforcer.notNull (aGroup, "Group");

    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMap.values ().stream ().filter (c -> c.isAssignedToGroup (aGroup)).count ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange deleteCRMSubscriber (@Nonnull final ICRMSubscriber aCRMSubscriber)
  {
    ValueEnforcer.notNull (aCRMSubscriber, "CRMSubscriber");

    final String sCRMSubscriberID = aCRMSubscriber.getID ();
    final CRMSubscriber aRealCRMSubscriber = (CRMSubscriber) getCRMSubscriberOfID (sCRMSubscriberID);
    if (aRealCRMSubscriber == null)
    {
      AuditHelper.onAuditDeleteFailure (CRMSubscriber.OT_CRM_SUBSCRIBER, sCRMSubscriberID, "id-not-found");
      return EChange.UNCHANGED;
    }

    m_aRWLock.writeLock ().lock ();
    try
    {
      if (ObjectHelper.setDeletionNow (aRealCRMSubscriber).isUnchanged ())
      {
        AuditHelper.onAuditDeleteFailure (CRMSubscriber.OT_CRM_SUBSCRIBER, sCRMSubscriberID, "already-deleted");
        return EChange.UNCHANGED;
      }
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditDeleteSuccess (CRMSubscriber.OT_CRM_SUBSCRIBER, sCRMSubscriberID);
    return EChange.CHANGED;
  }
}
