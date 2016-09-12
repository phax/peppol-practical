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

import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.error.SingleError;
import com.helger.commons.error.list.ErrorList;
import com.helger.commons.error.list.IErrorList;
import com.helger.commons.error.location.ErrorLocation;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.string.ToStringGenerator;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.SchematronHelper;
import com.helger.schematron.pure.SchematronResourcePure;

/**
 * Implementation of the {@link IXMLValidator} for XML Schema.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public class XMLSchematronValidator extends AbstractXMLValidator
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (XMLSchematronValidator.class);

  private final ISchematronResource m_aSchematronRes;

  public XMLSchematronValidator (@Nonnull final ISchematronResource aSchematronRes)
  {
    ValueEnforcer.notNull (aSchematronRes, "SchematronResource");
    if (!aSchematronRes.isValidSchematron ())
      throw new IllegalArgumentException ("Passed schematronResource is invalid Schematron: " + aSchematronRes);
    m_aSchematronRes = aSchematronRes;
  }

  @Nonnull
  public EXMLValidationType getValidationType ()
  {
    return EXMLValidationType.SCHEMATRON;
  }

  @Nonnull
  public ISchematronResource getSchematronResource ()
  {
    return m_aSchematronRes;
  }

  @Nonnull
  public IReadableResource getValidatingResource ()
  {
    return m_aSchematronRes.getResource ();
  }

  @Nonnull
  public IErrorList validateXMLInstance (@Nullable final String sResourceName, @Nonnull final Source aXML)
  {
    SchematronOutputType aSVRL = null;
    String sErrorMsg = "Internal error";
    try
    {
      aSVRL = SchematronHelper.applySchematron (m_aSchematronRes, aXML);
    }
    catch (final Exception ex)
    {
      // Validation failed - whysoever
      s_aLogger.error ("Failed to apply Schematron", ex);
      sErrorMsg += ": resolve any previous errors and try again";
    }
    if (aSVRL == null)
      return new ErrorList (SingleError.builderFatalError ()
                                       .setErrorLocation (new ErrorLocation (sResourceName))
                                       .setErrorText (sErrorMsg)
                                       .build ());

    // Returns a list of SVRLResourceError objects!
    return SchematronHelper.convertToErrorList (aSVRL, sResourceName);
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("schematronRes", m_aSchematronRes).toString ();
  }

  @Nonnull
  public static XMLSchematronValidator createFromSCHPure (@Nonnull final IReadableResource aSCH)
  {
    return new XMLSchematronValidator (new SchematronResourcePure (aSCH));
  }
}
