/**
 * Copyright (C) 2014-2019 Philip Helger (www.helger.com)
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
import com.helger.peppol.secure.LayoutAreaContentProviderSecure;
import com.helger.photon.ajax.executor.IAjaxExecutor;
import com.helger.photon.app.PhotonUnifiedResponse;
import com.helger.photon.core.execcontext.LayoutExecutionContext;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

/**
 * Ajax executor to get the update content the secure application's menu.
 *
 * @author Philip Helger
 */
public final class AjaxExecutorSecureUpdateMenuView implements IAjaxExecutor
{
  public void handleRequest (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                             @Nonnull final PhotonUnifiedResponse aAjaxResponse) throws Exception
  {
    final LayoutExecutionContext aLEC = LayoutExecutionContext.createForAjaxOrAction (aRequestScope);

    // Get the rendered content of the menu area
    final IHCNode aRoot = LayoutAreaContentProviderSecure.getMenuContent (aLEC);

    // Set as result property
    aAjaxResponse.html (aRoot);
  }
}
