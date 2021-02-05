/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.CommonsLinkedHashMap;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.error.IError;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.forms.EHCFormMethod;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.domain.EIDType;
import com.helger.peppol.domain.KVPair;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.peppolid.peppol.doctype.EPredefinedDocumentTypeIdentifier;
import com.helger.peppolid.peppol.pidscheme.EPredefinedParticipantIdentifierScheme;
import com.helger.peppolid.peppol.process.EPredefinedProcessIdentifier;
import com.helger.photon.bootstrap4.alert.BootstrapBox;
import com.helger.photon.bootstrap4.alert.EBootstrapAlertType;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.form.EBootstrapFormType;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.html.select.HCExtSelect;
import com.helger.photon.uicore.page.WebPageExecutionContext;

/**
 * Verify identifiers according to some predefined rules.
 *
 * @author Philip Helger
 */
public class PagePublicToolsIdentifierInformation extends AbstractAppWebPage
{
  public static final String FIELD_ID_TYPE = "type";
  public static final String FIELD_ID_VALUE = "value";

  private static final Logger LOGGER = LoggerFactory.getLogger (PagePublicToolsIdentifierInformation.class);

  public PagePublicToolsIdentifierInformation (@Nonnull @Nonempty final String sID)
  {
    super (sID, "ID information");
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final FormErrorList aFormErrors = new FormErrorList ();
    final boolean bShowInput = true;
    boolean bShowHeader = true;

    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      // Validate fields
      final String sIDType = aWPEC.params ().getAsString (FIELD_ID_TYPE);
      final EIDType eIDType = EIDType.getFromIDOrNull (sIDType);

      final String sIDValue = aWPEC.params ().getAsStringTrimmed (FIELD_ID_VALUE);

      if (StringHelper.hasNoText (sIDType))
        aFormErrors.addFieldError (FIELD_ID_TYPE, "An identifier type must be selected");
      else
        if (eIDType == null)
          aFormErrors.addFieldError (FIELD_ID_TYPE, "An identifier type must be selected");

      if (StringHelper.hasNoText (sIDValue))
        aFormErrors.addFieldError (FIELD_ID_VALUE, "An identifier value must be provided");

      if (aFormErrors.isEmpty ())
      {
        bShowHeader = false;
        LOGGER.info ("Checking identifier type " + eIDType.name () + " for value '" + sIDValue + "'");

        final ErrorList aIDErrors = new ErrorList ();
        final ICommonsList <KVPair> aDetails = new CommonsArrayList <> ();
        eIDType.getValidator ().validate (sIDValue, aIDErrors, aDetails);

        final IHCNode aDetailsNode;
        if (aDetails.isEmpty ())
          aDetailsNode = null;
        else
        {
          final HCUL aULDetails = new HCUL ();
          for (final KVPair aPair : aDetails)
            aULDetails.addAndReturnItem (aPair.getKey () + ": ")
                      .addChild (aPair.hasValue () ? code (aPair.getValue ()) : badgeInfo ("empty string"));
          aDetailsNode = new HCNodeList ().addChild (div ("Determined details are:")).addChild (aULDetails);
        }

        if (aIDErrors.containsNoError ())
        {
          final IHCNode aWarningsNode;
          final EBootstrapAlertType eAlertType;
          if (aIDErrors.isEmpty ())
          {
            // No errors, no warnings
            eAlertType = EBootstrapAlertType.SUCCESS;
            aWarningsNode = null;
          }
          else
          {
            // No errors but warnings
            eAlertType = EBootstrapAlertType.WARNING;
            final HCUL aUL = new HCUL ();
            for (final IError aError : aIDErrors)
              aUL.addItem (aError.getAsString (aDisplayLocale));
            aWarningsNode = new HCNodeList ().addChild (div ("But there are some warnings:")).addChild (aUL);
          }
          aNodeList.addChild (new BootstrapBox (eAlertType).addChild (div ("The identifier ").addChild (code (sIDValue))
                                                                                             .addChild (" is valid according to the rules of '" +
                                                                                                        eIDType.getDisplayName () +
                                                                                                        "'"))
                                                           .addChild (aWarningsNode)
                                                           .addChild (aDetailsNode));
          LOGGER.info (aIDErrors.isEmpty () ? "success" : "success with warning");
        }
        else
        {
          // At least one error
          final HCUL aUL = new HCUL ();
          for (final IError aError : aIDErrors)
            aUL.addItem (aError.getAsString (aDisplayLocale));
          aNodeList.addChild (error (div ("The identifier ").addChild (code (sIDValue))
                                                            .addChild (" is NOT valid according to the rules of '" +
                                                                       eIDType.getDisplayName () +
                                                                       "'")).addChild (aUL).addChild (aDetailsNode));
          LOGGER.info ("failure");
        }
      }
    }

    if (bShowInput)
    {
      if (bShowHeader)
      {
        final ICommonsOrderedMap <String, String> aRuleSets = new CommonsLinkedHashMap <> ();
        aRuleSets.put ("Peppol Policy for use of Identifiers v4.0", "https://docs.peppol.eu/edelivery/");
        aRuleSets.put ("Peppol Participant Identifier Code List v" + EPredefinedParticipantIdentifierScheme.CODE_LIST_VERSION,
                       "https://docs.peppol.eu/edelivery/codelists/");
        aRuleSets.put ("Peppol Document Type Identifier Code List v" + EPredefinedDocumentTypeIdentifier.CODE_LIST_VERSION,
                       "https://docs.peppol.eu/edelivery/codelists/");
        aRuleSets.put ("Peppol Process Identifier Code List v" + EPredefinedProcessIdentifier.CODE_LIST_VERSION,
                       "https://docs.peppol.eu/edelivery/codelists/");
        final HCUL aULRules = new HCUL ();
        for (final Map.Entry <String, String> aEntry : aRuleSets.entrySet ())
          aULRules.addAndReturnItem (aEntry.getKey () + " - ")
                  .addChild (new HCA (new SimpleURL (aEntry.getValue ())).setTargetBlank ().addChild ("see details"));
        aNodeList.addChild (info (div ("This page lets you verify identifiers against official rules. Current rule sets are:")).addChild (aULRules));
      }

      final HCExtSelect aTypeSelect = new HCExtSelect (new RequestField (FIELD_ID_TYPE));
      for (final EIDType e : EIDType.values ())
        aTypeSelect.addOption (e.getID (), e.getDisplayName ());
      aTypeSelect.addOptionPleaseSelect (aDisplayLocale);

      final BootstrapForm aForm = aNodeList.addAndReturnChild (getUIHandler ().createFormSelf (aWPEC)
                                                                              .setFormType (EBootstrapFormType.DEFAULT)
                                                                              .setMethod (EHCFormMethod.GET)
                                                                              .setLeft (2));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier type")
                                                   .setCtrl (aTypeSelect)
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_ID_TYPE)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier value")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_ID_VALUE)).setPlaceholder ("scheme::value"))
                                                   .setHelpText (div ("The identifier value to be checked for consistency. It MUST contain the scheme (like ").addChild (code (PeppolIdentifierHelper.DOCUMENT_TYPE_SCHEME_BUSDOX_DOCID_QNS))
                                                                                                                                                              .addChild (") AND the value as one long string"))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_ID_VALUE)));

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
      aToolbar.addSubmitButton ("Check identifier");
    }
  }
}
