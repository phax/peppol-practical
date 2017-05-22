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
package com.helger.peppol.secure.page;

import java.util.Locale;

import javax.annotation.Nonnull;

import com.helger.bdve.artefact.IValidationArtefact;
import com.helger.bdve.execute.IValidationExecutor;
import com.helger.bdve.executorset.IValidationExecutorSet;
import com.helger.bdve.executorset.VESID;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.io.file.FilenameHelper;
import com.helger.html.hc.IHCNode;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.pub.validation.ExtValidationKeyRegistry;
import com.helger.peppol.pub.validation.ExtValidationKeySelect;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.photon.bootstrap3.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap3.alert.BootstrapInfoBox;
import com.helger.photon.bootstrap3.alert.BootstrapWarnBox;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.button.BootstrapSubmitButton;
import com.helger.photon.bootstrap3.form.BootstrapForm;
import com.helger.photon.bootstrap3.form.BootstrapFormGroup;
import com.helger.photon.bootstrap3.form.EBootstrapFormType;
import com.helger.photon.bootstrap3.nav.BootstrapTabBox;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.html.select.HCExtSelect;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.prism.EPrismLanguage;
import com.helger.photon.uictrls.prism.EPrismPlugin;
import com.helger.photon.uictrls.prism.HCPrismJS;
import com.helger.schematron.pure.binding.IPSQueryBinding;
import com.helger.schematron.pure.binding.PSQueryBindingRegistry;
import com.helger.schematron.pure.errorhandler.CollectingPSErrorHandler;
import com.helger.schematron.pure.exchange.PSReader;
import com.helger.schematron.pure.model.PSSchema;
import com.helger.schematron.pure.preprocess.PSPreprocessor;
import com.helger.schematron.pure.preprocess.SchematronPreprocessException;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.serialize.write.EXMLSerializeIndent;
import com.helger.xml.serialize.write.IXMLWriterSettings;
import com.helger.xml.serialize.write.XMLWriterSettings;

public final class PageSecureSchematronTools extends AbstractAppWebPage
{
  private static final String ACTION_SHOW_PREPROCESSED_SCHEMA = "showpps";
  private static final String FIELD_VESID = "vesid";

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
      aForm.addFormGroup (new BootstrapFormGroup ().setCtrl (new BootstrapSubmitButton ().addChild ("Run")));
      aNodeList.addChild (aToolbar);
    }

    final String sVESID = aWPEC.getAttributeAsString (FIELD_VESID);
    final VESID aVESID = VESID.parseIDOrNull (sVESID);
    final IValidationExecutorSet aVES = ExtValidationKeyRegistry.getFromIDOrNull (aVESID);

    if (aVES != null)
    {
      if (aWPEC.hasAction (ACTION_SHOW_PREPROCESSED_SCHEMA))
      {
        final IXMLWriterSettings XWS = new XMLWriterSettings ().setIndent (EXMLSerializeIndent.INDENT_AND_ALIGN);

        aNodeList.addChild (new BootstrapInfoBox ().addChild ("Showing details of " + aVESID.getAsSingleID ()));
        final BootstrapTabBox aTabBox = new BootstrapTabBox ();
        for (final IValidationExecutor aVE : aVES.getAllExecutors ())
          if (aVE.getValidationType ().isSchematronBased ())
          {
            final IValidationArtefact aArtefact = aVE.getValidationArtefact ();
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
              // Highlight
              final HCPrismJS aPrism = new HCPrismJS (EPrismLanguage.MARKUP).addPlugin (EPrismPlugin.LINE_NUMBERS)
                                                                            .addChild (sXML);

              final CollectingPSErrorHandler aErrHdl = new CollectingPSErrorHandler ();
              aPreprocessedSchema.validateCompletely (aErrHdl);
              if (aErrHdl.isEmpty ())
                aTabContent = aPrism;
              else
              {
                final HCUL aUL = new HCUL ();
                aErrHdl.getAllErrors ().forEach (x -> aUL.addItem (x.getErrorText (aDisplayLocale)));
                aTabContent = new HCNodeList ().addChild (new BootstrapErrorBox ().addChild ("Errors in the Schematron:")
                                                                                  .addChild (aUL))
                                               .addChild (aPrism);
              }
            }
            catch (final Exception ex)
            {
              aTabContent = new BootstrapErrorBox ().addChild ("Error parsing Schematron: " + ex.getMessage ());
            }
            aTabBox.addTab ("t" +
                            aTabBox.getTabCount (),
                            FilenameHelper.getBaseName (aArtefact.getRuleResource ().getPath ()),
                            aTabContent);
          }
        if (aTabBox.hasNoTabs ())
          aNodeList.addChild (new BootstrapWarnBox ().addChild ("No Schematron artefacts found"));
        else
          aNodeList.addChild (aTabBox);
      }
      else
      {
        // TODO
      }
    }
  }
}
