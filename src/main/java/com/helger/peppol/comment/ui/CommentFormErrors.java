/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.peppol.comment.ui;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.enforce.ValueEnforcer;
import com.helger.peppol.comment.domain.IComment;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.photon.core.form.FormErrorList;


public final class CommentFormErrors extends FormErrorList
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

  @NonNull
  public static CommentFormErrors createForNewThread ()
  {
    return new CommentFormErrors (null, null);
  }

  @NonNull
  public static CommentFormErrors createForReply (@NonNull final ICommentThread aCommentThread,
                                                  @NonNull final IComment aParentComment)
  {
    ValueEnforcer.notNull (aCommentThread, "CommentThread");
    ValueEnforcer.notNull (aParentComment, "ParentComment");
    return new CommentFormErrors (aCommentThread, aParentComment);
  }
}
