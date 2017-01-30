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
package com.helger.peppol.ws;

import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.bdve.peppol.PeppolValidation330;
import com.helger.commons.charset.CCharset;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.commons.url.URLHelper;
import com.helger.commons.ws.WSClientConfig;
import com.helger.peppol.wsclient2.ItemType;
import com.helger.peppol.wsclient2.RequestType;
import com.helger.peppol.wsclient2.ResponseType;
import com.helger.peppol.wsclient2.ValidateFaultError;
import com.helger.peppol.wsclient2.ValidationResultType;
import com.helger.peppol.wsclient2.WSDVSPort;
import com.helger.peppol.wsclient2.WSDVSService;

public final class MainWSDVSClient
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MainWSDVSClient.class);

  public static void main (final String [] args) throws ValidateFaultError
  {
    s_aLogger.info ("Starting the engines");
    final String sXML = StreamHelper.getAllBytesAsString (new ClassPathResource ("ws/invoice1.xml"),
                                                          CCharset.CHARSET_UTF_8_OBJ);

    final WSDVSService aService = new WSDVSService (new FileSystemResource ("src/main/webapp/WEB-INF/wsdl/pp-dvs.wsdl").getAsURL ());
    final WSDVSPort aPort = aService.getWSDVSPort ();

    final WSClientConfig aWsClientConfig = new WSClientConfig (URLHelper.getAsURL ("http://localhost:8080/wsdvs"));
    aWsClientConfig.applyWSSettingsToBindingProvider ((BindingProvider) aPort);

    s_aLogger.info ("Starting validation process");
    final RequestType aRequest = new RequestType ();
    aRequest.setVesID (PeppolValidation330.VID_OPENPEPPOL_T10_V2.getAsSingleID ());
    aRequest.setXML (sXML);
    aRequest.setDisplayLocale ("en");
    final ResponseType aResponse = aPort.validate (aRequest);
    s_aLogger.info ("Success: " + aResponse.isSuccess ());
    s_aLogger.info ("Interrupted: " + aResponse.isInterrupted ());
    s_aLogger.info ("Most severe error level: " + aResponse.getMostSevereErrorLevel ());
    int nPos = 1;
    final int nMaxPos = aResponse.getResultCount ();
    for (final ValidationResultType aResult : aResponse.getResult ())
    {
      s_aLogger.info ("  [" +
                      nPos +
                      "/" +
                      nMaxPos +
                      "] " +
                      aResult.getArtifactType () +
                      " - " +
                      aResult.getArtifactPath ());
      ++nPos;

      s_aLogger.info ("  Success: " + aResult.getSuccess ());
      for (final ItemType aItem : aResult.getItem ())
      {
        s_aLogger.info ("    Error Level: " + aItem.getErrorLevel ());
        if (aItem.getErrorID () != null)
          s_aLogger.info ("    Error ID: " + aItem.getErrorID ());
        if (aItem.getErrorFieldName () != null)
          s_aLogger.info ("    Error Field: " + aItem.getErrorFieldName ());
        s_aLogger.info ("    Error Text: " + aItem.getErrorText ());
        if (aItem.getErrorLocation () != null)
          s_aLogger.info ("    Location: " + aItem.getErrorLocation ());
        if (aItem.getTest () != null)
          s_aLogger.info ("    Test: " + aItem.getTest ());
        s_aLogger.info ("--");
      }
    }
    s_aLogger.info ("Done");
  }
}
