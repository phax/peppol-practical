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
package com.helger.peppol.app.action;

import javax.annotation.concurrent.Immutable;

import com.helger.photon.core.action.IActionDeclaration;
import com.helger.photon.core.action.decl.SecureApplicationActionDeclaration;
import com.helger.photon.core.action.executor.ActionExecutorPing;

/**
 * This class defines the available actions for the config app
 *
 * @author Philip Helger
 */
@Immutable
public final class CActionSecure
{
  public static final IActionDeclaration PING = new SecureApplicationActionDeclaration ("ping",
                                                                                        ActionExecutorPing.class);

  private CActionSecure ()
  {}
}
