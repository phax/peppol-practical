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
package com.helger.peppol.secure.page;

import java.util.Locale;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.bdve.artefact.IValidationArtefact;
import com.helger.bdve.execute.IValidationExecutor;
import com.helger.bdve.executorset.IValidationExecutorSet;
import com.helger.bdve.executorset.VESID;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.grouping.HCPre;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.html.hc.impl.HCTextNode;
import com.helger.peppol.bdve.ExtValidationKeyRegistry;
import com.helger.peppol.bdve.ExtValidationKeySelect;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.photon.bootstrap3.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap3.alert.BootstrapInfoBox;
import com.helger.photon.bootstrap3.alert.BootstrapWarnBox;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.button.BootstrapSubmitButton;
import com.helger.photon.bootstrap3.form.BootstrapCheckBox;
import com.helger.photon.bootstrap3.form.BootstrapForm;
import com.helger.photon.bootstrap3.form.BootstrapFormGroup;
import com.helger.photon.bootstrap3.form.EBootstrapFormType;
import com.helger.photon.bootstrap3.nav.BootstrapTabBox;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.form.RequestFieldBoolean;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.html.select.HCExtSelect;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.prism.EPrismLanguage;
import com.helger.photon.uictrls.prism.EPrismPlugin;
import com.helger.photon.uictrls.prism.HCPrismJS;
import com.helger.schematron.CSchematron;
import com.helger.schematron.pure.binding.IPSQueryBinding;
import com.helger.schematron.pure.binding.PSQueryBindingRegistry;
import com.helger.schematron.pure.errorhandler.CollectingPSErrorHandler;
import com.helger.schematron.pure.exchange.PSReader;
import com.helger.schematron.pure.model.PSSchema;
import com.helger.schematron.pure.preprocess.PSPreprocessor;
import com.helger.schematron.pure.preprocess.SchematronPreprocessException;
import com.helger.schematron.svrl.CSVRL;
import com.helger.schematron.xslt.SchematronResourceSCH;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.namespace.MapBasedNamespaceContext;
import com.helger.xml.serialize.write.EXMLSerializeIndent;
import com.helger.xml.serialize.write.IXMLWriterSettings;
import com.helger.xml.serialize.write.XMLWriter;
import com.helger.xml.serialize.write.XMLWriterSettings;

public final class PageSecureSchematronTools extends AbstractAppWebPage
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PageSecureSchematronTools.class);
  private static final String FIELD_VESID = "vesid";
  private static final String ACTION_SHOW_PREPROCESSED_SCHEMA = "showpps";
  private static final String ACTION_SHOW_XSLT = "showxslt";
  private static final String FIELD_STYLE_OUTPUT = "styleoutput";
  public static final boolean DEFAULT_STYLE_OUTPUT = false;

  public PageSecureSchematronTools (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Schematron tools (Beta)");
  }

  private static final class ActionSelect extends HCExtSelect
  {
    public ActionSelect (@Nonnull final RequestField aRF)
    {
      super (aRF);
      addOption (ACTION_SHOW_PREPROCESSED_SCHEMA, "Show preprocessed schema");
      addOption (ACTION_SHOW_XSLT, "Show created XSLT");
    }
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    {
      final BootstrapButtonToolbar aToolbar = new BootstrapButtonToolbar (aWPEC);
      final BootstrapForm aForm = aToolbar.addAndReturnChild (new BootstrapForm (aWPEC).setFormType (EBootstrapFormType.INLINE));
      aForm.addFormGroup (new BootstrapFormGroup ().setCtrl (new ExtValidationKeySelect (new RequestField (FIELD_VESID),
                                                                                         aDisplayLocale)));
      aForm.addFormGroup (new BootstrapFormGroup ().setCtrl (new ActionSelect (new RequestField (CPageParam.PARAM_ACTION))));
      aForm.addFormGroup (new BootstrapFormGroup ().setCtrl (new BootstrapCheckBox (new RequestFieldBoolean (FIELD_STYLE_OUTPUT,
                                                                                                             DEFAULT_STYLE_OUTPUT)),
                                                             new HCTextNode (" format?")));
      aForm.addFormGroup (new BootstrapFormGroup ().setCtrl (new BootstrapSubmitButton ().addChild ("Run")));
      aNodeList.addChild (aToolbar);
    }

    final String sVESID = aWPEC.params ().getAsString (FIELD_VESID);
    final VESID aVESID = VESID.parseIDOrNull (sVESID);
    final boolean bStyleOutput = aWPEC.params ().isCheckBoxChecked (FIELD_STYLE_OUTPUT, DEFAULT_STYLE_OUTPUT);
    final IValidationExecutorSet aVES = ExtValidationKeyRegistry.getFromIDOrNull (aVESID);

    if (aVES != null)
    {
      if (aWPEC.hasAction (ACTION_SHOW_PREPROCESSED_SCHEMA))
      {
        s_aLogger.info ("Showing preprocessed Schematron of " + aVESID.getAsSingleID ());

        final MapBasedNamespaceContext aNSCtx = new MapBasedNamespaceContext ();
        aNSCtx.addDefaultNamespaceURI (CSchematron.NAMESPACE_SCHEMATRON);
        aNSCtx.addMapping ("xsl", "http://www.w3.org/1999/XSL/Transform");
        aNSCtx.addMapping ("svrl", CSVRL.SVRL_NAMESPACE_URI);
        final IXMLWriterSettings XWS = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.INDENT_AND_ALIGN)
                                                               .setNamespaceContext (aNSCtx);

        aNodeList.addChild (new BootstrapInfoBox ().addChild ("Showing preprocessed version of " +
                                                              aVESID.getAsSingleID ()));
        final BootstrapTabBox aTabBox = new BootstrapTabBox ();
        for (final IValidationExecutor aVE : aVES.getAllExecutors ())
        {
          final IValidationArtefact aArtefact = aVE.getValidationArtefact ();
          switch (aVE.getValidationType ())
          {
            case SCHEMATRON_PURE:
            case SCHEMATRON_SCH:
            {
              IHCNode aTabContent;
              try
              {
                // Read Schematron
                final PSSchema aSchema = new PSReader (aArtefact.getRuleResource (), null, null).readSchema ();
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
                  final HCPrismJS aPrism = new HCPrismJS (EPrismLanguage.MARKUP).addPlugin (EPrismPlugin.LINE_NUMBERS)
                                                                                .addChild (sXML);
                  aCode = aPrism;
                }
                else
                  aCode = new HCPre ().addChild (sXML);

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
                  aTabContent = new HCNodeList ().addChild (new BootstrapErrorBox ().addChild ("Errors in the Schematron:")
                                                                                    .addChild (aUL))
                                                 .addChild (aCode);
                }
              }
              catch (final Exception ex)
              {
                aTabContent = new BootstrapErrorBox ().addChild ("Error parsing Schematron: " + ex.getMessage ());
              }
              aTabBox.addTab ("t" + aTabBox.getTabCount (),
                              FilenameHelper.getBaseName (aArtefact.getRuleResource ().getPath ()),
                              aTabContent);
              break;
            }
            case SCHEMATRON_XSLT:
            {
              final IHCNode aTabContent = new BootstrapInfoBox ().addChild ("This is already XSLT");
              aTabBox.addTab ("t" + aTabBox.getTabCount (),
                              FilenameHelper.getBaseName (aArtefact.getRuleResource ().getPath ()),
                              aTabContent);
              break;
            }
          }
        }
        if (aTabBox.hasNoTabs ())
          aNodeList.addChild (new BootstrapWarnBox ().addChild ("No Schematron artefacts found"));
        else
          aNodeList.addChild (aTabBox);
      }
      else
        if (aWPEC.hasAction (ACTION_SHOW_XSLT))
        {
          s_aLogger.info ("Showing XSLT version of " + aVESID.getAsSingleID ());

          final MapBasedNamespaceContext aNSCtx = new MapBasedNamespaceContext ();
          aNSCtx.addDefaultNamespaceURI (CSchematron.NAMESPACE_SCHEMATRON);
          aNSCtx.addMapping ("xsl", "http://www.w3.org/1999/XSL/Transform");
          aNSCtx.addMapping ("svrl", CSVRL.SVRL_NAMESPACE_URI);
          final IXMLWriterSettings XWS = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.INDENT_AND_ALIGN)
                                                                 .setNamespaceContext (aNSCtx);

          aNodeList.addChild (new BootstrapInfoBox ().addChild ("Showing XSLT version of " + aVESID.getAsSingleID ()));
          final BootstrapTabBox aTabBox = new BootstrapTabBox ();
          for (final IValidationExecutor aVE : aVES.getAllExecutors ())
          {
            final IValidationArtefact aArtefact = aVE.getValidationArtefact ();
            switch (aVE.getValidationType ())
            {
              case SCHEMATRON_PURE:
              case SCHEMATRON_SCH:
              {
                IHCNode aTabContent;
                try
                {
                  // Read Schematron
                  final SchematronResourceSCH aSch = new SchematronResourceSCH (aArtefact.getRuleResource ());
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
                    final HCPrismJS aPrism = new HCPrismJS (EPrismLanguage.MARKUP).addPlugin (EPrismPlugin.LINE_NUMBERS)
                                                                                  .addChild (sXML);

                    aTabContent = aPrism;
                  }
                  else
                    aTabContent = new HCPre ().addChild (sXML);
                }
                catch (final Exception ex)
                {
                  aTabContent = new BootstrapErrorBox ().addChild ("Error parsing Schematron: " + ex.getMessage ());
                }
                aTabBox.addTab ("t" + aTabBox.getTabCount (),
                                FilenameHelper.getBaseName (aArtefact.getRuleResource ().getPath ()),
                                aTabContent);
                break;
              }
              case SCHEMATRON_XSLT:
              {
                final IHCNode aTabContent = new BootstrapInfoBox ().addChild ("This is already XSLT");
                aTabBox.addTab ("t" + aTabBox.getTabCount (),
                                FilenameHelper.getBaseName (aArtefact.getRuleResource ().getPath ()),
                                aTabContent);
                break;
              }
            }
          }
          if (aTabBox.hasNoTabs ())
            aNodeList.addChild (new BootstrapWarnBox ().addChild ("No Schematron artefacts found"));
          else
            aNodeList.addChild (aTabBox);
        }
        else
        {
          // TODO other action when necessary
        }
      s_aLogger.info ("Done");
    }
  }
}
