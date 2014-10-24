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
package com.helger.peppol.comment.ui;

import javax.annotation.concurrent.Immutable;

import com.helger.html.css.DefaultCSSClassProvider;
import com.helger.html.css.ICSSClassProvider;

/**
 * CSS classes for comments
 * 
 * @author philip
 */
@Immutable
public final class CCommentCSS
{
  // Viewing
  public static final ICSSClassProvider CSS_CLASS_COMMENT_VIEW = DefaultCSSClassProvider.create ("comment_view");
  public static final ICSSClassProvider CSS_CLASS_COMMENT_THREAD = DefaultCSSClassProvider.create ("commentthread");
  public static final ICSSClassProvider CSS_CLASS_COMMENT = DefaultCSSClassProvider.create ("comment");
  public static final ICSSClassProvider CSS_CLASS_REGISTERED_USER = DefaultCSSClassProvider.create ("registereduser");
  public static final ICSSClassProvider CSS_CLASS_TOOLBAR = DefaultCSSClassProvider.create ("toolbar");
  public static final ICSSClassProvider CSS_CLASS_TITLE = DefaultCSSClassProvider.create ("title");
  public static final ICSSClassProvider CSS_CLASS_TEXT = DefaultCSSClassProvider.create ("text");
  public static final ICSSClassProvider CSS_CLASS_AUTHOR = DefaultCSSClassProvider.create ("author");
  public static final ICSSClassProvider CSS_CLASS_CREATIONDT = DefaultCSSClassProvider.create ("creationdt");
  public static final ICSSClassProvider CSS_CLASS_EDITED = DefaultCSSClassProvider.create ("edited");

  // Creation
  public static final ICSSClassProvider CSS_CLASS_COMMENT_CREATE = DefaultCSSClassProvider.create ("comment_create");

  private CCommentCSS ()
  {}
}
