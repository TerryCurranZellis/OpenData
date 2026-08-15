# OpenData Third-Party Software Notices

**Project version:** 3.0.0  
**Review date:** 15 August 2026  
**Project licence:** Apache License, Version 2.0

## Purpose

OpenData uses third-party libraries and external development, build and
documentation tools. Those components retain their own licences and notices.
The OpenData Apache 2.0 licence does not relicense them.

The component versions below are taken from the Version 3.0.0 `pom.xml` baseline.
A release that redistributes dependency JARs must also preserve the licence and
notice material supplied with the exact resolved artifacts, including transitive
dependencies.

## Direct runtime dependencies

| Component | Coordinate | Version | Purpose | Principal licence |
|---|---|---:|---|---|
| OpenJFX JavaFX Controls | `org.openjfx:javafx-controls` | 26.0.1 | JavaFX controls and desktop UI | GPL-2.0 with Classpath Exception |
| OpenJFX JavaFX FXML | `org.openjfx:javafx-fxml` | 26.0.1 | FXML loading and controller binding | GPL-2.0 with Classpath Exception |
| Jackson Databind | `com.fasterxml.jackson.core:jackson-databind` | 2.22.1 | JSON binding | Apache-2.0 |
| Apache Commons CSV | `org.apache.commons:commons-csv` | 1.14.1 | CSV parsing | Apache-2.0 |
| Apache Commons CLI | `commons-cli:commons-cli` | 1.11.0 | Command-line parsing | Apache-2.0 |
| Apache Commons DBCP | `org.apache.commons:commons-dbcp2` | 2.14.0 | JDBC connection pooling | Apache-2.0 |
| jsoup | `org.jsoup:jsoup` | 1.22.2 | HTML parsing and link discovery | MIT |
| Apache POI OOXML | `org.apache.poi:poi-ooxml` | 5.5.1 | Excel/OOXML processing | Apache-2.0 |
| Log4j-to-JUL adapter | `org.apache.logging.log4j:log4j-to-jul` | 2.26.1 | Routes supported Log4j API calls to JUL | Apache-2.0 |
| Microsoft JDBC Driver for SQL Server | `com.microsoft.sqlserver:mssql-jdbc` | 13.5.0.jre11-preview | SQL Server access | MIT |
| Apache PDFBox | `org.apache.pdfbox:pdfbox` | 3.0.8 | PDF text extraction | Apache-2.0 |

JavaFX also resolves supporting OpenJFX modules such as JavaFX Base and JavaFX
Graphics transitively. The release dependency inventory is authoritative for the
exact platform-specific JavaFX artifacts included in a binary distribution.

The Microsoft JDBC coordinate is a **preview** release. That is a release-
readiness concern rather than a licensing exception. A formal release must
either approve that dependency explicitly or replace it through a separate
source/build change.

## Test and provided dependencies

| Component | Coordinate | Version | Scope | Principal licence |
|---|---|---:|---|---|
| JUnit Jupiter | `org.junit.jupiter:junit-jupiter` | 6.1.2 | Test | EPL-2.0 |
| Mockito Core | `org.mockito:mockito-core` | 5.23.0 | Test | MIT |
| Mockito JUnit Jupiter | `org.mockito:mockito-junit-jupiter` | 5.23.0 | Test | MIT |
| SpotBugs Annotations | `com.github.spotbugs:spotbugs-annotations` | 4.9.8 | Provided | LGPL-2.1 |

The resolved test and provided dependency graphs include transitive components;
the exact release graph must be retained as evidence.

## Build and quality tools

The project configures Maven Compiler, Surefire, Checkstyle, SpotBugs, PMD,
JaCoCo, Dependency and Javadoc plugins. These tools and their transitive
components are development dependencies unless an offline build environment or
build image is redistributed.

| Tool | Version/baseline | Principal licence or licensing note |
|---|---:|---|
| Apache Maven | 3.9+ required | Apache-2.0; not distributed by OpenData by default |
| Apache NetBeans | 31 (current development IDE) | Apache-2.0; not distributed by OpenData |
| JDK | 24 minimum; 26 current development JDK | Record the exact JDK distribution used for a release image; OpenData does not require one vendor |
| Checkstyle | 10.26.1 | LGPL-2.1-or-later; build-time tool |
| SpotBugs Maven Plugin | 4.10.3.0 | Build-time tool; retain upstream terms if redistributed |
| PMD Maven Plugin | 3.28.0 | Build-time tool; retain upstream terms if redistributed |
| JaCoCo Maven Plugin | 0.8.15 | EPL-2.0; build-time tool |
| Maven Enforcer Plugin | 3.6.3 | Apache-2.0; build-time tool |

## Documentation tools

| Tool | Use | Compliance rule |
|---|---|---|
| Pandoc | Markdown conversion | Not distributed by default; record exact version in release evidence |
| PlantUML | Diagram rendering | Record the licence of the exact PlantUML distribution/JAR used |
| Graphviz | Diagram layout | Not distributed by default; retain applicable licence if bundled |
| Microsoft HTML Help Workshop | CHM compilation on Windows | Development/documentation tool; do not redistribute unless its terms permit it |
| PowerShell | Repository automation | PowerShell 7 is MIT; Windows PowerShell follows Microsoft product terms |

Generated documentation and diagrams remain OpenData project material; use of a
tool to generate them does not by itself relicense the output. Third-party
fonts, themes, icons or templates incorporated into output must be reviewed
separately.

## Transitive inventory required for release

Run against the exact release commit and retain the output with release evidence:

```powershell
mvn -DskipTests dependency:tree `
    -DoutputFile=target/dependency-tree.txt

mvn -DskipTests dependency:list `
    -DincludeScope=runtime `
    -DoutputFile=target/runtime-dependencies.txt
```

Review direct and transitive runtime dependencies, test/provided dependencies,
redistributed build tooling, licence identifiers, copyright notices and all
upstream `NOTICE` requirements.

## Distribution rules

### Source-only archive

Include `LICENSE`, `NOTICE`, this file and `DATA-SOURCE-NOTICES.md`. Do not include
third-party JAR files unless their licences/notices have been collected.

### Distribution with dependency JARs or jpackage image

Retain the original dependency licence/notice metadata and include any notices
required by JavaFX and other resolved components. A jpackage/runtime image must
also record the JDK distribution and version used to create it.

### Shaded or combined executable

A combined artifact must preserve notices that would otherwise have been inside
separate JARs. A resolved dependency licence report and manual review are
mandatory before publication.

## External services and data

Ofgem, Open-Meteo and Octopus Energy are data providers or document issuers, not
runtime software dependencies. Their terms and attribution requirements are
recorded in `DATA-SOURCE-NOTICES.md`.

## Verification sources

- OpenJFX project and licence statement: https://openjfx.io/
- Apache licences and notices: https://www.apache.org/licenses/
- jsoup licence: https://github.com/jhy/jsoup/blob/master/LICENSE
- Microsoft JDBC driver licence: https://github.com/microsoft/mssql-jdbc/blob/main/LICENSE
- JUnit licence: https://github.com/junit-team/junit5/blob/main/LICENSE.md
- Mockito licence: https://github.com/mockito/mockito/blob/main/LICENSE
- PlantUML releases/licence variants: https://plantuml.com/download

Links are evidence pointers, not substitutes for licence files supplied with the
exact artifacts.

## Maintenance

Review this file whenever `pom.xml`, build plugins, JavaFX, documentation tooling
or release packaging changes. The exact resolved dependency graph remains the
release authority.
