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
package com.helger.peppol.page.ui;

import javax.annotation.Nonnull;

import com.helger.webbasics.form.RequestField;
import com.helger.webctrls.custom.HCExtSelect;

import eu.europa.ec.cipa.peppol.sml.ESML;

public class SMLSelect extends HCExtSelect
{
  public SMLSelect (@Nonnull final RequestField aRF)
  {
    super (aRF);
    for (final ESML eSML : ESML.values ())
      if (eSML.requiresClientCertificate ())
        addOption (eSML.name (), eSML.getManagementServiceURL ());
  }
}