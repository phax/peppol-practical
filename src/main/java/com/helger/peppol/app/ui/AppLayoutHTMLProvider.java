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
package com.helger.peppol.app.ui;

import javax.annotation.Nonnull;

import com.helger.appbasics.app.request.IRequestManager;
import com.helger.commons.annotations.OverrideOnDemand;
import com.helger.html.hc.html.HCHtml;
import com.helger.peppol.app.AppUtils;
import com.helger.webbasics.app.ISimpleWebExecutionContext;
import com.helger.webbasics.app.layout.ApplicationLayoutManager;
import com.helger.webbasics.app.layout.LayoutExecutionContext;
import com.helger.webbasics.app.layout.LayoutHTMLProvider;

/**
 * Main class for creating HTML output
 *
 * @author Philip Helger
 */
public class AppLayoutHTMLProvider extends LayoutHTMLProvider <LayoutExecutionContext>
{
  public AppLayoutHTMLProvider ()
  {
    super (ApplicationLayoutManager.<LayoutExecutionContext> getInstance ());
    setCreateLayoutAreaSpan (false);
  }

  @Override
  protected LayoutExecutionContext createLayoutExecutionContext (@Nonnull final ISimpleWebExecutionContext aSWEC,
                                                                 @Nonnull final IRequestManager aRequestManager)
  {
    return new LayoutExecutionContext (aSWEC, aRequestManager.getRequestMenuItem ());
  }

  /**
   * Fill the HTML HEAD element.
   *
   * @param aHtml
   *        The HTML object to be filled.
   */
  @Override
  @OverrideOnDemand
  protected void fillHead (@Nonnull final ISimpleWebExecutionContext aSWEC, @Nonnull final HCHtml aHtml)
  {
    super.fillHead (aSWEC, aHtml);
    aHtml.getHead ().setPageTitle (AppUtils.getApplicationTitle ());
  }
}
