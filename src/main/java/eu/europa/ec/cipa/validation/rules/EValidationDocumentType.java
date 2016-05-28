/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package eu.europa.ec.cipa.validation.rules;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.validation.Schema;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.EnumHelper;
import com.helger.jaxb.builder.IJAXBDocumentType;
import com.helger.ubl20.EUBL20DocumentType;
import com.helger.ubl21.EUBL21DocumentType;

/**
 * All predefined document types for which rules are contained.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public enum EValidationDocumentType implements IValidationDocumentType
{
  /** Pre award catalogue document type */
  TENDERING_CATALOGUE ("tenderingcatalogue", null),

  /** Post award catalogue document type */
  CATALOGUE ("catalogue", EUBL20DocumentType.CATALOGUE),

  /** Order document type */
  ORDER ("order", EUBL20DocumentType.ORDER),

  /** Order response document type */
  ORDERRESPONSE ("orderresponse", EUBL20DocumentType.ORDER_RESPONSE_SIMPLE),

  /** Invoice document type */
  INVOICE ("invoice", EUBL20DocumentType.INVOICE),

  /** Credit note document type */
  CREDIT_NOTE ("creditnote", EUBL20DocumentType.CREDIT_NOTE),

  /** Tender document type */
  TENDER ("tender", EUBL21DocumentType.TENDER),

  /** Call for tenders document type */
  CALL_FOR_TENDERS ("callfortenders", EUBL21DocumentType.CALL_FOR_TENDERS);

  private final String m_sID;
  private final IJAXBDocumentType m_aUBLDocType;

  private EValidationDocumentType (@Nonnull @Nonempty final String sID, @Nullable final IJAXBDocumentType aUBLDocType)
  {
    m_sID = sID;
    m_aUBLDocType = aUBLDocType;
  }

  /**
   * @return The internal ID of the document type. Has no association to
   *         anything outside.
   */
  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return The underlying UBL document type. May be <code>null</code> for
   *         certain special document types.
   */
  @Nullable
  public IJAXBDocumentType getUBLDocumentType ()
  {
    return m_aUBLDocType;
  }

  @Nullable
  public Schema getSchema (@Nullable final ClassLoader aClassLoader)
  {
    return m_aUBLDocType == null ? null : m_aUBLDocType.getSchema (aClassLoader);
  }

  @Nullable
  public static EValidationDocumentType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EValidationDocumentType.class, sID);
  }
}
