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
import com.helger.appbasics.security.user.IUser;
import com.helger.appbasics.security.user.IUserManager;
import com.helger.bootstrap3.button.BootstrapButton;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.idfactory.GlobalIDFactory;
import com.helger.commons.string.StringHelper;
import com.helger.commons.type.ITypedObject;
import com.helger.css.ECSSUnit;
import com.helger.css.property.CCSSProperties;
import com.helger.datetime.format.PDTToString;
import com.helger.html.hc.html.AbstractHCDiv;
import com.helger.html.hc.html.HCCol;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCRow;
import com.helger.html.hc.html.HCTable;
import com.helger.html.hc.htmlext.HCUtils;
import com.helger.html.js.builder.jquery.JQuery;
import com.helger.peppol.comment.domain.CommentThreadManager;
import com.helger.peppol.comment.domain.ComparatorCommentThreadCreationDateTime;
import com.helger.peppol.comment.domain.IComment;
import com.helger.peppol.comment.domain.ICommentIterationCallback;
import com.helger.peppol.comment.domain.ICommentThread;
import com.helger.webbasics.app.layout.ILayoutExecutionContext;

public class HCCommentShow extends AbstractHCDiv <HCCommentShow>
{
  /**
   * @param aCommentThread
   *        Parent thread
   * @param aComment
   *        Comment
   * @param aContainer
   *        HC node to be filled
   */
  private static void _fillPerCommentToolbar (final ICommentThread aCommentThread,
                                              final IComment aComment,
                                              final HCDiv aContainer)
  {
    // TODO
  }

  public HCCommentShow (@Nonnull final ILayoutExecutionContext aLEC, @Nonnull final ITypedObject <String> aObject)
  {
    this (aLEC, aObject, null);
  }

  public HCCommentShow (@Nonnull final ILayoutExecutionContext aLEC,
                        @Nonnull final ITypedObject <String> aObject,
                        @Nullable final CommentCreationFields aCreationFieldValues)
  {
    ValueEnforcer.notNull (aObject, "Object");

    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final String sContainerID = GlobalIDFactory.getNewStringID ();
    setID (sContainerID);

    // Get all existing comments
    final List <ICommentThread> aComments = CommentThreadManager.getInstance ().getCommentThreadsOfObject (aObject);
    if (ContainerHelper.isNotEmpty (aComments))
    {
      final IUserManager aUserMgr = AccessManager.getInstance ();
      final HCTable aTable = new HCTable (HCCol.star ());
      aTable.addBodyRow ()
            .addAndReturnCell (ECommentText.MSG_EXISTING_COMMENTS.getDisplayText (aDisplayLocale))
            .setColspan (aTable.getColumnCount ());
      aTable.addClass (CCommentCSS.CSS_CLASS_COMMENT_VIEW);
      for (final ICommentThread aCommentThread : ContainerHelper.getSorted (aComments,
                                                                            new ComparatorCommentThreadCreationDateTime ()))
      {
        final HCRow aRow = aTable.addBodyRow ();
        final HCDiv aThreadDiv = aRow.addCell ().addAndReturnChild (new HCDiv ());
        aThreadDiv.addClass (CCommentCSS.CSS_CLASS_COMMENT_THREAD);

        aCommentThread.iterateAllComments (new ICommentIterationCallback ()
        {
          public void onComment (final int nLevel,
                                 @Nullable final IComment aParentComment,
                                 @Nonnull final IComment aComment)
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

            // Build toolbar
            final HCDiv aToolbarDiv = new HCDiv ().addClass (CCommentCSS.CSS_CLASS_TOOLBAR);
            _fillPerCommentToolbar (aCommentThread, aComment, aToolbarDiv);

            // Show the main comment
            final HCDiv aCommentDiv = aThreadDiv.addAndReturnChild (new HCDiv ().addClass (CCommentCSS.CSS_CLASS_COMMENT));
            if (bRegisteredUser)
              aCommentDiv.addClass (CCommentCSS.CSS_CLASS_REGISTERED_USER);
            aCommentDiv.addStyle (CCSSProperties.MARGIN_LEFT.newValue (ECSSUnit.em (nLevel)));
            aCommentDiv.addChild (aToolbarDiv);
            aCommentDiv.addChild (new HCDiv ().addChild (sAuthor).addClass (CCommentCSS.CSS_CLASS_AUTHOR));
            aCommentDiv.addChild (new HCDiv ().addChild (PDTToString.getAsString (aComment.getCreationDateTime (),
                                                                                  aDisplayLocale))
                                              .addClass (CCommentCSS.CSS_CLASS_CREATIONDT));
            if (aComment.getLastModificationDateTime () != null)
              aCommentDiv.addChild (new HCDiv ().addChild (ECommentText.MSG_EDITED.getDisplayText (aDisplayLocale))
                                                .addClass (CCommentCSS.CSS_CLASS_EDITED));
            if (StringHelper.hasText (aComment.getTitle ()))
              aCommentDiv.addChild (new HCDiv ().addChild (aComment.getTitle ()).addClass (CCommentCSS.CSS_CLASS_TITLE));
            aCommentDiv.addChild (new HCDiv ().addChildren (HCUtils.nl2brList (aComment.getText ()))
                                              .addClass (CCommentCSS.CSS_CLASS_TEXT));
          }
        });
      }
      addChild (aTable);
    }

    // Add "create comment" button
    final BootstrapButton aButtonCreate = new BootstrapButton ().addChild (ECommentText.MSG_CREATE_COMMENT.getDisplayText (aDisplayLocale));
    final HCCommentCreate aCommentCreate = new HCCommentCreate (aLEC, sContainerID, aObject, aCreationFieldValues);
    aButtonCreate.setOnClick (JQuery.idRef (aCommentCreate).toggle ());
    addChild (aButtonCreate);
    addChild (aCommentCreate);
  }
}
