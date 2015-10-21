/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.html.forms.HCEditFile;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.html.grouping.IHCLI;
import com.helger.html.hc.html.textlevel.HCCode;
import com.helger.html.hc.html.textlevel.HCEM;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.page.AbstractAppWebPage;
import com.helger.peppol.validation.UBLDocumentValidator;
import com.helger.peppol.validation.ValidationConfiguration;
import com.helger.peppol.validation.ValidationLayerResult;
import com.helger.peppol.validation.ValidationLayerResultList;
import com.helger.peppol.validation.artefact.IValidationArtefact;
import com.helger.peppol.validation.domain.peppol.PeppolValidationKeys;
import com.helger.peppol.validation.peppol.PeppolValidationConfiguration;
import com.helger.photon.bootstrap3.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap3.alert.BootstrapInfoBox;
import com.helger.photon.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.form.BootstrapForm;
import com.helger.photon.bootstrap3.form.BootstrapFormGroup;
import com.helger.photon.bootstrap3.form.EBootstrapFormType;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.schematron.svrl.SVRLResourceError;
import com.helger.validation.error.FormErrors;
import com.helger.web.fileupload.FileItemResource;
import com.helger.web.fileupload.IFileItem;

public class PagePublicToolsValidateBIS2 extends AbstractAppWebPage
{
  private static final String FIELD_FILE = "file";

  public PagePublicToolsValidateBIS2 (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Validate BIS2 documents");
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
      final IFileItem aFileItem = aWPEC.getFileItem (FIELD_FILE);
      final String sFileName = aFileItem == null ? null : aFileItem.getNameSecure ();
      if (StringHelper.hasNoText (sFileName))
        aFormErrors.addFieldError (FIELD_FILE, "Please select a file to be validated.");
      else
      {
        // Start validation
        final ValidationConfiguration aConfig = PeppolValidationConfiguration.createDefault (PeppolValidationKeys.INVOICE_04_T10);
        final UBLDocumentValidator aValidator = new UBLDocumentValidator (aConfig);
        final ValidationLayerResultList aValidationResult = aValidator.applyCompleteValidation (new FileItemResource (aFileItem));
        int nWarnings = 0;
        int nErrors = 0;
        final HCNodeList aDetails = new HCNodeList ();
        for (final ValidationLayerResult aValidationResultItem : aValidationResult)
        {
          final IValidationArtefact aValidationArtefact = aValidationResultItem.getValidationArtefact ();
          final IResourceErrorGroup aResourceErrors = aValidationResultItem.getResourceErrorGroup ();
          final HCDiv aDiv = new HCDiv ();
          aDiv.addChild (aValidationArtefact.getValidationArtefactType ().getName ());
          aDiv.addChild (" - " + aValidationArtefact.getRuleResource ().getPath ());
          aDetails.addChild (aDiv);
          final HCUL aUL = new HCUL ();
          if (aResourceErrors.isEmpty ())
            aUL.addItem (new HCEM ().addChild ("All fine on this level"));
          else
            for (final IResourceError aError : aResourceErrors)
            {
              String sErrorLevel = "undefined";
              if (aError.getErrorLevel ().isMoreOrEqualSevereThan (EErrorLevel.ERROR))
              {
                nErrors++;
                sErrorLevel = "Error";
              }
              else
                if (aError.getErrorLevel ().isMoreOrEqualSevereThan (EErrorLevel.WARN))
                {
                  nWarnings++;
                  sErrorLevel = "Warning";
                }
              final IResourceLocation aLocation = aError.getLocation ();
              final SVRLResourceError aSVRLError = aError instanceof SVRLResourceError ? (SVRLResourceError) aError
                                                                                       : null;
              final IHCLI <?> aItem = aUL.addItem ();
              aItem.addChild (new HCDiv ().addChild (sErrorLevel));
              aItem.addChild (new HCDiv ().addChild ("Field: ")
                                          .addChild (new HCCode ().addChild (aLocation.getAsString ())));
              if (aSVRLError != null)
                aItem.addChild (new HCDiv ().addChild ("XPath test: ")
                                            .addChild (new HCCode ().addChild (aSVRLError.getTest ())));
              aItem.addChild (new HCDiv ().addChild ("Error message: " + aError.getDisplayText (aDisplayLocale)));
            }
          aDetails.addChild (aUL);
        }

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
                                                                     " warnings."));
        }
        else
        {
          if (nWarnings == 0)
            aNodeList.addChild (new BootstrapErrorBox ().addChild ("The file '" +
                                                                   sFileName +
                                                                   "' is invalid. It contains " +
                                                                   nErrors +
                                                                   " errors."));
          else
            aNodeList.addChild (new BootstrapErrorBox ().addChild ("The file '" +
                                                                   sFileName +
                                                                   "' is invalid. It contains " +
                                                                   nErrors +
                                                                   " errors and " +
                                                                   nWarnings +
                                                                   " warnings."));
        }
        aNodeList.addChild (aDetails);
      }
    }

    if (bShowInput)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (new BootstrapForm (EBootstrapFormType.HORIZONTAL).setAction (aWPEC.getSelfHref ()));
      aForm.setEncTypeFileUpload ();
      aForm.addChild (new BootstrapInfoBox ().addChild ("Select the PEPPOL UBL file for validation and upload it"));

      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("UBL file")
                                                   .setCtrl (new HCEditFile (FIELD_FILE))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_FILE)));

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
      aToolbar.addSubmitButton ("Show details");
    }
  }
}
