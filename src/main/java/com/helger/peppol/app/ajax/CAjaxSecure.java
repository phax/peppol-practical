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
package com.helger.peppol.app.ajax;

import javax.annotation.concurrent.Immutable;

import com.helger.webbasics.ajax.IAjaxFunctionDeclaration;
import com.helger.webbasics.ajax.decl.SecureApplicationAjaxFunctionDeclaration;
import com.helger.webbasics.form.ajax.AjaxExecutorSaveFormState;

/**
 * This class defines the available ajax functions for the config app
 *
 * @author Philip Helger
 */
@Immutable
public final class CAjaxSecure
{
  public static final IAjaxFunctionDeclaration SAVE_FORM_STATE = new SecureApplicationAjaxFunctionDeclaration ("saveFormState",
                                                                                                               AjaxExecutorSaveFormState.class);
  public static final IAjaxFunctionDeclaration UPDATE_MENU_VIEW = new SecureApplicationAjaxFunctionDeclaration ("updateMenuView",
                                                                                                                AjaxExecutorSecureUpdateMenuView.class);

  private CAjaxSecure ()
  {}
}
