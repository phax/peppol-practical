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

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.Source;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.io.resource.IReadableResource;

import eu.europa.ec.cipa.validation.rules.IValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.IValidationTransaction;

/**
 * Base interface for the validation pyramid
 * 
 * @author Philip Helger
 */
public interface IValidationPyramid
{
  /**
   * @return The validation document to which the pyramid can be applied. Never
   *         <code>null</code>.
   */
  @Nonnull
  IValidationDocumentType getValidationDocumentType ();

  /**
   * @return The validation transaction to which the pyramid can be applied.
   *         Never <code>null</code>.
   */
  @Nonnull
  IValidationTransaction getValidationTransaction ();

  /**
   * @return <code>true</code> if the validation pyramid is country independent,
   *         <code>false</code> if a specific country is defined
   * @see #getValidationCountry()
   */
  boolean isValidationCountryIndependent ();

  /**
   * @return <code>null</code> if no specific country is used in validation.
   * @see #isValidationCountryIndependent()
   */
  @Nullable
  Locale getValidationCountry ();

  /**
   * @return The number of contained validation layers. Always &ge; 0.
   */
  @Nonnegative
  int getValidationLayerCount ();

  /**
   * @return A non-<code>null</code> list of all contained validation layers in
   *         the order they are executed.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <ValidationPyramidLayer> getAllValidationLayers ();

  /**
   * Apply the validation pyramid on the passed XML resource.
   * 
   * @param aRes
   *        The XML resource to apply the validation pyramid on. May not be
   *        <code>null</code>.
   * @return The validation pyramid result. Never <code>null</code>.
   */
  @Nonnull
  ValidationPyramidResult applyValidation (@Nonnull IReadableResource aRes);

  /**
   * Apply the pyramid on the passed {@link Source} object.
   * 
   * @param aXML
   *        The XML {@link Source}. IMPORTANT: Must be a {@link Source} that can
   *        be opened multiple times. Using e.g. a StreamSource with a
   *        StringReader will result in an error!
   * @return a non-<code>null</code> validation pyramid result.
   */
  @Nonnull
  ValidationPyramidResult applyValidation (@Nonnull Source aXML);

  /**
   * Apply the pyramid on the passed {@link Source} object.
   * 
   * @param sResourceName
   *        The optional name of the source. Only used for error messages. May
   *        be <code>null</code>.
   * @param aXML
   *        The XML {@link Source}. IMPORTANT: Must be a {@link Source} that can
   *        be opened multiple times. Using e.g. a StreamSource with a
   *        StringReader will result in an error!
   * @return a non-<code>null</code> validation pyramid result.
   */
  @Nonnull
  ValidationPyramidResult applyValidation (@Nullable String sResourceName, @Nonnull Source aXML);
}
