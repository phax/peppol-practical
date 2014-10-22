/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.appbasics.app.dao.impl.DAOException;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsImmutableObject;
import com.helger.commons.annotations.UsedViaReflection;
import com.helger.commons.state.ESuccess;
import com.helger.commons.stats.IStatisticsHandlerCounter;
import com.helger.commons.stats.StatisticsManager;
import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ITypedObject;
import com.helger.commons.type.ObjectType;
import com.helger.scopes.singleton.GlobalSingleton;

/**
 * Main manager class for comments
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class CommentThreadManager extends GlobalSingleton
{
  private static final IStatisticsHandlerCounter s_aStatsCounterThreads = StatisticsManager.getCounterHandler (CommentThreadManager.class.getName () +
                                                                                                               "$threads");
  private static final IStatisticsHandlerCounter s_aStatsCounterComments = StatisticsManager.getCounterHandler (CommentThreadManager.class.getName () +
                                                                                                                "$comments");

  private final ReadWriteLock m_aRWLock = new ReentrantReadWriteLock ();
  @GuardedBy ("m_aRWLock")
  private final Map <ObjectType, CommentThreadObjectTypeManager> m_aMap = new HashMap <ObjectType, CommentThreadObjectTypeManager> ();

  @Deprecated
  @UsedViaReflection
  public CommentThreadManager ()
  {}

  @Nonnull
  public static CommentThreadManager getInstance ()
  {
    return getGlobalSingleton (CommentThreadManager.class);
  }

  public void registerObjectType (@Nonnull final ObjectType aObjectType)
  {
    ValueEnforcer.notNull (aObjectType, "ObjectType");

    m_aRWLock.writeLock ().lock ();
    try
    {
      if (m_aMap.containsKey (aObjectType))
        throw new IllegalArgumentException ("ObjectType already registered: " + aObjectType);

      m_aMap.put (aObjectType, new CommentThreadObjectTypeManager (aObjectType));
    }
    catch (final DAOException ex)
    {
      throw new IllegalStateException ("Failed to init comments of object type " + aObjectType, ex);
    }
    finally
    {
      m_aRWLock.writeLock ().unlock ();
    }
  }

  @Nullable
  public CommentThreadObjectTypeManager getManagerOfType (@Nullable final ObjectType aObjectType)
  {
    if (aObjectType == null)
      return null;

    m_aRWLock.readLock ().lock ();
    try
    {
      return m_aMap.get (aObjectType);
    }
    finally
    {
      m_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public ICommentThread createNewThread (@Nonnull final ITypedObject <String> aOwner, @Nonnull final IComment aComment)
  {
    ValueEnforcer.notNull (aOwner, "Owner");

    // Get/create the object type comment manager
    final CommentThreadObjectTypeManager aMgr = getManagerOfType (aOwner.getTypeID ());
    if (aMgr == null)
      return null;

    // Add to respective manager
    final ICommentThread ret = aMgr.createNewThread (aOwner.getID (), aComment);
    s_aStatsCounterThreads.increment ();
    s_aStatsCounterComments.increment ();
    return ret;
  }

  @Nonnull
  public ESuccess addCommentToThread (@Nonnull final ITypedObject <String> aOwner,
                                      @Nullable final String sCommentThreadID,
                                      @Nullable final String sParentCommentID,
                                      @Nonnull final IComment aComment)
  {
    // Get/create the object type comment manager
    final CommentThreadObjectTypeManager aMgr = getManagerOfType (aOwner.getTypeID ());
    if (aMgr == null)
      return ESuccess.FAILURE;

    // Add to respective manager
    final ESuccess ret = aMgr.addCommentToThread (aOwner.getID (), sCommentThreadID, sParentCommentID, aComment);
    if (ret.isSuccess ())
      s_aStatsCounterComments.increment ();
    return ret;
  }

  /**
   * Get a list of all comment threads for a certain object
   *
   * @param aObject
   *        The object to get the comment threads from
   * @return <code>null</code> if no comment threads are present
   */
  @Nullable
  @ReturnsImmutableObject
  public List <ICommentThread> getCommentThreadsOfObject (@Nullable final ITypedObject <String> aObject)
  {
    if (aObject != null)
    {
      // Resolve appropriate manager
      final CommentThreadObjectTypeManager aMgr = getManagerOfType (aObject.getTypeID ());
      if (aMgr != null)
        return aMgr.getAllCommentThreadsOfObject (aObject.getID ());
    }
    return null;
  }

  @Nullable
  public ICommentThread getCommentThreadOfID (@Nonnull final ITypedObject <?> aObject,
                                              @Nullable final String sCommentThreadID)
  {
    ValueEnforcer.notNull (aObject, "Object");

    return getCommentThreadOfID (aObject.getTypeID (), sCommentThreadID);
  }

  @Nullable
  public ICommentThread getCommentThreadOfID (@Nonnull final ObjectType aObjectType,
                                              @Nullable final String sCommentThreadID)
  {
    ValueEnforcer.notNull (aObjectType, "ObjectType");

    if (StringHelper.hasText (sCommentThreadID))
    {
      final CommentThreadObjectTypeManager aMgr = getManagerOfType (aObjectType);
      if (aMgr != null)
        return aMgr.getCommentThreadOfID (sCommentThreadID);
    }
    return null;
  }
}
