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

import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.CGlobal;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ArrayHelper;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.CommonsHashSet;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.collection.ext.ICommonsSet;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.commons.io.resource.IReadableResource;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.locale.country.CountryCache;
import com.helger.commons.string.StringHelper;

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;
import eu.europa.ec.cipa.validation.generic.EXMLValidationType;

/**
 * Contains all available PEPPOL Schematron validation artefacts. XML Schema
 * artifacts need to be handled manually from the respective validation document
 * type!
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public enum EValidationArtefact implements IValidationArtefact
{
  // Technical structure:
  ORDER_BII_CORE (EValidationLevel.TECHNICAL_STRUCTURE, EValidationDocumentType.ORDER, "biicore", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T01))),
  INVOICE_BII_CORE (EValidationLevel.TECHNICAL_STRUCTURE, EValidationDocumentType.INVOICE, "biicore", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                                                                                  ValidationTransaction.createUBLTransaction (ETransaction.T15))),
  CREDITNOTE_BII_CORE (EValidationLevel.TECHNICAL_STRUCTURE, EValidationDocumentType.CREDIT_NOTE, "biicore", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T14))),

  // Transaction requirements:
  CATALOGUE_EU_GEN (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.CATALOGUE, "eugen", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T19))),
  ORDER_EU_GEN (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.ORDER, "eugen", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T01))),
  INVOICE_EU_GEN (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.INVOICE, "eugen", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                                                                                   ValidationTransaction.createUBLTransaction (ETransaction.T15))),
  CREDITNOTE_EU_GEN (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.CREDIT_NOTE, "eugen", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T14))),
  ORDER_BII_RULES (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.ORDER, "biirules", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T01))),
  ORDERRESPONSE_BII_RULES (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.ORDERRESPONSE, "biirules", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T02),
                                                                                                                                                     ValidationTransaction.createUBLTransaction (ETransaction.T03))),
  INVOICE_BII_RULES (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.INVOICE, "biirules", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                                                                                         ValidationTransaction.createUBLTransaction (ETransaction.T15))),
  CREDITNOTE_BII_RULES (EValidationLevel.TRANSACTION_REQUIREMENTS, EValidationDocumentType.CREDIT_NOTE, "biirules", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T14))),

  // Profile requirements:
  INVOICE_BII_PROFILES (EValidationLevel.PROFILE_REQUIREMENTS, EValidationDocumentType.INVOICE, "biiprofiles", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                                                                                           ValidationTransaction.createUBLTransaction (ETransaction.T15))),
  CREDITNOTE_BII_PROFILES (EValidationLevel.PROFILE_REQUIREMENTS, EValidationDocumentType.CREDIT_NOTE, "biiprofiles", null, ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T14))),

  // Legal requirements - per transaction and country only one artifact should
  // be present:
  INVOICE_AUSTRIA_NATIONAL (EValidationLevel.LEGAL_REQUIREMENTS, EValidationDocumentType.INVOICE, "atnat", CountryCache.getInstance ()
                                                                                                                       .getCountry ("AT"), ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T10))),
  INVOICE_DENMARK_NATIONAL (EValidationLevel.LEGAL_REQUIREMENTS, EValidationDocumentType.INVOICE, "dknat", CountryCache.getInstance ()
                                                                                                                       .getCountry ("DK"), ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T10))),
  INVOICE_ITALY_NATIONAL (EValidationLevel.LEGAL_REQUIREMENTS, EValidationDocumentType.INVOICE, "itnat", CountryCache.getInstance ()
                                                                                                                     .getCountry ("IT"), ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T10))),
  INVOICE_NORWAY_NATIONAL (EValidationLevel.LEGAL_REQUIREMENTS, EValidationDocumentType.INVOICE, "nonat", CountryCache.getInstance ()
                                                                                                                      .getCountry ("NO"), ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                                                                                                                ValidationTransaction.createUBLTransaction (ETransaction.T15),
                                                                                                                                                                ValidationTransaction.createUBLTransaction (ETransaction.T17))),
  CREDITNOTE_AUSTRIA_NATIONAL (EValidationLevel.LEGAL_REQUIREMENTS, EValidationDocumentType.CREDIT_NOTE, "atnat", CountryCache.getInstance ()
                                                                                                                              .getCountry ("AT"), ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T14))),
  CREDITNOTE_NORWAY_NATIONAL (EValidationLevel.LEGAL_REQUIREMENTS, EValidationDocumentType.CREDIT_NOTE, "nonat", CountryCache.getInstance ()
                                                                                                                             .getCountry ("NO"), ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T14))),

  // Industry specific - per transaction and country, multiple artifacts may be
  // present. They need to be identified by ID!
  INVOICE_AUSTRIA_GOVERNMENT (EValidationLevel.INDUSTRY_SPECIFIC, EValidationDocumentType.INVOICE, "atgov", CountryCache.getInstance ()
                                                                                                                        .getCountry ("AT"), ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T10))),
  INVOICE_NORWAY_GOVERNMENT (EValidationLevel.INDUSTRY_SPECIFIC, EValidationDocumentType.INVOICE, "nogov", CountryCache.getInstance ()
                                                                                                                       .getCountry ("NO"), ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T10),
                                                                                                                                                                 ValidationTransaction.createUBLTransaction (ETransaction.T15))),
  CREDITNOTE_AUSTRIA_GOVERNMENT (EValidationLevel.INDUSTRY_SPECIFIC, EValidationDocumentType.CREDIT_NOTE, "atgov", CountryCache.getInstance ()
                                                                                                                               .getCountry ("AT"), ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T14))),
  CREDITNOTE_NORWAY_GOVERNMENT (EValidationLevel.INDUSTRY_SPECIFIC, EValidationDocumentType.CREDIT_NOTE, "nogov", CountryCache.getInstance ()
                                                                                                                              .getCountry ("NO"), ArrayHelper.newArray (ValidationTransaction.createUBLTransaction (ETransaction.T14)));
  // Entity specific - no such default artefact is present

  private static final Logger s_aLogger = LoggerFactory.getLogger (EValidationArtefact.class);
  private static final String BASE_DIRECTORY = "/rules/";

  private final EValidationLevel m_eLevel;
  private final EValidationDocumentType m_eDocType;
  private final String m_sDirName;
  private final String m_sFileNamePrefix;
  private final Locale m_aCountry;
  private final ICommonsSet <IValidationTransaction> m_aTransactions;

  /**
   * Constructor for invoice validation artefacts.
   *
   * @param eLevel
   *        The validation level of this artefact. May not be <code>null</code>.
   * @param eDocType
   *        The document type of this artefact. May not be <code>null</code>.
   * @param sDirName
   *        The name of the directory with this document type. May neither be
   *        <code>null</code> nor empty.
   * @param aCountry
   *        An optional country for which this artefact set applies. May be
   *        <code>null</code> for country independent artefacts.
   * @param aTransactions
   *        The transactions that are available for this artefact. May neither
   *        be <code>null</code> nor empty.
   */
  private EValidationArtefact (@Nonnull final EValidationLevel eLevel,
                               @Nonnull final EValidationDocumentType eDocType,
                               @Nonnull @Nonempty final String sDirName,
                               @Nullable final Locale aCountry,
                               @Nonnull @Nonempty final IValidationTransaction [] aTransactions)
  {
    if (StringHelper.hasNoText (sDirName))
      throw new IllegalArgumentException ("dirName is empty");

    if (aCountry != null && !eLevel.canHaveCountrySpecificArtefacts ())
      throw new IllegalArgumentException ("The validation level " +
                                          eLevel +
                                          " cannot have country specific artefacts but the country '" +
                                          aCountry.getCountry () +
                                          "' is provided!");

    if (ArrayHelper.isEmpty (aTransactions))
      throw new IllegalArgumentException ("no transaction specified");

    // Check if all transactions are supported by the Core data set!
    for (final IValidationTransaction aTransaction : aTransactions)
      if (!aTransaction.getTransaction ().isInCoreSupported ())
        throw new IllegalArgumentException ("The transaction '" +
                                            aTransaction.getTransaction ().getID () +
                                            "' is not supported by the BII core data set!");

    m_eLevel = eLevel;
    m_eDocType = eDocType;
    m_sDirName = sDirName;
    m_sFileNamePrefix = sDirName.toUpperCase (Locale.US);
    m_aCountry = aCountry;
    m_aTransactions = new CommonsHashSet<> (aTransactions);
  }

  /**
   * Get the ID of this validation artifact. This corresponds to the directory
   * name, where the artifacts for the different transactions reside.
   */
  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sDirName;
  }

  @Nonnull
  public EValidationLevel getValidationLevel ()
  {
    return m_eLevel;
  }

  @Nonnull
  public EXMLValidationType getValidationType ()
  {
    // Always Schematron in here!
    return EXMLValidationType.SCHEMATRON;
  }

  @Nonnull
  public EValidationDocumentType getValidationDocumentType ()
  {
    return m_eDocType;
  }

  @Nullable
  public Locale getValidationCountry ()
  {
    return m_aCountry;
  }

  public boolean isValidationCountryIndependent ()
  {
    return m_aCountry == null;
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsSet <IValidationTransaction> getAllValidationTransactions ()
  {
    return m_aTransactions.getClone ();
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsSet <ETransaction> getAllTransactions ()
  {
    final ICommonsSet <ETransaction> ret = new CommonsHashSet<> ();
    for (final IValidationTransaction aTransaction : m_aTransactions)
      ret.add (aTransaction.getTransaction ());
    return ret;
  }

  public boolean containsTransaction (@Nullable final ETransaction eTransaction)
  {
    if (eTransaction != null)
      for (final IValidationTransaction aTransaction : m_aTransactions)
        if (aTransaction.getTransaction ().equals (eTransaction))
          return true;
    return false;
  }

  @Nullable
  public IReadableResource getValidationXSDResource (@Nonnull final IValidationTransaction aTransaction)
  {
    // Only Schematrons are contained
    return null;
  }

  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  public ICommonsList <IReadableResource> getAllValidationXSDResources ()
  {
    // Only Schematrons are contained
    return new CommonsArrayList<> ();
  }

  @Nullable
  public IReadableResource getValidationSchematronResource (@Nonnull final IValidationTransaction aTransaction)
  {
    ValueEnforcer.notNull (aTransaction, "Transaction");

    if (!m_aTransactions.contains (aTransaction))
    {
      s_aLogger.warn ("Validation artifact does not contain transaction: " + aTransaction.getAsString ());
      return null;
    }

    // Assemble the file name:
    // 1. The directory name of this artefact
    // 2. The file name itself:
    // 2.1. The directory name of this artefact in uppercase
    // 2.2. The syntax binding
    // 2.3. The transaction ID
    final String sFileName = BASE_DIRECTORY +
                             m_sDirName +
                             "/" +
                             m_sFileNamePrefix +
                             '-' +
                             aTransaction.getSyntaxBinding ().getFileNamePart () +
                             '-' +
                             aTransaction.getTransaction () +
                             ".sch";
    return new ClassPathResource (sFileName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IReadableResource> getAllValidationSchematronResources ()
  {
    final ICommonsList <IReadableResource> aList = new CommonsArrayList<> ();
    for (final IValidationTransaction aTransaction : m_aTransactions)
    {
      final IReadableResource aSCH = getValidationSchematronResource (aTransaction);
      // May be null on internal error
      if (aSCH != null)
        aList.add (aSCH);
    }
    return aList;
  }

  @Nullable
  public IReadableResource getValidationXSLTResource (@Nonnull final IValidationTransaction aTransaction)
  {
    ValueEnforcer.notNull (aTransaction, "Transaction");

    if (!m_aTransactions.contains (aTransaction))
    {
      s_aLogger.warn ("Validation artifact does not contain transaction: " + aTransaction.getAsString ());
      return null;
    }

    // Assemble the file name:
    // 1. The directory name of this artefact
    // 2. The file name itself:
    // 2.1. The directory name of this artefact in uppercase
    // 2.2. The syntax binding
    // 2.3. The transaction ID
    final String sFileName = BASE_DIRECTORY +
                             m_sDirName +
                             "/" +
                             m_sFileNamePrefix +
                             '-' +
                             aTransaction.getSyntaxBinding ().getFileNamePart () +
                             '-' +
                             aTransaction.getTransaction () +
                             ".xslt";
    return new ClassPathResource (sFileName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IReadableResource> getAllValidationXSLTResources ()
  {
    final ICommonsList <IReadableResource> aList = new CommonsArrayList<> ();
    for (final IValidationTransaction aTransaction : m_aTransactions)
    {
      final IReadableResource aXSLT = getValidationXSLTResource (aTransaction);
      // May be null on internal error
      if (aXSLT != null)
        aList.add (aXSLT);
    }
    return aList;
  }

  /**
   * Shortcut for <code>getAllMatchingArtefacts (aLevel, null, null)</code>
   *
   * @param aLevel
   *        The desired validation level. If it is <code>null</code> all
   *        artefacts are considered.
   * @return A non-<code>null</code> list of all matching validation artefacts
   */
  @Nonnull
  @ReturnsMutableCopy
  public static List <EValidationArtefact> getAllArtefactsOfLevel (@Nullable final IValidationLevel aLevel)
  {
    return getAllMatchingArtefacts (aLevel, null, null);
  }

  /**
   * Get all matching artefacts, in the correct order.
   *
   * @param aLevel
   *        The desired validation level. If it is <code>null</code> all levels
   *        are considered.
   * @param aDocType
   *        The desired document type. If it is <code>null</code> all document
   *        types are considered.
   * @param aCountry
   *        The desired country. If the country of an artefact does not matter
   *        pass in <code>null</code>. If you want to have only the artefacts
   *        that are not country dependent and do NOT belong to a specific
   *        country, pass in {@link CGlobal#LOCALE_INDEPENDENT}.
   * @return A non-<code>null</code> list of all matching validation artefacts
   * @see EValidationLevel#canHaveCountrySpecificArtefacts()
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <EValidationArtefact> getAllMatchingArtefacts (@Nullable final IValidationLevel aLevel,
                                                                            @Nullable final IValidationDocumentType aDocType,
                                                                            @Nullable final Locale aCountry)
  {
    final ICommonsList <EValidationArtefact> ret = new CommonsArrayList<> ();
    for (final EValidationArtefact eArtefact : values ())
    {
      // Does the level match?
      if (aLevel == null || eArtefact.getValidationLevel ().equals (aLevel))
      {
        // Does the document type match?
        if (aDocType == null || eArtefact.getValidationDocumentType ().equals (aDocType))
        {
          // Does the country match?
          if (!eArtefact.getValidationLevel ().canHaveCountrySpecificArtefacts () ||
              aCountry == null ||
              aCountry.equals (eArtefact.getValidationCountry ()))
          {
            // Finally a match :)
            ret.add (eArtefact);
          }
        }
      }
    }
    return ret;
  }

  /**
   * @return A set of all countries (Locale objects) for which at least one
   *         special artefact is contained. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static Set <Locale> getAllCountriesWithSpecialRules ()
  {
    return getAllCountriesWithSpecialRules (null, null);
  }

  /**
   * Get a set of all countries that have specific rules, matching the
   * parameters.
   *
   * @param aLevel
   *        The desired validation level. If it is <code>null</code> all levels
   *        are considered.
   * @param aDocType
   *        The desired document type. If it is <code>null</code> all document
   *        types are considered.
   * @return A set of all countries (Locale objects) for which at least one
   *         special artefact is contained. Never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsSet <Locale> getAllCountriesWithSpecialRules (@Nullable final IValidationLevel aLevel,
                                                                      @Nullable final IValidationDocumentType aDocType)
  {
    final ICommonsSet <Locale> ret = new CommonsHashSet<> ();
    for (final IValidationArtefact eArtefact : values ())
    {
      // Is the artefact country dependent?
      if (!eArtefact.isValidationCountryIndependent ())
      {
        // Does the validation level match?
        if (aLevel == null || eArtefact.getValidationLevel ().equals (aLevel))
        {
          // Does the document type match?
          if (aDocType == null || eArtefact.getValidationDocumentType ().equals (aDocType))
          {
            // We found a match
            ret.add (eArtefact.getValidationCountry ());
          }
        }
      }
    }
    return ret;
  }

  /**
   * Get all artefacts that have rules for the specified transaction.
   *
   * @param eTransaction
   *        The transaction to search. May be <code>null</code>.
   * @return A non-<code>null</code> list with all artefacts supporting the
   *         specified transaction.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <EValidationArtefact> getAllArtefactsForTransaction (@Nullable final ETransaction eTransaction)
  {
    final ICommonsList <EValidationArtefact> ret = new CommonsArrayList<> ();
    for (final EValidationArtefact eArtefact : values ())
      if (eArtefact.containsTransaction (eTransaction))
        ret.add (eArtefact);
    return ret;
  }

  @Nullable
  public static EValidationArtefact getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EValidationArtefact.class, sID);
  }
}
