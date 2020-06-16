/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.bdve.api.EValidationType;
import com.helger.bdve.api.artefact.IValidationArtefact;
import com.helger.bdve.api.artefact.ValidationArtefact;
import com.helger.bdve.api.execute.ValidationExecutionManager;
import com.helger.bdve.api.executorset.IValidationExecutorSet;
import com.helger.bdve.api.executorset.VESID;
import com.helger.bdve.api.result.ValidationResult;
import com.helger.bdve.api.result.ValidationResultList;
import com.helger.bdve.engine.source.IValidationSourceXML;
import com.helger.bdve.engine.source.ValidationSourceXML;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.error.IError;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.statistics.IMutableStatisticsHandlerCounter;
import com.helger.commons.statistics.IMutableStatisticsHandlerKeyedCounter;
import com.helger.commons.statistics.IMutableStatisticsHandlerTimer;
import com.helger.commons.statistics.StatisticsManager;
import com.helger.commons.string.StringHelper;
import com.helger.commons.timing.StopWatch;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.forms.HCCheckBox;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.html.grouping.IHCLI;
import com.helger.html.hc.html.sections.HCH2;
import com.helger.html.hc.html.tabular.HCRow;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.bdve.ExtValidationKeyRegistry;
import com.helger.peppol.bdve.ExtValidationKeySelect;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.photon.audit.AuditHelper;
import com.helger.photon.bootstrap4.CBootstrapCSS;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.table.BootstrapTable;
import com.helger.photon.bootstrap4.uictrls.ext.BootstrapFileUpload;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.form.RequestFieldBoolean;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.schematron.svrl.SVRLResourceError;
import com.helger.web.fileupload.FileItemResource;
import com.helger.web.fileupload.IFileItem;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.sax.WrappedCollectingSAXErrorHandler;
import com.helger.xml.serialize.read.DOMReader;
import com.helger.xml.serialize.read.DOMReaderSettings;

public class PagePublicToolsDocumentValidation extends AbstractAppWebPage
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PagePublicToolsDocumentValidation.class);
  private static final IMutableStatisticsHandlerCounter s_aStatsCounter = StatisticsManager.getCounterHandler (PagePublicToolsDocumentValidation.class.getName () +
                                                                                                               "$count");
  private static final IMutableStatisticsHandlerKeyedCounter s_aStatsCounterVESID = StatisticsManager.getKeyedCounterHandler (PagePublicToolsDocumentValidation.class.getName () +
                                                                                                                              "vesid");
  private static final IMutableStatisticsHandlerTimer s_aStatsTimer = StatisticsManager.getTimerHandler (PagePublicToolsDocumentValidation.class);
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

    aNodeList.addChild (warn (div ("This page is moving.")).addChild (div ("The new home of the validation per upload is at my friends of ecosio: ").addChild (HCA.createLinkedWebsite ("https://ecosio.com/en/peppol-and-xml-document-validator/")))
                                                           .addChild (div ("This validation page will be shut down end of June 2020."))
                                                           .addChild (div (strong ("Note: ")).addChild ("the validation web service is NOT affected by this change and new validation rules WILL be added as usual."))
                                                           .addChild (div ("The endpoint address of the validation web service will also move to ecosio - expect this around end of September.")));

    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      // Validate fields
      final VESID aVESID = VESID.parseIDOrNull (aWPEC.params ().getAsString (FIELD_VES));
      final IValidationExecutorSet <IValidationSourceXML> aVES = ExtValidationKeyRegistry.getFromIDOrNull (aVESID);
      final IFileItem aFileItem = aWPEC.params ().getAsFileItem (FIELD_FILE);
      final String sFileName = aFileItem == null ? null : aFileItem.getNameSecure ();
      final boolean bShowWarnings = aWPEC.params ().isCheckBoxChecked (FIELD_SHOW_WARNINGS, DEFAULT_SHOW_WARNINGS);
      final boolean bShowInfos = true;

      if (aVES == null)
        aFormErrors.addFieldError (FIELD_VES, "Please select a valid rule set.");
      if (StringHelper.hasNoText (sFileName))
        aFormErrors.addFieldError (FIELD_FILE, "Please select a file to be validated.");

      if (aFormErrors.isEmpty ())
      {
        s_aStatsCounter.increment ();
        s_aStatsCounterVESID.increment (aVESID.getAsSingleID ());
        final StopWatch aSW = StopWatch.createdStarted ();

        LOGGER.info ("Validating " + sFileName + " using " + aVESID.getAsSingleID ());

        // Perform the validation
        final ValidationResultList aValidationResultList = new ValidationResultList ();
        {
          final FileItemResource aXMLRes = new FileItemResource (aFileItem);
          final ErrorList aXMLErrors = new ErrorList ();
          final Document aDoc = DOMReader.readXMLDOM (aXMLRes,
                                                      new DOMReaderSettings ().setErrorHandler (new WrappedCollectingSAXErrorHandler (aXMLErrors))
                                                                              .setLocale (aDisplayLocale)
                                                                              .setFeatureValues (EXMLParserFeature.AVOID_XML_ATTACKS));
          if (aDoc != null)
          {
            // First options reads XML again but provides line numbers
            // Second option uses prebuild Node but has no line numbers
            final ValidationSourceXML aSource = true ? ValidationSourceXML.create (aXMLRes)
                                                     : ValidationSourceXML.create (aXMLRes.getPath (), aDoc);
            // Start validation
            ValidationExecutionManager.executeValidation (aVES, aSource, aValidationResultList, aDisplayLocale);
          }

          // Add all XML parsing stuff - always first item
          // Also add if no error is present to have it shown in the list
          aValidationResultList.add (0, new ValidationResult (new ValidationArtefact (EValidationType.XML, aXMLRes), aXMLErrors));
        }

        // Show summary
        final HCNodeList aSummary = new HCNodeList ();

        {
          aSummary.addChild (new HCH2 ().addChild ("Summary"));

          final BootstrapTable aTable = new BootstrapTable ();
          final HCRow aHeaderRow = aTable.addHeaderRow ();
          aHeaderRow.addCell ("Validation type");
          aHeaderRow.addCell ("Validation artefact");
          aHeaderRow.addAndReturnCell ("Warnings").addClass (CSS_CLASS_RIGHT);
          aHeaderRow.addAndReturnCell ("Errors").addClass (CSS_CLASS_RIGHT);

          for (final ValidationResult aValidationResultItem : aValidationResultList)
            if (!aValidationResultItem.isIgnored ())
            {
              final HCRow aRow = aTable.addBodyRow ();
              final IValidationArtefact aValidationArtefact = aValidationResultItem.getValidationArtefact ();
              final IErrorList aItemErrors = aValidationResultItem.getErrorList ();
              final int nErrors = aItemErrors.getErrorCount ();
              final int nWarnings = aItemErrors.getCount (x -> x.getErrorLevel ().isEQ (EErrorLevel.WARN));
              if (nErrors > 0)
                aRow.addClass (CBootstrapCSS.BG_DANGER);
              else
                if (nWarnings > 0)
                  aRow.addClass (CBootstrapCSS.BG_WARNING);
                else
                  aRow.addClass (CBootstrapCSS.BG_SUCCESS);

              // Validation type
              aRow.addCell (aValidationArtefact.getValidationArtefactType ().getName ());

              // Validation artefact
              aRow.addCell (aValidationArtefact.getRuleResource ().getPath ());

              // Warnings on this level
              aRow.addAndReturnCell (Integer.toString (nWarnings)).addClass (CSS_CLASS_RIGHT);

              // Warnings on this error
              aRow.addAndReturnCell (Integer.toString (nErrors)).addClass (CSS_CLASS_RIGHT);
            }
          aSummary.addChild (aTable);
        }

        final HCNodeList aDetails = new HCNodeList ();
        aDetails.addChild (new HCH2 ().addChild ("Details"));

        // Show results per layer
        int nInfos = 0;
        int nWarnings = 0;
        int nErrors = 0;
        for (final ValidationResult aValidationResultItem : aValidationResultList)
        {
          final IValidationArtefact aValidationArtefact = aValidationResultItem.getValidationArtefact ();
          final IErrorList aItemErrors = aValidationResultItem.getErrorList ();

          // Header for level
          aDetails.addChild (div (strong (aValidationArtefact.getValidationArtefactType ().getName () +
                                          " - " +
                                          aValidationArtefact.getRuleResource ().getPath ())));
          final HCUL aUL = new HCUL ();
          if (aValidationResultItem.isIgnored ())
          {
            // Ignored layer?
            aUL.addItem (badgeInfo ("This layer was not executed because the prerequisite is not fulfilled"));
          }
          else
            if (aItemErrors.isEmpty ())
            {
              // No warnings, no errors
              aUL.addItem (badgeSuccess ("All fine on this level"));
            }
            else
            {
              int nItemsAdded = 0;
              for (final IError aError : aItemErrors)
              {
                IHCNode aErrorLevel;
                if (aError.getErrorLevel ().isGE (EErrorLevel.ERROR))
                {
                  nErrors++;
                  aErrorLevel = badgeDanger ("Error");
                }
                else
                  if (aError.getErrorLevel ().isGE (EErrorLevel.WARN))
                  {
                    if (!bShowWarnings)
                      continue;

                    nWarnings++;
                    aErrorLevel = badgeWarn ("Warning");
                  }
                  else
                    if (aError.getErrorLevel ().isGE (EErrorLevel.INFO))
                    {
                      if (!bShowInfos)
                        continue;

                      nInfos++;
                      aErrorLevel = badgeInfo ("Information");
                    }
                    else
                      aErrorLevel = badgeWarn ("undefined");

                final SVRLResourceError aSVRLError = aError instanceof SVRLResourceError ? (SVRLResourceError) aError : null;
                nItemsAdded++;
                final IHCLI <?> aItem = aUL.addItem ();
                aItem.addChild (div (aErrorLevel));

                final String sLocation = aError.getErrorLocation ().getAsString ();
                if (StringHelper.hasText (sLocation))
                  aItem.addChild (div ("Location: ").addChild (code (sLocation)));

                final String sFieldName = aError.getErrorFieldName ();
                if (StringHelper.hasText (sFieldName))
                  aItem.addChild (div ("Element/context: ").addChild (code (sFieldName)));

                if (aSVRLError != null)
                  aItem.addChild (div ("XPath test: ").addChild (code (aSVRLError.getTest ())));
                aItem.addChild (div ("Error message: " + aError.getErrorText (aDisplayLocale)));
                if (aError.hasLinkedException ())
                  aItem.addChild (div ("Technical details: ").addChild (code (aError.getLinkedExceptionMessage ())));
              }

              if (nItemsAdded == 0)
              {
                // Only warnings but warnings are disabled
                aUL.addItem (badgeSuccess ("All fine on this level - only suppressed warnings are contained"));
              }
            }
          aDetails.addChild (aUL);
        }

        // Build overall result
        if (nErrors == 0)
        {
          if (nWarnings == 0)
          {
            aNodeList.addChild (success ("Congratulations - the file '" + sFileName + "' is valid. No warnings and no errors."));
          }
          else
          {
            aNodeList.addChild (success ("Congratulations - the file '" +
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
            aNodeList.addChild (error ("The file '" +
                                       sFileName +
                                       "' is invalid. It contains " +
                                       nErrors +
                                       (nErrors == 1 ? " error" : " errors") +
                                       "."));
          }
          else
          {
            aNodeList.addChild (error ("The file '" +
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

        aNodeList.addChild (aSummary);
        aNodeList.addChild (aDetails);
        LOGGER.info ("Finished validation after " +
                     aSW.stopAndGetMillis () +
                     "ms; " +
                     nInfos +
                     " information; " +
                     nWarnings +
                     " warns; " +
                     nErrors +
                     " errors");
        s_aStatsTimer.addTime (aSW.getMillis ());

        // Audit execution
        AuditHelper.onAuditExecuteSuccess ("validation-bis2-upload",
                                           sFileName,
                                           aVES.getID (),
                                           aVES.getDisplayName (),
                                           Integer.valueOf (aValidationResultList.size ()),
                                           Long.valueOf (aSW.getMillis ()),
                                           Integer.valueOf (nInfos),
                                           Integer.valueOf (nWarnings),
                                           Integer.valueOf (nErrors));
      }
    }

    if (bShowInput)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (getUIHandler ().createFormSelf (aWPEC));
      aForm.setEncTypeFileUpload ();
      aForm.addChild (info ("Select the rule set and the XML file for validation and upload it"));

      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Rule set")
                                                   .setCtrl (new ExtValidationKeySelect (new RequestField (FIELD_VES), aDisplayLocale))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_VES)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("XML file")
                                                   .setCtrl (new BootstrapFileUpload (FIELD_FILE, aDisplayLocale))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_FILE)));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Show warnings?")
                                                   .setCtrl (new HCCheckBox (new RequestFieldBoolean (FIELD_SHOW_WARNINGS,
                                                                                                      DEFAULT_SHOW_WARNINGS)))
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_SHOW_WARNINGS)));

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
      aToolbar.addSubmitButton ("Show details");
    }
  }
}
