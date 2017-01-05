/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.type.TypedObject;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.AppSettings;
import com.helger.peppol.app.CApp;
import com.helger.peppol.comment.ui.CommentAction;
import com.helger.peppol.comment.ui.CommentFormErrors;
import com.helger.peppol.comment.ui.CommentUI;
import com.helger.peppol.comment.ui.ECommentAction;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uicore.page.external.BasePageViewExternal;
import com.helger.photon.uicore.page.external.PageViewExternalHTMLCleanser;
import com.helger.xml.microdom.util.MicroVisitor;

public class AppPageViewExternal extends BasePageViewExternal <WebPageExecutionContext>
{
  public AppPageViewExternal (@Nonnull @Nonempty final String sID,
                              @Nonnull final String sName,
                              @Nonnull final IReadableResource aResource)
  {
    super (sID, sName, aResource, aCont -> MicroVisitor.visit (aCont, new PageViewExternalHTMLCleanser ()));
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    super.fillContent (aWPEC);

    if (AppSettings.isWebPageCommentingEnabled ())
    {
      // Show comments and "add comment"
      final HCNodeList aNodeList = aWPEC.getNodeList ();
      final TypedObject <String> aTO = TypedObject.create (CApp.OT_PAGE, getID ());
      aNodeList.addChild (CommentUI.getCommentList (aWPEC,
                                                    aTO,
                                                    CommentAction.createGeneric (ECommentAction.NONE),
                                                    (CommentFormErrors) null,
                                                    (IHCNode) null,
                                                    true));
    }
  }
}
