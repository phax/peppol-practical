package com.helger.peppol.comment.ui;

import javax.annotation.concurrent.Immutable;

import com.helger.appbasics.security.login.LoggedInUserManager;
import com.helger.appbasics.security.util.SecurityUtils;
import com.helger.peppol.app.CApp;

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
    return SecurityUtils.hasCurrentUserRole (CApp.ROLE_COMMENT_MODERATOR_ID);
  }
}
