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
package com.helger.peppol.pub.page;

import java.util.Locale;

import javax.annotation.Nonnull;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.helger.bdve.EValidationType;
import com.helger.bdve.ValidationArtefactKey;
import com.helger.bdve.artefact.IValidationArtefact;
import com.helger.bdve.artefact.ValidationArtefact;
import com.helger.bdve.execute.IValidationExecutor;
import com.helger.bdve.execute.ValidationExecutionManager;
import com.helger.bdve.result.ValidationResult;
import com.helger.bdve.result.ValidationResultList;
import com.helger.bdve.source.ValidationSource;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.error.IError;
import com.helger.commons.error.SingleError;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.error.location.IErrorLocation;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.forms.HCEditFile;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.html.grouping.IHCLI;
import com.helger.html.hc.html.textlevel.HCCode;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.pub.validation.bis2.ExtValidationKeyRegistry;
import com.helger.peppol.pub.validation.bis2.ExtValidationKeySelect;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.peppol.validation.engine.peppol.PeppolValidationConfiguration;
import com.helger.photon.basic.audit.AuditHelper;
import com.helger.photon.bootstrap3.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap3.alert.BootstrapInfoBox;
import com.helger.photon.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.form.BootstrapForm;
import com.helger.photon.bootstrap3.form.BootstrapFormGroup;
import com.helger.photon.bootstrap3.label.BootstrapLabel;
import com.helger.photon.bootstrap3.label.EBootstrapLabelType;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.schematron.svrl.SVRLResourceError;
import com.helger.web.fileupload.FileItemResource;
import com.helger.web.fileupload.IFileItem;
import com.helger.xml.sax.AbstractSAXErrorHandler;

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
    final FormErrorList aFormErrors = new FormErrorList ();
    final boolean bShowInput = true;

    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      // Validate fields
      final String sValidationKey = aWPEC.getAttributeAsString (FIELD_VALIDATION_KEY);
      final ValidationArtefactKey aVK = ExtValidationKeyRegistry.getFromID (sValidationKey);
      final IFileItem aFileItem = aWPEC.getFileItem (FIELD_FILE);
      final String sFileName = aFileItem == null ? null : aFileItem.getNameSecure ();

      if (aVK == null)
        aFormErrors.addFieldError (FIELD_VALIDATION_KEY, "Please select a valid rule set.");
      if (StringHelper.hasNoText (sFileName))
        aFormErrors.addFieldError (FIELD_FILE, "Please select a file to be validated.");

      if (aFormErrors.isEmpty ())
      {
        // Start validation
        final ICommonsList <IValidationExecutor> aExecutors = PeppolValidationConfiguration.createDefault (aVK);
        final ValidationExecutionManager aValidator = new ValidationExecutionManager (aExecutors);

        // Perform the validation
        final FileItemResource aXMLRes = new FileItemResource (aFileItem);
        final ValidationResultList aValidationResultList = new ValidationResultList ();
        try
        {
          final ValidationSource aSource = ValidationSource.createXMLSource (aXMLRes);
          aValidator.executeValidation (aSource, aValidationResultList);
        }
        catch (final SAXParseException ex)
        {
          aValidationResultList.add (new ValidationResult (new ValidationArtefact (EValidationType.XML, aXMLRes, aVK),
                                                           new ErrorList (AbstractSAXErrorHandler.getSaxParseError (EErrorLevel.FATAL_ERROR,
                                                                                                                    ex))));
        }
        catch (final SAXException ex)
        {
          aValidationResultList.add (new ValidationResult (new ValidationArtefact (EValidationType.XML, aXMLRes, aVK),
                                                           new ErrorList (SingleError.builderError ()
                                                                                     .setLinkedException (ex)
                                                                                     .setErrorText ("Failed to parse file as XML")
                                                                                     .build ())));
        }

        // Show results per layer
        int nWarnings = 0;
        int nErrors = 0;
        final HCNodeList aDetails = new HCNodeList ();
        for (final ValidationResult aValidationResultItem : aValidationResultList)
        {
          final IValidationArtefact aValidationArtefact = aValidationResultItem.getValidationArtefact ();
          final IErrorList aItemErrors = aValidationResultItem.getErrorList ();

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
              for (final IError aError : aItemErrors)
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

                final IErrorLocation aLocation = aError.getErrorLocation ();
                final SVRLResourceError aSVRLError = aError instanceof SVRLResourceError ? (SVRLResourceError) aError
                                                                                         : null;
                final IHCLI <?> aItem = aUL.addItem ();
                aItem.addChild (new HCDiv ().addChild (aErrorLevel));
                aItem.addChild (new HCDiv ().addChild ("Location: ")
                                            .addChild (new HCCode ().addChild (aLocation.getAsString ())));
                if (aSVRLError != null)
                  aItem.addChild (new HCDiv ().addChild ("XPath test: ")
                                              .addChild (new HCCode ().addChild (aSVRLError.getTest ())));
                aItem.addChild (new HCDiv ().addChild ("Error message: " + aError.getErrorText (aDisplayLocale)));
                if (aError.hasLinkedException ())
                  aItem.addChild (new HCDiv ().addChild ("Technical details: ")
                                              .addChild (new HCCode ().addChild (aError.getLinkedExceptionMessage ())));
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
                                           aVK.getTransaction ().getID (),
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
      final BootstrapForm aForm = aNodeList.addAndReturnChild (getUIHandler ().createFormSelf (aWPEC));
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
