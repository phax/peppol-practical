/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.peppol.comment.ajax;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.photon.core.ajax.IAjaxInvoker;
import com.helger.photon.core.ajax.servlet.AbstractAjaxServlet;
import com.helger.photon.core.app.CApplication;
import com.helger.photon.core.servletstatus.ServletStatusManager;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

/**
 * Servlet that handles comment AJAX calls
 *
 * @author Philip Helger
 */
public class CommentAjaxServlet extends AbstractAjaxServlet
{
  public static final String SERVLET_DEFAULT_NAME = "comment";
  public static final String SERVLET_DEFAULT_PATH = '/' + SERVLET_DEFAULT_NAME;

  private static final boolean s_bIsRegistered = ServletStatusManager.isServletRegistered (CommentAjaxServlet.class);

  public CommentAjaxServlet ()
  {}

  public static boolean isServletRegisteredInServletContext ()
  {
    return s_bIsRegistered;
  }

  @Override
  @Nonnull
  @Nonempty
  protected String getApplicationID ()
  {
    return CApplication.APP_ID_PUBLIC;
  }

  @Override
  @Nonnull
  protected final IAjaxInvoker getAjaxInvoker (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope)
  {
    return CommentAjaxManager.getInstance ();
  }
}
