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
package com.helger.peppol.comment.ui;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.appbasics.security.AccessManager;
import com.helger.appbasics.security.login.LoggedInUserManager;
import com.helger.appbasics.security.user.IUser;
import com.helger.appbasics.security.user.IUserManager;
import com.helger.bootstrap3.button.BootstrapButton;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.button.EBootstrapButtonSize;
import com.helger.bootstrap3.label.BootstrapLabel;
import com.helger.bootstrap3.label.EBootstrapLabelType;
import com.helger.bootstrap3.panel.BootstrapPanel;
import com.helger.bootstrap3.panel.EBootstrapPanelType;
import com.helger.bootstrap3.table.BootstrapTableForm;
import com.helger.bootstrap3.tooltip.BootstrapTooltip;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.collections.NonBlockingStack;
import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ITypedObject;
import com.helger.css.property.CCSSProperties;
import com.helger.datetime.format.PDTToString;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.HCCol;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCSpan;
import com.helger.html.hc.html.HCTextArea;
import com.helger.html.hc.htmlext.HCUtils;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.js.builder.JSAnonymousFunction;
import com.helger.html.js.builder.JSAssocArray;
import com.helger.html.js.builder.JSExpr;
import com.helger.html.js.builder.JSStatementList;
import com.helger.html.js.builder.JSVar;
import com.helger.html.js.builder.jquery.JQuery;
import com.helger.html.js.builder.jquery.JQueryAjaxBuilder;
import com.helger.html.js.builder.jquery.JQueryInvocation;
import com.helger.peppol.app.ajax.AjaxExecutorPublicCommentAdd;
import com.helger.peppol.app.ajax.AjaxExecutorPublicCommentCreateThread;
import com.helger.peppol.app.ajax.AjaxExecutorPublicCommentDelete;
import com.helger.peppol.app.ajax.AjaxExecutorPublicCommentShowInput;
import com.helger.peppol.app.ajax.CAjaxPublic;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.comment.domain.ComparatorCommentThreadCreationDateTime;
import com.helger.peppol.comment.domain.IComment;
import com.helger.peppol.comment.domain.ICommentIterationCallback;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.validation.error.FormErrors;
import com.helger.webbasics.ajax.response.AjaxDefaultResponse;
import com.helger.webbasics.app.layout.ILayoutExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webctrls.autosize.HCTextAreaAutosize;
import com.helger.webctrls.custom.EDefaultIcon;
import com.helger.webctrls.js.JSJQueryUtils;
import com.helger.webctrls.styler.WebPageStylerManager;
import com.helger.webscopes.domain.IRequestWebScopeWithoutResponse;

public final class CommentUI
{
  private static final String FIELD_COMMENT_AUTHOR = AjaxExecutorPublicCommentCreateThread.PARAM_AUTHOR;
  private static final String FIELD_COMMENT_TITLE = AjaxExecutorPublicCommentCreateThread.PARAM_TITLE;
  private static final String FIELD_COMMENT_TEXT = AjaxExecutorPublicCommentCreateThread.PARAM_TEXT;

  private CommentUI ()
  {}

  @Nonnull
  public static IHCNode getCommentList (@Nonnull final ILayoutExecutionContext aLEC,
                                        @Nonnull final ITypedObject <String> aObject,
                                        @Nullable final FormErrors aFormErrors)
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
    if (ContainerHelper.isNotEmpty (aComments))
    {
      final IUserManager aUserMgr = AccessManager.getInstance ();
      final boolean bIsCommentModerator = CommentSecurity.isCurrentUserCommentModerator ();

      // Container for all threads
      final HCDiv aAllThreadsContainer = new HCDiv ().addClass (CCommentCSS.CSS_CLASS_COMMENT_CONTAINER);
      for (final ICommentThread aCommentThread : ContainerHelper.getSorted (aComments,
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
              if (bUserCanCreateComments)
              {
                aCommentResponseContainer = new HCDiv ();
                final BootstrapButton aResponseButton = new BootstrapButton (EBootstrapButtonSize.MINI).setIcon (EDefaultIcon.ADD);
                aCommentToolbar.addChild (aResponseButton);
                aCommentToolbar.addChild (new BootstrapTooltip (aResponseButton).setTitle (ECommentText.TOOLTIP_RESPONSE.getDisplayText (aDisplayLocale)));

                final JSAnonymousFunction aOnSuccess = new JSAnonymousFunction ();
                final JSVar aJSData = aOnSuccess.param ("data");
                aOnSuccess.body ().add (JQuery.idRef (aCommentResponseContainer)
                                              .empty ()
                                              .append (aJSData.ref (AjaxDefaultResponse.PROPERTY_HTML)));
                final JQueryInvocation aResponseAction = new JQueryAjaxBuilder ().cache (false)
                                                                                 .url (CAjaxPublic.COMMENT_SHOW_INPUT.getInvocationURL (aRequestScope))
                                                                                 .data (new JSAssocArray ().add (AjaxExecutorPublicCommentShowInput.PARAM_OBJECT_TYPE,
                                                                                                                 aObject.getTypeID ()
                                                                                                                        .getObjectTypeName ())
                                                                                                           .add (AjaxExecutorPublicCommentShowInput.PARAM_OBJECT_ID,
                                                                                                                 aObject.getID ())
                                                                                                           .add (AjaxExecutorPublicCommentShowInput.PARAM_COMMENT_THREAD_ID,
                                                                                                                 aCommentThread.getID ())
                                                                                                           .add (AjaxExecutorPublicCommentShowInput.PARAM_COMMENT_ID,
                                                                                                                 aComment.getID ())
                                                                                                           .add (AjaxExecutorPublicCommentShowInput.PARAM_RESULT_DIV_ID,
                                                                                                                 sResultDivID))
                                                                                 .success (JSJQueryUtils.jqueryAjaxSuccessHandler (aOnSuccess,
                                                                                                                                   false))
                                                                                 .build ();
                aResponseButton.setOnClick (aResponseAction);
              }
              if (bIsCommentModerator)
              {
                if (!aComment.isDeleted ())
                {
                  final BootstrapButton aDeleteButton = new BootstrapButton (EBootstrapButtonSize.MINI).setIcon (EDefaultIcon.DELETE);
                  aCommentToolbar.addChild (aDeleteButton);
                  aCommentToolbar.addChild (new BootstrapTooltip (aDeleteButton).setTitle (ECommentText.TOOLTIP_DELETE.getDisplayText (aDisplayLocale)));

                  final JSAnonymousFunction aOnSuccess = new JSAnonymousFunction ();
                  final JSVar aJSData = aOnSuccess.param ("data");
                  aOnSuccess.body ().add (JQuery.idRef (aDeleteButton).disable ());
                  aOnSuccess.body ().add (JQuery.idRef (aCommentPanel.getBody ())
                                                .append (aJSData.ref (AjaxDefaultResponse.PROPERTY_HTML)));
                  final JQueryInvocation aDeleteAction = new JQueryAjaxBuilder ().cache (false)
                                                                                 .url (CAjaxPublic.COMMENT_DELETE.getInvocationURL (aRequestScope))
                                                                                 .data (new JSAssocArray ().add (AjaxExecutorPublicCommentDelete.PARAM_OBJECT_TYPE,
                                                                                                                 aObject.getTypeID ()
                                                                                                                        .getObjectTypeName ())
                                                                                                           .add (AjaxExecutorPublicCommentDelete.PARAM_OBJECT_ID,
                                                                                                                 aObject.getID ())
                                                                                                           .add (AjaxExecutorPublicCommentDelete.PARAM_COMMENT_THREAD_ID,
                                                                                                                 aCommentThread.getID ())
                                                                                                           .add (AjaxExecutorPublicCommentDelete.PARAM_COMMENT_ID,
                                                                                                                 aComment.getID ()))
                                                                                 .success (JSJQueryUtils.jqueryAjaxSuccessHandler (aOnSuccess,
                                                                                                                                   false))
                                                                                 .build ();
                  aDeleteButton.setOnClick (aDeleteAction);
                }

                // Show source host
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
                final String sLastModText = aComment.getEditCount () > 0
                                                                        ? ECommentText.MSG_EDITED_AND_LAST_MODIFICATION.getDisplayTextWithArgs (aDisplayLocale,
                                                                                                                                                Integer.valueOf (aComment.getEditCount ()),
                                                                                                                                                sLastModDT)
                                                                        : ECommentText.MSG_LAST_MODIFICATION.getDisplayTextWithArgs (aDisplayLocale,
                                                                                                                                     sLastModDT);
                aHeader.addChild (new HCDiv ().addChild (sLastModText)
                                              .addClass (CCommentCSS.CSS_CLASS_COMMENT_LAST_MODIFICATION));
              }

              // Show the main comment text
              aCommentPanel.getBody ().addClass (CCommentCSS.CSS_CLASS_SINGLE_COMMENT);
              aCommentPanel.getBody ().addChild (new HCDiv ().addChildren (HCUtils.nl2brList (aComment.getText ()))
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

    // Create comment only for logged in users
    if (bUserCanCreateComments)
    {
      // Add "create comment" button
      ret.addChild (getCreateComment (aLEC, sResultDivID, aObject, null, null, aFormErrors));
    }
    else
      ret.addChild (new BootstrapLabel (EBootstrapLabelType.INFO).addChild (ECommentText.MSG_LOGIN_TO_COMMENT.getDisplayText (aDisplayLocale)));

    return ret;
  }

  @Nonnull
  public static IHCNode getCreateComment (@Nonnull final ILayoutExecutionContext aLEC,
                                          @Nonnull final String sResultDivID,
                                          @Nonnull final ITypedObject <String> aObject,
                                          @Nullable final ICommentThread aCommentThread,
                                          @Nullable final IComment aParentComment,
                                          @Nullable final FormErrors aFormErrors)
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
      aFormContainer.addChild (WebPageStylerManager.getStyler ().createIncorrectInputBox (aLEC));

    final BootstrapTableForm aTable = aFormContainer.addAndReturnChild (new BootstrapTableForm (new HCCol (130),
                                                                                                HCCol.star (),
                                                                                                new HCCol (25)));
    aTable.setTitle (ECommentText.MSG_CREATE_COMMENT.getDisplayText (aDisplayLocale));

    HCEdit aEditAuthor = null;
    if (aLoggedInUser != null)
    {
      aTable.createItemRow ()
            .setLabelMandatory (ECommentText.MSG_FIELD_AUTHOR.getDisplayText (aDisplayLocale))
            .setCtrl (aLoggedInUser.getDisplayName ());
    }
    else
    {
      aEditAuthor = new HCEdit (new RequestField (FIELD_COMMENT_AUTHOR));
      aTable.createItemRow ()
            .setLabelMandatory (ECommentText.MSG_FIELD_AUTHOR.getDisplayText (aDisplayLocale))
            .setCtrl (aEditAuthor)
            .setNote (BootstrapTooltip.createSimpleTooltip (ECommentText.DESC_FIELD_AUTHOR.getDisplayText (aDisplayLocale)))
            .setErrorList (aFormErrors == null ? null : aFormErrors.getListOfField (FIELD_COMMENT_AUTHOR));
    }

    final HCEdit aEditTitle = new HCEdit (new RequestField (FIELD_COMMENT_TITLE));
    aTable.createItemRow ()
          .setLabel (ECommentText.MSG_FIELD_TITLE.getDisplayText (aDisplayLocale))
          .setCtrl (aEditTitle)
          .setNote (BootstrapTooltip.createSimpleTooltip (ECommentText.DESC_FIELD_TITLE.getDisplayText (aDisplayLocale)))
          .setErrorList (aFormErrors == null ? null : aFormErrors.getListOfField (FIELD_COMMENT_TITLE));

    final HCTextArea aTextAreaContent = new HCTextAreaAutosize (new RequestField (FIELD_COMMENT_TEXT)).setRows (5);
    aTable.createItemRow ()
          .setLabelMandatory (ECommentText.MSG_FIELD_TEXT.getDisplayText (aDisplayLocale))
          .setCtrl (aTextAreaContent)
          .setNote (BootstrapTooltip.createSimpleTooltip (ECommentText.DESC_FIELD_TEXT.getDisplayText (aDisplayLocale)))
          .setErrorList (aFormErrors == null ? null : aFormErrors.getListOfField (FIELD_COMMENT_TEXT));

    final BootstrapButtonToolbar aToolbar = new BootstrapButtonToolbar (aLEC);
    // What to do on save?
    {
      final JSAnonymousFunction aOnSuccess = new JSAnonymousFunction ();
      final JSVar aJSData = aOnSuccess.param ("data");
      aOnSuccess.body ()
                .add (JQuery.idRef (sResultDivID).replaceWith (aJSData.ref (AjaxDefaultResponse.PROPERTY_HTML)));
      JQueryInvocation aSaveAction;
      if (bIsCreateNewThread)
      {
        // Create a new thread
        aSaveAction = new JQueryAjaxBuilder ().cache (false)
                                              .url (CAjaxPublic.COMMENT_CREATE_THREAD.getInvocationURL (aRequestScope))
                                              .data (new JSAssocArray ().add (AjaxExecutorPublicCommentCreateThread.PARAM_OBJECT_TYPE,
                                                                              aObject.getTypeID ().getObjectTypeName ())
                                                                        .add (AjaxExecutorPublicCommentCreateThread.PARAM_OBJECT_ID,
                                                                              aObject.getID ())
                                                                        .add (AjaxExecutorPublicCommentCreateThread.PARAM_AUTHOR,
                                                                              aLoggedInUser != null
                                                                                                   ? JSExpr.lit ("")
                                                                                                   : JQuery.idRef (aEditAuthor)
                                                                                                           .val ())
                                                                        .add (AjaxExecutorPublicCommentCreateThread.PARAM_TITLE,
                                                                              JQuery.idRef (aEditTitle).val ())
                                                                        .add (AjaxExecutorPublicCommentCreateThread.PARAM_TEXT,
                                                                              JQuery.idRef (aTextAreaContent).val ()))
                                              .success (JSJQueryUtils.jqueryAjaxSuccessHandler (aOnSuccess, false))
                                              .build ();
      }
      else
      {
        // Reply to a previous comment
        aSaveAction = new JQueryAjaxBuilder ().cache (false)
                                              .url (CAjaxPublic.COMMENT_ADD.getInvocationURL (aRequestScope))
                                              .data (new JSAssocArray ().add (AjaxExecutorPublicCommentAdd.PARAM_OBJECT_TYPE,
                                                                              aObject.getTypeID ().getObjectTypeName ())
                                                                        .add (AjaxExecutorPublicCommentAdd.PARAM_OBJECT_ID,
                                                                              aObject.getID ())
                                                                        .add (AjaxExecutorPublicCommentAdd.PARAM_COMMENT_THREAD_ID,
                                                                              aCommentThread.getID ())
                                                                        .add (AjaxExecutorPublicCommentAdd.PARAM_COMMENT_ID,
                                                                              aParentComment.getID ())
                                                                        .add (AjaxExecutorPublicCommentAdd.PARAM_OBJECT_ID,
                                                                              aObject.getID ())
                                                                        .add (AjaxExecutorPublicCommentAdd.PARAM_AUTHOR,
                                                                              aLoggedInUser != null
                                                                                                   ? JSExpr.lit ("")
                                                                                                   : JQuery.idRef (aEditAuthor)
                                                                                                           .val ())
                                                                        .add (AjaxExecutorPublicCommentAdd.PARAM_TITLE,
                                                                              JQuery.idRef (aEditTitle).val ())
                                                                        .add (AjaxExecutorPublicCommentAdd.PARAM_TEXT,
                                                                              JQuery.idRef (aTextAreaContent).val ()))
                                              .success (JSJQueryUtils.jqueryAjaxSuccessHandler (aOnSuccess, false))
                                              .build ();
      }
      aToolbar.addButtonSave (aDisplayLocale, aSaveAction);
    }

    BootstrapButton aButtonCreate = null;
    if (bIsCreateNewThread)
    {
      // The create button
      aButtonCreate = new BootstrapButton ().addChild (ECommentText.MSG_CREATE_COMMENT.getDisplayText (aDisplayLocale));
      aButtonCreate.setOnClick (new JSStatementList (JQuery.idRef (aFormContainer).show (), JQuery.jQueryThis ()
                                                                                                  .disable ()));
    }

    // What to do on cancel?
    {
      final JSStatementList aCancelAction = new JSStatementList (JQuery.idRefMultiple (aEditTitle, aTextAreaContent)
                                                                       .val (""), JQuery.idRef (aFormContainer).hide ());
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
    return ret;
  }
}