/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.helger.bdve.artefact.IValidationArtefact;
import com.helger.bdve.artefact.ValidationArtefact;
import com.helger.bdve.execute.ValidationExecutionManager;
import com.helger.bdve.executorset.IValidationExecutorSet;
import com.helger.bdve.executorset.VESID;
import com.helger.bdve.result.ValidationResult;
import com.helger.bdve.result.ValidationResultList;
import com.helger.bdve.source.ValidationSource;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.error.IError;
import com.helger.commons.error.SingleError;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.statistics.IMutableStatisticsHandlerTimer;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.StringHelper;
import com.helger.commons.timing.StopWatch;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.forms.HCEditFile;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.html.grouping.IHCLI;
import com.helger.html.hc.html.textlevel.HCCode;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.pub.validation.ExtValidationKeyRegistry;
import com.helger.peppol.pub.validation.ExtValidationKeySelect;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.photon.basic.audit.AuditHelper;
import com.helger.photon.bootstrap3.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap3.alert.BootstrapInfoBox;
import com.helger.photon.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.form.BootstrapCheckBox;
import com.helger.photon.bootstrap3.form.BootstrapForm;
import com.helger.photon.bootstrap3.form.BootstrapFormGroup;
import com.helger.photon.bootstrap3.label.BootstrapLabel;
import com.helger.photon.bootstrap3.label.EBootstrapLabelType;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.form.RequestFieldBoolean;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.schematron.svrl.SVRLResourceError;
import com.helger.web.fileupload.FileItemResource;
import com.helger.web.fileupload.IFileItem;
import com.helger.xml.sax.AbstractSAXErrorHandler;
import com.helger.xml.sax.WrappedCollectingSAXErrorHandler;
import com.helger.xml.serialize.read.DOMReader;
import com.helger.xml.serialize.read.DOMReaderSettings;

public class PagePublicToolsDocumentValidation extends AbstractAppWebPage
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PagePublicToolsDocumentValidation.class);
  private static final IMutableStatisticsHandlerTimer s_aTimer = StatisticsManager.getTimerHandler (PagePublicToolsDocumentValidation.class);
  private static final boolean DEFAULT_SHOW_WARNINGS = true;
  private static final String FIELD_VES = "ves";
  private static final String FIELD_FILE = "file";
  private static final String FIELD_SHOW_WARNINGS = "showwarnings";

  public PagePublicToolsDocumentValidation (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Document Validation (Upload)");
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
      final VESID aVESID = VESID.parseIDOrNull (aWPEC.params ().getAsString (FIELD_VES));
      final IValidationExecutorSet aVES = ExtValidationKeyRegistry.getFromIDOrNull (aVESID);
      final IFileItem aFileItem = aWPEC.params ().getAsFileItem (FIELD_FILE);
      final String sFileName = aFileItem == null ? null : aFileItem.getNameSecure ();
      final boolean bShowWarnings = aWPEC.params ().isCheckBoxChecked (FIELD_SHOW_WARNINGS, DEFAULT_SHOW_WARNINGS);

      if (aVES == null)
        aFormErrors.addFieldError (FIELD_VES, "Please select a valid rule set.");
      if (StringHelper.hasNoText (sFileName))
        aFormErrors.addFieldError (FIELD_FILE, "Please select a file to be validated.");

      if (aFormErrors.isEmpty ())
      {
        // Start validation
        s_aLogger.info ("Validating " + sFileName + " using " + aVESID.getAsSingleID ());
        final StopWatch aSW = StopWatch.createdStarted ();

        final ValidationExecutionManager aValidator = aVES.createExecutionManager ();

        // Perform the validation
        final FileItemResource aXMLRes = new FileItemResource (aFileItem);
        final ValidationResultList aValidationResultList = new ValidationResultList ();
        final ErrorList aXMLErrors = new ErrorList ();
        try
        {

          final Document aDoc = DOMReader.readXMLDOM (aXMLRes,
                                                      new DOMReaderSettings ().setErrorHandler (new WrappedCollectingSAXErrorHandler (aXMLErrors))
                                                                              .setLocale (aDisplayLocale));
          if (aDoc != null)
          {
            final ValidationSource aSource = ValidationSource.create (aXMLRes.getPath (), aDoc);
            aValidator.executeValidation (aSource, aValidationResultList, aDisplayLocale);
          }
        }
        catch (final SAXParseException ex)
        {
          // Already captured in ErrorList
          if (false)
            aValidationResultList.add (new ValidationResult (ValidationArtefact.createXML (aXMLRes,
                                                                                           aVES.getValidationArtefactKey ()),
                                                             new ErrorList (AbstractSAXErrorHandler.getSaxParseError (EErrorLevel.FATAL_ERROR,
                                                                                                                      ex))));
        }
        catch (final SAXException ex)
        {
          // Already captured in ErrorList
          if (false)
            aValidationResultList.add (new ValidationResult (ValidationArtefact.createXML (aXMLRes,
                                                                                           aVES.getValidationArtefactKey ()),
                                                             new ErrorList (SingleError.builderError ()
                                                                                       .setLinkedException (ex)
                                                                                       .setErrorText ("Failed to parse file as XML")
                                                                                       .build ())));
        }
        if (aXMLErrors.containsAtLeastOneFailure ())
        {
          // Add all XML parsing errors
          aValidationResultList.add (new ValidationResult (ValidationArtefact.createXML (aXMLRes,
                                                                                         aVES.getValidationArtefactKey ()),
                                                           aXMLErrors));
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
                if (aError.getErrorLevel ().isGE (EErrorLevel.ERROR))
                {
                  nErrors++;
                  aErrorLevel = new BootstrapLabel (EBootstrapLabelType.DANGER).addChild ("Error");
                }
                else
                  if (aError.getErrorLevel ().isGE (EErrorLevel.WARN))
                  {
                    if (!bShowWarnings)
                      continue;

                    nWarnings++;
                    aErrorLevel = new BootstrapLabel (EBootstrapLabelType.WARNING).addChild ("Warning");
                  }
                  else
                    aErrorLevel = new BootstrapLabel ().addChild ("undefined");

                final SVRLResourceError aSVRLError = aError instanceof SVRLResourceError ? (SVRLResourceError) aError
                                                                                         : null;
                final IHCLI <?> aItem = aUL.addItem ();
                aItem.addChild (new HCDiv ().addChild (aErrorLevel));

                final String sLocation = aError.getErrorLocation ().getAsString ();
                if (StringHelper.hasText (sLocation))
                  aItem.addChild (new HCDiv ().addChild ("Location: ").addChild (new HCCode ().addChild (sLocation)));

                final String sFieldName = aError.getErrorFieldName ();
                if (StringHelper.hasText (sFieldName))
                  aItem.addChild (new HCDiv ().addChild ("Element/context: ")
                                              .addChild (new HCCode ().addChild (sFieldName)));

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
          {
            aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Congratulations - the file '" +
                                                                     sFileName +
                                                                     "' is valid. No warnings and no errors."));
          }
          else
          {
            aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Congratulations - the file '" +
                                                                     sFileName +
                                                                     "' is valid but it contains " +
                                                                     nWarnings +
                                                                     (nWarnings == 1 ? " warning" : " warnings") +
                                                                     "."));
          }
        }
        else
        {
          if (nWarnings == 0)
          {
            aNodeList.addChild (new BootstrapErrorBox ().addChild ("The file '" +
                                                                   sFileName +
                                                                   "' is invalid. It contains " +
                                                                   nErrors +
                                                                   (nErrors == 1 ? " error" : " errors") +
                                                                   "."));
          }
          else
          {
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
        }
        aNodeList.addChild (aDetails);
        s_aLogger.info ("Finished validation after " +
                        aSW.stopAndGetMillis () +
                        "ms; " +
                        nWarnings +
                        " warns; " +
                        nErrors +
                        " errors");
        s_aTimer.addTime (aSW.getMillis ());

        // Audit execution
        AuditHelper.onAuditExecuteSuccess ("validation-bis2-upload",
                                           sFileName,
                                           aVES.getID (),
                                           aVES.getValidationArtefactKey (),
                                           Integer.valueOf (aValidationResultList.size ()),
                                           Long.valueOf (aSW.getMillis ()),
                                           Integer.valueOf (nErrors),
                                           Integer.valueOf (nWarnings));
      }
    }

    if (bShowInput)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (getUIHandler ().createFormSelf (aWPEC));
      aForm.setEncTypeFileUpload ();
      aForm.addChild (new BootstrapInfoBox ().addChild ("Select the rule set and the XML file for validation and upload it"));

      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Rule set")
                                                   .setCtrl (new ExtValidationKeySelect (new RequestField (FIELD_VES),
                                                                                         aDisplayLocale))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_VES)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("XML file")
                                                   .setCtrl (new HCEditFile (FIELD_FILE))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_FILE)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Show warnings?")
                                                   .setCtrl (new BootstrapCheckBox (new RequestFieldBoolean (FIELD_SHOW_WARNINGS,
                                                                                                             DEFAULT_SHOW_WARNINGS)).setInline (true))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_SHOW_WARNINGS)));

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
      aToolbar.addSubmitButton ("Show details");
    }
  }
}
