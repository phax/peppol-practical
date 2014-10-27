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
package com.helger.peppol.app.init;

import javax.annotation.Nonnull;

import com.helger.appbasics.app.locale.ILocaleManager;
import com.helger.appbasics.app.menu.IMenuTree;
import com.helger.peppol.app.CApp;
import com.helger.peppol.app.action.CActionPublic;
import com.helger.peppol.app.ajax.CAjaxPublic;
import com.helger.peppol.app.menu.MenuPublic;
import com.helger.peppol.app.ui.LayoutAreaContentProviderPublic;
import com.helger.webbasics.action.IActionInvoker;
import com.helger.webbasics.ajax.IAjaxInvoker;
import com.helger.webbasics.app.init.DefaultApplicationInitializer;
import com.helger.webbasics.app.layout.CLayout;
import com.helger.webbasics.app.layout.ILayoutManager;
import com.helger.webbasics.app.layout.LayoutExecutionContext;

/**
 * Initialize the view application stuff
 *
 * @author Philip Helger
 */
public final class InitializerPublic extends DefaultApplicationInitializer <LayoutExecutionContext>
{
  @Override
  public void initLocales (@Nonnull final ILocaleManager aLocaleMgr)
  {
    aLocaleMgr.registerLocale (CApp.LOCALE_DE);
    aLocaleMgr.registerLocale (CApp.LOCALE_EN);
    aLocaleMgr.setDefaultLocale (CApp.DEFAULT_LOCALE);
  }

  @Override
  public void initLayout (@Nonnull final ILayoutManager <LayoutExecutionContext> aLayoutMgr)
  {
    // Register all layout area handler (order is important for SEO!)
    aLayoutMgr.registerAreaContentProvider (CLayout.LAYOUT_AREAID_VIEWPORT, new LayoutAreaContentProviderPublic ());
  }

  @Override
  public void initMenu (@Nonnull final IMenuTree aMenuTree)
  {
    MenuPublic.init (aMenuTree);
  }

  @Override
  public void initAjax (@Nonnull final IAjaxInvoker aAjaxInvoker)
  {
    aAjaxInvoker.registerFunction (CAjaxPublic.COMMENT_ADD);
    aAjaxInvoker.registerFunction (CAjaxPublic.COMMENT_CREATE_THREAD);
    aAjaxInvoker.registerFunction (CAjaxPublic.COMMENT_DELETE);
    aAjaxInvoker.registerFunction (CAjaxPublic.COMMENT_SHOW_INPUT);
    aAjaxInvoker.registerFunction (CAjaxPublic.DATATABLES);
    aAjaxInvoker.registerFunction (CAjaxPublic.LOGIN);
    aAjaxInvoker.registerFunction (CAjaxPublic.UPDATE_MENU_VIEW);
  }

  @Override
  public void initActions (@Nonnull final IActionInvoker aActionInvoker)
  {
    aActionInvoker.registerAction (CActionPublic.DATATABLES_I18N);
  }

  @Override
  public void initRest ()
  {}
}
