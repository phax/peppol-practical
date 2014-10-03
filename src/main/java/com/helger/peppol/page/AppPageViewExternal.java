package com.helger.peppol.page;

import javax.annotation.Nonnull;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.io.IReadableResource;
import com.helger.commons.microdom.IMicroContainer;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webpages.PageViewExternal;

public class AppPageViewExternal extends PageViewExternal <WebPageExecutionContext>
{
  public AppPageViewExternal (@Nonnull @Nonempty final String sID,
                              @Nonnull final String sName,
                              @Nonnull final IReadableResource aResource)
  {
    super (sID, sName, aResource);
  }

  @Override
  protected void afterPageRead (@Nonnull final IMicroContainer aCont)
  {
    super.afterPageRead (aCont);
  }
}
