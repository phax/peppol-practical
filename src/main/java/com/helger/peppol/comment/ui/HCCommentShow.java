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
import com.helger.appbasics.security.util.SecurityUtils;
import com.helger.bootstrap3.button.BootstrapButton;
import com.helger.bootstrap3.button.EBootstrapButtonSize;
import com.helger.bootstrap3.label.BootstrapLabel;
import com.helger.bootstrap3.label.EBootstrapLabelType;
import com.helger.bootstrap3.panel.BootstrapPanel;
import com.helger.bootstrap3.panel.EBootstrapPanelType;
import com.helger.bootstrap3.tooltip.BootstrapTooltip;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ITypedObject;
import com.helger.datetime.format.PDTToString;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCSpan;
import com.helger.html.hc.htmlext.HCUtils;
import com.helger.html.js.builder.JSAnonymousFunction;
import com.helger.html.js.builder.JSAssocArray;
import com.helger.html.js.builder.JSVar;
import com.helger.html.js.builder.jquery.JQuery;
import com.helger.html.js.builder.jquery.JQueryAjaxBuilder;
import com.helger.html.js.builder.jquery.JQueryInvocation;
import com.helger.peppol.app.CApp;
import com.helger.peppol.app.ajax.AjaxExecutorPublicCommentDelete;
import com.helger.peppol.app.ajax.CAjaxPublic;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.comment.domain.ComparatorCommentThreadCreationDateTime;
import com.helger.peppol.comment.domain.IComment;
import com.helger.peppol.comment.domain.ICommentIterationCallback;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.validation.error.FormErrors;
import com.helger.webbasics.ajax.response.AjaxDefaultResponse;
import com.helger.webbasics.app.layout.ILayoutExecutionContext;
import com.helger.webctrls.custom.EDefaultIcon;
import com.helger.webctrls.js.JSJQueryUtils;
import com.helger.webscopes.domain.IRequestWebScopeWithoutResponse;

public final class HCCommentShow
{
  private HCCommentShow ()
  {}

  @Nonnull
  public static IHCNode createCommentList (@Nonnull final ILayoutExecutionContext aLEC,
                                           @Nonnull final ITypedObject <String> aObject,
                                           @Nullable final FormErrors aFormErrors)
  {
    ValueEnforcer.notNull (aLEC, "LEC");
    ValueEnforcer.notNull (aObject, "Object");

    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();
    final HCDiv ret = new HCDiv ();

    // Get all existing comments
    final List <ICommentThread> aComments = CommentThreadManager.getInstance ().getCommentThreadsOfObject (aObject);
    if (ContainerHelper.isNotEmpty (aComments))
    {
      final IUserManager aUserMgr = AccessManager.getInstance ();
      final HCDiv aAllComments = new HCDiv ().addClass (CCommentCSS.CSS_CLASS_COMMENT_CONTAINER);
      for (final ICommentThread aCommentThread : ContainerHelper.getSorted (aComments,
                                                                            new ComparatorCommentThreadCreationDateTime ()))
      {
        final HCDiv aThreadContainer = new HCDiv ();
        aThreadContainer.addClass (CCommentCSS.CSS_CLASS_COMMENT_THREAD);

        final boolean bIsCommentModerator = SecurityUtils.hasCurrentUserRole (CApp.ROLE_COMMENT_MODERATOR_ID);
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

              if (aComment.isDeleted ())
                aHeader.addChild ("[deleted] ");

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
                aHeader.addChild (" - ");
                aHeader.addChild (new HCSpan ().addChild (aComment.getTitle ())
                                               .addClass (CCommentCSS.CSS_CLASS_COMMENT_TITLE));
              }

              // Toolbar
              final HCSpan aCommentToolbar = new HCSpan ().addClass (CCommentCSS.CSS_CLASS_COMMENT_TOOLBAR);
              if (bIsCommentModerator)
              {
                if (!aComment.isDeleted ())
                {
                  final BootstrapButton aDeleteButton = new BootstrapButton (EBootstrapButtonSize.MINI).setIcon (EDefaultIcon.DELETE);
                  aCommentToolbar.addChild (aDeleteButton);
                  aCommentToolbar.addChild (new BootstrapTooltip (aDeleteButton).setTitle ("Delete this comment"));

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

                aCommentToolbar.addChild (BootstrapTooltip.createSimpleTooltip ("Original host: " + aComment.getHost ()));
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

              // Show the main comment
              aCommentPanel.getBody ().addClass (CCommentCSS.CSS_CLASS_SINGLE_COMMENT);
              aCommentPanel.getBody ().addChild (new HCDiv ().addChildren (HCUtils.nl2brList (aComment.getText ()))
                                                             .addClass (CCommentCSS.CSS_CLASS_COMMENT_TEXT));

              aThreadContainer.addChild (aCommentPanel);
            }
          }
        });

        // Show only thread panels which contain at least one comment
        if (aThreadContainer.hasChildren ())
          aAllComments.addChild (aThreadContainer);
      }
      ret.addChild (aAllComments);
    }

    // Create comment only for logged in users
    if (LoggedInUserManager.getInstance ().isUserLoggedInInCurrentSession ())
    {
      // Add "create comment" button
      ret.addChild (HCCommentCreate.showCreateComment (aLEC, ret.ensureID ().getID (), aObject, aFormErrors));
    }
    else
      ret.addChild (new BootstrapLabel (EBootstrapLabelType.INFO).addChild ("You must be logged in to comment!"));

    return ret;
  }
}
