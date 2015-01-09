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
package com.helger.peppol.app.init;

import javax.annotation.concurrent.Immutable;

import com.helger.appbasics.security.AccessManager;
import com.helger.appbasics.security.role.RoleManager;
import com.helger.appbasics.security.user.UserManager;
import com.helger.appbasics.security.usergroup.UserGroupManager;
import com.helger.peppol.app.CApp;

@Immutable
public final class AppSecurity
{
  private AppSecurity ()
  {}

  public static void init ()
  {
    // Call before accessing AccessManager!
    RoleManager.setCreateDefaults (false);
    UserManager.setCreateDefaults (false);
    UserGroupManager.setCreateDefaults (false);

    final AccessManager aAM = AccessManager.getInstance ();

    // Standard users
    if (!aAM.containsUserWithID (CApp.USER_ADMINISTRATOR_ID))
    {
      final boolean bDisabled = false;
      aAM.createPredefinedUser (CApp.USER_ADMINISTRATOR_ID,
                                CApp.USER_ADMINISTRATOR_LOGINNAME,
                                CApp.USER_ADMINISTRATOR_EMAIL,
                                CApp.USER_ADMINISTRATOR_PASSWORD,
                                CApp.USER_ADMINISTRATOR_FIRSTNAME,
                                CApp.USER_ADMINISTRATOR_LASTNAME,
                                CApp.USER_ADMINISTRATOR_LOCALE,
                                CApp.USER_ADMINISTRATOR_CUSTOMATTRS,
                                bDisabled);
    }

    // Create all roles
    if (!aAM.containsRoleWithID (CApp.ROLE_CONFIG_ID))
      aAM.createPredefinedRole (CApp.ROLE_CONFIG_ID, CApp.ROLE_CONFIG_NAME);
    if (!aAM.containsRoleWithID (CApp.ROLE_VIEW_ID))
      aAM.createPredefinedRole (CApp.ROLE_VIEW_ID, CApp.ROLE_VIEW_NAME);
    if (!aAM.containsRoleWithID (CApp.ROLE_COMMENT_MODERATOR_ID))
      aAM.createPredefinedRole (CApp.ROLE_COMMENT_MODERATOR_ID, CApp.ROLE_COMMENT_MODERATOR_NAME);

    // User group Administrators
    if (!aAM.containsUserGroupWithID (CApp.USERGROUP_ADMINISTRATORS_ID))
    {
      aAM.createPredefinedUserGroup (CApp.USERGROUP_ADMINISTRATORS_ID, CApp.USERGROUP_ADMINISTRATORS_NAME);
      // Assign administrator user to administrators user group
      aAM.assignUserToUserGroup (CApp.USERGROUP_ADMINISTRATORS_ID, CApp.USER_ADMINISTRATOR_ID);
    }
    aAM.assignRoleToUserGroup (CApp.USERGROUP_ADMINISTRATORS_ID, CApp.ROLE_CONFIG_ID);
    aAM.assignRoleToUserGroup (CApp.USERGROUP_ADMINISTRATORS_ID, CApp.ROLE_VIEW_ID);
    aAM.assignRoleToUserGroup (CApp.USERGROUP_ADMINISTRATORS_ID, CApp.ROLE_COMMENT_MODERATOR_ID);

    // User group for Config users
    if (!aAM.containsUserGroupWithID (CApp.USERGROUP_CONFIG_ID))
      aAM.createPredefinedUserGroup (CApp.USERGROUP_CONFIG_ID, CApp.USERGROUP_CONFIG_NAME);
    aAM.assignRoleToUserGroup (CApp.USERGROUP_CONFIG_ID, CApp.ROLE_CONFIG_ID);

    // User group for View users
    if (!aAM.containsUserGroupWithID (CApp.USERGROUP_VIEW_ID))
      aAM.createPredefinedUserGroup (CApp.USERGROUP_VIEW_ID, CApp.USERGROUP_VIEW_NAME);
    aAM.assignRoleToUserGroup (CApp.USERGROUP_VIEW_ID, CApp.ROLE_VIEW_ID);
  }
}
