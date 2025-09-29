/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.photon.app.mock.PhotonAppTestRule;
import com.helger.unittest.support.TestHelper;
import com.helger.xml.microdom.convert.MicroTypeConverter;
import com.helger.xml.microdom.serialize.MicroWriter;
import com.helger.xml.mock.XMLTestHelper;

/**
 * Unit test class for class {@link Comment}.
 *
 * @author Philip Helger
 */
public final class CommentTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (CommentTest.class);
  @Rule
  public TestRule m_aRule = new PhotonAppTestRule ();

  @Test
  public void testBasic ()
  {
    final Comment aComment = new Comment ("unittest", ECommentState.APPROVED, "userid", "creatorname", "title", "text");
    assertNotNull (aComment.getCreationDateTime ());
    assertNull (aComment.getLastModificationDateTime ());
    assertEquals ("userid", aComment.getUserID ());
    assertEquals ("creatorname", aComment.getCreatorName ());
    assertFalse (aComment.isDeleted ());
    assertEquals ("title", aComment.getTitle ());
    assertEquals ("text", aComment.getText ());

    if (false)
      LOGGER.info (MicroWriter.getNodeAsString (MicroTypeConverter.convertToMicroElement (aComment, "comment")));

    TestHelper.testDefaultSerialization (aComment);
    XMLTestHelper.testMicroTypeConversion (aComment);
  }
}
