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
package eu.europa.ec.cipa.validation.pyramid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.error.IResourceError;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.locale.country.CountryCache;
import com.helger.peppol.testfiles.ubl.EPeppolUBLTestFileType;
import com.helger.peppol.testfiles.ubl.PeppolUBLTestFiles;

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;
import eu.europa.ec.cipa.validation.rules.EValidationArtefact;
import eu.europa.ec.cipa.validation.rules.EValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.EValidationLevel;
import eu.europa.ec.cipa.validation.rules.ValidationTransaction;

/**
 * Test class for class {@link ValidationPyramid2}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class ValidationPyramid2Test
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (ValidationPyramid2Test.class);

  @Test
  public void testInvoice ()
  {
    final ValidationPyramid2 vp = ValidationPyramid2.createDefault (EValidationDocumentType.INVOICE,
                                                                    ValidationTransaction.createUBLTransaction (ETransaction.T10));
    for (final IReadableResource aTestFile : PeppolUBLTestFiles.getSuccessFiles (EPeppolUBLTestFileType.INVOICE))
    {
      for (final ValidationPyramidResultLayer aResultLayer : vp.applyValidation (aTestFile)
                                                               .getAllValidationResultLayers ())
        for (final IResourceError aError : aResultLayer.getValidationErrors ())
          s_aLogger.info (aResultLayer.getValidationLevel () + " " + aError.getAsString (Locale.US));
    }
  }

  @Test
  public void testInvoiceAT ()
  {
    final Locale aCountry = CountryCache.getInstance ().getCountry ("AT");
    final ValidationPyramid2 vp = ValidationPyramid2.createDefault (EValidationDocumentType.INVOICE,
                                                                    ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                    aCountry);
    vp.addValidationLayer (EValidationArtefact.INVOICE_AUSTRIA_GOVERNMENT);
    try
    {
      // Country mismatch
      vp.addValidationLayer (EValidationArtefact.INVOICE_NORWAY_GOVERNMENT);
      fail ();
    }
    catch (final IllegalArgumentException ex)
    {}

    for (final IReadableResource aTestFile : PeppolUBLTestFiles.getSuccessFiles (EPeppolUBLTestFileType.INVOICE, aCountry))
    {
      // Do validation
      final ValidationPyramidResult aResult = vp.applyValidation (aTestFile);
      assertNotNull (aResult);

      // Check that we have results for all levels except entity specific (even
      // of they may be empty)
      for (final EValidationLevel eValidationLevel : EValidationLevel.values ())
        if (eValidationLevel.isLowerOrEqualLevelThan (EValidationLevel.LEGAL_REQUIREMENTS))
          assertTrue (eValidationLevel.getID () +
                      " is not contained",
                      aResult.containsValidationResultLayerForLevel (eValidationLevel));

      // List all elements of all layers
      int nItems = 0;
      for (final ValidationPyramidResultLayer aResultLayer : aResult.getAllValidationResultLayers ())
      {
        assertNotNull (aResultLayer);
        assertNotNull (aResultLayer.getValidationLevel ());
        assertNotNull (aResultLayer.getXMLValidationType ());
        assertNotNull (aResultLayer.getValidationErrors ());

        for (final IResourceError aError : aResultLayer.getValidationErrors ())
        {
          s_aLogger.info (aResultLayer.getValidationLevel () + " " + aError.getAsString (Locale.US));
          nItems++;
        }
      }

      // Check that the number matches the aggregated number
      assertEquals (nItems, aResult.getAggregatedResults ().getSize ());
    }
  }
}
