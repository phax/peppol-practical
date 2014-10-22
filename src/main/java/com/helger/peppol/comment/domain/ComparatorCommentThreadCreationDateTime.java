package com.helger.peppol.comment.domain;

import javax.annotation.Nonnull;

import com.helger.commons.compare.AbstractComparator;
import com.helger.commons.compare.ESortOrder;

/**
 * Comparator for {@link ICommentThread} objects based the creation time
 *
 * @author Philip Helger
 */
public class ComparatorCommentThreadCreationDateTime extends AbstractComparator <ICommentThread>
{
  public ComparatorCommentThreadCreationDateTime ()
  {}

  public ComparatorCommentThreadCreationDateTime (@Nonnull final ESortOrder eSortOrder)
  {
    super (eSortOrder);
  }

  @Override
  protected int mainCompare (@Nonnull final ICommentThread aElement1, @Nonnull final ICommentThread aElement2)
  {
    return aElement1.getInitialComment ()
                    .getCreationDateTime ()
                    .compareTo (aElement2.getInitialComment ().getCreationDateTime ());
  }
}
