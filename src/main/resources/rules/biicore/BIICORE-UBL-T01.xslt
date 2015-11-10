<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
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
<xsl:stylesheet version="1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:Order-2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--Implementers: please note that overriding process-prolog or process-root is 
    the preferred method for meta-stylesheets to use where possible. -->
<xsl:param name="archiveDirParameter" />
  <xsl:param name="archiveNameParameter" />
  <xsl:param name="fileNameParameter" />
  <xsl:param name="fileDirParameter" />
  <xsl:variable name="document-uri">
    <xsl:value-of select="document-uri(/)" />
  </xsl:variable>

<!--PHASES-->


<!--PROLOG-->
<xsl:output indent="yes" method="xml" omit-xml-declaration="no" standalone="yes" />

<!--XSD TYPES FOR XSLT2-->


<!--KEYS AND FUNCTIONS-->


<!--DEFAULT RULES-->


<!--MODE: SCHEMATRON-SELECT-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<xsl:template match="*" mode="schematron-select-full-path">
    <xsl:apply-templates mode="schematron-get-full-path" select="." />
  </xsl:template>

<!--MODE: SCHEMATRON-FULL-PATH-->
<!--This mode can be used to generate an ugly though full XPath for locators-->
<xsl:template match="*" mode="schematron-get-full-path">
    <xsl:apply-templates mode="schematron-get-full-path" select="parent::*" />
    <xsl:text>/</xsl:text>
    <xsl:choose>
      <xsl:when test="namespace-uri()=''">
        <xsl:value-of select="name()" />
        <xsl:variable name="p_1" select="1+    count(preceding-sibling::*[name()=name(current())])" />
        <xsl:if test="$p_1>1 or following-sibling::*[name()=name(current())]">[<xsl:value-of select="$p_1" />]</xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>*[local-name()='</xsl:text>
        <xsl:value-of select="local-name()" />
        <xsl:text>']</xsl:text>
        <xsl:variable name="p_2" select="1+   count(preceding-sibling::*[local-name()=local-name(current())])" />
        <xsl:if test="$p_2>1 or following-sibling::*[local-name()=local-name(current())]">[<xsl:value-of select="$p_2" />]</xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <xsl:template match="@*" mode="schematron-get-full-path">
    <xsl:text>/</xsl:text>
    <xsl:choose>
      <xsl:when test="namespace-uri()=''">@<xsl:value-of select="name()" />
</xsl:when>
      <xsl:otherwise>
        <xsl:text>@*[local-name()='</xsl:text>
        <xsl:value-of select="local-name()" />
        <xsl:text>' and namespace-uri()='</xsl:text>
        <xsl:value-of select="namespace-uri()" />
        <xsl:text>']</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

<!--MODE: SCHEMATRON-FULL-PATH-2-->
<!--This mode can be used to generate prefixed XPath for humans-->
<xsl:template match="node() | @*" mode="schematron-get-full-path-2">
    <xsl:for-each select="ancestor-or-self::*">
      <xsl:text>/</xsl:text>
      <xsl:value-of select="name(.)" />
      <xsl:if test="preceding-sibling::*[name(.)=name(current())]">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1" />
        <xsl:text>]</xsl:text>
      </xsl:if>
    </xsl:for-each>
    <xsl:if test="not(self::*)">
      <xsl:text />/@<xsl:value-of select="name(.)" />
    </xsl:if>
  </xsl:template>
<!--MODE: SCHEMATRON-FULL-PATH-3-->
<!--This mode can be used to generate prefixed XPath for humans 
	(Top-level element has index)-->
<xsl:template match="node() | @*" mode="schematron-get-full-path-3">
    <xsl:for-each select="ancestor-or-self::*">
      <xsl:text>/</xsl:text>
      <xsl:value-of select="name(.)" />
      <xsl:if test="parent::*">
        <xsl:text>[</xsl:text>
        <xsl:value-of select="count(preceding-sibling::*[name(.)=name(current())])+1" />
        <xsl:text>]</xsl:text>
      </xsl:if>
    </xsl:for-each>
    <xsl:if test="not(self::*)">
      <xsl:text />/@<xsl:value-of select="name(.)" />
    </xsl:if>
  </xsl:template>

<!--MODE: GENERATE-ID-FROM-PATH -->
<xsl:template match="/" mode="generate-id-from-path" />
  <xsl:template match="text()" mode="generate-id-from-path">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:value-of select="concat('.text-', 1+count(preceding-sibling::text()), '-')" />
  </xsl:template>
  <xsl:template match="comment()" mode="generate-id-from-path">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:value-of select="concat('.comment-', 1+count(preceding-sibling::comment()), '-')" />
  </xsl:template>
  <xsl:template match="processing-instruction()" mode="generate-id-from-path">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:value-of select="concat('.processing-instruction-', 1+count(preceding-sibling::processing-instruction()), '-')" />
  </xsl:template>
  <xsl:template match="@*" mode="generate-id-from-path">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:value-of select="concat('.@', name())" />
  </xsl:template>
  <xsl:template match="*" mode="generate-id-from-path" priority="-0.5">
    <xsl:apply-templates mode="generate-id-from-path" select="parent::*" />
    <xsl:text>.</xsl:text>
    <xsl:value-of select="concat('.',name(),'-',1+count(preceding-sibling::*[name()=name(current())]),'-')" />
  </xsl:template>

<!--MODE: GENERATE-ID-2 -->
<xsl:template match="/" mode="generate-id-2">U</xsl:template>
  <xsl:template match="*" mode="generate-id-2" priority="2">
    <xsl:text>U</xsl:text>
    <xsl:number count="*" level="multiple" />
  </xsl:template>
  <xsl:template match="node()" mode="generate-id-2">
    <xsl:text>U.</xsl:text>
    <xsl:number count="*" level="multiple" />
    <xsl:text>n</xsl:text>
    <xsl:number count="node()" />
  </xsl:template>
  <xsl:template match="@*" mode="generate-id-2">
    <xsl:text>U.</xsl:text>
    <xsl:number count="*" level="multiple" />
    <xsl:text>_</xsl:text>
    <xsl:value-of select="string-length(local-name(.))" />
    <xsl:text>_</xsl:text>
    <xsl:value-of select="translate(name(),':','.')" />
  </xsl:template>
<!--Strip characters-->  <xsl:template match="text()" priority="-1" />

<!--SCHEMA SETUP-->
<xsl:template match="/">
    <svrl:schematron-output schemaVersion="" title="BIICORE T01 bound to UBL">
      <xsl:comment>
        <xsl:value-of select="$archiveDirParameter" />   
		 <xsl:value-of select="$archiveNameParameter" />  
		 <xsl:value-of select="$fileNameParameter" />  
		 <xsl:value-of select="$fileDirParameter" />
      </xsl:comment>
      <svrl:ns-prefix-in-attribute-values prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Order-2" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:attribute name="id">UBL-T01</xsl:attribute>
        <xsl:attribute name="name">UBL-T01</xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M7" select="/" />
    </svrl:schematron-output>
  </xsl:template>

<!--SCHEMATRON PATTERNS-->
<svrl:text>BIICORE T01 bound to UBL</svrl:text>
  <xsl:param name="Prerequisite1" select="contains(cbc:CustomizationID, 'urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0')" />
  <xsl:param name="Prerequisite2" select="contains(preceding::cbc:CustomizationID, 'urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0')" />

<!--PATTERN UBL-T01-->


	<!--RULE -->
<xsl:template match="/ubl:Order/cac:BuyerCustomerParty/cac:Party" mode="M7" priority="1005">
    <svrl:fired-rule context="/ubl:Order/cac:BuyerCustomerParty/cac:Party" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R435]-Element 'PartyIdentification' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyName)=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyName)=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R436]-Element 'PartyName' must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyLegalEntity)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyLegalEntity)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R437]-Element 'PartyLegalEntity' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Order/cac:SellerSupplierParty/cac:Party" mode="M7" priority="1004">
    <svrl:fired-rule context="/ubl:Order/cac:SellerSupplierParty/cac:Party" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R438]-Element 'PartyIdentification' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyName)=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyName)=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R439]-Element 'PartyName' must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyLegalEntity)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyLegalEntity)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R440]-Element 'PartyLegalEntity' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Order/cac:OrderLine/cac:LineItem/cac:Item" mode="M7" priority="1003">
    <svrl:fired-rule context="/ubl:Order/cac:OrderLine/cac:LineItem/cac:Item" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cbc:Description)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cbc:Description)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R441]-Element 'Description' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Order/cac:OriginatoCustomerParty" mode="M7" priority="1002">
    <svrl:fired-rule context="/ubl:Order/cac:OriginatoCustomerParty" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R442]-Element 'PartyIdentification' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyName)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyName)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R443]-Element 'PartyName' may occur at maximum 1 times</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Order" mode="M7" priority="1001">
    <svrl:fired-rule context="/ubl:Order" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="contains(cbc:CustomizationID, 'urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0')" />
      <xsl:otherwise>
        <svrl:failed-assert test="contains(cbc:CustomizationID, 'urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0')">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R000]-This XML instance is NOT a core BiiTrdm001 transaction</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="not(count(//*[not(text())]) > 0)" />
      <xsl:otherwise>
        <svrl:failed-assert test="not(count(//*[not(text())]) > 0)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R001]-An Order SHOULD not contain empty elements.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cbc:SalesOrderID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cbc:SalesOrderID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R002]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cbc:CopyIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cbc:CopyIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R003]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cbc:UUID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cbc:UUID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R004]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cbc:RequestedInvoiceCurrencyCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cbc:RequestedInvoiceCurrencyCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R005]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cbc:PricingCurrencyCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cbc:PricingCurrencyCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R006]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cbc:TaxCurrencyCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cbc:TaxCurrencyCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R007]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cbc:CustomerReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cbc:CustomerReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R008]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cbc:AccountingCostCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cbc:AccountingCostCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R009]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cbc:LineCountNumeric)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cbc:LineCountNumeric)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R010]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ValidityPeriod/cbc:StartDate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ValidityPeriod/cbc:StartDate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R011]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ValidityPeriod/cbc:StartTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ValidityPeriod/cbc:StartTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R012]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ValidityPeriod/cbc:EndTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ValidityPeriod/cbc:EndTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R013]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ValidityPeriod/cbc:DurationMeasure)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ValidityPeriod/cbc:DurationMeasure)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R014]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ValidityPeriod/cbc:Description)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ValidityPeriod/cbc:Description)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R015]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ValidityPeriod/cbc:DescriptionCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ValidityPeriod/cbc:DescriptionCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R016]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R017]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:UUID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:UUID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R018]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R019]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R020]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:DocumentType)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:DocumentType)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R021]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:XPath)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:QuotationDocumentReference/cbc:XPath)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R022]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:QuotationDocumentReference/cac:Attachment)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:QuotationDocumentReference/cac:Attachment)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R023]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R024]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:UUID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:UUID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R025]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R026]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R027]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:DocumentType)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:DocumentType)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R028]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:XPath)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:XPath)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R029]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cac:Attachment)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cac:Attachment)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R030]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R031]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:UUID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:UUID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R032]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R033]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R034]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:XPath)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cbc:XPath)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R035]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderDocumentReference/cac:Attachment)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderDocumentReference/cac:Attachment)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R036]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:CopyIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R037]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:UUID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:UUID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R038]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R039]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R040]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:XPath)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cbc:XPath)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R041]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:DocumentHash)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:DocumentHash)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R042]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryDate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryDate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R043]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AdditionalDocumentReference/cac:Attachment/cac:ExternalReference/cbc:ExpiryTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R044]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Contract/cbc:IssueDate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Contract/cbc:IssueDate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R045]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Contract/cbc:IssueTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Contract/cbc:IssueTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R046]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Contract/cbc:ContractTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Contract/cbc:ContractTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R047]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Contract/cac:ValidityPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Contract/cac:ValidityPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R048]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Contract/cac:ContractDocumentReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Contract/cac:ContractDocumentReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R049]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Signature)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Signature)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R050]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cbc:SupplierAssignedAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cbc:SupplierAssignedAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R051]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R052]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R053]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:DeliveryContact/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:DeliveryContact/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R054]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:DeliveryContact/cbc:Note)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:DeliveryContact/cbc:Note)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R055]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:DeliveryContact/cac:OtherCommunication)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:DeliveryContact/cac:OtherCommunication)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R056]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:AccountingContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:AccountingContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R057]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:BuyerContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:BuyerContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R058]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R059]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R060]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R061]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R062]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Language)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Language)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R063]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R064]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R065]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Floor)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Floor)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R066]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Room)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Room)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R067]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:BlockName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:BlockName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R068]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R069]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R070]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R071]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkCare)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R072]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R073]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R074]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R075]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Region)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:Region)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R076]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:District)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:District)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R077]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R078]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:AddressLine)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:AddressLine)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R079]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R080]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R081]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R082]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R083]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R084]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R085]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R086]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R087]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R088]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R089]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R090]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R091]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R092]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R093]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R094]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R095]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R096]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R097]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R098]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R099]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R100]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R101]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R102]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R103]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:CountrySubentity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:CountrySubentity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R104]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R105]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R106]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R107]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R108]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R109]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R110]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R111]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R112]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R113]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Postbox)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Postbox)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R114]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Floor)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Floor)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R115]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Room)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Room)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R116]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:StreetName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:StreetName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R117]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R118]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:BuildingName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R119]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:BuildingNumber)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:BuildingNumber)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R120]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Department)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Department)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R121]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:MarkAttention)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R122]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:MarkCare)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R123]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:CityName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:CityName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R124]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:PostalZone)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:PostalZone)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R125]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:CountrySubentity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:CountrySubentity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R126]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R127]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Region)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:Region)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R128]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:District)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cbc:District)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R129]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cac:AddressLine)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress/cac:AddressLine)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R130]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R131]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R132]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R133]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R134]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R135]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R136]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R137]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R138]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R139]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R140]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R141]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R142]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R143]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R144]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R145]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R146]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R147]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R148]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R149]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R150]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R151]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R152]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R153]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R154]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R155]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R156]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R157]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R158]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R159]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R160]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R161]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R162]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R163]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R164]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R165]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R166]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cbc:DataSendingCapability)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cbc:DataSendingCapability)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R167]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:DespatchContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:DespatchContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R168]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:AccountingContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:AccountingContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R169]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:SellerContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:SellerContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R170]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R171]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R172]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R173]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R174]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Language)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Language)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R175]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R176]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R177]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Floor)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Floor)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R178]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Room)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Room)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R179]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:BlockName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:BlockName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R180]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R181]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R182]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R183]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkCare)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R184]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R185]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R186]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Region)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:Region)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R187]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:District)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:District)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R188]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R189]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:AddressLine)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:AddressLine)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R190]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R191]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R192]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R193]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyTaxScheme)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R194]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R195]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R196]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R197]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R198]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R199]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R200]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R201]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R202]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R203]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R204]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R205]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R206]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R207]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R208]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R209]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R210]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R211]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R212]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R213]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R214]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R215]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R216]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R217]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R218]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R219]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R220]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R221]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R222]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R223]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R224]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R225]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R226]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R227]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R228]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cbc:SupplierAssignedAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cbc:SupplierAssignedAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R229]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R230]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R231]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:DeliveryContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:DeliveryContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R232]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:AccountingContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:AccountingContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R233]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:BuyerContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:BuyerContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R234]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R235]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R236]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R237]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R238]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Language)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Language)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R239]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:PostalAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:PostalAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R240]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R241]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:PartyTaxScheme)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:PartyTaxScheme)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R242]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:PartyLegalEntity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:PartyLegalEntity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R243]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R244]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R245]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R246]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R247]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R248]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R249]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R250]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorCustomerParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R251]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:FreightForwarderParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:FreightForwarderParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R252]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R253]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R254]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cbc:Quantity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cbc:Quantity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R255]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cbc:MinimumQuantity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cbc:MinimumQuantity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R256]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cbc:MaximumQuantity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cbc:MaximumQuantity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R257]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cbc:ActualDeliveryTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cbc:ActualDeliveryTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R258]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cbc:LatestDeliveryDate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cbc:LatestDeliveryDate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R259]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cbc:LatestDeliveryTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cbc:LatestDeliveryTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R260]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cbc:TrackingID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cbc:TrackingID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R261]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R262]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R263]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:Description)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:Description)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R264]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:Conditions)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:Conditions)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R265]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R266]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cbc:CountrySubentityCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R267]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:ValidityPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:ValidityPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R268]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R269]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:AddressFormatCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R270]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Floor)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Floor)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R271]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Room)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Room)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R272]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BlockName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BlockName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R273]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:BuildingName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R274]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:InhouseMail)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:InhouseMail)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R275]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Department)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R276]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkAttention)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkAttention)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R277]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkCare)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:MarkCare)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R278]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PlotIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:PlotIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R279]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CitySubdivisionName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R280]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:CountrySubentityCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R281]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Region)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:Region)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R282]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:District)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:District)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R283]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cbc:TimezoneOffset)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R284]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:AddressLine)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:AddressLine)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R285]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:Country/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R286]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:LocationCoordinate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Address/cac:LocationCoordinate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R287]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:StartTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:StartTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R288]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:EndTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:EndTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R289]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:DurationMeasure)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:DurationMeasure)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R290]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:DescriptionCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:DescriptionCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R291]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:Description)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:RequestedDeliveryPeriod/cbc:Description)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R292]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:PromisedDeliveryPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:PromisedDeliveryPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R293]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:EstimatedDeliveryPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:EstimatedDeliveryPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R294]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cbc:MarkCareIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R295]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R296]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cbc:WebsiteURI)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cbc:WebsiteURI)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R297]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cbc:LogoReferenceID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R298]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:Language)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:Language)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R299]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:PostalAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:PostalAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R300]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:PhysicalLocation)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:PhysicalLocation)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R301]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:PartyTaxScheme)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:PartyTaxScheme)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R302]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:PartyLegalEntity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:PartyLegalEntity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R303]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R304]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Note)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:Contact/cbc:Note)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R306]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:OtherCommunication)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:OtherCommunication)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R307]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:Person)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:Person)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R308]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:AgentParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryParty/cac:AgentParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R309]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:Despatch)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:Despatch)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R310]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:DeliveryTerms/cbc:LossRiskResponsibilityCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:DeliveryTerms/cbc:LossRiskResponsibilityCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R311]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:DeliveryTerms/cbc:LossRisk)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:DeliveryTerms/cbc:LossRisk)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R312]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:DeliveryTerms/cac:AllowanceCharge)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:DeliveryTerms/cac:AllowanceCharge)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R313]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cbc:Description)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cbc:Description)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R314]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cbc:Conditions)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cbc:Conditions)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R315]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cbc:CountrySubentity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cbc:CountrySubentity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R316]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cbc:CountrySubentityCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R317]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cac:ValidityPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cac:ValidityPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R318]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cac:Address)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:DeliveryTerms/cac:DeliveryLocation/cac:Address)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R319]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R320]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TransactionConditions)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TransactionConditions)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R321]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R322]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:AllowanceChargeReasonCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:AllowanceChargeReasonCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R323]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:MultiplierFactorNumeric)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:MultiplierFactorNumeric)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R324]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:PrepaidIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:PrepaidIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R325]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:SequenceNumeric)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:SequenceNumeric)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R326]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:BaseAmount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:BaseAmount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R327]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:AccountingCostCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:AccountingCostCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R328]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:AccountingCost)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AllowanceCharge/cbc:AccountingCost)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R329]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AllowanceCharge/cac:TaxCategory)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AllowanceCharge/cac:TaxCategory)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R330]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AllowanceCharge/cac:TaxTotal)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AllowanceCharge/cac:TaxTotal)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R331]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AllowanceCharge/cac:PaymentMeans)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AllowanceCharge/cac:PaymentMeans)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R332]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:DestinationCountry)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:DestinationCountry)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R333]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cbc:RoundingAmount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cbc:RoundingAmount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R334]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cbc:TaxEvidenceIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cbc:TaxEvidenceIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R335]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R336]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AnticipatedMonetaryTotal/cbc:TaxExclusiveAmount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AnticipatedMonetaryTotal/cbc:TaxExclusiveAmount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R337]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AnticipatedMonetaryTotal/cbc:TaxInclusiveAmount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AnticipatedMonetaryTotal/cbc:TaxInclusiveAmount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R338]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AnticipatedMonetaryTotal/cbc:PrepaidAmount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AnticipatedMonetaryTotal/cbc:PrepaidAmount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R339]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AnticipatedMonetaryTotal/cbc:PayableRoundingAmount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AnticipatedMonetaryTotal/cbc:PayableRoundingAmount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R340]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cbc:SubstitutionStatusCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cbc:SubstitutionStatusCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R341]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:SellerProposedSubstituteLineItem)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:SellerProposedSubstituteLineItem)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R342]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:SellerSubstitutedLineItem)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:SellerSubstitutedLineItem)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R343]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:BuyerProposedSubstituteLineItem)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:BuyerProposedSubstituteLineItem)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R344]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:CatalogueLineReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:CatalogueLineReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R345]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:QuotationLineReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:QuotationLineReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R346]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:DocumentReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:DocumentReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R347]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:SalesOrderID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:SalesOrderID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R348]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:Note)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:Note)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R349]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:UUID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:UUID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R350]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:LineStatusCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:LineStatusCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R351]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:MinimumQuantity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:MinimumQuantity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R352]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:MaximumQuantity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:MaximumQuantity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R353]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:MinimumBackorderQuantity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:MinimumBackorderQuantity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R354]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:MaximumBackorderQuantity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:MaximumBackorderQuantity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R355]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:InspectionMethodCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:InspectionMethodCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R356]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:BackOrderAllowedIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:BackOrderAllowedIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R357]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:AccountingCostCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cbc:AccountingCostCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R358]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R359]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:Quantity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:Quantity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R360]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:MinimumQuantity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:MinimumQuantity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R361]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:MaximumQuantity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:MaximumQuantity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R362]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:ActualDeliveryTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:ActualDeliveryTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R363]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:LatestDeliveryDate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:LatestDeliveryDate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R364]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:LatestDeliveryTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:LatestDeliveryTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R365]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:TrackingID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cbc:TrackingID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R366]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:DeliveryAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:DeliveryAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R367]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:DeliveryLocation)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:DeliveryLocation)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R368]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:StartTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:StartTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R369]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:EndTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:EndTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R370]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:DurationMeasure)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:DurationMeasure)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R371]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:DescriptionCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:DescriptionCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R372]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:Description)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:RequestedDeliveryPeriod/cbc:Description)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R373]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:PromisedDeliveryPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:PromisedDeliveryPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R374]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:EstimatedDeliveryPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:EstimatedDeliveryPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R375]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:DeliveryParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:DeliveryParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R376]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:Despatch)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Delivery/cac:Despatch)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R377]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:DeliveryTerms)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:DeliveryTerms)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R378]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:MarkCareIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R379]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R380]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:WebsiteURI)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:WebsiteURI)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R381]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:LogoReferenceID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R382]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:EndpointID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cbc:EndpointID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R383]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:Language)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:Language)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R384]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:PostalAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:PostalAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R385]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:PhysicalLocation)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:PhysicalLocation)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R386]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:PartyTaxScheme)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:PartyTaxScheme)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R387]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:PartyLegalEntity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:PartyLegalEntity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R388]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:Contact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:Contact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R389]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:Person)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:Person)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R390]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:AgentParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OriginatoriParty/cac:AgentParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R391]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OrderedShipment)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:OrderedShipment)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R392]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:PricingReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:PricingReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R393]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:AllowanceCharge)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:AllowanceCharge)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R394]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cbc:PriceChangeReason)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cbc:PriceChangeReason)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R395]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cbc:PriceTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cbc:PriceTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R396]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cbc:PriceType)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cbc:PriceType)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R397]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cbc:OrderableUnitFactorRate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cbc:OrderableUnitFactorRate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R398]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cac:ValidityPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cac:ValidityPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R399]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cac:PriceList)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cac:PriceList)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R400]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cac:AllowanceCharge)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Price/cac:AllowanceCharge)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R401]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:PackQuantity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:PackQuantity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R402]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:PackSizeNumeric)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:PackSizeNumeric)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R403]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:CatalogueIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:CatalogueIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R404]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:HazardousRiskIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:HazardousRiskIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R405]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:AdditionalInformation)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:AdditionalInformation)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R406]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:Keyword)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:Keyword)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R407]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:BrandName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:BrandName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R408]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:ModelName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cbc:ModelName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R409]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:ExtendedID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:ExtendedID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R410]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:PhysycalAttribute)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:PhysycalAttribute)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R411]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:MeasurementDimension)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:MeasurementDimension)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R412]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:IssuerParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:SellersItemIdentification/cbc:IssuerParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R413]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:ExtendedID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:ExtendedID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R414]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:PhysycalAttribute)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:PhysycalAttribute)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R415]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:MeasurementDimension)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:MeasurementDimension)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R416]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:IssuerParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:StandardItemIdentification/cbc:IssuerParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R417]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:BuyersItemIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:BuyersItemIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R418]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:CommodityClassification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:CommodityClassification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R419]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ManufacturersItemIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ManufacturersItemIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R420]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:CatalogueItemIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:CatalogueItemIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R421]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:AdditionalItemIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:AdditionalItemIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R422]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:CatalogueDocumentReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:CatalogueDocumentReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R423]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ItemSpecificationDocumentReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R424]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:OriginCountry)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:OriginCountry)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R425]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:OriginCountry)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:OriginCountry)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R425]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:HazardousItem)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:HazardousItem)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R427]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ManufacturerParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ManufacturerParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R428]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:InformationContentProviderParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:InformationContentProviderParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R429]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:OriginAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:OriginAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R430]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ItemInstance)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ItemInstance)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R431]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ClassifiedTaxCategory)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:ClassifiedTaxCategory)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R432]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:AdditionalItemProperty/cac:UsabilityPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:AdditionalItemProperty/cac:UsabilityPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R433]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:AdditionalItemProperty/cac:ItemPropertyGroup)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderLine/cac:LineItem/cac:Item/cac:AdditionalItemProperty/cac:ItemPropertyGroup)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R434]-A conformant CEN BII Order core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and count(cbc:Note)&lt;=1) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and count(cbc:Note)&lt;=1) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R444]-Element 'Note' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and count(cac:ValidityPeriod)&lt;=1) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and count(cac:ValidityPeriod)&lt;=1) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R445]-Element 'Validity Period' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and count(cac:OrderDocumentReference)&lt;=1) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and count(cac:OrderDocumentReference)&lt;=1) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R446]-Element 'Order Document Reference' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and count(cac:Contract)&lt;=1) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and count(cac:Contract)&lt;=1) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R447]-Element 'Contract' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and count(cac:Delivery)&lt;=1) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and count(cac:Delivery)&lt;=1) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R448]-Element 'Delivery' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Order/cac:Delivery/cac:DeliveryParty" mode="M7" priority="1000">
    <svrl:fired-rule context="/ubl:Order/cac:Delivery/cac:DeliveryParty" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyName)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyName)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T01-R449]-Element 'PartyName' may occur at maximum 1 times</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>
  <xsl:template match="text()" mode="M7" priority="-1" />
  <xsl:template match="@*|node()" mode="M7" priority="-2">
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>
</xsl:stylesheet>
