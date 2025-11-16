/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.base.string.StringHelper;
import com.helger.collection.commons.ICommonsList;
import com.helger.dao.DAOException;
import com.helger.masterdata.person.ESalutation;
import com.helger.photon.audit.AuditHelper;
import com.helger.photon.io.dao.AbstractPhotonMapBasedWALDAO;
import com.helger.photon.security.object.BusinessObjectHelper;


/**
 * Manager for {@link CRMSubscriber} instances.
 *
 * @author Philip Helger
 * @see com.helger.peppol.app.mgr.PPMetaManager
 */
@ThreadSafe
public final class CRMSubscriberManager extends AbstractPhotonMapBasedWALDAO <ICRMSubscriber, CRMSubscriber>
{
  public CRMSubscriberManager (@NonNull @Nonempty final String sFilename) throws DAOException
  {
    super (CRMSubscriber.class, sFilename);
  }

  @NonNull
  public ICRMSubscriber createCRMSubscriber (@Nullable final ESalutation eSalutation,
                                             @NonNull @Nonempty final String sName,
                                             @NonNull @Nonempty final String sEmailAddress,
                                             @Nullable final Set <ICRMGroup> aAssignedGroups)
  {
    final CRMSubscriber aCRMSubscriber = new CRMSubscriber (eSalutation, sName, sEmailAddress, aAssignedGroups);

    m_aRWLock.writeLocked ( () -> { internalCreateItem (aCRMSubscriber); });
    AuditHelper.onAuditCreateSuccess (CRMSubscriber.OT_CRM_SUBSCRIBER,
                                      aCRMSubscriber.getID (),
                                      eSalutation,
                                      sName,
                                      sEmailAddress,
                                      aAssignedGroups);
    return aCRMSubscriber;
  }

  @NonNull
  public EChange updateCRMSubscriber (@Nullable final String sCRMSubscriberID,
                                      @Nullable final ESalutation eSalutation,
                                      @NonNull @Nonempty final String sName,
                                      @NonNull @Nonempty final String sEmailAddress,
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

      BusinessObjectHelper.setLastModificationNow (aCRMSubscriber);
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

  @NonNull
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

      BusinessObjectHelper.setLastModificationNow (aCRMSubscriber);
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

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <ICRMSubscriber> getAllActiveCRMSubscribers ()
  {
    return getAll (ICRMSubscriber::isNotDeleted);
  }

  @NonNull
  @ReturnsMutableCopy
  public ICommonsList <ICRMSubscriber> getAllDeletedCRMSubscribers ()
  {
    return getAll (ICRMSubscriber::isDeleted);
  }

  @Nullable
  public ICRMSubscriber getCRMSubscriberOfID (@Nullable final String sID)
  {
    return getOfID (sID);
  }

  @Nullable
  public ICRMSubscriber getCRMSubscriberOfEmailAddress (@Nullable final String sEmailAddress)
  {
    if (StringHelper.isEmpty (sEmailAddress))
      return null;

    // Unify before checking
    final String sRealEmailAddress = ICRMSubscriber.getUnifiedEmailAddress (sEmailAddress);
    return findFirst (c -> c.getEmailAddress ().equals (sRealEmailAddress));
  }

  @Nonnegative
  public long getCRMSubscriberCountOfGroup (@NonNull final ICRMGroup aGroup)
  {
    ValueEnforcer.notNull (aGroup, "Group");

    return getCount (c -> c.isAssignedToGroup (aGroup));
  }

  @NonNull
  public EChange deleteCRMSubscriber (@NonNull final ICRMSubscriber aCRMSubscriber)
  {
    ValueEnforcer.notNull (aCRMSubscriber, "CRMSubscriber");

    final String sCRMSubscriberID = aCRMSubscriber.getID ();
    final CRMSubscriber aRealCRMSubscriber = getOfID (sCRMSubscriberID);
    if (aRealCRMSubscriber == null)
    {
      AuditHelper.onAuditDeleteFailure (CRMSubscriber.OT_CRM_SUBSCRIBER, "id-not-found", sCRMSubscriberID);
      return EChange.UNCHANGED;
    }

    m_aRWLock.writeLock ().lock ();
    try
    {
      if (BusinessObjectHelper.setDeletionNow (aRealCRMSubscriber).isUnchanged ())
      {
        AuditHelper.onAuditDeleteFailure (CRMSubscriber.OT_CRM_SUBSCRIBER, "already-deleted", sCRMSubscriberID);
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
