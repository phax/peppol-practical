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
package com.helger.peppol.app;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.quartz.Job;

import com.helger.appbasics.app.dao.impl.AbstractDAO;
import com.helger.appbasics.app.request.ApplicationRequestManager;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.email.EmailAddress;
import com.helger.schedule.job.AbstractJob;
import com.helger.schedule.job.IJobExceptionCallback;
import com.helger.smtp.ISMTPSettings;
import com.helger.web.scopes.domain.IRequestWebScopeWithoutResponse;
import com.helger.webbasics.action.servlet.AbstractActionServlet;
import com.helger.webbasics.ajax.servlet.AbstractAjaxServlet;
import com.helger.webbasics.app.error.AbstractErrorCallback;
import com.helger.webbasics.app.error.InternalErrorBuilder;
import com.helger.webbasics.app.error.InternalErrorHandler;
import com.helger.webbasics.mgr.MetaSystemManager;
import com.helger.webbasics.smtp.CNamedSMTPSettings;
import com.helger.webbasics.smtp.NamedSMTPSettings;

public final class AppInternalErrorHandler extends AbstractErrorCallback implements IJobExceptionCallback
{
  @Override
  protected void onError (@Nonnull final Throwable t,
                          @Nullable final IRequestWebScopeWithoutResponse aRequestScope,
                          @Nonnull @Nonempty final String sErrorCode)
  {
    final Locale aDisplayLocale = ApplicationRequestManager.getInstance ().getRequestDisplayLocale ();
    new InternalErrorBuilder ().setThrowable (t)
                               .setRequestScope (aRequestScope)
                               .addCustomData ("ErrorCode", sErrorCode)
                               .setDisplayLocale (aDisplayLocale)
                               .handle ();
  }

  public void onScheduledJobException (@Nonnull final Throwable t,
                                       @Nullable final String sJobClassName,
                                       @Nonnull final Job aJob,
                                       final boolean bIsLongRunning)
  {
    onError (t, null, "Error executing" +
                      (bIsLongRunning ? " long running" : "") +
                      " job " +
                      sJobClassName +
                      ": " +
                      aJob);
  }

  public static void doSetup ()
  {
    // Set global internal error handlers
    final AppInternalErrorHandler aIntErrHdl = new AppInternalErrorHandler ();
    AbstractAjaxServlet.getExceptionCallbacks ().addCallback (aIntErrHdl);
    AbstractActionServlet.getExceptionCallbacks ().addCallback (aIntErrHdl);
    AbstractDAO.getExceptionHandlersRead ().addCallback (aIntErrHdl);
    AbstractDAO.getExceptionHandlersWrite ().addCallback (aIntErrHdl);
    AbstractJob.getExceptionCallbacks ().addCallback (aIntErrHdl);

    final NamedSMTPSettings aNamedSettings = MetaSystemManager.getSMTPSettingsMgr ()
                                                              .getSettings (CNamedSMTPSettings.NAMED_SMTP_SETTINGS_DEFAULT_ID);
    final ISMTPSettings aSMTPSettings = aNamedSettings == null ? null : aNamedSettings.getSMTPSettings ();
    InternalErrorHandler.setSMTPSenderAddress (new EmailAddress ("peppol@helger.com", "peppol.helger.com application"));
    InternalErrorHandler.setSMTPReceiverAddress (new EmailAddress ("philip@helger.com", "Philip"));
    InternalErrorHandler.setSMTPSettings (aSMTPSettings);
  }
}
