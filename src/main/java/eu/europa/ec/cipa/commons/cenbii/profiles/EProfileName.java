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

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Translatable;
import com.helger.commons.text.IMultilingualText;
import com.helger.commons.text.display.IHasDisplayText;
import com.helger.commons.text.resolve.DefaultTextResolver;
import com.helger.commons.text.util.TextHelper;

/**
 * Contains the names of the BII profiles for later translation.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
@Translatable
public enum EProfileName implements IHasDisplayText
{
 BII14 ("Prior Information Notice"),
 BII10 ("Tender Notification"),
 BII11 ("Qualification"),
 BII22 ("Call for Tender"),
 BII12 ("Tendering Simple"),
 BII01 ("Catalogue only"),
 BII02 ("Catalogue update"),
 BII16 ("Catalogue deletion"),
 BII17 ("Multi party catalogue"),
 BII18 ("Punch-out"),
 BII20 ("Customer Initiated Sourcing"),
 BII03 ("Basic Order Only"),
 BII04 ("Basic Invoice only"),
 BII23 ("Invoice only with Dispute"),
 BII05 ("Billing"),
 BII06 ("Procurement"),
 BII07 ("Procurement with Invoice dispute"),
 BII08 ("Billing with dispute and reminder"),
 BII13 ("Advanced Procurement with dispatch"),
 BII15 ("Scanned Invoice"),
 BII19 ("Advanced Procurement"),
 BII09 ("Customs Bill"),
 BII21 ("Statement"),
 BII24 ("Attached Document"),
 BII25 ("Status Request"),
 BII26 ("Retrieve Business Document");

  private final IMultilingualText m_aTP;

  private EProfileName (@Nonnull final String sEN)
  {
    m_aTP = TextHelper.create_EN (sEN);
  }

  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return DefaultTextResolver.getTextStatic (this, m_aTP, aContentLocale);
  }
}
