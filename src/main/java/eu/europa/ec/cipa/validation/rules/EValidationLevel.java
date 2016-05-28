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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.lang.EnumHelper;

/**
 * This enum represents the validation hierarchy. The hierarchy must be iterated
 * from top to bottom.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public enum EValidationLevel implements IValidationLevel
{
  /**
   * Technical structure validation (e.g. XML schema) - never country specific
   */
  TECHNICAL_STRUCTURE ("technical", 10),

  /**
   * Validation rules based on BII transaction requirements - never country
   * specific
   */
  TRANSACTION_REQUIREMENTS ("transaction", 20),

  /**
   * Validation rules based on BII profile (=process) requirements - never
   * country specific
   */
  PROFILE_REQUIREMENTS ("profile", 30),

  /** Validation rules based on legal obligations - maybe country specific */
  LEGAL_REQUIREMENTS ("legal", 40),

  /** Industry specific validation rules - maybe country specific */
  INDUSTRY_SPECIFIC ("industry", 50),

  /**
   * Entity (=company) specific validation rules - this level represents
   * bilateral agreements - maybe country specific
   */
  ENTITY_SPECIFC ("entity", 60);

  private final String m_sID;
  private final int m_nLevel;

  private EValidationLevel (@Nonnull @Nonempty final String sID, @Nonnegative final int nLevel)
  {
    m_sID = sID;
    m_nLevel = nLevel;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnegative
  public int getLevel ()
  {
    return m_nLevel;
  }

  public boolean isLowerLevelThan (@Nonnull final IValidationLevel aLevel)
  {
    return m_nLevel < aLevel.getLevel ();
  }

  public boolean isLowerOrEqualLevelThan (@Nonnull final IValidationLevel aLevel)
  {
    return m_nLevel <= aLevel.getLevel ();
  }

  public boolean isHigherLevelThan (@Nonnull final IValidationLevel aLevel)
  {
    return m_nLevel > aLevel.getLevel ();
  }

  public boolean isHigherOrEqualLevelThan (@Nonnull final IValidationLevel aLevel)
  {
    return m_nLevel >= aLevel.getLevel ();
  }

  /**
   * @return <code>true</code> if this level can have country specific
   *         artefacts. <code>false</code> if this level is country independent!
   */
  public boolean canHaveCountrySpecificArtefacts ()
  {
    return isHigherOrEqualLevelThan (LEGAL_REQUIREMENTS);
  }

  @Nullable
  public static IValidationLevel getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EValidationLevel.class, sID);
  }

  /**
   * @return All validation levels in the order they must be executed. Neither
   *         <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public static List <EValidationLevel> getAllLevelsInValidationOrder ()
  {
    return CollectionHelper.newList (values ());
  }

  /**
   * Get the passed validation levels in the order they should be executed.
   *
   * @param aLevels
   *        The validation levels to be ordered
   * @return All validation levels in the order they must be executed.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <EValidationLevel> getAllLevelsInValidationOrder (@Nullable final EValidationLevel... aLevels)
  {
    return new CommonsArrayList<> (aLevels).getSortedInline (Comparator.comparingInt (EValidationLevel::getLevel));
  }

  /**
   * Get a list of all validation levels that support country specific
   * artefacts.
   *
   * @return All validation levels having country specific artefacts. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <EValidationLevel> getAllLevelsSupportingCountrySpecificArtefacts ()
  {
    final List <EValidationLevel> ret = new ArrayList <EValidationLevel> ();
    for (final EValidationLevel eValidationLevel : EValidationLevel.values ())
      if (eValidationLevel.canHaveCountrySpecificArtefacts ())
        ret.add (eValidationLevel);
    return ret;
  }

  /**
   * Get a list of all validation levels that do NOT support country specific
   * artefacts.
   *
   * @return All validation levels NOT having country specific artefacts. Never
   *         <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <EValidationLevel> getAllLevelsNotSupportingCountrySpecificArtefacts ()
  {
    final List <EValidationLevel> ret = new ArrayList <EValidationLevel> ();
    for (final EValidationLevel eValidationLevel : EValidationLevel.values ())
      if (!eValidationLevel.canHaveCountrySpecificArtefacts ())
        ret.add (eValidationLevel);
    return ret;
  }
}
