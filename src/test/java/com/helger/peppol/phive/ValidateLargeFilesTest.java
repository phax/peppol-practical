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
package com.helger.peppol.phive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.timing.StopWatch;
import com.helger.diver.api.version.VESID;
import com.helger.phive.api.execute.ValidationExecutionManager;
import com.helger.phive.api.executorset.IValidationExecutorSet;
import com.helger.phive.xml.source.IValidationSourceXML;
import com.helger.phive.xml.source.ValidationSourceXML;

public final class ValidateLargeFilesTest
{
  private static final Logger LOGGER = LoggerFactory.getLogger (ValidateLargeFilesTest.class);

  @Test
  @Ignore ("Takes ~15 + 14 minutes")
  public void testIssue () throws Exception
  {
    final IReadableResource aRes = new ClassPathResource ("external/validation/Large_Invoice_sample1.xml");
    assertTrue (aRes.exists ());
    final IValidationExecutorSet <IValidationSourceXML> aVES = ExtValidationKeyRegistry.getFromIDOrNull (VESID.parseID ("eu.peppol.bis3:invoice:2023.11"));
    // Start validating #1
    final StopWatch aSW = StopWatch.createdStarted ();
    LOGGER.info ("Start validating");
    var aVRL = ValidationExecutionManager.executeValidation (aVES, ValidationSourceXML.create (aRes), Locale.US);
    aSW.stop ();
    LOGGER.info ("End validating after " + aSW.getDuration ());
    // Validation #2
    aSW.restart ();
    LOGGER.info ("Start validating2");
    aVRL = ValidationExecutionManager.executeValidation (aVES, ValidationSourceXML.create (aRes), Locale.US);
    aSW.stop ();
    LOGGER.info ("End validating 2after " + aSW.getDuration ());

    assertNotNull (aVRL);
  }
}
