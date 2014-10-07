package com.helger.peppol.crm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.appbasics.app.dao.impl.AbstractSimpleDAO;
import com.helger.appbasics.app.dao.impl.DAOException;
import com.helger.appbasics.security.audit.AuditUtils;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.microdom.IMicroDocument;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.impl.MicroDocument;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;

/**
 * Manager for {@link CRMGroup} instances.
 *
 * @author Philip Helger
 * @see com.helger.peppol.mgr.MetaManager
 */
@ThreadSafe
public final class CRMGroupManager extends AbstractSimpleDAO
{
  private static final String ELEMENT_ROOT = "crmgroups";
  private static final String ELEMENT_ITEM = "crmgroup";

  private final Map <String, CRMGroup> m_aMap = new HashMap <String, CRMGroup> ();

  public CRMGroupManager (@Nonnull @Nonempty final String sFilename) throws DAOException
  {
    super (sFilename);
    initialRead ();
  }

  @Override
  @Nonnull
  protected EChange onRead (@Nonnull final IMicroDocument aDoc)
  {
    for (final IMicroElement eCRMGroup : aDoc.getDocumentElement ().getAllChildElements (ELEMENT_ITEM))
      _addCRMGroup (MicroTypeConverter.convertToNative (eCRMGroup, CRMGroup.class));
    return EChange.UNCHANGED;
  }

  @Override
  @Nonnull
  protected IMicroDocument createWriteData ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement (ELEMENT_ROOT);
    for (final CRMGroup aCRMGroup : ContainerHelper.getSortedByKey (m_aMap).values ())
      eRoot.appendChild (MicroTypeConverter.convertToMicroElement (aCRMGroup, ELEMENT_ITEM));
    return aDoc;
  }

  private void _addCRMGroup (@Nonnull final CRMGroup aCRMGroup)
  {
    ValueEnforcer.notNull (aCRMGroup, "CRMGroup");

    final String sCRMGroupID = aCRMGroup.getID ();
    if (m_aMap.containsKey (sCRMGroupID))
      throw new IllegalArgumentException ("CRMGroup ID '" + sCRMGroupID + "' is already in use!");
    m_aMap.put (sCRMGroupID, aCRMGroup);
  }

  @Nonnull
  public ICRMGroup createCRMGroup (@Nonnull @Nonempty final String sDisplayName,
                                   @Nonnull @Nonempty final String sSenderEmailAddress)
  {
    final CRMGroup aCRMGroup = new CRMGroup (sDisplayName, sSenderEmailAddress);

    m_aRWLock.writeLock ().lock ();
    try
    {
      _addCRMGroup (aCRMGroup);
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditUtils.onAuditCreateSuccess (CRMGroup.OT_CRM_GROUP, aCRMGroup.getID (), sDisplayName, sSenderEmailAddress);
    return aCRMGroup;
  }

  @Nonnull
  public EChange updateCRMGroup (@Nullable final String sCRMGroupID,
                                 @Nonnull @Nonempty final String sDisplayName,
                                 @Nonnull @Nonempty final String sSenderEmailAddress)
  {
    m_aRWLock.writeLock ().lock ();
    try
    {
      final CRMGroup aCRMGroup = m_aMap.get (sCRMGroupID);
      if (aCRMGroup == null)
      {
        AuditUtils.onAuditModifyFailure (CRMGroup.OT_CRM_GROUP, sCRMGroupID, "no-such-id");
        return EChange.UNCHANGED;
      }

      EChange eChange = EChange.UNCHANGED;
      // ID cannot be changed!
      eChange = eChange.or (aCRMGroup.setDisplayName (sDisplayName));
      eChange = eChange.or (aCRMGroup.setSenderEmailAddress (sSenderEmailAddress));
      if (eChange.isUnchanged ())
        return EChange.UNCHANGED;

      aCRMGroup.setLastModificationNow ();
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditUtils.onAuditModifySuccess (CRMGroup.OT_CRM_GROUP, "all", sCRMGroupID, sDisplayName, sSenderEmailAddress);
    return EChange.CHANGED;
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <? extends ICRMGroup> getAllCRMGroups ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return ContainerHelper.newList (m_aMap.values ());
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public ICRMGroup getCRMGroupOfID (@Nullable final String sID)
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

  public boolean containsCRMGroupWithID (@Nullable final String sID)
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

  public boolean isEmpty ()
  {
    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMap.isEmpty ();
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }
}
