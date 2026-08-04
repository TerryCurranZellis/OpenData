# OpenData Third-Party Software Notices

**Project version:** 2.0.0  
**Review date:** 3 August 2026  
**Project licence:** Apache License, Version 2.0

## Purpose

OpenData uses third-party libraries and external build/documentation tools. Those
components retain their own licences and notices. The OpenData Apache 2.0 licence
does not relicense them.

This file is the human-readable compliance index for the source repository. A
release that redistributes dependency JARs must also retain every upstream
`LICENSE` and `NOTICE` file supplied inside or alongside those artifacts and must
produce an inventory from the resolved release dependency graph.

## Direct runtime dependencies

The versions below are declared in `pom.xml` in the reviewed baseline.

| Component | Coordinate | Version | Purpose | Principal licence |
|---|---|---:|---|---|
| Apache Commons CLI | `commons-cli:commons-cli` | 1.11.0 | Command-line parsing | Apache-2.0 |
| Apache Commons CSV | `org.apache.commons:commons-csv` | 1.14.1 | CSV parsing | Apache-2.0 |
| Apache Commons DBCP | `org.apache.commons:commons-dbcp2` | 2.14.0 | JDBC pooling | Apache-2.0 |
| Log4j-to-JUL adapter | `org.apache.logging.log4j:log4j-to-jul` | 2.26.1 | Routes supported Log4j calls to JUL | Apache-2.0 |
| Apache PDFBox | `org.apache.pdfbox:pdfbox` | 3.0.7 | PDF text extraction | Apache-2.0 |
| Apache POI OOXML | `org.apache.poi:poi-ooxml` | 5.5.1 | Excel/OOXML processing | Apache-2.0 |
| Jackson Databind | `com.fasterxml.jackson.core:jackson-databind` | 2.22.1 | JSON binding | Apache-2.0 |
| jsoup | `org.jsoup:jsoup` | 1.22.2 | HTML parsing and link discovery | MIT |
| Microsoft JDBC Driver for SQL Server | `com.microsoft.sqlserver:mssql-jdbc` | 13.5.0.jre11-preview | SQL Server access | MIT |

The Microsoft JDBC coordinate is a **preview** release. That is a dependency and
release-readiness concern, not a licensing exception. A formal OpenData release
must either approve that preview dependency explicitly or move to a verified
stable driver through a separate source-code change.

## Test dependencies

| Component | Coordinate | Version | Principal licence |
|---|---|---:|---|
| JUnit Jupiter | `org.junit.jupiter:junit-jupiter` | 6.1.1 | EPL-2.0 |
| Mockito Core | `org.mockito:mockito-core` | 5.23.0 | MIT |
| Mockito JUnit Jupiter | `org.mockito:mockito-junit-jupiter` | 5.23.0 | MIT |

The resolved test graph also includes transitive components. Their exact versions
and licences must be taken from the Maven graph used for the release.

## Build and quality tools

The project configures Maven Compiler, Surefire, Checkstyle, SpotBugs, PMD,
JaCoCo, Dependency and Javadoc plugins. These tools and their transitive
components are development dependencies unless an offline build environment or
build image is distributed.

| Tool | Principal licence or licensing note |
|---|---|
| Apache Maven and Apache Maven plugins | Apache-2.0; not distributed by OpenData  |
| Apache NetBeans | Apache-2.0; not distributed by OpenData |
| Checkstyle | LGPL-2.1-or-later; not distributed by OpenData |
| SpotBugs | LGPL-2.1; not distributed by OpenData |
| PMD | BSD-style licence; not distributed by OpenData|
| JaCoCo | EPL-2.0;not distributed by OpenData |

## Documentation tools

| Tool | Use | Compliance rule |
|---|---|---|
| Pandoc | Markdown conversion | Not distributed by default
| PlantUML | Diagram rendering | Record the licence of the exact PlantUML distribution/JAR used |
| Graphviz | Diagram layout | Not distributed by default; retain applicable licence if bundled |
| PowerShell | Repository automation | PowerShell 7 is MIT; Windows PowerShell follows Microsoft product terms |
| Java Development Kit | Compilation and execution | Record the distribution and update used; OpenData does not require Oracle JDK specifically |

Generated documentation and diagrams remain OpenData project material; using a
tool to generate them does not by itself relicense the generated output. Any
third-party fonts, themes, icons or templates incorporated into an output must
still be reviewed separately.

## Transitive inventory required for release

Run against the exact release commit and preserve the output with the release
evidence:

```powershell
mvn -DskipTests dependency:tree `
    -DoutputFile=target/dependency-tree.txt

mvn -DskipTests dependency:list `
    -DincludeScope=runtime `
    -DoutputFile=target/runtime-dependencies.txt
```

The release review must cover direct and transitive runtime dependencies, test
dependencies, build tooling that is redistributed, licence identifiers,
copyright notices and any upstream `NOTICE` text.

## Distribution rules

### Source-only archive

Include `LICENSE`, `NOTICE`, this file and `DATA-SOURCE-NOTICES.md`. Do not include
third-party JAR files unless their licences/notices have been collected.

### Distribution with dependency JARs

Retain the original JAR metadata, including `META-INF/LICENSE` and
`META-INF/NOTICE`. Include any required MIT/EPL/LGPL/BSD notices and a resolved
component inventory.

### Shaded or combined executable

A combined artifact must preserve the notices that would otherwise have been
inside separate JARs. A dependency licence report and manual review are mandatory
before publication.

## External services and data

Ofgem, Open-Meteo and Octopus Energy are data providers or document issuers, not
runtime software dependencies. Their terms and attribution requirements are
recorded in `DATA-SOURCE-NOTICES.md`.

## Verification sources

- Apache licences and notices: https://www.apache.org/licenses/
- jsoup licence: https://github.com/jhy/jsoup/blob/master/LICENSE
- Microsoft JDBC driver licence: https://github.com/microsoft/mssql-jdbc/blob/main/LICENSE
- JUnit licence: https://github.com/junit-team/junit5/blob/main/LICENSE.md
- Mockito licence: https://github.com/mockito/mockito/blob/main/LICENSE
- PlantUML releases/licence variants: https://plantuml.com/download

Links are evidence pointers, not substitutes for the licence files supplied with
the exact artifacts.

## Maintenance

Review this file whenever `pom.xml`, build plugins, documentation tooling or
release packaging changes. A version number copied into this file is not proof
that the corresponding artifact was resolved or redistributed.
