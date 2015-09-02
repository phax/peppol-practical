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
<xsl:stylesheet version="1.0" xmlns:cac="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" xmlns:cbc="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" xmlns:ubl="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
    <svrl:schematron-output schemaVersion="" title="BIICORE T10 bound to UBL">
      <xsl:comment>
        <xsl:value-of select="$archiveDirParameter" />   
		 <xsl:value-of select="$archiveNameParameter" />  
		 <xsl:value-of select="$fileNameParameter" />  
		 <xsl:value-of select="$fileDirParameter" />
      </xsl:comment>
      <svrl:ns-prefix-in-attribute-values prefix="cbc" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="cac" uri="urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2" />
      <svrl:ns-prefix-in-attribute-values prefix="ubl" uri="urn:oasis:names:specification:ubl:schema:xsd:Invoice-2" />
      <svrl:active-pattern>
        <xsl:attribute name="document">
          <xsl:value-of select="document-uri(/)" />
        </xsl:attribute>
        <xsl:attribute name="id">UBL-T10</xsl:attribute>
        <xsl:attribute name="name">UBL-T10</xsl:attribute>
        <xsl:apply-templates />
      </svrl:active-pattern>
      <xsl:apply-templates mode="M7" select="/" />
    </svrl:schematron-output>
  </xsl:template>

<!--SCHEMATRON PATTERNS-->
<svrl:text>BIICORE T10 bound to UBL</svrl:text>
  <xsl:param name="Prerequisite1" select="contains(cbc:CustomizationID, 'urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0')" />
  <xsl:param name="Prerequisite2" select="contains(preceding::cbc:CustomizationID, 'urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0')" />

<!--PATTERN UBL-T10-->


	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:AccountingCustomerParty/cac:Party" mode="M7" priority="1008">
    <svrl:fired-rule context="/ubl:Invoice/cac:AccountingCustomerParty/cac:Party" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R387]-Element 'PartyIdentification' may occur at maximum 1 times.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R388]-Element 'PartyName' must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyTaxScheme)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyTaxScheme)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R389]-Element 'PartyTaxScheme' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:PayeeParty" mode="M7" priority="1007">
    <svrl:fired-rule context="/ubl:Invoice/cac:PayeeParty" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R402]-Element 'PartyIdentification' may occur at maximum 1 times.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R403]-Element 'PartyName' may occur at maximum 1 times</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:PaymentMeans/cac:PayeeFinancialAccount" mode="M7" priority="1006">
    <svrl:fired-rule context="/ubl:Invoice/cac:PaymentMeans/cac:PayeeFinancialAccount" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cbc:ID)=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cbc:ID)=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R404]-Element 'ID' must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:InvoiceLine" mode="M7" priority="1005">
    <svrl:fired-rule context="/ubl:InvoiceLine" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:TaxTotal)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:TaxTotal)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R394]-Element 'TaxTotal' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:Price)=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:Price)=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R395]-Element 'Price' must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:AccountingSupplierParty/cac:Party" mode="M7" priority="1004">
    <svrl:fired-rule context="/ubl:Invoice/cac:AccountingSupplierParty/cac:Party" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyIdentification)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R390]-Element 'PartyIdentification' may occur at maximum 1 times.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R391]-Element 'PartyName' must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PostalAddress)=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PostalAddress)=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R392]-Element 'PostalAddress' must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:PartyTaxScheme)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:PartyTaxScheme)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R393]-Element 'PartyTaxScheme' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:InvoiceLine/cac:Item" mode="M7" priority="1003">
    <svrl:fired-rule context="/ubl:Invoice/cac:InvoiceLine/cac:Item" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cbc:Description)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cbc:Description)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R396]-Element 'Description' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cbc:Name)=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cbc:Name)=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R397]-Element 'Name' must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:ClassifiedTaxCategory)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:ClassifiedTaxCategory)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R398]-Element 'ClassifiedTaxCategory' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:InvoiceLine/cac:Price" mode="M7" priority="1002">
    <svrl:fired-rule context="/ubl:Invoice/cac:InvoiceLine/cac:Price" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cac:AllowanceCharge)&lt;=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cac:AllowanceCharge)&lt;=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R399]-Element 'AllowanceCharge' may occur at maximum 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice" mode="M7" priority="1001">
    <svrl:fired-rule context="/ubl:Invoice" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="contains(cbc:CustomizationID, 'urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0')" />
      <xsl:otherwise>
        <svrl:failed-assert test="contains(cbc:CustomizationID, 'urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0')">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R000]-This XML instance is NOT a core BiiTrdm010 transaction</svrl:text>
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
          <svrl:text>[BIICORE-T10-R001]-An invoice SHOULD not contain empty elements.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R002]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R003]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cbc:IssueTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cbc:IssueTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R004]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R005]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R006]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cbc:PaymentCurrencyCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cbc:PaymentCurrencyCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R007]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cbc:PaymentAlternativeCurrencyCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cbc:PaymentAlternativeCurrencyCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R008]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R009]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R010]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BillingReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BillingReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R011]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:DespatchDocumentReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:DespatchDocumentReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R012]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ReceiptDocumentReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ReceiptDocumentReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R013]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OriginatorDocumentReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OriginatorDocumentReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R014]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R015]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:BuyerCustomerParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:BuyerCustomerParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R016]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:SellerSupplierParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:SellerSupplierParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R017]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxRepresentativeParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxRepresentativeParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R018]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:DeliveryTerms)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:DeliveryTerms)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R019]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PrepaidPayment)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PrepaidPayment)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R020]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxExchangeRate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxExchangeRate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R021]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PricingExchangeRate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PricingExchangeRate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R022]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentExchangeRate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentExchangeRate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R023]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentAlternativeExchangeRate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentAlternativeExchangeRate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R024]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoicePeriod/cbc:StartTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoicePeriod/cbc:StartTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R025]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoicePeriod/cbc:EndTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoicePeriod/cbc:EndTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R026]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoicePeriod/cbc:DurationMeasure)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoicePeriod/cbc:DurationMeasure)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R027]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoicePeriod/cbc:Description)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoicePeriod/cbc:Description)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R028]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoicePeriod/cbc:DescriptionCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoicePeriod/cbc:DescriptionCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R029]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderReference/cbc:SalesOrderID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderReference/cbc:SalesOrderID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R030]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderReference/cbc:CopyIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderReference/cbc:CopyIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R031]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderReference/cbc:UUID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderReference/cbc:UUID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R032]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderReference/cbc:IssueDate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderReference/cbc:IssueDate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R033]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderReference/cbc:IssueTime)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderReference/cbc:IssueTime)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R034]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderReference/cbc:CustomerReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderReference/cbc:CustomerReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R035]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:OrderReference/cac:DocumentReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:OrderReference/cac:DocumentReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R036]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:CooyIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:CooyIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R037]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:UUID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:UUID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R038]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:IssueDate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R039]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:DocumentTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R040]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:XPath)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ContractDocumentReference/cbc:XPath)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R041]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:ContractDocumentReference/cac:Attachment)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:ContractDocumentReference/cac:Attachment)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R042]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R043]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R044]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R045]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R046]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R047]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R048]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R049]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R050]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R051]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R052]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cbc:DataSendingCapability)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cbc:DataSendingCapability)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R053]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:DespatchContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:DespatchContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R054]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:AccountingContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:AccountingContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R055]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:SellerContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:SellerContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R056]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R057]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R058]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R059]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R060]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Language)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Language)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R061]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R062]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R063]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Floor)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Floor)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R064]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Room)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Room)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R065]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BlockName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BlockName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R066]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:BuildingName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R067]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R068]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R069]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:MarkCare)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R070]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R071]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R072]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Region)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:Region)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R073]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:District)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:District)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R074]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R075]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:AddressLine)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:AddressLine)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R076]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R077]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R078]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R079]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R080]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R081]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R082]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R083]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R084]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R085]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R086]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R087]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R088]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R089]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R090]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R091]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R092]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R093]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R094]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R095]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R096]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R097]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R098]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R099]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R100]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R101]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R102]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R103]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R104]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R105]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R106]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R107]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R108]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R109]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R110]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R111]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R112]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R113]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R114]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R115]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R116]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R117]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R118]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R119]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R120]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingSupplierParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R121]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cbc:SupplierAssignedAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cbc:SupplierAssignedAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R122]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cbc:CustomerAssignedAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R123]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cbc:AdditionalAccountID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R124]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:DeliveryContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:DeliveryContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R125]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:AccountingContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:AccountingContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R126]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:BuyerContact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:BuyerContact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R127]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cbc:MarkCareIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R128]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R129]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cbc:WebsiteURI)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R130]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cbc:LogoReferenceID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R131]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Language)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Language)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R132]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R133]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R134]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Floor)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Floor)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R135]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Room)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Room)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R136]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BlockName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BlockName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R137]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:BuildingName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R138]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:InhouseMail)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R139]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkAttention)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R140]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:MarkCare)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R141]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:PlotIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R142]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R143]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Region)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:Region)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R144]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:District)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:District)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R145]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R146]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:AddressLine)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:AddressLine)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R147]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R148]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PostalAddress/cac:LocationCoordinate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R149]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PhysicalLocation)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R150]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:RegistrationName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R151]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:TaxLevelCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R152]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReasonCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R153]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cbc:ExemptionReason)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R154]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:RegistrationAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R155]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R156]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:TaxTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R157]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cbc:CurrencyCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R158]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyTaxScheme/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R159]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R160]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AddressFormatCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R161]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Postbox)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R162]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Floor)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R163]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Room)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R164]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:StreetName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R165]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:AdditionalStreetName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R166]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BlockName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R167]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R168]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:BuildingNumber)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R169]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:InhouseMail)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R170]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Department)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R171]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkAttention)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R172]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:MarkCare)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R173]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PlotIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R174]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CitySubdivisionName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R175]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:PostalZone)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R176]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:CountrySubentityCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R177]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:Region)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R178]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:District)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R179]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cbc:TimezoneOffset)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R180]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:AddressLine)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R181]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:Country/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R182]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:RegistrationAddress/cac:LocationCoordinate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R183]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R184]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R185]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R186]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cbc:Note)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R187]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Contact/cac:OtherCommunication)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R188]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:Title)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R189]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:NameSuffix)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R190]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:Person/cbc:OrganizationDepartment)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R191]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AccountingCustomerParty/cac:Party/cac:AgentParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R192]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cbc:MarkCareIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cbc:MarkCareIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R193]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cbc:MarkAttentionIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R194]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cbc:WebsiteURI)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cbc:WebsiteURI)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R195]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cbc:LogoReferenceID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cbc:LogoReferenceID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R196]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cac:Language)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cac:Language)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R197]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cac:PostalAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cac:PostalAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R198]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cac:PhysicalLocation)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cac:PhysicalLocation)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R199]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cac:PartyTaxScheme)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cac:PartyTaxScheme)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R200]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cac:PartyLegalEntity/cbc:RegistrationName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R201]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cac:PartyLegalEntity/cac:RegistrationAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cac:PartyLegalEntity/cac:RegistrationAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R202]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cac:PartyLegalEntity/cac:CorporateRegistrationScheme)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R203]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cac:Contact)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cac:Contact)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R204]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cac:Person)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cac:Person)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R205]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PayeeParty/cac:AgentParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PayeeParty/cac:AgentParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R206]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R207]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R208]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R209]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R210]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R211]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R212]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R213]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R214]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R215]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R216]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R217]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R218]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R219]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R220]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R221]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R222]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R223]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R224]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R225]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R226]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R227]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R228]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R229]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R230]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R231]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R232]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R233]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R234]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R235]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R236]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R237]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R238]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R239]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:RequestedDeliveryPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:RequestedDeliveryPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R240]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:PromisedDeliveryPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:PromisedDeliveryPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R241]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:EstimatedDeliveryPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:EstimatedDeliveryPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R242]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:DeliveryParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:DeliveryParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R243]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Despatch)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:Delivery/cac:DeliveryLocation/cac:Despatch)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R244]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R245]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cbc:InstructionID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cbc:InstructionID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R246]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cbc:InstructionNote)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cbc:InstructionNote)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R247]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cac:CardAccount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cac:CardAccount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R248]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayerFinancialAccount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayerFinancialAccount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R249]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cac:CreditAccount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cac:CreditAccount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R250]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R251]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AccountTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:AccountTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R252]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:CurrencyCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cbc:CurrencyCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R253]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:Country)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:Country)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R254]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R255]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:Address)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:Address)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R256]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R257]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cac:Address)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentMeans/cac:PayeeFinancialAccount/cac:FinancialInstitutionBranch/cac:FinancialInstitution/cac:Address)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R258]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentTerms/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentTerms/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R259]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentTerms/cbc:PaymentMeansID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentTerms/cbc:PaymentMeansID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R260]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentTerms/cbc:PrepaidPaymentReferenceID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentTerms/cbc:PrepaidPaymentReferenceID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R261]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentTerms/cbc:ReferenceEventCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentTerms/cbc:ReferenceEventCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R262]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentTerms/cbc:SettlementDiscountPercent)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentTerms/cbc:SettlementDiscountPercent)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R263]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentTerms/cbc:PenaltySurchargePercent)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentTerms/cbc:PenaltySurchargePercent)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R264]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentTerms/cbc:Amount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentTerms/cbc:Amount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R265]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentTerms/cac:SettlementPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentTerms/cac:SettlementPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R266]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:PaymentTerms/cac:PenaltyPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:PaymentTerms/cac:PenaltyPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R267]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R268]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R269]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R270]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R271]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R272]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R273]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R274]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R275]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R276]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R277]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R278]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R279]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
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
          <svrl:text>[BIICORE-T10-R280]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:CalculationSequenceNumeric)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:CalculationSequenceNumeric)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R281]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:TransactionCurrencyTaxAmount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:TransactionCurrencyTaxAmount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R282]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:Percent)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:Percent)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R283]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:BaseUnitMeasure)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:BaseUnitMeasure)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R284]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:PerUnitAmount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:PerUnitAmount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R285]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRange)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRange)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R286]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRatePercent)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cbc:TierRatePercent)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R287]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R288]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:BaseUnitMeasure)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:BaseUnitMeasure)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R289]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:PerUnitAmount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:PerUnitAmount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R290]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRange)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRange)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R291]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRatePercent)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cbc:TierRatePercent)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R292]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R293]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R294]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R295]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:TaxTotal/cac:TaxSubtotal/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R296]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cbc:UUID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cbc:UUID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R297]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cbc:TaxPointDate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cbc:TaxPointDate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R298]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cbc:AccountingCostCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cbc:AccountingCostCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R299]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cbc:FreeOfChargeIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cbc:FreeOfChargeIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R300]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:OrderLineReference/cbc:SalesOrderLineID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:OrderLineReference/cbc:SalesOrderLineID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R301]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:DespatchLineReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:DespatchLineReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R302]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:ReceiptLineReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:ReceiptLineReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R303]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:BillingReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:BillingReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R304]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:DocumentReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:DocumentReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R305]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:PricingReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:PricingReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R306]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:OriginatorParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:OriginatorParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R307]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Delivery)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Delivery)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R308]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:PaymentTerms)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:PaymentTerms)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R309]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R310]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R311]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:MultiplierFactorNumeric)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:MultiplierFactorNumeric)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R312]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:PrepaidIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:PrepaidIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R313]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:SequenceNumeric)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:SequenceNumeric)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R314]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:BaseAmount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:BaseAmount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R315]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCostCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCostCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R316]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCost)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cbc:AccountingCost)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R317]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R318]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxTotal)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cac:TaxTotal)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R319]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cac:PaymentMeans)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:AllowanceCharge/cac:PaymentMeans)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R320]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:TaxTotal/cbc:RoundingAmount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:TaxTotal/cbc:RoundingAmount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R321]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:TaxTotal/cbc:TaxEvidenceIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:TaxTotal/cbc:TaxEvidenceIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R322]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:TaxTotal/cac:TaxSubtotal)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:TaxTotal/cac:TaxSubtotal)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R323]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:PackQuantity)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:PackQuantity)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R324]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:PackSizeNumeric)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:PackSizeNumeric)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R325]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:CatalogueIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:CatalogueIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R326]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:HazardousRiskIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:HazardousRiskIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R327]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:AdditionalInformation)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:AdditionalInformation)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R328]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:Keyword)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:Keyword)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R329]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:BrandName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:BrandName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R330]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:ModelName)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cbc:ModelName)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R331]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:ExtendedID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:ExtendedID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R332]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:PhysycalAttribute)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:PhysycalAttribute)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R333]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:MeasurementDimension)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:MeasurementDimension)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R334]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:IssuerParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:SellersItemIdentification/cbc:IssuerParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R335]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:ExtendedID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:ExtendedID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R336]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:PhysycalAttribute)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:PhysycalAttribute)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R337]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:MeasurementDimension)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:MeasurementDimension)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R338]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:IssuerParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:StandardItemIdentification/cbc:IssuerParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R339]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:BuyersItemIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R340]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:NatureCargo)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:NatureCargo)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R341]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CargoTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CargoTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R342]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CommodityCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:CommodityClassification/cbc:CommodityCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R343]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:ManufacturersItemIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:ManufacturersItemIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R344]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:CatalogueItemIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:CatalogueItemIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R345]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:AdditionalItemIdentification)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:AdditionalItemIdentification)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R346]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:CatalogueDocumentReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:CatalogueDocumentReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R347]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:ItemSpecificationDocumentReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:ItemSpecificationDocumentReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R348]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:OriginCountry)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:OriginCountry)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R349]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TransactionConditions)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TransactionConditions)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R350]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:HazardousItem)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:HazardousItem)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R351]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:ManufacturerParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:ManufacturerParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R352]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:InformationContentProviderParty)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:InformationContentProviderParty)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R353]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:OriginAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:OriginAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R354]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:ItemInstance)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:ItemInstance)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R355]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R356]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:BaseUnitMeasure)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:BaseUnitMeasure)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R357]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbcPerUnitAmount)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbcPerUnitAmount)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R358]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReasonCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReasonCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R359]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReason)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TaxExemptionReason)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R360]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TierRange)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TierRange)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R361]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TierRatePercent)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cbc:TierRatePercent)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R362]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:Name)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:Name)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R363]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R364]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R365]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionAddress)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionAddress)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R366]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:UsabilityPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:UsabilityPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R367]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:ItemPropertyGroup)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Item/cac:TaxCategory/cac:AdditionalProperty/cac:ItemPropertyGroup)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R368]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cbc:PriceChangeReason)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cbc:PriceChangeReason)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R369]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cbc:PriceTypeCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cbc:PriceTypeCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R370]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cbc:PriceType)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cbc:PriceType)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R371]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cbc:OrderableUnitFactorRate)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cbc:OrderableUnitFactorRate)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R372]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:ValidityPeriod)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:ValidityPeriod)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R373]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:PriceList)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:PriceList)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R374]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:ID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:ID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R375]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AllowanceChargeReasonCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R376]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:PrepaidIndicator)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:PrepaidIndicator)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R377]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:SequenceNumeric)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:SequenceNumeric)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R378]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCostCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCostCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R379]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCost)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cbc:AccountingCost)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R380]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress)&#10;) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Name) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:Percent) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:BaseUnitMeasure) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:PerUnitAmount) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReasonCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TaxExemptionReason) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRange) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cbc:TierRatePercent) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:Name) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:TaxTypeCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cbc:CurrencyCode) or not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxCategory/cac:TaxScheme/cac:JurisdictionRegionAddress) ) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R381]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxTotal)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:TaxTotal)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R382]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:PaymentMeans)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:Price/cac:AllowanceCharge/cac:PaymentMeans)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R383]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:OrderLineReference/cbc:UUID)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:OrderLineReference/cbc:UUID)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R384]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:OrderLineReference/cbc:LineStatusCode)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:OrderLineReference/cbc:LineStatusCode)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R385]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite1 and not(cac:InvoiceLine/cac:OrderLineReference/cac:OrderReference)) or not ($Prerequisite1)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite1 and not(cac:InvoiceLine/cac:OrderLineReference/cac:OrderReference)) or not ($Prerequisite1)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R386]-A conformant CEN BII invoice core data model SHOULD not have data elements not in the core.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates mode="M7" select="*|comment()|processing-instruction()" />
  </xsl:template>

	<!--RULE -->
<xsl:template match="/ubl:Invoice/cac:LegalMonetaryTotal" mode="M7" priority="1000">
    <svrl:fired-rule context="/ubl:Invoice/cac:LegalMonetaryTotal" />

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cbc:TaxExclusiveAmount)=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cbc:TaxExclusiveAmount)=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R400]-Element 'TaxExclusiveAmount' must occur exactly 1 times.</svrl:text>
        </svrl:failed-assert>
      </xsl:otherwise>
    </xsl:choose>

		<!--ASSERT -->
<xsl:choose>
      <xsl:when test="($Prerequisite2 and count(cbc:TaxInclusiveAmount)=1) or not ($Prerequisite2)" />
      <xsl:otherwise>
        <svrl:failed-assert test="($Prerequisite2 and count(cbc:TaxInclusiveAmount)=1) or not ($Prerequisite2)">
          <xsl:attribute name="flag">warning</xsl:attribute>
          <xsl:attribute name="location">
            <xsl:apply-templates mode="schematron-select-full-path" select="." />
          </xsl:attribute>
          <svrl:text>[BIICORE-T10-R401]-Element 'TaxInclusiveAmount' must occur exactly 1 times.</svrl:text>
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
