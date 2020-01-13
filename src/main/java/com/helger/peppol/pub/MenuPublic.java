/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
import com.helger.commons.text.display.ConstantHasDisplayText;
import com.helger.commons.url.SimpleURL;
import com.helger.peppol.ui.page.AppPageViewExternal;
import com.helger.photon.bootstrap4.pages.security.BasePageSecurityChangePassword;
import com.helger.photon.core.menu.IMenuItemPage;
import com.helger.photon.core.menu.IMenuTree;
import com.helger.photon.core.menu.filter.MenuObjectFilterNoUserLoggedIn;
import com.helger.photon.core.menu.filter.MenuObjectFilterUserLoggedIn;
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
                                                     "Setup Peppol AP",
                                                     new ClassPathResource ("viewpages/en/docs_setup_ap.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_SETUP_SMP_PHOSS,
                                                     "Setup phoss SMP",
                                                     new ClassPathResource ("viewpages/en/docs_setup_smp_phoss.xml")));
      if (false)
        aMenuTree.createItem (aSetup,
                              new AppPageViewExternal (CMenuPublic.MENU_DOCS_SETUP_SMP_CIPA,
                                                       "Setup CIPA SMP (deprecated)",
                                                       new ClassPathResource ("viewpages/en/invisible/docs_setup_smp_cipa.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_SMP_SML_INTERPLAY,
                                                     "SMP and SML interactions",
                                                     new ClassPathResource ("viewpages/en/docs_smp_sml_interplay.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_SML_SUPPORT,
                                                     "SML support",
                                                     new ClassPathResource ("viewpages/en/docs_sml_support.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_DOC_EXCHANGE,
                                                     "Document exchange explained",
                                                     new ClassPathResource ("viewpages/en/docs_document_exchange.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_PEPPOL_PKI,
                                                     "Peppol PKI explained",
                                                     new ClassPathResource ("viewpages/en/docs_peppol_pki.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_PEPPOL_CERT_UPDATE,
                                                     "Peppol Certificate update",
                                                     new ClassPathResource ("viewpages/en/docs_peppol_cert_update.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_PEPPOL_DICT,
                                                     "Peppol Dictionary/Abbreviations",
                                                     new ClassPathResource ("viewpages/en/docs_peppol_dict.xml")));

      // Not needed any more
      if (false)
        aMenuTree.createItem (aSetup,
                              new AppPageViewExternal ("docs-sml-migration",
                                                       "SML migration information",
                                                       new ClassPathResource ("viewpages/en/invisible/docs_sml_migration.xml")));
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
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_TOOLS_REST_API,
                                                     "REST API",
                                                     new ClassPathResource ("viewpages/en/rest_api.xml")));
    }

    // Validation stuff
    {
      final IMenuItemPage aValidation = aMenuTree.createRootItem (new BasePageShowChildren <WebPageExecutionContext> (CMenuPublic.MENU_VALIDATION,
                                                                                                                      "Document Validation",
                                                                                                                      aMenuTree));
      final IMenuItemPage aUpload = aMenuTree.createItem (aValidation,
                                                          new PagePublicToolsDocumentValidation (CMenuPublic.MENU_VALIDATION_UPLOAD));
      aMenuTree.createRedirect ("validation-bis2", aUpload);
      aMenuTree.createItem (aValidation,
                            new AppPageViewExternal (CMenuPublic.MENU_VALIDATION_DVS,
                                                     "Document Validation (WebService)",
                                                     new ClassPathResource ("viewpages/en/validation_dvs.xml")));
    }

    // EN 16931 - CEN/TC 434
    {
      aMenuTree.createRootItem (new AppPageViewExternal (CMenuPublic.MENU_EN16931,
                                                         "EN 16931 - CEN/TC 434",
                                                         new ClassPathResource ("viewpages/en/en16931.xml")));
    }

    aMenuTree.createRootSeparator ();

    // Newsletter stuff
    {
      aMenuTree.createRootItem (new PagePublicNewsletterSubscribe (CMenuPublic.MENU_NEWSLETTER_SUBSCRIBE));
      aMenuTree.createRootItem (new PagePublicNewsletterUnsubscribe (CMenuPublic.MENU_NEWSLETTER_UNSUBSCRIBE))
               .attrs ()
               .putIn (CMenuPublic.FLAG_FOOTER_COL2, true);
    }

    // Contact form
    {
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

    aMenuTree.createRootSeparator ();

    // External stuff
    {
      aMenuTree.createRootItem (CMenuPublic.MENU_PEPPOL_SERVICE_DESK,
                                new SimpleURL ("https://openpeppol.atlassian.net/servicedesk/customer/portal/1"),
                                new ConstantHasDisplayText ("OpenPEPPOL Service Desk (external)"));
    }

    // Set default
    aMenuTree.setDefaultMenuItemID (CMenuPublic.MENU_NEWS);
  }
}
