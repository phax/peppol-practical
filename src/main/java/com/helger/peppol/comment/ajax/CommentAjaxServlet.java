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

import com.helger.commons.http.EHttpMethod;
import com.helger.photon.basic.app.CApplicationID;
import com.helger.photon.core.ajax.IAjaxInvoker;
import com.helger.photon.core.ajax.servlet.AjaxXServletHandler;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;
import com.helger.xservlet.AbstractXServlet;

/**
 * Servlet that handles comment AJAX calls
 *
 * @author Philip Helger
 */
public final class CommentAjaxServlet extends AbstractXServlet
{
  public static final String SERVLET_DEFAULT_NAME = "comment";
  public static final String SERVLET_DEFAULT_PATH = '/' + SERVLET_DEFAULT_NAME;

  public CommentAjaxServlet ()
  {
    super ( () -> CApplicationID.APP_ID_PUBLIC);
    handlerRegistry ().registerHandler (EHttpMethod.GET, new AjaxXServletHandler ()
    {
      @Override
      protected IAjaxInvoker getAjaxInvoker (final IRequestWebScopeWithoutResponse aRequestScope)
      {
        return CommentAjaxManager.getInstance ();
      }
    });
  }
}
