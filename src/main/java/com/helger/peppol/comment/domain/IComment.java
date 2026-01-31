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
package com.helger.peppol.comment.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.style.MustImplementEqualsAndHashcode;
import com.helger.base.state.EChange;
import com.helger.base.type.ITypedObject;
import com.helger.datetime.domain.IHasCreationDateTime;
import com.helger.datetime.domain.IHasLastModificationDateTime;
import com.helger.security.authentication.subject.user.IHasUserID;


/**
 * Interface for a single comment object. It is not directly linked to the object to which the
 * comment is attached. Each comment belongs to an {@link ICommentThread}.
 *
 * @author Philip Helger
 */
@MustImplementEqualsAndHashcode
public interface IComment extends
                          ITypedObject <String>,
                          IHasCreationDateTime,
                          IHasLastModificationDateTime,
                          Serializable,
                          IHasUserID
{
  // status vars

  /**
   * @return The IP address or host from which the comment was triggered. This is used to identify
   *         spammers and block IP addresses.
   */
  @NonNull
  @Nonempty
  String getHost ();

  /**
   * @return The state of this comment.
   */
  @NonNull
  ECommentState getState ();

  /**
   * Change the state of the comment.
   *
   * @param eState
   *        The new state. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @NonNull
  EChange setState (@NonNull ECommentState eState);

  /**
   * @return <code>true</code> if this comment was deleted
   */
  boolean isDeleted ();

  /**
   * @return How often was this commented edited. This is 0 if the comment was just created and
   *         never edited.
   */
  @Nonnegative
  int getEditCount ();

  /**
   * Increment the edit count and set the last modification date time to now.
   */
  void onCommentEdited ();

  /**
   * @return How often was the entry reported as spam. Always &ge; 0.
   */
  @Nonnegative
  int getSpamReportCount ();

  /**
   * Increment the spam report count.
   */
  void onCommentSpamReport ();

  // content vars

  /**
   * @return The last modification date time or if not present the creation date time.
   */
  @NonNull
  LocalDateTime getLastChangeDateTime ();

  /**
   * @return The user who created the comment. May be <code>null</code> for public comments. One of
   *         userID or creator name must be present.
   */
  @Nullable
  String getUserID ();

  /**
   * @return The name of the person who created the comment. May be <code>null</code> for restricted
   *         comments. One of userID or creator name must be present.
   */
  @Nullable
  String getCreatorName ();

  /**
   * @return The comment title. May be <code>null</code>.
   */
  @Nullable
  String getTitle ();

  /**
   * @return The main comment text. May not be <code>null</code>.
   */
  @NonNull
  String getText ();
}
