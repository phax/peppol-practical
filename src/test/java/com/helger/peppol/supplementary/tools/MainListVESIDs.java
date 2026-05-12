/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.peppol.supplementary.tools;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jspecify.annotations.NonNull;

import com.helger.collection.commons.CommonsLinkedHashMap;
import com.helger.collection.commons.CommonsLinkedHashSet;
import com.helger.collection.commons.ICommonsList;
import com.helger.collection.commons.ICommonsOrderedMap;
import com.helger.collection.commons.ICommonsOrderedSet;
import com.helger.io.file.SimpleFileIO;
import com.helger.peppol.validate.VESRegistry;
import com.helger.peppol.validate.ctrl.HCVESSelect;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.api.executorset.ValidationExecutorSetAlias;
import com.helger.phive.xml.source.IValidationSourceXML;

public final class MainListVESIDs
{
  private static final String DEPRECATED_MARK = " <strong>(Deprecated)</strong>";
  private static final Pattern LI_PATTERN = Pattern.compile ("<li><code>([^<]+)</code>.*</li>");

  // VESID -> rendered "<li>...</li>" line
  private static ICommonsOrderedMap <String, String> _buildCurrentEntries ()
  {
    final ICommonsList <IValidationExecutorSet <IValidationSourceXML>> aAll = true ? HCVESSelect.getAllSortedCorrect (Locale.US)
                                                                                   : VESRegistry.getAllSortedByID ()
                                                                                                .copyOfValues ();

    final ICommonsOrderedMap <String, String> aMap = new CommonsLinkedHashMap <> ();
    for (final IValidationExecutorSet <IValidationSourceXML> aEntry : aAll)
    {
      final String sAliasHint;
      if (aEntry instanceof final ValidationExecutorSetAlias <?> aAlias)
        sAliasHint = " (alias to <code>" + aAlias.getSourceVES ().getID ().getAsSingleID () + "</code>)";
      else
        sAliasHint = "";

      final String sID = aEntry.getID ().getAsSingleID ();
      final String sLine = "<li><code>" +
                           sID +
                           "</code> - " +
                           aEntry.getDisplayName () +
                           sAliasHint +
                           (aEntry.getStatus ().isDeprecated () ? DEPRECATED_MARK : "") +
                           "</li>";
      aMap.put (sID, sLine);
    }
    return aMap;
  }

  @NonNull
  private static String _formatPayload (@NonNull final ICommonsOrderedMap <String, String> aEntries)
  {
    final StringBuilder aSB = new StringBuilder ();
    aSB.append ("<!-- ").append (aEntries.size ()).append (" entries -->\n");
    aSB.append ("<ul>\n");
    for (final String sLine : aEntries.values ())
      aSB.append (sLine).append ('\n');
    aSB.append ("</ul>\n");
    return aSB.toString ();
  }

  @NonNull
  private static ICommonsOrderedMap <String, String> _parsePreviousEntries (@NonNull final String sOldPayload)
  {
    final ICommonsOrderedMap <String, String> aMap = new CommonsLinkedHashMap <> ();
    for (final String sRawLine : sOldPayload.split ("\\r?\\n"))
    {
      final String sLine = sRawLine.trim ();
      final Matcher aMatcher = LI_PATTERN.matcher (sLine);
      if (aMatcher.matches ())
        aMap.put (aMatcher.group (1), sLine);
    }
    return aMap;
  }

  private static void _appendBulletSection (@NonNull final StringBuilder aSB,
                                            @NonNull final String sCaption,
                                            @NonNull final Iterable <String> aBulletLines)
  {
    aSB.append ("\n\n    <p>\n      ").append (sCaption).append ("\n    </p>\n");
    aSB.append ("<ul>\n");
    for (final String sLine : aBulletLines)
      aSB.append (sLine).append ('\n');
    aSB.append ("</ul>");
  }

  private static void _patchNewsXml (@NonNull final ICommonsOrderedMap <String, String> aAdded,
                                     @NonNull  final ICommonsOrderedSet <String> aRemoved)
  {
    final File aFile = new File ("src/main/resources/viewpages/en/news.xml");
    if (!aFile.exists ())
      throw new IllegalStateException ("Missing " + aFile);

    final String sOld = SimpleFileIO.getFileAsString (aFile, StandardCharsets.UTF_8);
    final String sAnchor = "<div id=\"tabs-news\" class=\"tab-pane active\" role=\"tabpanel\">";
    final int nIdx = sOld.indexOf (sAnchor);
    if (nIdx < 0)
      throw new IllegalStateException ("Missing news anchor in " + aFile);

    final String sToday = LocalDate.now ().toString ();
    final StringBuilder aSB = new StringBuilder ();
    aSB.append ("\n\n    <h3>").append (sToday).append (" - Document Validation extended</h3>\n");
    aSB.append ("    \n");
    aSB.append ("    <p>\n");
    aSB.append ("      <!-- TODO: describe the new validation rules -->\n");
    aSB.append ("    </p>");

    if (!aAdded.isEmpty ())
      _appendBulletSection (aSB, "Added VESIDs are:", aAdded.values ());
    if (!aRemoved.isEmpty ())
    {
      final ICommonsOrderedSet <String> aRemovedLines = new CommonsLinkedHashSet <> ();
      for (final String sID : aRemoved)
        aRemovedLines.add ("<li><code>" + sID + "</code></li>");
      _appendBulletSection (aSB, "Removed VESIDs are:", aRemovedLines);
    }

    final int nInsertAt = nIdx + sAnchor.length ();
    final String sNew = new StringBuilder ().append (sOld, 0, nInsertAt)
                                            .append (aSB)
                                            .append (sOld, nInsertAt, sOld.length ())
                                            .toString ();
    SimpleFileIO.writeFile (aFile, sNew, StandardCharsets.UTF_8);
    System.out.println ("Finished updating " + aFile.getAbsolutePath ());
  }

  public static void main (final String [] args)
  {
    final ICommonsOrderedMap <String, String> aNew = _buildCurrentEntries ();

    final File aFile = new File ("src/main/resources/viewpages/en/validation_dvs.xml");
    if (!aFile.exists ())
      throw new IllegalStateException ();

    final String sOld = SimpleFileIO.getFileAsString (aFile, StandardCharsets.UTF_8);

    final String sStart = "<!-- Created by MainListVESIDs -->";
    final int nStart = sOld.indexOf (sStart);
    if (nStart < 0)
      throw new IllegalStateException ();

    final String sEnd = "<!-- end of MainListVESIDs -->";
    final int nEnd = sOld.indexOf (sEnd);
    if (nEnd < 0)
      throw new IllegalStateException ();

    final String sOldPayload = sOld.substring (nStart + sStart.length (), nEnd);
    final ICommonsOrderedMap <String, String> aOld = _parsePreviousEntries (sOldPayload);

    // Compute deltas
    final ICommonsOrderedMap <String, String> aAdded = new CommonsLinkedHashMap <> ();
    final ICommonsOrderedMap <String, String> aNewlyDeprecated = new CommonsLinkedHashMap <> ();
    final ICommonsOrderedSet <String> aRemoved = new CommonsLinkedHashSet <> ();

    for (final var aEntry : aNew.entrySet ())
    {
      final String sID = aEntry.getKey ();
      final String sLineNew = aEntry.getValue ();
      final String sLineOld = aOld.get (sID);
      if (sLineOld == null)
        aAdded.put (sID, sLineNew);
      else
      {
        final boolean bWasDep = sLineOld.contains (DEPRECATED_MARK);
        final boolean bIsDep = sLineNew.contains (DEPRECATED_MARK);
        if (!bWasDep && bIsDep)
          aNewlyDeprecated.put (sID, sLineNew);
      }
    }
    for (final String sID : aOld.keySet ())
      if (!aNew.containsKey (sID))
        aRemoved.add (sID);

    // Write validation_dvs.xml
    final String sNewPayload = _formatPayload (aNew);
    final String sNew = new StringBuilder ().append (sOld, 0, nStart + sStart.length ())
                                            .append ('\n')
                                            .append (sNewPayload)
                                            .append (sOld, nEnd, sOld.length ())
                                            .toString ();
    SimpleFileIO.writeFile (aFile, sNew, StandardCharsets.UTF_8);
    System.out.println ("Finished updating " + aFile.getAbsolutePath ());
    System.out.println ("Delta: " +
                        aAdded.size () +
                        " added, " +
                        aNewlyDeprecated.size () +
                        " newly deprecated, " +
                        aRemoved.size () +
                        " removed");

    if (!aAdded.isEmpty () || !aRemoved.isEmpty ())
      _patchNewsXml (aAdded, aRemoved);
    else
      System.out.println ("No additions/removals - news.xml left untouched");
  }
}
