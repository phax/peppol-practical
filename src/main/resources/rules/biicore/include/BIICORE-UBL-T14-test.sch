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
<!--Schematron tests for binding UBL and transaction T14-->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T14" id="UBL-T14">
  <param name="BIICORE-T14-R000" value="contains(cbc:CustomizationID, 'urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0')" />
  <param name="BIICORE-T14-R001" value="not(count(//*[not(text())]) > 0)" />
  <param name="BIICORE-T14-R002" value="($Prerequisite1 and not(cbc:CopyIndicator) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R003" value="($Prerequisite1 and not(cbc:UUID) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R004" value="($Prerequisite1 and not(cbc:IssueTime) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R005" value="($Prerequisite1 and not(cbc:TaxPointDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R005" value="($Prerequisite1 and not(cbc:TaxCurrencyCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R006" value="($Prerequisite1 and not(cbc:PricingCurrencyCode) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R007" value="($Prerequisite1 and not(cbc:PaymentCurrencyCode) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R008" value="($Prerequisite1 and not(cbc:PaymentAlternativeCurrencyCode) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R009" value="($Prerequisite1 and not(cbc:AccountingCostCode) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R010" value="($Prerequisite1 and not(cbc:LineCountNumeric)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R011" value="($Prerequisite1 and not(cac:BillingReference/cac:SelfBilledInvoiceDocumentReference) or not(cac:BillingReference/cac:DebitNoteDocumentReference) or not(cac:BillingReference/cac:ReminderDocumentReference) or not(AdditionalDocumentReference) or not(BillingReferenceLine) or not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:CopyIndicator) or not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:UUID) or not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:IssueDate) or not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:DocumentTypeCode) or not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:DocumentType) or not(cac:BillingReference/cac:InvoiceDocumentReference/cbc:XPath) or not(cac:BillingReference/cac:InvoiceDocumentReference/cac:Attachment) or not(cac:BillingReference/cac:CreditNoteDocumentReference/cbc:CopyIndicator) or not(cac:BillingReference/cac:CreditNoteDocumentReference/cbc:UUID) or not(cac:BillingReference/cac:CreditNoteDocumentReference/cbc:IssueDate) or not(cac:BillingReference/cac:CreditNoteDocumentReference/cbc:DocumentTypeCode) or not(cac:BillingReference/cac:CreditNoteDocumentReference/cbc:DocumentType) or not(cac:BillingReference/cac:CreditNoteDocumentReference/cbc:XPath) or not(cac:BillingReference/cac:CreditNoteDocumentReference/cac:Attachment) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R012" value="($Prerequisite1 and not(cac:DespatchDocumentReference) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R013" value="($Prerequisite1 and not(cac:ReceiptDocumentReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R014" value="($Prerequisite1 and not(cac:OriginatorDocumentReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R015" value="($Prerequisite1 and not(cac:Signature)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R016" value="($Prerequisite1 and not(cac:BuyerCustomerParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R017" value="($Prerequisite1 and not(cac:SellerSupplierParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R018" value="($Prerequisite1 and not(cac:TaxRepresentativeParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R019" value="($Prerequisite1 and not(cac:DeliveryTerms)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R020" value="($Prerequisite1 and not(cac:PrepaidPayment)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R021" value="($Prerequisite1 and not(cac:TaxExchangeRate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R022" value="($Prerequisite1 and not(cac:PricingExchangeRate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R023" value="($Prerequisite1 and not(cac:PaymentExchangeRate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R024" value="($Prerequisite1 and not(cac:PaymentAlternativeExchangeRate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R025" value="($Prerequisite1 and not(cac:CreditNotePeriod/cbc:StartTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R026" value="($Prerequisite1 and not(cac:CreditNotePeriod/cbc:EndTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R027" value="($Prerequisite1 and not(cac:CreditNotePeriod/cbc:DurationMeasure)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R028" value="($Prerequisite1 and not(cac:CreditNotePeriod/cbc:Description)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R029" value="($Prerequisite1 and not(cac:CreditNotePeriod/cbc:DescriptionCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R030" value="($Prerequisite1 and not(cac:OrderReference/cbc:SalesOrderID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R031" value="($Prerequisite1 and not(cac:OrderReference/cbc:CopyIndicator) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R032" value="($Prerequisite1 and not(cac:OrderReference/cbc:UUID) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R033" value="($Prerequisite1 and not(cac:OrderReference/cbc:IssueDate) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R034" value="($Prerequisite1 and not(cac:OrderReference/cbc:IssueTime) ) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R035" value="($Prerequisite1 and not(cac:OrderReference/cbc:CustomerReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R036" value="($Prerequisite1 and not(cac:OrderReference/cac:DocumentReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R037" value="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R038" value="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:UUID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R039" value="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R040" value="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R041" value="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:XPath)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R042" value="($Prerequisite1 and not(cac:ContractDocumentReference/cac:Attachment)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R043" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:CooyIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R044" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:UUID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R045" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R046" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R047" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:XPath)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R048" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:DocumentHash)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R049" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R050" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R051" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R052" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R053" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cbc:DataSendingCapability)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R054" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:DespatchContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R055" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:AccountingContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R056" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:SellerContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R057" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R058" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R059" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R060" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R061" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Language)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R062" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R063" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R064" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Floor)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R065" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Room)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R066" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BlockName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R067" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R068" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R069" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R070" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R071" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R072" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R073" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Region)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R074" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:District)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R075" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R076" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:AddressLine)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R077" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R078" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R079" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R080" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R081" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R082" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R083" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R084" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R085" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R086" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R087" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R088" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R089" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R090" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R091" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R092" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R093" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R094" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R095" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R096" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R097" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R098" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R099" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R100" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R101" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R102" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R103" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R104" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R105" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R106" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R107" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R108" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R109" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R110" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R111" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R112" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R113" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R114" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R115" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R116" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R117" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R118" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R119" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R120" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R121" value="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R122" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cbc:SupplierAssignedAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R123" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R124" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R125" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:DeliveryContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R126" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:AccountingContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R127" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:BuyerContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R128" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R129" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R130" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R131" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R132" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Language)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R133" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R134" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R135" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Floor)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R136" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Room)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R137" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BlockName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R138" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R139" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R140" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R141" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R142" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R143" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R144" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Region)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R145" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:District)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R146" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R147" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:AddressLine)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R148" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R149" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R150" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R151" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R152" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R153" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R154" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R155" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R156" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R157" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R158" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R159" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R160" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R161" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R162" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R163" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R164" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R165" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R166" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R167" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R168" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R169" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R170" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R171" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R172" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R173" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R174" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R175" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R176" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R177" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R178" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R179" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R180" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R181" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R182" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R183" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R184" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R185" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R186" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R187" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R188" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R189" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R190" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R191" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R192" value="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R193" value="($Prerequisite1 and not(cac:PayeeParty/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R194" value="($Prerequisite1 and not(cac:PayeeParty/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R195" value="($Prerequisite1 and not(cac:PayeeParty/cbc:WebsiteURI)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R196" value="($Prerequisite1 and not(cac:PayeeParty/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R197" value="($Prerequisite1 and not(cac:PayeeParty/cac:Language)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R198" value="($Prerequisite1 and not(cac:PayeeParty/cac:PostalAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R199" value="($Prerequisite1 and not(cac:PayeeParty/cac:PhysicalLocation)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R200" value="($Prerequisite1 and not(cac:PayeeParty/cac:PartyTaxScheme)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R201" value="($Prerequisite1 and not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R202" value="($Prerequisite1 and not(cac:PayeeParty/cac:PartyLegalEntity/cac:RegistrationAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R203" value="($Prerequisite1 and not(cac:PayeeParty/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R204" value="($Prerequisite1 and not(cac:PayeeParty/cac:Contact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R205" value="($Prerequisite1 and not(cac:PayeeParty/cac:Person)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R206" value="($Prerequisite1 and not(cac:PayeeParty/cac:AgentParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R207" value="($Prerequisite1 and not(cac:Delivery/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R208" value="($Prerequisite1 and not(cac:Delivery/cbc:Quantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R209" value="($Prerequisite1 and not(cac:Delivery/cbc:MinimumQuantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R210" value="($Prerequisite1 and not(cac:Delivery/cbc:MaximumQuantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R211" value="($Prerequisite1 and not(cac:Delivery/cbc:ActualDeliveryTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R212" value="($Prerequisite1 and not(cac:Delivery/cbc:LatestDeliveryDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R213" value="($Prerequisite1 and not(cac:Delivery/cbc:LatestDeliveryTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R214" value="($Prerequisite1 and not(cac:Delivery/cbc:TrackingID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R215" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R216" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:Description)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R217" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:Conditions)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R218" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R219" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R220" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:ValidityPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R221" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R222" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R223" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Floor)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R224" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Room)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R225" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BlockName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R226" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R227" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:InhouseMail)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R228" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R229" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkAttention)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R230" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkCare)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R231" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PlotIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R232" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R233" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R234" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Region)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R235" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:District)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R236" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R237" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:AddressLine)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R238" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R239" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:LocationCoordinate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R240" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:RequestedDeliveryPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R241" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:PromisedDeliveryPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R242" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:EstimatedDeliveryPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R243" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:DeliveryParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R244" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Despatch)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R245" value="($Prerequisite1 and not(cac:PaymentMeans/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R246" value="($Prerequisite1 and not(cac:PaymentMeans/cbc:InstructionID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R247" value="($Prerequisite1 and not(cac:PaymentMeans/cbc:InstructionNote)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R248" value="($Prerequisite1 and not(cac:PaymentMeans/cac:CardAccount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R249" value="($Prerequisite1 and not(cac:PaymentMeans/cac:PayerFinancialAccount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R250" value="($Prerequisite1 and not(cac:PaymentMeans/cac:CreditAccount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R251" value="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R252" value="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AccountTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R253" value="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:CurrencyCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R254" value="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:Country)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R255" value="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R256" value="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:Address)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R257" value="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R258" value="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cac:Address)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R259" value="($Prerequisite1 and not(cac:PaymentTerms/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R260" value="($Prerequisite1 and not(cac:PaymentTerms/cbc:PaymentMeansID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R261" value="($Prerequisite1 and not(cac:PaymentTerms/cbc:PrepaidPaymentReferenceID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R262" value="($Prerequisite1 and not(cac:PaymentTerms/cbc:ReferenceEventCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R263" value="($Prerequisite1 and not(cac:PaymentTerms/cbc:SettlementDiscountPercent)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R264" value="($Prerequisite1 and not(cac:PaymentTerms/cbc:PenaltySurchargePercent)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R265" value="($Prerequisite1 and not(cac:PaymentTerms/cbc:Amount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R266" value="($Prerequisite1 and not(cac:PaymentTerms/cac:SettlementPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R267" value="($Prerequisite1 and not(cac:PaymentTerms/cac:PenaltyPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R268" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R269" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:AllowanceChargeReasonCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R270" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:MultiplierFactorNumeric)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R271" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:PrepaidIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R272" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:SequenceNumeric)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R273" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:BaseAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R274" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:AccountingCostCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R276" value="($Prerequisite1 and not(cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R277" value="($Prerequisite1 and not(cac:AllowanceCharge/cac:TaxTotal)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R278" value="($Prerequisite1 and not(cac:AllowanceCharge/cac:PaymentMeans)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R279" value="($Prerequisite1 and not(cac:TaxTotal/cbc:RoundingAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R280" value="($Prerequisite1 and not(cac:TaxTotal/cbc:TaxEvidenceIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R281" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:CalculationSequenceNumeric)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R282" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:TransactionCurrencyTaxAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R283" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:Percent)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R284" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:BaseUnitMeasure)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R285" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:PerUnitAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R286" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRange)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R287" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRatePercent)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R288" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R289" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:BaseUnitMeasure)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R290" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:PerUnitAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R291" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRange)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R292" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRatePercent)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R293" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R294" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R295" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R296" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R297" value="($Prerequisite1 and not(cac:CreditNoteLine/cbc:UUID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R298" value="($Prerequisite1 and not(cac:CreditNoteLine/cbc:TaxPointDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R299" value="($Prerequisite1 and not(cac:CreditNoteLine/cbc:AccountingCostCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R300" value="($Prerequisite1 and not(cac:CreditNoteLine/cbc:FreeOfChargeIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R301" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:OrderLineReference/cbc:SalesOrderLineID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R302" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:DespatchLineReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R303" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:ReceiptLineReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R304" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:BillingReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R305" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:DocumentReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R306" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:PricingReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R307" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:OriginatorParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R308" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Delivery)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R309" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:PaymentTerms)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R310" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:AllowanceCharge)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R321" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:TaxTotal/cbc:RoundingAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R322" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:TaxTotal/cbc:TaxEvidenceIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R323" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:TaxTotal/cac:TaxSubtotal)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R324" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cbc:PackQuantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R325" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cbc:PackSizeNumeric)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R326" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cbc:CatalogueIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R327" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cbc:HazardousRiskIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R328" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cbc:AdditionalInformation)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R329" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cbc:Keyword)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R330" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cbc:BrandName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R331" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cbc:ModelName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R332" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:SellersItemIdentification/cbc:ExtendedID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R333" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:SellersItemIdentification/cbc:PhysycalAttribute)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R334" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:SellersItemIdentification/cbc:MeasurementDimension)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R335" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:SellersItemIdentification/cbc:IssuerParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R336" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:StandardItemIdentification/cbc:ExtendedID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R337" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:StandardItemIdentification/cbc:PhysycalAttribute)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R338" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:StandardItemIdentification/cbc:MeasurementDimension)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R339" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:StandardItemIdentification/cbc:IssuerParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R340" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:BuyersItemIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R341" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:CommodityClassification/cbc:NatureCargo)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R342" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:CommodityClassification/cbc:CargoTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R343" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:CommodityClassification/cbc:CommodityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R344" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:ManufacturersItemIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R345" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:CatalogueItemIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R346" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:AdditionalItemIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R347" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:CatalogueDocumentReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R348" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:ItemSpecificationDocumentReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R349" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:OriginCountry)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R350" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TransactionConditions)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R351" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:HazardousItem)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R352" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:ManufacturerParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R353" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:InformationContentProviderParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R354" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:OriginAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R355" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:ItemInstance)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R356" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R357" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cbc:BaseUnitMeasure)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R358" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cbcPerUnitAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R359" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReasonCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R360" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReason)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R361" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cbc:TierRange)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R362" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cbc:TierRatePercent)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R363" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R364" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R365" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R366" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R367" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:UsabilityPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R368" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:ItemPropertyGroup)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R369" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cbc:PriceChangeReason)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R370" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cbc:PriceTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R371" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cbc:PriceType)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R372" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cbc:OrderableUnitFactorRate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R373" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cac:ValidityPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R374" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cac:PriceList)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R375" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R376" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R377" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cbc:PrepaidIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R378" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cbc:SequenceNumeric)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R379" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCostCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R380" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCost)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R381" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)&#10;) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R382" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:TaxTotal)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R383" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:Price/cac:AllowanceCharge/cac:PaymentMeans)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R384" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:OrderLineReference/cbc:UUID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R385" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:OrderLineReference/cbc:LineStatusCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T14-R386" value="($Prerequisite1 and not(cac:CreditNoteLine/cac:OrderLineReference/cac:OrderReference)) or not ($Prerequisite1)" />
  <param name="Credit_Note" value="/ubl:CreditNote" />
</pattern>
