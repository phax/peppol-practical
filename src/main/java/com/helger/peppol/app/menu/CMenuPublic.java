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

import javax.annotation.concurrent.Immutable;

/**
 * Menu items for the public application
 * 
 * @author Philip Helger
 */
@Immutable
public final class CMenuPublic
{
  public static final String MENU_LOGIN = "login";

  public static final String MENU_INDEX = "index";
  public static final String MENU_VALIDATION = "validation";
  public static final String MENU_VALIDATION_WS = "validation_ws";
  public static final String MENU_NEWSLETTER_SUBSCRIBE = "newsletter_subscribe";
  public static final String MENU_NEWSLETTER_UNSUBSCRIBE = "newsletter_unsubscribe";

  // flags
  public static final String FLAG_FOOTER_COL1 = "footercol1";
  public static final String FLAG_FOOTER_COL2 = "footercol2";
  public static final String FLAG_FOOTER_COL3 = "footercol3";

  private CMenuPublic ()
  {}
}
