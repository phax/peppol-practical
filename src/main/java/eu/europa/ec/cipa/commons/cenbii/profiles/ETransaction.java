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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;
import com.helger.commons.name.IHasName;

/**
 * Represents a single CEN BII transaction used in several collaborations.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public enum ETransaction implements IHasID <String>,IHasName
{
 T01 ("SubmitOrder", 1, true),
 T02 ("RejectOrder", 2, true),
 T03 ("AcceptOrder", 3, true),
 T04 ("SubmitCounterOffer", 4, true),
 T05 ("RejectCounterOffer", 5, true),
 T06 ("AcceptCounterOffer", 6, true),
 T07 ("CustomsBill", 7, true),
 T08 ("CustomsCreditNote", 8, true),
 T09 ("CustomsCorrectiveBill", 9, true),
 T10 ("SubmitInvoice", 10, true),
 T13 ("InvoiceDispute", 13, true),
 T14 ("CorrectWithCredit", 14, true),
 T15 ("CorrectWithDebit", 15, true),
 T16 ("ProvideDispatchAdvice", 16, true),
 T17 ("IssueReminder", 17, true),
 T18 ("SendCatalogueRequest", 18, true),
 T19 ("SubmitCatalogue", 19, true),
 T20 ("SubmitCatalogueItemUpdate", 20, true),
 T21 ("SubmitCataloguePriceUpdate", 21, true),
 T22 ("RequestCatalogueDelete", 22, true),
 T23 ("ConfirmCatalogueDelete", 23, true),
 T24 ("RequestQuote", 24, true),
 T25 ("RejectQuoteRequest", 25, true),
 T26 ("ProvideStatement", 26, true),
 T27 ("StatusRequest", 27, false),
 T28 ("RetrieveResponse", 28, false),
 T29 ("StatusResponse", 29, false),
 T30 ("AcceptAttachedDocument", 30, false),
 T31 ("RetrieveRequest", 31, false),
 T32 ("RejectAttachedDocument", 32, false),
 T33 ("SubmitAttachedDocument", 33, false),
 T34 ("NotifyPriorInformationNotice", 34, true),
 T35 ("ConfirmPriorInformationNoticePublication", 35, true),
 T36 ("RejectPriorInfomationNoticePublication", 36, true),
 T37 ("NotifyContractNotice", 37, true),
 T38 ("ConfirmContractNoticePublication", 38, true),
 T39 ("RejectContractNoticePublication", 39, true),
 T40 ("ProvideCallForTender", 40, false),
 T41 ("ProvideQualification", 41, false),
 T42 ("RejectQualification", 42, false),
 T43 ("AcceptQualification", 43, false),
 T44 ("SubmitTender", 44, false),
 T45 ("NotifyTenderReception", 45, false),
 T46 ("NotifyContractAwardNotice", 46, true),
 T47 ("ConfirmContracAwardNoticePublication", 47, true),
 T48 ("RejectContractAwardNoticePublication", 48, true),
 T51 ("RejectStatement", 51, true),
 T52 ("ScannedInvoice", 52, true),
 T53 ("ScannedCreditNote", 53, true),
 T54 ("MultiPartyCatalogue", 54, true),
 T55 ("RejectCatalogueRequest", 55, true),
 T56 ("ProvideQuote", 56, true),
 T57 ("AcceptCatalogue", 57, true),
 T58 ("RejectCatalogue", 58, true),
 T59 ("RejectCatalogueItemUpdate", 59, true),
 T60 ("AcceptCatalogueItemUpdate", 60, true),
 T61 ("RejectCataloguePriceUpdate", 61, true),
 T62 ("AcceptCataloguePriceUpdate", 62, true),
 T63 ("RequestRescan", 63, true);

  private final String m_sID;
  private final String m_sName;
  private final int m_nNumber;
  private final boolean m_bInCoreSupported;

  private ETransaction (@Nonnull @Nonempty final String sName,
                        @Nonnegative final int nNumber,
                        final boolean bInCoreSupported)
  {
    m_sID = name ();
    m_sName = sName;
    m_nNumber = nNumber;
    m_bInCoreSupported = bInCoreSupported;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return The pseudo name of this transaction. Does not require translation.
   */
  @Nonnull
  @Nonempty
  public String getName ()
  {
    return m_sName;
  }

  /**
   * @return The number of this transaction.
   */
  @Nonnegative
  public int getNumber ()
  {
    return m_nNumber;
  }

  /**
   * @return <code>true</code> if this object is part of the core data model,
   *         <code>false</code> if it is only contained in the full data model.
   */
  public boolean isInCoreSupported ()
  {
    return m_bInCoreSupported;
  }

  @Nullable
  public static ETransaction getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (ETransaction.class, sID);
  }
}
