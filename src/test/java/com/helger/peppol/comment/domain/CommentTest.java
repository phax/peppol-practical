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
package com.helger.peppol.comment.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.appbasics.mock.AppBasicTestRule;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.serialize.MicroWriter;
import com.helger.commons.mock.PHTestUtils;

/**
 * Unit test class for class {@link Comment}.
 *
 * @author Philip Helger
 */
public final class CommentTest
{
  @Rule
  public TestRule m_aRule = new AppBasicTestRule ();

  @Test
  public void testBasic ()
  {
    final Comment aComment = new Comment ("userid", "creatorname", "title", "text");
    assertNotNull (aComment.getCreationDateTime ());
    assertNull (aComment.getLastModificationDateTime ());
    assertEquals ("userid", aComment.getUserID ());
    assertEquals ("creatorname", aComment.getCreatorName ());
    assertFalse (aComment.isDeleted ());
    assertEquals ("title", aComment.getTitle ());
    assertEquals ("text", aComment.getText ());

    if (false)
      System.out.println (MicroWriter.getXMLString (MicroTypeConverter.convertToMicroElement (aComment, "comment")));

    PHTestUtils.testDefaultSerialization (aComment);
    PHTestUtils.testMicroTypeConversion (aComment);
  }
}
