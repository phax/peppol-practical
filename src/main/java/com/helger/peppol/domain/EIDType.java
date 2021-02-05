/**
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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
package com.helger.peppol.domain;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.error.IError;
import com.helger.commons.error.SingleError;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.name.IHasDisplayName;
import com.helger.commons.regex.RegExHelper;
import com.helger.commons.string.StringHelper;
import com.helger.peppolid.CIdentifier;
import com.helger.peppolid.factory.PeppolIdentifierFactory;
import com.helger.peppolid.peppol.PeppolIdentifierHelper;
import com.helger.peppolid.peppol.doctype.IPeppolDocumentTypeIdentifierParts;
import com.helger.peppolid.peppol.doctype.IPeppolPredefinedDocumentTypeIdentifier;
import com.helger.peppolid.peppol.doctype.PeppolDocumentTypeIdentifierParts;
import com.helger.peppolid.peppol.doctype.PredefinedDocumentTypeIdentifierManager;
import com.helger.peppolid.peppol.pidscheme.EPredefinedParticipantIdentifierScheme;
import com.helger.peppolid.peppol.pidscheme.IParticipantIdentifierScheme;
import com.helger.peppolid.peppol.pidscheme.ParticipantIdentifierSchemeManager;
import com.helger.peppolid.peppol.process.IPeppolPredefinedProcessIdentifier;
import com.helger.peppolid.peppol.process.PredefinedProcessIdentifierManager;

/**
 * Identifier type to validate
 *
 * @author Philip Helger
 */
public enum EIDType implements IHasID <String>, IHasDisplayName
{
  PEPPOL_PARTICIPANT ("ppid", "Peppol Participant ID", EIDType::_peppolParticipantID),
  PEPPOL_DOCUMENT_TYPE ("doctypeid", "Peppol DocumentType ID", EIDType::_peppolDocTypeID),
  PEPPOL_PROCESS ("pprocid", "Peppol Process ID", EIDType::_peppolProcessID);

  private final String m_sID;
  private final String m_sDisplayName;
  private final IIDTypeValidator m_aValidator;

  EIDType (@Nonnull @Nonempty final String sID, @Nonnull @Nonempty final String sDisplayName, @Nonnull final IIDTypeValidator aValidator)
  {
    m_sID = sID;
    m_sDisplayName = sDisplayName;
    m_aValidator = aValidator;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getDisplayName ()
  {
    return m_sDisplayName;
  }

  @Nonnull
  public IIDTypeValidator getValidator ()
  {
    return m_aValidator;
  }

  @Nullable
  public static EIDType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EIDType.class, sID);
  }

  @Nonnull
  private static IError _warn (final String s)
  {
    return SingleError.builderWarn ().setErrorText (s).build ();
  }

  @Nonnull
  private static IError _error (final String s)
  {
    return SingleError.builderError ().setErrorText (s).build ();
  }

  private static void _peppolParticipantID (@Nonnull @Nonempty final String sID,
                                            @Nonnull final ErrorList aErrorList,
                                            @Nonnull final List <KVPair> aDetails)
  {
    // This is quicker than splitting with RegEx!
    final ICommonsList <String> aSplitted = StringHelper.getExploded (CIdentifier.URL_SCHEME_VALUE_SEPARATOR, sID, 2);
    if (aSplitted.size () != 2)
      aErrorList.add (_error ("The separator '" + CIdentifier.URL_SCHEME_VALUE_SEPARATOR + "' between scheme and value is missing"));
    else
    {
      // Okay, we have exactly 2 parts
      int nErrors = aErrorList.getErrorCount ();
      final String sScheme = aSplitted.get (0);
      aDetails.add (new KVPair ("Identifier scheme", sScheme));

      if (StringHelper.hasNoText (sScheme))
        aErrorList.add (_error ("The identifier scheme part must not be empty"));
      else
      {
        if (sScheme.indexOf (CIdentifier.URL_SCHEME_VALUE_SEPARATOR) >= 0)
          aErrorList.add (_error ("The identifier scheme must not contain the character sequence '" +
                                  CIdentifier.URL_SCHEME_VALUE_SEPARATOR +
                                  "'"));
        if (sScheme.length () > PeppolIdentifierHelper.MAX_IDENTIFIER_SCHEME_LENGTH)
          aErrorList.add (_error ("The identifier scheme length of " +
                                  sScheme.length () +
                                  " must not exceed " +
                                  PeppolIdentifierHelper.MAX_IDENTIFIER_SCHEME_LENGTH +
                                  " characters"));

        // Participant specific
        if (!RegExHelper.stringMatchesPattern (PeppolIdentifierHelper.PARTICIPANT_IDENTIFIER_SCHEME_REGEX,
                                               Pattern.CASE_INSENSITIVE,
                                               sScheme))
          aErrorList.add (_error ("The identifier scheme '" +
                                  sScheme +
                                  "' must match the regular expression '" +
                                  PeppolIdentifierHelper.PARTICIPANT_IDENTIFIER_SCHEME_REGEX +
                                  "'"));

        if (!sScheme.equals (PeppolIdentifierHelper.PARTICIPANT_SCHEME_ISO6523_ACTORID_UPIS))
          aErrorList.add (_error ("The identifier scheme '" + sScheme + "' is not a known Peppol participant identifier scheme"));

        // Fallback check
        if (aErrorList.getErrorCount () == nErrors)
          if (!PeppolIdentifierFactory.INSTANCE.isParticipantIdentifierSchemeValid (sScheme))
            aErrorList.add (_error ("The identifier scheme '" + sScheme + "' is not valid according to the final Peppol rules"));
      }

      nErrors = aErrorList.getErrorCount ();
      final String sValue = aSplitted.get (1);
      aDetails.add (new KVPair ("Identifier value", sValue));

      if (StringHelper.hasNoText (sValue))
        aErrorList.add (_error ("The identifier value part must not be empty"));
      else
      {
        final ICommonsList <String> aSplittedValue = StringHelper.getExploded (':', sValue, 2);
        if (aSplittedValue.size () != 2)
          aErrorList.add (_error ("The separator ':' between issuing agency and effective value is missing"));
        else
        {
          final String sIssuingAgency = aSplittedValue.get (0);
          aDetails.add (new KVPair ("Issuing agency", sIssuingAgency));

          if (StringHelper.hasNoText (sIssuingAgency))
            aErrorList.add (_error ("The issuing agency part must not be empty"));
          else
          {
            final String sRegEx = "[0-9]{4}";
            if (!RegExHelper.stringMatchesPattern (sRegEx, sIssuingAgency))
              aErrorList.add (_error ("The issuing agency '" + sIssuingAgency + "' must match the regular expression '" + sRegEx + "'"));
            else
            {
              final IParticipantIdentifierScheme aPredefined = ParticipantIdentifierSchemeManager.getSchemeOfISO6523Code (sIssuingAgency);
              if (aPredefined == null)
                aErrorList.add (_error ("The issuing agency '" + sIssuingAgency + "' is not part of the official Peppol code list"));
              else
              {
                if (aPredefined.isDeprecated ())
                  aErrorList.add (_warn ("The issuing agency '" + sIssuingAgency + "' is deprecated"));

                if (aPredefined instanceof EPredefinedParticipantIdentifierScheme)
                {
                  final EPredefinedParticipantIdentifierScheme ePredefined = (EPredefinedParticipantIdentifierScheme) aPredefined;
                  aDetails.add (new KVPair ("Issuing agency name", ePredefined.getSchemeName ()));
                }
              }
            }
          }

          final String sEffectiveValue = aSplittedValue.get (1);
          aDetails.add (new KVPair ("Effective value", sEffectiveValue));
          if (StringHelper.hasNoText (sEffectiveValue))
          {
            // POLICY 1
            aErrorList.add (_error ("The effective value part must not be empty"));
          }
          else
          {
            // POLICY 1
            if (sEffectiveValue.length () > PeppolIdentifierHelper.MAX_PARTICIPANT_VALUE_LENGTH)
              aErrorList.add (_error ("The effective value length of " +
                                      sEffectiveValue.length () +
                                      " must not exceed " +
                                      PeppolIdentifierHelper.MAX_PARTICIPANT_VALUE_LENGTH +
                                      " characters"));

            // POLICY 1
            final String sRegEx = "[0-9a-zA-Z]+";
            if (!RegExHelper.stringMatchesPattern (sRegEx, sEffectiveValue))
              aErrorList.add (_error ("The effective value '" + sEffectiveValue + "' must match the regular expression '" + sRegEx + "'"));
          }
        }

        // Fallback check
        if (aErrorList.getErrorCount () == nErrors)
          if (!PeppolIdentifierFactory.INSTANCE.isParticipantIdentifierValueValid (sValue))
            aErrorList.add (_error ("The identifier value '" + sValue + "' is not valid according to the Peppol rules"));
      }
    }
  }

  private static void _peppolDocTypeID (@Nonnull @Nonempty final String sID,
                                        @Nonnull final ErrorList aErrorList,
                                        @Nonnull final List <KVPair> aDetails)
  {
    // This is quicker than splitting with RegEx!
    final ICommonsList <String> aSplitted = StringHelper.getExploded (CIdentifier.URL_SCHEME_VALUE_SEPARATOR, sID, 2);
    if (aSplitted.size () != 2)
      aErrorList.add (_error ("The separator '" + CIdentifier.URL_SCHEME_VALUE_SEPARATOR + "' between scheme and value is missing"));
    else
    {
      // Okay, we have exactly 2 parts
      int nErrors = aErrorList.getErrorCount ();
      final String sScheme = aSplitted.get (0);
      aDetails.add (new KVPair ("Identifier scheme", sScheme));

      if (StringHelper.hasNoText (sScheme))
        aErrorList.add (_error ("The identifier scheme part must not be empty"));
      else
      {
        if (sScheme.indexOf (CIdentifier.URL_SCHEME_VALUE_SEPARATOR) >= 0)
          aErrorList.add (_error ("The identifier scheme must not contain the character sequence '" +
                                  CIdentifier.URL_SCHEME_VALUE_SEPARATOR +
                                  "'"));
        if (sScheme.length () > PeppolIdentifierHelper.MAX_IDENTIFIER_SCHEME_LENGTH)
          aErrorList.add (_error ("The identifier scheme length of " +
                                  sScheme.length () +
                                  " must not exceed " +
                                  PeppolIdentifierHelper.MAX_IDENTIFIER_SCHEME_LENGTH +
                                  " characters"));

        // Document type specific check
        // PeppolIdentifierHelper.DOCUMENT_TYPE_SCHEME_PEPPOL_DOCTYPE_WILDCARD
        // is PfuoI 4.2
        if (!sScheme.equals (PeppolIdentifierHelper.DOCUMENT_TYPE_SCHEME_BUSDOX_DOCID_QNS))
          aErrorList.add (_error ("The identifier scheme '" + sScheme + "' is not a known Peppol document type identifier scheme"));

        // Fallback check
        if (aErrorList.getErrorCount () == nErrors)
          if (!PeppolIdentifierFactory.INSTANCE.isDocumentTypeIdentifierSchemeValid (sScheme))
            aErrorList.add (_error ("The identifier scheme '" + sScheme + "' is not valid according to the Peppol rules"));
      }

      nErrors = aErrorList.getErrorCount ();
      final String sValue = aSplitted.get (1);
      aDetails.add (new KVPair ("Identifier value", sValue));

      if (StringHelper.hasNoText (sValue))
      {
        // POLICY 1
        aErrorList.add (_error ("The identifier value part must not be empty"));
      }
      else
      {
        // POLICY 1
        if (sValue.length () > PeppolIdentifierHelper.MAX_DOCUMENT_TYPE_VALUE_LENGTH)
          aErrorList.add (_error ("The identifier value length of " +
                                  sValue.length () +
                                  " must not exceed " +
                                  PeppolIdentifierHelper.MAX_DOCUMENT_TYPE_VALUE_LENGTH +
                                  " characters"));

        // POLICY 1
        if (!StandardCharsets.ISO_8859_1.newEncoder ().canEncode (sValue))
          aErrorList.add (_error ("The identifier value '" + sValue + "' must not contain characters from outside ISO-8859-1"));

        final IPeppolPredefinedDocumentTypeIdentifier aPredefined = PredefinedDocumentTypeIdentifierManager.getDocumentTypeIdentifierOfID (sValue);
        if (aPredefined == null)
          aErrorList.add (_error ("The identifier value '" + sValue + "' is not part of the official Peppol code list"));
        else
        {
          if (aPredefined.isDeprecated ())
            aErrorList.add (_warn ("The identifier value '" + sValue + "' is deprecated"));
        }

        try
        {
          // Policy 20
          final IPeppolDocumentTypeIdentifierParts aParts = PeppolDocumentTypeIdentifierParts.extractFromString (sValue);
          aDetails.add (new KVPair ("Document element namespace URI", aParts.getRootNS ()));
          aDetails.add (new KVPair ("Document element local name", aParts.getLocalName ()));
          aDetails.add (new KVPair ("Customization ID", aParts.getCustomizationID ()));
          aDetails.add (new KVPair ("Version", aParts.getVersion ()));
        }
        catch (final RuntimeException ex)
        {
          aErrorList.add (_error (ex.getMessage ()));
        }

        // Fallback check
        if (aErrorList.getErrorCount () == nErrors)
          if (!PeppolIdentifierFactory.INSTANCE.isDocumentTypeIdentifierValueValid (sValue))
            aErrorList.add (_error ("The identifier value '" + sValue + "' is not valid according to the Peppol rules"));
      }
    }
  }

  private static void _peppolProcessID (@Nonnull @Nonempty final String sID,
                                        @Nonnull final ErrorList aErrorList,
                                        @Nonnull final List <KVPair> aDetails)
  {
    // This is quicker than splitting with RegEx!
    final ICommonsList <String> aSplitted = StringHelper.getExploded (CIdentifier.URL_SCHEME_VALUE_SEPARATOR, sID, 2);
    if (aSplitted.size () != 2)
      aErrorList.add (_error ("The separator '" + CIdentifier.URL_SCHEME_VALUE_SEPARATOR + "' between scheme and value is missing"));
    else
    {
      // Okay, we have exactly 2 parts
      int nErrors = aErrorList.getErrorCount ();
      final String sScheme = aSplitted.get (0);
      aDetails.add (new KVPair ("Identifier scheme", sScheme));

      if (StringHelper.hasNoText (sScheme))
        aErrorList.add (_error ("The identifier scheme part must not be empty"));
      else
      {
        if (sScheme.indexOf (CIdentifier.URL_SCHEME_VALUE_SEPARATOR) >= 0)
          aErrorList.add (_error ("The identifier scheme must not contain the character sequence '" +
                                  CIdentifier.URL_SCHEME_VALUE_SEPARATOR +
                                  "'"));
        if (sScheme.length () > PeppolIdentifierHelper.MAX_IDENTIFIER_SCHEME_LENGTH)
          aErrorList.add (_error ("The identifier scheme length of " +
                                  sScheme.length () +
                                  " must not exceed " +
                                  PeppolIdentifierHelper.MAX_IDENTIFIER_SCHEME_LENGTH +
                                  " characters"));

        // Process specific check
        if (!sScheme.equals (PeppolIdentifierHelper.PROCESS_SCHEME_CENBII_PROCID_UBL))
          aErrorList.add (_error ("The identifier scheme '" + sScheme + "' is not a known Peppol process identifier scheme"));

        // Fallback check
        if (aErrorList.getErrorCount () == nErrors)
          if (!PeppolIdentifierFactory.INSTANCE.isProcessIdentifierSchemeValid (sScheme))
            aErrorList.add (_error ("The identifier scheme '" + sScheme + "' is not valid according to the Peppol rules"));
      }

      nErrors = aErrorList.getErrorCount ();
      final String sValue = aSplitted.get (1);
      aDetails.add (new KVPair ("Identifier value", sValue));

      if (StringHelper.hasNoText (sValue))
      {
        // POLICY 1
        aErrorList.add (_error ("The identifier value part must not be empty"));
      }
      else
      {
        // POLICY 1
        if (sValue.length () > PeppolIdentifierHelper.MAX_PROCESS_VALUE_LENGTH)
          aErrorList.add (_error ("The identifier value length of " +
                                  sValue.length () +
                                  " must not exceed " +
                                  PeppolIdentifierHelper.MAX_PROCESS_VALUE_LENGTH +
                                  " characters"));

        // POLICY 1
        if (!StandardCharsets.ISO_8859_1.newEncoder ().canEncode (sValue))
          aErrorList.add (_error ("The identifier value '" + sValue + "' must not contain characters from outside ISO-8859-1"));

        // POLICY 25
        final String sRegEx = ".*\\s.*";
        if (RegExHelper.stringMatchesPattern (sRegEx, sValue))
          aErrorList.add (_error ("The identifier value '" + sValue + "' must not contain whitespace characters"));

        final IPeppolPredefinedProcessIdentifier aPredefined = PredefinedProcessIdentifierManager.getProcessIdentifierOfID (sValue);
        if (aPredefined == null)
          aErrorList.add (_error ("The identifier value '" + sValue + "' is not part of the official Peppol code list"));
        else
        {
          if (aPredefined.isDeprecated ())
            aErrorList.add (_warn ("The identifier value '" + sValue + "' is deprecated"));
        }

        // Fallback check
        if (aErrorList.getErrorCount () == nErrors)
          if (!PeppolIdentifierFactory.INSTANCE.isProcessIdentifierValueValid (sValue))
            aErrorList.add (_error ("The identifier value '" + sValue + "' is not valid according to the Peppol rules"));
      }
    }
  }
}
