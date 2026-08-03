# Testing and Quality

**Document ID:** ARCH-018  
**Version:** 2.0  
**Status:** Version 2.0.0 implementation baseline  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

## Automated baseline

The Maven build compiles with Java release 17 and runs JUnit through Surefire.
The current source contains tests for CLI parsing, bootstrap/runtime
configuration, RSA password handling, database pool setup, audit repositories,
plugin registration/selection/coordination, HTTP download, HTML discovery, CSV
and Excel parsing, and all three provider plugins.

Mocked JDBC repository tests verify SQL interaction and failure handling at unit
level; they are not live SQL Server integration evidence.

## Quality tooling

`verify` executes Checkstyle, SpotBugs, PMD, JaCoCo report generation and Maven
dependency analysis. The property `quality.failOnViolation` defaults to `false`,
so static and dependency findings are advisory unless strict mode is selected:

```powershell
mvn clean verify -Dquality.failOnViolation=true
```

Compilation and test failures still fail ordinary verification.

## Gaps

The automated baseline does not prove:

- clean/repeat installation on SQL Server;
- least-privilege permissions;
- real transaction commit, rollback, lock and pooled-session behaviour;
- live-provider acceptance;
- documentation output layout;
- absence of secrets from Git history and release artefacts; or
- a minimum test-coverage percentage.

The CLI/registry suite must cover repeated selection, lifecycle exclusivity, external registration-file rules and the `-d`/`-n` short-option decision.

## Required release evidence

A release candidate combines:

1. unit and component tests;
2. strict static-analysis review;
3. live SQL Server integration and rollback tests;
4. controlled dry/write acceptance runs;
5. documentation validation and rendered-manual inspection;
6. dependency/licence review; and
7. credential, private-key and customer-data scanning.

A green ordinary CI build is necessary but not sufficient because the quality
plugins are advisory by default and the hosted workflow does not provide a real
SQL Server acceptance environment.
