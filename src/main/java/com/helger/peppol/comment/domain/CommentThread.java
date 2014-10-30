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

import java.util.Collection;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.annotations.ReturnsMutableObject;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.hierarchy.DefaultHierarchyWalkerCallback;
import com.helger.commons.state.EChange;
import com.helger.commons.tree.utils.walk.TreeWalker;
import com.helger.commons.tree.withid.DefaultTreeItemWithID;
import com.helger.commons.tree.withid.unique.DefaultTreeWithGlobalUniqueID;
import com.helger.commons.type.ObjectType;

/**
 * This class represents a single thread of comments.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class CommentThread implements ICommentThread
{
  public static final ObjectType TYPE_COMMENT_THREAD = new ObjectType ("comment-thread");

  private final String m_sID;
  private final DefaultTreeWithGlobalUniqueID <String, IComment> m_aTree;

  public CommentThread (@Nonnull final IComment aInitialComment)
  {
    ValueEnforcer.notNull (aInitialComment, "InitialComment");

    m_aTree = new DefaultTreeWithGlobalUniqueID <String, IComment> ();
    m_aTree.getRootItem ().createChildItem (aInitialComment.getID (), aInitialComment);
    m_sID = aInitialComment.getID ();
  }

  CommentThread (@Nonnull final DefaultTreeWithGlobalUniqueID <String, IComment> aTree)
  {
    ValueEnforcer.notNull (aTree, "Tree");
    ValueEnforcer.notNull (aTree.getRootItem (), "Tree.RootItem");
    ValueEnforcer.isGT0 (aTree.getRootItem ().getChildCount (), "Tree.RootItem.Children");

    m_sID = aTree.getRootItem ().getChildAtIndex (0).getData ().getID ();
    m_aTree = aTree;
  }

  @Nonnull
  public ObjectType getTypeID ()
  {
    return TYPE_COMMENT_THREAD;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @ReturnsMutableObject (reason = "design")
  public DefaultTreeWithGlobalUniqueID <String, IComment> getTree ()
  {
    return m_aTree;
  }

  @Nonnull
  public IComment getInitialComment ()
  {
    return m_aTree.getRootItem ().getChildAtIndex (0).getData ();
  }

  @Nonnull
  public IComment addComment (@Nonnull final IComment aParentComment, @Nonnull final IComment aNewComment)
  {
    ValueEnforcer.notNull (aParentComment, "ParentComment");
    ValueEnforcer.notNull (aNewComment, "NewComment");

    // Resolve parent item
    final DefaultTreeItemWithID <String, IComment> aTreeItem = m_aTree.getItemWithID (aParentComment.getID ());
    if (aTreeItem == null)
      throw new IllegalArgumentException ("Failed to resolve parent comment '" + aParentComment.getID () + "'");

    aTreeItem.createChildItem (aNewComment.getID (), aNewComment);
    return aNewComment;
  }

  @Nonnull
  public EChange updateCommentState (@Nullable final String sCommentID, @Nonnull final ECommentState eNewState)
  {
    final IComment aComment = m_aTree.getItemDataWithID (sCommentID);
    if (aComment == null)
      return EChange.UNCHANGED;

    return aComment.setState (eNewState);
  }

  @Nonnegative
  public int getTotalCommentCount ()
  {
    return m_aTree.getItemCount ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <IComment> getAllComments ()
  {
    return m_aTree.getAllItemDatas ();
  }

  @Nullable
  public IComment getCommentOfID (@Nullable final String sCommentID)
  {
    final DefaultTreeItemWithID <String, IComment> aTreeItem = m_aTree.getItemWithID (sCommentID);
    return aTreeItem == null ? null : aTreeItem.getData ();
  }

  public void iterateAllComments (@Nonnull final ICommentIterationCallback aCallback)
  {
    TreeWalker.walkSubTree (m_aTree.getRootItem (),
                            new DefaultHierarchyWalkerCallback <DefaultTreeItemWithID <String, IComment>> ()
                            {
                              @Override
                              public void onItemBeforeChildren (@Nonnull final DefaultTreeItemWithID <String, IComment> aItem)
                              {
                                aCallback.onCommentStart (getLevel (), aItem.getParent ().getData (), aItem.getData ());
                              }

                              @Override
                              public void onItemAfterChildren (@Nonnull final DefaultTreeItemWithID <String, IComment> aItem)
                              {
                                aCallback.onCommentEnd (getLevel (), aItem.getParent ().getData (), aItem.getData ());
                              }
                            });
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof CommentThread))
      return false;
    final CommentThread rhs = (CommentThread) o;
    return m_sID.equals (rhs.m_sID) && m_aTree.equals (rhs.m_aTree);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sID).append (m_aTree).getHashCode ();
  }
}
