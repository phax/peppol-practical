package com.helger.peppol.page;

import javax.annotation.Nonnull;

import com.helger.commons.annotations.Nonempty;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webctrls.page.AbstractWebPageExt;

public abstract class AbstractAppWebPageExt extends AbstractWebPageExt <WebPageExecutionContext>
{
  public AbstractAppWebPageExt (@Nonnull @Nonempty final String sID, @Nonnull final String sName)
  {
    super (sID, sName);
  }
}
