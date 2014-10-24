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
  private static final String ATTR_HOST = "host";
  private static final String ATTR_STATE = "state";
  private static final String ATTR_EDITCOUNT = "editcount";
  private static final String ATTR_USERID = "userid";
  private static final String ATTR_CREATORNAME = "creatorname";
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
    eComment.setAttribute (ATTR_HOST, aComment.getHost ());
    eComment.setAttribute (ATTR_STATE, aComment.getState ().getID ());
    eComment.setAttribute (ATTR_EDITCOUNT, aComment.getEditCount ());
    eComment.setAttribute (ATTR_USERID, aComment.getUserID ());
    eComment.setAttribute (ATTR_CREATORNAME, aComment.getCreatorName ());
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

    final String sHost = eComment.getAttribute (ATTR_HOST);

    final String sState = eComment.getAttribute (ATTR_STATE);
    final ECommentState eState = ECommentState.getFromIDOrNull (sState);

    final String sEditCount = eComment.getAttribute (ATTR_EDITCOUNT);
    final int nEditCount = StringParser.parseInt (sEditCount, -1);

    final String sUserID = eComment.getAttribute (ATTR_USERID);

    final String sCreatorName = eComment.getAttribute (ATTR_CREATORNAME);

    final String sTitle = eComment.getAttribute (ATTR_TITLE);

    final String sText = eComment.getTextContentTrimmed ();

    // Create comment
    return new Comment (sCommentID,
                        aCreationDT,
                        aLastModDT,
                        sHost,
                        eState,
                        nEditCount,
                        sUserID,
                        sCreatorName,
                        sTitle,
                        sText);
  }
}
