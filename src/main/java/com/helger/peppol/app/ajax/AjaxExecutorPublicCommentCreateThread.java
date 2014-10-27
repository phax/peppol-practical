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

import com.helger.appbasics.security.login.LoggedInUserManager;
import com.helger.appbasics.security.user.IUser;
import com.helger.bootstrap3.alert.BootstrapErrorBox;
import com.helger.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ITypedObject;
import com.helger.commons.type.ObjectType;
import com.helger.commons.type.TypedObject;
import com.helger.html.hc.IHCNode;
import com.helger.peppol.comment.domain.Comment;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.comment.domain.ECommentState;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.peppol.comment.ui.CommentSecurity;
import com.helger.peppol.comment.ui.CommentUI;
import com.helger.peppol.comment.ui.ECommentText;
import com.helger.validation.error.FormErrors;
import com.helger.webbasics.ajax.executor.AbstractAjaxExecutor;
import com.helger.webbasics.ajax.response.AjaxDefaultResponse;
import com.helger.webbasics.ajax.response.IAjaxResponse;
import com.helger.webbasics.app.layout.LayoutExecutionContext;
import com.helger.webscopes.domain.IRequestWebScopeWithoutResponse;

/**
 * AJAX handler for creating a new comment thread.
 *
 * @author Philip Helger
 */
public final class AjaxExecutorPublicCommentCreateThread extends AbstractAjaxExecutor
{
  public static final String PARAM_OBJECT_TYPE = "objectType";
  public static final String PARAM_OBJECT_ID = "objectID";
  public static final String PARAM_AUTHOR = "author";
  public static final String PARAM_TITLE = "title";
  public static final String PARAM_TEXT = "text";
  private static final Logger s_aLogger = LoggerFactory.getLogger (AjaxExecutorPublicCommentCreateThread.class);

  @Override
  @Nonnull
  protected IAjaxResponse mainHandleRequest (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope) throws Exception
  {
    final LayoutExecutionContext aLEC = LayoutExecutionContext.createForAjaxOrAction (aRequestScope);
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final String sObjectType = aRequestScope.getAttributeAsString (PARAM_OBJECT_TYPE);
    final String sObjectID = aRequestScope.getAttributeAsString (PARAM_OBJECT_ID);
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
        CommentSecurity.canCurrentUserPostComments ())
    {
      // Create a dummy object
      final ITypedObject <String> aOwner = TypedObject.create (new ObjectType (sObjectType), sObjectID);

      final FormErrors aFormErrors = new FormErrors ();
      if (StringHelper.hasNoText (sAuthor))
      {
        // No author provided
        aFormErrors.addFieldError (PARAM_AUTHOR, ECommentText.MSG_ERR_COMMENT_NO_AUTHOR.getDisplayText (aDisplayLocale));
      }
      if (StringHelper.hasNoText (sText))
      {
        // No text provided
        aFormErrors.addFieldError (PARAM_TEXT, ECommentText.MSG_ERR_COMMENT_NO_TEXT.getDisplayText (aDisplayLocale));
      }

      IHCNode aStatusNode = null;
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
          aStatusNode = new BootstrapSuccessBox ().addChild (ECommentText.MSG_COMMENT_SAVE_SUCCESS.getDisplayText (aDisplayLocale));
        else
          aStatusNode = new BootstrapErrorBox ().addChild (ECommentText.MSG_COMMENT_SAVE_FAILURE.getDisplayText (aDisplayLocale));
      }

      // List of exiting comments + message box
      return AjaxDefaultResponse.createSuccess (aRequestScope,
                                                CommentUI.getCommentList (aLEC, aOwner, aFormErrors),
                                                aStatusNode);
    }

    // Somebody played around with the API
    s_aLogger.warn ("Failed to resolve comment object type '" + sObjectType + "' and/or object ID '" + sObjectID + "'");
    return AjaxDefaultResponse.createError ();
  }
}
