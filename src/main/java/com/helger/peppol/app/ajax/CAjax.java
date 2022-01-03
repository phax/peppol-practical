/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import java.util.function.Predicate;

import javax.annotation.concurrent.Immutable;

import com.helger.peppol.app.CPPApp;
import com.helger.photon.ajax.decl.AjaxFunctionDeclaration;
import com.helger.photon.ajax.decl.IAjaxFunctionDeclaration;
import com.helger.photon.security.login.LoggedInUserManager;
import com.helger.photon.uictrls.datatables.ajax.AjaxExecutorDataTables;
import com.helger.photon.uictrls.datatables.ajax.AjaxExecutorDataTablesI18N;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

/**
 * This class defines the available ajax functions for the view application.
 *
 * @author Philip Helger
 */
@Immutable
public final class CAjax
{
  public static final IAjaxFunctionDeclaration DATATABLES = AjaxFunctionDeclaration.builder ("dataTables")
                                                                                   .executor (AjaxExecutorDataTables.class)
                                                                                   .build ();
  public static final IAjaxFunctionDeclaration DATATABLES_I18N = AjaxFunctionDeclaration.builder ("datatables-i18n")
                                                                                        .executor (new AjaxExecutorDataTablesI18N (CPPApp.DEFAULT_LOCALE))
                                                                                        .build ();
  public static final IAjaxFunctionDeclaration LOGIN = AjaxFunctionDeclaration.builder ("login")
                                                                              .executor (AjaxExecutorPublicLogin.class)
                                                                              .build ();
  public static final IAjaxFunctionDeclaration UPDATE_MENU_VIEW_PUB = AjaxFunctionDeclaration.builder ("updateMenuViewPub")
                                                                                             .executor (AjaxExecutorPublicUpdateMenuView.class)
                                                                                             .build ();
  private static final Predicate <? super IRequestWebScopeWithoutResponse> FILTER_LOGIN = x -> LoggedInUserManager.getInstance ()
                                                                                                                  .isUserLoggedInInCurrentSession ();
  public static final IAjaxFunctionDeclaration UPDATE_MENU_VIEW_SEC = AjaxFunctionDeclaration.builder ("updateMenuViewSec")
                                                                                             .executor (AjaxExecutorSecureUpdateMenuView.class)
                                                                                             .filter (FILTER_LOGIN)
                                                                                             .build ();
  public static final IAjaxFunctionDeclaration COMMENT_ADD = AjaxFunctionDeclaration.builder ("addComment")
                                                                                    .executor (AjaxExecutorCommentAdd.class)
                                                                                    .filter (FILTER_LOGIN)
                                                                                    .build ();
  public static final IAjaxFunctionDeclaration COMMENT_CREATE_THREAD = AjaxFunctionDeclaration.builder ("createThread")
                                                                                              .executor (AjaxExecutorCommentCreateThread.class)
                                                                                              .filter (FILTER_LOGIN)
                                                                                              .build ();
  public static final IAjaxFunctionDeclaration COMMENT_DELETE = AjaxFunctionDeclaration.builder ("deleteComment")
                                                                                       .executor (AjaxExecutorCommentDelete.class)
                                                                                       .filter (FILTER_LOGIN)
                                                                                       .build ();
  public static final IAjaxFunctionDeclaration COMMENT_SHOW_INPUT = AjaxFunctionDeclaration.builder ("showInputForm")
                                                                                           .executor (AjaxExecutorCommentShowInput.class)
                                                                                           .filter (FILTER_LOGIN)
                                                                                           .build ();

  private CAjax ()
  {}
}
