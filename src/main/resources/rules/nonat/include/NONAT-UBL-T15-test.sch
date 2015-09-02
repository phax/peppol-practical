<?xml version="1.0" encoding="UTF-8"?>
<!--

    Version: MPL 1.1/EUPL 1.1

    The contents of this file are subject to the Mozilla Public License Version
    1.1 (the "License"); you may not use this file except in compliance with
    the License. You may obtain a copy of the License at:
    http://www.mozilla.org/MPL/

    Software distributed under the License is distributed on an "AS IS" basis,
    WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
    for the specific language governing rights and limitations under the
    License.

    The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)

    Alternatively, the contents of this file may be used under the
    terms of the EUPL, Version 1.1 or - as soon they will be approved
    by the European Commission - subsequent versions of the EUPL
    (the "Licence"); You may not use this work except in compliance
    with the Licence.
    You may obtain a copy of the Licence at:
    http://joinup.ec.europa.eu/software/page/eupl/licence-eupl

    Unless required by applicable law or agreed to in writing, software
    distributed under the Licence is distributed on an "AS IS" basis,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Licence for the specific language governing permissions and
    limitations under the Licence.

    If you wish to allow use of your version of this file only
    under the terms of the EUPL License and not to allow others to use
    your version of this file under the MPL, indicate your decision by
    deleting the provisions above and replace them with the notice and
    other provisions required by the EUPL License. If you do not delete
    the provisions above, a recipient may use your version of this file
    under either the MPL or the EUPL License.

-->
<!--This file is generated automatically! Do NOT edit!-->
<!--Schematron tests for binding UBL and transaction T15-->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T15" id="UBL-T15">
  <param name="NONAT-T15-R001" value="($Prerequisite1 and (cac:PartyLegalEntity/cbc:CompanyID != '')) or not ($Prerequisite1)" />
  <param name="NONAT-T15-R002" value="($Prerequisite1 and (//cac:PaymentMeans/cbc:PaymentDueDate != '')) or not ($Prerequisite1)" />
  <param name="NONAT-T15-R003" value="($Prerequisite1 and (//cac:Delivery/cbc:ActualDeliveryDate != '')) or not ($Prerequisite1)" />
  <param name="NONAT-T15-R004" value="($Prerequisite1 and //cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CityName and //cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PostalZone and //cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:IdentificationCode) or not ($Prerequisite1)" />
  <param name="NONAT-T15-R005" value="($Prerequisite1 and (cbc:InvoicedQuantity != '')) or not ($Prerequisite1)" />
  <param name="NONAT-T15-R006" value="($Prerequisite1 and (cac:PostalAddress/cbc:StreetName and cac:PostalAddress/cbc:CityName and cac:PostalAddress/cbc:PostalZone and cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or not ($Prerequisite1)" />
  <param name="NONAT-T15-R007" value="($Prerequisite1 and (cac:PostalAddress/cbc:StreetName and cac:PostalAddress/cbc:CityName and cac:PostalAddress/cbc:PostalZone and cac:PostalAddress/cac:Country/cbc:IdentificationCode)) or not ($Prerequisite1)" />
  <param name="Supplier_Party" value="//cac:AccountingSupplierParty/cac:Party" />
  <param name="Invoice_Line" value="//cac:InvoiceLine" />
  <param name="Invoice" value="/ubl:Invoice" />
  <param name="Customer_Party" value="//cac:AccountingCustomerParty/cac:Party" />
</pattern>
