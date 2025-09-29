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

import java.time.LocalDateTime;

import com.helger.annotation.concurrent.Immutable;
import com.helger.base.string.StringParser;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroElement;
import com.helger.xml.microdom.convert.IMicroTypeConverter;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

@Immutable
public final class CommentMicroTypeConverter implements IMicroTypeConverter <Comment>
{
  private static final String ATTR_ID = "id";
  private static final String ATTR_CREATIONLDT = "creationldt";
  private static final String ATTR_LASTMODLDT = "lastmodldt";
  private static final String ATTR_HOST = "host";
  private static final String ATTR_STATE = "state";
  private static final String ATTR_EDITCOUNT = "editcount";
  private static final String ATTR_SPAMREPORTCOUNT = "spamreportcount";
  private static final String ATTR_USERID = "userid";
  private static final String ATTR_CREATORNAME = "creatorname";
  private static final String ATTR_TITLE = "title";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Comment aValue,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final IMicroElement eComment = new MicroElement (sNamespaceURI, sTagName);
    eComment.setAttribute (ATTR_ID, aValue.getID ());
    eComment.setAttributeWithConversion (ATTR_CREATIONLDT, aValue.getCreationDateTime ());
    eComment.setAttributeWithConversion (ATTR_LASTMODLDT, aValue.getLastModificationDateTime ());
    eComment.setAttribute (ATTR_HOST, aValue.getHost ());
    eComment.setAttribute (ATTR_STATE, aValue.getState ().getID ());
    eComment.setAttribute (ATTR_EDITCOUNT, aValue.getEditCount ());
    eComment.setAttribute (ATTR_SPAMREPORTCOUNT, aValue.getSpamReportCount ());
    eComment.setAttribute (ATTR_USERID, aValue.getUserID ());
    eComment.setAttribute (ATTR_CREATORNAME, aValue.getCreatorName ());
    eComment.setAttribute (ATTR_TITLE, aValue.getTitle ());
    eComment.addText (aValue.getText ());
    return eComment;
  }

  @Nonnull
  public Comment convertToNative (@Nonnull final IMicroElement eComment)
  {
    final String sCommentID = eComment.getAttributeValue (ATTR_ID);
    final LocalDateTime aCreationLDT = eComment.getAttributeValueWithConversion (ATTR_CREATIONLDT, LocalDateTime.class);
    final LocalDateTime aLastModLDT = eComment.getAttributeValueWithConversion (ATTR_LASTMODLDT, LocalDateTime.class);

    final String sHost = eComment.getAttributeValue (ATTR_HOST);

    final String sState = eComment.getAttributeValue (ATTR_STATE);
    final ECommentState eState = ECommentState.getFromIDOrNull (sState);

    final String sEditCount = eComment.getAttributeValue (ATTR_EDITCOUNT);
    final int nEditCount = StringParser.parseInt (sEditCount, -1);

    final String sSpamReportCount = eComment.getAttributeValue (ATTR_SPAMREPORTCOUNT);
    final int nSpamReportCount = StringParser.parseInt (sSpamReportCount, 0);

    final String sUserID = eComment.getAttributeValue (ATTR_USERID);

    final String sCreatorName = eComment.getAttributeValue (ATTR_CREATORNAME);

    final String sTitle = eComment.getAttributeValue (ATTR_TITLE);

    final String sText = eComment.getTextContentTrimmed ();

    // Create comment
    return new Comment (sCommentID,
                        aCreationLDT,
                        aLastModLDT,
                        sHost,
                        eState,
                        nEditCount,
                        nSpamReportCount,
                        sUserID,
                        sCreatorName,
                        sTitle,
                        sText);
  }
}
