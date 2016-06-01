/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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

import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.MicroElement;
import com.helger.commons.microdom.convert.IMicroTypeConverter;
import com.helger.commons.tree.withid.unique.DefaultTreeWithGlobalUniqueID;
import com.helger.commons.tree.xml.IConverterTreeXML;
import com.helger.commons.tree.xml.MicroTypeConverterTreeXML;
import com.helger.commons.tree.xml.TreeXMLConverter;

@Immutable
public final class CommentThreadMicroTypeConverter implements IMicroTypeConverter
{
  private static final String ELEMENT_COMMENT = "comment";

  @Nonnull
  public IMicroElement convertToMicroElement (@Nonnull final Object aObject,
                                              @Nullable final String sNamespaceURI,
                                              @Nonnull final String sTagName)
  {
    final ICommentThread aCommentThread = (ICommentThread) aObject;

    final IMicroElement eCommentThread = new MicroElement (sNamespaceURI, sTagName);

    final IConverterTreeXML <IComment> aXMLConverter = new MicroTypeConverterTreeXML<> (sNamespaceURI,
                                                                                        ELEMENT_COMMENT,
                                                                                        Comment.class);
    eCommentThread.appendChild (TreeXMLConverter.getTreeWithStringIDAsXML (aCommentThread.getTree (), aXMLConverter));
    return eCommentThread;
  }

  @Nonnull
  public CommentThread convertToNative (@Nonnull final IMicroElement eCommentThread)
  {
    final IConverterTreeXML <IComment> aXMLConverter = new MicroTypeConverterTreeXML<> (eCommentThread.getNamespaceURI (),
                                                                                        ELEMENT_COMMENT,
                                                                                        Comment.class);
    final DefaultTreeWithGlobalUniqueID <String, IComment> aTree = TreeXMLConverter.getXMLAsTreeWithUniqueStringID (eCommentThread.getFirstChildElement (),
                                                                                                                    aXMLConverter);

    // Main add
    return new CommentThread (aTree);
  }
}
