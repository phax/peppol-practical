/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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
package com.helger.peppol.ui.page;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.IHCNode;
import com.helger.photon.bootstrap4.button.BootstrapLinkButton;
import com.helger.photon.bootstrap4.button.EBootstrapButtonSize;
import com.helger.photon.bootstrap4.button.EBootstrapButtonType;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPage;
import com.helger.photon.uicore.page.WebPageExecutionContext;

public abstract class AbstractAppWebPage extends AbstractBootstrapWebPage <WebPageExecutionContext>
{
  public AbstractAppWebPage (@Nonnull @Nonempty final String sID, @Nonnull final String sName)
  {
    super (sID, sName);
  }

  @Nonnull
  protected static BootstrapLinkButton _createOpenInBrowser (@Nonnull final String sURL)
  {
    return _createOpenInBrowser (sURL, "Open in browser");
  }

  @Nonnull
  protected static BootstrapLinkButton _createOpenInBrowser (@Nonnull final String sURL, @Nonnull final String sLabel)
  {
    return new BootstrapLinkButton (EBootstrapButtonSize.SMALL).setButtonType (EBootstrapButtonType.OUTLINE_INFO)
                                                               .setHref (new SimpleURL (sURL))
                                                               .addChild (sLabel)
                                                               .setTargetBlank ();
  }

  @Nonnull
  protected IHCNode _createTimingNode (final long nMillis)
  {
    return badgeInfo ("took " + nMillis + " milliseconds");
  }
}
