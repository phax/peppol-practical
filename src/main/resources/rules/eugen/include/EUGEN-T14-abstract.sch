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
<!--Abstract Schematron rules for T14-->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="T14">
  <rule context="$Tax_Category">
    <assert flag="fatal" test="$EUGEN-T14-R012">[EUGEN-T14-R012]-For each tax subcategory the category ID and the applicable tax percentage MUST be provided.</assert>
  </rule>
  <rule context="$Customer_Party">
    <assert flag="warning" test="$EUGEN-T14-R002">[EUGEN-T14-R002]-A customer postal address in a credit note SHOULD contain at least, street name and number, city name, zip code and country code.</assert>
  </rule>
  <rule context="$Allowance_Charge">
    <assert flag="fatal" test="$EUGEN-T14-R004">[EUGEN-T14-R004]-If the VAT total amount in a Credit Note exists then an Allowances Charges amount on document level MUST have tax category for VAT.</assert>
    <assert flag="fatal" test="$EUGEN-T14-R022">[EUGEN-T14-R022]-An allowance or charge amount MUST NOT be negative.</assert>
    <assert flag="warning" test="$EUGEN-T14-R023">[EUGEN-T14-R023]-AllowanceChargeReason text SHOULD be specified for all allowances and charges</assert>
  </rule>
  <rule context="$Total_Amounts">
    <assert flag="fatal" test="$EUGEN-T14-R019">[EUGEN-T14-R019]-Total payable amount in an invoice MUST NOT be negative</assert>
  </rule>
  <rule context="$Credit_Note">
    <assert flag="fatal" test="$EUGEN-T14-R007">[EUGEN-T14-R007]-If the VAT total amount in a Credit Note exists it MUST contain the suppliers VAT number.</assert>
    <assert flag="fatal" test="$EUGEN-T14-R015">[EUGEN-T14-R015]-IF VAT = "AE" (reverse charge) THEN it MUST contain Supplier VAT id and Customer VAT</assert>
    <assert flag="fatal" test="$EUGEN-T14-R016">[EUGEN-T14-R016]-IF VAT = "AE" (reverse charge) THEN VAT MAY NOT contain other VAT categories.</assert>
    <assert flag="fatal" test="$EUGEN-T14-R017">[EUGEN-T14-R017]-IF VAT = "AE" (reverse charge) THEN The taxable amount MUST equal the invoice total without VAT amount.</assert>
    <assert flag="fatal" test="$EUGEN-T14-R018">[EUGEN-T14-R018]-IF VAT = "AE" (reverse charge) THEN VAT tax amount MUST be zero.</assert>
    <assert flag="fatal" test="$EUGEN-T14-R024">[EUGEN-T14-R024]-Currency Identifier MUST be in stated in the currency stated on header level.</assert>
  </rule>
  <rule context="$Supplier_Party">
    <assert flag="warning" test="$EUGEN-T14-R001">[EUGEN-T14-R001]-A supplier postal address in a credit note SHOULD contain at least, street name and number, city name, zip code and country code.</assert>
  </rule>
  <rule context="$Invoice_Period">
    <assert flag="fatal" test="$EUGEN-T14-R020">[EUGEN-T14-R020]-If the credit note refers to a period, the period MUST have an start date.</assert>
    <assert flag="fatal" test="$EUGEN-T14-R021">[EUGEN-T14-R021]-If the credit note refers to a period, the period MUST have an end date.</assert>
  </rule>
  <rule context="$Tax_Subtotal">
    <assert flag="warning" test="$EUGEN-T14-R013">[EUGEN-T14-R013]-If the category for VAT is exempt (E) then an exemption reason SHOULD be provided.</assert>
  </rule>
  <rule context="$Credit_Note_Line">
    <assert flag="warning" test="$EUGEN-T14-R003">[EUGEN-T14-R003]-Each credit note line SHOULD contain the quantity and unit of measure</assert>
  </rule>
  <rule context="$Classified_Tax_Category">
    <assert flag="fatal" test="$EUGEN-T14-R031">[EUGEN-T14-R031]-If the VAT total amount in a Credit Note exists then each Credit Note line item must have a VAT category ID.</assert>
  </rule>
</pattern>
