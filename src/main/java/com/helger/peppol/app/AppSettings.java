/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.UsedViaReflection;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.exception.InitializationException;
import com.helger.scope.singleton.AbstractGlobalSingleton;
import com.helger.settings.ISettings;
import com.helger.settings.exchange.configfile.ConfigFile;
import com.helger.settings.exchange.configfile.ConfigFileBuilder;

/**
 * This class provides access to the settings as contained in the
 * <code>webapp.properties</code> file.
 *
 * @author Philip Helger
 */
public final class AppSettings extends AbstractGlobalSingleton
{
  /** The name of the file containing the settings */
  public static final String FILENAME = "webapp.properties";
  private static final ConfigFile s_aCF;

  static
  {
    s_aCF = new ConfigFileBuilder ().addPath ("private-webapp.properties").addPath (FILENAME).build ();
    if (!s_aCF.isRead ())
      throw new InitializationException ("Failed to init properties");
  }

  @Deprecated
  @UsedViaReflection
  private AppSettings ()
  {}

  @Nonnull
  public static ISettings getSettingsObject ()
  {
    return s_aCF.getSettings ();
  }

  @Nullable
  public static String getGlobalDebug ()
  {
    return s_aCF.getAsString ("global.debug");
  }

  @Nullable
  public static String getGlobalProduction ()
  {
    return s_aCF.getAsString ("global.production");
  }

  @Nullable
  public static String getDataPath ()
  {
    return s_aCF.getAsString ("webapp.datapath");
  }

  public static boolean isCheckFileAccess ()
  {
    return s_aCF.getAsBoolean ("webapp.checkfileaccess", true);
  }

  public static boolean isTestVersion ()
  {
    return s_aCF.getAsBoolean ("webapp.testversion", GlobalDebug.isDebugMode ());
  }

  public static boolean isWebPageCommentingEnabled ()
  {
    return s_aCF.getAsBoolean ("webapp.pagecomments.enabled", false);
  }

  public static boolean isRestLogExceptions ()
  {
    return s_aCF.getAsBoolean ("rest.log.exceptions", GlobalDebug.isDebugMode ());
  }

  public static boolean isRestExceptionsWithPayload ()
  {
    return s_aCF.getAsBoolean ("rest.exceptions.payload", GlobalDebug.isDebugMode ());
  }

  public static long getRESTAPIMaxRequestsPerSecond ()
  {
    return s_aCF.getAsLong ("rest.limit.requestspersecond", -1);
  }

  @Nullable
  public static String getRecaptchaWebKey ()
  {
    return s_aCF.getAsString ("recaptcha.webkey");
  }

  @Nullable
  public static String getRecaptchaSecretKey ()
  {
    return s_aCF.getAsString ("recaptcha.secretkey");
  }

  public static long getValidationAPIMaxRequestsPerSecond ()
  {
    return s_aCF.getAsLong ("validation.limit.requestspersecond", -1);
  }
}
