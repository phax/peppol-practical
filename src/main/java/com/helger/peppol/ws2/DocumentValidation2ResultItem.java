/**
 * Copyright (C) 2014-2017 Philip Helger (www.helger.com)
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
package com.helger.peppol.ws2;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.helger.commons.error.level.EErrorLevel;

import eu.europa.ec.cipa.validation.generic.EXMLValidationType;
import eu.europa.ec.cipa.validation.rules.EValidationLevel;

/**
 * Represents a single result item.
 *
 * @author philip
 */
@XmlAccessorType (XmlAccessType.FIELD)
@XmlType (name = "ValidationServiceResultItemType", propOrder = { "m_eValidationLevel",
                                                                  "m_eValidationType",
                                                                  "m_eErrorLevel",
                                                                  "m_sLocation",
                                                                  "m_sSVRLTest",
                                                                  "m_sErrorText",
                                                                  "m_sExceptionMessage" })
public final class DocumentValidation2ResultItem
{
  @XmlElement (name = "ValidationLevel", required = true)
  private EValidationLevel m_eValidationLevel;
  @XmlElement (name = "ValidationType", required = true)
  private EXMLValidationType m_eValidationType;
  @XmlElement (name = "ErrorLevel", required = true)
  private EErrorLevel m_eErrorLevel;
  @XmlElement (name = "Location", required = true)
  private String m_sLocation;
  @XmlElement (name = "SVRLTest")
  private String m_sSVRLTest;
  @XmlElement (name = "ErrorText", required = true)
  private String m_sErrorText;
  @XmlElement (name = "ExceptionMessage")
  private String m_sExceptionMessage;

  public DocumentValidation2ResultItem ()
  {}

  public void setValidationLevel (@Nullable final EValidationLevel eValidationLevel)
  {
    m_eValidationLevel = eValidationLevel;
  }

  public EValidationLevel getValidationLevel ()
  {
    return m_eValidationLevel;
  }

  public void setValidationType (@Nullable final EXMLValidationType eValidationType)
  {
    m_eValidationType = eValidationType;
  }

  @Nullable
  public EXMLValidationType getValidationType ()
  {
    return m_eValidationType;
  }

  public void setErrorLevel (@Nullable final EErrorLevel eErrorLevel)
  {
    m_eErrorLevel = eErrorLevel;
  }

  @Nullable
  public EErrorLevel getErrorLevel ()
  {
    return m_eErrorLevel;
  }

  public void setLocation (@Nullable final String sLocation)
  {
    m_sLocation = sLocation;
  }

  @Nullable
  public String getLocation ()
  {
    return m_sLocation;
  }

  public void setSVRLTest (@Nullable final String sSVRLTest)
  {
    m_sSVRLTest = sSVRLTest;
  }

  @Nullable
  public String getSVRLTest ()
  {
    return m_sSVRLTest;
  }

  public void setErrorText (@Nullable final String sErrorText)
  {
    m_sErrorText = sErrorText;
  }

  @Nullable
  public String getErrorText ()
  {
    return m_sErrorText;
  }

  public void setExceptionMessage (@Nullable final String sExceptionMessage)
  {
    m_sExceptionMessage = sExceptionMessage;
  }

  @Nullable
  public String getExceptionMessage ()
  {
    return m_sExceptionMessage;
  }
}
