package com.helger.peppol.supplementary.tools;

import java.util.Locale;
import java.util.Map;

import com.helger.bdve.executorset.IValidationExecutorSet;
import com.helger.bdve.executorset.VESID;
import com.helger.peppol.pub.validation.ExtValidationKeyRegistry;

public class MainListVESIDs
{
  public static void main (final String [] args)
  {
    final Locale aDisplayLocale = new Locale ("de_AT");
    for (final Map.Entry <VESID, IValidationExecutorSet> aEntry : ExtValidationKeyRegistry.getAllSorted (aDisplayLocale)
                                                                                          .entrySet ())
    {
      System.out.println ("<li><code>" +
                          aEntry.getKey ().getAsSingleID () +
                          "</code> - " +
                          aEntry.getValue ().getDisplayName () +
                          "</li>");
    }
  }
}
