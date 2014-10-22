package com.helger.peppol.comment.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.appbasics.mock.AppBasicTestRule;
import com.helger.commons.microdom.convert.MicroTypeConverter;
import com.helger.commons.microdom.serialize.MicroWriter;
import com.helger.commons.mock.PHTestUtils;

/**
 * Unit test class for class {@link CommentThread}.
 *
 * @author Philip Helger
 */
public final class CommentThreadTest
{
  @Rule
  public TestRule m_aRule = new AppBasicTestRule ();

  @Test
  public void testBasic ()
  {
    final IComment aInitialComment = new Comment ("userid", "creatorname", "title", "text");
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
    aCommentThread.addComment (aInitialComment, new Comment ("userid", "creatorname", "title2", "text2"));
    assertEquals (2, aCommentThread.getTotalCommentCount ());

    // Add another comments into the thread
    final IComment aComment3 = aCommentThread.addComment (aInitialComment, new Comment ("userid",
                                                                                        "creatorname",
                                                                                        "title3",
                                                                                        "text3"));
    assertEquals (3, aCommentThread.getTotalCommentCount ());
    assertSame (aComment3, aCommentThread.getCommentOfID (aComment3.getID ()));

    if (false)
      System.out.println (MicroWriter.getXMLString (MicroTypeConverter.convertToMicroElement (aCommentThread,
                                                                                              "commentthread")));

    PHTestUtils.testMicroTypeConversion (aCommentThread);
  }
}
