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
package com.helger.peppol.supplementary.tools;

import java.io.File;
import java.util.Comparator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.peppol.sharedui.domain.NiceNameEntry;
import com.helger.peppol.sharedui.ui.SharedUI;
import com.helger.peppolid.IProcessIdentifier;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroWriter;

public class MainCreateNiceNameMappingXML
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCreateNiceNameMappingXML.class);

  public static void main (final String [] args)
  {
    {
      final IMicroDocument aDoc = new MicroDocument ();
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("type", "doctypeid");
      for (final Map.Entry <String, NiceNameEntry> aEntry : SharedUI.getDocTypeNames ()
                                                                    .getSortedByKey (Comparator.naturalOrder ())
                                                                    .entrySet ())
      {
        final NiceNameEntry aNNE = aEntry.getValue ();
        final IMicroElement eItem = eRoot.appendElement ("item")
                                         .setAttribute ("id", aEntry.getKey ())
                                         .setAttribute ("name", aNNE.getName ())
                                         .setAttribute ("state", aNNE.getState ().getID ());
        if (aNNE.hasProcessIDs ())
          for (final IProcessIdentifier aProcID : aNNE.getAllProcIDs ())
            eItem.appendElement ("procid")
                 .setAttribute ("scheme", aProcID.getScheme ())
                 .setAttribute ("value", aProcID.getValue ());
      }
      MicroWriter.writeToFile (aDoc, new File ("docs/doctypeid-mapping.xml"));
    }

    {
      final IMicroDocument aDoc = new MicroDocument ();
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("type", "processid");
      for (final Map.Entry <String, NiceNameEntry> aEntry : SharedUI.getProcessNames ()
                                                                    .getSortedByKey (Comparator.naturalOrder ())
                                                                    .entrySet ())
        eRoot.appendElement ("item")
             .setAttribute ("id", aEntry.getKey ())
             .setAttribute ("name", aEntry.getValue ().getName ())
             .setAttribute ("state", aEntry.getValue ().getState ().getID ());
      MicroWriter.writeToFile (aDoc, new File ("docs/processid-mapping.xml"));
    }
    LOGGER.info ("Done");
  }
}
