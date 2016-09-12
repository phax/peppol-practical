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

import java.util.Locale;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.ToStringGenerator;
import com.helger.xml.serialize.read.DOMReader;

import eu.europa.ec.cipa.validation.generic.IXMLValidator;
import eu.europa.ec.cipa.validation.rules.EValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.EValidationLevel;
import eu.europa.ec.cipa.validation.rules.IValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.IValidationTransaction;
import eu.europa.ec.cipa.validation.rules.ValidationTransaction;

/**
 * Abstract base class for {@link IValidationPyramid}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public abstract class AbstractValidationPyramid implements IValidationPyramid
{
  protected final IValidationDocumentType m_aValidationDocType;
  protected final IValidationTransaction m_aValidationTransaction;
  protected final Locale m_aValidationCountry;
  protected final ICommonsList <ValidationPyramidLayer> m_aValidationLayers = new CommonsArrayList<> ();

  /**
   * Create a new validation pyramid that handles the first four levels.
   *
   * @param aValidationDocumentType
   *        Document type. Determines the
   *        {@link EValidationLevel#TECHNICAL_STRUCTURE} layer. May not be
   *        <code>null</code>.
   * @param aValidationTransaction
   *        Transaction. May not be <code>null</code>.
   * @param aValidationCountry
   *        The validation country. May be <code>null</code> to use only the
   *        country independent validation levels (the first three levels).
   * @see EValidationDocumentType
   * @see ValidationTransaction
   */
  public AbstractValidationPyramid (@Nonnull final IValidationDocumentType aValidationDocumentType,
                                    @Nonnull final IValidationTransaction aValidationTransaction,
                                    @Nullable final Locale aValidationCountry)
  {
    ValueEnforcer.notNull (aValidationDocumentType, "DocumentType");
    ValueEnforcer.notNull (aValidationTransaction, "Transaction");

    m_aValidationDocType = aValidationDocumentType;
    m_aValidationTransaction = aValidationTransaction;
    m_aValidationCountry = aValidationCountry;
  }

  @Nonnull
  public IValidationDocumentType getValidationDocumentType ()
  {
    return m_aValidationDocType;
  }

  @Nonnull
  public IValidationTransaction getValidationTransaction ()
  {
    return m_aValidationTransaction;
  }

  public boolean isValidationCountryIndependent ()
  {
    return m_aValidationCountry == null;
  }

  @Nullable
  public Locale getValidationCountry ()
  {
    return m_aValidationCountry;
  }

  @Nonnegative
  public int getValidationLayerCount ()
  {
    return m_aValidationLayers.size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <ValidationPyramidLayer> getAllValidationLayers ()
  {
    return m_aValidationLayers.getClone ();
  }

  @Nonnull
  public ValidationPyramidResult applyValidation (@Nonnull final IReadableResource aRes)
  {
    ValueEnforcer.notNull (aRes, "Resource");

    try
    {
      final Document aDoc = DOMReader.readXMLDOM (aRes);
      return applyValidation (aRes.getPath (), new DOMSource (aDoc));
    }
    catch (final SAXException ex)
    {
      throw new IllegalArgumentException ("Failed to parse XML", ex);
    }
  }

  @Nonnull
  public ValidationPyramidResult applyValidation (@Nonnull final Source aXML)
  {
    return applyValidation (null, aXML);
  }

  @Nonnull
  public ValidationPyramidResult applyValidation (@Nullable final String sResourceName, @Nonnull final Source aXML)
  {
    ValueEnforcer.notNull (aXML, "XML");

    final ValidationPyramidResult ret = new ValidationPyramidResult (m_aValidationDocType,
                                                                     m_aValidationTransaction,
                                                                     m_aValidationCountry);

    final int nMaxLayers = m_aValidationLayers.size ();
    int nLayerIndex = 0;

    // For all validation layers
    for (final ValidationPyramidLayer aValidationLayer : m_aValidationLayers)
    {
      // The validator to use
      final IXMLValidator aValidator = aValidationLayer.getValidator ();

      // Perform the validation
      final IErrorList aErrors = aValidator.validateXMLInstance (sResourceName, aXML);

      // Add the single result to the validation pyramid
      ret.addValidationResultLayer (new ValidationPyramidResultLayer (aValidationLayer.getValidationLevel (),
                                                                      aValidator.getValidationType (),
                                                                      aValidationLayer.isStopValidatingOnError (),
                                                                      aErrors));
      if (aValidationLayer.isStopValidatingOnError () && aErrors.containsAtLeastOneError ())
      {
        // Stop validating the whole pyramid!
        ret.setValidationInterrupted (nLayerIndex < (nMaxLayers - 1));
        break;
      }
      ++nLayerIndex;
    }
    return ret;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("docType", m_aValidationDocType)
                                       .append ("transaction", m_aValidationTransaction)
                                       .append ("country", m_aValidationCountry)
                                       .append ("layers", m_aValidationLayers)
                                       .toString ();
  }
}
