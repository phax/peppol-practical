package com.helger.peppol.phive;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.mutable.MutableInt;
import com.helger.ddd.model.DDDValueProviderList;
import com.helger.ddd.model.DDDValueProviderPerSyntax;
import com.helger.diver.api.version.VESID;

public class MainCheckDDDConsistency
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCheckDDDConsistency.class);

  public static void main (final String [] args)
  {
    final MutableInt aChecks = new MutableInt (0);
    final DDDValueProviderList aVPL = DDDValueProviderList.getDefaultValueProviderList ();
    for (final Map.Entry <String, DDDValueProviderPerSyntax> e1 : aVPL.valueProvidersPerSyntaxes ().entrySet ())
    {
      LOGGER.info ("Syntax " + e1.getKey ());
      e1.getValue ().forEachSelector ( (sf, sv, df, dv) -> {
        switch (df)
        {
          case VESID:
            aChecks.inc ();
            final String sVESID = dv;
            LOGGER.info ("  " + sVESID);
            if (ExtValidationKeyRegistry.getFromIDOrNull (VESID.parseID (sVESID)) == null)
              throw new IllegalStateException ("VES ID " + sVESID + " is unknown");
            break;
        }
      });
    }
    LOGGER.info (aChecks.intValue () + " checks done");
  }
}
