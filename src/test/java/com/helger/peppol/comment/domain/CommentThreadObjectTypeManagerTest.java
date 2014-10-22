package com.helger.peppol.comment.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.appbasics.app.dao.impl.DAOException;
import com.helger.appbasics.mock.AppBasicTestRule;
import com.helger.commons.type.ObjectType;

/**
 * Unit test class for class {@link CommentThreadObjectTypeManager}.
 *
 * @author Philip Helger
 */
public final class CommentThreadObjectTypeManagerTest
{
  @Rule
  public TestRule m_aRule = new AppBasicTestRule ();

  @Test
  public void testBasic () throws DAOException
  {
    final CommentThreadObjectTypeManager aMgr = new CommentThreadObjectTypeManager (new ObjectType ("mock"));

    final Comment aComment = new Comment ("userid", "creatorname", "title", "text");
    assertNotNull (aComment.getCreationDateTime ());
    assertNull (aComment.getLastModificationDateTime ());
    assertEquals ("userid", aComment.getUserID ());
    assertEquals ("creatorname", aComment.getCreatorName ());
    assertFalse (aComment.isDeleted ());
    assertEquals ("title", aComment.getTitle ());
    assertEquals ("text", aComment.getText ());

    final String sOwningObjectID = "blafoofasel";
    try
    {
      assertNotNull (aMgr.getAllCommentThreadsOfObject (sOwningObjectID));
      assertEquals (0, aMgr.getAllCommentThreadsOfObject (sOwningObjectID).size ());

      // Create new thread
      final String sThreadID = aMgr.createNewThread (sOwningObjectID, aComment).getID ();

      assertEquals (1, aMgr.getAllCommentThreadsOfObject (sOwningObjectID).size ());

      // Add another comments into the thread
      aMgr.addCommentToThread (sOwningObjectID, sThreadID, sThreadID, new Comment ("userid",
                                                                                   "creatorname",
                                                                                   "title2",
                                                                                   "text2"));

      assertEquals (1, aMgr.getAllCommentThreadsOfObject (sOwningObjectID).size ());

      // Adding the same should fail
      try
      {
        aMgr.createNewThread (sOwningObjectID, aComment);
        fail ();
      }
      catch (final IllegalArgumentException ex)
      {}

      // Create a second thread
      aMgr.createNewThread (sOwningObjectID, new Comment ("userid2", "creatorname2", "title4", "text4"));
      assertEquals (2, aMgr.getAllCommentThreadsOfObject (sOwningObjectID).size ());
    }
    finally
    {
      aMgr.removeAllCommentThreadsOfObject (sOwningObjectID);
    }
  }
}
