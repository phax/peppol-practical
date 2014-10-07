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
import com.helger.appbasics.app.menu.IMenuObject;
import com.helger.appbasics.app.menu.IMenuTree;
import com.helger.appbasics.app.menu.filter.AbstractMenuObjectFilter;
import com.helger.appbasics.app.menu.filter.MenuItemFilterUserAssignedToUserGroup;
import com.helger.peppol.app.CApp;
import com.helger.peppol.page.secure.PageCRMGroup;
import com.helger.peppol.page.secure.PageCRMSubscriber;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.app.page.system.PageShowChildren;
import com.helger.webbasics.form.FormStateManager;
import com.helger.webpages.DefaultMenuConfigurator;
import com.helger.webpages.form.BasePageSavedStates;

@Immutable
public final class MenuSecure
{
  private MenuSecure ()
  {}

  public static void init (@Nonnull final IMenuTree aMenuTree)
  {
    // We need this additional indirection layer, as the pages are initialized
    // statically!
    final MenuItemFilterUserAssignedToUserGroup aFilterAdministrators = new MenuItemFilterUserAssignedToUserGroup (CApp.USERGROUP_ADMINISTRATORS_ID);

    // CRM
    {
      final IMenuItemPage aCRM = aMenuTree.createRootItem (new PageShowChildren <WebPageExecutionContext> (CMenuSecure.MENU_CRM,
                                                                                                           "CRM",
                                                                                                           aMenuTree));
      aMenuTree.createItem (aCRM, new PageCRMGroup (CMenuSecure.MENU_CRM_GROUPS));
      aMenuTree.createItem (aCRM, new PageCRMSubscriber (CMenuSecure.MENU_CRM_SUBSCRIBERS));
    }

    // Administrator
    {
      final IMenuItemPage aAdmin = aMenuTree.createRootItem (new PageShowChildren <WebPageExecutionContext> (CMenuSecure.MENU_ADMIN,
                                                                                                             "Administration",
                                                                                                             aMenuTree))
                                            .setDisplayFilter (aFilterAdministrators);

      DefaultMenuConfigurator.addSecurityItems (aMenuTree, aAdmin, aFilterAdministrators, CApp.DEFAULT_LOCALE);
      DefaultMenuConfigurator.addMonitoringItems (aMenuTree, aAdmin, aFilterAdministrators);
      DefaultMenuConfigurator.addSysInfoItems (aMenuTree, aAdmin, aFilterAdministrators);
      DefaultMenuConfigurator.addDataItems (aMenuTree, aAdmin, aFilterAdministrators);
      DefaultMenuConfigurator.addSettingsItems (aMenuTree, aAdmin, aFilterAdministrators);
    }

    // Saved states
    final AbstractMenuObjectFilter aFilterSavedStates = new AbstractMenuObjectFilter ()
    {
      public boolean matchesFilter (final IMenuObject aValue)
      {
        // Show always after a form state was once stored
        return FormStateManager.getInstance ().containedOnceAFormState ();
      }
    };
    aMenuTree.createRootSeparator ().setDisplayFilter (aFilterSavedStates);
    aMenuTree.createRootItem (new BasePageSavedStates <WebPageExecutionContext> (CMenuSecure.MENU_SAVED_STATES,
                                                                                 "Saved objects"))
             .setDisplayFilter (aFilterSavedStates);

    // Default menu item
    aMenuTree.setDefaultMenuItemID (CMenuSecure.MENU_ADMIN);
  }
}
