package com.helger.peppol.crm;

import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.appbasics.object.IObject;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.name.IHasDisplayText;
import com.helger.masterdata.person.ESalutation;

/**
 * The read-only interface for a single CRM subscriber, that is subscribed to
 * multiple CRM groups.
 *
 * @author Philip Helger
 */
public interface ICRMSubscriber extends IObject, IHasDisplayText
{
  /**
   * Create a unified, all lowercase email address for easy comparison
   *
   * @param sEmailAddress
   *        Source email address. May not be <code>null</code>.
   * @return Unified email address.
   */
  @Nonnull
  static String getUnifiedEmailAddress (@Nonnull final String sEmailAddress)
  {
    return sEmailAddress.trim ().toLowerCase (Locale.US);
  }

  @Nullable
  ESalutation getSalutation ();

  @Nullable
  String getSalutationID ();

  @Nullable
  String getSalutationDisplayName (@Nonnull Locale aContentLocale);

  @Nonnull
  @Nonempty
  String getName ();

  @Nonnull
  @Nonempty
  String getEmailAddress ();

  @Nonnull
  @ReturnsMutableCopy
  Set <ICRMGroup> getAllAssignedGroups ();

  @Nonnegative
  int getAssignedGroupCount ();

  boolean isAssignedToGroup (@Nullable ICRMGroup aCRMGroup);
}
