# OpenData Third-Party Software Notices

**Project:** OpenData  
**Project version:** 1.0.0  
**Notice reviewed:** 31 July 2026  
**Project copyright:** Copyright 2026 Terry Curran

## Purpose of this document

OpenData is licensed under the Apache License, Version 2.0. OpenData also
uses third-party software that is supplied under separate licences.

This document:

- identifies the third-party software directly declared or used by OpenData;
- records the applicable licence for each component;
- distinguishes production dependencies from test, build and documentation
  tooling; and
- explains the notice obligations that apply when OpenData is redistributed.

The third-party licences remain the property of their respective copyright
holders. Nothing in the OpenData licence changes or replaces those licences.

This document is an attribution and compliance index. It does not replace the
complete licence texts, copyright notices or upstream `NOTICE` files supplied
with third-party software.

## Production and runtime dependencies

The following components are directly declared as non-test dependencies in the
OpenData Maven `pom.xml`.

| Component | Maven coordinate | Version | Use in OpenData | Licence |
|---|---|---:|---|---|
| Jackson Databind | `com.fasterxml.jackson.core:jackson-databind` | 2.22.1 | JSON parsing and data binding | Apache License 2.0 |
| Apache Commons CSV | `org.apache.commons:commons-csv` | 1.14.1 | CSV parsing and writing | Apache License 2.0 |
| Apache Commons CLI | `commons-cli:commons-cli` | 1.11.0 | Command-line option parsing | Apache License 2.0 |
| Apache Commons DBCP | `org.apache.commons:commons-dbcp2` | 2.14.0 | JDBC connection pooling | Apache License 2.0 |
| jsoup | `org.jsoup:jsoup` | 1.22.2 | HTML retrieval and parsing | MIT License |
| Apache POI OOXML | `org.apache.poi:poi-ooxml` | 5.5.1 | Microsoft Excel and OOXML processing | Apache License 2.0 |
| Apache Log4j to JUL adapter | `org.apache.logging.log4j:log4j-to-jul` | 2.26.1 | Routes Log4j API messages to `java.util.logging` | Apache License 2.0 |
| Microsoft JDBC Driver for SQL Server | `com.microsoft.sqlserver:mssql-jdbc` | 13.5.0.jre11-preview | SQL Server database connectivity | MIT License |
| Apache PDFBox | `org.apache.pdfbox:pdfbox` | 3.0.7 | PDF text extraction and processing | Apache License 2.0 |

### Required attribution notes

The Apache components listed above are developed by the Apache Software
Foundation and are licensed under the Apache License, Version 2.0. Their
original JAR files may contain `META-INF/LICENSE` and `META-INF/NOTICE` files.
Those files must not be removed when the JARs are redistributed.

jsoup is distributed under the MIT License and is copyright Jonathan Hedley and
contributors. The jsoup copyright notice and MIT permission notice must be
retained in copies or substantial portions of the software.

The Microsoft JDBC Driver for SQL Server is distributed under the MIT License
and is copyright Microsoft Corporation and contributors. Its copyright notice
and MIT permission notice must be retained in copies or substantial portions of
the driver.

## Test-only dependencies

These components are used to compile or execute OpenData tests and are not
required by the normal application runtime.

| Component | Maven coordinate | Version | Use in OpenData | Licence |
|---|---|---:|---|---|
| JUnit Jupiter | `org.junit.jupiter:junit-jupiter` | 6.1.1 | Unit-test framework | Eclipse Public License 2.0 |
| Mockito Core | `org.mockito:mockito-core` | 5.23.0 | Mock objects in tests | MIT License |
| Mockito JUnit Jupiter | `org.mockito:mockito-junit-jupiter` | 5.23.0 | Mockito integration with JUnit Jupiter | MIT License |

Test dependencies may resolve additional libraries, including JUnit Platform,
OpenTest4J, API Guardian, Byte Buddy and Objenesis. Their exact versions and
licences must be taken from the resolved Maven dependency graph for the
particular OpenData release.

## Build and quality tooling

The following software is used or configured for building, testing, checking
or documenting the Java source. It is development tooling and is not part of
the OpenData runtime unless it is deliberately included in a distribution.

| Tool or plugin | Version configured by OpenData | Purpose | Principal licence |
|---|---:|---|---|
| Apache Maven | Build environment version selected by the developer or CI system | Dependency management and build lifecycle | Apache License 2.0 |
| Maven Compiler Plugin | 3.15.0 | Java compilation | Apache License 2.0 |
| Maven Surefire Plugin | 3.5.6 | Test execution | Apache License 2.0 |
| Maven Checkstyle Plugin | 3.6.0 | Runs Checkstyle | Apache License 2.0 |
| Checkstyle | 10.26.1 | Source-code style analysis | LGPL 2.1 or later |
| SpotBugs Maven Plugin | 4.9.3.1 | Runs SpotBugs analysis | Apache License 2.0 |
| SpotBugs | Resolved by the plugin | Static bug analysis | LGPL 2.1 |
| Maven PMD Plugin | 3.27.0 | Runs PMD analysis | Apache License 2.0 |
| PMD | Resolved by the plugin | Source-code analysis | BSD-style licence, with some Apache-2.0 material |
| JaCoCo Maven Plugin | 0.8.13 | Test coverage reporting | Eclipse Public License 2.0 |
| Maven Dependency Plugin | 3.8.1 | Dependency analysis | Apache License 2.0 |
| Maven Javadoc Plugin | 3.11.2 | Javadoc generation | Apache License 2.0 |
| Maven Enforcer Plugin | 3.6.2, currently present but disabled in the POM | Build-environment rules | Apache License 2.0 |
| Apache NetBeans IDE | Development environment used to edit, compile, test and debug OpenData | Apache License 2.0. Apache NetBeans is not required to build or run OpenData and is not distributed with the project. |

Apache NetBeans is the project maintainer's preferred integrated development
environment. Its use does not make NetBeans part of OpenData, and contributors
may use any suitable editor or IDE. Apache NetBeans and its associated files
are not included in OpenData release packages.

Maven plugins have their own transitive dependencies. Those dependencies are
part of the build environment and should be included in a generated licence
report when the build toolchain itself is redistributed, embedded in a build
image, or supplied as an offline build bundle.

## Development, build and documentation environment

OpenData documentation is generated using external tools. These tools are not
embedded in the OpenData Java application.

| Tool | Use in OpenData | Licence or notice |
|---|---|---|
| Pandoc | Converts assembled Markdown into HTML, DOCX and PDF outputs | GNU General Public License, version 2 |
| PlantUML | Converts `.puml` diagram sources into generated diagrams | The standard distribution reports the GPL; PlantUML also publishes alternative distributions under LGPL, Apache, EPL and MIT terms. Record the actual JAR's licence using `java -jar plantuml.jar -license`. |
| Graphviz | Diagram layout engine used by PlantUML for applicable diagram types | Eclipse Public License 2.0 for current Graphviz versions |
| PowerShell | Executes the repository documentation and maintenance scripts | PowerShell 7 is MIT-licensed. Windows PowerShell is supplied as part of Windows and is governed by the applicable Microsoft product terms. |
| Oracle Java Development Kit 17 | Reference JDK used to compile, test and run OpenData during development | Oracle JDK licensing depends on the precise update release. Oracle JDK 17.0.12 and earlier were made available under the Oracle No-Fee Terms and Conditions; Oracle JDK 17.0.13 and later are generally available under the Oracle Technology Network License Agreement for Java SE or through applicable Oracle customer entitlements. Oracle JDK is not distributed with OpenData. |

OpenData targets the Java 17 platform and is not intended to require Oracle JDK
specifically. Oracle JDK is the reference development environment used by the
project maintainer. Compatible Java 17 or later JDK distributions may be used,
subject to their own certification, compatibility and licensing terms.

The precise Oracle JDK version and update number used for a release should be
recorded in the release build information because Oracle's applicable licence
can vary by Java version and update release
Generated SVG, PNG or other images produced from OpenData's own PlantUML
sources are not treated by PlantUML as covered works merely because PlantUML
generated them. The licence of the OpenData diagram source and resulting
project documentation remains governed by the OpenData project terms.

## Transitive dependencies

Maven automatically resolves dependencies required by the directly declared
components. A static notice cannot safely guarantee the exact transitive
dependency set because it can change whenever a direct dependency version,
plugin version, dependency-management rule or Maven resolver changes.

For every release, the maintainer should generate and retain the resolved
dependency inventory:

```powershell
mvn -DskipTests dependency:tree `
    -DoutputFile=target/dependency-tree.txt

mvn -DskipTests dependency:list `
    -DincludeScope=runtime `
    -DoutputFile=target/runtime-dependencies.txt
```

A release licence report should be generated from the same Maven configuration
used to build the release. Any licence-reporting plugin added for this purpose
should be pinned to an explicit version in `pom.xml`.

The generated report should cover at least:

- direct and transitive runtime dependencies;
- test dependencies;
- build-plugin dependencies where build tooling is redistributed;
- each component's name, group ID, artifact ID and resolved version;
- its licence identifier and copyright notice; and
- any upstream `NOTICE` text that must be reproduced.

## Distribution requirements

### Source-only distribution

When OpenData is distributed as source code and third-party binary JARs are not
included, retain:

- the OpenData `LICENSE` file;
- the OpenData `NOTICE` file;
- this `THIRD-PARTY-NOTICES.md` file; and
- the Maven `pom.xml`, which identifies the dependency coordinates and
  versions.

Users obtaining dependencies through Maven receive the third-party artefacts
under their original licences.

### Distribution with separate third-party JAR files

When dependency JARs are supplied alongside OpenData:

- distribute each JAR without removing or modifying its embedded licence and
  notice files;
- include this document;
- include or make available the corresponding complete third-party licence
  texts; and
- retain all copyright, attribution and trademark notices.

### Combined, shaded or executable JAR distribution

If a future release unpacks dependencies into a combined or shaded JAR, the
original `META-INF/LICENSE` and `META-INF/NOTICE` files can be overwritten or
lost. Before distributing such an artefact:

1. generate the complete resolved dependency and licence inventory;
2. aggregate all applicable copyright and attribution notices;
3. include the full licence texts in the distribution;
4. place the aggregated notices in a readable `META-INF/NOTICE`,
   `THIRD-PARTY-NOTICES.md`, application notice screen or accompanying
   documentation, as required by the relevant licences; and
5. verify that any LGPL, EPL, GPL or other reciprocal-licence obligations are
   satisfied for the form in which the applicable component is distributed.

## Software not distributed by OpenData

Merely using a development product or hosted service does not make it part of
the OpenData distribution. IDEs, operating systems, Git hosting, CI hosting,
database servers, office applications and similar external products are
therefore not listed as OpenData dependencies unless their files are copied
into a release.

Microsoft SQL Server is an external database product and is not included with
OpenData. Use of SQL Server is governed by the licence applicable to the user's
SQL Server installation. The separately identified Microsoft JDBC Driver is
open-source software under the MIT License.

## Data providers and external services

This file covers software. It does not grant rights to data downloaded or
processed by OpenData plugins.

Data obtained from providers such as Ofgem or Open-Meteo remains subject to the
provider's current data licence, attribution requirements, API terms and usage
policies. Those obligations should be recorded separately in a
`DATA-SOURCE-NOTICES.md` file and reviewed whenever a plugin or data source is
added or changed.

Use of a provider's name identifies the source or intended integration only and
does not imply sponsorship or endorsement of OpenData.

## Official licence references

- Apache License 2.0: <https://www.apache.org/licenses/LICENSE-2.0>
- MIT License: <https://opensource.org/license/mit>
- Eclipse Public License 2.0: <https://www.eclipse.org/legal/epl-2.0/>
- GNU Lesser General Public License 2.1: <https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html>
- GNU General Public License 2.0: <https://www.gnu.org/licenses/old-licenses/gpl-2.0.html>
- PMD licence: <https://pmd.github.io/pmd/license.html>
- PlantUML licence information: <https://plantuml.com/faq>
- Graphviz licence: <https://graphviz.org/license/>

## Maintenance

Review and regenerate this notice whenever:

- a Maven dependency or plugin is added, removed or upgraded;
- a documentation or build tool is added or replaced;
- the release packaging begins to include third-party binaries;
- a combined or shaded JAR is introduced;
- a container image or offline build bundle is distributed; or
- an upstream component changes its licence or required notice text.

The resolved dependency report created for a release is authoritative for the
component versions actually used. Where this manually maintained document and a
component's original licence or notice disagree, the original third-party
licence and notice control.
