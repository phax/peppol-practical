/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.peppol.comment.ui;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.NonBlockingStack;
import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ITypedObject;
import com.helger.css.property.CCSSProperties;
import com.helger.datetime.format.PDTToString;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.ext.HCExtHelper;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.textlevel.HCSpan;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.jquery.JQuery;
import com.helger.html.jquery.JQueryAjaxBuilder;
import com.helger.html.jquery.JQueryInvocation;
import com.helger.html.jscode.JSAnonymousFunction;
import com.helger.html.jscode.JSAssocArray;
import com.helger.html.jscode.JSExpr;
import com.helger.html.jscode.JSStatementList;
import com.helger.html.jscode.JSVar;
import com.helger.peppol.comment.ajax.AjaxExecutorCommentAdd;
import com.helger.peppol.comment.ajax.AjaxExecutorCommentCreateThread;
import com.helger.peppol.comment.ajax.AjaxExecutorCommentDelete;
import com.helger.peppol.comment.ajax.AjaxExecutorCommentShowInput;
import com.helger.peppol.comment.ajax.CAjaxComment;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.comment.domain.ComparatorCommentThreadCreationDateTime;
import com.helger.peppol.comment.domain.IComment;
import com.helger.peppol.comment.domain.ICommentIterationCallback;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.photon.basic.security.AccessManager;
import com.helger.photon.basic.security.login.LoggedInUserManager;
import com.helger.photon.basic.security.user.IUser;
import com.helger.photon.basic.security.user.IUserManager;
import com.helger.photon.bootstrap3.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap3.button.BootstrapButton;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.button.EBootstrapButtonSize;
import com.helger.photon.bootstrap3.form.BootstrapFormGroup;
import com.helger.photon.bootstrap3.form.BootstrapViewForm;
import com.helger.photon.bootstrap3.label.BootstrapLabel;
import com.helger.photon.bootstrap3.label.EBootstrapLabelType;
import com.helger.photon.bootstrap3.panel.BootstrapPanel;
import com.helger.photon.bootstrap3.panel.EBootstrapPanelType;
import com.helger.photon.bootstrap3.tooltip.BootstrapTooltip;
import com.helger.photon.core.EPhotonCoreText;
import com.helger.photon.core.ajax.response.AjaxHtmlResponse;
import com.helger.photon.core.app.context.ILayoutExecutionContext;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.icon.EDefaultIcon;
import com.helger.photon.uicore.js.JSJQueryHelper;
import com.helger.photon.uictrls.autosize.HCTextAreaAutosize;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

public final class CommentUI
{
  private static final String FIELD_COMMENT_AUTHOR = AjaxExecutorCommentCreateThread.PARAM_AUTHOR;
  private static final String FIELD_COMMENT_TITLE = AjaxExecutorCommentCreateThread.PARAM_TITLE;
  private static final String FIELD_COMMENT_TEXT = AjaxExecutorCommentCreateThread.PARAM_TEXT;

  private CommentUI ()
  {}

  @Nonnull
  public static IHCNode getCommentList (@Nonnull final ILayoutExecutionContext aLEC,
                                        @Nonnull final ITypedObject <String> aObject,
                                        @Nonnull final CommentAction aCommentAction,
                                        @Nullable final CommentFormErrors aFormErrors,
                                        @Nullable final IHCNode aMessageBox,
                                        final boolean bShowCreateComments)
  {
    ValueEnforcer.notNull (aLEC, "LEC");
    ValueEnforcer.notNull (aObject, "Object");

    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();
    final HCDiv ret = new HCDiv ();
    final String sResultDivID = ret.ensureID ().getID ();
    final boolean bUserCanCreateComments = CommentSecurity.canCurrentUserPostComments ();

    // Get all existing comments
    final List <ICommentThread> aComments = CommentThreadManager.getInstance ().getCommentThreadsOfObject (aObject);
    if (CollectionHelper.isNotEmpty (aComments))
    {
      final IUserManager aUserMgr = AccessManager.getInstance ();
      final boolean bIsCommentModerator = CommentSecurity.isCurrentUserCommentModerator ();

      // Container for all threads
      final HCDiv aAllThreadsContainer = new HCDiv ().addClass (CCommentCSS.CSS_CLASS_COMMENT_CONTAINER);
      for (final ICommentThread aCommentThread : CollectionHelper.getSorted (aComments,
                                                                             new ComparatorCommentThreadCreationDateTime ()))
      {
        // Container for this thread
        final HCDiv aThreadContainer = new HCDiv ();
        aThreadContainer.addClass (CCommentCSS.CSS_CLASS_COMMENT_THREAD);
        final NonBlockingStack <HCDiv> aStack = new NonBlockingStack <> ();
        aStack.push (aThreadContainer);

        aCommentThread.iterateAllComments (new ICommentIterationCallback ()
        {
          public void onCommentStart (final int nLevel,
                                      @Nullable final IComment aParentComment,
                                      @Nonnull final IComment aComment)
          {
            // Show only approved comments
            final boolean bIsApproved = aComment.getState ().isApproved ();
            if (bIsApproved || bIsCommentModerator)
            {
              // Get author name and determine if it is a registered user
              boolean bRegisteredUser = false;
              String sAuthor = null;
              if (StringHelper.hasText (aComment.getUserID ()))
              {
                final IUser aUser = aUserMgr.getUserOfID (aComment.getUserID ());
                if (aUser != null)
                {
                  sAuthor = aUser.getDisplayName ();
                  bRegisteredUser = true;
                }
              }
              if (sAuthor == null)
                sAuthor = aComment.getCreatorName ();

              // Fill panel header
              final BootstrapPanel aCommentPanel = new BootstrapPanel (bIsApproved ? EBootstrapPanelType.DEFAULT
                                                                                   : EBootstrapPanelType.DANGER);
              final HCDiv aHeader = aCommentPanel.getOrCreateHeader ();

              // Is comment deleted?
              if (aComment.isDeleted ())
                aHeader.addChild (ECommentText.MSG_IS_DELETED.getDisplayText (aDisplayLocale));

              // Creation date
              aHeader.addChild (new HCSpan ().addChild (PDTToString.getAsString (aComment.getCreationDateTime (),
                                                                                 aDisplayLocale))
                                             .addClass (CCommentCSS.CSS_CLASS_COMMENT_CREATIONDT));

              // Author
              aHeader.addChild (ECommentText.MSG_BY.getDisplayText (aDisplayLocale));
              final HCSpan aAuthor = new HCSpan ().addChild (sAuthor).addClass (CCommentCSS.CSS_CLASS_COMMENT_AUTHOR);
              if (bRegisteredUser)
                aAuthor.addClass (CCommentCSS.CSS_CLASS_COMMENT_REGISTERED_USER);
              aHeader.addChild (aAuthor);

              // Title
              if (StringHelper.hasText (aComment.getTitle ()))
              {
                aHeader.addChild (ECommentText.MSG_SEPARATOR_AUTHOR_TITLE.getDisplayText (aDisplayLocale));
                aHeader.addChild (new HCSpan ().addChild (aComment.getTitle ())
                                               .addClass (CCommentCSS.CSS_CLASS_COMMENT_TITLE));
              }

              // Toolbar
              final HCSpan aCommentToolbar = new HCSpan ().addClass (CCommentCSS.CSS_CLASS_COMMENT_TOOLBAR);
              HCDiv aCommentResponseContainer = null;
              // Respond to a comment - at maximum 6 levels
              if (bShowCreateComments && bUserCanCreateComments && !aComment.isDeleted () && nLevel < 6)
              {
                aCommentResponseContainer = new HCDiv ();
                final BootstrapButton aResponseButton = new BootstrapButton (EBootstrapButtonSize.MINI).setIcon (EDefaultIcon.ADD);
                aCommentToolbar.addChild (aResponseButton);
                aCommentToolbar.addChild (new BootstrapTooltip (aResponseButton).setTitle (ECommentText.TOOLTIP_RESPONSE.getDisplayText (aDisplayLocale)));

                if (aCommentAction.isMatching (ECommentAction.ADD_COMMENT, aCommentThread, aComment) &&
                    aFormErrors != null &&
                    aFormErrors.isReplyTo (aCommentThread, aComment))
                {
                  // Upon adding a response
                  if (aMessageBox == null || !aFormErrors.isEmpty ())
                  {
                    // Show the input form again
                    aCommentResponseContainer.addChild (getCreateComment (aLEC,
                                                                          sResultDivID,
                                                                          aObject,
                                                                          aCommentThread,
                                                                          aComment,
                                                                          aFormErrors,
                                                                          aMessageBox));
                  }
                  else
                  {
                    // Show the success or error message
                    aCommentPanel.getBody ().addChild (aMessageBox);
                  }
                }
                else
                {
                  // Add the JS to show the input form
                  final JSAnonymousFunction aOnSuccess = new JSAnonymousFunction ();
                  final JSVar aJSData = aOnSuccess.param ("data");
                  aOnSuccess.body ().add (JQuery.idRef (aCommentResponseContainer)
                                                .empty ()
                                                .append (aJSData.ref (AjaxHtmlResponse.PROPERTY_HTML)));
                  final JQueryInvocation aResponseAction = new JQueryAjaxBuilder ().cache (false)
                                                                                   .url (CAjaxComment.COMMENT_SHOW_INPUT.getInvocationURL (aRequestScope))
                                                                                   .data (new JSAssocArray ().add (AjaxExecutorCommentShowInput.PARAM_OBJECT_TYPE,
                                                                                                                   aObject.getObjectType ()
                                                                                                                          .getName ())
                                                                                                             .add (AjaxExecutorCommentShowInput.PARAM_OBJECT_ID,
                                                                                                                   aObject.getID ())
                                                                                                             .add (AjaxExecutorCommentShowInput.PARAM_COMMENT_THREAD_ID,
                                                                                                                   aCommentThread.getID ())
                                                                                                             .add (AjaxExecutorCommentShowInput.PARAM_COMMENT_ID,
                                                                                                                   aComment.getID ())
                                                                                                             .add (AjaxExecutorCommentShowInput.PARAM_RESULT_DIV_ID,
                                                                                                                   sResultDivID))
                                                                                   .success (JSJQueryHelper.jqueryAjaxSuccessHandler (aOnSuccess,
                                                                                                                                      null))
                                                                                   .build ();
                  aResponseButton.setOnClick (aResponseAction);
                }
              }

              if (bIsCommentModerator)
              {
                if (aCommentAction.isMatching (ECommentAction.DELETE_COMMENT, aCommentThread, aComment))
                  aCommentPanel.getBody ().addChild (aMessageBox);

                // Can the comment be deleted?
                if (!aComment.isDeleted ())
                {
                  final BootstrapButton aDeleteButton = new BootstrapButton (EBootstrapButtonSize.MINI).setIcon (EDefaultIcon.DELETE);
                  aCommentToolbar.addChild (aDeleteButton);
                  aCommentToolbar.addChild (new BootstrapTooltip (aDeleteButton).setTitle (ECommentText.TOOLTIP_DELETE.getDisplayText (aDisplayLocale)));

                  final JSAnonymousFunction aOnSuccess = new JSAnonymousFunction ();
                  final JSVar aJSData = aOnSuccess.param ("data");
                  aOnSuccess.body ().add (JQuery.idRef (sResultDivID)
                                                .replaceWith (aJSData.ref (AjaxHtmlResponse.PROPERTY_HTML)));
                  final JQueryInvocation aDeleteAction = new JQueryAjaxBuilder ().cache (false)
                                                                                 .url (CAjaxComment.COMMENT_DELETE.getInvocationURL (aRequestScope))
                                                                                 .data (new JSAssocArray ().add (AjaxExecutorCommentDelete.PARAM_OBJECT_TYPE,
                                                                                                                 aObject.getObjectType ()
                                                                                                                        .getName ())
                                                                                                           .add (AjaxExecutorCommentDelete.PARAM_OBJECT_ID,
                                                                                                                 aObject.getID ())
                                                                                                           .add (AjaxExecutorCommentDelete.PARAM_COMMENT_THREAD_ID,
                                                                                                                 aCommentThread.getID ())
                                                                                                           .add (AjaxExecutorCommentDelete.PARAM_COMMENT_ID,
                                                                                                                 aComment.getID ()))
                                                                                 .success (JSJQueryHelper.jqueryAjaxSuccessHandler (aOnSuccess,
                                                                                                                                    null))
                                                                                 .build ();
                  aDeleteButton.setOnClick (aDeleteAction);
                }

                // Show source host and further info
                aCommentToolbar.addChild (BootstrapTooltip.createSimpleTooltip (ECommentText.TOOLTIP_HOST.getDisplayTextWithArgs (aDisplayLocale,
                                                                                                                                  aComment.getHost ())));
              }
              if (aCommentToolbar.hasChildren ())
                aHeader.addChild (aCommentToolbar);

              // Last modification
              if (aComment.getLastModificationDateTime () != null)
              {
                final String sLastModDT = PDTToString.getAsString (aComment.getLastModificationDateTime (),
                                                                   aDisplayLocale);
                final String sLastModText = aComment.getEditCount () > 0 ? ECommentText.MSG_EDITED_AND_LAST_MODIFICATION.getDisplayTextWithArgs (aDisplayLocale,
                                                                                                                                                 Integer.valueOf (aComment.getEditCount ()),
                                                                                                                                                 sLastModDT)
                                                                         : ECommentText.MSG_LAST_MODIFICATION.getDisplayTextWithArgs (aDisplayLocale,
                                                                                                                                      sLastModDT);
                aHeader.addChild (new HCDiv ().addChild (sLastModText)
                                              .addClass (CCommentCSS.CSS_CLASS_COMMENT_LAST_MODIFICATION));
              }

              // Show the main comment text
              aCommentPanel.getBody ().addClass (CCommentCSS.CSS_CLASS_SINGLE_COMMENT);

              // Always put the text as the first part of the body
              aCommentPanel.getBody ().addChild (0,
                                                 new HCDiv ().addChildren (HCExtHelper.nl2brList (aComment.getText ()))
                                                             .addClass (CCommentCSS.CSS_CLASS_COMMENT_TEXT));
              // the dummy container for new comment form
              aCommentPanel.getBody ().addChild (aCommentResponseContainer);

              aStack.peek ().addChild (aCommentPanel);
              aStack.push (aCommentPanel.getBody ());
            }
            else
            {
              // Don't display - push the previous item
              aStack.push (aStack.peek ());
            }
          }

          public void onCommentEnd (final int nLevel,
                                    @Nullable final IComment aParentComment,
                                    @Nonnull final IComment aComment)
          {
            aStack.pop ();
          }
        });

        // Show only thread panels which contain at least one comment
        if (aThreadContainer.hasChildren ())
          aAllThreadsContainer.addChild (aThreadContainer);
      }
      ret.addChild (aAllThreadsContainer);
    }

    if (bShowCreateComments)
    {
      // Create comment only for logged in users
      if (bUserCanCreateComments)
      {
        // Add "create comment" button
        final boolean bIsForCreateThread = aCommentAction.isMatching (ECommentAction.CREATE_THREAD);
        ret.addChild (getCreateComment (aLEC,
                                        sResultDivID,
                                        aObject,
                                        null,
                                        null,
                                        bIsForCreateThread ? aFormErrors : null,
                                        bIsForCreateThread ? aMessageBox : null));
      }
      else
        ret.addChild (new BootstrapLabel (EBootstrapLabelType.INFO).addChild (ECommentText.MSG_LOGIN_TO_COMMENT.getDisplayText (aDisplayLocale)));
    }

    return ret;
  }

  @Nonnull
  public static IHCNode getCreateComment (@Nonnull final ILayoutExecutionContext aLEC,
                                          @Nonnull final String sResultDivID,
                                          @Nonnull final ITypedObject <String> aObject,
                                          @Nullable final ICommentThread aCommentThread,
                                          @Nullable final IComment aParentComment,
                                          @Nullable final CommentFormErrors aFormErrors,
                                          @Nullable final IHCNode aMessageBox)
  {
    final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final IUser aLoggedInUser = LoggedInUserManager.getInstance ().getCurrentUser ();
    final boolean bIsCreateNewThread = aCommentThread == null || aParentComment == null;

    final HCDiv aFormContainer = new HCDiv ();
    if (bIsCreateNewThread)
      if (aFormErrors == null || aFormErrors.isEmpty ())
        aFormContainer.addStyle (CCSSProperties.DISPLAY_NONE);
    aFormContainer.addClass (CCommentCSS.CSS_CLASS_COMMENT_CREATE);

    if (aFormErrors != null && !aFormErrors.isEmpty ())
      aFormContainer.addChild (new BootstrapErrorBox ().addChild (EPhotonCoreText.ERR_INCORRECT_INPUT.getDisplayText (aDisplayLocale)));

    final BootstrapViewForm aForm = aFormContainer.addAndReturnChild (new BootstrapViewForm ());
    aForm.setTitle (ECommentText.MSG_CREATE_COMMENT.getDisplayText (aDisplayLocale));

    HCEdit aEditAuthor = null;
    if (aLoggedInUser != null)
    {
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory (ECommentText.MSG_FIELD_AUTHOR.getDisplayText (aDisplayLocale))
                                                   .setCtrl (aLoggedInUser.getDisplayName ()));
    }
    else
    {
      aEditAuthor = new HCEdit (new RequestField (FIELD_COMMENT_AUTHOR));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory (ECommentText.MSG_FIELD_AUTHOR.getDisplayText (aDisplayLocale))
                                                   .setCtrl (aEditAuthor)
                                                   .setHelpText (ECommentText.DESC_FIELD_AUTHOR.getDisplayText (aDisplayLocale))
                                                   .setErrorList (aFormErrors == null ? null
                                                                                      : aFormErrors.getListOfField (FIELD_COMMENT_AUTHOR)));
    }

    final HCEdit aEditTitle = new HCEdit (new RequestField (FIELD_COMMENT_TITLE));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabel (ECommentText.MSG_FIELD_TITLE.getDisplayText (aDisplayLocale))
                                                 .setCtrl (aEditTitle)
                                                 .setHelpText (ECommentText.DESC_FIELD_TITLE.getDisplayText (aDisplayLocale))
                                                 .setErrorList (aFormErrors == null ? null
                                                                                    : aFormErrors.getListOfField (FIELD_COMMENT_TITLE)));

    final HCTextAreaAutosize aTextAreaContent = new HCTextAreaAutosize (new RequestField (FIELD_COMMENT_TEXT)).setRows (5);
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory (ECommentText.MSG_FIELD_TEXT.getDisplayText (aDisplayLocale))
                                                 .setCtrl (aTextAreaContent)
                                                 .setHelpText (ECommentText.DESC_FIELD_TEXT.getDisplayText (aDisplayLocale))
                                                 .setErrorList (aFormErrors == null ? null
                                                                                    : aFormErrors.getListOfField (FIELD_COMMENT_TEXT)));

    final BootstrapButtonToolbar aToolbar = new BootstrapButtonToolbar (aLEC);
    // What to do on save?
    {
      final JSAnonymousFunction aOnSuccess = new JSAnonymousFunction ();
      final JSVar aJSData = aOnSuccess.param ("data");
      aOnSuccess.body ().add (JQuery.idRef (sResultDivID).replaceWith (aJSData.ref (AjaxHtmlResponse.PROPERTY_HTML)));
      JQueryInvocation aSaveAction;
      if (bIsCreateNewThread)
      {
        // Create a new thread
        aSaveAction = new JQueryAjaxBuilder ().cache (false)
                                              .url (CAjaxComment.COMMENT_CREATE_THREAD.getInvocationURL (aRequestScope))
                                              .data (new JSAssocArray ().add (AjaxExecutorCommentCreateThread.PARAM_OBJECT_TYPE,
                                                                              aObject.getObjectType ().getName ())
                                                                        .add (AjaxExecutorCommentCreateThread.PARAM_OBJECT_ID,
                                                                              aObject.getID ())
                                                                        .add (AjaxExecutorCommentCreateThread.PARAM_AUTHOR,
                                                                              aLoggedInUser != null ? JSExpr.lit ("")
                                                                                                    : JQuery.idRef (aEditAuthor)
                                                                                                            .val ())
                                                                        .add (AjaxExecutorCommentCreateThread.PARAM_TITLE,
                                                                              JQuery.idRef (aEditTitle).val ())
                                                                        .add (AjaxExecutorCommentCreateThread.PARAM_TEXT,
                                                                              JQuery.idRef (aTextAreaContent).val ()))
                                              .success (JSJQueryHelper.jqueryAjaxSuccessHandler (aOnSuccess, null))
                                              .build ();
      }
      else
      {
        // Reply to a previous comment
        aSaveAction = new JQueryAjaxBuilder ().cache (false)
                                              .url (CAjaxComment.COMMENT_ADD.getInvocationURL (aRequestScope))
                                              .data (new JSAssocArray ().add (AjaxExecutorCommentAdd.PARAM_OBJECT_TYPE,
                                                                              aObject.getObjectType ().getName ())
                                                                        .add (AjaxExecutorCommentAdd.PARAM_OBJECT_ID,
                                                                              aObject.getID ())
                                                                        .add (AjaxExecutorCommentAdd.PARAM_COMMENT_THREAD_ID,
                                                                              aCommentThread.getID ())
                                                                        .add (AjaxExecutorCommentAdd.PARAM_COMMENT_ID,
                                                                              aParentComment.getID ())
                                                                        .add (AjaxExecutorCommentAdd.PARAM_OBJECT_ID,
                                                                              aObject.getID ())
                                                                        .add (AjaxExecutorCommentAdd.PARAM_AUTHOR,
                                                                              aLoggedInUser != null ? JSExpr.lit ("")
                                                                                                    : JQuery.idRef (aEditAuthor)
                                                                                                            .val ())
                                                                        .add (AjaxExecutorCommentAdd.PARAM_TITLE,
                                                                              JQuery.idRef (aEditTitle).val ())
                                                                        .add (AjaxExecutorCommentAdd.PARAM_TEXT,
                                                                              JQuery.idRef (aTextAreaContent).val ()))
                                              .success (JSJQueryHelper.jqueryAjaxSuccessHandler (aOnSuccess, null))
                                              .build ();
      }
      aToolbar.addButtonSave (aDisplayLocale, aSaveAction);
    }

    BootstrapButton aButtonCreate = null;
    if (bIsCreateNewThread)
    {
      // The create button
      aButtonCreate = new BootstrapButton ().addChild (ECommentText.MSG_CREATE_COMMENT.getDisplayText (aDisplayLocale));
      aButtonCreate.setOnClick (new JSStatementList (JQuery.idRef (aFormContainer).show (),
                                                     JQuery.jQueryThis ().disable ()));
    }

    // What to do on cancel?
    {
      final JSStatementList aCancelAction = new JSStatementList (JQuery.idRefMultiple (aEditTitle, aTextAreaContent)
                                                                       .val (""),
                                                                 JQuery.idRef (aFormContainer).hide ());
      if (aButtonCreate != null)
        aCancelAction.add (JQuery.idRef (aButtonCreate).enable ());
      if (aEditAuthor != null)
        aCancelAction.add (JQuery.idRef (aEditAuthor).val (""));
      aToolbar.addButtonCancel (aDisplayLocale, aCancelAction);
    }
    aFormContainer.addChild (aToolbar);

    // Show create comment button
    final HCNodeList ret = new HCNodeList ();
    ret.addChild (aButtonCreate);
    ret.addChild (aFormContainer);
    ret.addChild (aMessageBox);
    return ret;
  }
}
