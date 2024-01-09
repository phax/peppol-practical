package com.helger.peppol.phive;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.ddd.model.DDDValueProviderList;
import com.helger.ddd.model.DDDValueProviderPerSyntax;
import com.helger.ddd.model.EDDDDeterminedField;
import com.helger.ddd.model.EDDDSourceField;
import com.helger.diver.api.version.VESID;

public class MainCheckDDDConsistency
{
  private static final Logger LOGGER = LoggerFactory.getLogger (MainCheckDDDConsistency.class);

  public static void main (final String [] args)
  {
    int nChecks = 0;
    final DDDValueProviderList aVPL = DDDValueProviderList.getDefaultValueProviderList ();
    for (final Map.Entry <String, DDDValueProviderPerSyntax> e1 : aVPL.valueProvidersPerSyntaxes ().entrySet ())
    {
      final ICommonsMap <EDDDSourceField, ICommonsMap <String, ICommonsMap <EDDDDeterminedField, String>>> aSels = e1.getValue ()
                                                                                                                     .getAllSelectors ();
      for (final Map.Entry <EDDDSourceField, ICommonsMap <String, ICommonsMap <EDDDDeterminedField, String>>> e2 : aSels.entrySet ())
      {
        for (final Map.Entry <String, ICommonsMap <EDDDDeterminedField, String>> e3 : e2.getValue ().entrySet ())
        {
          for (final Map.Entry <EDDDDeterminedField, String> e4 : e3.getValue ().entrySet ())
          {
            switch (e4.getKey ())
            {
              case VESID:
                ++nChecks;
                final String sVESID = e4.getValue ();
                LOGGER.info (sVESID);
                if (ExtValidationKeyRegistry.getFromIDOrNull (VESID.parseID (sVESID)) == null)
                  throw new IllegalStateException ("VES ID " + sVESID + " is unknown");
                break;
            }
          }
        }
      }
    }
    LOGGER.info (nChecks + " checks done");
  }
}
