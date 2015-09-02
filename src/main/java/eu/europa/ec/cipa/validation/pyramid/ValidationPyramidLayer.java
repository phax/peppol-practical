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
import com.helger.commons.string.ToStringGenerator;

import eu.europa.ec.cipa.validation.generic.EXMLValidationType;
import eu.europa.ec.cipa.validation.generic.IXMLValidator;
import eu.europa.ec.cipa.validation.rules.IValidationLevel;

/**
 * Represent a single validation layer within the validation pyramid.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public class ValidationPyramidLayer
{
  private final IValidationLevel m_aValidationLevel;
  private final IXMLValidator m_aValidator;
  private final boolean m_bStopValidatingOnError;

  /**
   * Constructor
   * 
   * @param aValidationLevel
   *        The validation level of this layer. May not be <code>null</code>.
   * @param aValidator
   *        The validator implementation. May not be <code>null</code>.
   * @param bStopValidatingOnError
   *        indicates whether the validation should be stopped if validation
   *        fails on this layer.
   */
  public ValidationPyramidLayer (@Nonnull final IValidationLevel aValidationLevel,
                                 @Nonnull final IXMLValidator aValidator,
                                 final boolean bStopValidatingOnError)
  {
    ValueEnforcer.notNull (aValidationLevel, "Level");
    ValueEnforcer.notNull (aValidator, "Validator");

    m_aValidationLevel = aValidationLevel;
    m_aValidator = aValidator;
    m_bStopValidatingOnError = bStopValidatingOnError;
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
   * @return The validation type - XML Schema or Schematron. Never
   *         <code>null</code>.
   */
  @Nonnull
  public EXMLValidationType getValidationType ()
  {
    return m_aValidator.getValidationType ();
  }

  /**
   * @return The validator itself. Never <code>null</code>.
   */
  @Nonnull
  public IXMLValidator getValidator ()
  {
    return m_aValidator;
  }

  /**
   * @return <code>true</code> to indicate that the validation pyramid should
   *         not be executed any further if (at least) an error occurs in this
   *         validation layer.
   */
  public boolean isStopValidatingOnError ()
  {
    return m_bStopValidatingOnError;
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("level", m_aValidationLevel)
                                       .append ("validator", m_aValidator)
                                       .append ("stopValidatingOnError", m_bStopValidatingOnError)
                                       .toString ();
  }
}
