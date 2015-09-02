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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;

/**
 * Base interface for a single validation level.
 * 
 * @author Philip Helger
 * @see EValidationLevel
 */
public interface IValidationLevel extends IHasID <String>
{
  /**
   * @return The ID of this level. Mainly to be used for serialization.
   */
  @Nonnull
  @Nonempty
  String getID ();

  /**
   * @return The int level representation of this level. The lower the number
   *         the more generic are the validation rules. This number is only
   *         present for easy ordering of the validation level and does not
   *         serve any other purpose.
   */
  @Nonnegative
  int getLevel ();

  /**
   * Check if this level is lower than the passed level.
   * 
   * @param aLevel
   *        The level to check against. May not be <code>null</code>.
   * @return <code>true</code> if this level is lower than the passed level,
   *         <code>false</code> otherwise.
   */
  boolean isLowerLevelThan (@Nonnull IValidationLevel aLevel);

  /**
   * Check if this level is lower or equal than the passed level.
   * 
   * @param aLevel
   *        The level to check against. May not be <code>null</code>.
   * @return <code>true</code> if this level is lower or equal than the passed
   *         level, <code>false</code> otherwise.
   */
  boolean isLowerOrEqualLevelThan (@Nonnull IValidationLevel aLevel);

  /**
   * Check if this level is higher than the passed level.
   * 
   * @param aLevel
   *        The level to check against. May not be <code>null</code>.
   * @return <code>true</code> if this level is higher than the passed level,
   *         <code>false</code> otherwise.
   */
  boolean isHigherLevelThan (@Nonnull IValidationLevel aLevel);

  /**
   * Check if this level is higher or equal than the passed level.
   * 
   * @param aLevel
   *        The level to check against. May not be <code>null</code>.
   * @return <code>true</code> if this level is higher or equal than the passed
   *         level, <code>false</code> otherwise.
   */
  boolean isHigherOrEqualLevelThan (@Nonnull IValidationLevel aLevel);

  /**
   * @return <code>true</code> if this level can have country specific
   *         artefacts. <code>false</code> if this level is country independent!
   */
  boolean canHaveCountrySpecificArtefacts ();
}
