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
package com.helger.peppol.comment.ui;

import javax.annotation.concurrent.Immutable;

import com.helger.peppol.app.CApp;
import com.helger.photon.basic.security.login.LoggedInUserManager;
import com.helger.photon.basic.security.util.SecurityHelper;

@Immutable
public final class CommentSecurity
{
  private CommentSecurity ()
  {}

  public static boolean canCurrentUserPostComments ()
  {
    return LoggedInUserManager.getInstance ().isUserLoggedInInCurrentSession ();
  }

  public static boolean isCurrentUserCommentModerator ()
  {
    return SecurityHelper.hasCurrentUserRole (CApp.ROLE_COMMENT_MODERATOR_ID);
  }
}
