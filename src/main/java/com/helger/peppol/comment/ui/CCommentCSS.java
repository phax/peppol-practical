/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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
 * @author Philip Helger
 */
@Immutable
public final class CCommentCSS
{
  // Viewing
  public static final ICSSClassProvider CSS_CLASS_COMMENT_CONTAINER = DefaultCSSClassProvider.create ("comment-container");
  public static final ICSSClassProvider CSS_CLASS_COMMENT_THREAD = DefaultCSSClassProvider.create ("comment-thread");
  public static final ICSSClassProvider CSS_CLASS_SINGLE_COMMENT = DefaultCSSClassProvider.create ("comment-single-comment");
  public static final ICSSClassProvider CSS_CLASS_COMMENT_REGISTERED_USER = DefaultCSSClassProvider.create ("comment-registered-user");
  public static final ICSSClassProvider CSS_CLASS_COMMENT_TOOLBAR = DefaultCSSClassProvider.create ("comment-toolbar");
  public static final ICSSClassProvider CSS_CLASS_COMMENT_TITLE = DefaultCSSClassProvider.create ("comment-title");
  public static final ICSSClassProvider CSS_CLASS_COMMENT_TEXT = DefaultCSSClassProvider.create ("comment-text");
  public static final ICSSClassProvider CSS_CLASS_COMMENT_AUTHOR = DefaultCSSClassProvider.create ("comment-author");
  public static final ICSSClassProvider CSS_CLASS_COMMENT_CREATIONDT = DefaultCSSClassProvider.create ("comment-creation-datetime");
  public static final ICSSClassProvider CSS_CLASS_COMMENT_LAST_MODIFICATION = DefaultCSSClassProvider.create ("comment-last-modification");

  // Creation
  public static final ICSSClassProvider CSS_CLASS_COMMENT_CREATE = DefaultCSSClassProvider.create ("comment-create");

  private CCommentCSS ()
  {}
}
