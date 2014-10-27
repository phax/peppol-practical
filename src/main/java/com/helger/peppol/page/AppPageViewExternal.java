/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.peppol.page;

import javax.annotation.Nonnull;

import com.helger.commons.annotations.Nonempty;
import com.helger.commons.hierarchy.DefaultHierarchyWalkerCallback;
import com.helger.commons.io.IReadableResource;
import com.helger.commons.microdom.IMicroComment;
import com.helger.commons.microdom.IMicroContainer;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.microdom.utils.MicroWalker;
import com.helger.commons.type.TypedObject;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.AppSettings;
import com.helger.peppol.app.CApp;
import com.helger.peppol.comment.ui.CommentAction;
import com.helger.peppol.comment.ui.CommentUI;
import com.helger.peppol.comment.ui.ECommentAction;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webctrls.page.PageViewExternal;

public class AppPageViewExternal extends PageViewExternal <WebPageExecutionContext>
{
  private static final class StripComments extends DefaultHierarchyWalkerCallback <IMicroNode>
  {
    @Override
    public void onItemBeforeChildren (final IMicroNode aItem)
    {
      if (aItem instanceof IMicroComment)
      {
        final IMicroComment e = (IMicroComment) aItem;
        e.getParent ().removeChild (e);
      }
    }
  }

  public AppPageViewExternal (@Nonnull @Nonempty final String sID,
                              @Nonnull final String sName,
                              @Nonnull final IReadableResource aResource)
  {
    super (sID, sName, aResource);
    // Do additional cleansing since the overloaded method is not called in the
    // ctor!
    MicroWalker.walkNode (m_aParsedContent, new StripComments ());
  }

  @Override
  protected void afterPageRead (@Nonnull final IMicroContainer aCont)
  {
    super.afterPageRead (aCont);
    // Do additional cleansing
    MicroWalker.walkNode (aCont, new StripComments ());
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
                                                    null,
                                                    null));
    }
  }
}
