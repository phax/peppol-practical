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
package eu.europa.ec.cipa.test.error;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.ToStringGenerator;

public class TestResource
{
  private final IReadableResource m_aRes;
  private final Set <AbstractErrorDefinition> m_aExpectedErrors = new HashSet <AbstractErrorDefinition> ();

  public TestResource (@Nonnull final IReadableResource aRes,
                       @Nullable final Set <AbstractErrorDefinition> aExpectedErrors)
  {
    ValueEnforcer.notNull (aRes, "Resource");

    m_aRes = aRes;
    if (aExpectedErrors != null)
      m_aExpectedErrors.addAll (aExpectedErrors);
  }

  /**
   * @return The XML resource path
   */
  @Nonnull
  public IReadableResource getResource ()
  {
    return m_aRes;
  }

  /**
   * @return The filename of the underlying resources
   */
  @Nonnull
  public String getFilename ()
  {
    return m_aRes.getPath ();
  }

  /**
   * @return The expected validation errors
   */
  @Nonnull
  @ReturnsMutableCopy
  public Set <AbstractErrorDefinition> getAllExpectedErrors ()
  {
    return CollectionHelper.newSet (m_aExpectedErrors);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (null).append ("resource", m_aRes)
                                       .append ("expectedErrors", m_aExpectedErrors)
                                       .toString ();
  }
}
