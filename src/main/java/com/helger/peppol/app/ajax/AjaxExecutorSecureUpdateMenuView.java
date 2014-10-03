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

import javax.annotation.Nonnull;

import com.helger.html.hc.IHCNode;
import com.helger.peppol.app.ui.LayoutAreaContentProviderSecure;
import com.helger.webbasics.ajax.executor.AbstractAjaxExecutor;
import com.helger.webbasics.ajax.response.AjaxDefaultResponse;
import com.helger.webbasics.ajax.response.IAjaxResponse;
import com.helger.webbasics.app.layout.LayoutExecutionContext;
import com.helger.webscopes.domain.IRequestWebScopeWithoutResponse;

/**
 * Ajax executor to get the update content the secure application's menu.
 *
 * @author Philip Helger
 */
public final class AjaxExecutorSecureUpdateMenuView extends AbstractAjaxExecutor
{
  @Override
  @Nonnull
  protected IAjaxResponse mainHandleRequest (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope) throws Exception
  {
    final LayoutExecutionContext aLEC = LayoutExecutionContext.createForAjaxOrAction (aRequestScope);

    // Get the rendered content of the menu area
    final IHCNode aRoot = LayoutAreaContentProviderSecure.getMenuContent (aLEC);

    // Set as result property
    return AjaxDefaultResponse.createSuccess (aRequestScope, aRoot);
  }
}
