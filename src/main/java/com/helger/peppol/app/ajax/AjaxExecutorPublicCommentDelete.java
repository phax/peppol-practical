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
package com.helger.peppol.app.ajax;

import java.util.Locale;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.bootstrap3.alert.BootstrapErrorBox;
import com.helger.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ITypedObject;
import com.helger.commons.type.ObjectType;
import com.helger.commons.type.TypedObject;
import com.helger.html.hc.IHCNode;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.comment.domain.ECommentState;
import com.helger.peppol.comment.ui.CommentSecurity;
import com.helger.peppol.comment.ui.ECommentText;
import com.helger.webbasics.ajax.executor.AbstractAjaxExecutor;
import com.helger.webbasics.ajax.response.AjaxDefaultResponse;
import com.helger.webbasics.ajax.response.IAjaxResponse;
import com.helger.webbasics.app.layout.LayoutExecutionContext;
import com.helger.webscopes.domain.IRequestWebScopeWithoutResponse;

/**
 * AJAX handler for deleting a single comment
 *
 * @author Philip Helger
 */
public final class AjaxExecutorPublicCommentDelete extends AbstractAjaxExecutor
{
  public static final String PARAM_OBJECT_TYPE = "objectType";
  public static final String PARAM_OBJECT_ID = "objectID";
  public static final String PARAM_COMMENT_THREAD_ID = "commentThreadID";
  public static final String PARAM_COMMENT_ID = "commentID";
  private static final Logger s_aLogger = LoggerFactory.getLogger (AjaxExecutorPublicCommentDelete.class);

  @Override
  @Nonnull
  protected IAjaxResponse mainHandleRequest (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope) throws Exception
  {
    final LayoutExecutionContext aLEC = LayoutExecutionContext.createForAjaxOrAction (aRequestScope);
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final String sObjectType = aRequestScope.getAttributeAsString (PARAM_OBJECT_TYPE);
    final String sObjectID = aRequestScope.getAttributeAsString (PARAM_OBJECT_ID);
    final String sCommentThreadID = aRequestScope.getAttributeAsString (PARAM_COMMENT_THREAD_ID);
    final String sCommentID = aRequestScope.getAttributeAsString (PARAM_COMMENT_ID);

    if (StringHelper.hasText (sObjectType) &&
        StringHelper.hasText (sObjectID) &&
        StringHelper.hasText (sCommentThreadID) &&
        StringHelper.hasText (sCommentID) &&
        CommentSecurity.isCurrentUserCommentModerator ())
    {
      // Create a dummy object
      final ITypedObject <String> aOwner = TypedObject.create (new ObjectType (sObjectType), sObjectID);

      // Go ahead and delete
      final EChange eChange = CommentThreadManager.getInstance ()
                                                  .updateCommentState (aOwner,
                                                                       sCommentThreadID,
                                                                       sCommentID,
                                                                       ECommentState.DELETED_BY_MODERATOR);
      IHCNode aStatusNode;
      if (eChange.isChanged ())
        aStatusNode = new BootstrapSuccessBox ().addChild (ECommentText.MSG_COMMENT_DELETE_SUCCESS.getDisplayText (aDisplayLocale));
      else
        aStatusNode = new BootstrapErrorBox ().addChild (ECommentText.MSG_COMMENT_DELETE_FAILURE.getDisplayText (aDisplayLocale));

      // Message box + list of exiting comments
      return AjaxDefaultResponse.createSuccess (aRequestScope, aStatusNode);
    }

    // Somebody played around with the API
    s_aLogger.warn ("Failed to resolve comment object type '" +
                    sObjectType +
                    "' and/or object ID '" +
                    sObjectID +
                    "' for deletion of comment '" +
                    sCommentID +
                    "' in thread '" +
                    sCommentThreadID +
                    "'");
    return AjaxDefaultResponse.createError ();
  }
}