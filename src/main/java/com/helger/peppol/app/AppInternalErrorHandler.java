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
import com.helger.web.smtp.ISMTPSettings;
import com.helger.webbasics.action.servlet.AbstractActionServlet;
import com.helger.webbasics.ajax.servlet.AbstractAjaxServlet;
import com.helger.webbasics.app.error.AbstractErrorCallback;
import com.helger.webbasics.app.error.InternalErrorBuilder;
import com.helger.webbasics.app.error.InternalErrorHandler;
import com.helger.webbasics.mgr.MetaSystemManager;
import com.helger.webbasics.smtp.CNamedSMTPSettings;
import com.helger.webbasics.smtp.NamedSMTPSettings;
import com.helger.webscopes.domain.IRequestWebScopeWithoutResponse;

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
