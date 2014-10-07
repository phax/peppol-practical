package com.helger.peppol.crm;

import javax.annotation.Nonnull;

import com.helger.appbasics.object.IObject;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.name.IHasDisplayName;

/**
 * The read-only interface for a single CRM group.
 * 
 * @author Philip Helger
 */
public interface ICRMGroup extends IObject, IHasDisplayName
{
  /**
   * @return The default sender email address for this group.
   */
  @Nonnull
  @Nonempty
  String getSenderEmailAddress ();
}
