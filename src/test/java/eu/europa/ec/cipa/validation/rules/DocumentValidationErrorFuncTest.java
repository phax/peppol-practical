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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.error.EErrorLevel;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.locale.country.CountryCache;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.xml.serialize.read.DOMReader;
import com.helger.commons.xml.serialize.write.XMLWriter;
import com.helger.peppol.testfiles.ErrorDefinition;
import com.helger.peppol.testfiles.TestResource;
import com.helger.peppol.testfiles.ubl.EPeppolUBLTestFileType;
import com.helger.peppol.testfiles.ubl.PeppolBISV1TestFiles;
import com.helger.schematron.SchematronHelper;
import com.helger.schematron.pure.SchematronResourcePure;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.SVRLWriter;
import com.helger.ubl20.UBL20Reader;

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import oasis.names.specification.ubl.schema.xsd.order_2.OrderType;

/**
 * Validate documents using the supplied functionality of
 * {@link EValidationArtefact}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class DocumentValidationErrorFuncTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (DocumentValidationErrorFuncTest.class);

  @Nonnull
  @ReturnsMutableCopy
  private static Set <ErrorDefinition> _getAllFailedAssertionErrorCode (@Nonnull final SchematronOutputType aSVRL)
  {
    final Set <ErrorDefinition> ret = new HashSet <ErrorDefinition> ();
    final List <SVRLFailedAssert> aFAs = SVRLHelper.getAllFailedAssertions (aSVRL);
    for (final SVRLFailedAssert aFA : aFAs)
    {
      final String sText = aFA.getText ();
      final boolean bIsWarning = aFA.getFlag ().isLessOrEqualSevereThan (EErrorLevel.WARN);
      final String [] aMatches = RegExHelper.getAllMatchingGroupValues ("^\\[(.+)\\].+", sText);
      if (aMatches != null)
      {
        if (bIsWarning)
          ret.add (ErrorDefinition.createWarning (aMatches[0]));
        else
          ret.add (ErrorDefinition.createError (aMatches[0]));
      }
    }
    return ret;
  }

  @Test
  public void testReadOrdersError () throws SAXException
  {
    final IValidationTransaction aVT = ValidationTransaction.createUBLTransaction (ETransaction.T01);
    // For all available orders
    for (final TestResource aTestDoc : PeppolBISV1TestFiles.getErrorFiles (EPeppolUBLTestFileType.ORDER))
    {
      // Get the UBL XML file
      final IReadableResource aTestFile = aTestDoc.getResource ();
      final Document aTestFileDoc = DOMReader.readXMLDOM (aTestFile);

      // Ensure the UBL file validates against the scheme
      final OrderType aUBLOrder = UBL20Reader.order ().read (aTestFileDoc);
      assertNotNull (aUBLOrder);

      final Set <ErrorDefinition> aErrCodes = new HashSet <ErrorDefinition> ();

      // Test the country-independent orders layers
      for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.ORDER,
                                                                                              CGlobal.LOCALE_INDEPENDENT))
      {

        SchematronOutputType aSVRL;
        aSVRL = SchematronHelper.applySchematron (new SchematronResourcePure (eArtefact.getValidationSchematronResource (aVT)), aTestFileDoc);
        assertNotNull (aSVRL);

        if (false)
        {
          // For debugging purposes: print the SVRL
          s_aLogger.info (XMLWriter.getXMLString (SVRLWriter.createXML (aSVRL)));
        }

        aErrCodes.addAll (_getAllFailedAssertionErrorCode (aSVRL));
      }

      final Set <ErrorDefinition> aCopy = new TreeSet <ErrorDefinition> (aErrCodes);
      for (final ErrorDefinition aExpectedErrCode : aTestDoc.getAllExpectedErrors ())
        assertTrue (aTestDoc.getPath () +
                    " expected " +
                    aExpectedErrCode.toString () +
                    " but having " +
                    aCopy.toString (),
                    aCopy.remove (aExpectedErrCode));
      if (!aCopy.isEmpty ())
        s_aLogger.info (aCopy.toString ());
      assertTrue (aTestDoc.getPath () + " also indicated: " + aCopy.toString (), aCopy.isEmpty ());
    }
  }

  @Test
  public void testReadInvoicesError () throws SAXException
  {
    final IValidationTransaction aVT = ValidationTransaction.createUBLTransaction (ETransaction.T10);
    // For all available orders
    for (final TestResource aTestDoc : PeppolBISV1TestFiles.getErrorFiles (EPeppolUBLTestFileType.INVOICE))
    {
      // Get the UBL XML file
      final IReadableResource aTestFile = aTestDoc.getResource ();
      final Document aTestFileDoc = DOMReader.readXMLDOM (aTestFile);

      // Ensure the UBL file validates against the scheme
      final InvoiceType aUBLInvoice = UBL20Reader.invoice ().read (aTestFileDoc);
      assertNotNull (aUBLInvoice);

      final Set <ErrorDefinition> aErrCodes = new HashSet <ErrorDefinition> ();

      // Test the country-independent invoice layers
      for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null,
                                                                                              EValidationDocumentType.INVOICE,
                                                                                              CGlobal.LOCALE_INDEPENDENT))
      {
        SchematronOutputType aSVRL;
        aSVRL = SchematronHelper.applySchematron (new SchematronResourcePure (eArtefact.getValidationSchematronResource (aVT)), aTestFileDoc);
        assertNotNull (aSVRL);

        if (false)
        {
          // For debugging purposes: print the SVRL
          s_aLogger.info (SVRLWriter.createXMLString (aSVRL));
        }

        aErrCodes.addAll (_getAllFailedAssertionErrorCode (aSVRL));
      }
      final Set <ErrorDefinition> aCopy = new TreeSet <ErrorDefinition> (aErrCodes);
      for (final ErrorDefinition aExpectedErrCode : aTestDoc.getAllExpectedErrors ())
        assertTrue (aTestDoc.getPath () +
                    " expected " +
                    aExpectedErrCode.toString () +
                    " but having " +
                    aCopy.toString (),
                    aCopy.remove (aExpectedErrCode));
      assertTrue (aTestDoc.getPath () + " also indicated: " + aCopy, aCopy.isEmpty ());
    }
  }

  @Test
  public void testReadInvoicesErrorAT () throws SAXException
  {
    final IValidationTransaction aVT = ValidationTransaction.createUBLTransaction (ETransaction.T10);
    // For all available orders
    final Locale aCountry = CountryCache.getInstance ().getCountry ("AT");
    for (final TestResource aTestDoc : PeppolBISV1TestFiles.getErrorFiles (EPeppolUBLTestFileType.INVOICE, aCountry))
    {
      // Get the UBL XML file
      final IReadableResource aTestFile = aTestDoc.getResource ();
      final Document aTestFileDoc = DOMReader.readXMLDOM (aTestFile);

      if (true)
        s_aLogger.info (aTestFile.getPath ());

      // Ensure the UBL file validates against the scheme
      try
      {
        final InvoiceType aUBLInvoice = UBL20Reader.invoice ().read (aTestFileDoc);
        assertNotNull (aUBLInvoice);

        final Set <ErrorDefinition> aErrCodes = new HashSet <ErrorDefinition> ();

        // Test the country-dependent invoice layers
        for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (null, EValidationDocumentType.INVOICE, aCountry))
        {
          SchematronOutputType aSVRL;
          aSVRL = SchematronHelper.applySchematron (new SchematronResourcePure (eArtefact.getValidationSchematronResource (aVT)), aTestFileDoc);
          assertNotNull (aSVRL);

          if (false)
          {
            // For debugging purposes: print the SVRL
            s_aLogger.info (SVRLWriter.createXMLString (aSVRL));
          }

          aErrCodes.addAll (_getAllFailedAssertionErrorCode (aSVRL));
        }
        final Set <ErrorDefinition> aCopy = new TreeSet <ErrorDefinition> (aErrCodes);
        for (final ErrorDefinition aExpectedErrCode : aTestDoc.getAllExpectedErrors ())
          assertTrue (aTestDoc.getPath () +
                      " expected " +
                      aExpectedErrCode.toString () +
                      " but having " +
                      aCopy.toString (),
                      aCopy.remove (aExpectedErrCode));
        assertTrue (aTestDoc.getPath () + " also indicated: " + aCopy, aCopy.isEmpty ());
      }
      catch (final OutOfMemoryError ex)
      {
        // Continue with next. May happen with
        // /test-invoices/error/atgov-t10-fail-r014.xml
        s_aLogger.warn ("OufOfMemoryError for " + aTestFile.getPath () + " - continuing with next file!");
      }
    }
  }
}
