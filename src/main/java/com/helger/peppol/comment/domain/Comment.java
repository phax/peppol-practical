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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.joda.time.DateTime;

import com.helger.appbasics.security.login.LoggedInUserManager;
import com.helger.appbasics.security.user.IUser;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.idfactory.GlobalIDFactory;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ObjectType;
import com.helger.datetime.PDTFactory;

/**
 * Default implementation of the {@link IComment} interface.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public final class Comment implements IComment
{
  public static final ObjectType TYPE_COMMENT = new ObjectType ("comment");

  private final String m_sID;
  private final DateTime m_aCreationDT;
  private DateTime m_aLastModDT;
  private ECommentState m_eState;
  private int m_nEditCount;

  private final String m_sUserID;
  private final String m_sCreatorName;
  private final String m_sTitle;
  private final String m_sText;

  /**
   * Create a new comment
   * @param eState
   *        The state of the comment. Must be suitable for creation and cannot
   *        be <code>null</code>.
   * @param sUserID
   *        User ID. May be <code>null</code> if creator name is not
   *        <code>null</code>.
   * @param sCreatorName
   *        Name of the comment creator. May be <code>null</code> if userID is
   *        not <code>null</code>.
   * @param sTitle
   *        comment title. May be <code>null</code>.
   * @param sText
   *        Comment text. May neither be <code>null</code> nor empty.
   */
  public Comment (@Nonnull final ECommentState eState,
                  @Nullable final String sUserID,
                  @Nullable final String sCreatorName,
                  @Nullable final String sTitle,
                  @Nonnull @Nonempty final String sText)
  {
    this (GlobalIDFactory.getNewPersistentStringID (),
          PDTFactory.getCurrentDateTime (),
          (DateTime) null,
          eState,
          0,
          sUserID,
          sCreatorName,
          sTitle,
          sText);
    if (!eState.isSuitableForCreation ())
      throw new IllegalArgumentException ("Invalid state used: " + eState);
  }

  /**
   * Deserializing constructor
   *
   * @param sID
   *        ID of the object
   * @param aCreationDT
   *        creation date time
   * @param aLastModDT
   *        last modification date time
   * @param eState
   *        The state of the comment. Must be suitable for creation and cannot
   *        be <code>null</code>.
   * @param nEditCount
   *        How often was the comment edited?
   * @param sUserID
   *        user ID
   * @param sCreatorName
   *        name of the comment creator
   * @param sTitle
   *        comment title
   * @param sText
   *        comment Text
   */
  Comment (@Nonnull @Nonempty final String sID,
           @Nonnull final DateTime aCreationDT,
           @Nullable final DateTime aLastModDT,
           @Nonnull final ECommentState eState,
           @Nonnegative final int nEditCount,
           @Nullable final String sUserID,
           @Nullable final String sCreatorName,
           @Nullable final String sTitle,
           @Nonnull @Nonempty final String sText)
  {
    ValueEnforcer.notEmpty (sID, "ID");
    ValueEnforcer.notNull (aCreationDT, "CreationDT");
    if (StringHelper.hasNoText (sUserID) && StringHelper.hasNoText (sCreatorName))
      throw new IllegalArgumentException ("Either userID or creator name must be present!");
    ValueEnforcer.notEmpty (sText, "Text");
    m_sID = sID;
    m_aCreationDT = aCreationDT;
    m_aLastModDT = aLastModDT;
    m_sUserID = sUserID;
    m_sCreatorName = sCreatorName;
    m_sTitle = sTitle;
    m_sText = sText;
    setState (eState);
    m_nEditCount = nEditCount;
  }

  @Nonnull
  public ObjectType getTypeID ()
  {
    return TYPE_COMMENT;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  public DateTime getCreationDateTime ()
  {
    return m_aCreationDT;
  }

  @Nullable
  public DateTime getLastModificationDateTime ()
  {
    return m_aLastModDT;
  }

  @Nonnull
  public ECommentState getState ()
  {
    return m_eState;
  }

  @Nonnull
  public EChange setState (@Nonnull final ECommentState eState)
  {
    ValueEnforcer.notNull (eState, "State");
    if (eState.equals (m_eState))
      return EChange.UNCHANGED;
    m_eState = eState;
    return EChange.CHANGED;
  }

  @Nonnegative
  public int getEditCount ()
  {
    return m_nEditCount;
  }

  public void onEdit ()
  {
    m_nEditCount++;
    m_aLastModDT = PDTFactory.getCurrentDateTime ();
  }

  @Nullable
  public String getUserID ()
  {
    return m_sUserID;
  }

  @Nullable
  public String getCreatorName ()
  {
    return m_sCreatorName;
  }

  public boolean isDeleted ()
  {
    return m_eState.isDeleted ();
  }

  @Nullable
  public String getTitle ()
  {
    return m_sTitle;
  }

  @Nonnull
  @Nonempty
  public String getText ()
  {
    return m_sText;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (!(o instanceof Comment))
      return false;
    final Comment rhs = (Comment) o;
    return m_sID.equals (rhs.m_sID);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sID).getHashCode ();
  }

  @Nonnull
  public static IComment createForCurrentUser (@Nullable final String sTitle,
                                               @Nonnull @Nonempty final String sText,
                                               @Nonnull final ECommentState eState)
  {
    final IUser aCurrentUser = LoggedInUserManager.getInstance ().getCurrentUser ();
    if (aCurrentUser == null)
      throw new IllegalStateException ("No user present!");

    return new Comment (eState, aCurrentUser.getID (), aCurrentUser.getDisplayName (), sTitle, sText);
  }
}
