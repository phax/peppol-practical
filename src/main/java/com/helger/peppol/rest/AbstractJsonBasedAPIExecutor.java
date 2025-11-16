/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.peppol.rest;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.rt.StackTraceHelper;
import com.helger.base.state.ETriState;
import com.helger.diagnostics.error.IError;
import com.helger.diagnostics.error.level.EErrorLevel;
import com.helger.diagnostics.error.level.IErrorLevel;
import com.helger.json.IJsonObject;
import com.helger.json.JsonObject;


public abstract class AbstractJsonBasedAPIExecutor extends AbstractPPAPIExecutor
{
  @NonNull
  protected static String getErrorLevel (@NonNull final IErrorLevel aErrorLevel)
  {
    if (aErrorLevel.isGE (EErrorLevel.ERROR))
      return "ERROR";
    if (aErrorLevel.isGE (EErrorLevel.WARN))
      return "WARN";
    return "SUCCESS";
  }

  @NonNull
  protected static String getTriState (@NonNull final ETriState eTriState)
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

  @NonNull
  protected static IJsonObject createItem (@NonNull final IErrorLevel aErrorLevel,
                                           @Nullable final String sErrorID,
                                           @Nullable final String sErrorFieldName,
                                           @Nullable final String sErrorLocation,
                                           @NonNull final String sErrorText,
                                           @Nullable final Throwable t)
  {
    return new JsonObject ().add ("errorLevel", getErrorLevel (aErrorLevel))
                            .addIfNotNull ("errorID", sErrorID)
                            .addIfNotNull ("errorFieldName", sErrorFieldName)
                            .addIfNotNull ("errorLocation", sErrorLocation)
                            .add ("errorText", sErrorText)
                            .addIfNotNull ("exception", getStackTrace (t));
  }

  @NonNull
  protected static IJsonObject createItem (@NonNull final IError aError, @NonNull final Locale aDisplayLocale)
  {
    return createItem (aError.getErrorLevel (),
                       aError.getErrorID (),
                       aError.getErrorFieldName (),
                       aError.hasErrorLocation () ? aError.getErrorLocation ().getAsString () : null,
                       aError.getErrorText (aDisplayLocale),
                       aError.getLinkedException ());
  }
}
