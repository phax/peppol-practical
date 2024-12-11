/*
 * Copyright (C) 2014-2024 Philip Helger (www.helger.com)
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

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import com.helger.peppol.servicedomain.EPeppolNetwork;
import com.helger.peppol.sml.ESML;
import com.helger.peppol.sml.ESMPAPIType;
import com.helger.peppolid.IParticipantIdentifier;
import com.helger.peppolid.factory.IIdentifierFactory;
import com.helger.smpclient.url.BDXLURLProvider;
import com.helger.smpclient.url.ISMPURLProvider;
import com.helger.smpclient.url.PeppolURLProvider;
import com.helger.smpclient.url.SMPDNSResolutionException;

/**
 * Extracted because used some times
 *
 * @author Philip Helger
 */
public final class SMPQueryParams
{
  private static final Logger LOGGER = LoggerFactory.getLogger (SMPQueryParams.class);

  private ESMPAPIType m_eSMPAPIType;
  private EPeppolNetwork m_ePeppolNetwork;
  private IIdentifierFactory m_aIF;
  private IParticipantIdentifier m_aParticipantID;
  private URI m_aSMPHostURI;
  private boolean m_bTrustAllCerts = false;

  private SMPQueryParams ()
  {}

  @Nonnull
  public ESMPAPIType getSMPAPIType ()
  {
    return m_eSMPAPIType;
  }

  @Nullable
  public EPeppolNetwork getPeppolNetwork ()
  {
    return m_ePeppolNetwork;
  }

  @Nonnull
  public IIdentifierFactory getIF ()
  {
    return m_aIF;
  }

  @Nonnull
  public IParticipantIdentifier getParticipantID ()
  {
    return m_aParticipantID;
  }

  @Nonnull
  public URI getSMPHostURI ()
  {
    return m_aSMPHostURI;
  }

  public boolean isTrustAllCertificates ()
  {
    return m_bTrustAllCerts;
  }

  private boolean _isSMPRegisteredInDNSViaInetAddress ()
  {
    // Extract host part
    final String sHost = m_aSMPHostURI.getHost ();

    LOGGER.info ("Checking for existance of '" + m_aParticipantID.getURIEncoded () + "' in DNS via '" + sHost + "'");
    try
    {
      // Use system default DNS lookup
      InetAddress.getByName (sHost);
      // Found it
      return true;
    }
    catch (final UnknownHostException ex)
    {
      return false;
    }
  }

  private boolean _isSMPRegisteredInDNSViaDnsJava ()
  {
    // Extract host part
    final String sHost = m_aSMPHostURI.getHost ();

    LOGGER.info ("Checking for existance of '" + m_aParticipantID.getURIEncoded () + "' in DNS via '" + sHost + "'");

    // Convert to structured host name
    Name aParsedName = null;
    try
    {
      aParsedName = Name.fromString (sHost);
    }
    catch (final TextParseException ex)
    {
      // ignore
    }

    // Check IP v4 and IP v6
    Record [] aRecords = new Lookup (aParsedName, Type.A).run ();
    if (aRecords == null)
    {
      LOGGER.info ("Found no A record. Checking for AAAA record now.");
      aRecords = new Lookup (aParsedName, Type.AAAA).run ();
    }

    boolean bFoundAny = false;
    if (aRecords != null)
    {
      for (final Record aRecord : aRecords)
        if (aRecord instanceof ARecord)
        {
          // IP v4
          final ARecord aSpecificRec = (ARecord) aRecord;
          final String sResolvedIP = aSpecificRec.rdataToString ();
          LOGGER.info ("[A] --> '" + sResolvedIP + "'");
          bFoundAny = true;
        }
        else
          if (aRecord instanceof AAAARecord)
          {
            // IP v6
            final AAAARecord aSpecificRec = (AAAARecord) aRecord;
            final String sResolvedIP = aSpecificRec.rdataToString ();
            LOGGER.info ("[AAAA] --> '" + sResolvedIP + "'");
            bFoundAny = true;
          }
    }
    else
      LOGGER.warn ("Found neither A nor AAAA record");

    return bFoundAny;
  }

  public boolean isSMPRegisteredInDNS ()
  {
    if (true)
    {
      // Internal caching is quite good and subsequent calls only take ~1ms
      return _isSMPRegisteredInDNSViaInetAddress ();
    }

    // This takes 25 seconds on the first call and doesn't deliver a result
    // Also afterwards, it takes a bit longer
    return _isSMPRegisteredInDNSViaDnsJava ();
  }

  @Nonnull
  private static ISMPURLProvider _getURLProvider (@Nonnull final ESMPAPIType eAPIType)
  {
    return eAPIType == ESMPAPIType.PEPPOL ? PeppolURLProvider.INSTANCE : BDXLURLProvider.INSTANCE;
  }

  @Nullable
  public static SMPQueryParams createForSMLOrNull (@Nonnull final ISMLConfiguration aCurSML,
                                                   @Nullable final String sParticipantIDScheme,
                                                   @Nullable final String sParticipantIDValue,
                                                   final boolean bLogOnError)
  {
    final SMPQueryParams ret = new SMPQueryParams ();
    ret.m_eSMPAPIType = aCurSML.getSMPAPIType ();
    if (ret.m_eSMPAPIType == ESMPAPIType.PEPPOL)
    {
      ret.m_ePeppolNetwork = ESML.DIGIT_PRODUCTION.getID ().equals (aCurSML.getID ()) ? EPeppolNetwork.PRODUCTION
                                                                                      : EPeppolNetwork.TEST;
    }
    ret.m_aIF = aCurSML.getSMPIdentifierType ().getIdentifierFactory ();
    ret.m_aParticipantID = ret.m_aIF.createParticipantIdentifier (sParticipantIDScheme, sParticipantIDValue);
    if (ret.m_aParticipantID == null)
    {
      // Participant ID is invalid for this scheme
      if (bLogOnError)
        LOGGER.warn ("Failed to parse participant ID '" + sParticipantIDScheme + "' and '" + sParticipantIDValue + "'");
      return null;
    }

    try
    {
      ret.m_aSMPHostURI = _getURLProvider (ret.m_eSMPAPIType).getSMPURIOfParticipant (ret.m_aParticipantID,
                                                                                      aCurSML.getDNSZone ());
      if (ret.m_eSMPAPIType != ESMPAPIType.PEPPOL && "https".equals (ret.m_aSMPHostURI.getScheme ()))
        ret.m_bTrustAllCerts = true;
    }
    catch (final SMPDNSResolutionException ex)
    {
      // For BDXL lookup -> no such participant
      if (bLogOnError)
        LOGGER.warn ("Failed to resolve participant " + ret.m_aParticipantID + " in DNS");
      return null;
    }
    return ret;
  }
}
