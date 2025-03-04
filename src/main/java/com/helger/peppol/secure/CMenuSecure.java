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

import javax.annotation.concurrent.Immutable;

/**
 * Menu items for the secure application
 *
 * @author Philip Helger
 */
@Immutable
public final class CMenuSecure
{
  // Menu item IDs
  public static final String MENU_CRM = "crm";
  public static final String MENU_CRM_GROUPS = "crm-groups";
  public static final String MENU_CRM_SUBSCRIBERS = "crm-subscribers";

  public static final String MENU_PEPPOL = "peppol";
  public static final String MENU_PEPPOL_SEND_AS4 = "peppol-send-as4";

  public static final String MENU_COMMENTS = "comments";
  public static final String MENU_SCH_TOOLS = "schematron-tools";

  public static final String MENU_ADMIN = "admin";
  public static final String MENU_ADMIN_CHANGE_PASSWORD = "admin_change_password";
  public static final String MENU_ADMIN_ADDONS = "admin-addons";
  public static final String MENU_SML_CONFIGURATION = "sml_configuration";

  private CMenuSecure ()
  {}
}
