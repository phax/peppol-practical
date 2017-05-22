/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import com.helger.commons.type.ObjectType;
import com.helger.photon.basic.app.dao.impl.DAOException;
import com.helger.photon.basic.mock.PhotonBasicWebTestRule;

/**
 * Unit test class for class {@link CommentThreadObjectTypeManager}.
 *
 * @author Philip Helger
 */
public final class CommentThreadObjectTypeManagerTest
{
  @Rule
  public TestRule m_aRule = new PhotonBasicWebTestRule ();

  @Test
  public void testBasic () throws DAOException
  {
    final CommentThreadObjectTypeManager aMgr = new CommentThreadObjectTypeManager (new ObjectType ("mock"));

    final Comment aComment = new Comment ("unittest", ECommentState.APPROVED, "userid", "creatorname", "title", "text");
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
      aMgr.addCommentToThread (sOwningObjectID,
                               sThreadID,
                               sThreadID,
                               new Comment ("unittest",
                                            ECommentState.APPROVED,
                                            "userid",
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
      aMgr.createNewThread (sOwningObjectID,
                            new Comment ("unittest",
                                         ECommentState.APPROVED,
                                         "userid2",
                                         "creatorname2",
                                         "title4",
                                         "text4"));
      assertEquals (2, aMgr.getAllCommentThreadsOfObject (sOwningObjectID).size ());
    }
    finally
    {
      aMgr.removeAllCommentThreadsOfObject (sOwningObjectID);
    }
  }
}
