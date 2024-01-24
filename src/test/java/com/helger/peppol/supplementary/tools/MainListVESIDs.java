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
package com.helger.peppol.supplementary.tools;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.io.file.SimpleFileIO;
import com.helger.peppol.phive.ExtValidationKeyRegistry;
import com.helger.peppol.phive.ExtValidationKeySelect;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.xml.source.IValidationSourceXML;

public final class MainListVESIDs
{
  private static String _getPayload ()
  {
    final ICommonsList <IValidationExecutorSet <IValidationSourceXML>> aAll = true ? ExtValidationKeySelect.getAllSortedCorrect (Locale.US)
                                                                                   : ExtValidationKeyRegistry.getAllSortedByID ()
                                                                                                             .copyOfValues ();

    final StringBuilder aSB = new StringBuilder ();
    aSB.append ("<!-- ").append (aAll.size ()).append (" entries -->\n");
    aSB.append ("<ul>\n");
    for (final IValidationExecutorSet <IValidationSourceXML> aEntry : aAll)
    {
      aSB.append ("<li><code>")
         .append (aEntry.getID ().getAsSingleID ())
         .append ("</code> - ")
         .append (aEntry.getDisplayName ())
         .append (aEntry.isDeprecated () ? " <strong>(Deprecated)</strong>" : "")
         .append ("</li>\n");
    }
    return aSB.append ("</ul>\n").toString ();
  }

  public static void main (final String [] args)
  {
    final String sNewPayload = _getPayload ();

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

    final String sNew = new StringBuilder ().append (sOld, 0, nStart + sStart.length ())
                                            .append ('\n')
                                            .append (sNewPayload)
                                            .append (sOld, nEnd, sOld.length ())
                                            .toString ();
    SimpleFileIO.writeFile (aFile, sNew, StandardCharsets.UTF_8);
    System.out.println ("Finished updating " + aFile.getAbsolutePath ());
  }
}
