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

import javax.annotation.Nonnull;

import com.helger.commons.compare.AbstractComparator;
import com.helger.commons.compare.ESortOrder;

public class ComparatorCommentLastChange extends AbstractComparator <IComment>
{
  public ComparatorCommentLastChange ()
  {
    super ();
  }

  public ComparatorCommentLastChange (@Nonnull final ESortOrder eSortOrder)
  {
    super (eSortOrder);
  }

  @Override
  protected int mainCompare (@Nonnull final IComment aElement1, @Nonnull final IComment aElement2)
  {
    return aElement1.getLastChangeDateTime ().compareTo (aElement2.getLastChangeDateTime ());
  }
}
