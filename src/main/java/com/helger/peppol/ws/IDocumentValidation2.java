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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.helger.peppol.ws.types.ValidationServiceResult;

@WebService (targetNamespace = "http://peppol.helger.com/ws/documentvalidationservice/201701/")
public interface IDocumentValidation2
{
  /**
   * Execute the BIS2 validation.<br>
   * Because of JAXB dependencies, @XmlElement (required = true) cannot be put
   * on all required parameters even though it makes sense.
   *
   * @param sBusinessSpecID
   *        Business specification ID to use. Required.
   * @param sTransactionID
   *        Transaction ID to use. Defines the document type. Required.
   * @param sCountry
   *        Country specific rule locale. Optional.
   * @param sSectorKey
   *        Sector specific rule ID. Optional.
   * @param sXMLDoc
   *        The XML content to be validated. Required.
   * @param sDisplayLocale
   *        The display locale to use. Optional.
   * @return The non-<code>null</code> validation result.
   */
  @Nonnull
  ValidationServiceResult executeValidation (@Nonnull @WebParam (name = "SpecificationID") String sBusinessSpecID,
                                             @Nonnull @WebParam (name = "TransactionID") String sTransactionID,
                                             @Nullable @WebParam (name = "Country") String sCountry,
                                             @Nullable @WebParam (name = "SectorKey") String sSectorKey,
                                             @Nonnull @WebParam (name = "Document") String sXMLDoc,
                                             @Nullable @WebParam (name = "DisplayLocale") String sDisplayLocale);
}
