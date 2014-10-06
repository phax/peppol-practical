package com.helger.peppol.crm;

import javax.annotation.Nonnull;

import com.helger.appbasics.object.IObject;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.name.IHasDisplayName;

public interface ICRMGroup extends IObject, IHasDisplayName
{
  @Nonnull
  @Nonempty
  String getSenderEmailAddress ();
}
