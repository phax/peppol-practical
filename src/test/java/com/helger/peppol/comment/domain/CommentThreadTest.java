/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
import static org.junit.Assert.assertSame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.serialize.MicroWriter;
import com.helger.commons.mock.CommonsTestHelper;
import com.helger.photon.basic.mock.PhotonBasicTestRule;

/**
 * Unit test class for class {@link CommentThread}.
 *
 * @author Philip Helger
 */
public final class CommentThreadTest
{
  @Rule
  public TestRule m_aRule = new PhotonBasicTestRule ();

  @Test
  public void testBasic ()
  {
    final IComment aInitialComment = new Comment ("unittest",
                                                  ECommentState.APPROVED,
                                                  "userid",
                                                  "creatorname",
                                                  "title",
                                                  "text");
    assertNotNull (aInitialComment.getCreationDateTime ());
    assertNull (aInitialComment.getLastModificationDateTime ());
    assertEquals ("userid", aInitialComment.getUserID ());
    assertEquals ("creatorname", aInitialComment.getCreatorName ());
    assertFalse (aInitialComment.isDeleted ());
    assertEquals ("title", aInitialComment.getTitle ());
    assertEquals ("text", aInitialComment.getText ());

    final ICommentThread aCommentThread = new CommentThread (aInitialComment);
    assertEquals (aInitialComment.getID (), aCommentThread.getID ());
    assertEquals (1, aCommentThread.getTotalCommentCount ());

    // Add another comments into the thread
    aCommentThread.addComment (aInitialComment,
                               new Comment ("unittest",
                                            ECommentState.APPROVED,
                                            "userid",
                                            "creatorname",
                                            "title2",
                                            "text2"));
    assertEquals (2, aCommentThread.getTotalCommentCount ());

    // Add another comments into the thread
    final IComment aComment3 = aCommentThread.addComment (aInitialComment,
                                                          new Comment ("unittest",
                                                                       ECommentState.APPROVED,
                                                                       "userid",
                                                                       "creatorname",
                                                                       "title3",
                                                                       "text3"));
    assertEquals (3, aCommentThread.getTotalCommentCount ());
    assertSame (aComment3, aCommentThread.getCommentOfID (aComment3.getID ()));

    if (false)
      System.out.println (MicroWriter.getXMLString (MicroTypeConverter.convertToMicroElement (aCommentThread,
                                                                                              "commentthread")));

    CommonsTestHelper.testMicroTypeConversion (aCommentThread);
  }
}
