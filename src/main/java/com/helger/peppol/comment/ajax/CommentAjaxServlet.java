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
package com.helger.peppol.comment.ajax;

import javax.annotation.Nonnull;

import com.helger.commons.annotations.Nonempty;
import com.helger.webbasics.ajax.IAjaxInvoker;
import com.helger.webbasics.ajax.servlet.AbstractAjaxServlet;
import com.helger.webbasics.app.CApplication;
import com.helger.webscopes.domain.IRequestWebScopeWithoutResponse;

/**
 * Servlet that handles comment AJAX calls
 *
 * @author Philip Helger
 */
public class CommentAjaxServlet extends AbstractAjaxServlet
{
  public static final String SERVLET_DEFAULT_NAME = "comment";
  public static final String SERVLET_DEFAULT_PATH = '/' + SERVLET_DEFAULT_NAME;

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
