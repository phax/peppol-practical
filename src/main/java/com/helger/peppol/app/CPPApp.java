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
package com.helger.peppol.app;

import java.util.List;
import java.util.Locale;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotation.CodingStyleguideUnaware;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.type.ObjectType;
import com.helger.peppol.sharedui.CSharedUI;
import com.helger.photon.security.CSecurity;

/**
 * Contains application wide constants.
 *
 * @author Philip Helger
 */
@Immutable
public final class CPPApp
{
  public static final Locale DEFAULT_LOCALE = CSharedUI.LOCALE_EN;

  public static final String DEFAULT_USER_AGENT = "Peppol-Practical/1.0";

  // Security roles
  public static final String ROLE_VIEW_ID = "view";
  public static final String ROLE_VIEW_NAME = "View user";
  public static final String ROLE_VIEW_DESCRIPTION = null;
  public static final ICommonsMap <String, String> ROLE_VIEW_CUSTOMATTRS = null;
  public static final String ROLE_COMMENT_MODERATOR_ID = "commentmod";
  public static final String ROLE_COMMENT_MODERATOR_NAME = "Comment moderator";
  public static final String ROLE_COMMENT_MODERATOR_DESCRIPTION = null;
  public static final ICommonsMap <String, String> ROLE_COMMENT_MODERATOR_CUSTOMATTRS = null;
  public static final String ROLE_PEPPOL_SENDERS_ID = "peppolsenders";
  public static final String ROLE_PEPPOL_SENDERS_NAME = "Peppol Sender";
  public static final String ROLE_PEPPOL_SENDERS_DESCRIPTION = null;
  public static final ICommonsMap <String, String> ROLE_PEPPOL_SENDERS_CUSTOMATTRS = null;

  @CodingStyleguideUnaware
  public static final List <String> REQUIRED_ROLE_IDS_VIEW = new CommonsArrayList <> (ROLE_VIEW_ID).getAsUnmodifiable ();

  // User groups
  public static final String USERGROUP_ADMINISTRATORS_ID = CSecurity.USERGROUP_ADMINISTRATORS_ID;
  public static final String USERGROUP_ADMINISTRATORS_NAME = CSecurity.USERGROUP_ADMINISTRATORS_NAME;
  public static final String USERGROUP_ADMINISTRATORS_DESCRIPTION = null;
  public static final ICommonsMap <String, String> USERGROUP_ADMINISTRATORS_CUSTOMATTRS = null;
  public static final String USERGROUP_CONFIG_ID = "ugconfig";
  public static final String USERGROUP_CONFIG_NAME = "Config user";
  public static final String USERGROUP_CONFIG_DESCRIPTION = null;
  public static final ICommonsMap <String, String> USERGROUP_CONFIG_CUSTOMATTRS = null;
  public static final String USERGROUP_VIEW_ID = "ugview";
  public static final String USERGROUP_VIEW_NAME = "View user";
  public static final String USERGROUP_VIEW_DESCRIPTION = null;
  public static final ICommonsMap <String, String> USERGROUP_VIEW_CUSTOMATTRS = null;

  // User ID
  public static final String USER_ADMINISTRATOR_ID = CSecurity.USER_ADMINISTRATOR_ID;
  public static final String USER_ADMINISTRATOR_LOGINNAME = CSecurity.USER_ADMINISTRATOR_EMAIL;
  public static final String USER_ADMINISTRATOR_EMAIL = CSecurity.USER_ADMINISTRATOR_EMAIL;
  public static final String USER_ADMINISTRATOR_PASSWORD = CSecurity.USER_ADMINISTRATOR_PASSWORD;
  public static final String USER_ADMINISTRATOR_FIRSTNAME = null;
  public static final String USER_ADMINISTRATOR_LASTNAME = CSecurity.USER_ADMINISTRATOR_NAME;
  public static final String USER_ADMINISTRATOR_DESCRIPTION = null;
  public static final Locale USER_ADMINISTRATOR_LOCALE = CPPApp.DEFAULT_LOCALE;
  public static final ICommonsMap <String, String> USER_ADMINISTRATOR_CUSTOMATTRS = null;

  public static final ObjectType OT_PAGE = new ObjectType ("webpage");

  private CPPApp ()
  {}
}
