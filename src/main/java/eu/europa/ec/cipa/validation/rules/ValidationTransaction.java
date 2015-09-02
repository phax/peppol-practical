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
import javax.annotation.concurrent.Immutable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

import eu.europa.ec.cipa.commons.cenbii.profiles.ETransaction;

/**
 * This is the default implementation of the {@link IValidationTransaction}
 * interface. It represents a single "validation transaction" consisting of a
 * syntax binding (see {@link EValidationSyntaxBinding}) and a CEN BII
 * transaction.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Immutable
public class ValidationTransaction implements IValidationTransaction
{
  private final IValidationSyntaxBinding m_aSyntaxBinding;
  private final ETransaction m_eTransaction;

  /**
   * Constructor
   *
   * @param aSyntaxBinding
   *        Syntax binding to use. May not be <code>null</code>.
   * @param eTransaction
   *        BII transaction to use. May not be <code>null</code>.
   */
  public ValidationTransaction (@Nonnull final IValidationSyntaxBinding aSyntaxBinding,
                                @Nonnull final ETransaction eTransaction)
  {
    ValueEnforcer.notNull (aSyntaxBinding, "SyntaxBinding");
    ValueEnforcer.notNull (eTransaction, "TransactionID");

    m_aSyntaxBinding = aSyntaxBinding;
    m_eTransaction = eTransaction;
  }

  @Nonnull
  public IValidationSyntaxBinding getSyntaxBinding ()
  {
    return m_aSyntaxBinding;
  }

  @Nonnull
  public ETransaction getTransaction ()
  {
    return m_eTransaction;
  }

  @Nonnull
  public String getAsString ()
  {
    return m_aSyntaxBinding.getFileNamePart () + "-" + m_eTransaction.name ();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final ValidationTransaction rhs = (ValidationTransaction) o;
    return m_aSyntaxBinding.equals (rhs.m_aSyntaxBinding) && m_eTransaction.equals (rhs.m_eTransaction);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aSyntaxBinding).append (m_eTransaction).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("syntaxBinding", m_aSyntaxBinding)
                                       .append ("transaction", m_eTransaction)
                                       .toString ();
  }

  /**
   * This is a shortcut method for creating a transaction using the UBL syntax
   * binding.
   *
   * @param eTransaction
   *        The transaction to use. May not be <code>null</code>.
   * @return The non-<code>null</code> object.
   */
  @Nonnull
  public static IValidationTransaction createUBLTransaction (@Nonnull final ETransaction eTransaction)
  {
    return new ValidationTransaction (EValidationSyntaxBinding.UBL, eTransaction);
  }
}
