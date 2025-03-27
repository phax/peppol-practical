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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.commons.url.SimpleURL;
import com.helger.html.hc.html.forms.EHCFormMethod;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.sharedui.domain.ISMLConfiguration;
import com.helger.peppol.sharedui.domain.SMPQueryParams;
import com.helger.peppol.sharedui.mgr.ISMLConfigurationManager;
import com.helger.peppol.sharedui.mgr.SharedUIMetaManager;
import com.helger.peppol.sharedui.ui.AbstractAppWebPage;
import com.helger.peppol.sml.ESML;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.photon.bootstrap4.CBootstrapCSS;
import com.helger.photon.bootstrap4.button.BootstrapButton;
import com.helger.photon.bootstrap4.button.BootstrapLinkButton;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.famfam.EFamFamIcon;

public class PagePublicToolsParticipantCheckBelgium extends AbstractAppWebPage
{
  public static final String FIELD_ID_VALUE = "value";

  private static final Logger LOGGER = LoggerFactory.getLogger (PagePublicToolsParticipantCheckBelgium.class);

  public PagePublicToolsParticipantCheckBelgium (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Belgium Participant Check");
  }

  private void _checkParticipant (@Nonnull final WebPageExecutionContext aWPEC,
                                  @Nonnull final String sParticipantIDValue)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final ISMLConfigurationManager aSMLConfigurationMgr = SharedUIMetaManager.getSMLConfigurationMgr ();

    LOGGER.info ("Performing Belgium Participant Check for '" + sParticipantIDValue + "'");

    final ISMLConfiguration aSMLConfiguration = aSMLConfigurationMgr.getSMLInfoOfID (ESML.DIGIT_PRODUCTION.getID ());
    final SMPQueryParams aSMPQP_CBE = SMPQueryParams.createForSMLOrNull (aSMLConfiguration,
                                                                         PeppolIdentifierHelper.DEFAULT_PARTICIPANT_SCHEME,
                                                                         "0208:" + sParticipantIDValue,
                                                                         false);
    final boolean bIsCBE = aSMPQP_CBE == null ? false : aSMPQP_CBE.isSMPRegisteredInDNS ();

    final SMPQueryParams aSMPQP_VAT = SMPQueryParams.createForSMLOrNull (aSMLConfiguration,
                                                                         PeppolIdentifierHelper.DEFAULT_PARTICIPANT_SCHEME,
                                                                         "9925:" + sParticipantIDValue,
                                                                         false);
    final boolean bIsVAT = aSMPQP_VAT == null ? false : aSMPQP_VAT.isSMPRegisteredInDNS ();
    if (bIsCBE || bIsVAT)
    {
      if (bIsCBE)
      {
        final IParticipantIdentifier aPID = aSMPQP_CBE.getParticipantID ();
        aNodeList.addChild (success ("The Belgium Enterprise is registered in the Peppol Production Network with their CBE number and the Participant ID ").addChild (code (aPID.getURIEncoded ())));

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

        final ISimpleURL aDirectoryURL = new SimpleURL ("https://directory.peppol.eu/participant/" +
                                                        aPID.getURIPercentEncoded ());
        aToolbar.addChild (new BootstrapLinkButton ().addChild ("Peppol Directory Lookup")
                                                     .setHref (aDirectoryURL)
                                                     .setTargetBlank ());
        aNodeList.addChild (aToolbar);
      }
      if (bIsVAT)
      {
        final IParticipantIdentifier aPID = aSMPQP_VAT.getParticipantID ();
        aNodeList.addChild (success ("The Belgium Enterprise is registered in the Peppol Production Network with their VATIN and the Participant ID ").addChild (code (aPID.getURIEncoded ())));

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

        final ISimpleURL aDirectoryURL = new SimpleURL ("https://directory.peppol.eu/participant/" +
                                                        aPID.getURIPercentEncoded ());
        aToolbar.addChild (new BootstrapLinkButton ().addChild ("Peppol Directory Lookup")
                                                     .setHref (aDirectoryURL)
                                                     .setTargetBlank ());
        aNodeList.addChild (aToolbar);
      }
    }
    else

    {
      aNodeList.addChild (error ("The Belgium Enterprise was not found in the Peppol Production Network - neither with the CBE number nor with the VAT number"));
    }
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final FormErrorList aFormErrors = new FormErrorList ();
    final boolean bShowInput = true;

    aNodeList.addChild (info ("With this page you can check if a Belgium enterprise is registered to the Peppol Production Network." +
                              " You can test with either a ").addChild (strong ("CBE number"))
                                                             .addChild (" or a ")
                                                             .addChild (strong ("VAT number")));

    String sParticipantIDValue = null;
    boolean bQueried = false;
    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      // Validate fields
      sParticipantIDValue = StringHelper.trim (aWPEC.params ().getAsString (FIELD_ID_VALUE));

      if (StringHelper.hasNoText (sParticipantIDValue))
        aFormErrors.addFieldError (FIELD_ID_VALUE, "Please provide an value to check");

      if (aFormErrors.isEmpty ())
      {
        _checkParticipant (aWPEC, sParticipantIDValue);
        bQueried = true;
      }
    }

    if (bShowInput)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (getUIHandler ().createFormSelf (aWPEC)
                                                                              .setMethod (EHCFormMethod.GET)
                                                                              .setLeft (3, 3, 3, 2, 2));
      if (bQueried)
      {
        // Just some spacing
        aForm.addClass (CBootstrapCSS.MT_5);
      }

      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Identifier value")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_ID_VALUE,
                                                                                           sParticipantIDValue)).setPlaceholder ("Identifier value"))
                                                   .setHelpText (div ("This should either be the CBE number or the VAT number of the enterprise your want to check"))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_ID_VALUE)));

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
      aToolbar.addSubmitButton ("Check Existence");
    }
  }
}
