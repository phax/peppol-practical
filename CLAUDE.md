# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Source code for the [peppol.helger.com](http://peppol.helger.com) website — a Peppol information portal with document validation tools, SMP/SML query tools, REST APIs, and SOAP web services. Single-module Maven WAR application.

## Build Commands

```bash
mvn clean install                    # Full build
mvn test                             # Run all tests
mvn -Dtest=CommentTest test          # Run single test class
mvn -Dtest=CommentTest#testMethod test  # Run single test method
mvn -DskipTests clean package        # Package WAR without tests
```

**Local development**: Run `com.helger.peppol.jetty.RunInJettyPP` from IDE or test classpath → launches Jetty on `http://localhost:8080/`.

**Docker**: `mvn clean package && docker build -t peppol-practical .` → Tomcat 10.1 on JDK 17.

## Architecture

**Web framework**: Helger Photon (Bootstrap 4 variant) — a server-side web framework with page-based navigation, AJAX handlers, and servlet integration.

**Package layout** (`com.helger.peppol`):

| Package | Purpose |
|---------|---------|
| `app` | Application config, menus, initialization (`CPPApp`, `AppSecurity`) |
| `pub` | Public-facing pages (SMP/SML queries, validation, tools) |
| `secure` | Authenticated admin pages |
| `rest` | REST API endpoints (rate-limited) |
| `ws` | SOAP web service endpoints (WSDL-generated) |
| `servlet` | Servlet/filter setup, application lifecycle |
| `ui` | Shared UI components and layouts |
| `comment` | Comment system (domain model + UI) |
| `crm` | CRM/subscriber management |
| `ajax` | AJAX request handlers |
| `testendpoint` | Peppol test endpoint functionality |

**Generated code**: JAXB classes in `com.helger.peppol.wsclient2` are generated from `src/main/webapp/WEB-INF/wsdl/pp-dvs.wsdl` by the `jaxws-maven-plugin`. Do not edit these manually.

## Key Libraries

All from the `com.helger` ecosystem — do not introduce alternatives:

- **ph-commons** — core utilities, collections (`ICommonsList`, `CommonsArrayList`), type converters
- **peppol-commons** (v12.4.x) — Peppol identifiers, SML/SMP clients, ID schemes
- **ph-ubl** — UBL 2.0–2.3 document models
- **ph-schematron** — Schematron/XSLT validation
- **phive** — validation engine and result types
- **peppol-shared-ui / peppol-shared-validation / peppol-shared-as4** — shared Peppol UI and validation components (SNAPSHOT dependency)

## Testing

- JUnit 4 (not JUnit 5)
- Tests use `PhotonAppTestRule` for application context setup
- Test data from `peppol-testfiles` dependency
- Surefire runs with `-Xmx1024m`

## Configuration

- `src/main/resources/application.properties` — main config (data path, rate limits, truststores, reCAPTCHA)
- `src/main/resources/log4j2.xml` / `log4j2.prod.xml` — logging (dev vs production)
- Webapp data stored in `generated/` directory (relative to working directory)

## Build Plugins

- **ph-jscompress-maven-plugin** — minifies JS from `src/main/webapp`
- **ph-csscompress-maven-plugin** — minifies CSS from `src/main/webapp`
- **jaxws-maven-plugin** — generates JAXB/JAX-WS code from WSDL

## Requirements

- Java 11+ to build (Java 17 in Docker, Java 21 works locally)
- Maven 3.9+
- Parent POM: `com.helger:parent-pom:3.0.3`
