/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.europa.ec.cipa.validation.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.locale.country.CountryCache;
import com.helger.schematron.xslt.SchematronResourceXSLT;

/**
 * Test class for class {@link EValidationArtefact}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class EValidationArtefactTest {
  @Test
  public void testSchematronExisting () {
    // Check if all referenced SCH artefacts exist
    for (final IValidationArtefact aArtefacts : EValidationArtefact.values ())
      for (final IReadableResource aSchematron : aArtefacts.getAllValidationSchematronResources ())
        assertTrue ("Does not exist: " + aSchematron.toString (), aSchematron.exists ());
  }

  @Test
  public void testXSLTExisting () {
    // Check if all referenced XSLT artefacts exist
    for (final EValidationArtefact aArtefacts : EValidationArtefact.values ())
      for (final IReadableResource aXSLT : aArtefacts.getAllValidationXSLTResources ())
        assertTrue ("Does not exist: " + aXSLT.toString (), aXSLT.exists ());
  }

  @Test
  public void testGetAllValidationXSLTResources () {
    for (final EValidationArtefact aArtefacts : EValidationArtefact.values ()) {
      // Don't run the Schematron compilation because on some artefacts it takes
      // up to 15 minutes to compile one!!!!!
      for (final IReadableResource aXSLT : aArtefacts.getAllValidationXSLTResources ()) {
        assertTrue ("No valid schematron: " + aXSLT.toString (),
                    new SchematronResourceXSLT (aXSLT).isValidSchematron ());
      }
    }
  }

  @Test
  public void testGetAllMatchingArtefacts () {
    // Test all
    assertEquals (EValidationArtefact.values ().length, EValidationArtefact.getAllMatchingArtefacts (null, null, null)
                                                                           .size ());

    // Test for level only
    assertEquals (3, EValidationArtefact.getAllMatchingArtefacts (EValidationLevel.TECHNICAL_STRUCTURE, null, null)
                                        .size ());
    assertEquals (8,
                  EValidationArtefact.getAllMatchingArtefacts (EValidationLevel.TRANSACTION_REQUIREMENTS, null, null)
                                     .size ());
    assertEquals (2, EValidationArtefact.getAllMatchingArtefacts (EValidationLevel.PROFILE_REQUIREMENTS, null, null)
                                        .size ());
    assertEquals (6, EValidationArtefact.getAllMatchingArtefacts (EValidationLevel.LEGAL_REQUIREMENTS, null, null)
                                        .size ());
    assertEquals (4, EValidationArtefact.getAllMatchingArtefacts (EValidationLevel.INDUSTRY_SPECIFIC, null, null)
                                        .size ());
    assertEquals (0, EValidationArtefact.getAllMatchingArtefacts (EValidationLevel.ENTITY_SPECIFC, null, null).size ());

    // Test document level only
    assertEquals (0,
                  EValidationArtefact.getAllMatchingArtefacts (null, EValidationDocumentType.TENDERING_CATALOGUE, null)
                                     .size ());
    assertEquals (1, EValidationArtefact.getAllMatchingArtefacts (null, EValidationDocumentType.CATALOGUE, null)
                                        .size ());
    assertEquals (3, EValidationArtefact.getAllMatchingArtefacts (null, EValidationDocumentType.ORDER, null).size ());
    assertEquals (1, EValidationArtefact.getAllMatchingArtefacts (null, EValidationDocumentType.ORDERRESPONSE, null)
                                        .size ());
    assertEquals (10, EValidationArtefact.getAllMatchingArtefacts (null, EValidationDocumentType.INVOICE, null).size ());
    assertEquals (8, EValidationArtefact.getAllMatchingArtefacts (null, EValidationDocumentType.CREDIT_NOTE, null)
                                        .size ());

    // Test country only
    assertEquals (13,
                  EValidationArtefact.getAllMatchingArtefacts (null,
                                                               null,
                                                               CountryCache.getInstance ().getCountry ("XX")).size ());
    assertEquals (17,
                  EValidationArtefact.getAllMatchingArtefacts (null,
                                                               null,
                                                               CountryCache.getInstance ().getCountry ("AT")).size ());
    assertEquals (17,
                  EValidationArtefact.getAllMatchingArtefacts (null,
                                                               null,
                                                               CountryCache.getInstance ().getCountry ("NO")).size ());
    assertEquals (14,
                  EValidationArtefact.getAllMatchingArtefacts (null,
                                                               null,
                                                               CountryCache.getInstance ().getCountry ("DK")).size ());
    assertEquals (14,
                  EValidationArtefact.getAllMatchingArtefacts (null,
                                                               null,
                                                               CountryCache.getInstance ().getCountry ("IT")).size ());
  }

  @Test
  public void testGetAllCountriesWithSpecialRules () {
    // All counties
    assertEquals (4, EValidationArtefact.getAllCountriesWithSpecialRules (null, null).size ());
    // Level specific
    assertEquals (0, EValidationArtefact.getAllCountriesWithSpecialRules (EValidationLevel.TECHNICAL_STRUCTURE, null)
                                        .size ());
    assertEquals (0,
                  EValidationArtefact.getAllCountriesWithSpecialRules (EValidationLevel.TRANSACTION_REQUIREMENTS, null)
                                     .size ());
    assertEquals (0, EValidationArtefact.getAllCountriesWithSpecialRules (EValidationLevel.PROFILE_REQUIREMENTS, null)
                                        .size ());
    assertEquals (4, EValidationArtefact.getAllCountriesWithSpecialRules (EValidationLevel.LEGAL_REQUIREMENTS, null)
                                        .size ());
    assertEquals (2, EValidationArtefact.getAllCountriesWithSpecialRules (EValidationLevel.INDUSTRY_SPECIFIC, null)
                                        .size ());
    assertEquals (0, EValidationArtefact.getAllCountriesWithSpecialRules (EValidationLevel.ENTITY_SPECIFC, null)
                                        .size ());
    // Document type specific
    assertEquals (0,
                  EValidationArtefact.getAllCountriesWithSpecialRules (null,
                                                                       EValidationDocumentType.TENDERING_CATALOGUE)
                                     .size ());
    assertEquals (0, EValidationArtefact.getAllCountriesWithSpecialRules (null, EValidationDocumentType.CATALOGUE)
                                        .size ());
    assertEquals (0, EValidationArtefact.getAllCountriesWithSpecialRules (null, EValidationDocumentType.ORDER).size ());
    assertEquals (0, EValidationArtefact.getAllCountriesWithSpecialRules (null, EValidationDocumentType.ORDERRESPONSE)
                                        .size ());
    assertEquals (4, EValidationArtefact.getAllCountriesWithSpecialRules (null, EValidationDocumentType.INVOICE)
                                        .size ());
    // Level and document type specific
    assertEquals (0,
                  EValidationArtefact.getAllCountriesWithSpecialRules (EValidationLevel.LEGAL_REQUIREMENTS,
                                                                       EValidationDocumentType.TENDERING_CATALOGUE)
                                     .size ());
    assertEquals (0,
                  EValidationArtefact.getAllCountriesWithSpecialRules (EValidationLevel.LEGAL_REQUIREMENTS,
                                                                       EValidationDocumentType.CATALOGUE).size ());
    assertEquals (4,
                  EValidationArtefact.getAllCountriesWithSpecialRules (EValidationLevel.LEGAL_REQUIREMENTS,
                                                                       EValidationDocumentType.INVOICE).size ());
    assertEquals (2,
                  EValidationArtefact.getAllCountriesWithSpecialRules (EValidationLevel.INDUSTRY_SPECIFIC,
                                                                       EValidationDocumentType.INVOICE).size ());
    assertEquals (0,
                  EValidationArtefact.getAllCountriesWithSpecialRules (EValidationLevel.ENTITY_SPECIFC,
                                                                       EValidationDocumentType.INVOICE).size ());
  }
}
