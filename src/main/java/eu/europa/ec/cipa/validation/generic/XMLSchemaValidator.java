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
package eu.europa.ec.cipa.validation.generic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.error.EErrorLevel;
import com.helger.commons.error.IResourceErrorGroup;
import com.helger.commons.error.ResourceError;
import com.helger.commons.error.ResourceErrorGroup;
import com.helger.commons.error.ResourceLocation;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.ToStringGenerator;
import com.helger.xml.schema.XMLSchemaCache;
import com.helger.xml.schema.XMLSchemaValidationHelper;

/**
 * Implementation of the {@link IXMLValidator} for XML Schema.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public class XMLSchemaValidator extends AbstractXMLValidator
{
  private final Schema m_aSchema;

  /**
   * Constructor for schema validation based on a single resources.
   * 
   * @param aXSD
   *        The resource where the XSD file can be found. May not be
   *        <code>null</code>.
   * @see XMLSchemaCache
   */
  public XMLSchemaValidator (@Nonnull final IReadableResource aXSD)
  {
    this (XMLSchemaCache.getInstance ().getSchema (aXSD));
  }

  /**
   * Constructor for schema validation based on the given resources (order is
   * important).
   * 
   * @param aXSDs
   *        The resources where the XSD files can be found. May neither be
   *        <code>null</code> nor empty.
   * @see XMLSchemaCache
   */
  public XMLSchemaValidator (@Nonnull @Nonempty final IReadableResource... aXSDs)
  {
    this (XMLSchemaCache.getInstance ().getSchema (aXSDs));
  }

  /**
   * Constructor for a pre-built {@link Schema} object.
   * 
   * @param aSchema
   *        The schema to be used for validation. May not be <code>null</code>.
   */
  public XMLSchemaValidator (@Nonnull final Schema aSchema)
  {
    m_aSchema = ValueEnforcer.notNull (aSchema, "Schema");
  }

  /**
   * @return The {@link Schema} to be used. Never <code>null</code>.
   */
  @Nonnull
  public Schema getSchema ()
  {
    return m_aSchema;
  }

  @Nonnull
  public EXMLValidationType getValidationType ()
  {
    return EXMLValidationType.XSD;
  }

  @Nonnull
  public IResourceErrorGroup validateXMLInstance (@Nullable final String sResourceName, @Nonnull final Source aXML)
  {
    try
    {
      return XMLSchemaValidationHelper.validate (m_aSchema, aXML);
    }
    catch (final IllegalArgumentException ex)
    {
      // Failed to read/parse XML
      final String sErrMsg = ex.getCause () != null ? ex.getCause ().getMessage () : "Failed to read/parse XML";
      return new ResourceErrorGroup (new ResourceError (new ResourceLocation (sResourceName),
                                                        EErrorLevel.FATAL_ERROR,
                                                        sErrMsg));
    }
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("schema", m_aSchema).toString ();
  }
}
