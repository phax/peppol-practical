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
  <rule context="$CreditNote">
    <assert flag="fatal" test="$ATNAT-T14-R001">[ATNAT-T14-R001]-If the credit note total exceeds € 10.000, the VAT number of the customer MUST be provided, if the supplier has a registered office in Austria.</assert>
    <assert flag="fatal" test="$ATNAT-T14-R003">[ATNAT-T14-R003]-The credit note MUST contain either the actual delivery date or the delivery period.</assert>
    <assert flag="fatal" test="$ATNAT-T14-R004">[ATNAT-T14-R004]-If products or services are subject to the Reverse Charge System (customer has to bear the tax, not the supplier - Austria: UStG § 19) the VAT identification number of the customer MUST be provided.</assert>
    <assert flag="fatal" test="$ATNAT-T14-R005">[ATNAT-T14-R005]-If the credit note total exceeds € 400, the VAT number of the supplier MUST be provided, if the supplier has a registered office in Austria.</assert>
  </rule>
  <rule context="$Tax_Category">
    <assert flag="fatal" test="$ATNAT-T14-R002">[ATNAT-T14-R002]-If the tax percentage in a tax category is 0% then the tax category identifier MUST be "E" or "AE" (UN-5305).</assert>
    <assert flag="fatal" test="$ATNAT-T14-R006">[ATNAT-T14-R006]-If products or services are subject to the Reverse Charge System (customer has to bear the tax, not the supplier - Austria: UStG § 19) the VAT tax amount must be 0.</assert>
  </rule>
</pattern>
