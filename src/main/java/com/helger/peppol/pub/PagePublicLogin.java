/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.peppol.pub;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.sharedui.page.AbstractAppWebPage;
import com.helger.peppol.ui.AppCommonUI;
import com.helger.photon.uicore.page.WebPageExecutionContext;


public final class PagePublicLogin extends AbstractAppWebPage
{
  public PagePublicLogin (@NonNull @Nonempty final String sID)
  {
    super (sID, "Login");
  }

  @Override
  @Nullable
  public String getHeaderText (@NonNull final WebPageExecutionContext aWPEC)
  {
    return super.getHeaderText (aWPEC);
  }

  @Override
  protected void fillContent (final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    aNodeList.addChild (AppCommonUI.createViewLoginForm (aWPEC, null, true));
  }
}
