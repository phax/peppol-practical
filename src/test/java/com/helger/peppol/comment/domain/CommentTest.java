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
