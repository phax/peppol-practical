package com.helger.peppol.supplementary.tools;

import java.io.File;
import java.util.Comparator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.peppol.ui.AppCommonUI;
import com.helger.peppol.ui.NiceNameEntry;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.MicroDocument;
import com.helger.xml.microdom.serialize.MicroWriter;

public class MainCreateMappingXML
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCreateMappingXML.class);

  public static void main (final String [] args)
  {
    {
      final IMicroDocument aDoc = new MicroDocument ();
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("type", "doctypeid");
      for (final Map.Entry <String, NiceNameEntry> aEntry : AppCommonUI.getDocTypeNames ()
                                                                       .getSortedByKey (Comparator.naturalOrder ())
                                                                       .entrySet ())
        eRoot.appendElement ("item")
             .setAttribute ("id", aEntry.getKey ())
             .setAttribute ("name", aEntry.getValue ().getName ())
             .setAttribute ("deprecated", aEntry.getValue ().isDeprecated ());
      MicroWriter.writeToFile (aDoc, new File ("doctypeid-mapping.xml"));
    }
    {
      final IMicroDocument aDoc = new MicroDocument ();
      final IMicroElement eRoot = aDoc.appendElement ("root");
      eRoot.setAttribute ("type", "processid");
      for (final Map.Entry <String, NiceNameEntry> aEntry : AppCommonUI.getProcessNames ()
                                                                       .getSortedByKey (Comparator.naturalOrder ())
                                                                       .entrySet ())
        eRoot.appendElement ("item")
             .setAttribute ("id", aEntry.getKey ())
             .setAttribute ("name", aEntry.getValue ().getName ())
             .setAttribute ("deprecated", aEntry.getValue ().isDeprecated ());
      MicroWriter.writeToFile (aDoc, new File ("processid-mapping.xml"));
    }
    LOGGER.info ("Done");
  }
}
