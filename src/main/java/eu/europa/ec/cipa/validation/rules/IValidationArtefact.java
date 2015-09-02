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

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.id.IHasID;
import com.helger.commons.io.resource.IReadableResource;

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;
import eu.europa.ec.cipa.validation.generic.EXMLValidationType;

/**
 * Interface for a single validation artefact.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 * @see EValidationArtefact
 */
public interface IValidationArtefact extends IHasID <String>
{
  /**
   * @return The validation level. May not be <code>null</code>.
   */
  @Nonnull
  IValidationLevel getValidationLevel ();

  /**
   * @return The validation type - XML Schema or Schematron. May not be
   *         <code>null</code>.
   */
  @Nonnull
  EXMLValidationType getValidationType ();

  /**
   * @return The document type to which this artefact can be applied. May not be
   *         <code>null</code>.
   */
  @Nonnull
  IValidationDocumentType getValidationDocumentType ();

  /**
   * @return The country for which this artefact is relevant. May be
   *         <code>null</code> for country-independent artefacts.
   */
  @Nullable
  Locale getValidationCountry ();

  /**
   * @return <code>true</code> if this artefact is country independent,
   *         <code>false</code> otherwise. This is a shortcut for
   *         <code>getValidationCountry () == null</code>.
   */
  boolean isValidationCountryIndependent ();

  /**
   * @return A set with all validation transactions supported by this artefact.
   *         Neither <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  Set <IValidationTransaction> getAllValidationTransactions ();

  /**
   * @return A set with all BII transaction supported by this artefact. Neither
   *         <code>null</code> nor empty.
   */
  @Nonnull
  @Nonempty
  @ReturnsMutableCopy
  Set <ETransaction> getAllTransactions ();

  /**
   * Check if the passed transaction is supported by this validation artefact.
   *
   * @param eTransaction
   *        The transaction to be searched. May be <code>null</code>.
   * @return <code>true</code> if the passed transaction is not
   *         <code>null</code> and is contained in this artefact
   */
  boolean containsTransaction (@Nullable ETransaction eTransaction);

  /**
   * Get the XML Schema resource (.XSD) of this artefact for the specified
   * transaction.
   *
   * @param aTransaction
   *        The transaction to be searched. May not be <code>null</code>.
   * @return <code>null</code> if no such transaction is present.
   */
  @Nullable
  IReadableResource getValidationXSDResource (@Nonnull IValidationTransaction aTransaction);

  /**
   * @return A list of all XML Schema resources (.XSD) for the current artefact.
   *         Never <code>null</code>. The returned list may be empty if this is
   *         a Schematron artifact.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <IReadableResource> getAllValidationXSDResources ();

  /**
   * Get the Schematron resource (.SCH) of this artefact for the specified
   * transaction.
   *
   * @param aTransaction
   *        The transaction to be searched. May not be <code>null</code>.
   * @return <code>null</code> if no such transaction is present.
   */
  @Nullable
  IReadableResource getValidationSchematronResource (@Nonnull IValidationTransaction aTransaction);

  /**
   * @return A list of all Schematron resources (.SCH) for the current artefact.
   *         Never <code>null</code>. The returned list may be empty if this is
   *         a XSD artifact.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <IReadableResource> getAllValidationSchematronResources ();
}
