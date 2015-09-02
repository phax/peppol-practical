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

import java.util.Collections;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.validation.Schema;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.io.resource.IReadableResource;

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;
import eu.europa.ec.cipa.validation.generic.IXMLValidator;
import eu.europa.ec.cipa.validation.generic.XMLSchemaValidator;
import eu.europa.ec.cipa.validation.generic.XMLSchematronValidator;
import eu.europa.ec.cipa.validation.rules.EValidationArtefact;
import eu.europa.ec.cipa.validation.rules.EValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.EValidationLevel;
import eu.europa.ec.cipa.validation.rules.IValidationArtefact;
import eu.europa.ec.cipa.validation.rules.IValidationDocumentType;
import eu.europa.ec.cipa.validation.rules.IValidationLevel;
import eu.europa.ec.cipa.validation.rules.IValidationSyntaxBinding;
import eu.europa.ec.cipa.validation.rules.IValidationTransaction;
import eu.europa.ec.cipa.validation.rules.ValidationTransaction;

/**
 * Second version of the validation pyramid - can handle industry and entity
 * specific artifacts much better. By default this validation pyramid is empty
 * compared to the old ValidationPyramid!
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@NotThreadSafe
public class ValidationPyramid2 extends AbstractValidationPyramid
{
  /**
   * Create a new validation pyramid with a specified country.
   *
   * @param aValidationDocumentType
   *        Document type. Determines the
   *        {@link EValidationLevel#TECHNICAL_STRUCTURE} layer. May not be
   *        <code>null</code>.
   * @param aValidationTransaction
   *        Transaction. May not be <code>null</code>.
   * @param aValidationCountry
   *        The validation country. May be <code>null</code> to use only the
   *        country independent validation levels (the first three levels).
   * @see EValidationDocumentType
   * @see ValidationTransaction
   */
  public ValidationPyramid2 (@Nonnull final IValidationDocumentType aValidationDocumentType,
                             @Nonnull final IValidationTransaction aValidationTransaction,
                             @Nullable final Locale aValidationCountry)
  {
    super (aValidationDocumentType, aValidationTransaction, aValidationCountry);
  }

  /**
   * Add the first 4 levels (technical structure, transaction requirements,
   * profile requirements and legal requirements) based on the provided document
   * type and transaction.
   *
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addDefaultLayers ()
  {
    // Check if an XML schema is present for the technical structure
    final Schema aXMLSchema = m_aValidationDocType.getSchema ();
    if (aXMLSchema != null)
    {
      // Add the XML schema validator first
      final XMLSchemaValidator aValidator = new XMLSchemaValidator (aXMLSchema);
      // true: If the XSD validation fails no Schematron validation is needed
      addValidationLayer (new ValidationPyramidLayer (EValidationLevel.TECHNICAL_STRUCTURE, aValidator, true));
    }

    final Locale aLookupCountry = m_aValidationCountry == null ? CGlobal.LOCALE_INDEPENDENT : m_aValidationCountry;

    // Iterate over all country independent validation levels in the correct
    // order + legal requirements - this means industry specific rules are not
    // automatically added!
    for (final IValidationLevel eLevel : EValidationLevel.getAllLevelsInValidationOrder ())
      if (eLevel.isLowerOrEqualLevelThan (EValidationLevel.LEGAL_REQUIREMENTS))
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
            addValidationLayer (new ValidationPyramidLayer (eLevel,
                                                            XMLSchematronValidator.createFromSCHPure (aSCH),
                                                            false));
          }
        }
      }
    return this;
  }

  /**
   * Add a new generic validation layer
   *
   * @param aLayer
   *        The layer to add. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addValidationLayer (@Nonnull final ValidationPyramidLayer aLayer)
  {
    ValueEnforcer.notNull (aLayer, "Layer");

    m_aValidationLayers.add (aLayer);
    // Sort validation layers, so that the basic layers always come first
    Collections.sort (m_aValidationLayers, new ComparatorValidationPyramidLayerByLevel ());
    return this;
  }

  /**
   * Add a new validation layer
   *
   * @param aValidationArtefact
   *        The artefact to add. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addValidationLayer (@Nonnull final IValidationArtefact aValidationArtefact)
  {
    ValueEnforcer.notNull (aValidationArtefact, "ValidationArtefact");

    if (!aValidationArtefact.getValidationDocumentType ().equals (m_aValidationDocType))
      throw new IllegalArgumentException ("The passed validation artefact belongs to a different document type than this pyramid (" +
                                          m_aValidationDocType.getID () +
                                          ")");

    if (!EqualsHelper.equals (aValidationArtefact.getValidationCountry (), m_aValidationCountry))
      throw new IllegalArgumentException ("The passed validation artefact belongs to a different country than this pyramid (" +
                                          m_aValidationCountry +
                                          ")");

    if (!aValidationArtefact.containsTransaction (m_aValidationTransaction.getTransaction ()))
      throw new IllegalArgumentException ("The passed validation artefact has no support for transaction " +
                                          m_aValidationTransaction.getTransaction ());

    // Build the corresponding validation
    IXMLValidator aValidator = null;
    switch (aValidationArtefact.getValidationType ())
    {
      case XSD:
        final IReadableResource aXSD = aValidationArtefact.getValidationXSDResource (m_aValidationTransaction);
        if (aXSD != null)
          aValidator = new XMLSchemaValidator (aXSD);
        break;
      case SCHEMATRON:
        final IReadableResource aSCH = aValidationArtefact.getValidationSchematronResource (m_aValidationTransaction);
        if (aSCH != null)
          aValidator = XMLSchematronValidator.createFromSCHPure (aSCH);
        break;
      default:
        throw new IllegalStateException ("Unsupported validation type " + aValidationArtefact.getValidationType ());
    }

    if (aValidator == null)
    {
      // No action needed because e.g. no UBL syntax binding is present
      return this;
    }

    final ValidationPyramidLayer aLayer = new ValidationPyramidLayer (aValidationArtefact.getValidationLevel (),
                                                                      aValidator,
                                                                      false);
    return addValidationLayer (aLayer);
  }

  /**
   * Add an industry specific XML Schema to the validation
   *
   * @param aXSD
   *        The XML schema to be validated. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addIndustrySpecificXSDLayer (@Nonnull final IReadableResource aXSD)
  {
    ValueEnforcer.notNull (aXSD, "XSD Resource");

    return addIndustrySpecificLayer (new XMLSchemaValidator (aXSD));
  }

  /**
   * Add an industry specific Schematron to the validation
   *
   * @param aSCH
   *        The Schematron to be validated. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addIndustrySpecificSchematronLayer (@Nonnull final IReadableResource aSCH)
  {
    ValueEnforcer.notNull (aSCH, "Schematron Resource");

    return addIndustrySpecificLayer (XMLSchematronValidator.createFromSCHPure (aSCH));
  }

  /**
   * Add an industry specific layer to the validation
   *
   * @param aValidator
   *        The validator to be applied on this layer. May not be
   *        <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addIndustrySpecificLayer (@Nonnull final IXMLValidator aValidator)
  {
    ValueEnforcer.notNull (aValidator, "Validator");

    final ValidationPyramidLayer aLayer = new ValidationPyramidLayer (EValidationLevel.INDUSTRY_SPECIFIC,
                                                                      aValidator,
                                                                      false);
    return addValidationLayer (aLayer);
  }

  /**
   * Add an entity specific XML Schema to the validation
   *
   * @param aXSD
   *        The XML schema to be validated. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addEntitySpecificXSDLayer (@Nonnull final IReadableResource aXSD)
  {
    ValueEnforcer.notNull (aXSD, "XSD Resource");

    return addEntitySpecificLayer (new XMLSchemaValidator (aXSD));
  }

  /**
   * Add an entity specific Schematron to the validation
   *
   * @param aSCH
   *        The Schematron to be validated. May not be <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addEntitySpecificSchematronLayer (@Nonnull final IReadableResource aSCH)
  {
    ValueEnforcer.notNull (aSCH, "Schematron Resource");

    return addEntitySpecificLayer (XMLSchematronValidator.createFromSCHPure (aSCH));
  }

  /**
   * Add an entity specific layer to the validation
   *
   * @param aValidator
   *        The validator to be applied on this layer. May not be
   *        <code>null</code>.
   * @return this
   */
  @Nonnull
  public ValidationPyramid2 addEntitySpecificLayer (@Nonnull final IXMLValidator aValidator)
  {
    ValueEnforcer.notNull (aValidator, "Validator");

    final ValidationPyramidLayer aLayer = new ValidationPyramidLayer (EValidationLevel.ENTITY_SPECIFC,
                                                                      aValidator,
                                                                      false);
    return addValidationLayer (aLayer);
  }

  /**
   * Factory method that creates a pre-filled validation pyramid that contains
   * the first 3 levels.
   *
   * @param aValidationDocumentType
   *        Document type. Determines the XML Schema of
   *        {@link EValidationLevel#TECHNICAL_STRUCTURE} layer. May not be
   *        <code>null</code>.
   * @param aValidationTransaction
   *        Transaction. May not be <code>null</code>.
   * @return The created validation pyramid and never <code>null</code>.
   */
  @Nonnull
  public static ValidationPyramid2 createDefault (@Nonnull final IValidationDocumentType aValidationDocumentType,
                                                  @Nonnull final IValidationTransaction aValidationTransaction)
  {
    return createDefault (aValidationDocumentType, aValidationTransaction, (Locale) null);
  }

  /**
   * Factory method that creates a pre-filled validation pyramid that contains
   * the first 3 or 4 levels.
   *
   * @param aValidationDocumentType
   *        Document type. Determines the XML Schema of
   *        {@link EValidationLevel#TECHNICAL_STRUCTURE} layer. May not be
   *        <code>null</code>.
   * @param aValidationSyntaxBinding
   *        Syntax binding to use. May not be <code>null</code>.
   * @param eValidationTransaction
   *        BII transaction to use. May not be <code>null</code>.
   * @param aValidationCountry
   *        The validation country. May be <code>null</code> to use only the
   *        country independent validation levels (the first three levels).
   * @return The created validation pyramid and never <code>null</code>.
   */
  @Nonnull
  public static ValidationPyramid2 createDefault (@Nonnull final IValidationDocumentType aValidationDocumentType,
                                                  @Nonnull final IValidationSyntaxBinding aValidationSyntaxBinding,
                                                  @Nonnull final ETransaction eValidationTransaction,
                                                  @Nullable final Locale aValidationCountry)
  {
    return createDefault (aValidationDocumentType,
                          new ValidationTransaction (aValidationSyntaxBinding, eValidationTransaction),
                          aValidationCountry);
  }

  /**
   * Factory method that creates a pre-filled validation pyramid that contains
   * the first 3 or 4 levels.
   *
   * @param aValidationDocumentType
   *        Document type. Determines the XML Schema of
   *        {@link EValidationLevel#TECHNICAL_STRUCTURE} layer. May not be
   *        <code>null</code>.
   * @param aValidationTransaction
   *        Transaction. May not be <code>null</code>.
   * @param aValidationCountry
   *        The validation country. May be <code>null</code> to use only the
   *        country independent validation levels (the first three levels).
   * @return The created validation pyramid and never <code>null</code>.
   * @see #addDefaultLayers()
   */
  @Nonnull
  public static ValidationPyramid2 createDefault (@Nonnull final IValidationDocumentType aValidationDocumentType,
                                                  @Nonnull final IValidationTransaction aValidationTransaction,
                                                  @Nullable final Locale aValidationCountry)
  {
    final ValidationPyramid2 ret = new ValidationPyramid2 (aValidationDocumentType,
                                                           aValidationTransaction,
                                                           aValidationCountry);
    // Add the first 3 or 4 layers
    ret.addDefaultLayers ();
    return ret;
  }
}
