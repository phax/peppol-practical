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

import java.io.Serializable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.type.ITypedObject;
import com.helger.datetime.IHasCreationDateTime;
import com.helger.datetime.IHasLastModificationDateTime;

/**
 * Interface for a single comment object. It is not directly linked to the
 * object to which the comment is attached. Each comment belongs to an
 * {@link ICommentThread}.
 *
 * @author Philip Helger
 */
public interface IComment extends ITypedObject <String>, IHasCreationDateTime, IHasLastModificationDateTime, Serializable
{
  /**
   * @return The user who created the comment. May be <code>null</code> for
   *         public comments. One of userID or creator name must be present.
   */
  @Nullable
  String getUserID ();

  /**
   * @return The name of the person who created the comment. May be
   *         <code>null</code> for restricted comments. One of userID or creator
   *         name must be present.
   */
  @Nullable
  String getCreatorName ();

  /**
   * @return <code>true</code> if this comment was deleted
   */
  boolean isDeleted ();

  /**
   * @return The comment title. May be <code>null</code>.
   */
  @Nullable
  String getTitle ();

  /**
   * @return The main comment text. May not be <code>null</code>.
   */
  @Nonnull
  String getText ();

  /**
   * @return The state of this comment.
   */
  @Nonnull
  ECommentState getState ();

  /**
   * @return How often was this commented edited. This is 0 if the comment was
   *         just created and never edited.
   */
  @Nonnegative
  int getEditCount ();
}
