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
import java.util.Locale;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsImmutableObject;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.text.display.IHasDisplayText;

/**
 * Defines the predefined CEN BII profiles. Each profile consists of a set of
 * collaborations ({@link ECollaboration}) and belongs to a group {@link EGroup}
 * .<br>
 * Source: http://www.cen.eu/cwa/bii/specs/Profiles/IndexWG1.html
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public enum EProfile implements IHasDisplayText
{
  BII14 (EGroup.PUBLICATION, EProfileName.BII14, 14, new ECollaboration [] { ECollaboration.COLL018 }),
  BII10 (EGroup.PUBLICATION, EProfileName.BII10, 10, new ECollaboration [] { ECollaboration.COLL019,
                                                                             ECollaboration.COLL023 }),
  BII11 (EGroup.TENDERING, EProfileName.BII11, 11, new ECollaboration [] { ECollaboration.COLL021 }),
  BII22 (EGroup.TENDERING, EProfileName.BII22, 22, new ECollaboration [] { ECollaboration.COLL020 }),
  BII12 (EGroup.TENDERING, EProfileName.BII12, 12, new ECollaboration [] { ECollaboration.COLL022 }),
  BII01 (EGroup.SOURCING, EProfileName.BII01, 1, new ECollaboration [] { ECollaboration.COLL009 }),
  BII02 (EGroup.SOURCING, EProfileName.BII02, 2, new ECollaboration [] { ECollaboration.COLL010,
                                                                         ECollaboration.COLL011 }),
  BII16 (EGroup.SOURCING, EProfileName.BII16, 16, new ECollaboration [] { ECollaboration.COLL012 }),
  BII17 (EGroup.SOURCING, EProfileName.BII17, 17, new ECollaboration [] { ECollaboration.COLL008,
                                                                          ECollaboration.COLL026 }),
  BII18 (EGroup.SOURCING, EProfileName.BII18, 18, new ECollaboration [] { ECollaboration.COLL027 }),
  BII20 (EGroup.SOURCING, EProfileName.BII20, 20, new ECollaboration [] { ECollaboration.COLL013,
                                                                          ECollaboration.COLL027 }),
  BII03 (EGroup.ORDERING_AND_BILLING, EProfileName.BII03, 3, new ECollaboration [] { ECollaboration.COLL001 }),
  BII04 (EGroup.ORDERING_AND_BILLING, EProfileName.BII04, 4, new ECollaboration [] { ECollaboration.COLL004 }),
  BII23 (EGroup.ORDERING_AND_BILLING, EProfileName.BII23, 23, new ECollaboration [] { ECollaboration.COLL004,
                                                                                      ECollaboration.COLL029 }),
  BII05 (EGroup.ORDERING_AND_BILLING, EProfileName.BII05, 5, new ECollaboration [] { ECollaboration.COLL004,
                                                                                     ECollaboration.COLL005 }),
  BII06 (EGroup.ORDERING_AND_BILLING, EProfileName.BII06, 6, new ECollaboration [] { ECollaboration.COLL001,
                                                                                     ECollaboration.COLL003,
                                                                                     ECollaboration.COLL004,
                                                                                     ECollaboration.COLL005 }),
  BII07 (EGroup.ORDERING_AND_BILLING, EProfileName.BII07, 7, new ECollaboration [] { ECollaboration.COLL001,
                                                                                     ECollaboration.COLL003,
                                                                                     ECollaboration.COLL004,
                                                                                     ECollaboration.COLL029,
                                                                                     ECollaboration.COLL005 }),
  BII08 (EGroup.ORDERING_AND_BILLING, EProfileName.BII08, 8, new ECollaboration [] { ECollaboration.COLL004,
                                                                                     ECollaboration.COLL029,
                                                                                     ECollaboration.COLL005,
                                                                                     ECollaboration.COLL007 }),
  BII13 (EGroup.ORDERING_AND_BILLING, EProfileName.BII13, 13, new ECollaboration [] { ECollaboration.COLL001,
                                                                                      ECollaboration.COLL003,
                                                                                      ECollaboration.COLL002,
                                                                                      ECollaboration.COLL028,
                                                                                      ECollaboration.COLL006,
                                                                                      ECollaboration.COLL004,
                                                                                      ECollaboration.COLL029,
                                                                                      ECollaboration.COLL005,
                                                                                      ECollaboration.COLL007 }),
  BII15 (EGroup.ORDERING_AND_BILLING, EProfileName.BII15, 15, new ECollaboration [] { ECollaboration.COLL031 }),
  BII19 (EGroup.ORDERING_AND_BILLING, EProfileName.BII19, 19, new ECollaboration [] { ECollaboration.COLL001,
                                                                                      ECollaboration.COLL003,
                                                                                      ECollaboration.COLL002,
                                                                                      ECollaboration.COLL028,
                                                                                      ECollaboration.COLL004,
                                                                                      ECollaboration.COLL029,
                                                                                      ECollaboration.COLL005,
                                                                                      ECollaboration.COLL007 }),
  BII09 (EGroup.SUPPORT, EProfileName.BII09, 9, new ECollaboration [] { ECollaboration.COLL030 }),
  BII21 (EGroup.SUPPORT, EProfileName.BII21, 21, new ECollaboration [] { ECollaboration.COLL014 }),
  BII24 (EGroup.SUPPORT, EProfileName.BII24, 24, new ECollaboration [] { ECollaboration.COLL017 }),
  BII25 (EGroup.SUPPORT, EProfileName.BII25, 25, new ECollaboration [] { ECollaboration.COLL015 }),
  BII26 (EGroup.SUPPORT, EProfileName.BII26, 26, new ECollaboration [] { ECollaboration.COLL016 });

  private final EGroup m_eGroup;
  private final IHasDisplayText m_aName;
  private final int m_nNumber;
  private final List <ECollaboration> m_aCollaborations;
  private final boolean m_bInCoreSupported;

  private void _checkCollaborationCoreSupport ()
  {
    final boolean bFirstCollaborationSupported = m_aCollaborations.get (0).isInCoreSupported ();
    final int nMax = m_aCollaborations.size ();
    for (int i = 1; i < nMax; i++)
      if (m_aCollaborations.get (i).isInCoreSupported () != bFirstCollaborationSupported)
        throw new IllegalStateException ("Different core support states for " + toString ());
  }

  private EProfile (@Nonnull final EGroup eGroup,
                    @Nonnull final EProfileName eName,
                    @Nonnegative final int nNumber,
                    @Nonnull @Nonempty final ECollaboration [] aCollaborations)
  {
    m_eGroup = eGroup;
    m_aName = eName;
    m_nNumber = nNumber;
    m_aCollaborations = new CommonsArrayList<> (aCollaborations).getAsUnmodifiable ();
    // All collaborations in a profile must share the same state
    m_bInCoreSupported = m_aCollaborations.get (0).isInCoreSupported ();
    if (GlobalDebug.isDebugMode ())
      _checkCollaborationCoreSupport ();
  }

  /**
   * @return The CEN BII group to which this profile belongs.
   */
  @Nonnull
  public EGroup getGroup ()
  {
    return m_eGroup;
  }

  /**
   * @return The display name of this profile in the specified locale. Currently
   *         only English is supported.
   */
  @Nullable
  public String getDisplayText (@Nonnull final Locale aContentLocale)
  {
    return m_aName.getDisplayText (aContentLocale);
  }

  /**
   * @return The numeric value of this profile (e.g. BII04 returns 4, BII22
   *         returns 22 etc.)
   */
  @Nonnegative
  public int getNumber ()
  {
    return m_nNumber;
  }

  /**
   * @return A non-<code>null</code> non empty list of all collaborations
   *         contained in this profile.
   */
  @Nonnull
  @Nonempty
  @ReturnsImmutableObject
  public List <ECollaboration> getAllCollaborations ()
  {
    return m_aCollaborations;
  }

  /**
   * Check if the passed collaboration is contained in this profile.
   *
   * @param eCollaboration
   *        The collaboration to query. May be <code>null</code>.
   * @return <code>true</code> if the passed collaboration is contained in this
   *         profile, <code>false</code> otherwise.
   */
  public boolean containsCollaboration (@Nullable final ECollaboration eCollaboration)
  {
    return m_aCollaborations.contains (eCollaboration);
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
   * Get a list with all profiles supporting a certain collaboration.
   *
   * @param eCollaboration
   *        The collaboration to be searched. May not be <code>null</code>.
   * @return A non-<code>null</code> non-empty list with all collaborations. It
   *         may never be empty, because each transaction must occur in at least
   *         one collaboration.
   */
  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsList <EProfile> getAllProfilesWithCollaboration (@Nonnull final ECollaboration eCollaboration)
  {
    ValueEnforcer.notNull (eCollaboration, "Collaboration");

    final ICommonsList <EProfile> ret = new CommonsArrayList<> ();
    for (final EProfile eProfile : values ())
      if (eProfile.containsCollaboration (eCollaboration))
        ret.add (eProfile);
    return ret;
  }
}
