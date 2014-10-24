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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.appbasics.security.login.LoggedInUserManager;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.table.BootstrapTableForm;
import com.helger.bootstrap3.tooltip.BootstrapTooltip;
import com.helger.commons.type.ITypedObject;
import com.helger.css.property.CCSSProperties;
import com.helger.html.hc.html.AbstractHCDiv;
import com.helger.html.hc.html.HCCol;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCTextArea;
import com.helger.html.js.builder.JSAnonymousFunction;
import com.helger.html.js.builder.JSAssocArray;
import com.helger.html.js.builder.JSExpr;
import com.helger.html.js.builder.JSStatementList;
import com.helger.html.js.builder.JSVar;
import com.helger.html.js.builder.jquery.JQuery;
import com.helger.html.js.builder.jquery.JQueryAjaxBuilder;
import com.helger.html.js.builder.jquery.JQueryInvocation;
import com.helger.peppol.app.ajax.AjaxExecutorPublicCommentCreateThread;
import com.helger.peppol.app.ajax.CAjaxPublic;
import com.helger.webbasics.ajax.response.AjaxDefaultResponse;
import com.helger.webbasics.app.layout.ILayoutExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webctrls.autosize.HCTextAreaAutosize;
import com.helger.webctrls.js.JSJQueryUtils;
import com.helger.webscopes.domain.IRequestWebScopeWithoutResponse;

public final class HCCommentCreate extends AbstractHCDiv <HCCommentCreate>
{
  public HCCommentCreate (@Nonnull final ILayoutExecutionContext aLEC,
                          @Nonnull final String sResultDivID,
                          @Nonnull final ITypedObject <String> aObject,
                          @Nullable final CommentCreationFields aCreationFieldValues)
  {
    if (aCreationFieldValues == null)
      addStyle (CCSSProperties.DISPLAY_NONE);
    addClass (CCommentCSS.CSS_CLASS_COMMENT_CREATE);

    final IRequestWebScopeWithoutResponse aRequestScope = aLEC.getRequestScope ();
    final Locale aDisplayLocale = aLEC.getDisplayLocale ();
    final boolean bLoggedInUser = LoggedInUserManager.getInstance ().isUserLoggedInInCurrentSession ();

    final BootstrapTableForm aTable = addAndReturnChild (new BootstrapTableForm (new HCCol (130),
                                                                                 HCCol.star (),
                                                                                 new HCCol (25)));
    aTable.setTitle (ECommentText.MSG_CREATE_COMMENT.getDisplayText (aDisplayLocale));

    HCEdit aEditAuthor = null;
    if (!bLoggedInUser)
    {
      aEditAuthor = new HCEdit (new RequestField ("commentAuthor")).setValue (aCreationFieldValues == null ? null
                                                                                                          : aCreationFieldValues.getAuthor ());
      aTable.createItemRow ()
            .setLabelMandatory (ECommentText.MSG_FIELD_AUTHOR.getDisplayText (aDisplayLocale))
            .setCtrl (aEditAuthor)
            .setNote (BootstrapTooltip.createSimpleTooltip (ECommentText.DESC_FIELD_AUTHOR.getDisplayText (aDisplayLocale)));
    }

    final HCEdit aEditTitle = new HCEdit (new RequestField ("commentTitle")).setValue (aCreationFieldValues == null ? null
                                                                                                                   : aCreationFieldValues.getTitle ());
    aTable.createItemRow ()
          .setLabel (ECommentText.MSG_FIELD_TITLE.getDisplayText (aDisplayLocale))
          .setCtrl (aEditTitle)
          .setNote (BootstrapTooltip.createSimpleTooltip (ECommentText.DESC_FIELD_TITLE.getDisplayText (aDisplayLocale)));

    final HCTextArea aTextAreaContent = new HCTextAreaAutosize (new RequestField ("commentText")).setRows (5)
                                                                                                 .setValue (aCreationFieldValues == null ? null
                                                                                                                                        : aCreationFieldValues.getText ());
    aTable.createItemRow ()
          .setLabelMandatory (ECommentText.MSG_FIELD_TEXT.getDisplayText (aDisplayLocale))
          .setCtrl (aTextAreaContent)
          .setNote (BootstrapTooltip.createSimpleTooltip (ECommentText.DESC_FIELD_TEXT.getDisplayText (aDisplayLocale)));

    final BootstrapButtonToolbar aToolbar = new BootstrapButtonToolbar (aLEC);
    // What to do on save?
    {
      final JSAnonymousFunction aOnSuccess = new JSAnonymousFunction ();
      final JSVar aJSData = aOnSuccess.param ("data");
      aOnSuccess.body ()
                .add (JQuery.idRef (sResultDivID).replaceWith (aJSData.ref (AjaxDefaultResponse.PROPERTY_HTML)));
      final JQueryInvocation aSaveAction = new JQueryAjaxBuilder ().cache (false)
                                                                   .url (CAjaxPublic.COMMENT_CREATE_THREAD.getInvocationURL (aRequestScope))
                                                                   .data (new JSAssocArray ().add (AjaxExecutorPublicCommentCreateThread.PARAM_OBJECT_TYPE,
                                                                                                   aObject.getTypeID ()
                                                                                                          .getObjectTypeName ())
                                                                                             .add (AjaxExecutorPublicCommentCreateThread.PARAM_OBJECT_ID,
                                                                                                   aObject.getID ())
                                                                                             .add (AjaxExecutorPublicCommentCreateThread.PARAM_AUTHOR,
                                                                                                   bLoggedInUser ? JSExpr.lit ("")
                                                                                                                : JQuery.idRef (aEditAuthor)
                                                                                                                        .val ())
                                                                                             .add (AjaxExecutorPublicCommentCreateThread.PARAM_TITLE,
                                                                                                   JQuery.idRef (aEditTitle)
                                                                                                         .val ())
                                                                                             .add (AjaxExecutorPublicCommentCreateThread.PARAM_TEXT,
                                                                                                   JQuery.idRef (aTextAreaContent)
                                                                                                         .val ()))
                                                                   .success (JSJQueryUtils.jqueryAjaxSuccessHandler (aOnSuccess,
                                                                                                                     false))
                                                                   .build ();

      aToolbar.addButtonSave (aDisplayLocale, aSaveAction);
    }

    // What to do on cancel?
    {
      final JSStatementList aCancelAction = new JSStatementList (JQuery.idRefMultiple (aEditTitle, aTextAreaContent)
                                                                       .val (""), JQuery.idRef (this).hide ());
      if (aEditAuthor != null)
        aCancelAction.add (JQuery.idRef (aEditAuthor).val (""));
      aToolbar.addButtonCancel (aDisplayLocale, aCancelAction);
    }
    addChild (aToolbar);
  }
}
