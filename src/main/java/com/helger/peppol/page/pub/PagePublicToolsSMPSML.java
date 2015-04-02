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
package com.helger.peppol.page.pub;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import com.helger.appbasics.security.audit.AuditUtils;
import com.helger.bootstrap3.alert.BootstrapErrorBox;
import com.helger.bootstrap3.alert.BootstrapInfoBox;
import com.helger.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.form.BootstrapForm;
import com.helger.bootstrap3.form.BootstrapFormGroup;
import com.helger.bootstrap3.form.BootstrapHelpBlock;
import com.helger.bootstrap3.form.EBootstrapFormType;
import com.helger.bootstrap3.nav.BootstrapTabBox;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.io.streams.StreamUtils;
import com.helger.commons.random.VerySecureRandom;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.url.URLUtils;
import com.helger.html.hc.CHCParam;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCEditFile;
import com.helger.html.hc.html.HCEditPassword;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.AppUtils;
import com.helger.peppol.page.ui.SMLSelect;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.smlclient.smp.BadRequestFault;
import com.helger.peppol.smlclient.smp.InternalErrorFault;
import com.helger.peppol.smlclient.smp.NotFoundFault;
import com.helger.peppol.smlclient.smp.UnauthorizedFault;
import com.helger.peppol.utils.KeyStoreUtils;
import com.helger.validation.error.FormErrors;
import com.helger.web.fileupload.IFileItem;
import com.helger.web.https.DoNothingTrustManager;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webctrls.page.AbstractWebPageExt;
import com.sun.xml.ws.client.ClientTransportException;

public class PagePublicToolsSMPSML extends AbstractWebPageExt <WebPageExecutionContext>
{
  private static final String FIELD_SML = "sml";
  private static final String FIELD_SMP_ID = "smpid";
  private static final String FIELD_PHYSICAL_ADDRESS = "physicaladdr";
  private static final String FIELD_LOGICAL_ADDRESS = "logicaladdr";
  private static final String FIELD_KEYSTORE = "keystore";
  private static final String FIELD_KEYSTORE_PW = "keystorepw";

  private static final String HELPTEXT_SMP_ID = "This is the unique ID your SMP will have inside the SML. All continuing operations must use this ID. You can choose this ID yourself but please make sure it only contains characters, numbers and the hyphen character. All uppercase names are appreciated!";
  private static final String HELPTEXT_PHYSICAL_ADDRESS = "This must be the IPv4 address of your SMP. IPv6 addresses are not yet supported!";
  private static final String HELPTEXT_LOGICAL_ADDRESS = "This must be the fully qualified domain name of your SMP. This can be either a domain name like 'http://smp.example.org' or a IP address like 'http://1.1.1.1'!";
  private static final String HELPTEXT_KEYSTORE = "A Java key store of type JKS with only your PEPPOL SMP certificate is required to perform the action! The uploaded key store is used for nothing else than for this selected action and will be discarded afterwards!";
  private static final String HELPTEXT_KEYSTORE_PW = "The password of the JKS key store is required to access the content of the key store!";

  private static final String SUBACTION_SMP_REGISTER = "smpregister";
  private static final String SUBACTION_SMP_UPDATE = "smpupdate";
  private static final String SUBACTION_SMP_DELETE = "smpdelete";

  private static final String PATTERN_SMP_ID = "[a-zA-Z0-9-]+";
  private static final String PATTERN_IPV4 = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b";

  private static final String DEFAULT_SML = ESML.PRODUCTION.name ();

  public PagePublicToolsSMPSML (@Nonnull @Nonempty final String sID)
  {
    super (sID, "SMP - SML tools");
  }

  @Nullable
  private SSLSocketFactory _loadKeyStoreAndCreateSSLSocketFactory (@Nullable final IFileItem aKeyStoreFile,
                                                                   @Nullable final String sKeyStorePassword,
                                                                   @Nonnull final FormErrors aFormErrors)
  {
    KeyStore aKeyStore = null;
    if (aKeyStoreFile == null || aKeyStoreFile.getSize () == 0L)
      aFormErrors.addFieldError (FIELD_KEYSTORE, "A key store file must be selected!");
    else
      if (sKeyStorePassword == null)
      {
        aFormErrors.addFieldError (FIELD_KEYSTORE_PW, "The key store password is missing!");
      }
      else
      {
        // Try to load the key store
        final InputStream aIS = aKeyStoreFile.getInputStream ();
        try
        {
          aKeyStore = KeyStore.getInstance (KeyStoreUtils.KEYSTORE_TYPE_JKS);
          aKeyStore.load (aIS, sKeyStorePassword.toCharArray ());
        }
        catch (final KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex)
        {
          aFormErrors.addFieldError (FIELD_KEYSTORE_PW,
                                     "The key store could not be loaded with the provided password. Technical details: " +
                                         ex.getMessage ());
          aKeyStore = null;
        }
        finally
        {
          StreamUtils.close (aIS);
        }
      }

    SSLSocketFactory aSocketFactory = null;
    if (aKeyStore != null)
    {
      // Try to create the socket factory from the provided key store
      try
      {
        final KeyManagerFactory aKeyManagerFactory = KeyManagerFactory.getInstance ("SunX509");
        aKeyManagerFactory.init (aKeyStore, sKeyStorePassword.toCharArray ());

        final SSLContext aSSLContext = SSLContext.getInstance ("TLS");
        aSSLContext.init (aKeyManagerFactory.getKeyManagers (),
                          new TrustManager [] { new DoNothingTrustManager (false) },
                          VerySecureRandom.getInstance ());
        aSocketFactory = aSSLContext.getSocketFactory ();
      }
      catch (final NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException | KeyStoreException ex)
      {
        aFormErrors.addFieldError (FIELD_KEYSTORE,
                                   "Failed to use the provided key store for TLS connection. Technical details: " +
                                       ex.getMessage ());
      }
    }
    return aSocketFactory;
  }

  private void _registerSMPtoSML (@Nonnull final WebPageExecutionContext aWPEC, @Nonnull final FormErrors aFormErrors)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final String sSML = aWPEC.getAttributeAsString (FIELD_SML);
    final ISMLInfo aSML = AppUtils.getSMLOfID (sSML);
    final String sSMPID = aWPEC.getAttributeAsString (FIELD_SMP_ID);
    final String sPhysicalAddress = aWPEC.getAttributeAsString (FIELD_PHYSICAL_ADDRESS);
    final String sLogicalAddress = aWPEC.getAttributeAsString (FIELD_LOGICAL_ADDRESS);
    final IFileItem aKeyStoreFile = aWPEC.getFileItem (FIELD_KEYSTORE);
    final String sKeyStorePassword = aWPEC.getAttributeAsString (FIELD_KEYSTORE_PW);

    if (aSML == null)
      aFormErrors.addFieldError (FIELD_SML, "A valid SML must be selected!");

    if (StringHelper.hasNoText (sSMPID))
      aFormErrors.addFieldError (FIELD_SMP_ID, "A non-empty SMP ID must be provided!");
    else
      if (!RegExHelper.stringMatchesPattern (PATTERN_SMP_ID, sSMPID))
        aFormErrors.addFieldError (FIELD_SMP_ID,
                                   "The provided SMP ID contains invalid characters. It must match the following regular expression: " +
                                       PATTERN_SMP_ID);

    if (StringHelper.hasNoText (sPhysicalAddress))
      aFormErrors.addFieldError (FIELD_PHYSICAL_ADDRESS, "A physical address must be provided!");
    else
      if (!RegExHelper.stringMatchesPattern (PATTERN_IPV4, sPhysicalAddress))
        aFormErrors.addFieldError (FIELD_PHYSICAL_ADDRESS,
                                   "The provided physical address does not seem to be an IPv4 address!");
      else
      {
        final String [] aParts = StringHelper.getExplodedArray ('.', sPhysicalAddress, 4);
        final byte [] aBytes = new byte [] { (byte) StringParser.parseInt (aParts[0], -1),
                                            (byte) StringParser.parseInt (aParts[1], -1),
                                            (byte) StringParser.parseInt (aParts[2], -1),
                                            (byte) StringParser.parseInt (aParts[3], -1) };
        try
        {
          InetAddress.getByAddress (aBytes);
        }
        catch (final UnknownHostException ex)
        {
          aFormErrors.addFieldError (FIELD_PHYSICAL_ADDRESS,
                                     "The provided IP address does not resolve to a valid host. Technical details: " +
                                         ex.getMessage ());
        }
      }

    if (StringHelper.hasNoText (sLogicalAddress))
      aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                 "A logical address must be provided in the form 'http://smp.example.org'!");
    else
    {
      final URL aURL = URLUtils.getAsURL (sLogicalAddress);
      if (aURL == null)
        aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                   "The provided logical address seems not be a URL! Please use the form 'http://smp.example.org'");
      else
      {
        if (!"http".equals (aURL.getProtocol ()))
          aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                     "The provided logical address must use the 'http' protocol and may not use the '" +
                                         aURL.getProtocol () +
                                         "' protocol. According to the SMP specification, no other protocols than 'http' are allowed!");
        // -1 means default port
        if (aURL.getPort () != 80 && aURL.getPort () != -1)
          aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                     "The provided logical address must use the default http port 80 and not port " +
                                         aURL.getPort () +
                                         ". According to the SMP specification, no other ports are allowed!");
        if (StringHelper.hasText (aURL.getPath ()) && !"/".equals (aURL.getPath ()))
          aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS, "The provided logical address may not contain a path (" +
                                                            aURL.getPath () +
                                                            ") because according to the SMP specifications it must run in the root (/) path!");
      }
    }

    final SSLSocketFactory aSocketFactory = _loadKeyStoreAndCreateSSLSocketFactory (aKeyStoreFile,
                                                                                    sKeyStorePassword,
                                                                                    aFormErrors);

    if (aFormErrors.isEmpty ())
    {
      final SecuredSMPSMLClient aCaller = new SecuredSMPSMLClient (aSML, aSocketFactory);
      try
      {
        aCaller.create (sSMPID, sPhysicalAddress, sLogicalAddress);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Successfully registered SMP '" +
                                                                 sSMPID +
                                                                 "' with physical address '" +
                                                                 sPhysicalAddress +
                                                                 "' and logical address '" +
                                                                 sLogicalAddress +
                                                                 "' to the SML '" +
                                                                 aSML.getManagementServiceURL () +
                                                                 "'."));
        AuditUtils.onAuditExecuteSuccess ("smp-sml-create",
                                          sSMPID,
                                          sPhysicalAddress,
                                          sLogicalAddress,
                                          aSML.getManagementServiceURL ());
      }
      catch (BadRequestFault | InternalErrorFault | UnauthorizedFault | ClientTransportException ex)
      {
        aNodeList.addChild (new BootstrapErrorBox ().addChild ("Error registering SMP '" +
                                                               sSMPID +
                                                               "' with physical address '" +
                                                               sPhysicalAddress +
                                                               "' and logical address '" +
                                                               sLogicalAddress +
                                                               "' to the SML '" +
                                                               aSML.getManagementServiceURL () +
                                                               "'. Technical details: " +
                                                               ex.getMessage ()));
        AuditUtils.onAuditExecuteFailure ("smp-sml-create",
                                          sSMPID,
                                          sPhysicalAddress,
                                          sLogicalAddress,
                                          aSML.getManagementServiceURL (),
                                          ex.getClass (),
                                          ex.getMessage ());
      }
    }
  }

  private void _updateSMPatSML (@Nonnull final WebPageExecutionContext aWPEC, @Nonnull final FormErrors aFormErrors)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final String sSML = aWPEC.getAttributeAsString (FIELD_SML);
    final ISMLInfo aSML = AppUtils.getSMLOfID (sSML);
    final String sSMPID = aWPEC.getAttributeAsString (FIELD_SMP_ID);
    final String sPhysicalAddress = aWPEC.getAttributeAsString (FIELD_PHYSICAL_ADDRESS);
    final String sLogicalAddress = aWPEC.getAttributeAsString (FIELD_LOGICAL_ADDRESS);
    final IFileItem aKeyStoreFile = aWPEC.getFileItem (FIELD_KEYSTORE);
    final String sKeyStorePassword = aWPEC.getAttributeAsString (FIELD_KEYSTORE_PW);

    if (aSML == null)
      aFormErrors.addFieldError (FIELD_SML, "A valid SML must be selected!");

    if (StringHelper.hasNoText (sSMPID))
      aFormErrors.addFieldError (FIELD_SMP_ID, "A non-empty SMP ID must be provided!");
    else
      if (!RegExHelper.stringMatchesPattern (PATTERN_SMP_ID, sSMPID))
        aFormErrors.addFieldError (FIELD_SMP_ID,
                                   "The provided SMP ID contains invalid characters. It must match the following regular expression: " +
                                       PATTERN_SMP_ID);

    if (StringHelper.hasNoText (sPhysicalAddress))
      aFormErrors.addFieldError (FIELD_PHYSICAL_ADDRESS, "A physical address must be provided!");
    else
      if (!RegExHelper.stringMatchesPattern (PATTERN_IPV4, sPhysicalAddress))
        aFormErrors.addFieldError (FIELD_PHYSICAL_ADDRESS,
                                   "The provided physical address does not seem to be an IPv4 address!");
      else
      {
        final String [] aParts = StringHelper.getExplodedArray ('.', sPhysicalAddress, 4);
        final byte [] aBytes = new byte [] { (byte) StringParser.parseInt (aParts[0], -1),
                                            (byte) StringParser.parseInt (aParts[1], -1),
                                            (byte) StringParser.parseInt (aParts[2], -1),
                                            (byte) StringParser.parseInt (aParts[3], -1) };
        try
        {
          InetAddress.getByAddress (aBytes);
        }
        catch (final UnknownHostException ex)
        {
          aFormErrors.addFieldError (FIELD_PHYSICAL_ADDRESS,
                                     "The provided IP address does not resolve to a valid host. Technical details: " +
                                         ex.getMessage ());
        }
      }

    if (StringHelper.hasNoText (sLogicalAddress))
      aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                 "A logical address must be provided in the form 'http://smp.example.org'!");
    else
    {
      final URL aURL = URLUtils.getAsURL (sLogicalAddress);
      if (aURL == null)
        aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                   "The provided logical address seems not be a URL! Please use the form 'http://smp.example.org'");
      else
      {
        if (!"http".equals (aURL.getProtocol ()))
          aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                     "The provided logical address must use the 'http' protocol and may not use the '" +
                                         aURL.getProtocol () +
                                         "' protocol. According to the SMP specification, no other protocols than 'http' are allowed!");
        // -1 means default port
        if (aURL.getPort () != 80 && aURL.getPort () != -1)
          aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                     "The provided logical address must use the default http port 80 and not port " +
                                         aURL.getPort () +
                                         ". According to the SMP specification, no other ports are allowed!");
        if (StringHelper.hasText (aURL.getPath ()) && !"/".equals (aURL.getPath ()))
          aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS, "The provided logical address may not contain a path (" +
                                                            aURL.getPath () +
                                                            ") because according to the SMP specifications it must run in the root (/) path!");
      }
    }

    final SSLSocketFactory aSocketFactory = _loadKeyStoreAndCreateSSLSocketFactory (aKeyStoreFile,
                                                                                    sKeyStorePassword,
                                                                                    aFormErrors);

    if (aFormErrors.isEmpty ())
    {
      final SecuredSMPSMLClient aCaller = new SecuredSMPSMLClient (aSML, aSocketFactory);
      try
      {
        aCaller.update (sSMPID, sPhysicalAddress, sLogicalAddress);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Successfully updated SMP '" +
                                                                 sSMPID +
                                                                 "' with physical address '" +
                                                                 sPhysicalAddress +
                                                                 "' and logical address '" +
                                                                 sLogicalAddress +
                                                                 "' at the SML '" +
                                                                 aSML.getManagementServiceURL () +
                                                                 "'."));
        AuditUtils.onAuditExecuteSuccess ("smp-sml-update",
                                          sSMPID,
                                          sPhysicalAddress,
                                          sLogicalAddress,
                                          aSML.getManagementServiceURL ());
      }
      catch (BadRequestFault | InternalErrorFault | UnauthorizedFault | NotFoundFault ex)
      {
        aNodeList.addChild (new BootstrapErrorBox ().addChild ("Error updating SMP '" +
                                                               sSMPID +
                                                               "' with physical address '" +
                                                               sPhysicalAddress +
                                                               "' and logical address '" +
                                                               sLogicalAddress +
                                                               "' to the SML '" +
                                                               aSML.getManagementServiceURL () +
                                                               "'. Technical details: " +
                                                               ex.getMessage ()));
        AuditUtils.onAuditExecuteFailure ("smp-sml-update",
                                          sSMPID,
                                          sPhysicalAddress,
                                          sLogicalAddress,
                                          aSML.getManagementServiceURL (),
                                          ex.getClass (),
                                          ex.getMessage ());
      }
    }
  }

  private void _deleteSMPfromSML (@Nonnull final WebPageExecutionContext aWPEC, @Nonnull final FormErrors aFormErrors)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final String sSML = aWPEC.getAttributeAsString (FIELD_SML);
    final ISMLInfo aSML = AppUtils.getSMLOfID (sSML);
    final String sSMPID = aWPEC.getAttributeAsString (FIELD_SMP_ID);
    final IFileItem aKeyStoreFile = aWPEC.getFileItem (FIELD_KEYSTORE);
    final String sKeyStorePassword = aWPEC.getAttributeAsString (FIELD_KEYSTORE_PW);

    if (aSML == null)
      aFormErrors.addFieldError (FIELD_SML, "A valid SML must be selected!");

    if (StringHelper.hasNoText (sSMPID))
      aFormErrors.addFieldError (FIELD_SMP_ID, "A non-empty SMP ID must be provided!");
    else
      if (!RegExHelper.stringMatchesPattern (PATTERN_SMP_ID, sSMPID))
        aFormErrors.addFieldError (FIELD_SMP_ID,
                                   "The provided SMP ID contains invalid characters. It must match the following regular expression: " +
                                       PATTERN_SMP_ID);

    final SSLSocketFactory aSocketFactory = _loadKeyStoreAndCreateSSLSocketFactory (aKeyStoreFile,
                                                                                    sKeyStorePassword,
                                                                                    aFormErrors);

    if (aFormErrors.isEmpty ())
    {
      final SecuredSMPSMLClient aCaller = new SecuredSMPSMLClient (aSML, aSocketFactory);
      try
      {
        aCaller.delete (sSMPID);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild ("Successfully deleted SMP '" +
                                                                 sSMPID +
                                                                 "' from the SML '" +
                                                                 aSML.getManagementServiceURL () +
                                                                 "'."));
        AuditUtils.onAuditExecuteSuccess ("smp-sml-delete", sSMPID, aSML.getManagementServiceURL ());
      }
      catch (BadRequestFault | InternalErrorFault | UnauthorizedFault | NotFoundFault ex)
      {
        aNodeList.addChild (new BootstrapErrorBox ().addChild ("Error deleting SMP '" +
                                                               sSMPID +
                                                               "' from the SML '" +
                                                               aSML.getManagementServiceURL () +
                                                               "'. Technical details: " +
                                                               ex.getMessage ()));
        AuditUtils.onAuditExecuteFailure ("smp-sml-delete",
                                          sSMPID,
                                          aSML.getManagementServiceURL (),
                                          ex.getClass (),
                                          ex.getMessage ());
      }
    }
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final FormErrors aFormErrors = new FormErrors ();
    final boolean bShowInput = true;

    if (aWPEC.hasAction (ACTION_PERFORM))
    {
      if (aWPEC.hasSubAction (SUBACTION_SMP_REGISTER))
        _registerSMPtoSML (aWPEC, aFormErrors);
      else
        if (aWPEC.hasSubAction (SUBACTION_SMP_UPDATE))
          _updateSMPatSML (aWPEC, aFormErrors);
        else
          if (aWPEC.hasSubAction (SUBACTION_SMP_DELETE))
            _deleteSMPfromSML (aWPEC, aFormErrors);
    }

    if (bShowInput)
    {
      final BootstrapTabBox aTabBox = new BootstrapTabBox ();

      // Register SMP at SML
      {
        final BootstrapForm aForm = new BootstrapForm (EBootstrapFormType.HORIZONTAL).setAction (aWPEC.getSelfHref ());
        aForm.setEncTypeFileUpload ().setLeft (3);
        aForm.addChild (new BootstrapInfoBox ().addChild ("Register a new SMP to the SML. This must only be done once per SMP!"));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML")
                                                     .setCtrl (new SMLSelect (new RequestField (FIELD_SML, DEFAULT_SML)))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SML)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP ID")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_SMP_ID)).setPlaceholder ("Your SMP ID"),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_SMP_ID))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SMP_ID)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Physical address")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_PHYSICAL_ADDRESS)).setPlaceholder ("The IPv4 address of your SMP. E.g. 1.2.3.4"),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_PHYSICAL_ADDRESS))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PHYSICAL_ADDRESS)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Logical address")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_LOGICAL_ADDRESS)).setPlaceholder ("The domain name of your SMP server. E.g. http://smp.example.org"),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_LOGICAL_ADDRESS))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_LOGICAL_ADDRESS)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP key store")
                                                     .setCtrl (new HCEditFile (FIELD_KEYSTORE),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_KEYSTORE))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("SMP key store password")
                                                     .setCtrl (new HCEditPassword (FIELD_KEYSTORE_PW).setPlaceholder ("The password for the SMP keystore. May be empty."),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_KEYSTORE_PW))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE_PW)));

        final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
        aToolbar.addHiddenField (CHCParam.PARAM_ACTION, ACTION_PERFORM);
        aToolbar.addHiddenField (CHCParam.PARAM_SUBACTION, SUBACTION_SMP_REGISTER);
        aToolbar.addSubmitButton ("Register SMP at SML");

        aTabBox.addTab ("Register SMP to SML", aForm, aWPEC.hasSubAction (SUBACTION_SMP_REGISTER));
      }

      // Update SMP at SML
      {
        final BootstrapForm aForm = new BootstrapForm (EBootstrapFormType.HORIZONTAL).setAction (aWPEC.getSelfHref ());
        aForm.setEncTypeFileUpload ().setLeft (3);
        aForm.addChild (new BootstrapInfoBox ().addChild ("Update an existing SMP at the SML. This must only be done when either the IP address or the host name of the SMP changed!"));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML")
                                                     .setCtrl (new SMLSelect (new RequestField (FIELD_SML, DEFAULT_SML)))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SML)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP ID")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_SMP_ID)).setPlaceholder ("Your SMP ID"),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_SMP_ID))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SMP_ID)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Physical address")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_PHYSICAL_ADDRESS)).setPlaceholder ("The IPv4 address of your SMP. E.g. 1.2.3.4"),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_PHYSICAL_ADDRESS))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PHYSICAL_ADDRESS)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Logical address")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_LOGICAL_ADDRESS)).setPlaceholder ("The domain name of your SMP server. E.g. http://smp.example.org"),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_LOGICAL_ADDRESS))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_LOGICAL_ADDRESS)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP key store")
                                                     .setCtrl (new HCEditFile (FIELD_KEYSTORE),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_KEYSTORE))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("SMP key store password")
                                                     .setCtrl (new HCEditPassword (FIELD_KEYSTORE_PW).setPlaceholder ("The password for the SMP keystore. May be empty."),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_KEYSTORE_PW))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE_PW)));

        final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
        aToolbar.addHiddenField (CHCParam.PARAM_ACTION, ACTION_PERFORM);
        aToolbar.addHiddenField (CHCParam.PARAM_SUBACTION, SUBACTION_SMP_UPDATE);
        aToolbar.addSubmitButton ("Update SMP at SML");

        aTabBox.addTab ("Update SMP at SML", aForm, aWPEC.hasSubAction (SUBACTION_SMP_UPDATE));
      }

      // Delete SMP from SML
      {
        final BootstrapForm aForm = new BootstrapForm (EBootstrapFormType.HORIZONTAL).setAction (aWPEC.getSelfHref ());
        aForm.setEncTypeFileUpload ().setLeft (3);
        aForm.addChild (new BootstrapInfoBox ().addChild ("Delete an existing SMP from the SML."));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML")
                                                     .setCtrl (new SMLSelect (new RequestField (FIELD_SML, DEFAULT_SML)))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SML)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP ID")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_SMP_ID)).setPlaceholder ("Your SMP ID"),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_SMP_ID))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SMP_ID)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP key store")
                                                     .setCtrl (new HCEditFile (FIELD_KEYSTORE),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_KEYSTORE))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("SMP key store password")
                                                     .setCtrl (new HCEditPassword (FIELD_KEYSTORE_PW).setPlaceholder ("The password for the SMP keystore. May be empty."),
                                                               new BootstrapHelpBlock ().addChild (HELPTEXT_KEYSTORE_PW))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE_PW)));

        final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
        aToolbar.addHiddenField (CHCParam.PARAM_ACTION, ACTION_PERFORM);
        aToolbar.addHiddenField (CHCParam.PARAM_SUBACTION, SUBACTION_SMP_DELETE);
        aToolbar.addSubmitButton ("Delete SMP from SML");

        aTabBox.addTab ("Delete SMP from SML", aForm, aWPEC.hasSubAction (SUBACTION_SMP_DELETE));
      }

      aNodeList.addChild (aTabBox);
    }
  }
}
