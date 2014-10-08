package com.helger.peppol.page.secure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.appbasics.app.menu.ApplicationMenuTree;
import com.helger.appbasics.app.menu.IMenuItemPage;
import com.helger.appbasics.app.menu.IMenuObject;
import com.helger.appbasics.app.menu.IMenuTree;
import com.helger.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.bootstrap3.button.BootstrapButton;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.callback.INonThrowingRunnableWithParameter;
import com.helger.commons.mutable.MutableInt;
import com.helger.html.hc.CHCParam;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCH2;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.page.AppPageViewExternal;
import com.helger.webbasics.app.CApplication;
import com.helger.webbasics.app.page.AbstractWebPage;
import com.helger.webbasics.app.page.WebPageExecutionContext;

public final class PageSecureAdminAddons extends AbstractWebPage <WebPageExecutionContext>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PageSecureAdminAddons.class);

  private static final String ACTION_EXPIRE_PAGE_CACHE = "expirePageCache";

  public PageSecureAdminAddons (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Additional administration");
  }

  @Nullable
  private static IHCNode _handleAction (@Nullable final String sAction)
  {
    if (ACTION_EXPIRE_PAGE_CACHE.equals (sAction))
    {
      final IMenuTree aPublicMenuTree = ApplicationMenuTree.getInstanceOfScope (CApplication.APP_ID_PUBLIC)
                                                           .getInstanceTree ();

      // Bulk modify
      final MutableInt aCounterUpdated = new MutableInt (0);
      final MutableInt aCounterNoNeed = new MutableInt (0);
      aPublicMenuTree.iterateAllMenuObjects (new INonThrowingRunnableWithParameter <IMenuObject> ()
      {
        public void run (final IMenuObject aMenuObj)
        {
          if (aMenuObj instanceof IMenuItemPage)
          {
            final IMenuItemPage aMenuItemPage = (IMenuItemPage) aMenuObj;
            if (aMenuItemPage.getPage () instanceof AppPageViewExternal)
            {
              final AppPageViewExternal aPageViewExternal = (AppPageViewExternal) aMenuItemPage.getPage ();
              if (aPageViewExternal.isReadEveryTime ())
                aCounterNoNeed.inc ();
              else
              {
                aPageViewExternal.updateFromResource ();
                aCounterUpdated.inc ();
              }
            }
          }
        }
      });
      final String sMsg = aCounterUpdated.intValue () +
                          " pages were reloaded. On " +
                          aCounterNoNeed.intValue () +
                          " pages no action was necessary because they are set to reload every time.";
      s_aLogger.info (sMsg);
      return new BootstrapSuccessBox ().addChild (sMsg);
    }

    return null;
  }

  @Override
  public void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    // Perform
    aNodeList.addChild (_handleAction (aWPEC.getAction ()));

    aNodeList.addChild (new HCH2 ().addChild ("Cache handling"));
    aNodeList.addChild (new HCDiv ().addChild (new BootstrapButton ().setOnClick (aWPEC.getSelfHref ()
                                                                                       .add (CHCParam.PARAM_ACTION,
                                                                                             ACTION_EXPIRE_PAGE_CACHE))
                                                                     .addChild ("Expire static page cache")));
  }
}
