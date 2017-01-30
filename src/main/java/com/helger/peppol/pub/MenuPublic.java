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
package com.helger.peppol.pub;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.peppol.pub.page.PagePublicContact;
import com.helger.peppol.pub.page.PagePublicLogin;
import com.helger.peppol.pub.page.PagePublicNewsletterSubscribe;
import com.helger.peppol.pub.page.PagePublicNewsletterUnsubscribe;
import com.helger.peppol.pub.page.PagePublicSignUp;
import com.helger.peppol.pub.page.PagePublicToolsParticipantInformation;
import com.helger.peppol.pub.page.PagePublicToolsSMPSML;
import com.helger.peppol.pub.page.PagePublicToolsTestEndpoints;
import com.helger.peppol.pub.page.PagePublicToolsValidateBIS2;
import com.helger.peppol.ui.page.AppPageViewExternal;
import com.helger.photon.basic.app.menu.IMenuItemPage;
import com.helger.photon.basic.app.menu.IMenuTree;
import com.helger.photon.bootstrap3.pages.security.BasePageSecurityChangePassword;
import com.helger.photon.security.menu.MenuObjectFilterNoUserLoggedIn;
import com.helger.photon.security.menu.MenuObjectFilterUserLoggedIn;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uicore.page.system.BasePageShowChildren;

@Immutable
public final class MenuPublic
{
  private MenuPublic ()
  {}

  public static void init (@Nonnull final IMenuTree aMenuTree)
  {
    // Common stuff
    aMenuTree.createRootItem (new AppPageViewExternal (CMenuPublic.MENU_INDEX,
                                                       "Overview",
                                                       new ClassPathResource ("viewpages/en/index.xml")));

    // News stuff
    {
      aMenuTree.createRootItem (new AppPageViewExternal (CMenuPublic.MENU_NEWS,
                                                         "News",
                                                         new ClassPathResource ("viewpages/en/news.xml")));
    }

    // Setup stuff
    {
      final IMenuItemPage aSetup = aMenuTree.createRootItem (new BasePageShowChildren <WebPageExecutionContext> (CMenuPublic.MENU_DOCS,
                                                                                                                 "Technical documentation",
                                                                                                                 aMenuTree));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_SETUP_AP_PH,
                                                     "Setup PEPPOL AP",
                                                     new ClassPathResource ("viewpages/en/docs_setup_ap.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_SETUP_SMP_PH,
                                                     "Setup phoss SMP",
                                                     new ClassPathResource ("viewpages/en/docs_setup_smp_ph.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_SETUP_SMP_CIPA,
                                                     "Setup CIPA SMP",
                                                     new ClassPathResource ("viewpages/en/docs_setup_smp.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_SML_SUPPORT,
                                                     "SML support",
                                                     new ClassPathResource ("viewpages/en/docs_sml_support.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_DOC_EXCHANGE,
                                                     "Document exchange explained",
                                                     new ClassPathResource ("viewpages/en/docs_document_exchange.xml")));

      // Not needed any more
      if (false)
        aMenuTree.createItem (aSetup,
                              new AppPageViewExternal (CMenuPublic.MENU_DOCS_SML_MIGRATION,
                                                       "SML migration information",
                                                       new ClassPathResource ("viewpages/en/docs_sml_migration.xml")));
    }

    // Tools stuff
    {
      final IMenuItemPage aSetup = aMenuTree.createRootItem (new BasePageShowChildren <WebPageExecutionContext> (CMenuPublic.MENU_TOOLS,
                                                                                                                 "Tools",
                                                                                                                 aMenuTree));
      aMenuTree.createItem (aSetup,
                            new PagePublicToolsParticipantInformation (CMenuPublic.MENU_TOOLS_PARTICIPANT_INFO));
      aMenuTree.createItem (aSetup, new PagePublicToolsSMPSML (CMenuPublic.MENU_TOOLS_SMP_SML));
      aMenuTree.createItem (aSetup, new PagePublicToolsTestEndpoints (CMenuPublic.MENU_TOOLS_TEST_ENDPOINTS));
    }

    // Validation stuff
    {
      final IMenuItemPage aValidation = aMenuTree.createRootItem (new BasePageShowChildren <WebPageExecutionContext> (CMenuPublic.MENU_VALIDATION,
                                                                                                                      "Validation",
                                                                                                                      aMenuTree));
      aMenuTree.createItem (aValidation, new PagePublicToolsValidateBIS2 (CMenuPublic.MENU_VALIDATION_BIS2));
      aMenuTree.createItem (aValidation,
                            new AppPageViewExternal (CMenuPublic.MENU_VALIDATION_DVS,
                                                     "Document Validation Service",
                                                     new ClassPathResource ("viewpages/en/validation_dvs.xml")));
    }

    // Newsletter stuff
    {
      aMenuTree.createRootItem (new PagePublicNewsletterSubscribe (CMenuPublic.MENU_NEWSLETTER_SUBSCRIBE));
      aMenuTree.createRootItem (new PagePublicNewsletterUnsubscribe (CMenuPublic.MENU_NEWSLETTER_UNSUBSCRIBE))
               .setAttribute (CMenuPublic.FLAG_FOOTER_COL1, true);
    }

    // Contact form
    {
      aMenuTree.createRootSeparator ();
      aMenuTree.createRootItem (new PagePublicContact (CMenuPublic.MENU_CONTACT));
    }

    aMenuTree.createRootSeparator ();

    // Register/Login stuff
    {
      aMenuTree.createRootItem (new PagePublicLogin (CMenuPublic.MENU_LOGIN))
               .setDisplayFilter (new MenuObjectFilterNoUserLoggedIn ());
      aMenuTree.createRootItem (new PagePublicSignUp (CMenuPublic.MENU_SIGN_UP))
               .setDisplayFilter (new MenuObjectFilterNoUserLoggedIn ());
    }

    // Logged in user stuff
    {
      aMenuTree.createRootItem (new BasePageSecurityChangePassword <WebPageExecutionContext> (CMenuPublic.MENU_CHANGE_PASSWORD))
               .setDisplayFilter (new MenuObjectFilterUserLoggedIn ());
    }

    // Set default
    aMenuTree.setDefaultMenuItemID (CMenuPublic.MENU_NEWS);
  }
}
