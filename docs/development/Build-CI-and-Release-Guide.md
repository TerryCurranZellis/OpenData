# Build, CI and Release Guide

**Document ID:** DEV-CI-001  
**Version:** 2.1  
**Status:** Version 2.0.0 implementation baseline  
**Baseline date:** 3 August 2026

---

![Build, documentation and release pipeline](../diagrams/generated/ci-release-pipeline.svg)

## Local build stages

```powershell
mvn clean test
mvn clean verify
```

`verify` compiles, runs unit tests, executes Checkstyle, SpotBugs and PMD,
creates a JaCoCo report and performs dependency analysis. Static and dependency
findings are advisory unless strict mode is enabled:

```powershell
mvn clean verify -Dquality.failOnViolation=true
```

Documentation checks are separate:

```powershell
.\scripts\Validate-Documentation.ps1 -FailOnWarning
.\scripts\Build-Documentation.ps1 -RenderDiagrams
```

## GitHub workflows

The repository contains build, documentation and release workflows.

- The build workflow runs Java 24 and `mvn clean verify`, then uploads available
  test and quality reports.
- The documentation workflow validates and generates maintained manuals.
- The release workflow packages tagged or manually dispatched releases.

Ordinary CI inherits `quality.failOnViolation=false`; a green build does not
prove that static-analysis reports are empty. SQL Server and live-provider
acceptance tests are not supplied by the standard hosted workflow.

## Release preparation

A release candidate must satisfy the
[final release checklist](../release/Final-Release-Checklist.md), including:

- strict Java quality review;
- clean documentation validation and diagram rendering;
- clean and repeat SQL Server installation;
- registration and encrypted-password restart;
- plugin dry/write, rollback and idempotency evidence;
- dependency and licence review; and
- proof that credentials, private keys, statements and database backups are not
  present in release artefacts.

Prepare a local package with:

```powershell
.\scripts\New-ReleasePackage.ps1 -Version 2.0.0
```

The current Maven JAR has no executable `Main-Class` manifest and does not bundle
runtime dependencies. It must not be described as a self-contained executable.

## Evidence retention

Retain the verified commit, Java and Maven versions, dependency report, test and
quality reports, SQL Server version and scripts, documentation outputs,
checksums and release approval. Redact or exclude secrets and customer source
data.
