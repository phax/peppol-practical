/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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

import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ITypedObject;
import com.helger.commons.type.ObjectType;
import com.helger.commons.type.TypedObject;
import com.helger.html.hc.IHCNode;
import com.helger.peppol.comment.domain.Comment;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.comment.domain.ECommentState;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.peppol.comment.ui.CommentAction;
import com.helger.peppol.comment.ui.CommentFormErrors;
import com.helger.peppol.comment.ui.CommentSecurity;
import com.helger.peppol.comment.ui.CommentUI;
import com.helger.peppol.comment.ui.ECommentAction;
import com.helger.peppol.comment.ui.ECommentText;
import com.helger.photon.bootstrap3.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.photon.core.PhotonUnifiedResponse;
import com.helger.photon.core.ajax.executor.IAjaxExecutor;
import com.helger.photon.core.app.context.LayoutExecutionContext;
import com.helger.photon.security.login.LoggedInUserManager;
import com.helger.photon.security.user.IUser;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

/**
 * AJAX handler for creating a new comment thread.
 *
 * @author Philip Helger
 */
public final class AjaxExecutorCommentCreateThread implements IAjaxExecutor
{
  public static final String PARAM_OBJECT_TYPE = "objectType";
  public static final String PARAM_OBJECT_ID = "objectID";
  public static final String PARAM_AUTHOR = "author";
  public static final String PARAM_TITLE = "title";
  public static final String PARAM_TEXT = "text";
  private static final Logger LOGGER = LoggerFactory.getLogger (AjaxExecutorCommentCreateThread.class);

  public void handleRequest (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                             @Nonnull final PhotonUnifiedResponse aAjaxResponse) throws Exception
  {
    final LayoutExecutionContext aLEC = LayoutExecutionContext.createForAjaxOrAction (aRequestScope);
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final String sObjectType = aRequestScope.params ().getAsString (PARAM_OBJECT_TYPE);
    final String sObjectID = aRequestScope.params ().getAsString (PARAM_OBJECT_ID);
    String sAuthor = aRequestScope.params ().getAsString (PARAM_AUTHOR);
    final String sTitle = aRequestScope.params ().getAsString (PARAM_TITLE);
    final String sText = aRequestScope.params ().getAsString (PARAM_TEXT);

    // Get info on current user
    final IUser aCurrentUser = LoggedInUserManager.getInstance ().getCurrentUser ();
    final String sCurrentUserID = aCurrentUser != null ? aCurrentUser.getID () : null;
    if (aCurrentUser != null)
      sAuthor = aCurrentUser.getDisplayName ();

    if (StringHelper.hasText (sObjectType) &&
        StringHelper.hasText (sObjectID) &&
        CommentSecurity.canCurrentUserPostComments ())
    {
      // Create a dummy object
      final ITypedObject <String> aOwner = TypedObject.create (new ObjectType (sObjectType), sObjectID);

      final CommentFormErrors aFormErrors = CommentFormErrors.createForNewThread ();
      if (StringHelper.hasNoText (sAuthor))
      {
        // No author provided
        aFormErrors.addFieldError (PARAM_AUTHOR,
                                   ECommentText.MSG_ERR_COMMENT_NO_AUTHOR.getDisplayText (aDisplayLocale));
      }
      if (StringHelper.hasNoText (sText))
      {
        // No text provided
        aFormErrors.addFieldError (PARAM_TEXT, ECommentText.MSG_ERR_COMMENT_NO_TEXT.getDisplayText (aDisplayLocale));
      }

      IHCNode aMessageBox = null;
      if (aFormErrors.isEmpty ())
      {
        // Go ahead and save
        final ICommentThread aNewThread = CommentThreadManager.getInstance ()
                                                              .createNewThread (aOwner,
                                                                                new Comment (aRequestScope.getRemoteHost (),
                                                                                             ECommentState.APPROVED,
                                                                                             sCurrentUserID,
                                                                                             sAuthor,
                                                                                             sTitle,
                                                                                             sText));
        if (aNewThread != null)
          aMessageBox = new BootstrapSuccessBox ().addChild (ECommentText.MSG_COMMENT_SAVE_SUCCESS.getDisplayText (aDisplayLocale));
        else
          aMessageBox = new BootstrapErrorBox ().addChild (ECommentText.MSG_COMMENT_SAVE_FAILURE.getDisplayText (aDisplayLocale));
      }

      // List of exiting comments + message box
      aAjaxResponse.html (CommentUI.getCommentList (aLEC,
                                                    aOwner,
                                                    CommentAction.createGeneric (ECommentAction.CREATE_THREAD),
                                                    aFormErrors,
                                                    aMessageBox,
                                                    true));
      return;
    }

    // Somebody played around with the API
    LOGGER.warn ("Failed to resolve comment object type '" + sObjectType + "' and/or object ID '" + sObjectID + "'");
    aAjaxResponse.createNotFound ();
  }
}
