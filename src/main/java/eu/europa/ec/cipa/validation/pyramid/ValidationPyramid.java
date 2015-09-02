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
package eu.europa.ec.cipa.validation.pyramid;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.validation.Schema;

import com.helger.commons.CGlobal;
import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.io.resource.IReadableResource;

import eu.europa.ec.cipa.validation.generic.XMLSchemaValidator;
import eu.europa.ec.cipa.validation.generic.XMLSchematronValidator;
import eu.europa.ec.cipa.validation.rules.EValidationArtefact;
import eu.europa.ec.cipa.validation.rules.EValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.EValidationLevel;
import eu.europa.ec.cipa.validation.rules.IValidationArtefact;
import eu.europa.ec.cipa.validation.rules.IValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.IValidationLevel;
import eu.europa.ec.cipa.validation.rules.IValidationTransaction;
import eu.europa.ec.cipa.validation.rules.ValidationTransaction;

/**
 * This class represents the PEPPOL validation pyramid. It evaluates all
 * applicable rules for a document based on the specified parameters. All
 * validation levels (see {@link EValidationLevel}) are handled.<br>
 * Note: the profile to be validated is automatically determined from the
 * profile contained in the UBL document. If a certain profile is present, it's
 * rules are applied when the corresponding level is applied.
 *
 * @deprecated See {@link ValidationPyramid2} - offers better support for
 *             validation layer selection!
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
@Deprecated
@DevelopersNote ("See ValidationPyramid2 - offers better support for validation layer selection!")
public class ValidationPyramid extends AbstractValidationPyramid
{
  /**
   * Create a new validation pyramid that is country independent and handles all
   * available levels.
   *
   * @param aValidationDocumentType
   *        Document type. Determines the XML Schema of
   *        {@link EValidationLevel#TECHNICAL_STRUCTURE} layer. May not be
   *        <code>null</code>.
   * @param aValidationTransaction
   *        Transaction. May not be <code>null</code>.
   * @see EValidationDocumentType
   * @see ValidationTransaction
   */
  public ValidationPyramid (@Nonnull final IValidationDocumentType aValidationDocumentType,
                            @Nonnull final IValidationTransaction aValidationTransaction)
  {
    this (aValidationDocumentType, aValidationTransaction, null);
  }

  /**
   * Create a new validation pyramid that handles all available levels.
   *
   * @param aValidationDocumentType
   *        Document type. Determines the XML Schema of
   *        {@link EValidationLevel#TECHNICAL_STRUCTURE} layer. May not be
   *        <code>null</code>.
   * @param aValidationTransaction
   *        Transaction. May not be <code>null</code>.
   * @param aValidationCountry
   *        The validation country. May be <code>null</code> to use only the
   *        country independent validation levels.
   * @see EValidationDocumentType
   * @see ValidationTransaction
   */
  public ValidationPyramid (@Nonnull final IValidationDocumentType aValidationDocumentType,
                            @Nonnull final IValidationTransaction aValidationTransaction,
                            @Nullable final Locale aValidationCountry)
  {
    this (aValidationDocumentType,
          aValidationTransaction,
          aValidationCountry,
          EValidationLevel.getAllLevelsInValidationOrder ());
  }

  /**
   * Create a new validation pyramid
   *
   * @param aValidationDocumentType
   *        Document type. Determines the XML Schema of
   *        {@link EValidationLevel#TECHNICAL_STRUCTURE} layer. May not be
   *        <code>null</code>.
   * @param aValidationTransaction
   *        Transaction. May not be <code>null</code>.
   * @param aValidationCountry
   *        The validation country. May be <code>null</code> to use only the
   *        country independent levels.
   * @param aValidationLevelsInOrder
   *        All validation levels to consider in the order they should be
   *        executed. May neither be <code>null</code> nor empty. See
   *        {@link EValidationLevel#getAllLevelsInValidationOrder(EValidationLevel...)}
   *        for ordering of levels.
   * @see EValidationDocumentType
   * @see ValidationTransaction
   */
  public ValidationPyramid (@Nonnull final IValidationDocumentType aValidationDocumentType,
                            @Nonnull final IValidationTransaction aValidationTransaction,
                            @Nullable final Locale aValidationCountry,
                            @Nonnull @Nonempty final List <? extends IValidationLevel> aValidationLevelsInOrder)
  {
    super (aValidationDocumentType, aValidationTransaction, aValidationCountry);
    if (CollectionHelper.isEmpty (aValidationLevelsInOrder))
      throw new IllegalArgumentException ("No validation levels passed!");

    // Check if an XML schema is present for the technical structure
    final Schema aXMLSchema = aValidationDocumentType.getSchema ();
    if (aXMLSchema != null)
    {
      // Add the XML schema validator first
      final XMLSchemaValidator aValidator = new XMLSchemaValidator (aXMLSchema);
      // true: If the XSD validation fails no Schematron validation is needed
      m_aValidationLayers.add (new ValidationPyramidLayer (EValidationLevel.TECHNICAL_STRUCTURE, aValidator, true));
    }

    final Locale aLookupCountry = m_aValidationCountry == null ? CGlobal.LOCALE_INDEPENDENT : m_aValidationCountry;

    // Iterate over all validation levels in the correct order
    for (final IValidationLevel eLevel : aValidationLevelsInOrder)
    {
      // Determine all validation artefacts that match
      for (final IValidationArtefact eArtefact : EValidationArtefact.getAllMatchingArtefacts (eLevel,
                                                                                              m_aValidationDocType,
                                                                                              aLookupCountry))
      {
        // Get the Schematron SCH for the specified transaction in this level
        final IReadableResource aSCH = eArtefact.getValidationSchematronResource (m_aValidationTransaction);
        if (aSCH != null)
        {
          // We found a matching layer
          m_aValidationLayers.add (new ValidationPyramidLayer (eLevel,
                                                               XMLSchematronValidator.createFromSCHPure (aSCH),
                                                               false));
        }
      }
    }
  }
}
