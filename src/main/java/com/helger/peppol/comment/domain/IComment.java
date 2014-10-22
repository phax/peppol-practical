package com.helger.peppol.comment.domain;

import java.io.Serializable;

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
}
