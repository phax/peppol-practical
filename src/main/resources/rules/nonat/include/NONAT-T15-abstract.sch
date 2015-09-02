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
<!--Abstract Schematron rules for T15-->
<pattern xmlns="http://purl.oclc.org/dsdl/schematron" abstract="true" id="T15">
  <rule context="$Customer_Party">
    <assert flag="fatal" test="$NONAT-T15-R007">[NONAT-T15-R007]-A customer postal address in an invoice MUST contain at least, Street name, city name, zip code and country code.</assert>
  </rule>
  <rule context="$Supplier_Party">
    <assert flag="fatal" test="$NONAT-T15-R001">[NONAT-T15-R001]-The Norwegian legal registration ID for the supplier MUST be provided according to "FOR 2004-12-01 nr 1558 - § 5-1-1. Point 2"</assert>
    <assert flag="fatal" test="$NONAT-T15-R006">[NONAT-T15-R006]-A supplier postal address in an invoice MUST contain at least, Street name, city name, zip code and country code.</assert>
  </rule>
  <rule context="$Invoice">
    <assert flag="fatal" test="$NONAT-T15-R002">[NONAT-T15-R002]-Payment due date MUST be provided in the invoice according to "FOR 2004-12-01 nr 1558 - § 5-1-1. Point 5" </assert>
    <assert flag="fatal" test="$NONAT-T15-R003">[NONAT-T15-R003]-The actual delivery date  MUST be provided in the invoice according to "FOR 2004-12-01 nr 1558 - § 5-1-1. Point 4" </assert>
    <assert flag="fatal" test="$NONAT-T15-R004">[NONAT-T15-R004]-A Delivery address in an invoice MUST contain at least, city, zip code and country code according to "FOR 2004-12-01 nr 1558 - § 5-1-1. Point 4"</assert>
  </rule>
  <rule context="$Invoice_Line">
    <assert flag="fatal" test="$NONAT-T15-R005">[NONAT-T15-R005]-Each invoice line MUST contain a quantity according to "FOR 2004-12-01 nr 1558 - § 5-1-1. Point 3" </assert>
  </rule>
</pattern>
