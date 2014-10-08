/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.peppol.crm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.mock.PHTestUtils;
import com.helger.commons.string.StringHelper;
import com.helger.webbasics.mock.WebBasicTestRule;

/**
 * Unit test class for class {@link CRMGroup}.
 * 
 * @author Philip Helger
 */
public final class CRMGroupTest
{
  @Rule
  public TestRule m_aRule = new WebBasicTestRule ();

  @Test
  public void testBasic ()
  {
    final CRMGroup aGroup = new CRMGroup ("Name", "bla@foo.com");
    assertTrue (StringHelper.hasText (aGroup.getID ()));
    assertEquals ("Name", aGroup.getDisplayName ());
    assertEquals ("bla@foo.com", aGroup.getSenderEmailAddress ());
    // Only ID is relevant!
    PHTestUtils.testDefaultImplementationWithDifferentContentObject (aGroup, new CRMGroup ("Name", "bla@foo.com"));
    PHTestUtils.testMicroTypeConversion (aGroup);
  }
}
