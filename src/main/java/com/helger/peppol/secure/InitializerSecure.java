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
package com.helger.peppol.secure;

import javax.annotation.Nonnull;

import com.helger.photon.basic.app.menu.IMenuTree;
import com.helger.photon.core.app.context.LayoutExecutionContext;
import com.helger.photon.core.app.init.IApplicationInitializer;
import com.helger.photon.core.app.layout.CLayout;
import com.helger.photon.core.app.layout.ILayoutManager;

/**
 * Initialize the config application stuff
 *
 * @author Philip Helger
 */
public final class InitializerSecure implements IApplicationInitializer <LayoutExecutionContext>
{
  @Override
  public void initLayout (@Nonnull final ILayoutManager <LayoutExecutionContext> aLayoutMgr)
  {
    aLayoutMgr.registerAreaContentProvider (CLayout.LAYOUT_AREAID_VIEWPORT, new LayoutAreaContentProviderSecure ());
  }

  @Override
  public void initMenu (@Nonnull final IMenuTree aMenuTree)
  {
    MenuSecure.init (aMenuTree);
  }
}
