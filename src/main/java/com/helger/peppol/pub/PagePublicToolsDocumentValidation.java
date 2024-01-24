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
package com.helger.peppol.pub;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.photon.uicore.page.WebPageExecutionContext;

public class PagePublicToolsDocumentValidation extends AbstractAppWebPage
{
  public PagePublicToolsDocumentValidation (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Document Validation (Upload)");
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    aNodeList.addChild (warn (div ("This page was moved.")).addChild (div ("The new home of the validation per upload is at my friends of ecosio: ").addChild (HCA.createLinkedWebsite ("https://ecosio.com/en/peppol-and-xml-document-validator/")))
                                                           .addChild (div ("They are using my validation engine and helping me to save some server resources :)"))
                                                           .addChild (div (strong ("Note: ")).addChild ("the validation web service is currently not affected by this change and new validation rules will be added as usual.")));
  }
}
