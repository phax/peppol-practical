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

import java.time.LocalDateTime;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.id.factory.GlobalIDFactory;
import com.helger.base.state.EChange;
import com.helger.base.string.StringHelper;
import com.helger.base.type.ObjectType;
import com.helger.datetime.helper.PDTFactory;
import com.helger.photon.security.login.LoggedInUserManager;
import com.helger.photon.security.user.IUser;


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
  private final LocalDateTime m_aCreationDT;
  private LocalDateTime m_aLastModDT;
  private ECommentState m_eState;
  private int m_nEditCount;
  private int m_nSpamReportCount;
  private final String m_sHost;

  private final String m_sUserID;
  private final String m_sCreatorName;
  private final String m_sTitle;
  private final String m_sText;

  /**
   * Create a new comment
   *
   * @param sHost
   *        The IP address or host from which the comment was triggered.
   * @param eState
   *        The state of the comment. Must be suitable for creation and cannot be <code>null</code>.
   * @param sUserID
   *        User ID. May be <code>null</code> if creator name is not <code>null</code>.
   * @param sCreatorName
   *        Name of the comment creator. May be <code>null</code> if userID is not
   *        <code>null</code>.
   * @param sTitle
   *        comment title. May be <code>null</code>.
   * @param sText
   *        Comment text. May neither be <code>null</code> nor empty.
   */
  public Comment (@NonNull @Nonempty final String sHost,
                  @NonNull final ECommentState eState,
                  @Nullable final String sUserID,
                  @Nullable final String sCreatorName,
                  @Nullable final String sTitle,
                  @NonNull @Nonempty final String sText)
  {
    this (GlobalIDFactory.getNewPersistentStringID (),
          PDTFactory.getCurrentLocalDateTime (),
          (LocalDateTime) null,
          sHost,
          eState,
          0,
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
   * @param sHost
   *        The IP address or host from which the comment was triggered.
   * @param eState
   *        The state of the comment. Must be suitable for creation and cannot be <code>null</code>.
   * @param nEditCount
   *        How often was the comment edited?
   * @param nSpamReportCount
   *        How often was the comment reported as spam.
   * @param sUserID
   *        user ID
   * @param sCreatorName
   *        name of the comment creator
   * @param sTitle
   *        comment title
   * @param sText
   *        comment Text
   */
  Comment (@NonNull @Nonempty final String sID,
           @NonNull final LocalDateTime aCreationDT,
           @Nullable final LocalDateTime aLastModDT,
           @NonNull @Nonempty final String sHost,
           @NonNull final ECommentState eState,
           @Nonnegative final int nEditCount,
           @Nonnegative final int nSpamReportCount,
           @Nullable final String sUserID,
           @Nullable final String sCreatorName,
           @Nullable final String sTitle,
           @NonNull @Nonempty final String sText)
  {
    ValueEnforcer.notEmpty (sID, "ID");
    ValueEnforcer.notNull (aCreationDT, "CreationDT");
    ValueEnforcer.notEmpty (sHost, "Host");
    ValueEnforcer.notNull (eState, "State");
    if (StringHelper.isEmpty (sUserID) && StringHelper.isEmpty (sCreatorName))
      throw new IllegalArgumentException ("Either userID or creator name must be present!");
    ValueEnforcer.notEmpty (sText, "Text");
    m_sID = sID;
    m_aCreationDT = aCreationDT;
    m_aLastModDT = aLastModDT;
    m_sHost = sHost;
    m_eState = eState;
    m_nEditCount = nEditCount;
    m_nSpamReportCount = nSpamReportCount;
    m_sUserID = sUserID;
    m_sCreatorName = sCreatorName;
    m_sTitle = sTitle;
    m_sText = sText;
  }

  @NonNull
  public ObjectType getObjectType ()
  {
    return TYPE_COMMENT;
  }

  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @NonNull
  public LocalDateTime getCreationDateTime ()
  {
    return m_aCreationDT;
  }

  @Nullable
  public LocalDateTime getLastModificationDateTime ()
  {
    return m_aLastModDT;
  }

  @NonNull
  public LocalDateTime getLastChangeDateTime ()
  {
    if (m_aLastModDT != null)
      return m_aLastModDT;
    return m_aCreationDT;
  }

  @NonNull
  @Nonempty
  public String getHost ()
  {
    return m_sHost;
  }

  @NonNull
  public ECommentState getState ()
  {
    return m_eState;
  }

  @NonNull
  public EChange setState (@NonNull final ECommentState eState)
  {
    ValueEnforcer.notNull (eState, "State");
    if (eState.equals (m_eState))
      return EChange.UNCHANGED;
    m_eState = eState;
    m_aLastModDT = PDTFactory.getCurrentLocalDateTime ();
    return EChange.CHANGED;
  }

  public boolean isDeleted ()
  {
    return m_eState.isDeleted ();
  }

  @Nonnegative
  public int getEditCount ()
  {
    return m_nEditCount;
  }

  public void onCommentEdited ()
  {
    m_nEditCount++;
    m_aLastModDT = PDTFactory.getCurrentLocalDateTime ();
  }

  @Nonnegative
  public int getSpamReportCount ()
  {
    return m_nSpamReportCount;
  }

  public void onCommentSpamReport ()
  {
    m_nSpamReportCount++;
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

  @Nullable
  public String getTitle ()
  {
    return m_sTitle;
  }

  @NonNull
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
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final Comment rhs = (Comment) o;
    return m_sID.equals (rhs.m_sID);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_sID).getHashCode ();
  }

  @NonNull
  public static IComment createForCurrentUser (@NonNull @Nonempty final String sHost,
                                               @NonNull final ECommentState eState,
                                               @Nullable final String sTitle,
                                               @NonNull @Nonempty final String sText)
  {
    final IUser aCurrentUser = LoggedInUserManager.getInstance ().getCurrentUser ();
    if (aCurrentUser == null)
      throw new IllegalStateException ("No user present!");

    return new Comment (sHost, eState, aCurrentUser.getID (), aCurrentUser.getDisplayName (), sTitle, sText);
  }
}
