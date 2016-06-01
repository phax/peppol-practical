/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.peppol.ws.types;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.error.EErrorLevel;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
@XmlType (name = "ValidationServiceResultType", propOrder = { "m_eReturnCode",
                                                              "m_bValidationInterrupted",
                                                              "m_eMostSeverErrorLevel",
                                                              "m_aItems" })
public final class ValidationServiceResult
{
  @XmlElement (name = "ReturnCode", required = true)
  private EValidationServiceReturnCode m_eReturnCode = EValidationServiceReturnCode.NO_ERROR;
  @XmlElement (name = "ValidationInterrupted", required = true)
  private boolean m_bValidationInterrupted = false;
  @XmlElement (name = "MostSevereErrorLevel", required = true)
  private EErrorLevel m_eMostSeverErrorLevel;
  @XmlElement (name = "Items", required = true)
  private List <ValidationServiceResultItem> m_aItems;

  public ValidationServiceResult ()
  {}

  public void setReturnCode (@Nullable final EValidationServiceReturnCode eReturnCode)
  {
    m_eReturnCode = eReturnCode;
  }

  @Nullable
  public EValidationServiceReturnCode getReturnCode ()
  {
    return m_eReturnCode;
  }

  public void setValidationInterrupted (final boolean bValidationInterrupted)
  {
    m_bValidationInterrupted = bValidationInterrupted;
  }

  public boolean isValidationInterupted ()
  {
    return m_bValidationInterrupted;
  }

  public void setMostSevereErrorLevel (@Nullable final EErrorLevel eErrorLevel)
  {
    m_eMostSeverErrorLevel = eErrorLevel;
  }

  @Nullable
  public EErrorLevel getMostSevereErrorLevel ()
  {
    return m_eMostSeverErrorLevel;
  }

  @Nonnull
  @ReturnsMutableObject ("JAXB Design")
  @DevelopersNote ("Cannot change the name, because it must match JAXB naming conventions!")
  public List <ValidationServiceResultItem> getItems ()
  {
    if (m_aItems == null)
      m_aItems = new CommonsArrayList<> ();
    return m_aItems;
  }
}
