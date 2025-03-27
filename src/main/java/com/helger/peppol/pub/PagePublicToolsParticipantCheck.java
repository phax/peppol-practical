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

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.html.forms.EHCFormMethod;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCLabel;
import com.helger.html.hc.html.forms.HCRadioButton;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.sharedui.domain.ISMLConfiguration;
import com.helger.peppol.sharedui.domain.SMPQueryParams;
import com.helger.peppol.sharedui.mgr.ISMLConfigurationManager;
import com.helger.peppol.sharedui.mgr.SharedUIMetaManager;
import com.helger.peppol.sharedui.page.AbstractAppWebPage;
import com.helger.peppol.sml.ESML;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.factory.SimpleIdentifierFactory;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.photon.bootstrap4.CBootstrapCSS;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.button.BootstrapLinkButton;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormCheck;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.form.RequestFieldBooleanMultiValue;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.famfam.EFamFamIcon;

public class PagePublicToolsParticipantCheck extends AbstractAppWebPage
{
  public static final String SML_PROD = "smlprod";
  public static final String SML_TEST = "smltest";
  public static final String FIELD_ID_SCHEME = "scheme";
  public static final String FIELD_ID_VALUE = "value";
  public static final String FIELD_SML = "sml";

  public static final String DEFAULT_ID_SCHEME = PeppolIdentifierHelper.DEFAULT_PARTICIPANT_SCHEME;

  public PagePublicToolsParticipantCheck (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Participant Check");
  }

  private boolean _checkParticipant (@Nonnull final WebPageExecutionContext aWPEC,
                                     @Nonnull final ISMLConfiguration aSMLConfiguration,
                                     @Nonnull final SMPQueryParams aSMPQueryParams,
                                     final boolean bIsProdSML)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final IParticipantIdentifier aPID = aSMPQueryParams.getParticipantID ();

    final boolean bFoundAny = aSMPQueryParams.isSMPRegisteredInDNS ();
    if (bFoundAny)
    {
      aNodeList.addChild (success ("The Peppol Participant ID ").addChild (code (aPID.getURIEncoded ()))
                                                                .addChild (" is registered in the Peppol ")
                                                                .addChild (strong (bIsProdSML ? "Production"
                                                                                              : "Test/Pilot"))
                                                                .addChild (" Network"));

      final BootstrapButtonToolbar aToolbar = new BootstrapButtonToolbar (aWPEC);
      aToolbar.addChild (new BootstrapButton ().addChild ("Participant Information")
                                               .setIcon (EFamFamIcon.USER_GREEN)
                                               .setOnClick (aWPEC.getLinkToMenuItem (CMenuPublic.MENU_TOOLS_PARTICIPANT_INFO)
                                                                 .add (PagePublicToolsParticipantInformation.FIELD_ID_SCHEME,
                                                                       aPID.getScheme ())
                                                                 .add (PagePublicToolsParticipantInformation.FIELD_ID_VALUE,
                                                                       aPID.getValue ())
                                                                 .add (PagePublicToolsParticipantInformation.FIELD_SML,
                                                                       aSMLConfiguration.getID ())
                                                                 .add (CPageParam.PARAM_ACTION,
                                                                       CPageParam.ACTION_PERFORM)));

      final ISimpleURL aDirectoryURL = new SimpleURL ((bIsProdSML ? "https://directory.peppol.eu"
                                                                  : "https://test-directory.peppol.eu") +
                                                      "/participant/" +
                                                      aPID.getURIPercentEncoded ());
      aToolbar.addChild (new BootstrapLinkButton ().addChild ("Peppol Directory Lookup")
                                                   .setHref (aDirectoryURL)
                                                   .setTargetBlank ());
      aNodeList.addChild (aToolbar);
    }
    else
    {
      aNodeList.addChild (error ("The Peppol Participant ID ").addChild (code (aPID.getURIEncoded ()))
                                                              .addChild (" is NOT registered in the Peppol ")
                                                              .addChild (strong (bIsProdSML ? "Production"
                                                                                            : "Test/Pilot"))
                                                              .addChild (" Network"));
    }
    return bFoundAny;
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final ISMLConfigurationManager aSMLConfigurationMgr = SharedUIMetaManager.getSMLConfigurationMgr ();
    final FormErrorList aFormErrors = new FormErrorList ();
    final boolean bShowInput = true;

    aNodeList.addChild (info ("This page lets you check, if a Peppol Participant ID is registered or not. This check is based on querying the DNS and is therefore more reliable then checking the Peppol Directory"));

    String sParticipantIDScheme = DEFAULT_ID_SCHEME;
    String sParticipantIDValue = null;
    String sSML = null;
    boolean bQueried = false;
    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      // Validate fields
      sParticipantIDScheme = StringHelper.trim (aWPEC.params ().getAsString (FIELD_ID_SCHEME));
      sParticipantIDValue = StringHelper.trim (aWPEC.params ().getAsString (FIELD_ID_VALUE));
      // Allow for multiple values to be provided, separated by whitespace
      final ICommonsList <String> aPIDValues = RegExHelper.getSplitToList (sParticipantIDValue, "\\s+");
      sSML = aWPEC.params ().getAsStringTrimmed (FIELD_SML);
      final boolean bIsProdSML = SML_PROD.equals (sSML);
      final ISMLConfiguration aSMLConfiguration = aSMLConfigurationMgr.getSMLInfoOfID (bIsProdSML ? ESML.DIGIT_PRODUCTION.getID ()
                                                                                                  : ESML.DIGIT_TEST.getID ());
      final IIdentifierFactory aIF = aSMLConfiguration != null ? aSMLConfiguration.getSMPIdentifierType ()
                                                                                  .getIdentifierFactory ()
                                                               : SimpleIdentifierFactory.INSTANCE;

      if (StringHelper.hasNoText (sParticipantIDScheme))
        aFormErrors.addFieldError (FIELD_ID_SCHEME, "Please provide an identifier scheme");
      else
        if (!aIF.isParticipantIdentifierSchemeValid (sParticipantIDScheme))
          aFormErrors.addFieldError (FIELD_ID_SCHEME,
                                     "The participant identifier scheme '" + sParticipantIDScheme + "' is not valid!");

      if (aPIDValues.isEmpty ())
        aFormErrors.addFieldError (FIELD_ID_VALUE, "Please provide an identifier value");
      else
      {
        // Check each provided value
        for (final String sPIDValue : aPIDValues)
          if (!aIF.isParticipantIdentifierValueValid (sParticipantIDScheme, sPIDValue))
            aFormErrors.addFieldError (FIELD_ID_VALUE,
                                       "The participant identifier value '" + sPIDValue + "' is not valid!");
      }

      if (aSMLConfiguration == null)
        aFormErrors.addFieldError (FIELD_SML, "A valid SML must be selected!");

      if (aFormErrors.isEmpty ())
      {
        // Check each provided value
        for (final String sPIDValue : aPIDValues)
        {
          final IParticipantIdentifier aPID = aIF.createParticipantIdentifier (sParticipantIDScheme, sPIDValue);
          if (aPID == null)
          {
            aNodeList.addChild (error ("The combination of participant identifier scheme (").addChild (code (sParticipantIDScheme))
                                                                                            .addChild (") and value (")
                                                                                            .addChild (code (sPIDValue))
                                                                                            .addChild (") is invalid!"));
          }
          else
          {
            final SMPQueryParams aSMPQueryParams = SMPQueryParams.createForSMLOrNull (aSMLConfiguration,
                                                                                      sParticipantIDScheme,
                                                                                      sPIDValue,
                                                                                      true);
            if (aSMPQueryParams != null)
            {
              _checkParticipant (aWPEC, aSMLConfiguration, aSMPQueryParams, bIsProdSML);
              bQueried = true;
            }
          }
        }
      }
    }

    if (bShowInput)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (getUIHandler ().createFormSelf (aWPEC)
                                                                              .setMethod (EHCFormMethod.POST)
                                                                              .setLeft (3, 3, 3, 2, 2));
      if (bQueried)
      {
        // Just some spacing
        aForm.addClass (CBootstrapCSS.MT_5);
      }

      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier scheme")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_ID_SCHEME,
                                                                                           sParticipantIDScheme)).setPlaceholder ("Identifier scheme"))
                                                   .setHelpText (div ("The most common identifier scheme is ").addChild (code (DEFAULT_ID_SCHEME)))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_ID_SCHEME)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier value")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_ID_VALUE,
                                                                                           sParticipantIDValue)).setPlaceholder ("Identifier value"))
                                                   .setHelpText (div ("The identifier value must look like ").addChild (code ("9915:test")))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_ID_VALUE)));
      {
        final HCRadioButton aRB1 = new HCRadioButton (new RequestFieldBooleanMultiValue (FIELD_SML, SML_PROD, true));
        final HCRadioButton aRB2 = new HCRadioButton (new RequestFieldBooleanMultiValue (FIELD_SML, SML_TEST, false));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Peppol Network")
                                                     .setCtrl (new BootstrapFormCheck (aRB1).addChild (new HCLabel ().setFor (aRB1)
                                                                                                                     .addChild ("Production")),
                                                               new BootstrapFormCheck (aRB2).addChild (new HCLabel ().setFor (aRB2)
                                                                                                                     .addChild ("Test/Pilot")))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SML)));

      }

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
      aToolbar.addSubmitButton ("Check Existence");
    }
  }
}
