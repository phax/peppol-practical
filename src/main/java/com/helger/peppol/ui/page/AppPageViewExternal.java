/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.type.TypedObject;
import com.helger.html.EHTMLVersion;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.AppConfig;
import com.helger.peppol.app.CPPApp;
import com.helger.peppol.comment.ui.CommentAction;
import com.helger.peppol.comment.ui.CommentFormErrors;
import com.helger.peppol.comment.ui.CommentUI;
import com.helger.peppol.comment.ui.ECommentAction;
import com.helger.photon.bootstrap4.pages.BootstrapWebPageUIHandler;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uicore.page.external.BasePageViewExternal;
import com.helger.photon.uicore.page.external.PageViewExternalHTMLCleanser;
import com.helger.xml.microdom.IMicroContainer;
import com.helger.xml.microdom.util.MicroVisitor;

public class AppPageViewExternal extends BasePageViewExternal <WebPageExecutionContext>
{
  private static void _cleanCode (@Nonnull final IMicroContainer aCont)
  {
    // Do not clean texts, because this destroys "pre" formatting!
    final PageViewExternalHTMLCleanser aCleanser = new PageViewExternalHTMLCleanser (EHTMLVersion.HTML5).setCleanTexts (false);
    MicroVisitor.visit (aCont, aCleanser);
  }

  public AppPageViewExternal (@Nonnull @Nonempty final String sID, @Nonnull final String sName, @Nonnull final IReadableResource aResource)
  {
    // Special content cleaner
    super (sID, sName, aResource, AppPageViewExternal::_cleanCode);
  }

  @Override
  @Nullable
  public IHCNode getHeaderNode (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final String sHeaderText = getHeaderText (aWPEC);
    return BootstrapWebPageUIHandler.INSTANCE.createPageHeader (sHeaderText);
  }

  @Override
  protected final void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    super.fillContent (aWPEC);

    if (AppConfig.isWebPageCommentingEnabled ())
    {
      // Show comments and "add comment"
      final HCNodeList aNodeList = aWPEC.getNodeList ();
      final TypedObject <String> aTO = TypedObject.create (CPPApp.OT_PAGE, getID ());
      aNodeList.addChild (CommentUI.getCommentList (aWPEC,
                                                    aTO,
                                                    CommentAction.createGeneric (ECommentAction.NONE),
                                                    (CommentFormErrors) null,
                                                    (IHCNode) null,
                                                    true));
    }
  }
}
