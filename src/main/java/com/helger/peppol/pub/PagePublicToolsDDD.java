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

import java.util.Comparator;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.SimpleURL;
import com.helger.ddd.DDDVersion;
import com.helger.ddd.DocumentDetails;
import com.helger.ddd.DocumentDetailsDeterminator;
import com.helger.ddd.model.DDDSyntax;
import com.helger.ddd.model.DDDSyntaxList;
import com.helger.ddd.model.DDDValueProviderList;
import com.helger.html.hc.html.forms.HCHiddenField;
import com.helger.html.hc.html.forms.HCTextArea;
import com.helger.html.hc.html.grouping.HCUL;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.photon.bootstrap4.button.BootstrapSubmitButton;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPage;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.xml.serialize.read.DOMReader;

public final class PagePublicToolsDDD extends AbstractBootstrapWebPage <WebPageExecutionContext>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (PagePublicToolsDDD.class);
  private static final String FIELD_PAYLOAD = "payload";
  private static final DDDSyntaxList DDD_SL = DDDSyntaxList.getDefaultSyntaxList ();
  private static final DocumentDetailsDeterminator DDD = new DocumentDetailsDeterminator (DDD_SL,
                                                                                          DDDValueProviderList.getDefaultValueProviderList ());

  public PagePublicToolsDDD (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Run DDD");
  }

  @Override
  public void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();

    aNodeList.addChild (info ("This page uses DDD v" +
                              DDDVersion.getVersionNumber () +
                              " to determine the VESID of the provided document. DDD is an ").addChild (new HCA ().setHref (new SimpleURL ("https://github.com/phax/ddd"))
                                                                                                                  .addChild ("OpenSource library"))
                                                                                             .addChild (" to detect document details for choosing the correct validation artefacts."));

    final boolean bShowForm = true;
    final FormErrorList aFormErrors = new FormErrorList ();
    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      final String sPayload = aWPEC.params ().getAsStringTrimmed (FIELD_PAYLOAD);
      if (StringHelper.hasNoText (sPayload))
        aFormErrors.addFieldError (FIELD_PAYLOAD, "No payload was provided");
      else
      {
        final Document aDoc = DOMReader.readXMLDOM (sPayload);
        if (aDoc == null || aDoc.getDocumentElement () == null)
          aFormErrors.addFieldError (FIELD_PAYLOAD, "The provided payload is invalid XML");
        else
        {
          aNodeList.addChild (success ("Successfully loaded payload as XML"));
          LOGGER.info ("Now running DDD");
          final DocumentDetails aDocDetails = DDD.findDocumentDetails (aDoc.getDocumentElement ());
          if (aDocDetails != null)
          {
            final HCUL aUL = new HCUL ();

            aUL.addItem (span ("Profile Name: ").addChild (aDocDetails.hasProfileName () ? code (aDocDetails.getProfileName ())
                                                                                         : em ("not found")));
            aUL.addItem (span ("Syntax ID: ").addChild (aDocDetails.hasSyntaxID () ? code (aDocDetails.getSyntaxID ())
                                                                                   : em ("not found")));
            aUL.addItem (span ("Sender ID: ").addChild (aDocDetails.hasSenderID () ? code (aDocDetails.getSenderID ()
                                                                                                      .getURIEncoded ())
                                                                                   : em ("not found")));
            aUL.addItem (span ("Sender Name: ").addChild (aDocDetails.hasSenderName () ? code (aDocDetails.getSenderName ())
                                                                                       : em ("not found")));
            aUL.addItem (span ("Sender Country Code: ").addChild (aDocDetails.hasSenderCountryCode () ? code (aDocDetails.getSenderCountryCode ())
                                                                                                      : em ("not found")));
            aUL.addItem (span ("Receiver ID: ").addChild (aDocDetails.hasReceiverID () ? code (aDocDetails.getReceiverID ()
                                                                                                          .getURIEncoded ())
                                                                                       : em ("not found")));
            aUL.addItem (span ("Receiver Name: ").addChild (aDocDetails.hasReceiverName () ? code (aDocDetails.getReceiverName ())
                                                                                           : em ("not found")));
            aUL.addItem (span ("Receiver Country Code: ").addChild (aDocDetails.hasReceiverCountryCode () ? code (aDocDetails.getReceiverCountryCode ())
                                                                                                          : em ("not found")));
            aUL.addItem (span ("Document Type ID: ").addChild (aDocDetails.hasDocumentTypeID () ? code (aDocDetails.getDocumentTypeID ()
                                                                                                                   .getURIEncoded ())
                                                                                                : em ("not found")));
            aUL.addItem (span ("Process ID: ").addChild (aDocDetails.hasProcessID () ? code (aDocDetails.getProcessID ()
                                                                                                        .getURIEncoded ())
                                                                                     : em ("not found")));
            aUL.addItem (span ("Business Document ID: ").addChild (aDocDetails.hasBusinessDocumentID () ? code (aDocDetails.getBusinessDocumentID ())
                                                                                                        : em ("not found")));
            aUL.addItem (span ("VESID: ").addChild (aDocDetails.hasVESID () ? code (aDocDetails.getVESID ())
                                                                            : em ("not found")));
            aNodeList.addChild (success ("DDD results:").addChild (aUL));
          }
          else
          {
            final HCUL aUL = new HCUL ();
            for (final var e : CollectionHelper.getSortedByValue (DDD_SL.getAllSyntaxes (),
                                                                  Comparator.comparing (DDDSyntax::getName))
                                               .values ())
              aUL.addItem (e.getName ());

            aNodeList.addChild (warn (div ("The payload XML is not known by this DDD version. Supported syntaxes are:").addChild (aUL)));
          }
        }
      }
    }

    if (bShowForm)
    {
      final BootstrapForm aForm = aNodeList.addAndReturnChild (getUIHandler ().createFormSelf (aWPEC));
      aForm.setLeft (2);
      aForm.addChild (new HCHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM));
      aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Payload XML")
                                                   .setCtrl (new HCTextArea (new RequestField (FIELD_PAYLOAD)).setRows (10))
                                                   .setHelpText ("Put in the full business document you want to have determined. The determination is currently limited to documents available in the Peppol Network. The content is only analyzed by the service and NOT stored persistently.")
                                                   .setErrorList (aFormErrors.getListOfField (FIELD_PAYLOAD)));
      aForm.addChild (new BootstrapSubmitButton ().addChild ("Determine details"));
    }
  }
}
