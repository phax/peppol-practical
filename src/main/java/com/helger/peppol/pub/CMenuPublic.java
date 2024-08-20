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

import javax.annotation.concurrent.Immutable;

/**
 * Menu items for the public application
 *
 * @author Philip Helger
 */
@Immutable
public final class CMenuPublic
{
  public static final String MENU_INDEX = "index";
  public static final String MENU_DOCS = "docs";
  public static final String MENU_DOCS_SETUP_AP_PH = "docs-setup-ap";
  public static final String MENU_DOCS_SETUP_SMP_CIPA = "docs-setup-smp";
  public static final String MENU_DOCS_SETUP_SMP_PHOSS = "docs-setup-smp-ph";
  public static final String MENU_DOCS_SMP_SML_INTERPLAY = "docs-smp-sml-interplay";
  public static final String MENU_DOCS_SML_SUPPORT = "docs-sml-support";
  public static final String MENU_DOCS_DOC_EXCHANGE = "docs-doc-exchange";
  public static final String MENU_DOCS_PEPPOL_PKI = "docs-peppol-pki";
  public static final String MENU_DOCS_PEPPOL_CERT_UPDATE = "docs-peppol-cert-update";
  public static final String MENU_DOCS_PEPPOL_MLR = "docs-peppol-mlr";
  public static final String MENU_DOCS_PEPPOL_DICT = "docs-peppol-dict";
  public static final String MENU_DOCS_PEPPOL_FIREWALL = "docs-peppol-firewall";
  public static final String MENU_DOCS_SOFTWARE_VENDORS = "docs-software-vendors";
  public static final String MENU_NEWS = "news";
  public static final String MENU_TOOLS = "tools";
  public static final String MENU_TOOLS_PARTICIPANT_CHECK = "tools-pid-check";
  public static final String MENU_TOOLS_PARTICIPANT_CHECK_BE = "tools-pid-check-be";
  public static final String MENU_TOOLS_PARTICIPANT_INFO = "tools-participant";
  public static final String MENU_TOOLS_ID_INFO = "tools-id-info";
  public static final String MENU_TOOLS_SMP_SML = "tools-smp-sml";
  public static final String MENU_TOOLS_TEST_ENDPOINTS = "tools-test-endpoints";
  public static final String MENU_TOOLS_REST_API = "tools-rest-api";
  public static final String MENU_VALIDATION = "validation";
  public static final String MENU_VALIDATION_UPLOAD = "validation-upload";
  public static final String MENU_VALIDATION_DVS = "validation-ws2";
  public static final String MENU_EN16931 = "en16931";
  public static final String MENU_LOGIN = "login";
  public static final String MENU_SIGN_UP = "signup";
  public static final String MENU_CHANGE_PASSWORD = "changepassword";
  public static final String MENU_NEWSLETTER_SUBSCRIBE = "newsletter-subscribe";
  public static final String MENU_CONTACT = "contact";
  public static final String MENU_PEPPOL_SERVICE_DESK = "peppol-service-desk";
  public static final String MENU_THE_INVOICING_HUB = "theinvoicinghub";

  // footer
  public static final String MENU_NEWSLETTER_UNSUBSCRIBE = "newsletter-unsubscribe";

  // flags
  public static final String FLAG_FOOTER_COL1 = "footercol1";
  public static final String FLAG_FOOTER_COL2 = "footercol2";
  public static final String FLAG_FOOTER_COL3 = "footercol3";

  private CMenuPublic ()
  {}
}
