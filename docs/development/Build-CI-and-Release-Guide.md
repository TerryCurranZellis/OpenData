# Build, CI and Release Guide

**Document ID:** DEV-CI-001  
**Version:** 3.0.0  
**Status:** Version 3.0.0 implementation baseline  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

![Build, documentation and release pipeline](../diagrams/generated/ci-release-pipeline.svg)

## Build environment

The POM enforces Maven 3.9 or later and Java 24 or later. Maven compilation uses
`release=24`. The current developer workstation baseline is JDK 26 with Apache
NetBeans 31, but release verification must still include JDK 24.

## Local build stages

```powershell
mvn clean test
mvn clean verify
```

`verify` compiles, runs unit tests and executes the configured quality/reporting
plugins. Review their outputs according to the POM's advisory/strict settings.

## Documentation checks

```powershell
. .\scripts\Invoke-Documentation.ps1
Invoke-Documentation -ProjectRoot $PWD -Action Test -FailOnWarning
Invoke-Documentation -ProjectRoot $PWD -Action All -RenderDiagrams
```

The Windows Technical User Guide CHM additionally requires Microsoft HTML Help
Workshop.

## GitHub workflows

The repository build workflow verifies the Java 24 minimum baseline. The
documentation workflow validates/generated maintained manuals and the release
workflow handles tagged or manually dispatched releases according to repository
configuration.

Hosted CI does not replace SQL Server/live-provider acceptance, GUI interaction
acceptance, Windows Help verification or final distribution inspection.

## Version 3.0.0 release preparation

A release candidate must satisfy the
[final release checklist](../release/Final-Release-Checklist.md), including:

- clean `mvn clean verify` on Java 24;
- GUI acceptance and final screenshot capture;
- CHM Help plus JavaFX fallback verification;
- clean documentation validation and diagram rendering;
- clean/repeat SQL Server installation;
- plugin dry-run/write, rollback and idempotency evidence;
- dependency and licence review, including JavaFX;
- proof that credentials, private keys, statements and database backups are not
  present in release artifacts; and
- packaging/checksum verification.

If using the repository packaging script, create/test the intended package from
the release candidate:

```powershell
.\scripts\Build-Windows-Package.ps1 -Type app-image
```

Record the actual package layout and launch commands in release evidence. Do not
describe an artifact as self-contained until it has been tested on a clean
machine.

## Evidence retention

Retain the verified commit, Java and Maven versions, dependency report, test and
quality reports, SQL Server version/scripts, GUI acceptance results, screenshot
set, documentation outputs, checksums and release approval. Redact or exclude
secrets and customer source data.
