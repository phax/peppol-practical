/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.peppol.secure;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.annotation.Nonempty;
import com.helger.diver.api.coord.DVRCoordinate;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.forms.HCCheckBox;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.io.file.FilenameHelper;
import com.helger.io.resource.IReadableResource;
import com.helger.peppol.sharedui.page.AbstractAppWebPage;
import com.helger.peppol.validate.VESRegistry;
import com.helger.peppol.validate.ctrl.HCVESSelect;
import com.helger.phive.api.EValidationType;
import com.helger.phive.api.IValidationType;
import com.helger.phive.api.executor.IValidationExecutor;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.xml.source.IValidationSourceXML;
import com.helger.photon.bootstrap4.button.BootstrapSubmitButton;
import com.helger.photon.bootstrap4.buttongroup.BootstrapButtonToolbar;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.form.EBootstrapFormType;
import com.helger.photon.bootstrap4.nav.BootstrapTabBox;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.form.RequestFieldBoolean;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.html.select.HCExtSelect;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.prism.EPrismLanguage;
import com.helger.photon.uictrls.prism.HCPrismJS;
import com.helger.photon.uictrls.prism.PrismPluginLineNumbers;
import com.helger.schematron.CSchematron;
import com.helger.schematron.pure.binding.IPSQueryBinding;
import com.helger.schematron.pure.binding.PSQueryBindingRegistry;
import com.helger.schematron.pure.errorhandler.CollectingPSErrorHandler;
import com.helger.schematron.pure.exchange.PSReader;
import com.helger.schematron.pure.model.PSSchema;
import com.helger.schematron.pure.preprocess.PSPreprocessor;
import com.helger.schematron.pure.preprocess.SchematronPreprocessException;
import com.helger.schematron.sch.SchematronResourceSCH;
import com.helger.schematron.svrl.CSVRL;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.namespace.MapBasedNamespaceContext;
import com.helger.xml.serialize.write.EXMLSerializeIndent;
import com.helger.xml.serialize.write.IXMLWriterSettings;
import com.helger.xml.serialize.write.XMLWriter;
import com.helger.xml.serialize.write.XMLWriterSettings;

public final class PageSecureSchematronTools extends AbstractAppWebPage
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PageSecureSchematronTools.class);
  private static final String FIELD_VESID = "vesid";
  private static final String ACTION_SHOW_PREPROCESSED_SCHEMA = "showpps";
  private static final String ACTION_SHOW_XSLT = "showxslt";
  private static final String FIELD_STYLE_OUTPUT = "styleoutput";
  public static final boolean DEFAULT_STYLE_OUTPUT = false;

  public PageSecureSchematronTools (@NonNull @Nonempty final String sID)
  {
    super (sID, "Schematron tools (Beta)");
  }

  private static final class ActionSelect extends HCExtSelect
  {
    public ActionSelect (@NonNull final RequestField aRF)
    {
      super (aRF);
      addOption (ACTION_SHOW_PREPROCESSED_SCHEMA, "Show preprocessed schema");
      addOption (ACTION_SHOW_XSLT, "Show created XSLT");
    }
  }

  @Override
  protected void fillContent (@NonNull final WebPageExecutionContext aWPEC)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    {
      final BootstrapButtonToolbar aToolbar = new BootstrapButtonToolbar (aWPEC);
      final BootstrapForm aForm = aToolbar.addAndReturnChild (new BootstrapForm (aWPEC).setFormType (EBootstrapFormType.INLINE));
      aForm.addFormGroup (new BootstrapFormGroup ().setCtrl (new HCVESSelect (new RequestField (FIELD_VESID),
                                                                                         aDisplayLocale)));
      aForm.addFormGroup (new BootstrapFormGroup ().setCtrl (new ActionSelect (new RequestField (CPageParam.PARAM_ACTION))));
      aForm.addFormGroup (new BootstrapFormGroup ().setCtrl (new HCCheckBox (new RequestFieldBoolean (FIELD_STYLE_OUTPUT,
                                                                                                      DEFAULT_STYLE_OUTPUT)),
                                                             new HCTextNode (" format?")));
      aForm.addFormGroup (new BootstrapFormGroup ().setCtrl (new BootstrapSubmitButton ().addChild ("Run")));
      aNodeList.addChild (aToolbar);
    }

    final String sVESID = aWPEC.params ().getAsString (FIELD_VESID);
    final DVRCoordinate aVESID = DVRCoordinate.parseOrNull (sVESID);
    final boolean bStyleOutput = aWPEC.params ().isCheckBoxChecked (FIELD_STYLE_OUTPUT, DEFAULT_STYLE_OUTPUT);
    final IValidationExecutorSet <IValidationSourceXML> aVES = VESRegistry.getFromIDOrNull (aVESID);

    if (aVES != null)
    {
      if (aWPEC.hasAction (ACTION_SHOW_PREPROCESSED_SCHEMA))
      {
        LOGGER.info ("Showing preprocessed Schematron of " + aVESID.getAsSingleID ());

        final MapBasedNamespaceContext aNSCtx = new MapBasedNamespaceContext ();
        aNSCtx.addDefaultNamespaceURI (CSchematron.NAMESPACE_SCHEMATRON);
        aNSCtx.addMapping ("xsl", "http://www.w3.org/1999/XSL/Transform");
        aNSCtx.addMapping ("svrl", CSVRL.SVRL_NAMESPACE_URI);
        final IXMLWriterSettings XWS = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.INDENT_AND_ALIGN)
                                                               .setNamespaceContext (aNSCtx);

        aNodeList.addChild (info ("Showing preprocessed version of " + aVESID.getAsSingleID ()));
        final BootstrapTabBox aTabBox = new BootstrapTabBox ();
        for (final IValidationExecutor <?> aVE : aVES.getAllExecutors ())
        {
          final IReadableResource aRes = aVE.getValidationArtefact ().getRuleResource ();
          final IValidationType aType = aVE.getValidationArtefact ().getValidationType ();
          if (aType == EValidationType.SCHEMATRON_PURE || aType == EValidationType.SCHEMATRON_SCH)
          {
            IHCNode aTabContent;
            try
            {
              // Read Schematron
              final PSSchema aSchema = new PSReader (aRes, null, null).readSchema ();
              final IPSQueryBinding aQueryBinding = PSQueryBindingRegistry.getQueryBindingOfNameOrThrow (aSchema.getQueryBinding ());
              final PSPreprocessor aPreprocessor = PSPreprocessor.createPreprocessorWithoutInformationLoss (aQueryBinding);
              // Pre-process
              final PSSchema aPreprocessedSchema = aPreprocessor.getAsPreprocessedSchema (aSchema);
              if (aPreprocessedSchema == null)
                throw new SchematronPreprocessException ("Failed to preprocess schema " +
                                                         aSchema +
                                                         " with query binding " +
                                                         aQueryBinding);
              // Convert to XML string
              final String sXML = MicroWriter.getNodeAsString (aPreprocessedSchema.getAsMicroElement (), XWS);

              IHCNode aCode;
              if (bStyleOutput)
              {
                final HCPrismJS aPrism = new HCPrismJS (EPrismLanguage.MARKUP).addPlugin (new PrismPluginLineNumbers ())
                                                                              .addChild (sXML);
                aCode = aPrism;
              }
              else
                aCode = pre (sXML);

              final CollectingPSErrorHandler aErrHdl = new CollectingPSErrorHandler ();
              aPreprocessedSchema.validateCompletely (aErrHdl);
              if (aErrHdl.isEmpty ())
              {
                aTabContent = aCode;
              }
              else
              {
                final HCUL aUL = new HCUL ();
                aErrHdl.getAllErrors ().forEach (x -> aUL.addItem (x.getErrorText (aDisplayLocale)));
                aTabContent = new HCNodeList ().addChild (error ("Errors in the Schematron:").addChild (aUL))
                                               .addChild (aCode);
              }
            }
            catch (final Exception ex)
            {
              aTabContent = error ("Error parsing Schematron: " + ex.getMessage ());
            }
            aTabBox.addTab ("t" + aTabBox.getTabCount (), FilenameHelper.getBaseName (aRes.getPath ()), aTabContent);
          }
          else
            if (aType == EValidationType.SCHEMATRON_XSLT)
            {
              final IHCNode aTabContent = info ("This is already XSLT");
              aTabBox.addTab ("t" + aTabBox.getTabCount (), FilenameHelper.getBaseName (aRes.getPath ()), aTabContent);
            }
        }
        if (aTabBox.hasNoTabs ())
          aNodeList.addChild (warn ("No Schematron artefacts found"));
        else
          aNodeList.addChild (aTabBox);
      }
      else
        if (aWPEC.hasAction (ACTION_SHOW_XSLT))
        {
          LOGGER.info ("Showing XSLT version of " + aVESID.getAsSingleID ());

          final MapBasedNamespaceContext aNSCtx = new MapBasedNamespaceContext ();
          aNSCtx.addDefaultNamespaceURI (CSchematron.NAMESPACE_SCHEMATRON);
          aNSCtx.addMapping ("xsl", "http://www.w3.org/1999/XSL/Transform");
          aNSCtx.addMapping ("svrl", CSVRL.SVRL_NAMESPACE_URI);
          final IXMLWriterSettings XWS = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.INDENT_AND_ALIGN)
                                                                 .setNamespaceContext (aNSCtx);

          aNodeList.addChild (info ("Showing XSLT version of " + aVESID.getAsSingleID ()));
          final BootstrapTabBox aTabBox = new BootstrapTabBox ();
          for (final IValidationExecutor <IValidationSourceXML> aVE : aVES.getAllExecutors ())
          {
            final IReadableResource aRes = aVE.getValidationArtefact ().getRuleResource ();
            final IValidationType aType = aVE.getValidationArtefact ().getValidationType ();
            if (aType == EValidationType.SCHEMATRON_PURE || aType == EValidationType.SCHEMATRON_SCH)
            {
              IHCNode aTabContent;
              try
              {
                // Read Schematron
                final SchematronResourceSCH aSch = new SchematronResourceSCH (aRes);
                if (!aSch.isValidSchematron ())
                  throw new SchematronPreprocessException ("Invalid Schematron!");
                final Document aXSLT = aSch.getXSLTProvider ().getXSLTDocument ();
                if (aXSLT == null)
                  throw new SchematronPreprocessException ("Failed to convert to XSLT!");
                // Convert to XML string
                final String sXML = XMLWriter.getNodeAsString (aXSLT, XWS);
                // Highlight
                if (bStyleOutput)
                {
                  final HCPrismJS aPrism = new HCPrismJS (EPrismLanguage.MARKUP).addPlugin (new PrismPluginLineNumbers ())
                                                                                .addChild (sXML);

                  aTabContent = aPrism;
                }
                else
                  aTabContent = pre (sXML);
              }
              catch (final Exception ex)
              {
                aTabContent = error ("Error parsing Schematron: " + ex.getMessage ());
              }
              aTabBox.addTab ("t" + aTabBox.getTabCount (), FilenameHelper.getBaseName (aRes.getPath ()), aTabContent);
            }
            else
              if (aType == EValidationType.SCHEMATRON_XSLT)
              {
                final IHCNode aTabContent = info ("This is already XSLT");
                aTabBox.addTab ("t" + aTabBox.getTabCount (),
                                FilenameHelper.getBaseName (aRes.getPath ()),
                                aTabContent);
              }
          }
          if (aTabBox.hasNoTabs ())
            aNodeList.addChild (warn ("No Schematron artefacts found"));
          else
            aNodeList.addChild (aTabBox);
        }
        else
        {
          // TODO other action when necessary
        }
      LOGGER.info ("Done");
    }
  }
}
