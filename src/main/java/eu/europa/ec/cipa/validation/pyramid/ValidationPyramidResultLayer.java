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

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.string.ToStringGenerator;

import eu.europa.ec.cipa.validation.generic.EXMLValidationType;
import eu.europa.ec.cipa.validation.rules.IValidationLevel;

/**
 * Represents a single result layer of the validation pyramid.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public class ValidationPyramidResultLayer
{
  private final IValidationLevel m_aValidationLevel;
  private final EXMLValidationType m_eXMLValidationType;
  private final boolean m_bStopValidatingOnError;
  private final IErrorList m_aValidationErrors;

  /**
   * Constructor.
   *
   * @param aValidationLevel
   *        The validation level. May not be <code>null</code>.
   * @param eXMLValidationType
   *        The validation type. May not be <code>null</code>.
   * @param bStopValidatingOnError
   *        if <code>true</code> the valid is stopped, when there is an error on
   *        this level.
   * @param aValidationErrors
   *        The result of this layer. May not be <code>null</code> but may be
   *        empty.
   */
  public ValidationPyramidResultLayer (@Nonnull final IValidationLevel aValidationLevel,
                                       @Nonnull final EXMLValidationType eXMLValidationType,
                                       final boolean bStopValidatingOnError,
                                       @Nonnull final IErrorList aValidationErrors)
  {
    ValueEnforcer.notNull (aValidationLevel, "ValidationLevel");
    ValueEnforcer.notNull (eXMLValidationType, "ValidationType");
    ValueEnforcer.notNull (aValidationErrors, "Errors");
    m_aValidationLevel = aValidationLevel;
    m_eXMLValidationType = eXMLValidationType;
    m_bStopValidatingOnError = bStopValidatingOnError;
    m_aValidationErrors = aValidationErrors;
  }

  /**
   * @return The validation level. Never <code>null</code>.
   */
  @Nonnull
  public IValidationLevel getValidationLevel ()
  {
    return m_aValidationLevel;
  }

  /**
   * @return The validation type. Never <code>null</code>.
   */
  @Nonnull
  public EXMLValidationType getXMLValidationType ()
  {
    return m_eXMLValidationType;
  }

  /**
   * @return <code>true</code> if an error on this level is stopping the whole
   *         validation process.
   */
  public boolean iStopValidatingOnError ()
  {
    return m_bStopValidatingOnError;
  }

  /**
   * @return The collected validation errors. Never <code>null</code>.
   */
  @Nonnull
  public IErrorList getValidationErrors ()
  {
    return m_aValidationErrors;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("validationLevel", m_aValidationLevel)
                                       .append ("xmlValidationType", m_eXMLValidationType)
                                       .append ("validationErrors", m_aValidationErrors)
                                       .toString ();
  }
}
