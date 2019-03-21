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
package com.helger.peppol.secure;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.peppol.app.CPPApp;
import com.helger.peppol.secure.page.PageSecureAdminAddons;
import com.helger.peppol.secure.page.PageSecureCRMGroup;
import com.helger.peppol.secure.page.PageSecureCRMSubscriber;
import com.helger.peppol.secure.page.PageSecureCommentAdmin;
import com.helger.peppol.secure.page.PageSecureSMLConfiguration;
import com.helger.peppol.secure.page.PageSecureSchematronTools;
import com.helger.photon.bootstrap4.pages.BootstrapPagesMenuConfigurator;
import com.helger.photon.core.menu.IMenuItemPage;
import com.helger.photon.core.menu.IMenuTree;
import com.helger.photon.core.menu.filter.MenuObjectFilterUserAssignedToUserGroup;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uicore.page.system.BasePageShowChildren;

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

    // CRM
    {
      final IMenuItemPage aCRM = aMenuTree.createRootItem (new BasePageShowChildren <WebPageExecutionContext> (CMenuSecure.MENU_CRM,
                                                                                                               "CRM",
                                                                                                               aMenuTree));
      aMenuTree.createItem (aCRM, new PageSecureCRMGroup (CMenuSecure.MENU_CRM_GROUPS));
      aMenuTree.createItem (aCRM, new PageSecureCRMSubscriber (CMenuSecure.MENU_CRM_SUBSCRIBERS));
    }

    // Comments
    {
      aMenuTree.createRootItem (new PageSecureCommentAdmin (CMenuSecure.MENU_COMMENTS));
    }

    // Schematron tools
    {
      aMenuTree.createRootItem (new PageSecureSchematronTools (CMenuSecure.MENU_SCH_TOOLS));
    }

    // Administrator
    {
      final IMenuItemPage aAdmin = aMenuTree.createRootItem (new BasePageShowChildren <WebPageExecutionContext> (CMenuSecure.MENU_ADMIN,
                                                                                                                 "Administration",
                                                                                                                 aMenuTree))
                                            .setDisplayFilter (aFilterAdministrators);
      aMenuTree.createItem (aAdmin, new PageSecureAdminAddons (CMenuSecure.MENU_ADMIN_ADDONS));
      aMenuTree.createItem (aAdmin, new PageSecureSMLConfiguration (CMenuSecure.MENU_SML_CONFIGURATION));
      BootstrapPagesMenuConfigurator.addAllItems (aMenuTree, aAdmin, aFilterAdministrators, CPPApp.DEFAULT_LOCALE);
    }

    // Default menu item
    aMenuTree.setDefaultMenuItemID (CMenuSecure.MENU_COMMENTS);
  }
}
