package com.helger.peppol.comment.domain;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Callback interface for iterating all comments within a comment thread
 *
 * @author Philip Helger
 */
public interface ICommentIterationCallback
{
  /**
   * Called for each comment within a comment thread
   *
   * @param nLevel
   *        Current nesting level
   * @param aParentComment
   *        The parent comment - is <code>null</code> for the top-level entry
   * @param aComment
   *        The current comment
   */
  void onComment (@Nonnegative int nLevel, @Nullable IComment aParentComment, @Nonnull IComment aComment);
}
