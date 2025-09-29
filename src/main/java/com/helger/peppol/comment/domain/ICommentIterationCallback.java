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

import com.helger.annotation.Nonnegative;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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
  default void onCommentStart (@Nonnegative final int nLevel,
                               @Nullable final IComment aParentComment,
                               @Nonnull final IComment aComment)
  {}

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
  default void onCommentEnd (@Nonnegative final int nLevel,
                             @Nullable final IComment aParentComment,
                             @Nonnull final IComment aComment)
  {}
}
