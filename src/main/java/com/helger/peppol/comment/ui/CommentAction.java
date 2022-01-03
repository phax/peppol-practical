/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.peppol.comment.domain.IComment;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.photon.core.form.FormErrorList;

public final class CommentAction extends FormErrorList
{
  private final ECommentAction m_eCommentAction;
  private final ICommentThread m_aCommentThread;
  private final IComment m_aParentComment;

  private CommentAction (@Nonnull final ECommentAction eCommentAction,
                         @Nullable final ICommentThread aCommentThread,
                         @Nullable final IComment aParentComment)
  {
    ValueEnforcer.notNull (eCommentAction, "CommentAction");
    m_eCommentAction = eCommentAction;
    m_aCommentThread = aCommentThread;
    m_aParentComment = aParentComment;
  }

  public boolean isMatching (@Nonnull final ECommentAction eAction)
  {
    return m_eCommentAction.equals (eAction);
  }

  public boolean isMatching (@Nullable final ICommentThread aCommentThread, @Nullable final IComment aComment)
  {
    return m_aCommentThread != null &&
           m_aCommentThread.equals (aCommentThread) &&
           m_aParentComment != null &&
           m_aParentComment.equals (aComment);
  }

  public boolean isMatching (@Nonnull final ECommentAction eAction,
                             @Nullable final ICommentThread aCommentThread,
                             @Nullable final IComment aComment)
  {
    return isMatching (eAction) && isMatching (aCommentThread, aComment);
  }

  @Nonnull
  public ECommentAction getAction ()
  {
    return m_eCommentAction;
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
  public static CommentAction createGeneric (@Nonnull final ECommentAction eCommentAction)
  {
    return new CommentAction (eCommentAction, null, null);
  }

  @Nonnull
  public static CommentAction createForComment (@Nonnull final ECommentAction eCommentAction,
                                                @Nonnull final ICommentThread aCommentThread,
                                                @Nonnull final IComment aParentComment)
  {
    ValueEnforcer.notNull (aCommentThread, "CommentThread");
    ValueEnforcer.notNull (aParentComment, "ParentComment");
    return new CommentAction (eCommentAction, aCommentThread, aParentComment);
  }
}
