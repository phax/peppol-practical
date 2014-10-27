package com.helger.peppol.comment.ui;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.peppol.comment.domain.IComment;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.validation.error.FormErrors;

public final class CommentFormErrors extends FormErrors
{
  private final ICommentThread m_aCommentThread;
  private final IComment m_aParentComment;

  private CommentFormErrors (@Nullable final ICommentThread aCommentThread, @Nullable final IComment aParentComment)
  {
    m_aCommentThread = aCommentThread;
    m_aParentComment = aParentComment;
  }

  public boolean isForNewThread ()
  {
    return m_aCommentThread == null;
  }

  public boolean isReplyTo (@Nullable final ICommentThread aCommentThread, @Nullable final IComment aComment)
  {
    return m_aCommentThread != null &&
           m_aCommentThread.equals (aCommentThread) &&
           m_aParentComment != null &&
           m_aParentComment.equals (aComment);
  }

  @Nullable
  public ICommentThread getCommentThread ()
  {
    return m_aCommentThread;
  }

  @Nullable
  public IComment getParentComment ()
  {
    return m_aParentComment;
  }

  @Nonnull
  public static CommentFormErrors createForNewThread ()
  {
    return new CommentFormErrors (null, null);
  }

  @Nonnull
  public static CommentFormErrors createForReply (@Nonnull final ICommentThread aCommentThread,
                                                  @Nonnull final IComment aParentComment)
  {
    ValueEnforcer.notNull (aCommentThread, "CommentThread");
    ValueEnforcer.notNull (aParentComment, "ParentComment");
    return new CommentFormErrors (aCommentThread, aParentComment);
  }
}
