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
package com.helger.peppol.crm;

import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.text.display.IHasDisplayText;
import com.helger.masterdata.person.ESalutation;
import com.helger.photon.basic.object.IBusinessObject;

/**
 * The read-only interface for a single CRM subscriber, that is subscribed to
 * multiple CRM groups.
 *
 * @author Philip Helger
 */
public interface ICRMSubscriber extends IBusinessObject, IHasDisplayText
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
