/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.peppol.pub.page;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.error.EErrorLevel;
import com.helger.commons.error.IResourceError;
import com.helger.commons.error.IResourceErrorGroup;
import com.helger.commons.error.IResourceLocation;
import com.helger.commons.errorlist.FormErrors;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.forms.HCEditFile;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.html.grouping.IHCLI;
import com.helger.html.hc.html.textlevel.HCCode;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.pub.validation.bis2.ExtValidationKey;
import com.helger.peppol.pub.validation.bis2.ExtValidationKeyRegistry;
import com.helger.peppol.pub.validation.bis2.ExtValidationKeySelect;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.peppol.validation.api.ValidationKey;
import com.helger.peppol.validation.api.artefact.IValidationArtefact;
import com.helger.peppol.validation.api.result.ValidationLayerResult;
import com.helger.peppol.validation.api.result.ValidationLayerResultList;
import com.helger.peppol.validation.engine.UBLDocumentValidator;
import com.helger.peppol.validation.engine.peppol.PeppolValidationConfiguration;
import com.helger.photon.basic.audit.AuditHelper;
import com.helger.photon.bootstrap3.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap3.alert.BootstrapInfoBox;
import com.helger.photon.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.form.BootstrapForm;
import com.helger.photon.bootstrap3.form.BootstrapFormGroup;
import com.helger.photon.bootstrap3.form.EBootstrapFormType;
import com.helger.photon.bootstrap3.label.BootstrapLabel;
import com.helger.photon.bootstrap3.label.EBootstrapLabelType;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.schematron.svrl.SVRLResourceError;
import com.helger.web.fileupload.FileItemResource;
import com.helger.web.fileupload.IFileItem;

public class PagePublicToolsValidateBIS2 extends AbstractAppWebPage
{
  private static final String FIELD_VALIDATION_KEY = "validationkey";
  private static final String FIELD_FILE = "file";

  public PagePublicToolsValidateBIS2 (@Nonnull @Nonempty final String sID)
  {
    super (sID, "BIS2 Document Validation");
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final FormErrors aFormErrors = new FormErrors ();
    final boolean bShowInput = true;

    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      // Validate fields
      final String sValidationKey = aWPEC.getAttributeAsString (FIELD_VALIDATION_KEY);
      final ExtValidationKey aExtValidationKey = ExtValidationKeyRegistry.getFromID (sValidationKey);
      final IFileItem aFileItem = aWPEC.getFileItem (FIELD_FILE);
      final String sFileName = aFileItem == null ? null : aFileItem.getNameSecure ();

      if (aExtValidationKey == null)
        aFormErrors.addFieldError (FIELD_VALIDATION_KEY, "Please select a valid rule set.");
      if (StringHelper.hasNoText (sFileName))
        aFormErrors.addFieldError (FIELD_FILE, "Please select a file to be validated.");

      if (aFormErrors.isEmpty ())
      {
        // Start validation
        final ValidationKey aVK = aExtValidationKey.getValidationKey ();
        final UBLDocumentValidator aValidator = new UBLDocumentValidator (PeppolValidationConfiguration.createDefault (aVK));

        // Perform the validation
        final ValidationLayerResultList aValidationResultList = aValidator.applyCompleteValidation (new FileItemResource (aFileItem));

        // Show results per layer
        int nWarnings = 0;
        int nErrors = 0;
        final HCNodeList aDetails = new HCNodeList ();
        for (final ValidationLayerResult aValidationResultItem : aValidationResultList)
        {
          final IValidationArtefact aValidationArtefact = aValidationResultItem.getValidationArtefact ();
          final IResourceErrorGroup aItemErrors = aValidationResultItem.getResourceErrorGroup ();

          final HCDiv aDiv = new HCDiv ();
          aDiv.addChild (aValidationArtefact.getValidationArtefactType ().getName ());
          aDiv.addChild (" - " + aValidationArtefact.getRuleResource ().getPath ());
          aDetails.addChild (aDiv);
          final HCUL aUL = new HCUL ();
          if (aValidationResultItem.isIgnored ())
          {
            aUL.addItem (new BootstrapLabel (EBootstrapLabelType.INFO).addChild ("This layer was not executed because the prerequisite is not fulfilled"));
          }
          else
            if (aItemErrors.isEmpty ())
            {
              aUL.addItem (new BootstrapLabel (EBootstrapLabelType.SUCCESS).addChild ("All fine on this level"));
            }
            else
              for (final IResourceError aError : aItemErrors)
              {
                IHCNode aErrorLevel;
                if (aError.getErrorLevel ().isMoreOrEqualSevereThan (EErrorLevel.ERROR))
                {
                  nErrors++;
                  aErrorLevel = new BootstrapLabel (EBootstrapLabelType.DANGER).addChild ("Error");
                }
                else
                  if (aError.getErrorLevel ().isMoreOrEqualSevereThan (EErrorLevel.WARN))
                  {
                    nWarnings++;
                    aErrorLevel = new BootstrapLabel (EBootstrapLabelType.WARNING).addChild ("Warning");
                  }
                  else
                    aErrorLevel = new BootstrapLabel ().addChild ("undefined");

                final IResourceLocation aLocation = aError.getLocation ();
                final SVRLResourceError aSVRLError = aError instanceof SVRLResourceError ? (SVRLResourceError) aError
                                                                                         : null;
                final IHCLI <?> aItem = aUL.addItem ();
                aItem.addChild (new HCDiv ().addChild (aErrorLevel));
                aItem.addChild (new HCDiv ().addChild ("Field: ")
                                            .addChild (new HCCode ().addChild (aLocation.getAsString ())));
                if (aSVRLError != null)
                  aItem.addChild (new HCDiv ().addChild ("XPath test: ")
                                              .addChild (new HCCode ().addChild (aSVRLError.getTest ())));
                aItem.addChild (new HCDiv ().addChild ("Error message: " + aError.getDisplayText (aDisplayLocale)));
              }
          aDetails.addChild (aUL);
        }

        // Build overall result
        if (nErrors == 0)
        {
          if (nWarnings == 0)
            aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Congratulations - the file '" +
                                                                     sFileName +
                                                                     "' is valid. No warnings and no errors."));
          else
            aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Congratulations - the file '" +
                                                                     sFileName +
                                                                     "' is valid but it contains " +
                                                                     nWarnings +
                                                                     (nWarnings == 1 ? " warning" : " warnings") +
                                                                     "."));
        }
        else
        {
          if (nWarnings == 0)
            aNodeList.addChild (new BootstrapErrorBox ().addChild ("The file '" +
                                                                   sFileName +
                                                                   "' is invalid. It contains " +
                                                                   nErrors +
                                                                   (nErrors == 1 ? " error" : " errors") +
                                                                   "."));
          else
            aNodeList.addChild (new BootstrapErrorBox ().addChild ("The file '" +
                                                                   sFileName +
                                                                   "' is invalid. It contains " +
                                                                   nErrors +
                                                                   (nErrors == 1 ? " error" : " errors") +
                                                                   " and " +
                                                                   nWarnings +
                                                                   (nWarnings == 1 ? " warning" : " warnings") +
                                                                   "."));
        }
        aNodeList.addChild (aDetails);

        // Audit execution
        AuditHelper.onAuditExecuteSuccess ("validation-bis2-upload",
                                           sFileName,
                                           aVK.getBusinessSpecification ().getID (),
                                           aVK.getTransaction ().getTransactionKey (),
                                           aVK.getCountryCode (),
                                           aVK.getSectorKey () == null ? null : aVK.getSectorKey ().getID (),
                                           aVK.getPrerequisiteXPath (),
                                           Integer.valueOf (aValidationResultList.getSize ()),
                                           Integer.valueOf (nErrors),
                                           Integer.valueOf (nWarnings));
      }
    }

    if (bShowInput)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (new BootstrapForm (EBootstrapFormType.HORIZONTAL).setAction (aWPEC.getSelfHref ()));
      aForm.setEncTypeFileUpload ();
      aForm.addChild (new BootstrapInfoBox ().addChild ("Select the PEPPOL UBL file for validation and upload it"));

      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Rule set")
                                                   .setCtrl (new ExtValidationKeySelect (new RequestField (FIELD_VALIDATION_KEY),
                                                                                         aDisplayLocale))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_VALIDATION_KEY)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("UBL file")
                                                   .setCtrl (new HCEditFile (FIELD_FILE))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_FILE)));

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
      aToolbar.addSubmitButton ("Show details");
    }
  }
}
