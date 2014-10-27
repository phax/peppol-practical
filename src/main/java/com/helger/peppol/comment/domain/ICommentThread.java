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

import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.state.EChange;
import com.helger.commons.tree.withid.unique.DefaultTreeWithGlobalUniqueID;
import com.helger.commons.type.ITypedObject;

/**
 * Interface for a single comment thread. It holds {@link IComment} objects in a
 * tree structured way.
 *
 * @author Philip Helger
 */
public interface ICommentThread extends ITypedObject <String>
{
  @Nonnull
  DefaultTreeWithGlobalUniqueID <String, IComment> getTree ();

  /**
   * @return The comment that started this thread
   */
  @Nonnull
  IComment getInitialComment ();

  /**
   * Add a comment to this thread.
   *
   * @param aParentComment
   *        The non-<code>null</code> parent comment
   * @param aNewComment
   *        The comment to be added as an answer to the the parent comment
   * @return the added comment
   */
  @Nonnull
  IComment addComment (@Nonnull IComment aParentComment, @Nonnull IComment aNewComment);

  /**
   * Delete a comment FROM this thread.
   *
   * @param sCommentID
   *        The ID of the comment to be removed. May be <code>null</code>.
   * @param eNewState
   *        The new state to be set. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  EChange updateCommentState (@Nullable String sCommentID, @Nonnull ECommentState eNewState);

  /**
   * @return The total comment count. Always &ge; 0.
   */
  @Nonnegative
  int getTotalCommentCount ();

  /**
   * @return A list of all comments in this thread
   */
  @Nonnull
  @ReturnsMutableCopy
  List <IComment> getAllComments ();

  /**
   * Get the comment with the passed ID within this thread
   *
   * @param sCommentID
   *        The ID to search
   * @return <code>null</code> if no such comment exists
   */
  @Nullable
  IComment getCommentOfID (@Nullable String sCommentID);

  /**
   * Iterate the comment tree in this thread
   *
   * @param aCallback
   *        The callback handler to invoke. May not be <code>null</code>.
   */
  void iterateAllComments (@Nonnull ICommentIterationCallback aCallback);
}
