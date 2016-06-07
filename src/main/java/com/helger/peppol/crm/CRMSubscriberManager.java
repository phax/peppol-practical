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

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.ICommonsCollection;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.masterdata.person.ESalutation;
import com.helger.photon.basic.app.dao.impl.AbstractMapBasedWALDAO;
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
public final class CRMSubscriberManager extends AbstractMapBasedWALDAO <ICRMSubscriber, CRMSubscriber>
{
  public CRMSubscriberManager (@Nonnull @Nonempty final String sFilename) throws DAOException
  {
    super (CRMSubscriber.class, sFilename);
  }

  @Nonnull
  public ICRMSubscriber createCRMSubscriber (@Nullable final ESalutation eSalutation,
                                             @Nonnull @Nonempty final String sName,
                                             @Nonnull @Nonempty final String sEmailAddress,
                                             @Nullable final Set <ICRMGroup> aAssignedGroups)
  {
    final CRMSubscriber aCRMSubscriber = new CRMSubscriber (eSalutation, sName, sEmailAddress, aAssignedGroups);

    m_aRWLock.writeLocked ( () -> {
      internalCreateItem (aCRMSubscriber);
    });
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
    final CRMSubscriber aCRMSubscriber = getOfID (sCRMSubscriberID);
    if (aCRMSubscriber == null)
    {
      AuditHelper.onAuditModifyFailure (CRMSubscriber.OT_CRM_SUBSCRIBER, sCRMSubscriberID, "no-such-id");
      return EChange.UNCHANGED;
    }

    m_aRWLock.writeLock ().lock ();
    try
    {
      EChange eChange = EChange.UNCHANGED;
      // ID cannot be changed!
      eChange = eChange.or (aCRMSubscriber.setSalutation (eSalutation));
      eChange = eChange.or (aCRMSubscriber.setName (sName));
      eChange = eChange.or (aCRMSubscriber.setEmailAddress (sEmailAddress));
      eChange = eChange.or (aCRMSubscriber.setAssignedGroups (aAssignedGroups));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;

      ObjectHelper.setLastModificationNow (aCRMSubscriber);
      internalUpdateItem (aCRMSubscriber);
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
    final CRMSubscriber aCRMSubscriber = getOfID (sCRMSubscriberID);
    if (aCRMSubscriber == null)
    {
      AuditHelper.onAuditModifyFailure (CRMSubscriber.OT_CRM_SUBSCRIBER, sCRMSubscriberID, "no-such-id");
      return EChange.UNCHANGED;
    }

    m_aRWLock.writeLock ().lock ();
    try
    {
      EChange eChange = EChange.UNCHANGED;
      eChange = eChange.or (aCRMSubscriber.setAssignedGroups (aAssignedGroups));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;

      ObjectHelper.setLastModificationNow (aCRMSubscriber);
      internalUpdateItem (aCRMSubscriber);
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
  public ICommonsCollection <? extends ICRMSubscriber> getAllCRMSubscribers ()
  {
    return getAll ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <? extends ICRMSubscriber> getAllActiveCRMSubscribers ()
  {
    return getAll (c -> !c.isDeleted ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <? extends ICRMSubscriber> getAllDeletedCRMSubscribers ()
  {
    return getAll (c -> c.isDeleted ());
  }

  @Nullable
  public ICRMSubscriber getCRMSubscriberOfID (@Nullable final String sID)
  {
    return getOfID (sID);
  }

  public boolean containsCRMSubscriberWithID (@Nullable final String sID)
  {
    return containsWithID (sID);
  }

  @Nullable
  public ICRMSubscriber getCRMSubscriberOfEmailAddress (@Nullable final String sEmailAddress)
  {
    if (StringHelper.hasNoText (sEmailAddress))
      return null;

    // Unify before checking
    final String sRealEmailAddress = ICRMSubscriber.getUnifiedEmailAddress (sEmailAddress);
    return findFirst (c -> c.getEmailAddress ().equals (sRealEmailAddress));
  }

  @Nonnegative
  public long getCRMSubscriberCountOfGroup (@Nonnull final ICRMGroup aGroup)
  {
    ValueEnforcer.notNull (aGroup, "Group");

    return getCount (c -> c.isAssignedToGroup (aGroup));
  }

  @Nonnull
  public EChange deleteCRMSubscriber (@Nonnull final ICRMSubscriber aCRMSubscriber)
  {
    ValueEnforcer.notNull (aCRMSubscriber, "CRMSubscriber");

    final String sCRMSubscriberID = aCRMSubscriber.getID ();
    final CRMSubscriber aRealCRMSubscriber = getOfID (sCRMSubscriberID);
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
      internalMarkItemDeleted (aRealCRMSubscriber);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditDeleteSuccess (CRMSubscriber.OT_CRM_SUBSCRIBER, sCRMSubscriberID);
    return EChange.CHANGED;
  }
}
