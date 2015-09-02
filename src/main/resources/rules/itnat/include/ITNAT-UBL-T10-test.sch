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
<!--Schematron tests for binding UBL and transaction T10-->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T10" id="UBL-T10">
  <param name="IT-T10-R003" value="($Prerequisite1 and (cac:PartyTaxScheme/cbc:CompanyID[@schemeID = 'IT:VAT'] and cac:PartyName/cbc:Name)) or not ($Prerequisite1)" />
  <param name="IT-T10-R005" value="($Prerequisite1 and (cbc:StreetName and cbc:CityName and cbc:PostalZone and cbc:CountrySubentity and cac:Country/cbc:IdentificationCode)) or not ($Prerequisite1)" />
  <param name="IT-T10-R008" value="($Prerequisite1 and (cbc:StreetName and cbc:CityName and cbc:PostalZone and cbc:CountrySubentity and cac:Country/cbc:IdentificationCode)) or not ($Prerequisite1)" />
  <param name="IT-T10-R013" value="($Prerequisite1 and not(cac:PartyLegalEntity/cbc:CompanyID[@schemeID = 'IT:CC']) or (cac:PartyLegalEntity[cbc:CompanyID/@schemeID = 'IT:CC']/cac:CorporateRegistrationScheme/cac:JurisdictionRegionAddress/cbc:CountrySubentity) or (cac:PartyLegalEntity[cbc:CompanyID/@schemeID = 'IT:CC']/cac:CorporateRegistrationScheme/cac:JurisdictionRegionAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="IT-T10-R016" value="($Prerequisite1 and cbc:InvoiceTypeCode) or not ($Prerequisite1)" />
  <param name="IT-T10-R017" value="($Prerequisite1 and (cbc:ID and cbc:IssueDate and cbc:DocumentType)) or not ($Prerequisite1)" />
  <param name="IT-T10-R024" value="($Prerequisite1 and (cbc:InvoicedQuantity) and (cbc:InvoicedQuantity/@unitCode)) or not ($Prerequisite1)" />
  <param name="IT-T10-R031" value="($Prerequisite1 and (cac:Price/cbc:PriceAmount)) or not ($Prerequisite1)" />
  <param name="IT-T10-R032" value="($Prerequisite1 and (cbc:ID and cbc:IssueDate and cbc:DocumentType)) or not ($Prerequisite1)" />
  <param name="Invoice_Line" value="//cac:InvoiceLine" />
  <param name="Invoice" value="/ubl:Invoice" />
  <param name="Supplier_Party_Address" value="//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress" />
  <param name="Customer_Party_Address" value="//cac:AccountingCustomerParty/cac:Party/cac:PostalAddress" />
  <param name="Transport_Document" value="//cac:DespatchDocumentReference" />
  <param name="Tax_Representative_Party" value="//cac:TaxRepresentativeParty" />
  <param name="Supplier_Party" value="//cac:AccountingSupplierParty/cac:Party" />
  <param name="Line_Level_Transport_Document" value="//cac:DocumentReference" />
</pattern>
