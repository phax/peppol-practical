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
package com.helger.peppol.app.mgr;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.dao.DAOException;
import com.helger.peppol.domain.ExtendedSMLInfo;
import com.helger.peppol.domain.IExtendedSMLInfo;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.sml.SMLInfo;
import com.helger.photon.app.dao.AbstractPhotonMapBasedWALDAO;
import com.helger.photon.audit.AuditHelper;

public final class SMLInfoManager extends AbstractPhotonMapBasedWALDAO <IExtendedSMLInfo, ExtendedSMLInfo> implements
                                  ISMLInfoManager
{
  public SMLInfoManager (@Nonnull @Nonempty final String sFilename) throws DAOException
  {
    super (ExtendedSMLInfo.class, sFilename);
  }

  @Override
  @Nonnull
  protected EChange onInit ()
  {
    // Add the default transport profiles
    for (final ESML e : ESML.values ())
      internalCreateItem (ExtendedSMLInfo.create (e));
    return EChange.CHANGED;
  }

  @Nonnull
  public IExtendedSMLInfo createSMLInfo (@Nonnull @Nonempty final String sDisplayName,
                                         @Nonnull @Nonempty final String sDNSZone,
                                         @Nonnull @Nonempty final String sManagementServiceURL,
                                         final boolean bClientCertificateRequired,
                                         @Nonnull final ESMPAPIType eSMPAPIType)
  {
    final SMLInfo aSMLInfo = new SMLInfo (sDisplayName, sDNSZone, sManagementServiceURL, bClientCertificateRequired);
    final ExtendedSMLInfo aExtSMLInfo = new ExtendedSMLInfo (aSMLInfo, eSMPAPIType);

    m_aRWLock.writeLocked ( () -> {
      internalCreateItem (aExtSMLInfo);
    });
    AuditHelper.onAuditCreateSuccess (SMLInfo.OT,
                                      aSMLInfo.getID (),
                                      sDisplayName,
                                      sDNSZone,
                                      sManagementServiceURL,
                                      Boolean.valueOf (bClientCertificateRequired),
                                      eSMPAPIType);
    return aExtSMLInfo;
  }

  @Nonnull
  public EChange updateSMLInfo (@Nullable final String sSMLInfoID,
                                @Nonnull @Nonempty final String sDisplayName,
                                @Nonnull @Nonempty final String sDNSZone,
                                @Nonnull @Nonempty final String sManagementServiceURL,
                                final boolean bClientCertificateRequired,
                                @Nonnull final ESMPAPIType eSMPAPIType)
  {
    final ExtendedSMLInfo aExtSMLInfo = getOfID (sSMLInfoID);
    if (aExtSMLInfo == null)
    {
      AuditHelper.onAuditModifyFailure (SMLInfo.OT, sSMLInfoID, "no-such-id");
      return EChange.UNCHANGED;
    }

    m_aRWLock.writeLock ().lock ();
    try
    {
      final SMLInfo aSMLInfo = aExtSMLInfo.getSMLInfo ();
      EChange eChange = EChange.UNCHANGED;
      eChange = eChange.or (aSMLInfo.setDisplayName (sDisplayName));
      eChange = eChange.or (aSMLInfo.setDNSZone (sDNSZone));
      eChange = eChange.or (aSMLInfo.setManagementServiceURL (sManagementServiceURL));
      eChange = eChange.or (aSMLInfo.setClientCertificateRequired (bClientCertificateRequired));
      eChange = eChange.or (aExtSMLInfo.setSMPAPIType (eSMPAPIType));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;

      internalUpdateItem (aExtSMLInfo);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditModifySuccess (SMLInfo.OT,
                                      "all",
                                      sSMLInfoID,
                                      sDisplayName,
                                      sDNSZone,
                                      sManagementServiceURL,
                                      Boolean.valueOf (bClientCertificateRequired),
                                      eSMPAPIType);
    return EChange.CHANGED;
  }

  @Nullable
  public EChange removeSMLInfo (@Nullable final String sSMLInfoID)
  {
    if (StringHelper.hasNoText (sSMLInfoID))
      return EChange.UNCHANGED;

    m_aRWLock.writeLock ().lock ();
    try
    {
      final ExtendedSMLInfo aExtSMLInfo = internalDeleteItem (sSMLInfoID);
      if (aExtSMLInfo == null)
      {
        AuditHelper.onAuditDeleteFailure (SMLInfo.OT, "no-such-id", sSMLInfoID);
        return EChange.UNCHANGED;
      }
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditDeleteSuccess (SMLInfo.OT, sSMLInfoID);
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IExtendedSMLInfo> getAllSorted ()
  {
    return getAll ().getSortedInline ( (c1, c2) -> {
      final String d1 = c1.getSMLInfo ().getDNSZone ();
      final String d2 = c2.getSMLInfo ().getDNSZone ();
      // Short before long
      int ret = d1.length () - d2.length ();
      if (ret == 0)
        ret = d1.compareTo (d2);
      return ret;
    });
  }

  @Nullable
  public IExtendedSMLInfo getSMLInfoOfID (@Nullable final String sID)
  {
    return getOfID (sID);
  }

  public boolean containsSMLInfoWithID (@Nullable final String sID)
  {
    return containsWithID (sID);
  }
}
