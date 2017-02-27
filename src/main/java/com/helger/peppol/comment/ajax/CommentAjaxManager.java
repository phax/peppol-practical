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
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.UsedViaReflection;
import com.helger.commons.collection.ext.ICommonsMap;
import com.helger.commons.string.ToStringGenerator;
import com.helger.photon.core.ajax.AjaxInvoker;
import com.helger.photon.core.ajax.IAjaxExecutor;
import com.helger.photon.core.ajax.IAjaxFunctionDeclaration;
import com.helger.photon.core.ajax.IAjaxInvoker;
import com.helger.photon.core.ajax.response.IAjaxResponse;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;
import com.helger.web.scope.singleton.AbstractGlobalWebSingleton;

/**
 * The comment AJAX manager.
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class CommentAjaxManager extends AbstractGlobalWebSingleton implements IAjaxInvoker
{
  private final AjaxInvoker m_aInvoker = new AjaxInvoker ();

  /**
   * Private constructor. Avoid outside instantiation
   */
  @Deprecated
  @UsedViaReflection
  public CommentAjaxManager ()
  {
    // Register all functions centrally here
    m_aInvoker.registerFunction (CAjaxComment.COMMENT_ADD);
    m_aInvoker.registerFunction (CAjaxComment.COMMENT_CREATE_THREAD);
    m_aInvoker.registerFunction (CAjaxComment.COMMENT_DELETE);
    m_aInvoker.registerFunction (CAjaxComment.COMMENT_SHOW_INPUT);
  }

  @Nonnull
  public static CommentAjaxManager getInstance ()
  {
    return getGlobalSingleton (CommentAjaxManager.class);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsMap <String, IAjaxFunctionDeclaration> getAllRegisteredFunctions ()
  {
    return m_aInvoker.getAllRegisteredFunctions ();
  }

  @Nullable
  public IAjaxFunctionDeclaration getRegisteredFunction (@Nullable final String sFunctionName)
  {
    return m_aInvoker.getRegisteredFunction (sFunctionName);
  }

  public boolean isRegisteredFunction (@Nullable final String sFunctionName)
  {
    return m_aInvoker.isRegisteredFunction (sFunctionName);
  }

  public void registerFunction (@Nonnull final IAjaxFunctionDeclaration aFunction)
  {
    m_aInvoker.registerFunction (aFunction);
  }

  @Nonnull
  public IAjaxResponse invokeFunction (@Nonnull final String sFunctionName,
                                       @Nonnull final IAjaxExecutor aAjaxExecutor,
                                       @Nonnull final IRequestWebScopeWithoutResponse aRequestScope) throws Throwable
  {
    return m_aInvoker.invokeFunction (sFunctionName, aAjaxExecutor, aRequestScope);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ()).append ("invoker", m_aInvoker).getToString ();
  }
}
