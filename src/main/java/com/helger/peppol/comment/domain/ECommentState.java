/*
 * Copyright (C) 2014-2023 Philip Helger (www.helger.com)
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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.text.display.IHasDisplayText;

/**
 * Represents the different comment states.
 *
 * @author Philip Helger
 */
public enum ECommentState implements IHasID <String>, IHasDisplayText
{
  /** Moderation is enabled, and this comment needs approval. */
  TO_BE_APPROVED ("tobeapproved", ECommentStateText.TO_BE_APPROVED),
  /** This comment is already approved, or moderation is disabled. */
  APPROVED ("approved", ECommentStateText.APPROVED),
  /** This comment was rejected by the moderator. */
  REJECTED ("rejected", ECommentStateText.REJECTED),
  /** This comment was deleted by the user itself. */
  DELETED_BY_USER ("deletedbyuser", ECommentStateText.DELETED_BY_USER),
  /** This comment was deleted by a moderator. */
  DELETED_BY_MODERATOR ("deletedbymoderator", ECommentStateText.DELETED_BY_MODERATOR);

  private final String m_sID;
  private final ECommentStateText m_aDisplayText;

  private ECommentState (@Nonnull @Nonempty final String sID, @Nonnull final ECommentStateText aDisplayText)
  {
    m_sID = sID;
    m_aDisplayText = aDisplayText;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getShortcut ()
  {
    return m_sID;
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return m_aDisplayText.getDisplayText (aContentLocale);
  }

  public boolean isSuitableForCreation ()
  {
    return this == TO_BE_APPROVED || this == APPROVED;
  }

  public boolean isApproved ()
  {
    return this == APPROVED;
  }

  public boolean isDeleted ()
  {
    return this == DELETED_BY_USER || this == DELETED_BY_MODERATOR;
  }

  @Nullable
  public static ECommentState getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (ECommentState.class, sID);
  }

  @Nullable
  public static ECommentState getFromIDOrDefault (@Nullable final String sID, @Nullable final ECommentState eDefault)
  {
    return EnumHelper.getFromIDOrDefault (ECommentState.class, sID, eDefault);
  }
}
