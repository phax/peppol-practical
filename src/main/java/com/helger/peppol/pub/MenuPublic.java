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
package com.helger.peppol.pub;

import org.jspecify.annotations.NonNull;

import com.helger.annotation.concurrent.Immutable;
import com.helger.io.resource.ClassPathResource;
import com.helger.peppol.app.CPPApp;
import com.helger.peppol.sharedui.page.pub.CSharedUIMenuPublic;
import com.helger.peppol.sharedui.page.pub.PagePublicContact;
import com.helger.peppol.sharedui.page.pub.PagePublicToolsDDD;
import com.helger.peppol.sharedui.page.pub.PagePublicToolsIdentifierInformation;
import com.helger.peppol.sharedui.page.pub.PagePublicToolsParticipantCheck;
import com.helger.peppol.sharedui.page.pub.PagePublicToolsParticipantCheckBelgium;
import com.helger.peppol.sharedui.page.pub.PagePublicToolsParticipantInformation;
import com.helger.peppol.ui.page.AppPageViewExternal;
import com.helger.photon.bootstrap4.pages.security.BasePageSecurityChangePassword;
import com.helger.photon.core.menu.IMenuItemPage;
import com.helger.photon.core.menu.IMenuTree;
import com.helger.photon.core.menu.filter.MenuObjectFilterNoUserLoggedIn;
import com.helger.photon.core.menu.filter.MenuObjectFilterUserLoggedIn;
import com.helger.photon.uicore.page.system.BasePageShowChildren;
import com.helger.text.display.ConstantHasDisplayText;
import com.helger.url.SimpleURL;

@Immutable
public final class MenuPublic
{
  private MenuPublic ()
  {}

  public static void init (@NonNull final IMenuTree aMenuTree)
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
      final IMenuItemPage aSetup = aMenuTree.createRootItem (new BasePageShowChildren <> (CMenuPublic.MENU_DOCS,
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
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_PEPPOL_DIRECTORY,
                                                     "Peppol Directory",
                                                     new ClassPathResource ("viewpages/en/docs_peppol_directory.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_PEPPOL_CERT_UPDATE,
                                                     "Peppol Certificate update",
                                                     new ClassPathResource ("viewpages/en/docs_peppol_cert_update.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_PEPPOL_MLR,
                                                     "Peppol MLR / MLS",
                                                     new ClassPathResource ("viewpages/en/docs_peppol_mlr.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_PEPPOL_FIREWALL,
                                                     "Peppol Firewall requirements",
                                                     new ClassPathResource ("viewpages/en/docs_peppol_firewall.xml")));
      aMenuTree.createSeparator (aSetup);
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_PEPPOL_DICT,
                                                     "Peppol Dictionary/Abbreviations",
                                                     new ClassPathResource ("viewpages/en/docs_peppol_dict.xml")));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_DOCS_SOFTWARE_VENDORS,
                                                     "Software Vendors and Solutions",
                                                     new ClassPathResource ("viewpages/en/docs_software_vendors.xml")));

      // Not needed any more
      if (false)
        aMenuTree.createItem (aSetup,
                              new AppPageViewExternal ("docs-sml-migration",
                                                       "SML migration information",
                                                       new ClassPathResource ("viewpages/en/invisible/docs_sml_migration.xml")));
    }

    // Tools stuff
    {
      final IMenuItemPage aSetup = aMenuTree.createRootItem (new BasePageShowChildren <> (CMenuPublic.MENU_TOOLS,
                                                                                          "Tools",
                                                                                          aMenuTree));
      final IMenuItemPage aCheck = aMenuTree.createItem (aSetup,
                                                         new PagePublicToolsParticipantCheck (CSharedUIMenuPublic.MENU_TOOLS_PARTICIPANT_CHECK));
      {
        aMenuTree.createItem (aCheck,
                              new PagePublicToolsParticipantCheckBelgium (CSharedUIMenuPublic.MENU_TOOLS_PARTICIPANT_CHECK_BE));
      }
      aMenuTree.createItem (aSetup,
                            new PagePublicToolsParticipantInformation (CSharedUIMenuPublic.MENU_TOOLS_PARTICIPANT_INFO,
                                                                       CPPApp.DEFAULT_USER_AGENT));
      aMenuTree.createItem (aSetup, new PagePublicToolsIdentifierInformation (CSharedUIMenuPublic.MENU_TOOLS_ID_INFO));
      aMenuTree.createItem (aSetup, new PagePublicToolsSMPSML (CMenuPublic.MENU_TOOLS_SMP_SML));
      aMenuTree.createItem (aSetup, new PagePublicToolsTestEndpoints (CMenuPublic.MENU_TOOLS_TEST_ENDPOINTS));
      aMenuTree.createItem (aSetup,
                            new AppPageViewExternal (CMenuPublic.MENU_TOOLS_REST_API,
                                                     "REST API",
                                                     new ClassPathResource ("viewpages/en/rest_api.xml")));

      aMenuTree.createItem (aSetup, new PagePublicToolsDDD (CSharedUIMenuPublic.MENU_TOOLS_DDD));
    }

    // Validation stuff
    {
      final IMenuItemPage aValidation = aMenuTree.createRootItem (new BasePageShowChildren <> (CMenuPublic.MENU_VALIDATION,
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

    // Country specific stuff
    {
      final IMenuItemPage aCountries = aMenuTree.createRootItem (new BasePageShowChildren <> (CMenuPublic.MENU_COUNTRIES,
                                                                                              "Countries",
                                                                                              aMenuTree));
      aMenuTree.createItem (aCountries,
                            new AppPageViewExternal (CMenuPublic.MENU_COUNTRY_DE,
                                                     "Germany",
                                                     new ClassPathResource ("viewpages/en/country_de.xml")));
    }

    aMenuTree.createRootItem (CMenuPublic.MENU_GITHUB_PEPPOL,
                              new SimpleURL ("https://github.com/phax/peppol"),
                              new ConstantHasDisplayText ("Open Source Peppol (external)"));

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
      aMenuTree.createRootItem (new BasePageSecurityChangePassword <> (CMenuPublic.MENU_CHANGE_PASSWORD))
               .setDisplayFilter (new MenuObjectFilterUserLoggedIn ());
    }

    // External stuff
    {
      aMenuTree.createRootSeparator ();
      aMenuTree.createRootItem (CMenuPublic.MENU_PEPPOL_SERVICE_DESK,
                                new SimpleURL ("https://openpeppol.atlassian.net/servicedesk/customer/portal/1"),
                                new ConstantHasDisplayText ("OpenPeppol Service Desk (external)"));

      aMenuTree.createRootSeparator ();
      aMenuTree.createRootItem (CMenuPublic.MENU_PEPPOLHUB,
                                new SimpleURL ("https://shop.peppolhub.com/"),
                                new ConstantHasDisplayText ("phoss Webinars and Recordings (external)"));

      aMenuTree.createRootSeparator ();
      aMenuTree.createRootItem (CMenuPublic.MENU_THE_INVOICING_HUB,
                                new SimpleURL ("https://www.theinvoicinghub.com"),
                                new ConstantHasDisplayText ("The Invoicing Hub (external)"));
    }

    // Set default
    aMenuTree.setDefaultMenuItemID (CMenuPublic.MENU_NEWS);
  }
}
