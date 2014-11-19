/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.peppol.page.pub;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.appbasics.security.login.LoggedInUserManager;
import com.helger.bootstrap3.button.BootstrapButton;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.form.BootstrapForm;
import com.helger.bootstrap3.form.BootstrapFormGroup;
import com.helger.bootstrap3.label.BootstrapLabel;
import com.helger.bootstrap3.label.EBootstrapLabelType;
import com.helger.bootstrap3.table.BootstrapTable;
import com.helger.bootstrap3.table.BootstrapTableFormView;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.compare.ESortOrder;
import com.helger.commons.string.StringHelper;
import com.helger.commons.url.ISimpleURL;
import com.helger.commons.url.SimpleURL;
import com.helger.datetime.format.PDTToString;
import com.helger.html.hc.CHCParam;
import com.helger.html.hc.html.AbstractHCForm;
import com.helger.html.hc.html.HCA;
import com.helger.html.hc.html.HCCol;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.html.HCRow;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.app.menu.CMenuPublic;
import com.helger.peppol.mgr.MetaManager;
import com.helger.peppol.page.AbstractAppFormPage;
import com.helger.peppol.testep.domain.TestEndpoint;
import com.helger.peppol.testep.domain.TestEndpointManager;
import com.helger.validation.error.FormErrors;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webctrls.custom.EDefaultIcon;
import com.helger.webctrls.custom.toolbar.IButtonToolbar;
import com.helger.webctrls.datatables.DataTables;

public class PagePublicToolsTestEndpoints extends AbstractAppFormPage <TestEndpoint>
{
  private static final String FIELD_COMPANY_NAME = "companyname";
  private static final String FIELD_CONTACT_PERSON = "contactperson";
  private static final String FIELD_PARTICIPANT_ID_SCHEME = "participantidscheme";
  private static final String FIELD_PARTICIPANT_ID_VALUE = "participantidvalue";
  private static final String FIELD_TRANSPORT_PROFILE = "transportprofile";

  public PagePublicToolsTestEndpoints (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Test endpoints");
  }

  @Nonnull
  private static SimpleURL _createParticipantInfoURL (@Nonnull final WebPageExecutionContext aWPEC,
                                                      @Nonnull final TestEndpoint aTestEndpoint)
  {
    return aWPEC.getLinkToMenuItem (CMenuPublic.MENU_TOOLS_PARTICIPANT_INFO)
                .add (CHCParam.PARAM_ACTION, ACTION_PERFORM)
                .add (PagePublicToolsParticipantInformation.FIELD_ID_SCHEME, aTestEndpoint.getParticipantIDScheme ())
                .add (PagePublicToolsParticipantInformation.FIELD_ID_VALUE, aTestEndpoint.getParticipantIDValue ());
  }

  @Override
  protected TestEndpoint getSelectedObject (@Nonnull final WebPageExecutionContext aWPEC, @Nullable final String sID)
  {
    final TestEndpointManager aTestEndpointMgr = MetaManager.getTestEndpointMgr ();
    return aTestEndpointMgr.getTestEndpointOfID (sID);
  }

  @Override
  protected void modifyViewToolbar (@Nonnull final WebPageExecutionContext aWPEC,
                                    @Nonnull final TestEndpoint aSelectedObject,
                                    @Nonnull final IButtonToolbar <?> aToolbar)
  {
    aToolbar.addChild (new BootstrapButton ().setOnClick (_createParticipantInfoURL (aWPEC, aSelectedObject))
                                             .addChild ("Show participant information")
                                             .setIcon (EDefaultIcon.MAGNIFIER));
  }

  @Override
  protected void showSelectedObject (@Nonnull final WebPageExecutionContext aWPEC,
                                     @Nonnull final TestEndpoint aSelectedObject)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();

    final BootstrapTableFormView aTable = aNodeList.addAndReturnChild (new BootstrapTableFormView (new HCCol (170),
                                                                                                   HCCol.star ()));
    aTable.createItemRow ()
          .setLabel ("Creation date")
          .setCtrl (PDTToString.getAsString (aSelectedObject.getCreationDateTime (), aDisplayLocale));
    aTable.createItemRow ().setLabel ("Company name").setCtrl (aSelectedObject.getCompanyName ());
    if (StringHelper.hasText (aSelectedObject.getContactPerson ()))
      aTable.createItemRow ().setLabel ("Contact person").setCtrl (aSelectedObject.getContactPerson ());
    aTable.createItemRow ()
          .setLabel ("Participant information")
          .setCtrl (aSelectedObject.getParticipantIdentifier ().getURIEncoded ());
    aTable.createItemRow ().setLabel ("Transport profile").setCtrl (aSelectedObject.getTransportProfile ().name ());
  }

  @Override
  protected void validateAndSaveInputParameters (@Nonnull final WebPageExecutionContext aWPEC,
                                                 @Nullable final TestEndpoint aSelectedObject,
                                                 @Nonnull final FormErrors aFormErrors,
                                                 final boolean bEdit)
  {}

  @Override
  protected void showInputForm (@Nonnull final WebPageExecutionContext aWPEC,
                                @Nullable final TestEndpoint aSelectedObject,
                                @Nonnull final AbstractHCForm <?> aForm,
                                final boolean bEdit,
                                final boolean bCopy,
                                @Nonnull final FormErrors aFormErrors)
  {
    final BootstrapForm aRealForm = (BootstrapForm) aForm;

    aRealForm.addFormGroup (new BootstrapFormGroup ().setLabelAlternative ("Company name")
                                                     .setCtrl (new HCEdit (new RequestField (FIELD_COMPANY_NAME,
                                                                                             aSelectedObject == null ? null
                                                                                                                    : aSelectedObject.getCompanyName ())))
                                                     .setHelpText ("The name of the company operating the test AccessPoint")
                                                     .setErrorList (aFormErrors.getListOfField (FIELD_COMPANY_NAME)));
  }

  @Override
  protected void showListOfExistingObjects (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final TestEndpointManager aTestEndpointMgr = MetaManager.getTestEndpointMgr ();

    // Toolbar on top
    final BootstrapButtonToolbar aToolbar = aNodeList.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
    if (LoggedInUserManager.getInstance ().isUserLoggedInInCurrentSession ())
      aToolbar.addButtonNew ("Create new test endpoint", createCreateURL (aWPEC));
    else
      aToolbar.addChild (new BootstrapLabel (EBootstrapLabelType.INFO).addChild ("You need to be logged in to create test endpoints."));

    // List existing
    final BootstrapTable aTable = new BootstrapTable (HCCol.star (), HCCol.star (), HCCol.star (), createActionCol (2)).setID (getID ());
    aTable.addHeaderRow ().addCells ("Participant ID", "Company", "Transport profile", "Actions");

    for (final TestEndpoint aCurObject : aTestEndpointMgr.getAllTestEndpoints ())
    {
      final ISimpleURL aViewLink = createViewURL (aWPEC, aCurObject);

      final HCRow aRow = aTable.addBodyRow ();
      aRow.addCell (new HCA (aViewLink).addChild (aCurObject.getParticipantIdentifier ().getValue ()));
      aRow.addCell (aCurObject.getCompanyName ());
      aRow.addCell (aCurObject.getTransportProfile ().name ());
      aRow.addCell (new HCA (_createParticipantInfoURL (aWPEC, aCurObject)).setTitle ("Show participant information")
                                                                           .addChild (EDefaultIcon.MAGNIFIER.getAsNode ()));
    }
    aNodeList.addChild (aTable);

    final DataTables aDataTables = getStyler ().createDefaultDataTables (aWPEC, aTable);
    aDataTables.getOrCreateColumnOfTarget (3).addClass (CSS_CLASS_ACTION_COL).setSortable (false);
    aDataTables.setInitialSorting (1, ESortOrder.ASCENDING);
    aNodeList.addChild (aDataTables);
  }
}
