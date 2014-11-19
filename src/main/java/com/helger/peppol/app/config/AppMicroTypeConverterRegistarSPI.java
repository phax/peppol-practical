/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.peppol.app.config;

import com.helger.commons.annotations.IsSPIImplementation;
import com.helger.commons.microdom.convert.IMicroTypeConverterRegistrarSPI;
import com.helger.commons.microdom.convert.IMicroTypeConverterRegistry;
import com.helger.peppol.comment.domain.Comment;
import com.helger.peppol.comment.domain.CommentMicroTypeConverter;
import com.helger.peppol.comment.domain.CommentThread;
import com.helger.peppol.comment.domain.CommentThreadMicroTypeConverter;
import com.helger.peppol.crm.CRMGroup;
import com.helger.peppol.crm.CRMGroupMicroTypeConverter;
import com.helger.peppol.crm.CRMSubscriber;
import com.helger.peppol.crm.CRMSubscriberMicroTypeConverter;
import com.helger.peppol.testep.domain.TestEndpoint;
import com.helger.peppol.testep.domain.TestEndpointData;
import com.helger.peppol.testep.domain.TestEndpointDataMicroTypeConverter;
import com.helger.peppol.testep.domain.TestEndpointMicroTypeConverter;

@IsSPIImplementation
public final class AppMicroTypeConverterRegistarSPI implements IMicroTypeConverterRegistrarSPI
{
  public void registerMicroTypeConverter (final IMicroTypeConverterRegistry aRegistry)
  {
    // CRM stuff
    aRegistry.registerMicroElementTypeConverter (CRMGroup.class, new CRMGroupMicroTypeConverter ());
    aRegistry.registerMicroElementTypeConverter (CRMSubscriber.class, new CRMSubscriberMicroTypeConverter ());
    // Comment stuff
    aRegistry.registerMicroElementTypeConverter (Comment.class, new CommentMicroTypeConverter ());
    aRegistry.registerMicroElementTypeConverter (CommentThread.class, new CommentThreadMicroTypeConverter ());
    // Test Endpoint
    aRegistry.registerMicroElementTypeConverter (TestEndpointData.class, new TestEndpointDataMicroTypeConverter ());
    aRegistry.registerMicroElementTypeConverter (TestEndpoint.class, new TestEndpointMicroTypeConverter ());
  }
}
