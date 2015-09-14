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
package eu.europa.ec.cipa.validation.example;

import java.util.List;
import java.util.Locale;

import com.helger.commons.error.IResourceError;
import com.helger.commons.io.resource.ClassPathResource;

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;
import eu.europa.ec.cipa.validation.pyramid.ValidationPyramid2;
import eu.europa.ec.cipa.validation.pyramid.ValidationPyramidResultLayer;
import eu.europa.ec.cipa.validation.rules.EValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.ValidationTransaction;

/**
 * A simple main application that shows how to apply {@link ValidationPyramid2}
 * in the correct way.
 * 
 * @author Philip Helger
 */
public final class ExampleValidateInvoice
{
  public static void main (final String [] args)
  {
    // Main validation object
    final ValidationPyramid2 vp = ValidationPyramid2.createDefault (EValidationDocumentType.INVOICE,
                                                                    ValidationTransaction.createUBLTransaction (ETransaction.T10));

    // Perform the validation
    System.out.println ("Performing validation");
    final List <ValidationPyramidResultLayer> aResults = vp.applyValidation (new ClassPathResource ("test-invoice.xml"))
                                                           .getAllValidationResultLayers ();

    // Show the results
    System.out.println ("Validation results:");
    if (aResults.isEmpty ())
      System.out.println ("  The document is valid!");
    else
      for (final ValidationPyramidResultLayer aResultLayer : aResults)
        for (final IResourceError aError : aResultLayer.getValidationErrors ())
          System.out.println ("  " + aResultLayer.getValidationLevel () + " " + aError.getAsString (Locale.US));
  }
}
