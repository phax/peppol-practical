/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import java.nio.charset.StandardCharsets;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.X509Certificate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.helger.commons.state.EValidity;
import com.helger.commons.state.IValidityIndicator;
import com.helger.commons.string.StringHelper;
import com.helger.commons.wrapper.Wrapper;
import com.helger.config.IConfig;
import com.helger.css.property.CCSSProperties;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCHiddenField;
import com.helger.html.hc.html.forms.HCTextArea;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.jaxb.GenericJAXBMarshaller;
import com.helger.peppol.app.AppConfig;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.ui.AppCommonUI;
import com.helger.peppol.utils.EPeppolCertificateCheckResult;
import com.helger.peppolid.IDocumentTypeIdentifier;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.peppolid.peppol.doctype.EPredefinedDocumentTypeIdentifier;
import com.helger.peppolid.peppol.process.EPredefinedProcessIdentifier;
import com.helger.phase4.client.IAS4ClientBuildMessageCallback;
import com.helger.phase4.crypto.AS4CryptoFactoryInMemoryKeyStore;
import com.helger.phase4.crypto.IAS4CryptoFactory;
import com.helger.phase4.dump.AS4IncomingDumperFileBased;
import com.helger.phase4.dump.AS4OutgoingDumperFileBased;
import com.helger.phase4.ebms3header.Ebms3SignalMessage;
import com.helger.phase4.messaging.domain.AS4UserMessage;
import com.helger.phase4.messaging.domain.AbstractAS4Message;
import com.helger.phase4.peppol.Phase4PeppolSender;
import com.helger.phase4.sender.AbstractAS4UserMessageBuilder.ESimpleUserMessageSendResult;
import com.helger.phase4.util.Phase4Exception;
import com.helger.photon.bootstrap4.button.BootstrapSubmitButton;
import com.helger.photon.bootstrap4.form.BootstrapForm;
import com.helger.photon.bootstrap4.form.BootstrapFormGroup;
import com.helger.photon.bootstrap4.pages.AbstractBootstrapWebPage;
import com.helger.photon.bootstrap4.uictrls.prism.BootstrapPrismJS;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.prism.EPrismLanguage;
import com.helger.photon.uictrls.prism.PrismPluginLineNumbers;
import com.helger.security.keystore.EKeyStoreType;
import com.helger.security.keystore.IKeyStoreType;
import com.helger.security.keystore.KeyStoreHelper;
import com.helger.security.keystore.LoadedKey;
import com.helger.security.keystore.LoadedKeyStore;
import com.helger.smpclient.peppol.SMPClientReadOnly;
import com.helger.smpclient.url.SMPDNSResolutionException;
import com.helger.xml.serialize.read.DOMReader;
import com.helger.xml.serialize.write.XMLWriter;

public class PageSecurePeppolSendAS4 extends AbstractBootstrapWebPage <WebPageExecutionContext>
{
  public static final String FIELD_SENDER_ID = "senderid";
  public static final String FIELD_RECEIVER_ID = "receiverid";
  public static final String FIELD_DOCTYPE_ID = "doctypeid";
  public static final String FIELD_PROCESS_ID = "processid";
  public static final String FIELD_PAYLOAD = "payload";

  private static final Logger LOGGER = LoggerFactory.getLogger (PageSecurePeppolSendAS4.class);
  private static final boolean DEFAULTS = true;
  private static final String DEFAULT_SENDER_ID = DEFAULTS ? "iso6523-actorid-upis::9915:helger" : null;
  private static final String DEFAULT_RECEIVER_ID = DEFAULTS ? "iso6523-actorid-upis::9915:honeypot" : null;
  private static final String DEFAULT_DOCTYPE_ID = DEFAULTS ? EPredefinedDocumentTypeIdentifier.INVOICE_EN16931_PEPPOL_V30.getAsDocumentTypeIdentifier ()
                                                                                                                          .getURIEncoded ()
                                                            : null;
  private static final String DEFAULT_PROCESS_ID = DEFAULTS ? EPredefinedProcessIdentifier.BIS3_BILLING.getAsProcessIdentifier ()
                                                                                                       .getURIEncoded ()
                                                            : null;
  // Namespace URI must be present
  private static final Supplier <String> DEFAULT_PAYLOAD = () -> DEFAULTS ? "<?xml version='1.0' encoding='UTF-8'?>\n<!-- ... -->"
                                                                          : null;

  private static final IAS4CryptoFactory AS4_CF;
  static
  {
    final IConfig aConfig = AppConfig.getConfig ();
    final IKeyStoreType eTrustStoreType = EKeyStoreType.getFromIDCaseInsensitiveOrNull (aConfig.getAsString ("peppol.as4.truststore.type"));
    final LoadedKeyStore aLTS = KeyStoreHelper.loadKeyStore (eTrustStoreType,
                                                             aConfig.getAsString ("peppol.as4.truststore.path"),
                                                             aConfig.getAsString ("peppol.as4.truststore.password"));
    if (aLTS.isSuccess ())
    {
      LOGGER.info ("Loaded Peppol Trust Store");
      final IKeyStoreType eKeyStoreType = EKeyStoreType.getFromIDCaseInsensitiveOrNull (aConfig.getAsString ("peppol.as4.keystore.type"));
      final LoadedKeyStore aLKS = KeyStoreHelper.loadKeyStore (eKeyStoreType,
                                                               aConfig.getAsString ("peppol.as4.keystore.path"),
                                                               aConfig.getAsString ("peppol.as4.keystore.password"));
      if (aLKS.isSuccess ())
      {
        LOGGER.info ("Loaded Peppol Key Store");
        final LoadedKey <PrivateKeyEntry> aLPK = KeyStoreHelper.loadPrivateKey (aLKS.getKeyStore (),
                                                                                aConfig.getAsString ("peppol.as4.keystore.path"),
                                                                                aConfig.getAsString ("peppol.as4.keystore.key.alias"),
                                                                                aConfig.getAsCharArray ("peppol.as4.keystore.key.password"));
        if (aLPK.isSuccess ())
        {
          LOGGER.info ("Loaded Peppol Key");
          AS4_CF = new AS4CryptoFactoryInMemoryKeyStore (aLKS.getKeyStore (),
                                                         aConfig.getAsString ("peppol.as4.keystore.key.alias"),
                                                         aConfig.getAsString ("peppol.as4.keystore.key.password"),
                                                         aLTS.getKeyStore ());
        }
        else
        {
          LOGGER.error ("Failed to load Peppol Key");
          AS4_CF = null;
        }
      }
      else
      {
        LOGGER.error ("Failed to load Peppol Key Store");
        AS4_CF = null;
      }
    }
    else
    {
      LOGGER.error ("Failed to load Peppol Trust Store");
      AS4_CF = null;
    }
  }

  public PageSecurePeppolSendAS4 (final String sID)
  {
    super (sID, "Send Peppol AS4 message");
  }

  @Override
  protected IValidityIndicator isValidToDisplayPage (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    if (AS4_CF == null)
    {
      aNodeList.addChild (error ("Cannot display this page, because the Peppol Certificate configuration is incomplete"));
      return EValidity.INVALID;
    }
    return super.isValidToDisplayPage (aWPEC);
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final IIdentifierFactory aIF = Phase4PeppolSender.IF;

    final FormErrorList aFormErrors = new FormErrorList ();

    if (aWPEC.params ().hasStringValue (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM))
    {
      final String sSenderID = aWPEC.params ().getAsStringTrimmed (FIELD_SENDER_ID);
      final IParticipantIdentifier aSenderID = aIF.parseParticipantIdentifier (sSenderID);

      final String sReceiverID = aWPEC.params ().getAsStringTrimmed (FIELD_RECEIVER_ID);
      final IParticipantIdentifier aReceiverID = aIF.parseParticipantIdentifier (sReceiverID);

      final String sDocTypeID = aWPEC.params ().getAsStringTrimmed (FIELD_DOCTYPE_ID);
      final IDocumentTypeIdentifier aDocTypeID = aIF.parseDocumentTypeIdentifier (sDocTypeID);

      final String sProcessID = aWPEC.params ().getAsStringTrimmed (FIELD_PROCESS_ID);
      final IProcessIdentifier aProcessID = aIF.parseProcessIdentifier (sProcessID);

      final String sPayload = aWPEC.params ().getAsStringTrimmed (FIELD_PAYLOAD);
      final Document aPayloadDoc = DOMReader.readXMLDOM (sPayload);

      if (StringHelper.hasNoText (sSenderID))
        aFormErrors.addFieldError (FIELD_SENDER_ID, "A sending participant ID must be provided.");
      else
        if (aSenderID == null)
          aFormErrors.addFieldError (FIELD_SENDER_ID, "The sending participant ID could not be parsed.");

      if (StringHelper.hasNoText (sReceiverID))
        aFormErrors.addFieldError (FIELD_RECEIVER_ID, "A receiving participant ID must be provided.");
      else
        if (aReceiverID == null)
          aFormErrors.addFieldError (FIELD_RECEIVER_ID, "The receiving participant ID could not be parsed.");

      if (StringHelper.hasNoText (sDocTypeID))
        aFormErrors.addFieldError (FIELD_DOCTYPE_ID, "A document type ID must be provided.");
      else
        if (aDocTypeID == null)
          aFormErrors.addFieldError (FIELD_DOCTYPE_ID, "The document type ID could not be parsed.");

      if (StringHelper.hasNoText (sProcessID))
        aFormErrors.addFieldError (FIELD_PROCESS_ID, "A process ID must be provided.");
      else
        if (aProcessID == null)
          aFormErrors.addFieldError (FIELD_PROCESS_ID, "The process ID could not be parsed.");

      if (StringHelper.hasNoText (sPayload))
        aFormErrors.addFieldError (FIELD_PAYLOAD, "A payload must be provided.");
      else
        if (aPayloadDoc == null)
          aFormErrors.addFieldError (FIELD_PAYLOAD, "The payload is not wellformed XML.");

      if (aFormErrors.isEmpty ())
      {
        final HCDiv aNL = new HCDiv ().addStyle (CCSSProperties.MAX_WIDTH.newValue ("80vw"));

        final String sAS4PayloadDoc = XMLWriter.getNodeAsString (aPayloadDoc);
        final byte [] aAS4PayloadBytes = XMLWriter.getNodeAsBytes (aPayloadDoc);

        aNL.addChild (h3 ("Sending document"));

        // Show payload
        aNL.addChild (new BootstrapPrismJS (EPrismLanguage.MARKUP).addPlugin (new PrismPluginLineNumbers ())
                                                                  .addChild (sAS4PayloadDoc));

        final IAS4ClientBuildMessageCallback aBuildMessageCallback = new IAS4ClientBuildMessageCallback ()
        {
          public void onAS4Message (final AbstractAS4Message <?> aMsg)
          {
            final AS4UserMessage aUserMsg = (AS4UserMessage) aMsg;
            LOGGER.info ("Sending out AS4 message with message ID '" +
                         aUserMsg.getEbms3UserMessage ().getMessageInfo ().getMessageId () +
                         "'");
          }
        };

        try
        {
          final SMPClientReadOnly aSMPClient = new SMPClientReadOnly (Phase4PeppolSender.URL_PROVIDER,
                                                                      aReceiverID,
                                                                      ESML.DIGIT_TEST);

          // What to remember
          final Wrapper <String> aEndpointURL = new Wrapper <> ();
          final Wrapper <X509Certificate> aEndpointCert = new Wrapper <> ();
          final Wrapper <EPeppolCertificateCheckResult> aEndpointCertCheck = new Wrapper <> ();
          final Wrapper <Phase4Exception> aSendEx = new Wrapper <> ();
          final Wrapper <byte []> aResponseBytes = new Wrapper <> ();
          final Wrapper <Ebms3SignalMessage> aResponseMsg = new Wrapper <> ();

          LOGGER.info ("Sending Peppol AS4 message from '" +
                       aSenderID.getURIEncoded () +
                       "' to '" +
                       aReceiverID.getURIEncoded () +
                       "' using document type '" +
                       aDocTypeID.getURIEncoded () +
                       "' and process ID '" +
                       aProcessID.getURIEncoded () +
                       "'");

          // Try to send message
          final ESimpleUserMessageSendResult eResult = Phase4PeppolSender.builder ()
                                                                         .cryptoFactory (AS4_CF)
                                                                         .documentTypeID (aDocTypeID)
                                                                         .processID (aProcessID)
                                                                         .senderParticipantID (aSenderID)
                                                                         .receiverParticipantID (aReceiverID)
                                                                         .senderPartyID ("POP000306")
                                                                         .payload (aAS4PayloadBytes)
                                                                         .smpClient (aSMPClient)
                                                                         .endpointURLConsumer (aEndpointURL::set)
                                                                         .certificateConsumer ( (cert, dt, res) -> {
                                                                           aEndpointCert.set (cert);
                                                                           aEndpointCertCheck.set (res);
                                                                         })
                                                                         .validationConfiguration (null)
                                                                         .buildMessageCallback (aBuildMessageCallback)
                                                                         .outgoingDumper (new AS4OutgoingDumperFileBased ())
                                                                         .incomingDumper (new AS4IncomingDumperFileBased ())
                                                                         .rawResponseConsumer (r -> aResponseBytes.set (r.getResponse ()))
                                                                         .signalMsgConsumer ( (signalMsg,
                                                                                               mmd,
                                                                                               state) -> aResponseMsg.set (signalMsg))
                                                                         .sendMessageAndCheckForReceipt (aSendEx::set);

          LOGGER.info ("Sending Peppol AS4 message resulted in " + eResult);

          if (aEndpointURL.isSet ())
            aNL.addChild (div ("Sending to this endpoint URL: ").addChild (code (aEndpointURL.get ())));
          if (aEndpointCert.isSet ())
            aNL.addChild (div ("The message is encrypted for the following receiver: ").addChild (code (aEndpointCert.get ()
                                                                                                                     .getSubjectX500Principal ()
                                                                                                                     .getName ())));
          if (aEndpointCertCheck.isSet ())
            aNL.addChild (div ("The certificate verification resulted in: ").addChild (code (aEndpointCertCheck.get ()
                                                                                                               .name ())));

          if (eResult.isSuccess ())
            aNL.addChild (success ("Successfully send AS4 message to Peppol receiver ").addChild (code (aReceiverID.getURIEncoded ())));
          else
            aNL.addChild (error ().addChild (div ("Failed to send AS4 message to Peppol receiver ").addChild (code (aReceiverID.getURIEncoded ()))
                                                                                                   .addChild (" with result ")
                                                                                                   .addChild (code (eResult.name ())))
                                  .addChild (AppCommonUI.getTechnicalDetailsUI (aSendEx.get (), true)));

          boolean bShowRaw = true;
          if (aResponseMsg.isSet ())
          {
            // Don't do XSD validation here because there is no defined
            // "SignalMessage" element
            final String sSignalMessage = new GenericJAXBMarshaller <> (Ebms3SignalMessage.class,
                                                                        GenericJAXBMarshaller.createSimpleJAXBElement (new QName (com.helger.phase4.ebms3header.ObjectFactory._Messaging_QNAME.getNamespaceURI (),
                                                                                                                                  "SignalMessage"),
                                                                                                                       Ebms3SignalMessage.class)).setFormattedOutput (true)
                                                                                                                                                 .getAsString (aResponseMsg.get ());
            if (StringHelper.hasText (sSignalMessage))
            {
              // Show payload
              aNL.addChild (div ("Response ebMS Signal Message"));
              aNL.addChild (new BootstrapPrismJS (EPrismLanguage.MARKUP).addPlugin (new PrismPluginLineNumbers ())
                                                                        .addChild (sSignalMessage));
              bShowRaw = false;
            }
          }
          if (aResponseBytes.isSet ())
          {
            if (bShowRaw)
            {
              aNL.addChild (div ("Response message - NOT a valid response"));
              aNL.addChild (new BootstrapPrismJS (EPrismLanguage.MARKUP).addPlugin (new PrismPluginLineNumbers ())
                                                                        .addChild (new String (aResponseBytes.get (),
                                                                                               StandardCharsets.UTF_8)));
            }
            // Else already shown above
          }
          else
          {
            if (eResult.isSuccess ())
              aNL.addChild (error ("Received no response content :("));
          }
        }
        catch (final SMPDNSResolutionException ex)
        {
          aNL.addChild (error (div ("Error creating the SMP client.")).addChild (AppCommonUI.getTechnicalDetailsUI (ex,
                                                                                                                    false)));
        }

        if (true)
          aNodeList.addChild (aNL);
        else
          aWPEC.postRedirectGetInternal (aNL);
      }
      else
        aNodeList.addChild (getUIHandler ().createIncorrectInputBox (aWPEC));
    }

    aNodeList.addChild (h3 ("Send new Peppol AS4 message (Test network only)"));

    final BootstrapForm aForm = aNodeList.addAndReturnChild (new BootstrapForm (aWPEC));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Sending participant ID")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_SENDER_ID,
                                                                                         DEFAULT_SENDER_ID)))
                                                 .setHelpText (span ("The sending Peppol participant identifier. Must include the ").addChild (code (PeppolIdentifierHelper.DEFAULT_PARTICIPANT_SCHEME))
                                                                                                                                    .addChild (" prefix."))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_SENDER_ID)));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Receiving participant ID")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_RECEIVER_ID,
                                                                                         DEFAULT_RECEIVER_ID)))
                                                 .setHelpText (span ("The receiving Peppol participant identifier. Must include the ").addChild (code (PeppolIdentifierHelper.DEFAULT_PARTICIPANT_SCHEME))
                                                                                                                                      .addChild (" prefix."))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_RECEIVER_ID)));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Document type ID")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_DOCTYPE_ID,
                                                                                         DEFAULT_DOCTYPE_ID)))
                                                 .setHelpText (span ("The Peppol document type identifier. Must include the ").addChild (code (PeppolIdentifierHelper.DOCUMENT_TYPE_SCHEME_BUSDOX_DOCID_QNS))
                                                                                                                              .addChild (" prefix."))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_DOCTYPE_ID)));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Process ID")
                                                 .setCtrl (new HCEdit (new RequestField (FIELD_PROCESS_ID,
                                                                                         DEFAULT_PROCESS_ID)))
                                                 .setHelpText (span ("The Peppol process identifier. Must include the ").addChild (code (PeppolIdentifierHelper.DEFAULT_PROCESS_SCHEME))
                                                                                                                        .addChild (" prefix."))
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_PROCESS_ID)));
    aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("XML Payload to be send")
                                                 .setCtrl (new HCTextArea (new RequestField (FIELD_PAYLOAD,
                                                                                             DEFAULT_PAYLOAD.get ())).setRows (8))
                                                 .setHelpText ("This MUST be wellformed XML - e.g. a UBL Invoice or a CII Invoice. NO Schematron validation is performed. The SBDH is added automatically.")
                                                 .setErrorList (aFormErrors.getListOfField (FIELD_PAYLOAD)));
    aForm.addChild (new HCHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM));
    aForm.addChild (new BootstrapSubmitButton ().addChild ("Send Peppol AS4 message"));
  }
}
