/**
 * Copyright (C) 2014-2020 Philip Helger (www.helger.com)
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
package com.helger.peppol.comment.ui;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Translatable;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.text.display.IHasDisplayTextWithArgs;
import com.helger.commons.text.resolve.DefaultTextResolver;
import com.helger.commons.text.util.TextHelper;

@Translatable
public enum ECommentText implements IHasDisplayTextWithArgs
{
  MSG_IS_DELETED ("[gelöscht] ", "[deleted] "),
  MSG_BY (" von ", " by "),
  MSG_SEPARATOR_AUTHOR_TITLE (" - ", " - "),
  TOOLTIP_RESPONSE ("Auf diesen Kommentar antworten", "Reply to this comment"),
  TOOLTIP_DELETE ("Diesen Kommentar löschen", "Delete this comment"),
  TOOLTIP_HOST ("Quell-Host: {0}", "Original host: {0}"),
  MSG_LAST_MODIFICATION ("Letzte Änderung: {0}", "Last modification: {0}"),
  MSG_EDITED_AND_LAST_MODIFICATION ("{0} mal bearbeitet. Zuletzt: {1}", "Edited {0} times. Last modification: {1}"),
  MSG_CREATE_COMMENT ("Neuen Kommentar erstellen", "Create new comment"),
  MSG_LOGIN_TO_COMMENT ("Sie müssen angemeldet sein, um einen Kommentar zu verfassen!", "You must be logged in to post a comment!"),

  MSG_FIELD_AUTHOR ("Ihr Name", "Your name"),
  DESC_FIELD_AUTHOR ("Geben Sie Ihren Namen an. Ohne den Namen kann der Kommentar nicht gespeichert werden",
                     "Provide your name here. The comment cannot be saved without a name"),
  MSG_FIELD_TITLE ("Titel", "Title"),
  DESC_FIELD_TITLE ("Geben Sie den Titel des Kommentars an. Dieser kann auch leer bleiben",
                    "Provide the title/subject of your comment. The title is optional and you're not required to insert any text here"),
  MSG_FIELD_TEXT ("Ihr Kommentar", "Your comment"),
  DESC_FIELD_TEXT ("Geben Sie Ihren Kommentar in diesem Feld an. Es können mehrere Zeilen verwendet werden, HTML Code wird jedoch nicht interpretiert",
                   "Provide your comment here. It can have multiple lines but HTML code is not interpreted at all!"),

  MSG_COMMENT_SAVE_SUCCESS ("Ihr Kommentar wurde erfolgreich gespeichert!", "Your comment was successfully saved!"),
  MSG_COMMENT_SAVE_FAILURE ("Ihr Kommentar konnte nicht gespeichert werden!", "Your comment could not be saved!"),
  MSG_ERR_COMMENT_NO_TEXT ("Der Text darf nicht leer sein. Geben Sie einen Text an!",
                           "The comment text may not be empty. Provide a comment text!"),
  MSG_ERR_COMMENT_NO_AUTHOR ("Ihr Name darf nicht leer sein. Geben Sie Ihre Namen an!", "Your name may not be empty. Provide your name!"),

  MSG_COMMENT_DELETE_SUCCESS ("Der Kommentar wurde erfolgreich gelöscht!", "The comment was successfully deleted!"),
  MSG_COMMENT_DELETE_FAILURE ("Der Kommentar konnte nicht gelöscht werden!", "The comment could not be deleted!");

  private final IMultilingualText m_aTP;

  private ECommentText (@Nonnull final String sDE, @Nonnull final String sEN)
  {
    m_aTP = TextHelper.create_DE_EN (sDE, sEN);
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return DefaultTextResolver.getTextStatic (this, m_aTP, aContentLocale);
  }
}
