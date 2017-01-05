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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.charset.CCharset;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.stream.StreamHelper;
import com.helger.peppol.wsclient.ETransaction;
import com.helger.peppol.wsclient.EValidationDocumentType;
import com.helger.peppol.wsclient.EValidationSyntaxBinding;
import com.helger.peppol.wsclient.IDocumentValidation;
import com.helger.peppol.wsclient.ValidationServiceResultItemType;
import com.helger.peppol.wsclient.ValidationServiceResultType;
import com.helger.peppol.wsclient.WSDocumentValidationService;

public final class WSDocumentValiationServiceTester
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (WSDocumentValiationServiceTester.class);

  public static void main (final String [] args)
  {
    s_aLogger.info ("Starting the engines");
    final String sXML = StreamHelper.getAllBytesAsString (new ClassPathResource ("invoice1.xml"),
                                                          CCharset.CHARSET_UTF_8_OBJ);

    final WSDocumentValidationService aService = new WSDocumentValidationService ();
    final IDocumentValidation aPort = aService.getWSDocumentValidationPort ();
    s_aLogger.info ("Starting validation process");
    final ValidationServiceResultType aResult = aPort.executeValidationPyramid (EValidationSyntaxBinding.UBL,
                                                                                EValidationDocumentType.INVOICE,
                                                                                ETransaction.T_10,
                                                                                null,
                                                                                false,
                                                                                sXML,
                                                                                "de");
    s_aLogger.info ("Return code: " + aResult.getReturnCode ());
    s_aLogger.info ("Most severe error level: " + aResult.getMostSevereErrorLevel ());
    for (final ValidationServiceResultItemType aItem : aResult.getItems ())
    {
      s_aLogger.info (aItem.getErrorLevel () +
                      " in " +
                      aItem.getValidationType () +
                      " validation on level " +
                      aItem.getValidationLevel ());
      s_aLogger.info ("  " + aItem.getErrorText ());
      s_aLogger.info ("  Location: " + aItem.getLocation ());
      if (aItem.getSVRLTest () != null)
        s_aLogger.info ("  Test: " + aItem.getSVRLTest ());
    }
    s_aLogger.info ("Done");
  }
}
