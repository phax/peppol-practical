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
import static org.junit.Assert.assertNotNull;

import java.util.Locale;

import org.junit.Test;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.error.EErrorLevel;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.locale.country.CountryCache;
import com.helger.commons.xml.serialize.write.XMLWriter;
import com.helger.peppol.testfiles.peppolubl.EPeppolUBLTestFileType;
import com.helger.peppol.testfiles.peppolubl.PeppolUBLTestFiles;
import com.helger.schematron.SchematronHelper;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.SVRLWriter;
import com.helger.schematron.xslt.SchematronResourceXSLT;
import com.helger.ubl20.UBL20Reader;

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;
import oasis.names.specification.ubl.schema.xsd.catalogue_2.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_2.OrderType;
import oasis.names.specification.ubl.schema.xsd.orderresponsesimple_2.OrderResponseSimpleType;

/**
 * Validate documents using the supplied functionality of
 * {@link EValidationArtefact}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class DocumentValidationSuccessFuncTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (DocumentValidationSuccessFuncTest.class);

  @Test
  public void testReadCataloguesSuccess ()
  {
    // For all available catalogues
    for (final IReadableResource aTestFile : PeppolUBLTestFiles.getSuccessFiles (EPeppolUBLTestFileType.CATALOGUE))
    {
      // Ensure the UBL file validates against the scheme
      final CatalogueType aUBLCatalogue = UBL20Reader.readCatalogue (aTestFile);
      assertNotNull (aUBLCatalogue);

      // Test the country-independent catalogue layers
      for (final EValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.CATALOGUE,
                                                                                              CGlobal.LOCALE_INDEPENDENT))
      {
        // Get the XSLT for transaction T19
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (ValidationTransaction.createUBLTransaction (ETransaction.T19));

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aTestFile);
        assertNotNull (aSVRL);

        if (false)
        {
          // For debugging purposes: print the SVRL
          s_aLogger.info (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLHelper.getAllFailedAssertions (aSVRL))
        {
          assertEquals (aTestFile.toString (), EErrorLevel.WARN, aFailedAssert.getFlag ());
        }
      }
    }
  }

  @Test
  public void testReadOrdersSuccess ()
  {
    // For all available orders
    for (final IReadableResource aTestFile : PeppolUBLTestFiles.getSuccessFiles (EPeppolUBLTestFileType.ORDER))
    {
      // Ensure the UBL file validates against the scheme
      final OrderType aUBLOrder = UBL20Reader.readOrder (aTestFile);
      assertNotNull (aUBLOrder);

      // Test the country-independent orders layers
      for (final EValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.ORDER,
                                                                                              CGlobal.LOCALE_INDEPENDENT))
      {
        // Get the XSLT for transaction T01
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (ValidationTransaction.createUBLTransaction (ETransaction.T01));

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aTestFile);
        assertNotNull (aSVRL);

        if (false)
        {
          // For debugging purposes: print the SVRL
          s_aLogger.info (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLHelper.getAllFailedAssertions (aSVRL))
        {
          assertEquals (aTestFile.toString () +
                        "\n" +
                        aFailedAssert.toString (),
                        EErrorLevel.WARN,
                        aFailedAssert.getFlag ());
        }
      }
    }
  }

  @Test
  public void testReadOrderResponsesSuccess ()
  {
    // For all available orders
    for (final IReadableResource aTestFile : PeppolUBLTestFiles.getSuccessFiles (EPeppolUBLTestFileType.ORDERRESPONSE))
    {
      // Ensure the UBL file validates against the scheme
      final OrderResponseSimpleType aUBLOrderResponse = UBL20Reader.readOrderResponseSimple (aTestFile);
      assertNotNull (aUBLOrderResponse);

      // Test the country-independent orders layers
      for (final EValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.ORDERRESPONSE,
                                                                                              CGlobal.LOCALE_INDEPENDENT))
      {
        // Get the XSLT for transaction T01
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (ValidationTransaction.createUBLTransaction (ETransaction.T02));

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aTestFile);
        assertNotNull (aSVRL);

        if (false)
        {
          // For debugging purposes: print the SVRL
          s_aLogger.info (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLHelper.getAllFailedAssertions (aSVRL))
        {
          assertEquals (aTestFile.toString () +
                        "\n" +
                        aFailedAssert.toString (),
                        EErrorLevel.WARN,
                        aFailedAssert.getFlag ());
        }
      }
    }
  }

  @Test
  public void testReadCreditNotesSuccess ()
  {
    // For all available credit notes
    for (final IReadableResource aTestFile : PeppolUBLTestFiles.getSuccessFiles (EPeppolUBLTestFileType.CREDITNOTE))
    {
      // Ensure the UBL file validates against the scheme
      final CreditNoteType aUBLCreditNote = UBL20Reader.readCreditNote (aTestFile);
      assertNotNull (aUBLCreditNote);

      // Test the country-independent orders layers
      for (final EValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.CREDIT_NOTE,
                                                                                              CGlobal.LOCALE_INDEPENDENT))
      {
        // Get the XSLT for transaction T14
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (ValidationTransaction.createUBLTransaction (ETransaction.T14));

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aTestFile);
        assertNotNull (aSVRL);

        if (false)
        {
          // For debugging purposes: print the SVRL
          s_aLogger.info (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLHelper.getAllFailedAssertions (aSVRL))
        {
          assertEquals (aTestFile.toString () +
                        "\n" +
                        aFailedAssert.toString (),
                        EErrorLevel.WARN,
                        aFailedAssert.getFlag ());
        }
      }
    }
  }

  @Test
  public void testReadInvoicesSuccess ()
  {
    final IValidationTransaction aVT = ValidationTransaction.createUBLTransaction (ETransaction.T10);
    // For all available invoices
    for (final IReadableResource aTestFile : PeppolUBLTestFiles.getSuccessFiles (EPeppolUBLTestFileType.INVOICE))
    {
      // Ensure the UBL file validates against the scheme
      final InvoiceType aUBLInvoice = UBL20Reader.readInvoice (aTestFile);
      assertNotNull (aUBLInvoice);

      // Test the country-independent invoice layers
      for (final EValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.INVOICE,
                                                                                              CGlobal.LOCALE_INDEPENDENT))
      {
        // Get the XSLT for transaction T10
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (aVT);

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aTestFile);
        assertNotNull (aSVRL);

        if (false)
        {
          // For debugging purposes: print the SVRL
          s_aLogger.info (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLHelper.getAllFailedAssertions (aSVRL))
        {
          assertEquals (aTestFile.toString () +
                        "\n" +
                        aFailedAssert.toString (),
                        EErrorLevel.WARN,
                        aFailedAssert.getFlag ());
        }
      }
    }
  }

  @Test
  public void testReadInvoicesATSuccess ()
  {
    final Locale aCountry = CountryCache.getInstance ().getCountry ("AT");
    // For all available invoices
    for (final IReadableResource aTestFile : PeppolUBLTestFiles.getSuccessFiles (EPeppolUBLTestFileType.INVOICE, aCountry))
    {
      // Ensure the UBL file validates against the scheme
      final InvoiceType aUBLInvoice = UBL20Reader.readInvoice (aTestFile);
      assertNotNull (aUBLInvoice);

      // Test the country-independent invoice layers
      for (final EValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.INVOICE,
                                                                                              aCountry))
      {
        // Get the XSLT for transaction T10
        final IReadableResource aXSLT = eArtefact.getValidationXSLTResource (ValidationTransaction.createUBLTransaction (ETransaction.T10));

        // And now run the main "Schematron" validation
        final SchematronOutputType aSVRL = SchematronHelper.applySchematron (new SchematronResourceXSLT (aXSLT),
                                                                             aTestFile);
        assertNotNull (aSVRL);

        if (false)
        {
          // For debugging purposes: print the SVRL
          s_aLogger.info (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        // Check that all failed assertions are only warnings
        for (final SVRLFailedAssert aFailedAssert : SVRLHelper.getAllFailedAssertions (aSVRL))
        {
          assertEquals (aTestFile.toString () +
                        " " +
                        aFailedAssert.toString (),
                        EErrorLevel.WARN,
                        aFailedAssert.getFlag ());
        }
      }
    }
  }
}
