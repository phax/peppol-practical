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

import com.helger.appbasics.app.menu.IMenuItemPage;
import com.helger.appbasics.app.menu.IMenuTree;
import com.helger.appbasics.app.menu.filter.MenuItemFilterNotLoggedIn;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.peppol.page.AppPageViewExternal;
import com.helger.peppol.page.pub.PagePublicLogin;
import com.helger.peppol.page.pub.PagePublicNewsletterSubscribe;
import com.helger.peppol.page.pub.PagePublicNewsletterUnsubscribe;
import com.helger.peppol.page.pub.PagePublicParticipantInformation;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.app.page.system.PageShowChildren;

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

    // Setup stuff
    {
      final IMenuItemPage aSetup = aMenuTree.createRootItem (new PageShowChildren <WebPageExecutionContext> (CMenuPublic.MENU_SETUP,
                                                                                                             "Setup guides",
                                                                                                             aMenuTree));
      aMenuTree.createItem (aSetup, new AppPageViewExternal (CMenuPublic.MENU_SETUP_AP,
                                                             "Setup PEPPOL AP",
                                                             new ClassPathResource ("viewpages/en/setup_ap.xml")));
      aMenuTree.createItem (aSetup, new AppPageViewExternal (CMenuPublic.MENU_SETUP_SMP,
                                                             "Setup PEPPOL SMP",
                                                             new ClassPathResource ("viewpages/en/setup_smp.xml")));
    }

    // Tools stuff
    {
      final IMenuItemPage aSetup = aMenuTree.createRootItem (new PageShowChildren <WebPageExecutionContext> (CMenuPublic.MENU_TOOLS,
                                                                                                             "Tools",
                                                                                                             aMenuTree));
      aMenuTree.createItem (aSetup, new PagePublicParticipantInformation (CMenuPublic.MENU_TOOLS_PARTICIPANT));
    }

    // Validation stuff
    {
      final IMenuItemPage aValidation = aMenuTree.createRootItem (new PageShowChildren <WebPageExecutionContext> (CMenuPublic.MENU_VALIDATION,
                                                                                                                  "Validation",
                                                                                                                  aMenuTree));
      aMenuTree.createItem (aValidation, new AppPageViewExternal (CMenuPublic.MENU_VALIDATION_WS,
                                                                  "PEPPOL document validation WebService",
                                                                  new ClassPathResource ("viewpages/en/ws_docval.xml")));
    }

    // Newsletter stuff
    {
      aMenuTree.createRootItem (new PagePublicNewsletterSubscribe (CMenuPublic.MENU_NEWSLETTER_SUBSCRIBE));
      aMenuTree.createRootItem (new PagePublicNewsletterUnsubscribe (CMenuPublic.MENU_NEWSLETTER_UNSUBSCRIBE))
               .setAttribute (CMenuPublic.FLAG_FOOTER_COL1, true);
    }

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
