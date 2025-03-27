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

import javax.annotation.Nonnull;

import com.helger.commons.annotation.UsedViaReflection;
import com.helger.config.IConfig;
import com.helger.peppol.sharedui.config.SharedUIConfig;
import com.helger.scope.singleton.AbstractGlobalSingleton;

/**
 * This class provides access to the settings as contained in the
 * <code>application.properties</code> file.
 *
 * @author Philip Helger
 */
public final class AppConfig extends AbstractGlobalSingleton
{
  @Deprecated
  @UsedViaReflection
  private AppConfig ()
  {}

  @Nonnull
  public static IConfig getConfig ()
  {
    return SharedUIConfig.getConfig ();
  }

  public static boolean isWebPageCommentingEnabled ()
  {
    return getConfig ().getAsBoolean ("webapp.pagecomments.enabled", false);
  }
}
