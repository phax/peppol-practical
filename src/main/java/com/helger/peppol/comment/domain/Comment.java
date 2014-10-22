package com.helger.peppol.comment.domain;

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
  private final DateTime m_aLastModDT;
  private final String m_sUserID;
  private final String m_sCreatorName;
  private boolean m_bDeleted;
  private final String m_sTitle;
  private final String m_sText;

  /**
   * Create a new comment
   *
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
  public Comment (@Nullable final String sUserID,
                  @Nullable final String sCreatorName,
                  @Nullable final String sTitle,
                  @Nonnull @Nonempty final String sText)
  {
    this (GlobalIDFactory.getNewPersistentStringID (),
          PDTFactory.getCurrentDateTime (),
          (DateTime) null,
          sUserID,
          sCreatorName,
          false,
          sTitle,
          sText);
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
   * @param sUserID
   *        user ID
   * @param sCreatorName
   *        name of the comment creator
   * @param bDeleted
   *        <code>true</code> indicates a deleted comment
   * @param sTitle
   *        comment title
   * @param sText
   *        comment Text
   */
  Comment (@Nonnull @Nonempty final String sID,
           @Nonnull final DateTime aCreationDT,
           @Nullable final DateTime aLastModDT,
           @Nullable final String sUserID,
           @Nullable final String sCreatorName,
           final boolean bDeleted,
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
    setDeleted (bDeleted);
    m_sTitle = sTitle;
    m_sText = sText;
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
    return m_bDeleted;
  }

  @Nonnull
  public EChange setDeleted (final boolean bDeleted)
  {
    if (bDeleted == m_bDeleted)
      return EChange.UNCHANGED;
    m_bDeleted = bDeleted;
    return EChange.CHANGED;
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
  public static IComment createForCurrentUser (@Nullable final String sTitle, @Nonnull @Nonempty final String sText)
  {
    final IUser aCurrentUser = LoggedInUserManager.getInstance ().getCurrentUser ();
    if (aCurrentUser == null)
      throw new IllegalStateException ("No user present!");

    return new Comment (aCurrentUser.getID (), aCurrentUser.getDisplayName (), sTitle, sText);
  }
}
