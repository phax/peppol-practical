package com.helger.peppol.rest;

import com.helger.peppol.sharedui.api.AbstractRateLimitingAPIExecutor;

public abstract class AbstractPPAPIExecutor extends AbstractRateLimitingAPIExecutor
{
  public static final String DEFAULT_USER_AGENT = "Peppol-Practical/1.0";

  protected AbstractPPAPIExecutor ()
  {
    super (DEFAULT_USER_AGENT);
  }
}
