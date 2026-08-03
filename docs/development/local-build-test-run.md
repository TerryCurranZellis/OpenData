# Local Build, Test and Run

**Document ID:** DEV-BUILD-001  
**Version:** 2.1  
**Status:** Current Version 2.0.0 developer procedure  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## Prerequisites

- JDK 17 or later, compiling with `release=17`;
- Maven 3.9 or later;
- Git;
- NetBeans or another Java IDE;
- SQL Server for registration, runtime configuration and write-mode tests;
- Windows PowerShell 5.1 for repository automation;
- Pandoc and PlantUML for documentation generation;
- XeLaTeX plus `rsvg-convert` or Inkscape for PDF generation.

The minimum Maven version is documented but the current Maven Enforcer block is
commented out, so developers must verify their environment explicitly.

## Build and quality

```powershell
mvn clean test
mvn clean verify
```

Strict quality review:

```powershell
.\scripts\Invoke-Code-Quality.ps1 -Strict
```

Normal `verify` runs the static-analysis tools in advisory mode. Inspect reports
even when Maven succeeds.

## Run from an IDE

The current POM creates a non-executable library JAR. Configure the IDE with:

```text
Main class: com.towermarsh.opendata.OpenData
Working directory: repository root
Arguments: --plugin ofgem,openmeteo --dry-run --parallelism 2
```

Do not publish `java -jar` instructions until the manifest and dependency
packaging have been implemented and tested.

## Minimum developer verification

1. run the complete unit suite;
2. execute strict static analysis and review every finding;
3. list registered plugins;
4. run Ofgem and OpenMeteo dry runs separately;
5. run them together with parallelism two;
6. test Octopus with disposable statement fixtures, an isolated database and an
   explicit archive directory in write mode;
7. validate documentation and render diagrams; and
8. run the SQL Server acceptance matrix for persistence or configuration
   changes.

Octopus dry run skips completion-ledger access and parses every matching input PDF without provider writes or archive movement.

## Testing documentation examples

The Java files under `docs/templates/plugin-java` and
`docs/examples/example-plugin` are documentation templates and are not compiled
by the project build. When they are changed, copy them into a temporary package
below `src/main/java`, add a temporary properties resource, compile, then remove
the temporary files. This prevents examples drifting away from the current
framework API.

## Local and generated files

Do not commit passwords, external plugin registration files, logs, downloads, database
backups, customer PDFs, private PFX files or generated manuals unless repository
policy explicitly identifies an output as maintained.
