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
package com.helger.peppol.comment.ui;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class CommentCreationFields
{
  private final String m_sAuthor;
  private final String m_sTitle;
  private final String m_sText;

  public CommentCreationFields (@Nullable final String sAuthor,
                                @Nullable final String sTitle,
                                @Nullable final String sText)
  {
    m_sAuthor = sAuthor;
    m_sTitle = sTitle;
    m_sText = sText;
  }

  @Nullable
  public String getAuthor ()
  {
    return m_sAuthor;
  }

  @Nullable
  public String getTitle ()
  {
    return m_sTitle;
  }

  @Nullable
  public String getText ()
  {
    return m_sText;
  }
}
