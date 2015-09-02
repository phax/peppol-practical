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
<!--Schematron tests for binding UBL and transaction T01-->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" is-a="T01" id="UBL-T01">
  <param name="BIICORE-T01-R000" value="contains(cbc:CustomizationID, 'urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0')" />
  <param name="BIICORE-T01-R001" value="not(count(//*[not(text())]) > 0)" />
  <param name="BIICORE-T01-R002" value="($Prerequisite1 and not(cbc:SalesOrderID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R003" value="($Prerequisite1 and not(cbc:CopyIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R004" value="($Prerequisite1 and not(cbc:UUID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R005" value="($Prerequisite1 and not(cbc:RequestedInvoiceCurrencyCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R006" value="($Prerequisite1 and not(cbc:PricingCurrencyCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R007" value="($Prerequisite1 and not(cbc:TaxCurrencyCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R008" value="($Prerequisite1 and not(cbc:CustomerReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R009" value="($Prerequisite1 and not(cbc:AccountingCostCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R010" value="($Prerequisite1 and not(cbc:LineCountNumeric)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R011" value="($Prerequisite1 and not(cac:ValidityPeriod/cbc:StartDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R012" value="($Prerequisite1 and not(cac:ValidityPeriod/cbc:StartTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R013" value="($Prerequisite1 and not(cac:ValidityPeriod/cbc:EndTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R014" value="($Prerequisite1 and not(cac:ValidityPeriod/cbc:DurationMeasure)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R015" value="($Prerequisite1 and not(cac:ValidityPeriod/cbc:Description)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R016" value="($Prerequisite1 and not(cac:ValidityPeriod/cbc:DescriptionCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R017" value="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R018" value="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:UUID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R019" value="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R020" value="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R021" value="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:DocumentType)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R022" value="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:XPath)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R023" value="($Prerequisite1 and not(cac:QuotationDocumentReference/cac:Attachment)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R024" value="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R025" value="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:UUID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R026" value="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R027" value="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R028" value="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:DocumentType)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R029" value="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:XPath)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R030" value="($Prerequisite1 and not(cac:OrderDocumentReference/cac:Attachment)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R031" value="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R032" value="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:UUID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R033" value="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R034" value="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R035" value="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:XPath)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R036" value="($Prerequisite1 and not(cac:OrderDocumentReference/cac:Attachment)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R037" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R038" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:UUID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R039" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R040" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R041" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:XPath)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R042" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:DocumentHash)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R043" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R044" value="($Prerequisite1 and not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R045" value="($Prerequisite1 and not(cac:Contract/cbc:IssueDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R046" value="($Prerequisite1 and not(cac:Contract/cbc:IssueTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R047" value="($Prerequisite1 and not(cac:Contract/cbc:ContractTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R048" value="($Prerequisite1 and not(cac:Contract/cac:ValidityPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R049" value="($Prerequisite1 and not(cac:Contract/cac:ContractDocumentReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R050" value="($Prerequisite1 and not(cac:Signature)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R051" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cbc:SupplierAssignedAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R052" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R053" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R054" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:DeliveryContact/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R055" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:DeliveryContact/cbc:Note)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R056" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:DeliveryContact/cac:OtherCommunication)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R057" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:AccountingContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R058" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:BuyerContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R059" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R060" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R061" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R062" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R063" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Language)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R064" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R065" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R066" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Floor)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R067" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Room)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R068" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:BlockName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R069" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R070" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R071" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R072" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R073" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R074" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R075" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R076" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Region)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R077" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:District)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R078" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R079" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:AddressLine)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R080" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R081" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R082" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R083" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R084" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R085" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R086" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R087" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R088" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R089" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R090" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R091" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R092" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R093" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R094" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R095" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R096" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R097" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R098" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R099" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R100" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R101" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R102" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R103" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R104" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:CountrySubentity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R105" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R106" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R107" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R108" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R109" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R110" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R111" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R112" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R113" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R114" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Postbox)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R115" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Floor)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R116" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Room)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R117" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:StreetName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R118" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R119" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R120" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:BuildingNumber)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R121" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Department)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R122" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R123" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R124" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:CityName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R125" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:PostalZone)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R126" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:CountrySubentity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R127" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R128" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Region)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R129" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:District)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R130" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cac:AddressLine)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R131" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R132" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R133" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R134" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R135" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R136" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R137" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R138" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R139" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R140" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R141" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R142" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R143" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R144" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R145" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R146" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R147" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R148" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R149" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R150" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R151" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R152" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R153" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R154" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R155" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R156" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R157" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R158" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R159" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R160" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R161" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R162" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R163" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R164" value="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R165" value="($Prerequisite1 and not(cac:SellerSupplierParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R166" value="($Prerequisite1 and not(cac:SellerSupplierParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R167" value="($Prerequisite1 and not(cac:SellerSupplierParty/cbc:DataSendingCapability)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R168" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:DespatchContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R169" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:AccountingContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R170" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:SellerContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R171" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R172" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R173" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R174" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R175" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Language)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R176" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R177" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R178" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Floor)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R179" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Room)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R180" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:BlockName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R181" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R182" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R183" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R184" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R185" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R186" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R187" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Region)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R188" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:District)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R189" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R190" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:AddressLine)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R191" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R192" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R193" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R194" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R195" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R196" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R197" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R198" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R199" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R200" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R201" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R202" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R203" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R204" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R205" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R206" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R207" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R208" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R209" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R210" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R211" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R212" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R213" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R214" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R215" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R216" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R217" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R218" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R219" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R220" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R221" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R222" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R223" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R224" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R225" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R226" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R227" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R228" value="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R229" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cbc:SupplierAssignedAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R230" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R231" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R232" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:DeliveryContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R233" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:AccountingContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R234" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:BuyerContact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R235" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R236" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R237" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R238" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R239" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Language)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R240" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:PostalAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R241" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R242" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:PartyTaxScheme)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R243" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:PartyLegalEntity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R244" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R245" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R246" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R247" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R248" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R249" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R250" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R251" value="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R252" value="($Prerequisite1 and not(cac:FreightForwarderParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R253" value="($Prerequisite1 and not(cac:AccountingCustomerParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R254" value="($Prerequisite1 and not(cac:Delivery/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R255" value="($Prerequisite1 and not(cac:Delivery/cbc:Quantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R256" value="($Prerequisite1 and not(cac:Delivery/cbc:MinimumQuantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R257" value="($Prerequisite1 and not(cac:Delivery/cbc:MaximumQuantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R258" value="($Prerequisite1 and not(cac:Delivery/cbc:ActualDeliveryTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R259" value="($Prerequisite1 and not(cac:Delivery/cbc:LatestDeliveryDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R260" value="($Prerequisite1 and not(cac:Delivery/cbc:LatestDeliveryTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R261" value="($Prerequisite1 and not(cac:Delivery/cbc:TrackingID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R262" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R263" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R264" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:Description)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R265" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:Conditions)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R266" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R267" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R268" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:ValidityPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R269" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R270" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R271" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Floor)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R272" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Room)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R273" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BlockName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R274" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R275" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:InhouseMail)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R276" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R277" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkAttention)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R278" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkCare)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R279" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PlotIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R280" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R281" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R282" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Region)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R283" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:District)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R284" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R285" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:AddressLine)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R286" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R287" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:LocationCoordinate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R288" value="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:StartTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R289" value="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:EndTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R290" value="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:DurationMeasure)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R291" value="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:DescriptionCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R292" value="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:Description)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R293" value="($Prerequisite1 and not(cac:Delivery/cac:PromisedDeliveryPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R294" value="($Prerequisite1 and not(cac:Delivery/cac:EstimatedDeliveryPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R295" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R296" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R297" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cbc:WebsiteURI)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R298" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R299" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:Language)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R300" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:PostalAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R301" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:PhysicalLocation)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R302" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:PartyTaxScheme)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R303" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:PartyLegalEntity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R304" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R306" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Note)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R307" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:OtherCommunication)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R308" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:Person)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R309" value="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:AgentParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R310" value="($Prerequisite1 and not(cac:Delivery/cac:Despatch)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R311" value="($Prerequisite1 and not(cac:DeliveryTerms/cbc:LossRiskResponsibilityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R312" value="($Prerequisite1 and not(cac:DeliveryTerms/cbc:LossRisk)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R313" value="($Prerequisite1 and not(cac:DeliveryTerms/cac:AllowanceCharge)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R314" value="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cbc:Description)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R315" value="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cbc:Conditions)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R316" value="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cbc:CountrySubentity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R317" value="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R318" value="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cac:ValidityPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R319" value="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cac:Address)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R320" value="($Prerequisite1 and not(cac:PaymentMeans)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R321" value="($Prerequisite1 and not(cac:TransactionConditions)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R322" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R323" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:AllowanceChargeReasonCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R324" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:MultiplierFactorNumeric)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R325" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:PrepaidIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R326" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:SequenceNumeric)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R327" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:BaseAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R328" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:AccountingCostCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R329" value="($Prerequisite1 and not(cac:AllowanceCharge/cbc:AccountingCost)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R330" value="($Prerequisite1 and not(cac:AllowanceCharge/cac:TaxCategory)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R331" value="($Prerequisite1 and not(cac:AllowanceCharge/cac:TaxTotal)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R332" value="($Prerequisite1 and not(cac:AllowanceCharge/cac:PaymentMeans)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R333" value="($Prerequisite1 and not(cac:DestinationCountry)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R334" value="($Prerequisite1 and not(cac:TaxTotal/cbc:RoundingAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R335" value="($Prerequisite1 and not(cac:TaxTotal/cbc:TaxEvidenceIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R336" value="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R337" value="($Prerequisite1 and not(cac:AnticipatedMonetaryTotal/cbc:TaxExclusiveAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R338" value="($Prerequisite1 and not(cac:AnticipatedMonetaryTotal/cbc:TaxInclusiveAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R339" value="($Prerequisite1 and not(cac:AnticipatedMonetaryTotal/cbc:PrepaidAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R340" value="($Prerequisite1 and not(cac:AnticipatedMonetaryTotal/cbc:PayableRoundingAmount)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R341" value="($Prerequisite1 and not(cac:OrderLine/cbc:SubstitutionStatusCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R342" value="($Prerequisite1 and not(cac:OrderLine/cac:SellerProposedSubstituteLineItem)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R343" value="($Prerequisite1 and not(cac:OrderLine/cac:SellerSubstitutedLineItem)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R344" value="($Prerequisite1 and not(cac:OrderLine/cac:BuyerProposedSubstituteLineItem)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R345" value="($Prerequisite1 and not(cac:OrderLine/cac:CatalogueLineReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R346" value="($Prerequisite1 and not(cac:OrderLine/cac:QuotationLineReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R347" value="($Prerequisite1 and not(cac:OrderLine/cac:DocumentReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R348" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:SalesOrderID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R349" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:Note)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R350" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:UUID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R351" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:LineStatusCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R352" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:MinimumQuantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R353" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:MaximumQuantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R354" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:MinimumBackorderQuantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R355" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:MaximumBackorderQuantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R356" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:InspectionMethodCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R357" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:BackOrderAllowedIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R358" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:AccountingCostCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R359" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:ID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R360" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:Quantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R361" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:MinimumQuantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R362" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:MaximumQuantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R363" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:ActualDeliveryTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R364" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:LatestDeliveryDate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R365" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:LatestDeliveryTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R366" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:TrackingID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R367" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:DeliveryAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R368" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:DeliveryLocation)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R369" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:StartTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R370" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:EndTime)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R371" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:DurationMeasure)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R372" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:DescriptionCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R373" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:Description)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R374" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:PromisedDeliveryPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R375" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:EstimatedDeliveryPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R376" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:DeliveryParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R377" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:Despatch)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R378" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:DeliveryTerms)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R379" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R380" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R381" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:WebsiteURI)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R382" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R383" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:EndpointID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R384" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:Language)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R385" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:PostalAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R386" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:PhysicalLocation)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R387" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:PartyTaxScheme)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R388" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:PartyLegalEntity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R389" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:Contact)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R390" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:Person)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R391" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:AgentParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R392" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OrderedShipment)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R393" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:PricingReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R394" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:AllowanceCharge)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R395" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cbc:PriceChangeReason)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R396" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cbc:PriceTypeCode)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R397" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cbc:PriceType)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R398" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cbc:OrderableUnitFactorRate)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R399" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cac:ValidityPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R400" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cac:PriceList)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R401" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cac:AllowanceCharge)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R402" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:PackQuantity)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R403" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:PackSizeNumeric)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R404" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:CatalogueIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R405" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:HazardousRiskIndicator)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R406" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:AdditionalInformation)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R407" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:Keyword)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R408" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:BrandName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R409" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:ModelName)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R410" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:ExtendedID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R411" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:PhysycalAttribute)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R412" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:MeasurementDimension)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R413" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:IssuerParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R414" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:ExtendedID)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R415" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:PhysycalAttribute)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R416" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:MeasurementDimension)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R417" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:IssuerParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R418" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:BuyersItemIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R419" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:CommodityClassification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R420" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ManufacturersItemIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R421" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:CatalogueItemIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R422" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:AdditionalItemIdentification)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R423" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:CatalogueDocumentReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R424" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R425" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:OriginCountry)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R426" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:TransactionConditions)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R427" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:HazardousItem)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R428" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ManufacturerParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R429" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:InformationContentProviderParty)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R430" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:OriginAddress)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R431" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ItemInstance)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R432" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ClassifiedTaxCategory)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R433" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:AdditionalItemProperty/cac:UsabilityPeriod)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R434" value="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:AdditionalItemProperty/cac:ItemPropertyGroup)) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R435" value="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)" />
  <param name="BIICORE-T01-R436" value="($Prerequisite2 and count(cac:PartyName)=1) or not ($Prerequisite2)" />
  <param name="BIICORE-T01-R437" value="($Prerequisite2 and count(cac:PartyLegalEntity)&lt;=1) or not ($Prerequisite2)" />
  <param name="BIICORE-T01-R438" value="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)" />
  <param name="BIICORE-T01-R439" value="($Prerequisite2 and count(cac:PartyName)=1) or not ($Prerequisite2)" />
  <param name="BIICORE-T01-R440" value="($Prerequisite2 and count(cac:PartyLegalEntity)&lt;=1) or not ($Prerequisite2)" />
  <param name="BIICORE-T01-R441" value="($Prerequisite2 and count(cbc:Description)&lt;=1) or not ($Prerequisite2)" />
  <param name="BIICORE-T01-R442" value="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)" />
  <param name="BIICORE-T01-R443" value="($Prerequisite2 and count(cac:PartyName)&lt;=1) or not ($Prerequisite2)" />
  <param name="BIICORE-T01-R444" value="($Prerequisite1 and count(cbc:Note)&lt;=1) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R445" value="($Prerequisite1 and count(cac:ValidityPeriod)&lt;=1) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R446" value="($Prerequisite1 and count(cac:OrderDocumentReference)&lt;=1) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R447" value="($Prerequisite1 and count(cac:Contract)&lt;=1) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R448" value="($Prerequisite1 and count(cac:Delivery)&lt;=1) or not ($Prerequisite1)" />
  <param name="BIICORE-T01-R449" value="($Prerequisite2 and count(cac:PartyName)&lt;=1) or not ($Prerequisite2)" />
  <param name="Order" value="/ubl:Order" />
  <param name="Customer" value="/ubl:Order/cac:BuyerCustomerParty/cac:Party" />
  <param name="Supplier" value="/ubl:Order/cac:SellerSupplierParty/cac:Party" />
  <param name="OrderLine" value="/ubl:Order/cac:OrderLine" />
  <param name="Item" value="/ubl:Order/cac:OrderLine/cac:LineItem/cac:Item" />
  <param name="Monetary_Total" value="/ubl:Order/cac:AnticipatedLegalMonetaryTotal" />
  <param name="Originator" value="/ubl:Order/cac:OriginatoCustomerParty" />
  <param name="Delivery_Party" value="/ubl:Order/cac:Delivery/cac:DeliveryParty" />
</pattern>
