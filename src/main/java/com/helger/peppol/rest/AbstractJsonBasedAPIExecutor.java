package com.helger.peppol.rest;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.error.IError;
import com.helger.commons.error.level.EErrorLevel;
import com.helger.commons.error.level.IErrorLevel;
import com.helger.commons.lang.StackTraceHelper;
import com.helger.commons.state.ETriState;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;
import com.helger.photon.api.IAPIExecutor;

public abstract class AbstractJsonBasedAPIExecutor implements IAPIExecutor
{
  @Nonnull
  protected static String getErrorLevel (@Nonnull final IErrorLevel aErrorLevel)
  {
    if (aErrorLevel.isGE (EErrorLevel.ERROR))
      return "ERROR";
    if (aErrorLevel.isGE (EErrorLevel.WARN))
      return "WARN";
    return "SUCCESS";
  }

  @Nonnull
  protected static String getTriState (@Nonnull final ETriState eTriState)
  {
    if (eTriState.isTrue ())
      return "TRUE";
    if (eTriState.isFalse ())
      return "FALSE";
    return "UNDEFINED";
  }

  @Nullable
  protected static IJsonObject getStackTrace (@Nullable final Throwable t)
  {
    if (t == null)
      return null;
    return new JsonObject ().add ("class", t.getClass ().getName ())
                            .addIfNotNull ("message", t.getMessage ())
                            .add ("stackTrace", StackTraceHelper.getStackAsString (t));
  }

  @Nonnull
  protected static IJsonObject createItem (@Nonnull final IErrorLevel aErrorLevel,
                                           @Nullable final String sErrorID,
                                           @Nullable final String sErrorFieldName,
                                           @Nullable final String sErrorLocation,
                                           @Nonnull final String sErrorText,
                                           @Nullable final Throwable t)
  {
    return new JsonObject ().add ("errorLevel", getErrorLevel (aErrorLevel))
                            .addIfNotNull ("errorID", sErrorID)
                            .addIfNotNull ("errorFieldName", sErrorFieldName)
                            .addIfNotNull ("errorLocation", sErrorLocation)
                            .add ("errorText", sErrorText)
                            .addIfNotNull ("exception", getStackTrace (t));
  }

  @Nonnull
  protected static IJsonObject createItem (@Nonnull final IError aError, @Nonnull final Locale aDisplayLocale)
  {
    return createItem (aError.getErrorLevel (),
                       aError.getErrorID (),
                       aError.getErrorFieldName (),
                       aError.hasErrorLocation () ? aError.getErrorLocation ().getAsString () : null,
                       aError.getErrorText (aDisplayLocale),
                       aError.getLinkedException ());
  }
}
