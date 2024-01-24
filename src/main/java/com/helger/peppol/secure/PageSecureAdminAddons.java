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
package com.helger.peppol.secure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.mutable.MutableInt;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.photon.audit.AuditHelper;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPage;
import com.helger.photon.core.appid.CApplicationID;
import com.helger.photon.core.appid.PhotonGlobalState;
import com.helger.photon.core.menu.IMenuItemPage;
import com.helger.photon.core.menu.IMenuTree;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uicore.page.external.IWebPageResourceContent;

public final class PageSecureAdminAddons extends AbstractBootstrapWebPage <WebPageExecutionContext>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PageSecureAdminAddons.class);

  private static final String ACTION_EXPIRE_PAGE_CACHE = "expirePageCache";

  public PageSecureAdminAddons (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Additional administration");
  }

  @Nullable
  private IHCNode _handleAction (@Nullable final String sAction)
  {
    if (ACTION_EXPIRE_PAGE_CACHE.equals (sAction))
    {
      final IMenuTree aPublicMenuTree = PhotonGlobalState.state (CApplicationID.APP_ID_PUBLIC).getMenuTree ();

      // Bulk modify
      final MutableInt aCounterUpdated = new MutableInt (0);
      final MutableInt aCounterNoNeed = new MutableInt (0);
      aPublicMenuTree.iterateAllMenuObjects (aMenuObj -> {
        if (aMenuObj instanceof IMenuItemPage)
        {
          final IMenuItemPage aMenuItemPage = (IMenuItemPage) aMenuObj;
          if (aMenuItemPage.getPage () instanceof IWebPageResourceContent)
          {
            final IWebPageResourceContent aPageViewExternal = (IWebPageResourceContent) aMenuItemPage.getPage ();
            if (aPageViewExternal.isReadEveryTime ())
              aCounterNoNeed.inc ();
            else
            {
              aPageViewExternal.updateFromResource ();
              aCounterUpdated.inc ();
            }
          }
        }
      });
      final String sMsg = aCounterUpdated.intValue () +
                          " pages were reloaded." +
                          (aCounterNoNeed.isGT0 () ? " On " +
                                                     aCounterNoNeed.intValue () +
                                                     " pages no action was necessary because they are set to reload every time."
                                                   : "");
      LOGGER.info (sMsg);
      AuditHelper.onAuditExecuteSuccess ("page-reload", aCounterUpdated.getAsInteger (), aCounterNoNeed.getAsInteger ());
      return success (sMsg);
    }

    return null;
  }

  @Override
  public void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    // Perform
    aNodeList.addChild (_handleAction (aWPEC.getAction ()));

    aNodeList.addChild (h2 ("Cache handling"));
    aNodeList.addChild (div (new BootstrapButton ().setOnClick (aWPEC.getSelfHref ()
                                                                     .add (CPageParam.PARAM_ACTION, ACTION_EXPIRE_PAGE_CACHE))
                                                   .addChild ("Expire static page cache")));
  }
}
