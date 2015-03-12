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
import com.helger.commons.factory.IFactory;
import com.helger.webbasics.ajax.IAjaxExecutor;
import com.helger.webbasics.ajax.decl.AbstractAjaxFunctionDeclaration;

/**
 * Ajax function declaration for the public application
 *
 * @author Philip Helger
 */
public class CommentAjaxFunctionDeclaration extends AbstractAjaxFunctionDeclaration
{
  public CommentAjaxFunctionDeclaration (@Nonnull @Nonempty final String sFunctionName,
                                         @Nonnull final IAjaxExecutor aExecutor)
  {
    super (sFunctionName, aExecutor);
  }

  public CommentAjaxFunctionDeclaration (@Nonnull @Nonempty final String sFunctionName,
                                         @Nonnull final Class <? extends IAjaxExecutor> aExecutorClass)
  {
    super (sFunctionName, aExecutorClass);
  }

  public CommentAjaxFunctionDeclaration (@Nonnull @Nonempty final String sFunctionName,
                                         @Nonnull final IFactory <? extends IAjaxExecutor> aExecutorFactory)
  {
    super (sFunctionName, aExecutorFactory);
  }

  @Nonnull
  @Nonempty
  public String getAjaxServletPath ()
  {
    return CommentAjaxServlet.SERVLET_DEFAULT_PATH + '/';
  }
}
