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
package com.helger.peppol.comment.domain;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ELockType;
import com.helger.commons.annotation.MustBeLocked;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.collection.impl.ICommonsSet;
import com.helger.commons.state.EChange;
import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ObjectType;
import com.helger.dao.DAOException;
import com.helger.photon.audit.AuditHelper;
import com.helger.photon.core.interror.InternalErrorBuilder;
import com.helger.photon.io.dao.AbstractPhotonSimpleDAO;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.convert.MicroTypeConverter;

/**
 * This class manages all comments of a certain object type.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class CommentThreadObjectTypeManager extends AbstractPhotonSimpleDAO
{
  private static final String ELEMENT_ROOT = "objects";
  private static final String ATTR_OBJECT_TYPE = "objecttype";
  private static final String ELEMENT_ITEM = "object";
  private static final String ATTR_ID = "id";
  private static final String ELEMENT_COMMENTTHREAD = "commentthread";

  private final ObjectType m_aObjectType;

  // multi map from owning object to list <ICommentThread>
  private final ICommonsMap <String, ICommonsList <ICommentThread>> m_aObjectToCommentThreads = new CommonsHashMap <> ();

  // Status map from comment thread ID to comment thread
  private final ICommonsMap <String, ICommentThread> m_aAllCommentThreads = new CommonsHashMap <> ();

  public CommentThreadObjectTypeManager (@Nonnull final ObjectType aObjectType) throws DAOException
  {
    super ("comments/" + aObjectType.getName () + ".xml");
    m_aObjectType = aObjectType;
    initialRead ();
  }

  @Override
  @Nonnull
  protected EChange onRead (@Nonnull final IMicroDocument aDoc)
  {
    final IMicroElement eRoot = aDoc.getDocumentElement ();
    for (final IMicroElement eItem : eRoot.getAllChildElements (ELEMENT_ITEM))
    {
      final String sOwningObjectID = eItem.getAttributeValue (ATTR_ID);
      for (final IMicroElement eCommentThread : eItem.getAllChildElements (ELEMENT_COMMENTTHREAD))
      {
        final ICommentThread aCommentThread = MicroTypeConverter.convertToNative (eCommentThread, CommentThread.class);
        _addCommentThread (sOwningObjectID, aCommentThread);
      }
    }
    return EChange.UNCHANGED;
  }

  @Override
  protected IMicroDocument createWriteData ()
  {
    final IMicroDocument aDoc = new MicroDocument ();
    final IMicroElement eRoot = aDoc.appendElement (ELEMENT_ROOT);
    eRoot.setAttribute (ATTR_OBJECT_TYPE, m_aObjectType.getName ());
    for (final Map.Entry <String, ICommonsList <ICommentThread>> aEntry : m_aObjectToCommentThreads.entrySet ())
    {
      final IMicroElement eItem = eRoot.appendElement (ELEMENT_ITEM);
      eItem.setAttribute (ATTR_ID, aEntry.getKey ());
      for (final ICommentThread aCommentThread : aEntry.getValue ())
        eItem.appendChild (MicroTypeConverter.convertToMicroElement (aCommentThread, ELEMENT_COMMENTTHREAD));
    }
    return aDoc;
  }

  @Nonnull
  public ObjectType getObjectType ()
  {
    return m_aObjectType;
  }

  @MustBeLocked (ELockType.WRITE)
  private void _addCommentThread (@Nonnull final String sOwningObjectID, @Nonnull final ICommentThread aCommentThread)
  {
    // Check before adding to the maps!
    final String sCommentThreadID = aCommentThread.getID ();
    if (m_aAllCommentThreads.containsKey (sCommentThreadID))
      throw new IllegalArgumentException ("Comment thread with ID '" + sCommentThreadID + "' already contained!");

    // Add to object-to-comment map
    if (m_aObjectToCommentThreads.computeIfAbsent (sOwningObjectID, k -> new CommonsArrayList <> ())
                                 .addObject (aCommentThread)
                                 .isUnchanged ())
      throw new IllegalStateException ("Failed to add comment " +
                                       aCommentThread +
                                       " to owner '" +
                                       sOwningObjectID +
                                       "'");

    // Add to all-commentthreads map
    m_aAllCommentThreads.put (sCommentThreadID, aCommentThread);
  }

  @Nonnull
  public ICommentThread createNewThread (@Nonnull final String sOwningObjectID, @Nonnull final IComment aComment)
  {
    ValueEnforcer.notNull (sOwningObjectID, "OwningObjectID");
    ValueEnforcer.notNull (aComment, "Comment");

    final ICommentThread aCommentThread = new CommentThread (aComment);
    m_aRWLock.writeLock ().lock ();
    try
    {
      _addCommentThread (sOwningObjectID, aCommentThread);
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
    AuditHelper.onAuditCreateSuccess (CommentThread.TYPE_COMMENT_THREAD, sOwningObjectID, aCommentThread.getID ());

    // Inform me if something happens
    new InternalErrorBuilder ().addErrorMessage ("CommentThread created")
                               .addCustomData ("Thread ID", aCommentThread.getID ())
                               .setAddClassPath (false)
                               .setInvokeCustomExceptionHandler (false)
                               .setDuplicateEliminiationCounter (0)
                               .handle ();
    return aCommentThread;
  }

  @Nonnull
  public ESuccess addCommentToThread (@Nonnull final String sOwningObjectID,
                                      @Nullable final String sCommentThreadID,
                                      @Nullable final String sParentCommentID,
                                      @Nonnull final IComment aNewComment)
  {
    ValueEnforcer.notNull (sOwningObjectID, "OwningObjectID");
    ValueEnforcer.notNull (aNewComment, "NewComment");

    final ICommentThread aCommentThread = getCommentThreadOfID (sCommentThreadID);
    if (aCommentThread == null)
      return ESuccess.FAILURE;

    m_aRWLock.writeLock ().lock ();
    try
    {
      final IComment aParentComment = aCommentThread.getCommentOfID (sParentCommentID);
      if (aParentComment == null)
        return ESuccess.FAILURE;

      aCommentThread.addComment (aParentComment, aNewComment);
      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    AuditHelper.onAuditCreateSuccess (Comment.TYPE_COMMENT,
                                      sOwningObjectID,
                                      sCommentThreadID,
                                      sParentCommentID,
                                      aNewComment.getID ());
    // Inform me if something happens
    new InternalErrorBuilder ().addErrorMessage ("Comment created")
                               .addCustomData ("Thread ID", aCommentThread.getID ())
                               .addCustomData ("Comment ID", aNewComment.getID ())
                               .setAddClassPath (false)
                               .setInvokeCustomExceptionHandler (false)
                               .setDuplicateEliminiationCounter (0)
                               .handle ();
    return ESuccess.SUCCESS;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsSet <String> getAllOwningObjectIDs ()
  {
    return m_aRWLock.readLockedGet (m_aObjectToCommentThreads::copyOfKeySet);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsMap <String, ICommonsList <ICommentThread>> getAllCommentThreads ()
  {
    return m_aRWLock.readLockedGet ( () -> m_aObjectToCommentThreads.getClone ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <ICommentThread> getAllCommentThreadsOfObject (@Nullable final String sOwningObjectID)
  {
    if (StringHelper.hasText (sOwningObjectID))
    {
      m_aRWLock.readLock ().lock ();
      try
      {
        final ICommonsList <ICommentThread> ret = m_aObjectToCommentThreads.get (sOwningObjectID);
        if (ret != null)
          return ret.getClone ();
      }
      finally
      {
        m_aRWLock.readLock ().unlock ();
      }
    }

    return new CommonsArrayList <> ();
  }

  @Nullable
  public ICommentThread getCommentThreadOfID (@Nullable final String sCommentThreadID)
  {
    if (StringHelper.hasNoText (sCommentThreadID))
      return null;

    return m_aRWLock.readLockedGet ( () -> m_aAllCommentThreads.get (sCommentThreadID));
  }

  @Nullable
  public IComment getCommentOfID (@Nullable final String sCommentID)
  {
    if (StringHelper.hasNoText (sCommentID))
      return null;

    m_aRWLock.readLock ().lock ();
    try
    {
      for (final ICommentThread aCommentThread : m_aAllCommentThreads.values ())
      {
        final IComment aComment = aCommentThread.getCommentOfID (sCommentID);
        if (aComment != null)
          return aComment;
      }
      return null;
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  public EChange updateCommentState (@Nullable final String sOwningObjectID,
                                     @Nullable final String sCommentThreadID,
                                     @Nullable final String sCommentID,
                                     @Nonnull final ECommentState eNewState)
  {
    if (StringHelper.hasNoText (sOwningObjectID))
      return EChange.UNCHANGED;
    if (StringHelper.hasNoText (sCommentThreadID))
      return EChange.UNCHANGED;
    if (StringHelper.hasNoText (sCommentID))
      return EChange.UNCHANGED;

    m_aRWLock.writeLock ().lock ();
    try
    {
      final ICommentThread aCommentThread = m_aAllCommentThreads.get (sCommentThreadID);
      if (aCommentThread == null)
      {
        AuditHelper.onAuditModifyFailure (Comment.TYPE_COMMENT,
                                          "state",
                                          "no-such-id",
                                          sOwningObjectID,
                                          sCommentThreadID,
                                          sCommentID);
        return EChange.UNCHANGED;
      }

      final ICommonsList <ICommentThread> ret = m_aObjectToCommentThreads.get (sOwningObjectID);
      if (ret == null || !ret.contains (aCommentThread))
      {
        AuditHelper.onAuditModifyFailure (Comment.TYPE_COMMENT,
                                          "state",
                                          "invalid-owner",
                                          sOwningObjectID,
                                          sCommentThreadID,
                                          sCommentID);
        return EChange.UNCHANGED;
      }

      if (aCommentThread.updateCommentState (sCommentID, eNewState).isUnchanged ())
        return EChange.UNCHANGED;

      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    AuditHelper.onAuditModifySuccess (Comment.TYPE_COMMENT, "state", sOwningObjectID, sCommentThreadID, sCommentID);
    return EChange.CHANGED;
  }

  @Nonnull
  public EChange removeAllCommentThreadsOfObject (@Nullable final String sOwningObjectID)
  {
    if (StringHelper.hasNoText (sOwningObjectID))
      return EChange.UNCHANGED;

    ICommonsList <ICommentThread> aRemovedCommentThreads;
    m_aRWLock.writeLock ().lock ();
    try
    {
      aRemovedCommentThreads = m_aObjectToCommentThreads.remove (sOwningObjectID);
      if (aRemovedCommentThreads == null)
      {
        AuditHelper.onAuditDeleteFailure (CommentThread.TYPE_COMMENT_THREAD, "no-such-id", sOwningObjectID);
        return EChange.UNCHANGED;
      }

      for (final ICommentThread aCommentThread : aRemovedCommentThreads)
        if (m_aAllCommentThreads.remove (aCommentThread.getID ()) != aCommentThread)
          throw new IllegalStateException ("Internal inconsistency removeAllCommentThreadsOfObject of '" +
                                           sOwningObjectID +
                                           "'");

      markAsChanged ();
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }

    for (final ICommentThread aCommentThread : aRemovedCommentThreads)
      AuditHelper.onAuditDeleteSuccess (CommentThread.TYPE_COMMENT_THREAD, sOwningObjectID, aCommentThread.getID ());
    return EChange.CHANGED;
  }
}
