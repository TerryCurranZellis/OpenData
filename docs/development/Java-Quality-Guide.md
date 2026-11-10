# Java Quality Guide

**Document ID:** DEV-QUALITY-001  
**Version:** 4.0.0  
**Status:** Current 4.0.0 baseline; 4.1.0 refactoring review  
**Baseline date:** 10 November 2026  
**Minimum Java version:** 24

---

## Purpose

OpenData uses Maven tests, static analysis, dependency analysis, coverage
reporting and Javadoc generation to detect defects and maintain a measurable
quality baseline.

## Configured verification

`mvn clean verify` currently executes:

| Control | Implementation | Default enforcement |
|---|---|---|
| Compilation | Maven Compiler Plugin, `release=24` | Fails build |
| Unit tests | Maven Surefire | Fails build |
| Checkstyle | `config/quality/checkstyle.xml` | Fails build on violation |
| SpotBugs | Maximum effort, medium threshold | Fails build on detected bugs |
| JaCoCo | Test coverage report | Report only; no threshold |
| Dependency analysis | Maven Dependency Plugin | Fails on unresolved warnings |

The 4.1.0 review branch defaults `quality.failOnViolation=true` in Maven;
Checkstyle and SpotBugs findings and dependency-analysis warnings fail the
normal verification build. PMD rules exist in the repository, but a PMD
execution is not bound to the active `verify` lifecycle.

## Commands

Standard verification:

```powershell
mvn clean verify
```

Strict static-analysis enforcement:

```powershell
mvn clean verify -Dquality.failOnViolation=true
```

Generate Javadoc explicitly:

```powershell
mvn javadoc:javadoc
```

The Javadoc plugin is configured but is not bound to `verify`. JaCoCo, test and
quality reports are written beneath `target`.

## Interpreting results

A successful strict verification build confirms that the configured Checkstyle
and SpotBugs gates, Maven dependency analysis and tests passed. Review generated
reports as additional evidence; coverage reporting has no enforced minimum.

The GitHub Actions build explicitly runs
`mvn --batch-mode --no-transfer-progress -Dquality.failOnViolation=true clean verify`. Tagged release automation is not a
substitute for reviewing the reports.

## Source-quality expectations

- All production packages, including templates, have `package-info.java`
  listing classes, records, interfaces and enums with links.
- Every Java source file has `@author Terry Curran` and a Javadoc `@version`;
  modified source files use `@version 4.1.0` and retain historical `@since`.
- `JavadocInventoryTest` and `ModuleBoundariesTest` prevent regression.
- Public contracts and non-obvious transaction, concurrency and security
  behaviour require useful Javadoc.
- New warnings should not be introduced merely because the historical baseline
  is advisory.
- Suppressions must be narrow, justified and reviewed.
- Direct `System.out` or `System.err` use is acceptable only at a deliberate CLI
  boundary; application diagnostics use `java.util.logging`.
- Test names should describe behaviour, not implementation mechanics.

## Current limitations

The Maven Enforcer plugin is active and requires Maven 3.9 or later and Java 24
or later. JaCoCo creates a report but no minimum coverage rule is configured.
Javadoc generation is configured but is not bound as a mandatory `verify`
gate. Author/version/package coverage is checked by `JavadocInventoryTest`.
JAcoCo reports do not enforce a minimum coverage percentage. These are explicit baseline limitations, not claims of strict quality
gating.
