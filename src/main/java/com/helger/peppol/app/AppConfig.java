/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
import com.helger.config.ConfigFactory;
import com.helger.config.IConfig;
import com.helger.scope.singleton.AbstractGlobalSingleton;

/**
 * This class provides access to the settings as contained in the
 * <code>webapp.properties</code> file.
 *
 * @author Philip Helger
 */
public final class AppConfig extends AbstractGlobalSingleton
{
  /** The name of the file containing the settings */
  private static final IConfig s_aCF = ConfigFactory.getDefaultConfig ();

  @Deprecated
  @UsedViaReflection
  private AppConfig ()
  {}

  @Nonnull
  public static IConfig getConfig ()
  {
    return s_aCF;
  }

  @Nullable
  public static String getGlobalDebug ()
  {
    return getConfig ().getAsString ("global.debug");
  }

  @Nullable
  public static String getGlobalProduction ()
  {
    return getConfig ().getAsString ("global.production");
  }

  @Nullable
  public static String getDataPath ()
  {
    return getConfig ().getAsString ("webapp.datapath");
  }

  public static boolean isCheckFileAccess ()
  {
    return getConfig ().getAsBoolean ("webapp.checkfileaccess", true);
  }

  public static boolean isTestVersion ()
  {
    return getConfig ().getAsBoolean ("webapp.testversion", GlobalDebug.isDebugMode ());
  }

  public static boolean isWebPageCommentingEnabled ()
  {
    return getConfig ().getAsBoolean ("webapp.pagecomments.enabled", false);
  }

  public static boolean isRestLogExceptions ()
  {
    return getConfig ().getAsBoolean ("rest.log.exceptions", GlobalDebug.isDebugMode ());
  }

  public static boolean isRestExceptionsWithPayload ()
  {
    return getConfig ().getAsBoolean ("rest.exceptions.payload", GlobalDebug.isDebugMode ());
  }

  public static long getRESTAPIMaxRequestsPerSecond ()
  {
    return getConfig ().getAsLong ("rest.limit.requestspersecond", -1);
  }

  @Nullable
  public static String getRecaptchaWebKey ()
  {
    return getConfig ().getAsString ("recaptcha.webkey");
  }

  @Nullable
  public static String getRecaptchaSecretKey ()
  {
    return getConfig ().getAsString ("recaptcha.secretkey");
  }

  public static long getValidationAPIMaxRequestsPerSecond ()
  {
    return getConfig ().getAsLong ("validation.limit.requestspersecond", -1);
  }
}
