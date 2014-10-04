/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;
import eu.europa.ec.cipa.validation.rules.EValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.EValidationSyntaxBinding;

@WebService (targetNamespace = "http://peppol.phloc.com/ws/documentvalidationservice/20110915/")
public interface IDocumentValidation
{
  /**
   * Execute the validation pyramid.<br>
   * Because of JAXB dependencies, @XmlElement (required = true) cannot be put
   * on all required parameters even though it makes sense.
   * 
   * @param eSyntaxBinding
   *        Syntax binding to use. Required.
   * @param eDocType
   *        Document type to use. Required.
   * @param eTransaction
   *        Transaction to use. Required.
   * @param sCountry
   *        Country specific rule locale. Optional.
   * @param bIndustrySpecificRules
   *        Use industry specific rules? Required.
   * @param sXMLDoc
   *        The XML content to be validated. Required.
   * @param sDisplayLocale
   *        The display locale to use. Optional.
   * @return The non-<code>null</code> validation result.
   */
  @Nonnull
  ValidationServiceResult executeValidationPyramid (@Nonnull @WebParam (name = "SyntaxBinding") EValidationSyntaxBinding eSyntaxBinding,
                                                    @Nonnull @WebParam (name = "DocumentType") EValidationDocumentType eDocType,
                                                    @Nonnull @WebParam (name = "Transaction") ETransaction eTransaction,
                                                    @Nullable @WebParam (name = "Country") String sCountry,
                                                    @WebParam (name = "IndustrySpecificRules") boolean bIndustrySpecificRules,
                                                    @Nonnull @WebParam (name = "Document") String sXMLDoc,
                                                    @Nullable @WebParam (name = "DisplayLocale") String sDisplayLocale);
}
