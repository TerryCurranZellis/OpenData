# Local Build, Test and Run

**Document ID:** DEV-BUILD-001  
**Version:** 3.0.0  
**Status:** Current Version 3.0.0 developer procedure  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Prerequisites

- JDK 24 or later; the Maven compiler uses `release=24`;
- current development environment: JDK 26;
- Maven 3.9 or later;
- Git;
- current development IDE: Apache NetBeans 31 (another Java IDE is optional);
- SQL Server for registration, runtime configuration and write-mode tests;
- Windows PowerShell 5.1 for maintained repository automation;
- Pandoc and PlantUML for documentation generation;
- XeLaTeX plus `rsvg-convert` or Inkscape for PDF generation; and
- Microsoft HTML Help Workshop when CHM output is required.

The Maven Enforcer plugin actively requires Maven 3.9+ and Java 24+. A later
JDK can be used for development, but code must remain compatible with the Java 24
release target.

## Build and quality

```powershell
mvn clean test
mvn clean verify
```

Review the configured Checkstyle, SpotBugs, PMD, JaCoCo and dependency-analysis
outputs even when advisory quality settings allow Maven to complete.

## Run from an IDE

Configure the IDE with:

```text
Main class: com.towermarsh.opendata.OpenData
Working directory: repository root
```

No arguments starts the JavaFX GUI. Example explicit arguments are:

```text
--gui
--plugin ofgem --dry-run
--plugin ofgem --plugin openmeteo --execute --parallelism 2
```

## Minimum developer verification

1. run the complete unit suite on the changed branch;
2. verify compilation/tests on the Java 24 minimum baseline before release;
3. smoke-test the merged GUI on the current JDK 26 development environment;
4. list registered plugins and inspect at least one plugin detail;
5. run Ofgem, OpenMeteo and Octopus dry-runs separately;
6. run a controlled multi-plugin execution with bounded parallelism;
7. test GUI registration, state changes, details, settings, logs and Help;
8. test GUI Execute/Dry-run with the live-log completion gate;
9. validate documentation and render diagrams; and
10. run the SQL Server acceptance matrix for persistence/configuration changes.

Octopus dry-run skips completion-ledger access and parses matching input PDFs
without provider writes or archive movement.

## Documentation build

```powershell
. .\scripts\Invoke-Documentation.ps1
Invoke-Documentation -ProjectRoot $PWD -Action Test -FailOnWarning
Invoke-Documentation -ProjectRoot $PWD -Action All -RenderDiagrams
```

Use `scripts/Convert-PlantUml.ps1` directly when only diagrams need to be
regenerated.

## Local and generated files

Do not commit passwords, external plugin registration files, logs, downloads,
database backups, customer PDFs, private PFX files or generated manuals unless
repository policy explicitly identifies an output as maintained.
