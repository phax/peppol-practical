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
package com.helger.peppol.app.menu;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.appbasics.app.menu.IMenuTree;
import com.helger.appbasics.app.menu.filter.MenuItemFilterNotLoggedIn;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.peppol.page.AppPageViewExternal;
import com.helger.peppol.page.pub.PagePublicLogin;

@Immutable
public final class MenuPublic
{
  private MenuPublic ()
  {}

  public static void init (@Nonnull final IMenuTree aMenuTree)
  {
    // Common stuff
    aMenuTree.createRootItem (new AppPageViewExternal (CMenuPublic.MENU_INDEX,
                                                       "Index",
                                                       new ClassPathResource ("viewpages/en/index.xml")));

    if (false)
    {
      // Not logged in
      aMenuTree.createRootSeparator ().setDisplayFilter (MenuItemFilterNotLoggedIn.getInstance ());
      aMenuTree.createRootItem (new PagePublicLogin (CMenuPublic.MENU_LOGIN))
               .setDisplayFilter (MenuItemFilterNotLoggedIn.getInstance ());
    }

    // Set default
    aMenuTree.setDefaultMenuItemID (CMenuPublic.MENU_INDEX);
  }
}
