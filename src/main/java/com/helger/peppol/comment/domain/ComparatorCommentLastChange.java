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
