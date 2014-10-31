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
package com.helger.peppol.page.pub;

import java.net.URL;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.busdox.servicemetadata.manageservicemetadataservice._1.BadRequestFault;
import org.busdox.servicemetadata.manageservicemetadataservice._1.InternalErrorFault;
import org.busdox.servicemetadata.manageservicemetadataservice._1.UnauthorizedFault;

import com.helger.bootstrap3.alert.BootstrapInfoBox;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.form.BootstrapForm;
import com.helger.bootstrap3.form.BootstrapFormGroup;
import com.helger.bootstrap3.form.BootstrapHelpBlock;
import com.helger.bootstrap3.form.EBootstrapFormType;
import com.helger.bootstrap3.nav.BootstrapTabBox;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.URLUtils;
import com.helger.html.hc.CHCParam;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.page.ui.SMLSelect;
import com.helger.validation.error.FormErrors;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webctrls.page.AbstractWebPageExt;

import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.peppol.sml.ISMLInfo;
import eu.europa.ec.cipa.sml.client.ManageServiceMetadataServiceCaller;

public class PagePublicToolsSMPSML extends AbstractWebPageExt <WebPageExecutionContext>
{
  private static final String FIELD_SML = "sml";
  private static final String FIELD_SMP_ID = "smpid";
  private static final String FIELD_PHYSICAL_ADDRESS = "physicaladdr";
  private static final String FIELD_LOGICAL_ADDRESS = "logicaladdr";
  private static final String SUBACTION_SMP_REGISTER = "smpregister";

  private static final String PATTERN_SMP_ID = "[a-zA-Z0-9-]+";
  private static final String PATTERN_IPV4 = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b";

  public PagePublicToolsSMPSML (@Nonnull @Nonempty final String sID)
  {
    super (sID, "SMP - SML tools");
  }

  @Nullable
  private static ISMLInfo _getSML (@Nonnull final String sSML)
  {
    if (StringHelper.hasText (sSML))
      for (final ESML eSML : ESML.values ())
        if (eSML.name ().equals (sSML))
          return eSML;
    return null;
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final FormErrors aFormErrors = new FormErrors ();
    final boolean bShowInput = true;

    if (aWPEC.hasAction (ACTION_PERFORM))
    {
      if (aWPEC.hasSubAction (SUBACTION_SMP_REGISTER))
      {
        final String sSML = aWPEC.getAttributeAsString (FIELD_SML);
        final ISMLInfo aSML = _getSML (sSML);
        final String sSMPID = aWPEC.getAttributeAsString (FIELD_SMP_ID);
        final String sPhysicalAddress = aWPEC.getAttributeAsString (FIELD_PHYSICAL_ADDRESS);
        final String sLogicalAddress = aWPEC.getAttributeAsString (FIELD_LOGICAL_ADDRESS);

        if (aSML == null)
          aFormErrors.addFieldError (FIELD_SML, "A valid SML must be selected!");

        if (StringHelper.hasNoText (sSMPID))
          aFormErrors.addFieldError (FIELD_SMP_ID, "A non-empty SMP ID must be provided!");
        else
          if (!RegExHelper.stringMatchesPattern (PATTERN_SMP_ID, sSMPID))
            aFormErrors.addFieldError (FIELD_SMP_ID,
                                       "The provided SMP ID contains invalid characters. It must match the following regular expression: " +
                                           PATTERN_SMP_ID);

        if (StringHelper.hasNoText (sPhysicalAddress))
          aFormErrors.addFieldError (FIELD_PHYSICAL_ADDRESS, "A physical address must be provided!");
        else
          if (!RegExHelper.stringMatchesPattern (PATTERN_IPV4, sPhysicalAddress))
            aFormErrors.addFieldError (FIELD_PHYSICAL_ADDRESS,
                                       "The provided physical address does not seem to be an IPv4 address!");

        if (StringHelper.hasNoText (sLogicalAddress))
          aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS, "A logical address must be provided!");
        else
        {
          final URL aURL = URLUtils.getAsURL (sLogicalAddress);
          if (aURL == null)
            aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS, "The provided logical address seems not be a URL!");
          else
          {
            if (!"http".equals (aURL.getProtocol ()))
              aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                         "The provided logical address must use the 'http' protocol and may not use the '" +
                                             aURL.getProtocol () +
                                             "' protocol. According to the SMP specification, no other protocols than 'http' are allowed!");
            // -1 means default port
            if (aURL.getPort () != 80 && aURL.getPort () != -1)
              aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                         "The provided logical address must use the default http port 80 and not port " +
                                             aURL.getPort () +
                                             ". According to the SMP specification, no other ports are allowed!");
            if (StringHelper.hasText (aURL.getPath ()) && !"/".equals (aURL.getPath ()))
              aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                         "The provided logical address may not contain a path (" +
                                             aURL.getPath () +
                                             ") because according to the SMP specifications it must run in the root (/) path!");
          }
        }

        if (aFormErrors.isEmpty ())
        {
          // test before
          if (false)
          {
            final ManageServiceMetadataServiceCaller aCaller = new ManageServiceMetadataServiceCaller (aSML.getManageServiceMetaDataEndpointAddress ());
            try
            {
              aCaller.create (sSMPID, sPhysicalAddress, sLogicalAddress);
            }
            catch (BadRequestFault | InternalErrorFault | UnauthorizedFault ex)
            {
              // TODO
            }
          }
        }
      }
      // Validate fields
    }

    if (bShowInput)
    {
      final BootstrapTabBox aTabBox = new BootstrapTabBox ();

      final BootstrapForm aForm = new BootstrapForm (EBootstrapFormType.HORIZONTAL).setAction (aWPEC.getSelfHref ());
      aForm.setLeft (3);
      aForm.addChild (new BootstrapInfoBox ().addChild ("Register a new SMP to the SML. This must only be done once per SMP!"));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML")
                                                   .setCtrl (new SMLSelect (new RequestField (FIELD_SML)))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_SML)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP ID")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_SMP_ID)).setPlaceholder ("Your SMP ID"),
                                                             new BootstrapHelpBlock ().addChild ("This is the unique ID your SMP will have inside the SML. All continuing operations must use this ID. You can choose this ID yourself but please make sure it only contains characters, numbers and the hyphen character. All uppercase names are appreciated!"))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_SMP_ID)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Physical address")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_PHYSICAL_ADDRESS)).setPlaceholder ("The IPv4 address of your SMP. E.g. 1.2.3.4"))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_PHYSICAL_ADDRESS)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Logical address")
                                                   .setCtrl (new HCEdit (new RequestField (FIELD_LOGICAL_ADDRESS)).setPlaceholder ("The domain name of your SMP server. E.g. http://smp.example.org"))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_LOGICAL_ADDRESS)));

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CHCParam.PARAM_ACTION, ACTION_PERFORM);
      aToolbar.addHiddenField (CHCParam.PARAM_SUBACTION, SUBACTION_SMP_REGISTER);
      aToolbar.addSubmitButton ("Register SMP at SML");

      aTabBox.addTab ("Register SMP", aForm);
      aNodeList.addChild (aTabBox);
    }
  }
}
