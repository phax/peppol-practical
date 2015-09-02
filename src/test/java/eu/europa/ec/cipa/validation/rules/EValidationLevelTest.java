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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.string.StringHelper;

/**
 * Test class for class {@link EValidationLevel}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class EValidationLevelTest
{
  @Test
  public void testAll ()
  {
    for (final EValidationLevel eLevel : EValidationLevel.values ())
    {
      assertTrue (StringHelper.hasText (eLevel.getID ()));
      assertSame (eLevel, EValidationLevel.getFromIDOrNull (eLevel.getID ()));

      assertTrue (eLevel.isHigherOrEqualLevelThan (EValidationLevel.TECHNICAL_STRUCTURE));
      assertFalse (EValidationLevel.TECHNICAL_STRUCTURE.isHigherOrEqualLevelThan (EValidationLevel.PROFILE_REQUIREMENTS));

      assertFalse (eLevel.isLowerLevelThan (EValidationLevel.TECHNICAL_STRUCTURE));
      assertTrue (EValidationLevel.TECHNICAL_STRUCTURE.isLowerLevelThan (EValidationLevel.PROFILE_REQUIREMENTS));

      assertTrue (eLevel.isLowerOrEqualLevelThan (EValidationLevel.ENTITY_SPECIFC));
      assertFalse (EValidationLevel.ENTITY_SPECIFC.isLowerOrEqualLevelThan (EValidationLevel.PROFILE_REQUIREMENTS));

      assertFalse (eLevel.isHigherLevelThan (EValidationLevel.ENTITY_SPECIFC));
      assertTrue (EValidationLevel.ENTITY_SPECIFC.isHigherLevelThan (EValidationLevel.PROFILE_REQUIREMENTS));
    }
  }

  @Test
  public void testSorting ()
  {
    // Test order
    List <EValidationLevel> aOrdered = EValidationLevel.getAllLevelsInValidationOrder ();
    assertNotNull (aOrdered);
    assertEquals (EValidationLevel.values ().length, aOrdered.size ());
    assertEquals (EValidationLevel.TECHNICAL_STRUCTURE, CollectionHelper.getFirstElement (aOrdered));

    // One element only
    aOrdered = EValidationLevel.getAllLevelsInValidationOrder (EValidationLevel.PROFILE_REQUIREMENTS);
    assertNotNull (aOrdered);
    assertEquals (1, aOrdered.size ());
    assertEquals (EValidationLevel.PROFILE_REQUIREMENTS, CollectionHelper.getFirstElement (aOrdered));

    // Two element only
    aOrdered = EValidationLevel.getAllLevelsInValidationOrder (EValidationLevel.ENTITY_SPECIFC,
                                                               EValidationLevel.PROFILE_REQUIREMENTS);
    assertNotNull (aOrdered);
    assertEquals (2, aOrdered.size ());
    assertEquals (EValidationLevel.PROFILE_REQUIREMENTS, CollectionHelper.getFirstElement (aOrdered));
    assertEquals (EValidationLevel.ENTITY_SPECIFC, CollectionHelper.getLastElement (aOrdered));
  }

  @Test
  public void testCountrySpecific ()
  {
    final List <EValidationLevel> aCountrySpecific = EValidationLevel.getAllLevelsSupportingCountrySpecificArtefacts ();
    assertNotNull (aCountrySpecific);
    assertFalse (aCountrySpecific.isEmpty ());
    final List <EValidationLevel> aNotCountrySpecific = EValidationLevel.getAllLevelsNotSupportingCountrySpecificArtefacts ();
    assertNotNull (aNotCountrySpecific);
    assertFalse (aNotCountrySpecific.isEmpty ());

    // No intersection!
    assertTrue (CollectionHelper.getIntersected (aCountrySpecific, aNotCountrySpecific).isEmpty ());
    assertEquals (EValidationLevel.values ().length, aCountrySpecific.size () + aNotCountrySpecific.size ());
  }
}
