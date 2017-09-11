/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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

import javax.annotation.concurrent.Immutable;

import com.helger.photon.core.ajax.IAjaxFunctionDeclaration;

/**
 * This class defines the available ajax functions for comments
 *
 * @author Philip Helger
 */
@Immutable
public final class CAjaxComment
{
  public static final IAjaxFunctionDeclaration COMMENT_ADD = new CommentAjaxFunctionDeclarationBuilder ("addComment").withExecutor (AjaxExecutorCommentAdd.class)
                                                                                                                     .build ();
  public static final IAjaxFunctionDeclaration COMMENT_CREATE_THREAD = new CommentAjaxFunctionDeclarationBuilder ("createThread").withExecutor (AjaxExecutorCommentCreateThread.class)
                                                                                                                                 .build ();
  public static final IAjaxFunctionDeclaration COMMENT_DELETE = new CommentAjaxFunctionDeclarationBuilder ("deleteComment").withExecutor (AjaxExecutorCommentDelete.class)
                                                                                                                           .build ();
  public static final IAjaxFunctionDeclaration COMMENT_SHOW_INPUT = new CommentAjaxFunctionDeclarationBuilder ("showInputForm").withExecutor (AjaxExecutorCommentShowInput.class)
                                                                                                                               .build ();

  private CAjaxComment ()
  {}
}
