/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import com.helger.annotation.concurrent.Immutable;
import com.helger.peppol.app.CPPApp;
import com.helger.peppol.photon.smlconfig.page.PageSecureSMLConfiguration;
import com.helger.peppol.sharedui.page.secure.PageSecurePeppolSendAS4;
import com.helger.photon.bootstrap4.pages.BootstrapPagesMenuConfigurator;
import com.helger.photon.bootstrap4.pages.security.BasePageSecurityChangePassword;
import com.helger.photon.core.menu.IMenuItemPage;
import com.helger.photon.core.menu.IMenuTree;
import com.helger.photon.core.menu.filter.MenuObjectFilterUserAssignedToUserGroup;
import com.helger.photon.core.menu.filter.MenuObjectFilterUserHasRole;
import com.helger.photon.uicore.page.system.BasePageShowChildren;

import jakarta.annotation.Nonnull;

@Immutable
public final class MenuSecure
{
  private MenuSecure ()
  {}

  public static void init (@Nonnull final IMenuTree aMenuTree)
  {
    // We need this additional indirection layer, as the pages are initialized
    // statically!
    final MenuObjectFilterUserAssignedToUserGroup aFilterAdministrators = new MenuObjectFilterUserAssignedToUserGroup (CPPApp.USERGROUP_ADMINISTRATORS_ID);
    final MenuObjectFilterUserHasRole aFilterPeppolSenders = new MenuObjectFilterUserHasRole (CPPApp.ROLE_PEPPOL_SENDERS_ID);

    // CRM
    {
      final IMenuItemPage aCRM = aMenuTree.createRootItem (new BasePageShowChildren <> (CMenuSecure.MENU_CRM,
                                                                                        "CRM",
                                                                                        aMenuTree))
                                          .setDisplayFilter (aFilterAdministrators);
      aMenuTree.createItem (aCRM, new PageSecureCRMGroup (CMenuSecure.MENU_CRM_GROUPS))
               .setDisplayFilter (aFilterAdministrators);
      aMenuTree.createItem (aCRM, new PageSecureCRMSubscriber (CMenuSecure.MENU_CRM_SUBSCRIBERS))
               .setDisplayFilter (aFilterAdministrators);
    }

    // Peppol
    {
      final IMenuItemPage aPeppol = aMenuTree.createRootItem (new BasePageShowChildren <> (CMenuSecure.MENU_PEPPOL,
                                                                                           "Peppol",
                                                                                           aMenuTree));
      aMenuTree.createItem (aPeppol, new PageSecurePeppolSendAS4 (CMenuSecure.MENU_PEPPOL_SEND_AS4))
               .setDisplayFilter (aFilterPeppolSenders);
    }

    // Comments
    {
      aMenuTree.createRootItem (new PageSecureCommentAdmin (CMenuSecure.MENU_COMMENTS))
               .setDisplayFilter (aFilterAdministrators);
    }

    // Schematron tools
    {
      aMenuTree.createRootItem (new PageSecureSchematronTools (CMenuSecure.MENU_SCH_TOOLS))
               .setDisplayFilter (aFilterAdministrators);
    }

    // Administrator
    {
      final IMenuItemPage aAdmin = aMenuTree.createRootItem (new BasePageShowChildren <> (CMenuSecure.MENU_ADMIN,
                                                                                          "Administration",
                                                                                          aMenuTree));
      // Must be accessible for all Config users
      aMenuTree.createItem (aAdmin, new BasePageSecurityChangePassword <> (CMenuSecure.MENU_ADMIN_CHANGE_PASSWORD));

      // Admins only
      aMenuTree.createItem (aAdmin, new PageSecureAdminAddons (CMenuSecure.MENU_ADMIN_ADDONS))
               .setDisplayFilter (aFilterAdministrators);
      aMenuTree.createItem (aAdmin, new PageSecureSMLConfiguration (CMenuSecure.MENU_SML_CONFIGURATION))
               .setDisplayFilter (aFilterAdministrators);
      BootstrapPagesMenuConfigurator.addAllItems (aMenuTree, aAdmin, aFilterAdministrators, CPPApp.DEFAULT_LOCALE);
    }

    // Default menu item
    aMenuTree.setDefaultMenuItemID (CMenuSecure.MENU_COMMENTS);
  }
}
