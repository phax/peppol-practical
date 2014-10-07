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
