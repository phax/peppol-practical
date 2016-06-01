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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.error.IResourceErrorGroup;
import com.helger.commons.error.ResourceErrorGroup;
import com.helger.commons.string.ToStringGenerator;

import eu.europa.ec.cipa.validation.rules.IValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.IValidationLevel;
import eu.europa.ec.cipa.validation.rules.IValidationTransaction;

/**
 * Represents a result of validating the whole pyramid.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public class ValidationPyramidResult
{
  private final IValidationDocumentType m_aValidationDocumentType;
  private final IValidationTransaction m_aValidationTransaction;
  private final Locale m_aValidationCountry;
  private final ICommonsList <ValidationPyramidResultLayer> m_aValidationResultLayers = new CommonsArrayList<> ();
  private boolean m_bValidationInterrupted = false;

  /**
   * Constructor. Passing the input parameters to validation to query them from
   * the result object without having access to the query parameters.
   *
   * @param aValidationDocumentType
   *        The document type to be validated. May not be <code>null</code>.
   * @param aValidationTransaction
   *        The transaction to be validated. May not be <code>null</code>.
   * @param aValidationCountry
   *        The relevant country. May be <code>null</code>.
   */
  public ValidationPyramidResult (@Nonnull final IValidationDocumentType aValidationDocumentType,
                                  @Nonnull final IValidationTransaction aValidationTransaction,
                                  @Nullable final Locale aValidationCountry)
  {
    ValueEnforcer.notNull (aValidationDocumentType, "ValidationDocumentType");
    ValueEnforcer.notNull (aValidationTransaction, "ValidationTransaction");

    m_aValidationDocumentType = aValidationDocumentType;
    m_aValidationTransaction = aValidationTransaction;
    m_aValidationCountry = aValidationCountry;
  }

  /**
   * @return The document type the validation pyramid was applied on. Never
   *         <code>null</code>.
   */
  @Nonnull
  public IValidationDocumentType getValidationDocumentType ()
  {
    return m_aValidationDocumentType;
  }

  /**
   * @return The transaction the validation pyramid was applied on. Never
   *         <code>null</code>.
   */
  @Nonnull
  public IValidationTransaction getValidationTransaction ()
  {
    return m_aValidationTransaction;
  }

  /**
   * @return <code>true</code> if no special country specified rules were used
   *         in the validation pyramid.
   */
  public boolean isValidationCountryIndependent ()
  {
    return m_aValidationCountry == null;
  }

  /**
   * @return The country for which country specific rules were applied in the
   *         validation pyramid.
   */
  @Nullable
  public Locale getValidationCountry ()
  {
    return m_aValidationCountry;
  }

  /**
   * Add a new validation result layer.
   *
   * @param aResultLayer
   *        The new validation result layer. May not be <code>null</code>.
   */
  public void addValidationResultLayer (@Nonnull final ValidationPyramidResultLayer aResultLayer)
  {
    ValueEnforcer.notNull (aResultLayer, "ResultLayer");
    m_aValidationResultLayers.add (aResultLayer);
  }

  /**
   * @return A non-<code>null</code> list of all contained validation result
   *         layers.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <ValidationPyramidResultLayer> getAllValidationResultLayers ()
  {
    return m_aValidationResultLayers.getClone ();
  }

  /**
   * Call this method with <code>true</code> to indicate that not the whole
   * pyramid was handled, so this result reflects only the result of validating
   * a part of the pyramid.
   *
   * @param bInterrupted
   *        <code>true</code> if interrupted, <code>false</code> otherwise.
   */
  public void setValidationInterrupted (final boolean bInterrupted)
  {
    m_bValidationInterrupted = bInterrupted;
  }

  /**
   * @return <code>true</code> if not the whole validation pyramid was executed.
   */
  public boolean isValidationInterrupted ()
  {
    return m_bValidationInterrupted;
  }

  /**
   * Check if this result set contains results for the specified validation
   * level.
   *
   * @param aValidationLevel
   *        The validation level to check. May not be <code>null</code>.
   * @return <code>true</code> if results are contained for the specified level,
   *         <code>false</code> otherwise.
   */
  public boolean containsValidationResultLayerForLevel (@Nonnull final IValidationLevel aValidationLevel)
  {
    ValueEnforcer.notNull (aValidationLevel, "ValidationLevel");

    for (final ValidationPyramidResultLayer aValidationResultLayer : m_aValidationResultLayers)
      if (aValidationResultLayer.getValidationLevel ().equals (aValidationLevel))
        return true;
    return false;
  }

  /**
   * Get all validation result layers for the passed validation level.
   *
   * @param aValidationLevel
   *        The validation level to use. May not be <code>null</code>.
   * @return A non-<code>null</code> potentially empty list of all validation
   *         result layers.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <ValidationPyramidResultLayer> getValidationResultLayersForLevel (@Nonnull final IValidationLevel aValidationLevel)
  {
    ValueEnforcer.notNull (aValidationLevel, "ValidationLevel");

    final ICommonsList <ValidationPyramidResultLayer> ret = new CommonsArrayList<> ();
    for (final ValidationPyramidResultLayer aValidationResultLayer : m_aValidationResultLayers)
      if (aValidationResultLayer.getValidationLevel ().equals (aValidationLevel))
        ret.add (aValidationResultLayer);
    return ret;
  }

  /**
   * Get an aggregated error object, that contains the elements of all
   * validation result layers.
   *
   * @return A non-<code>null</code> aggregated result error object.
   */
  @Nonnull
  @ReturnsMutableCopy
  public IResourceErrorGroup getAggregatedResults ()
  {
    final ResourceErrorGroup aAggregatedResults = new ResourceErrorGroup ();
    for (final ValidationPyramidResultLayer aValidationResultLayer : m_aValidationResultLayers)
      aAggregatedResults.addResourceErrorGroup (aValidationResultLayer.getValidationErrors ());
    return aAggregatedResults;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("docType", m_aValidationDocumentType)
                                       .append ("transaction", m_aValidationTransaction)
                                       .appendIfNotNull ("country", m_aValidationCountry)
                                       .append ("resultLayers", m_aValidationResultLayers)
                                       .append ("interrupted", m_bValidationInterrupted)
                                       .toString ();
  }
}
