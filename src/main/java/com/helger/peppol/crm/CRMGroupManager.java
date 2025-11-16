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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.base.state.EChange;
import com.helger.dao.DAOException;
import com.helger.photon.audit.AuditHelper;
import com.helger.photon.io.dao.AbstractPhotonMapBasedWALDAO;
import com.helger.photon.security.object.BusinessObjectHelper;


/**
 * Manager for {@link CRMGroup} instances.
 *
 * @author Philip Helger
 * @see com.helger.peppol.app.mgr.PPMetaManager
 */
@ThreadSafe
public final class CRMGroupManager extends AbstractPhotonMapBasedWALDAO <ICRMGroup, CRMGroup>
{
  public CRMGroupManager (@NonNull @Nonempty final String sFilename) throws DAOException
  {
    super (CRMGroup.class, sFilename);
  }

  @NonNull
  public ICRMGroup createCRMGroup (@NonNull @Nonempty final String sDisplayName,
                                   @NonNull @Nonempty final String sSenderEmailAddress)
  {
    final CRMGroup aCRMGroup = new CRMGroup (sDisplayName, sSenderEmailAddress);

    m_aRWLock.writeLocked ( () -> { internalCreateItem (aCRMGroup); });
    AuditHelper.onAuditCreateSuccess (CRMGroup.OT_CRM_GROUP, aCRMGroup.getID (), sDisplayName, sSenderEmailAddress);
    return aCRMGroup;
  }

  @NonNull
  public EChange updateCRMGroup (@Nullable final String sCRMGroupID,
                                 @NonNull @Nonempty final String sDisplayName,
                                 @NonNull @Nonempty final String sSenderEmailAddress)
  {
    final CRMGroup aCRMGroup = getOfID (sCRMGroupID);
    if (aCRMGroup == null)
    {
      AuditHelper.onAuditModifyFailure (CRMGroup.OT_CRM_GROUP, sCRMGroupID, "no-such-id");
      return EChange.UNCHANGED;
    }

    m_aRWLock.writeLock ().lock ();
    try
    {
      EChange eChange = EChange.UNCHANGED;
      // ID cannot be changed!
      eChange = eChange.or (aCRMGroup.setDisplayName (sDisplayName));
      eChange = eChange.or (aCRMGroup.setSenderEmailAddress (sSenderEmailAddress));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;

      BusinessObjectHelper.setLastModificationNow (aCRMGroup);
      internalUpdateItem (aCRMGroup);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditModifySuccess (CRMGroup.OT_CRM_GROUP, "all", sCRMGroupID, sDisplayName, sSenderEmailAddress);
    return EChange.CHANGED;
  }

  @Nullable
  public ICRMGroup getCRMGroupOfID (@Nullable final String sID)
  {
    return getOfID (sID);
  }
}
