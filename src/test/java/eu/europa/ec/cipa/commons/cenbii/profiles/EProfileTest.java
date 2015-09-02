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
package eu.europa.ec.cipa.commons.cenbii.profiles;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Locale;

import org.junit.Test;


/**
 * Test class for class {@link EProfile}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class EProfileTest {
  @Test
  public void testBasic () {
    for (final EProfile eProfile : EProfile.values ()) {
      assertNotNull (eProfile.getGroup ());
      assertNotNull (eProfile.getDisplayText (Locale.ENGLISH));
      assertTrue (eProfile.getNumber () > 0);
      assertNotNull (eProfile.getAllCollaborations ());
      assertFalse (eProfile.getAllCollaborations ().isEmpty ());
      assertSame (eProfile, EProfile.valueOf (eProfile.name ()));
      eProfile.isInCoreSupported ();
    }
  }

  @Test
  public void testGetAllProfilesWithCollaboration () {
    for (final ECollaboration eCollaboration : ECollaboration.values ()) {
      final List <EProfile> aList = EProfile.getAllProfilesWithCollaboration (eCollaboration);
      assertNotNull (aList);
      assertTrue (aList.size () > 0);
    }

    try {
      EProfile.getAllProfilesWithCollaboration (null);
      fail ();
    }
    catch (final NullPointerException ex) {}
  }
}
