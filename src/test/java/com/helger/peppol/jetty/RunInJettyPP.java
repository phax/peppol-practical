/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.peppol.jetty;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.system.SystemProperties;
import com.helger.photon.jetty.JettyStarter;
import com.helger.settings.exchange.configfile.ConfigFile;
import com.helger.settings.exchange.configfile.ConfigFileBuilder;

/**
 * Run peppol-practical as a standalone web application in Jetty on port 8080.
 * <br>
 * http://localhost:8080/
 *
 * @author Philip Helger
 */
@Immutable
public final class RunInJettyPP
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (RunInJettyPP.class);

  public static void main (final String [] args) throws Exception
  {
    // Proxy configuration is simply applied by setting system properties
    final ConfigFile aCF = new ConfigFileBuilder ().addPaths ("private-configProxy.properties",
                                                              "configProxy.properties")
                                                   .build ();
    for (final Map.Entry <String, Object> aEntry : aCF.getAllEntries ().entrySet ())
    {
      final String sKey = aEntry.getKey ();
      final String sValue = String.valueOf (aEntry.getValue ());
      SystemProperties.setPropertyValue (sKey, sValue);
      s_aLogger.info ("Setting Proxy property " + sKey + "=" + sValue);
    }

    new JettyStarter (RunInJettyPP.class).run ();
  }
}
