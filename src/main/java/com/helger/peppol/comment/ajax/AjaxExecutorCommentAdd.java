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

import java.util.Locale;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.state.ESuccess;
import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ITypedObject;
import com.helger.commons.type.ObjectType;
import com.helger.commons.type.TypedObject;
import com.helger.html.hc.IHCNode;
import com.helger.peppol.comment.domain.Comment;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.comment.domain.ECommentState;
import com.helger.peppol.comment.domain.IComment;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.peppol.comment.ui.CommentAction;
import com.helger.peppol.comment.ui.CommentFormErrors;
import com.helger.peppol.comment.ui.CommentSecurity;
import com.helger.peppol.comment.ui.CommentUI;
import com.helger.peppol.comment.ui.ECommentAction;
import com.helger.peppol.comment.ui.ECommentText;
import com.helger.photon.bootstrap3.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.photon.core.ajax.executor.AbstractAjaxExecutor;
import com.helger.photon.core.ajax.response.AjaxHtmlResponse;
import com.helger.photon.core.app.context.LayoutExecutionContext;
import com.helger.photon.security.login.LoggedInUserManager;
import com.helger.photon.security.user.IUser;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

/**
 * AJAX handler for creating a new comment thread.
 *
 * @author Philip Helger
 */
public final class AjaxExecutorCommentAdd extends AbstractAjaxExecutor
{
  public static final String PARAM_OBJECT_TYPE = "objectType";
  public static final String PARAM_OBJECT_ID = "objectID";
  public static final String PARAM_COMMENT_THREAD_ID = "commentThreadID";
  public static final String PARAM_COMMENT_ID = "commentID";
  public static final String PARAM_AUTHOR = "author";
  public static final String PARAM_TITLE = "title";
  public static final String PARAM_TEXT = "text";
  private static final Logger s_aLogger = LoggerFactory.getLogger (AjaxExecutorCommentAdd.class);

  @Override
  @Nonnull
  protected AjaxHtmlResponse mainHandleRequest (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope) throws Exception
  {
    final LayoutExecutionContext aLEC = LayoutExecutionContext.createForAjaxOrAction (aRequestScope);
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final String sObjectType = aRequestScope.getAttributeAsString (PARAM_OBJECT_TYPE);
    final String sObjectID = aRequestScope.getAttributeAsString (PARAM_OBJECT_ID);
    final String sCommentThreadID = aRequestScope.getAttributeAsString (PARAM_COMMENT_THREAD_ID);
    final String sCommentID = aRequestScope.getAttributeAsString (PARAM_COMMENT_ID);
    String sAuthor = aRequestScope.getAttributeAsString (PARAM_AUTHOR);
    final String sTitle = aRequestScope.getAttributeAsString (PARAM_TITLE);
    final String sText = aRequestScope.getAttributeAsString (PARAM_TEXT);

    // Get info on current user
    final IUser aCurrentUser = LoggedInUserManager.getInstance ().getCurrentUser ();
    final String sCurrentUserID = aCurrentUser != null ? aCurrentUser.getID () : null;
    if (aCurrentUser != null)
      sAuthor = aCurrentUser.getDisplayName ();

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
          final CommentFormErrors aFormErrors = CommentFormErrors.createForReply (aCommentThread, aParentComment);
          if (StringHelper.hasNoText (sAuthor))
          {
            // No author provided
            aFormErrors.addFieldError (PARAM_AUTHOR,
                                       ECommentText.MSG_ERR_COMMENT_NO_AUTHOR.getDisplayText (aDisplayLocale));
          }
          if (StringHelper.hasNoText (sText))
          {
            // No text provided
            aFormErrors.addFieldError (PARAM_TEXT,
                                       ECommentText.MSG_ERR_COMMENT_NO_TEXT.getDisplayText (aDisplayLocale));
          }

          IHCNode aMessageBox = null;
          if (aFormErrors.isEmpty ())
          {
            // Go ahead and save
            final ESuccess eSuccess = CommentThreadManager.getInstance ()
                                                          .addCommentToThread (aOwner,
                                                                               sCommentThreadID,
                                                                               sCommentID,
                                                                               new Comment (aRequestScope.getRemoteHost (),
                                                                                            ECommentState.APPROVED,
                                                                                            sCurrentUserID,
                                                                                            sAuthor,
                                                                                            sTitle,
                                                                                            sText));
            if (eSuccess.isSuccess ())
              aMessageBox = new BootstrapSuccessBox ().addChild (ECommentText.MSG_COMMENT_SAVE_SUCCESS.getDisplayText (aDisplayLocale));
            else
              aMessageBox = new BootstrapErrorBox ().addChild (ECommentText.MSG_COMMENT_SAVE_FAILURE.getDisplayText (aDisplayLocale));
          }

          // List of exiting comments + message box
          return AjaxHtmlResponse.createSuccess (aRequestScope,
                                                 CommentUI.getCommentList (aLEC,
                                                                           aOwner,
                                                                           CommentAction.createForComment (ECommentAction.ADD_COMMENT,
                                                                                                           aCommentThread,
                                                                                                           aParentComment),
                                                                           aFormErrors,
                                                                           aMessageBox,
                                                                           true));
        }
      }
    }

    // Somebody played around with the API
    s_aLogger.warn ("Failed to resolve comment object type '" +
                    sObjectType +
                    "' and/or object ID '" +
                    sObjectID +
                    "' for adding to comment '" +
                    sCommentID +
                    "' in thread '" +
                    sCommentThreadID +
                    "'");
    return AjaxHtmlResponse.createError ("Missing required parameters");
  }
}
