package com.helger.peppol.comment.domain;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.joda.time.DateTime;

import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.IMicroTypeConverter;
import com.helger.commons.microdom.impl.MicroElement;
import com.helger.commons.string.StringParser;

@Immutable
public final class CommentMicroTypeConverter implements IMicroTypeConverter
{
  private static final String ATTR_ID = "id";
  private static final String ATTR_CREATIONDT = "creationdt";
  private static final String ATTR_LASTMODDT = "lastmoddt";
  private static final String ATTR_USERID = "userid";
  private static final String ATTR_CREATORNAME = "creatorname";
  private static final String ATTR_DELETED = "deleted";
  private static final String ATTR_TITLE = "title";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IComment aComment = (IComment) aObject;

    final IMicroElement eComment = new MicroElement (sNamespaceURI, sTagName);
    eComment.setAttribute (ATTR_ID, aComment.getID ());
    eComment.setAttributeWithConversion (ATTR_CREATIONDT, aComment.getCreationDateTime ());
    eComment.setAttributeWithConversion (ATTR_LASTMODDT, aComment.getLastModificationDateTime ());
    eComment.setAttribute (ATTR_USERID, aComment.getUserID ());
    eComment.setAttribute (ATTR_CREATORNAME, aComment.getCreatorName ());
    eComment.setAttribute (ATTR_DELETED, Boolean.toString (aComment.isDeleted ()));
    eComment.setAttribute (ATTR_TITLE, aComment.getTitle ());
    eComment.appendText (aComment.getText ());
    return eComment;
  }

  @Nonnull
  public Comment convertToNative (@Nonnull final IMicroElement eComment)
  {
    final String sCommentID = eComment.getAttribute (ATTR_ID);
    final DateTime aCreationDT = eComment.getAttributeWithConversion (ATTR_CREATIONDT, DateTime.class);
    final DateTime aLastModDT = eComment.getAttributeWithConversion (ATTR_LASTMODDT, DateTime.class);

    final String sUserID = eComment.getAttribute (ATTR_USERID);

    final String sCreatorName = eComment.getAttribute (ATTR_CREATORNAME);

    final String sDeleted = eComment.getAttribute (ATTR_DELETED);
    final boolean bDeleted = StringParser.parseBool (sDeleted);

    final String sTitle = eComment.getAttribute (ATTR_TITLE);

    final String sText = eComment.getTextContent ();

    // Main add
    return new Comment (sCommentID, aCreationDT, aLastModDT, sUserID, sCreatorName, bDeleted, sTitle, sText);
  }
}
