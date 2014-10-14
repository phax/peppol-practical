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

import java.net.InetAddress;
import java.net.URL;
import java.util.Locale;

import javax.annotation.Nonnull;

import org.busdox.servicemetadata.publishing._1.ServiceGroupType;

import com.helger.bootstrap3.alert.BootstrapErrorBox;
import com.helger.bootstrap3.button.BootstrapButtonToolbar;
import com.helger.bootstrap3.table.BootstrapTableForm;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.string.StringHelper;
import com.helger.html.hc.CHCParam;
import com.helger.html.hc.html.AbstractHCForm;
import com.helger.html.hc.html.HCCode;
import com.helger.html.hc.html.HCCol;
import com.helger.html.hc.html.HCDiv;
import com.helger.html.hc.html.HCEdit;
import com.helger.html.hc.impl.HCNodeList;
import com.helger.peppol.page.IdentifierIssuingAgencySelect;
import com.helger.validation.error.FormErrors;
import com.helger.web.dns.IPV4Addr;
import com.helger.webbasics.app.page.WebPageExecutionContext;
import com.helger.webbasics.form.RequestField;
import com.helger.webpages.AbstractWebPageExt;

import eu.europa.ec.cipa.peppol.identifier.issuingagency.EPredefinedIdentifierIssuingAgency;
import eu.europa.ec.cipa.peppol.identifier.participant.SimpleParticipantIdentifier;
import eu.europa.ec.cipa.peppol.sml.ESML;
import eu.europa.ec.cipa.smp.client.SMPServiceCallerReadonly;

public class PagePublicParticipantInformation extends AbstractWebPageExt <WebPageExecutionContext>
{
  private static final String FIELD_IDTYPE = "idtype";
  private static final String FIELD_IDVALUE = "idvalue";

  public PagePublicParticipantInformation (@Nonnull @Nonempty final String sID)
  {
    super (sID, "Participant information");
  }

  private static EPredefinedIdentifierIssuingAgency _getIIA (final String s)
  {
    if (StringHelper.hasText (s))
      for (final EPredefinedIdentifierIssuingAgency eAgency : EPredefinedIdentifierIssuingAgency.values ())
        if (eAgency.getSchemeID ().equals (s))
          return eAgency;
    return null;
  }

  @Override
  protected void fillContent (@Nonnull final WebPageExecutionContext aWPEC)
  {
    final HCNodeList aNodeList = aWPEC.getNodeList ();
    final Locale aDisplayLocale = aWPEC.getDisplayLocale ();
    final FormErrors aFormErrors = new FormErrors ();
    boolean bShowInput = true;

    if (aWPEC.hasAction (ACTION_SAVE))
    {
      // Validate fields
      final String sIDType = aWPEC.getAttributeAsString (FIELD_IDTYPE);
      final EPredefinedIdentifierIssuingAgency eAgency = _getIIA (sIDType);
      final String sIDValue = aWPEC.getAttributeAsString (FIELD_IDVALUE);

      if (StringHelper.hasNoText (sIDType))
        aFormErrors.addFieldError (FIELD_IDTYPE, "Please select an identifier type");
      else
        if (eAgency == null)
          aFormErrors.addFieldError (FIELD_IDTYPE, "Please select a valid identifier type");

      if (StringHelper.hasNoText (sIDValue))
        aFormErrors.addFieldError (FIELD_IDVALUE, "Please provide an identifier value");

      if (!aFormErrors.hasErrorsOrWarnings ())
      {
        bShowInput = false;

        final SimpleParticipantIdentifier aParticipantID = eAgency.createParticipantIdentifier (sIDValue);
        final SMPServiceCallerReadonly aSMPClient = new SMPServiceCallerReadonly (aParticipantID, ESML.PRODUCTION);
        try
        {
          final URL aSMPHost = aSMPClient.getSMPHost ().toURL ();
          final InetAddress aInetAddress = InetAddress.getByName (aSMPHost.getHost ());
          final InetAddress aNice = InetAddress.getByAddress (aInetAddress.getAddress ());
          aNodeList.addChild (new HCDiv ().addChild ("Querying the following SMP:"));
          aNodeList.addChild (new HCDiv ().addChild ("PEPPOL name: ")
                                          .addChild (new HCCode ().addChild (aSMPHost.toExternalForm ())));
          aNodeList.addChild (new HCDiv ().addChild ("Nice name: ")
                                          .addChild (new HCCode ().addChild (aNice.getCanonicalHostName ())));
          aNodeList.addChild (new HCDiv ().addChild ("IP address ")
                                          .addChild (new HCCode ().addChild (new IPV4Addr (aInetAddress).getAsString ())));
          final ServiceGroupType aSG = aSMPClient.getServiceGroupOrNull (aParticipantID);
        }
        catch (final Exception ex)
        {
          aNodeList.addChild (new BootstrapErrorBox ().addChild (new HCDiv ().addChild ("Error querying SMP."))
                                                      .addChild (new HCDiv ().addChild ("Technical details: " +
                                                                                        ex.getMessage ())));
        }
      }
    }

    if (bShowInput)
    {
      final AbstractHCForm <?> aForm = aNodeList.addAndReturnChild (createFormSelf (aWPEC));
      final BootstrapTableForm aTable = aForm.addAndReturnChild (new BootstrapTableForm (new HCCol (170), HCCol.star ()));
      aTable.createItemRow ()
            .setLabelMandatory ("Identifier type")
            .setCtrl (new IdentifierIssuingAgencySelect (new RequestField (FIELD_IDTYPE), aDisplayLocale))
            .setErrorList (aFormErrors.getListOfField (FIELD_IDTYPE));
      aTable.createItemRow ()
            .setLabelMandatory ("Identifier value")
            .setCtrl (new HCEdit (new RequestField (FIELD_IDVALUE)))
            .setErrorList (aFormErrors.getListOfField (FIELD_IDVALUE));

      final BootstrapButtonToolbar aToolbar = aForm.addAndReturnChild (new BootstrapButtonToolbar (aWPEC));
      aToolbar.addHiddenField (CHCParam.PARAM_ACTION, ACTION_SAVE);
      aToolbar.addSubmitButton ("Show details");
    }
  }
}
