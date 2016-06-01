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
package eu.europa.ec.cipa.commons.cenbii.profiles;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsImmutableObject;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.name.IHasName;

/**
 * Defines a single CEN BII collaboration, that is used in 1-n profiles (
 * {@link EProfile}). Each collaboration consists of 1-n transactions (process
 * steps, {@link ETransaction}).
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public enum ECollaboration implements IHasID <String>, IHasName
{
  COLL001 ("Ordering", new ETransaction [] { ETransaction.T01 }),
  COLL002 ("CounterOfferSubmission", new ETransaction [] { ETransaction.T04 }),
  COLL003 ("OrderResponse", new ETransaction [] { ETransaction.T02, ETransaction.T03 }),
  COLL004 ("Invoicing", new ETransaction [] { ETransaction.T10 }),
  COLL005 ("ResolveInvoiceDispute", new ETransaction [] { ETransaction.T14, ETransaction.T15 }),
  COLL006 ("DispatchAdvice", new ETransaction [] { ETransaction.T16 }),
  COLL007 ("Reminder", new ETransaction [] { ETransaction.T17 }),
  COLL008 ("RequestCatalogue", new ETransaction [] { ETransaction.T18, ETransaction.T55 }),
  COLL009 ("CatalogueSubmission", new ETransaction [] { ETransaction.T19, ETransaction.T57, ETransaction.T58 }),
  COLL010 ("CatalogueItemUpdate", new ETransaction [] { ETransaction.T20, ETransaction.T59, ETransaction.T60 }),
  COLL011 ("CataloguePriceUpdate", new ETransaction [] { ETransaction.T21, ETransaction.T61, ETransaction.T62 }),
  COLL012 ("CatalogueDelete", new ETransaction [] { ETransaction.T22, ETransaction.T23 }),
  COLL013 ("QuoteRequest", new ETransaction [] { ETransaction.T24, ETransaction.T25 }),
  COLL014 ("Statement", new ETransaction [] { ETransaction.T26, ETransaction.T51 }),
  COLL015 ("StatusRequest", new ETransaction [] { ETransaction.T27, ETransaction.T29 }),
  COLL016 ("RetrieveDocument", new ETransaction [] { ETransaction.T31, ETransaction.T28 }),
  COLL017 ("Attachment", new ETransaction [] { ETransaction.T33, ETransaction.T30, ETransaction.T32 }),
  COLL018 ("PriorInformationNotification", new ETransaction [] { ETransaction.T34,
                                                                 ETransaction.T35,
                                                                 ETransaction.T36 }),
  COLL019 ("ContractNotification", new ETransaction [] { ETransaction.T37, ETransaction.T38, ETransaction.T39 }),
  COLL020 ("CallForTender", new ETransaction [] { ETransaction.T40 }),
  COLL021 ("Qualification", new ETransaction [] { ETransaction.T41, ETransaction.T42, ETransaction.T43 }),
  COLL022 ("TenderSubmission", new ETransaction [] { ETransaction.T44, ETransaction.T45 }),
  COLL023 ("ContractAwardNotification", new ETransaction [] { ETransaction.T46, ETransaction.T47, ETransaction.T48 }),
  COLL026 ("MultiPartyCatalogue", new ETransaction [] { ETransaction.T54 }),
  COLL027 ("QuoteSubmission", new ETransaction [] { ETransaction.T56 }),
  COLL028 ("CounterOfferResponse", new ETransaction [] { ETransaction.T05, ETransaction.T06 }),
  COLL029 ("InvoiceDispute", new ETransaction [] { ETransaction.T13 }),
  COLL030 ("CustomsBilling", new ETransaction [] { ETransaction.T07, ETransaction.T08, ETransaction.T09 }),
  COLL031 ("ScannedInvoice", new ETransaction [] { ETransaction.T52, ETransaction.T53, ETransaction.T63 });

  private final String m_sID;
  private final String m_sName;
  private final List <ETransaction> m_aTransactions;
  private final boolean m_bInCoreSupported;

  private void _checkTransactionCoreSupport ()
  {
    final int nMax = m_aTransactions.size ();
    final boolean bSupported = m_aTransactions.get (0).isInCoreSupported ();
    for (int i = 1; i < nMax; i++)
      if (m_aTransactions.get (i).isInCoreSupported () != bSupported)
        throw new IllegalStateException ("Different core support states for " + toString ());
  }

  private ECollaboration (@Nonnull @Nonempty final String sName, @Nonnull @Nonempty final ETransaction [] aTransactions)
  {
    m_sID = name ();
    m_sName = sName;
    m_aTransactions = new CommonsArrayList<> (aTransactions).getAsUnmodifiable ();
    // All transactions in a collaboration must share the same state
    m_bInCoreSupported = m_aTransactions.get (0).isInCoreSupported ();
    if (GlobalDebug.isDebugMode ())
      _checkTransactionCoreSupport ();
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  /**
   * @return A non-<code>null</code> non empty list of all transactions
   *         contained in this collaboration.
   */
  @Nonnull
  @Nonempty
  @ReturnsImmutableObject
  public List <ETransaction> getAllTransactions ()
  {
    return m_aTransactions;
  }

  /**
   * Check if the passed transaction is contained in this collaboration.
   *
   * @param eTransaction
   *        The transaction to query. May be <code>null</code>.
   * @return <code>true</code> if the passed transaction is contained in this
   *         collaboration, <code>false</code> otherwise.
   */
  public boolean containsTransaction (@Nullable final ETransaction eTransaction)
  {
    return m_aTransactions.contains (eTransaction);
  }

  /**
   * @return <code>true</code> if this object is part of the core data model,
   *         <code>false</code> if it is only contained in the full data model.
   */
  public boolean isInCoreSupported ()
  {
    return m_bInCoreSupported;
  }

  /**
   * Get a list with all collaborations supporting a certain transaction.
   *
   * @param eTransaction
   *        The transaction to be searched. May not be <code>null</code>.
   * @return A non-<code>null</code> non-empty list with all collaborations. It
   *         may never be empty, because each transaction must occur in at least
   *         one collaboration.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <ECollaboration> getAllCollaborationsWithTransaction (@Nonnull final ETransaction eTransaction)
  {
    ValueEnforcer.notNull (eTransaction, "Transaction");

    final ICommonsList <ECollaboration> ret = new CommonsArrayList<> ();
    for (final ECollaboration eCollaboration : values ())
      if (eCollaboration.containsTransaction (eTransaction))
        ret.add (eCollaboration);
    return ret;
  }

  @Nullable
  public static ECollaboration getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (ECollaboration.class, sID);
  }
}
