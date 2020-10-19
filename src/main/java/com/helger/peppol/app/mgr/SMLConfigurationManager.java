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
import com.helger.peppol.domain.ISMLConfiguration;
import com.helger.peppol.domain.SMLConfiguration;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppol.sml.SMLInfo;
import com.helger.peppolid.factory.ESMPIdentifierType;
import com.helger.photon.app.dao.AbstractPhotonMapBasedWALDAO;
import com.helger.photon.audit.AuditHelper;

public final class SMLConfigurationManager extends AbstractPhotonMapBasedWALDAO <ISMLConfiguration, SMLConfiguration> implements
                                           ISMLConfigurationManager
{
  public SMLConfigurationManager (@Nonnull @Nonempty final String sFilename) throws DAOException
  {
    super (SMLConfiguration.class, sFilename);
  }

  @Override
  @Nonnull
  protected EChange onInit ()
  {
    // Add the default transport profiles
    for (final ESML e : ESML.values ())
      internalCreateItem (SMLConfiguration.create (e));
    return EChange.CHANGED;
  }

  @Nonnull
  public ISMLConfiguration createSMLInfo (@Nonnull @Nonempty final String sSMLInfoID,
                                          @Nonnull @Nonempty final String sDisplayName,
                                          @Nonnull @Nonempty final String sDNSZone,
                                          @Nonnull @Nonempty final String sManagementServiceURL,
                                          final boolean bClientCertificateRequired,
                                          @Nonnull final ESMPAPIType eSMPAPIType,
                                          @Nonnull final ESMPIdentifierType eSMPIdentifierType,
                                          final boolean bProduction)
  {
    final SMLInfo aSMLInfo = new SMLInfo (sSMLInfoID, sDisplayName, sDNSZone, sManagementServiceURL, bClientCertificateRequired);
    final SMLConfiguration aExtSMLInfo = new SMLConfiguration (aSMLInfo, eSMPAPIType, eSMPIdentifierType, bProduction);

    m_aRWLock.writeLocked ( () -> {
      internalCreateItem (aExtSMLInfo);
    });
    AuditHelper.onAuditCreateSuccess (SMLInfo.OT,
                                      sSMLInfoID,
                                      sDisplayName,
                                      sDNSZone,
                                      sManagementServiceURL,
                                      Boolean.valueOf (bClientCertificateRequired),
                                      eSMPAPIType,
                                      eSMPIdentifierType,
                                      Boolean.valueOf (bProduction));
    return aExtSMLInfo;
  }

  @Nonnull
  public EChange updateSMLInfo (@Nullable final String sSMLInfoID,
                                @Nonnull @Nonempty final String sDisplayName,
                                @Nonnull @Nonempty final String sDNSZone,
                                @Nonnull @Nonempty final String sManagementServiceURL,
                                final boolean bClientCertificateRequired,
                                @Nonnull final ESMPAPIType eSMPAPIType,
                                @Nonnull final ESMPIdentifierType eSMPIdentifierType,
                                final boolean bProduction)
  {
    final SMLConfiguration aExtSMLInfo = getOfID (sSMLInfoID);
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
      eChange = eChange.or (aExtSMLInfo.setSMPIdentifierType (eSMPIdentifierType));
      eChange = eChange.or (aExtSMLInfo.setProduction (bProduction));
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
                                      eSMPAPIType,
                                      eSMPIdentifierType,
                                      Boolean.valueOf (bProduction));
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
      final SMLConfiguration aExtSMLInfo = internalDeleteItem (sSMLInfoID);
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
  public ICommonsList <ISMLConfiguration> getAllSorted ()
  {
    return getAll ().getSortedInline ( (c1, c2) -> {
      // Production before test
      final int nProd1 = c1.isProduction () ? 1 : 0;
      final int nProd2 = c2.isProduction () ? 1 : 0;
      int ret = nProd1 - nProd2;
      if (ret == 0)
      {
        // Short before long
        final String d1 = c1.getSMLInfo ().getDNSZone ();
        final String d2 = c2.getSMLInfo ().getDNSZone ();
        ret = d1.length () - d2.length ();
        if (ret == 0)
        {
          // One name before the other
          ret = d1.compareTo (d2);
        }
      }
      return ret;
    });
  }

  @Nullable
  public ISMLConfiguration getSMLInfoOfID (@Nullable final String sID)
  {
    return getOfID (sID);
  }

  public boolean containsSMLInfoWithID (@Nullable final String sID)
  {
    return containsWithID (sID);
  }
}
