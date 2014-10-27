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
import com.helger.bootstrap3.label.BootstrapLabel;
import com.helger.bootstrap3.label.EBootstrapLabelType;
import com.helger.bootstrap3.panel.BootstrapPanel;
import com.helger.bootstrap3.tooltip.BootstrapTooltip;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.idfactory.GlobalIDFactory;
import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ITypedObject;
import com.helger.datetime.format.PDTToString;
import com.helger.html.hc.html.AbstractHCDiv;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCSpan;
import com.helger.html.hc.htmlext.HCUtils;
import com.helger.html.js.builder.jquery.JQuery;
import com.helger.peppol.app.CApp;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.comment.domain.ComparatorCommentThreadCreationDateTime;
import com.helger.peppol.comment.domain.IComment;
import com.helger.peppol.comment.domain.ICommentIterationCallback;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.webbasics.app.layout.ILayoutExecutionContext;

public class HCCommentShow extends AbstractHCDiv <HCCommentShow>
{
  public HCCommentShow (@Nonnull final ILayoutExecutionContext aLEC, @Nonnull final ITypedObject <String> aObject)
  {
    this (aLEC, aObject, null);
  }

  public HCCommentShow (@Nonnull final ILayoutExecutionContext aLEC,
                        @Nonnull final ITypedObject <String> aObject,
                        @Nullable final CommentCreationFields aCreationFieldValues)
  {
    ValueEnforcer.notNull (aLEC, "LEC");
    ValueEnforcer.notNull (aObject, "Object");

    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final String sContainerID = GlobalIDFactory.getNewStringID ();
    setID (sContainerID);

    // Get all existing comments
    final List <ICommentThread> aComments = CommentThreadManager.getInstance ().getCommentThreadsOfObject (aObject);
    if (ContainerHelper.isNotEmpty (aComments))
    {
      final IUserManager aUserMgr = AccessManager.getInstance ();
      final HCDiv aAllComments = new HCDiv ().addClass (CCommentCSS.CSS_CLASS_COMMENT_CONTAINER);
      for (final ICommentThread aCommentThread : ContainerHelper.getSorted (aComments,
                                                                            new ComparatorCommentThreadCreationDateTime ()))
      {
        final BootstrapPanel aPanel = aAllComments.addAndReturnChild (new BootstrapPanel ());
        aPanel.addClass (CCommentCSS.CSS_CLASS_COMMENT_THREAD);

        final boolean bIsCommentModerator = SecurityUtils.hasCurrentUserRole (CApp.ROLE_COMMENT_MODERATOR_ID);
        aCommentThread.iterateAllComments (new ICommentIterationCallback ()
        {
          public void onComment (final int nLevel,
                                 @Nullable final IComment aParentComment,
                                 @Nonnull final IComment aComment)
          {
            // Show only approved comments
            if (aComment.getState ().isApproved ())
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
              final HCDiv aHeader = aPanel.getOrCreateHeader ();

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
              final HCSpan aToolbarDiv = new HCSpan ().addClass (CCommentCSS.CSS_CLASS_COMMENT_TOOLBAR);
              if (bIsCommentModerator)
                aToolbarDiv.addChild (BootstrapTooltip.createSimpleTooltip ("Original host: " + aComment.getHost ()));
              if (aToolbarDiv.hasChildren ())
                aHeader.addChild (aToolbarDiv);

              // Last modification
              if (aComment.getLastModificationDateTime () != null)
                aHeader.addChild (new HCDiv ().addChild (ECommentText.MSG_LAST_MODIFICATION.getDisplayTextWithArgs (aDisplayLocale,
                                                                                                                    Integer.valueOf (aComment.getEditCount ()),
                                                                                                                    PDTToString.getAsString (aComment.getLastModificationDateTime (),
                                                                                                                                             aDisplayLocale)))
                                              .addClass (CCommentCSS.CSS_CLASS_COMMENT_LAST_MODIFICATION));

              // Show the main comment
              aPanel.getBody ().addClass (CCommentCSS.CSS_CLASS_SINGLE_COMMENT);
              aPanel.getBody ().addChild (new HCDiv ().addChildren (HCUtils.nl2brList (aComment.getText ()))
                                                      .addClass (CCommentCSS.CSS_CLASS_COMMENT_TEXT));
            }
          }
        });
      }
      addChild (aAllComments);
    }

    // Create comment only for logged in users
    if (LoggedInUserManager.getInstance ().isUserLoggedInInCurrentSession ())
    {
      // Add "create comment" button
      final BootstrapButton aButtonCreate = new BootstrapButton ().addChild (ECommentText.MSG_CREATE_COMMENT.getDisplayText (aDisplayLocale));
      final HCCommentCreate aCommentCreate = new HCCommentCreate (aLEC, sContainerID, aObject, aCreationFieldValues);
      aButtonCreate.setOnClick (JQuery.idRef (aCommentCreate).toggle ());
      addChild (aButtonCreate);
      addChild (aCommentCreate);
    }
    else
      addChild (new BootstrapLabel (EBootstrapLabelType.INFO).addChild ("You must be logged in to comment!"));
  }
}
