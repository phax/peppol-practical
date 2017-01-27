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
package com.helger.peppol.ws2;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService (targetNamespace = "http://peppol.helger.com/ws/documentvalidationservice/201701/")
public interface IDocumentValidation2
{
  /**
   * Execute the BIS2 validation.<br>
   * Because of JAXB dependencies, @XmlElement (required = true) cannot be put
   * on all required parameters even though it makes sense.
   *
   * @param sVESID
   *        The validation executor set ID. Required.
   * @param sXMLDoc
   *        The XML content to be validated. Required.
   * @param sDisplayLocale
   *        The display locale to use. Optional. If none is provided, the
   *        default locale "en_US" is used.
   * @return The non-<code>null</code> validation result.
   */
  @Nonnull
  DocumentValidation2Result executeValidation (@Nonnull @WebParam (name = "VESID") String sVESID,
                                               @Nonnull @WebParam (name = "Document") String sXMLDoc,
                                               @Nullable @WebParam (name = "DisplayLocale") String sDisplayLocale);
}
