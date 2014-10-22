package com.helger.peppol.comment.domain;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.convert.IMicroTypeConverter;
import com.helger.commons.microdom.impl.MicroElement;
import com.helger.commons.tree.utils.xml.IConverterTreeXML;
import com.helger.commons.tree.utils.xml.MicroTypeConverterTreeXML;
import com.helger.commons.tree.utils.xml.TreeXMLConverter;
import com.helger.commons.tree.withid.unique.DefaultTreeWithGlobalUniqueID;

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

    final IConverterTreeXML <IComment> aXMLConverter = new MicroTypeConverterTreeXML <IComment> (sNamespaceURI,
                                                                                                 ELEMENT_COMMENT,
                                                                                                 Comment.class);
    eCommentThread.appendChild (TreeXMLConverter.getTreeWithStringIDAsXML (aCommentThread.getTree (), aXMLConverter));
    return eCommentThread;
  }

  @Nonnull
  public CommentThread convertToNative (@Nonnull final IMicroElement eCommentThread)
  {
    final IConverterTreeXML <IComment> aXMLConverter = new MicroTypeConverterTreeXML <IComment> (eCommentThread.getNamespaceURI (),
                                                                                                 ELEMENT_COMMENT,
                                                                                                 Comment.class);
    final DefaultTreeWithGlobalUniqueID <String, IComment> aTree = TreeXMLConverter.getXMLAsTreeWithUniqueStringID (eCommentThread.getFirstChildElement (),
                                                                                                                    aXMLConverter);

    // Main add
    return new CommentThread (aTree);
  }
}
