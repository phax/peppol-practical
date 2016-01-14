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
package com.helger.peppol.comment.ajax;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ITypedObject;
import com.helger.commons.type.ObjectType;
import com.helger.commons.type.TypedObject;
import com.helger.html.hc.IHCNode;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.comment.domain.IComment;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.peppol.comment.ui.CommentSecurity;
import com.helger.peppol.comment.ui.CommentUI;
import com.helger.photon.core.ajax.executor.AbstractAjaxExecutor;
import com.helger.photon.core.ajax.response.AjaxHtmlResponse;
import com.helger.photon.core.app.context.LayoutExecutionContext;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

/**
 * AJAX handler for deleting a single comment
 *
 * @author Philip Helger
 */
public final class AjaxExecutorCommentShowInput extends AbstractAjaxExecutor
{
  public static final String PARAM_OBJECT_TYPE = "objectType";
  public static final String PARAM_OBJECT_ID = "objectID";
  public static final String PARAM_COMMENT_THREAD_ID = "commentThreadID";
  public static final String PARAM_COMMENT_ID = "commentID";
  public static final String PARAM_RESULT_DIV_ID = "resultDivID";
  private static final Logger s_aLogger = LoggerFactory.getLogger (AjaxExecutorCommentShowInput.class);

  @Override
  @Nonnull
  protected AjaxHtmlResponse mainHandleRequest (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope) throws Exception
  {
    final LayoutExecutionContext aLEC = LayoutExecutionContext.createForAjaxOrAction (aRequestScope);
    final String sObjectType = aRequestScope.getAttributeAsString (PARAM_OBJECT_TYPE);
    final String sObjectID = aRequestScope.getAttributeAsString (PARAM_OBJECT_ID);
    final String sCommentThreadID = aRequestScope.getAttributeAsString (PARAM_COMMENT_THREAD_ID);
    final String sCommentID = aRequestScope.getAttributeAsString (PARAM_COMMENT_ID);
    final String sResultDivID = aRequestScope.getAttributeAsString (PARAM_RESULT_DIV_ID);

    if (StringHelper.hasText (sObjectType) &&
        StringHelper.hasText (sObjectID) &&
        StringHelper.hasText (sCommentThreadID) &&
        StringHelper.hasText (sCommentID) &&
        CommentSecurity.canCurrentUserPostComments ())
    {
      // Create a dummy object
      final ITypedObject <String> aOwner = TypedObject.create (new ObjectType (sObjectType), sObjectID);

      final ICommentThread aCommentThread = CommentThreadManager.getInstance ().getCommentThreadOfID (aOwner,
                                                                                                      sCommentThreadID);
      if (aCommentThread != null)
      {
        final IComment aParentComment = aCommentThread.getCommentOfID (sCommentID);
        if (aParentComment != null)
        {
          // response
          final IHCNode aNode = CommentUI.getCreateComment (aLEC,
                                                            sResultDivID,
                                                            aOwner,
                                                            aCommentThread,
                                                            aParentComment,
                                                            null,
                                                            null);
          return AjaxHtmlResponse.createSuccess (aRequestScope, aNode);
        }
      }
    }

    // Somebody played around with the API
    s_aLogger.warn ("Failed to resolve comment object type '" +
                    sObjectType +
                    "' and/or object ID '" +
                    sObjectID +
                    "' for showing input of comment '" +
                    sCommentID +
                    "' in thread '" +
                    sCommentThreadID +
                    "'");
    return AjaxHtmlResponse.createError ("Missing required parameter");
  }
}
