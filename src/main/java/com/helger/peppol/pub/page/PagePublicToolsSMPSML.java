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

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.datetime.PDTFactory;
import com.helger.commons.datetime.PDTFromString;
import com.helger.commons.datetime.PDTToString;
import com.helger.commons.lang.ClassHelper;
import com.helger.commons.random.RandomHelper;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.url.SimpleURL;
import com.helger.commons.url.URLHelper;
import com.helger.commons.ws.TrustManagerTrustAll;
import com.helger.html.hc.html.forms.HCEdit;
import com.helger.html.hc.html.forms.HCEditFile;
import com.helger.html.hc.html.forms.HCEditPassword;
import com.helger.html.hc.html.grouping.HCDiv;
import com.helger.html.hc.html.textlevel.HCA;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.network.dns.IPV4Addr;
import com.helger.peppol.app.CPPApp;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.sml.ISMLInfo;
import com.helger.peppol.smlclient.BDMSLClient;
import com.helger.peppol.smlclient.ManageServiceMetadataServiceCaller;
import com.helger.peppol.smlclient.smp.BadRequestFault;
import com.helger.peppol.smlclient.smp.InternalErrorFault;
import com.helger.peppol.smlclient.smp.NotFoundFault;
import com.helger.peppol.smlclient.smp.UnauthorizedFault;
import com.helger.peppol.ui.page.AbstractAppWebPage;
import com.helger.peppol.ui.select.SMLSelect;
import com.helger.photon.basic.audit.AuditHelper;
import com.helger.photon.bootstrap3.alert.BootstrapErrorBox;
import com.helger.photon.bootstrap3.alert.BootstrapInfoBox;
import com.helger.photon.bootstrap3.alert.BootstrapSuccessBox;
import com.helger.photon.bootstrap3.alert.BootstrapWarnBox;
import com.helger.photon.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.photon.bootstrap3.form.BootstrapForm;
import com.helger.photon.bootstrap3.form.BootstrapFormGroup;
import com.helger.photon.bootstrap3.nav.BootstrapTabBox;
import com.helger.photon.bootstrap3.pages.BootstrapWebPageUIHandler;
import com.helger.photon.bootstrap3.uictrls.datetimepicker.BootstrapDateTimePicker;
import com.helger.photon.core.form.FormErrorList;
import com.helger.photon.core.form.RequestField;
import com.helger.photon.core.form.RequestFieldDate;
import com.helger.photon.uicore.css.CPageParam;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uictrls.autosize.HCTextAreaAutosize;
import com.helger.security.certificate.CertificateHelper;
import com.helger.security.keystore.EKeyStoreType;
import com.helger.security.keystore.IKeyStoreType;
import com.helger.web.fileupload.IFileItem;
import com.sun.xml.ws.client.ClientTransportException;

public class PagePublicToolsSMPSML extends AbstractAppWebPage
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PagePublicToolsSMPSML.class);

  private static final String FIELD_SML = "sml";
  private static final String FIELD_SMP_ID = "smpid";
  private static final String FIELD_PHYSICAL_ADDRESS = "physicaladdr";
  private static final String FIELD_LOGICAL_ADDRESS = "logicaladdr";
  private static final String FIELD_KEYSTORE = "keystore";
  private static final String FIELD_KEYSTORE_PW = "keystorepw";
  private static final String FIELD_PM_MIGRATION_DATE = "pmmigdate";
  private static final String FIELD_PM_PUBLIC_KEY = "pmpubkey";

  private static final String HELPTEXT_SMP_ID = "This is the unique ID your SMP will have inside the SML. All continuing operations must use this ID. You can choose this ID yourself but please make sure it only contains characters, numbers and the hyphen character. All uppercase names are appreciated!";
  private static final String HELPTEXT_PHYSICAL_ADDRESS = "This must be the IPv4 address of your SMP. IPv6 addresses are not yet supported!";
  private static final String HELPTEXT_LOGICAL_ADDRESS = "This must be the fully qualified domain name of your SMP. This can be either a domain name like 'http://smp.example.org' or a IP address like 'http://1.1.1.1'!";
  private static final String HELPTEXT_KEYSTORE = "A Java key store of type JKS with only your PEPPOL SMP key is required to perform the action! Remember to use the production keystore when accessing the SML and the pilot keystore when accessing the SMK! The uploaded key store is used for nothing else than for this selected action and will be discarded afterwards!";
  private static final String HELPTEXT_KEYSTORE_PW = "The password of the JKS key store is required to access the content of the key store! The password is neither logged nor stored anywhere and discarded after opening the keystore.";

  private static final String SUBACTION_SMP_REGISTER = "smpregister";
  private static final String SUBACTION_SMP_UPDATE = "smpupdate";
  private static final String SUBACTION_SMP_DELETE = "smpdelete";
  private static final String SUBACTION_SMP_UPDATE_CERT = "smpupdatecert";

  private static final ISMLInfo DEFAULT_SML = ESML.DIGIT_PRODUCTION;
  private static final String SECURITY_PROVIDER = null;

  public PagePublicToolsSMPSML (@Nonnull @Nonempty final String sID)
  {
    super (sID, "SMP - SML tools");
  }

  @Nonnull
  @Nonempty
  private static String _getTechnicalDetails (@Nonnull final Throwable t)
  {
    return " Technical details: " + ClassHelper.getClassLocalName (t) + " " + StringHelper.getNotNull (t.getMessage ());
  }

  @Nullable
  private static SSLSocketFactory _loadKeyStoreAndCreateSSLSocketFactory (@Nonnull final IKeyStoreType aKeyStoreType,
                                                                          @Nullable final String sSecurityProvider,
                                                                          @Nullable final IFileItem aKeyStoreFile,
                                                                          @Nullable final String sKeyStorePassword,
                                                                          @Nonnull final FormErrorList aFormErrors,
                                                                          @Nonnull final Locale aDisplayLocale)
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
        try (final InputStream aIS = aKeyStoreFile.getInputStream ())
        {
          aKeyStore = StringHelper.hasText (sSecurityProvider) ? aKeyStoreType.getKeyStore (sSecurityProvider)
                                                               : aKeyStoreType.getKeyStore ();
          aKeyStore.load (aIS, sKeyStorePassword.toCharArray ());

          // Get all aliases
          final ICommonsList <String> aAllAliases = CollectionHelper.newList (aKeyStore.aliases ());
          s_aLogger.info ("Successfully loaded key store of type " +
                          aKeyStoreType.getID () +
                          " containing " +
                          aAllAliases.size () +
                          " aliases");

          // Check key and certificate count
          final LocalDate aNow = PDTFactory.getCurrentLocalDate ();
          int nKeyCount = 0;
          int nCertificateCount = 0;
          int nInvalidKeyCount = 0;
          for (final String sAlias : aAllAliases)
          {
            final boolean bIsKeyEntry = aKeyStore.isKeyEntry (sAlias);
            final boolean bIsCertificateEntry = aKeyStore.isCertificateEntry (sAlias);
            if (bIsKeyEntry)
              ++nKeyCount;
            if (bIsCertificateEntry)
              ++nCertificateCount;
            s_aLogger.info ("  Alias '" +
                            sAlias +
                            "'" +
                            (bIsKeyEntry ? " [key entry]" : "") +
                            (bIsCertificateEntry ? " [certificate]" : ""));

            if (bIsKeyEntry)
              try
              {
                // Read key and check for validity
                final KeyStore.ProtectionParameter aProtection = new KeyStore.PasswordProtection (sKeyStorePassword.toCharArray ());
                final KeyStore.Entry aEntry = aKeyStore.getEntry (sAlias, aProtection);
                if (aEntry instanceof KeyStore.PrivateKeyEntry)
                {
                  final Certificate aCert = ((KeyStore.PrivateKeyEntry) aEntry).getCertificate ();
                  if (aCert instanceof X509Certificate)
                  {
                    final X509Certificate aX509Cert = (X509Certificate) aCert;
                    final LocalDate aNotBefore = PDTFactory.createLocalDate (aX509Cert.getNotBefore ());
                    final LocalDate aNotAfter = PDTFactory.createLocalDate (aX509Cert.getNotBefore ());
                    if (aNow.isBefore (aNotBefore))
                    {
                      final String sMsg = "The key '" +
                                          sAlias +
                                          "' in the keystore is not valid before " +
                                          PDTToString.getAsString (aNotBefore, aDisplayLocale) +
                                          "!";
                      s_aLogger.error (sMsg);
                      aFormErrors.addFieldError (FIELD_KEYSTORE, sMsg);
                      nInvalidKeyCount++;
                    }
                    if (aNow.isAfter (aNotAfter))
                    {
                      final String sMsg = "The key '" +
                                          sAlias +
                                          "' in the keystore is not valid after " +
                                          PDTToString.getAsString (aNotAfter, aDisplayLocale) +
                                          "!";
                      s_aLogger.error (sMsg);
                      aFormErrors.addFieldError (FIELD_KEYSTORE, sMsg);
                      nInvalidKeyCount++;
                    }
                  }
                }
              }
              catch (final Exception ex)
              {
                // Ignore
              }
          }

          if (nInvalidKeyCount > 0)
          {
            // Error messages are already displayed
            aKeyStore = null;
          }
          else
            if (nKeyCount != 1)
            {
              final String sMsg = "The keystore must contain exactly one key entry but " +
                                  nKeyCount +
                                  " key entries and " +
                                  nCertificateCount +
                                  " certificate entries were found!";
              s_aLogger.error (sMsg);
              aFormErrors.addFieldError (FIELD_KEYSTORE, sMsg);
              aKeyStore = null;
            }
        }
        catch (final GeneralSecurityException | IOException ex)
        {
          final String sMsg = "The key store could not be loaded with the provided password.";
          s_aLogger.error (sMsg, ex);
          aFormErrors.addFieldError (FIELD_KEYSTORE_PW, sMsg + _getTechnicalDetails (ex));
          aKeyStore = null;
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
                          new TrustManager [] { new TrustManagerTrustAll (false) },
                          RandomHelper.getSecureRandom ());
        aSocketFactory = aSSLContext.getSocketFactory ();
        s_aLogger.info ("Successfully created TLS socket factory with the provided keystore password!");
      }
      catch (final GeneralSecurityException ex)
      {
        final String sMsg = "Failed to use the provided key store for TLS connection.";
        s_aLogger.error (sMsg, ex);
        aFormErrors.addFieldError (FIELD_KEYSTORE, sMsg + _getTechnicalDetails (ex));
      }
    }
    return aSocketFactory;
  }

  @Nonnull
  private static ManageServiceMetadataServiceCaller _create (@Nonnull final ISMLInfo aSML,
                                                             @Nonnull final SSLSocketFactory aSocketFactory)
  {
    final ManageServiceMetadataServiceCaller ret = new ManageServiceMetadataServiceCaller (aSML);
    ret.setSSLSocketFactory (aSocketFactory);
    return ret;
  }

  private static void _registerSMPtoSML (@Nonnull final WebPageExecutionContext aWPEC,
                                         @Nonnull final FormErrorList aFormErrors)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final String sSML = aWPEC.params ().getAsString (FIELD_SML);
    final ISMLInfo aSML = ESML.getFromIDOrNull (sSML);
    final String sSMPID = aWPEC.params ().getAsString (FIELD_SMP_ID);
    final String sPhysicalAddress = aWPEC.params ().getAsString (FIELD_PHYSICAL_ADDRESS);
    final String sLogicalAddress = aWPEC.params ().getAsString (FIELD_LOGICAL_ADDRESS);
    final IFileItem aKeyStoreFile = aWPEC.params ().getAsFileItem (FIELD_KEYSTORE);
    final String sKeyStorePassword = aWPEC.params ().getAsString (FIELD_KEYSTORE_PW);

    if (aSML == null)
      aFormErrors.addFieldError (FIELD_SML, "A valid SML must be selected!");

    if (StringHelper.hasNoText (sSMPID))
      aFormErrors.addFieldError (FIELD_SMP_ID, "A non-empty SMP ID must be provided!");
    else
      if (!RegExHelper.stringMatchesPattern (CPPApp.PATTERN_SMP_ID, sSMPID))
        aFormErrors.addFieldError (FIELD_SMP_ID,
                                   "The provided SMP ID contains invalid characters. It must match the following regular expression: " +
                                                 CPPApp.PATTERN_SMP_ID);

    if (StringHelper.hasNoText (sPhysicalAddress))
      aFormErrors.addFieldError (FIELD_PHYSICAL_ADDRESS, "A physical address must be provided!");
    else
      if (!RegExHelper.stringMatchesPattern (IPV4Addr.PATTERN_IPV4, sPhysicalAddress))
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
                                     "The provided IP address does not resolve to a valid host." +
                                                             _getTechnicalDetails (ex));
        }
      }

    if (StringHelper.hasNoText (sLogicalAddress))
      aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                 "A logical address must be provided in the form 'http://smp.example.org'!");
    else
    {
      final URL aURL = URLHelper.getAsURL (sLogicalAddress);
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
          aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                     "The provided logical address may not contain a path (" +
                                                            aURL.getPath () +
                                                            ") because according to the SMP specifications it must run in the root (/) path!");
      }
    }

    final SSLSocketFactory aSocketFactory = _loadKeyStoreAndCreateSSLSocketFactory (EKeyStoreType.JKS,
                                                                                    SECURITY_PROVIDER,
                                                                                    aKeyStoreFile,
                                                                                    sKeyStorePassword,
                                                                                    aFormErrors,
                                                                                    aDisplayLocale);

    if (aFormErrors.isEmpty ())
    {
      final ManageServiceMetadataServiceCaller aCaller = _create (aSML, aSocketFactory);
      try
      {
        aCaller.create (sSMPID, sPhysicalAddress, sLogicalAddress);

        final String sMsg = "Successfully registered SMP '" +
                            sSMPID +
                            "' with physical address '" +
                            sPhysicalAddress +
                            "' and logical address '" +
                            sLogicalAddress +
                            "' to the SML '" +
                            aSML.getManagementServiceURL () +
                            "'.";
        s_aLogger.info (sMsg);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild (sMsg));
        AuditHelper.onAuditExecuteSuccess ("smp-sml-create",
                                           sSMPID,
                                           sPhysicalAddress,
                                           sLogicalAddress,
                                           aSML.getManagementServiceURL ());
      }
      catch (final BadRequestFault | InternalErrorFault | UnauthorizedFault | ClientTransportException ex)
      {
        final String sMsg = "Error registering SMP '" +
                            sSMPID +
                            "' with physical address '" +
                            sPhysicalAddress +
                            "' and logical address '" +
                            sLogicalAddress +
                            "' to the SML '" +
                            aSML.getManagementServiceURL () +
                            "'.";
        s_aLogger.error (sMsg, ex);
        aNodeList.addChild (new BootstrapErrorBox ().addChild (sMsg + _getTechnicalDetails (ex)));
        AuditHelper.onAuditExecuteFailure ("smp-sml-create",
                                           sSMPID,
                                           sPhysicalAddress,
                                           sLogicalAddress,
                                           aSML.getManagementServiceURL (),
                                           ex.getClass (),
                                           ex.getMessage ());
      }
    }
    else
      aNodeList.addChild (BootstrapWebPageUIHandler.INSTANCE.createIncorrectInputBox (aWPEC));
  }

  private static void _updateSMPatSML (@Nonnull final WebPageExecutionContext aWPEC,
                                       @Nonnull final FormErrorList aFormErrors)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final String sSML = aWPEC.params ().getAsString (FIELD_SML);
    final ISMLInfo aSML = ESML.getFromIDOrNull (sSML);
    final String sSMPID = aWPEC.params ().getAsString (FIELD_SMP_ID);
    final String sPhysicalAddress = aWPEC.params ().getAsString (FIELD_PHYSICAL_ADDRESS);
    final String sLogicalAddress = aWPEC.params ().getAsString (FIELD_LOGICAL_ADDRESS);
    final IFileItem aKeyStoreFile = aWPEC.params ().getAsFileItem (FIELD_KEYSTORE);
    final String sKeyStorePassword = aWPEC.params ().getAsString (FIELD_KEYSTORE_PW);

    if (aSML == null)
      aFormErrors.addFieldError (FIELD_SML, "A valid SML must be selected!");

    if (StringHelper.hasNoText (sSMPID))
      aFormErrors.addFieldError (FIELD_SMP_ID, "A non-empty SMP ID must be provided!");
    else
      if (!RegExHelper.stringMatchesPattern (CPPApp.PATTERN_SMP_ID, sSMPID))
        aFormErrors.addFieldError (FIELD_SMP_ID,
                                   "The provided SMP ID contains invalid characters. It must match the following regular expression: " +
                                                 CPPApp.PATTERN_SMP_ID);

    if (StringHelper.hasNoText (sPhysicalAddress))
      aFormErrors.addFieldError (FIELD_PHYSICAL_ADDRESS, "A physical address must be provided!");
    else
      if (!RegExHelper.stringMatchesPattern (IPV4Addr.PATTERN_IPV4, sPhysicalAddress))
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
                                     "The provided IP address does not resolve to a valid host." +
                                                             _getTechnicalDetails (ex));
        }
      }

    if (StringHelper.hasNoText (sLogicalAddress))
      aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                 "A logical address must be provided in the form 'http://smp.example.org'!");
    else
    {
      final URL aURL = URLHelper.getAsURL (sLogicalAddress);
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
          aFormErrors.addFieldError (FIELD_LOGICAL_ADDRESS,
                                     "The provided logical address may not contain a path (" +
                                                            aURL.getPath () +
                                                            ") because according to the SMP specifications it must run in the root (/) path!");
      }
    }

    final SSLSocketFactory aSocketFactory = _loadKeyStoreAndCreateSSLSocketFactory (EKeyStoreType.JKS,
                                                                                    SECURITY_PROVIDER,
                                                                                    aKeyStoreFile,
                                                                                    sKeyStorePassword,
                                                                                    aFormErrors,
                                                                                    aDisplayLocale);

    if (aFormErrors.isEmpty ())
    {
      final ManageServiceMetadataServiceCaller aCaller = _create (aSML, aSocketFactory);
      try
      {
        aCaller.update (sSMPID, sPhysicalAddress, sLogicalAddress);

        final String sMsg = "Successfully updated SMP '" +
                            sSMPID +
                            "' with physical address '" +
                            sPhysicalAddress +
                            "' and logical address '" +
                            sLogicalAddress +
                            "' at the SML '" +
                            aSML.getManagementServiceURL () +
                            "'.";
        s_aLogger.info (sMsg);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild (sMsg));
        AuditHelper.onAuditExecuteSuccess ("smp-sml-update",
                                           sSMPID,
                                           sPhysicalAddress,
                                           sLogicalAddress,
                                           aSML.getManagementServiceURL ());
      }
      catch (final BadRequestFault
                   | InternalErrorFault
                   | UnauthorizedFault
                   | NotFoundFault
                   | ClientTransportException ex)
      {
        final String sMsg = "Error updating SMP '" +
                            sSMPID +
                            "' with physical address '" +
                            sPhysicalAddress +
                            "' and logical address '" +
                            sLogicalAddress +
                            "' to the SML '" +
                            aSML.getManagementServiceURL () +
                            "'.";
        s_aLogger.error (sMsg, ex);
        aNodeList.addChild (new BootstrapErrorBox ().addChild (sMsg + _getTechnicalDetails (ex)));
        AuditHelper.onAuditExecuteFailure ("smp-sml-update",
                                           sSMPID,
                                           sPhysicalAddress,
                                           sLogicalAddress,
                                           aSML.getManagementServiceURL (),
                                           ex.getClass (),
                                           ex.getMessage ());
      }
    }
    else
      aNodeList.addChild (BootstrapWebPageUIHandler.INSTANCE.createIncorrectInputBox (aWPEC));
  }

  private static void _deleteSMPfromSML (@Nonnull final WebPageExecutionContext aWPEC,
                                         @Nonnull final FormErrorList aFormErrors)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final String sSML = aWPEC.params ().getAsString (FIELD_SML);
    final ISMLInfo aSML = ESML.getFromIDOrNull (sSML);
    final String sSMPID = aWPEC.params ().getAsString (FIELD_SMP_ID);
    final IFileItem aKeyStoreFile = aWPEC.params ().getAsFileItem (FIELD_KEYSTORE);
    final String sKeyStorePassword = aWPEC.params ().getAsString (FIELD_KEYSTORE_PW);

    if (aSML == null)
      aFormErrors.addFieldError (FIELD_SML, "A valid SML must be selected!");

    if (StringHelper.hasNoText (sSMPID))
      aFormErrors.addFieldError (FIELD_SMP_ID, "A non-empty SMP ID must be provided!");
    else
      if (!RegExHelper.stringMatchesPattern (CPPApp.PATTERN_SMP_ID, sSMPID))
        aFormErrors.addFieldError (FIELD_SMP_ID,
                                   "The provided SMP ID contains invalid characters. It must match the following regular expression: " +
                                                 CPPApp.PATTERN_SMP_ID);

    final SSLSocketFactory aSocketFactory = _loadKeyStoreAndCreateSSLSocketFactory (EKeyStoreType.JKS,
                                                                                    SECURITY_PROVIDER,
                                                                                    aKeyStoreFile,
                                                                                    sKeyStorePassword,
                                                                                    aFormErrors,
                                                                                    aDisplayLocale);

    if (aFormErrors.isEmpty ())
    {
      final ManageServiceMetadataServiceCaller aCaller = _create (aSML, aSocketFactory);
      try
      {
        aCaller.delete (sSMPID);

        final String sMsg = "Successfully deleted SMP '" +
                            sSMPID +
                            "' from the SML '" +
                            aSML.getManagementServiceURL () +
                            "'.";
        s_aLogger.info (sMsg);
        aNodeList.addChild (new BootstrapSuccessBox ().addChild (sMsg));
        AuditHelper.onAuditExecuteSuccess ("smp-sml-delete", sSMPID, aSML.getManagementServiceURL ());
      }
      catch (final BadRequestFault
                   | InternalErrorFault
                   | UnauthorizedFault
                   | NotFoundFault
                   | ClientTransportException ex)
      {
        final String sMsg = "Error deleting SMP '" +
                            sSMPID +
                            "' from the SML '" +
                            aSML.getManagementServiceURL () +
                            "'.";
        s_aLogger.error (sMsg, ex);
        aNodeList.addChild (new BootstrapErrorBox ().addChild (sMsg + _getTechnicalDetails (ex)));
        AuditHelper.onAuditExecuteFailure ("smp-sml-delete",
                                           sSMPID,
                                           aSML.getManagementServiceURL (),
                                           ex.getClass (),
                                           ex.getMessage ());
      }
    }
    else
      aNodeList.addChild (BootstrapWebPageUIHandler.INSTANCE.createIncorrectInputBox (aWPEC));
  }

  private static void _updateSMPCertAtSML (@Nonnull final WebPageExecutionContext aWPEC,
                                           @Nonnull final FormErrorList aFormErrors)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final LocalDate aNow = PDTFactory.getCurrentLocalDate ();
    final String sSML = aWPEC.params ().getAsString (FIELD_SML);
    final ISMLInfo aSML = ESML.getFromIDOrNull (sSML);
    final IFileItem aKeyStoreFile = aWPEC.params ().getAsFileItem (FIELD_KEYSTORE);
    final String sKeyStorePassword = aWPEC.params ().getAsString (FIELD_KEYSTORE_PW);
    final String sMigrationDate = aWPEC.params ().getAsString (FIELD_PM_MIGRATION_DATE);
    final LocalDate aMigrationDate = PDTFromString.getLocalDateFromString (sMigrationDate, aDisplayLocale);
    final String sMigrationPublicKey = aWPEC.params ().getAsString (FIELD_PM_PUBLIC_KEY);
    X509Certificate aMigrationPublicKey = null;

    if (aSML == null)
      aFormErrors.addFieldError (FIELD_SML, "A valid SML must be selected!");

    if (StringHelper.hasText (sMigrationDate))
    {
      if (aMigrationDate == null)
        aFormErrors.addFieldError (FIELD_PM_MIGRATION_DATE,
                                   "The provided certificate migration date '" + sMigrationDate + "' is invalid!");
      else
        if (aMigrationDate.compareTo (aNow) <= 0)
          aFormErrors.addFieldError (FIELD_PM_MIGRATION_DATE, "The certificate migration date must be in the future!");
    }

    if (StringHelper.hasNoText (sMigrationPublicKey))
    {
      aFormErrors.addFieldError (FIELD_PM_PUBLIC_KEY, "A new public key must be provided.");
    }
    else
    {
      try
      {
        aMigrationPublicKey = CertificateHelper.convertStringToCertficate (sMigrationPublicKey);
      }
      catch (final CertificateException ex)
      {
        // Fall through
      }

      if (aMigrationPublicKey == null)
        aFormErrors.addFieldError (FIELD_PM_PUBLIC_KEY,
                                   "The provided public key cannot be parsed as a X.509 certificate.");
      else
      {
        try
        {
          aMigrationPublicKey.checkValidity ();
        }
        catch (final CertificateExpiredException ex)
        {
          aFormErrors.addFieldError (FIELD_PM_PUBLIC_KEY, "The provided public key is already expired!");
          aMigrationPublicKey = null;
        }
        catch (final CertificateNotYetValidException ex)
        {
          // That's okay
        }
      }
    }

    if (aMigrationPublicKey != null)
    {
      final LocalDate aNotBefore = PDTFactory.createLocalDate (aMigrationPublicKey.getNotBefore ());
      final LocalDate aNotAfter = PDTFactory.createLocalDate (aMigrationPublicKey.getNotAfter ());

      if (aMigrationDate != null)
      {
        if (aMigrationDate.isBefore (aNotBefore))
          aFormErrors.addFieldError (FIELD_PM_MIGRATION_DATE,
                                     "The provided certificate migration date " +
                                                              PDTToString.getAsString (aMigrationDate, aDisplayLocale) +
                                                              " must not be before the certificate NotBefore date " +
                                                              PDTToString.getAsString (aNotBefore, aDisplayLocale) +
                                                              "!");

        if (aMigrationDate.isAfter (aNotAfter))
          aFormErrors.addFieldError (FIELD_PM_MIGRATION_DATE,
                                     "The provided certificate migration date " +
                                                              PDTToString.getAsString (aMigrationDate, aDisplayLocale) +
                                                              " must not be after the certificate NotAfter date " +
                                                              PDTToString.getAsString (aNotAfter, aDisplayLocale) +
                                                              "!");
      }
      else
      {
        if (aNotBefore.compareTo (aNow) <= 0)
          aFormErrors.addFieldError (FIELD_PM_PUBLIC_KEY,
                                     "The effective certificate migration date (" +
                                                          PDTToString.getAsString (aNotBefore, aDisplayLocale) +
                                                          " - taken from the new public key) must be in the future!");
      }
    }

    final SSLSocketFactory aSocketFactory = _loadKeyStoreAndCreateSSLSocketFactory (EKeyStoreType.JKS,
                                                                                    SECURITY_PROVIDER,
                                                                                    aKeyStoreFile,
                                                                                    sKeyStorePassword,
                                                                                    aFormErrors,
                                                                                    aDisplayLocale);

    if (aFormErrors.isEmpty ())
    {
      final BDMSLClient aCaller = new BDMSLClient (aSML);
      aCaller.setSSLSocketFactory (aSocketFactory);

      try
      {
        if (false)
          aCaller.prepareChangeCertificate (sMigrationPublicKey, aMigrationDate);

        final LocalDate aNotBefore = PDTFactory.createLocalDate (aMigrationPublicKey.getNotBefore ());
        final LocalDate aNotAfter = PDTFactory.createLocalDate (aMigrationPublicKey.getNotAfter ());

        final LocalDate aEffectiveMigrationDate = aMigrationDate != null ? aMigrationDate : aNotBefore;
        final String sMsg = "Successfully prepared migration of SMP certificate at SML '" +
                            aSML.getManagementServiceURL () +
                            "'" +
                            " to be exchanged at " +
                            PDTToString.getAsString (aEffectiveMigrationDate, aDisplayLocale) +
                            ".";
        s_aLogger.info (sMsg);

        final BootstrapSuccessBox aBox = new BootstrapSuccessBox ();
        aBox.addChild (new HCDiv ().addChild (sMsg));
        aBox.addChild (new HCDiv ().addChild ("Issuer: " + aMigrationPublicKey.getIssuerDN ().toString ()));
        aBox.addChild (new HCDiv ().addChild ("Subject: " + aMigrationPublicKey.getSubjectDN ().toString ()));
        aBox.addChild (new HCDiv ().addChild ("Not before: " + PDTToString.getAsString (aNotBefore, aDisplayLocale)));
        aBox.addChild (new HCDiv ().addChild ("Not after: " + PDTToString.getAsString (aNotAfter, aDisplayLocale)));
        aNodeList.addChild (aBox);

        AuditHelper.onAuditExecuteSuccess ("smp-sml-update-cert",
                                           aSML.getManagementServiceURL (),
                                           sMigrationPublicKey,
                                           aMigrationDate);
      }
      catch (final com.helger.peppol.smlclient.bdmsl.BadRequestFault
                   | com.helger.peppol.smlclient.bdmsl.InternalErrorFault
                   | com.helger.peppol.smlclient.bdmsl.NotFoundFault
                   | com.helger.peppol.smlclient.bdmsl.UnauthorizedFault
                   | ClientTransportException ex)
      {
        final String sMsg = "Error preparing migration of SMP certificate at SML '" +
                            aSML.getManagementServiceURL () +
                            "'.";
        s_aLogger.error (sMsg, ex);
        aNodeList.addChild (new BootstrapErrorBox ().addChild (sMsg + _getTechnicalDetails (ex)));
        AuditHelper.onAuditExecuteFailure ("smp-sml-update-cert",
                                           aSML.getManagementServiceURL (),
                                           sMigrationPublicKey,
                                           aMigrationDate,
                                           ex.getClass (),
                                           ex.getMessage ());
      }
    }
    else
      aNodeList.addChild (BootstrapWebPageUIHandler.INSTANCE.createIncorrectInputBox (aWPEC));
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final FormErrorList aFormErrors = new FormErrorList ();
    final boolean bShowInput = true;

    aNodeList.addChild (new BootstrapWarnBox ().addChild ("Note: as of ")
                                               .addChild (new HCA (new SimpleURL ("https://github.com/phax/peppol-smp-server")).addChild ("phoss SMP server 5.0.0 beta 1"))
                                               .addChild (" this registration feature is contained directly in the SMP software! So you don't need to send your certificates over the Internet anymore!"));

    if (aWPEC.hasAction (CPageParam.ACTION_PERFORM))
    {
      if (aWPEC.hasSubAction (SUBACTION_SMP_REGISTER))
        _registerSMPtoSML (aWPEC, aFormErrors);
      else
        if (aWPEC.hasSubAction (SUBACTION_SMP_UPDATE))
          _updateSMPatSML (aWPEC, aFormErrors);
        else
          if (aWPEC.hasSubAction (SUBACTION_SMP_DELETE))
            _deleteSMPfromSML (aWPEC, aFormErrors);
          else
            if (aWPEC.hasSubAction (SUBACTION_SMP_UPDATE_CERT))
              _updateSMPCertAtSML (aWPEC, aFormErrors);
    }

    if (bShowInput)
    {
      final int nLeft = 3;
      final BootstrapTabBox aTabBox = new BootstrapTabBox ();

      // Register SMP at SML
      {
        final BootstrapForm aForm = getUIHandler ().createFormSelf (aWPEC);
        aForm.setEncTypeFileUpload ().setLeft (nLeft);
        aForm.addChild (new BootstrapInfoBox ().addChild ("Register a new SMP to the SML. This must only be done once per SMP!"));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML")
                                                     .setCtrl (new SMLSelect (new RequestField (FIELD_SML,
                                                                                                DEFAULT_SML)))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SML)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP ID")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_SMP_ID)).setPlaceholder ("Your SMP ID"))
                                                     .setHelpText (HELPTEXT_SMP_ID)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SMP_ID)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Physical address")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_PHYSICAL_ADDRESS)).setPlaceholder ("The IPv4 address of your SMP. E.g. 1.2.3.4"))
                                                     .setHelpText (HELPTEXT_PHYSICAL_ADDRESS)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PHYSICAL_ADDRESS)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Logical address")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_LOGICAL_ADDRESS)).setPlaceholder ("The domain name of your SMP server. E.g. http://smp.example.org"))
                                                     .setHelpText (HELPTEXT_LOGICAL_ADDRESS)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_LOGICAL_ADDRESS)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP key store")
                                                     .setCtrl (new HCEditFile (FIELD_KEYSTORE))
                                                     .setHelpText (HELPTEXT_KEYSTORE)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("SMP key store password")
                                                     .setCtrl (new HCEditPassword (FIELD_KEYSTORE_PW).setPlaceholder ("The password for the SMP keystore. May be empty."))
                                                     .setHelpText (HELPTEXT_KEYSTORE_PW)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE_PW)));

        final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
        aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
        aToolbar.addHiddenField (CPageParam.PARAM_SUBACTION, SUBACTION_SMP_REGISTER);
        aToolbar.addSubmitButton ("Register SMP at SML");

        aTabBox.addTab ("register", "Register SMP to SML", aForm, aWPEC.hasSubAction (SUBACTION_SMP_REGISTER));
      }

      // Update SMP at SML
      {
        final BootstrapForm aForm = getUIHandler ().createFormSelf (aWPEC);
        aForm.setEncTypeFileUpload ().setLeft (nLeft);
        aForm.addChild (new BootstrapInfoBox ().addChild ("Update an existing SMP at the SML. This must only be done when either the IP address or the host name of the SMP changed!"));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML")
                                                     .setCtrl (new SMLSelect (new RequestField (FIELD_SML,
                                                                                                DEFAULT_SML)))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SML)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP ID")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_SMP_ID)).setPlaceholder ("Your SMP ID"))
                                                     .setHelpText (HELPTEXT_SMP_ID)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SMP_ID)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Physical address")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_PHYSICAL_ADDRESS)).setPlaceholder ("The IPv4 address of your SMP. E.g. 1.2.3.4"))
                                                     .setHelpText (HELPTEXT_PHYSICAL_ADDRESS)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PHYSICAL_ADDRESS)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Logical address")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_LOGICAL_ADDRESS)).setPlaceholder ("The domain name of your SMP server. E.g. http://smp.example.org"))
                                                     .setHelpText (HELPTEXT_LOGICAL_ADDRESS)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_LOGICAL_ADDRESS)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP key store")
                                                     .setCtrl (new HCEditFile (FIELD_KEYSTORE))
                                                     .setHelpText (HELPTEXT_KEYSTORE)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("SMP key store password")
                                                     .setCtrl (new HCEditPassword (FIELD_KEYSTORE_PW).setPlaceholder ("The password for the SMP keystore. May be empty."))
                                                     .setHelpText (HELPTEXT_KEYSTORE_PW)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE_PW)));

        final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
        aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
        aToolbar.addHiddenField (CPageParam.PARAM_SUBACTION, SUBACTION_SMP_UPDATE);
        aToolbar.addSubmitButton ("Update SMP at SML");

        aTabBox.addTab ("update", "Update SMP at SML", aForm, aWPEC.hasSubAction (SUBACTION_SMP_UPDATE));
      }

      // Delete SMP from SML
      {
        final BootstrapForm aForm = getUIHandler ().createFormFileUploadSelf (aWPEC);
        aForm.setLeft (nLeft);
        aForm.addChild (new BootstrapInfoBox ().addChild ("Delete an existing SMP from the SML."));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML")
                                                     .setCtrl (new SMLSelect (new RequestField (FIELD_SML,
                                                                                                DEFAULT_SML)))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SML)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP ID")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_SMP_ID)).setPlaceholder ("Your SMP ID"))
                                                     .setHelpText (HELPTEXT_SMP_ID)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SMP_ID)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SMP key store")
                                                     .setCtrl (new HCEditFile (FIELD_KEYSTORE))
                                                     .setHelpText (HELPTEXT_KEYSTORE)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("SMP key store password")
                                                     .setCtrl (new HCEditPassword (FIELD_KEYSTORE_PW).setPlaceholder ("The password for the SMP keystore. May be empty."))
                                                     .setHelpText (HELPTEXT_KEYSTORE_PW)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE_PW)));

        final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
        aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
        aToolbar.addHiddenField (CPageParam.PARAM_SUBACTION, SUBACTION_SMP_DELETE);
        aToolbar.addSubmitButton ("Delete SMP from SML");

        aTabBox.addTab ("delete", "Delete SMP from SML", aForm, aWPEC.hasSubAction (SUBACTION_SMP_DELETE));
      }

      // Update SMP certificate in SML
      {
        final BootstrapForm aForm = getUIHandler ().createFormFileUploadSelf (aWPEC);
        aForm.setLeft (nLeft);
        aForm.addChild (new BootstrapInfoBox ().addChildren (new HCDiv ().addChild ("Prepare the update of your PEPPOL SMP certificate in the future."),
                                                             new HCDiv ().addChild ("This works only if your SMP certificate is NOT expired yet." +
                                                                                    " If your SMP certificate is already expired contact CEF-EDELIVERY-SUPPORT@ec.europa.eu with your SMP ID, the new certificate and the requested exchange date!"),
                                                             new HCDiv ().addChild ("Note: this is a custom extension that only works with the CEF SML instances!")));
        aForm.addChild (new BootstrapWarnBox ().addChild ("It is your responsibility to update the PEPPOL certificate in your SMP at the specified time!"));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("SML")
                                                     .setCtrl (new SMLSelect (new RequestField (FIELD_SML,
                                                                                                DEFAULT_SML)))
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_SML)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("Existing SMP key store")
                                                     .setCtrl (new HCEditFile (FIELD_KEYSTORE))
                                                     .setHelpText (HELPTEXT_KEYSTORE)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Existing SMP key store password")
                                                     .setCtrl (new HCEditPassword (FIELD_KEYSTORE_PW).setPlaceholder ("The password for the existing SMP keystore. May be empty."))
                                                     .setHelpText (HELPTEXT_KEYSTORE_PW)
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_KEYSTORE_PW)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabel ("Certificate migration date")
                                                     .setCtrl (new BootstrapDateTimePicker (new RequestFieldDate (FIELD_PM_MIGRATION_DATE,
                                                                                                                  aDisplayLocale)).setStartDate (PDTFactory.getCurrentLocalDate ()
                                                                                                                                                           .plusDays (1)))
                                                     .setHelpText ("The SML will replace the certificate at this date. It must be in the future and within the validity period of the provided new public key. If not provided, the 'valid from' part of the certificate is used.")
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PM_MIGRATION_DATE)));
        aForm.addFormGroup (new BootstrapFormGroup ().setLabelMandatory ("New public key")
                                                     .setCtrl (new HCTextAreaAutosize (new RequestField (FIELD_PM_PUBLIC_KEY)).setRows (5))
                                                     .setHelpText ("Paste the public part of your new certificate here (using PEM encoding). Do NOT paste your new private key here.")
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_PM_PUBLIC_KEY)));

        final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
        aToolbar.addHiddenField (CPageParam.PARAM_ACTION, CPageParam.ACTION_PERFORM);
        aToolbar.addHiddenField (CPageParam.PARAM_SUBACTION, SUBACTION_SMP_UPDATE_CERT);
        aToolbar.addSubmitButton ("Prepare certificate update");

        aTabBox.addTab ("updatecert",
                        "Update SMP certificate at SML",
                        aForm,
                        aWPEC.hasSubAction (SUBACTION_SMP_UPDATE_CERT));
      }

      aNodeList.addChild (aTabBox);
    }
  }
}
