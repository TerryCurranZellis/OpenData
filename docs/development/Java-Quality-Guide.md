# Java Quality Guide

**Document ID:** DEV-QUALITY-001  
**Version:** 2.0  
**Status:** Implemented advisory quality baseline  
**Baseline date:** 3 August 2026  
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
| Compilation | Maven Compiler Plugin, `release=17` | Fails build |
| Unit tests | Maven Surefire | Fails build |
| Checkstyle | `config/quality/checkstyle.xml` | Reports unless strict |
| SpotBugs | Maximum effort, medium threshold | Reports unless strict |
| PMD | `config/quality/pmd-ruleset.xml` | Reports unless strict |
| JaCoCo | Test coverage report | Report only; no threshold |
| Dependency analysis | Maven Dependency Plugin | Warns unless strict |

The POM defines `quality.failOnViolation=false`. This means normal verification
still fails for compilation and test failures, but static-analysis and
dependency-analysis findings are advisory. Strict mode changes that property to
`true`.

## Commands

Standard verification:

```powershell
mvn clean verify
```

Repository wrapper:

```powershell
.\scripts\Invoke-Code-Quality.ps1
```

Strict static-analysis enforcement:

```powershell
.\scripts\Invoke-Code-Quality.ps1 -Strict
```

Equivalent Maven command:

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

A successful non-strict build does **not** mean Checkstyle, SpotBugs, PMD or
dependency analysis found nothing. Review the console and generated reports.
Treat high-confidence defect findings as release blockers even when the current
advisory configuration permits the build to continue.

The GitHub build workflow runs ordinary `mvn clean verify`; it therefore uses the
same advisory static-analysis setting. Tagged release automation is not a
substitute for reviewing the reports.

## Source-quality expectations

- All production packages containing Java classes have `package-info.java`.
- Public contracts and non-obvious transaction, concurrency and security
  behaviour require useful Javadoc.
- New warnings should not be introduced merely because the historical baseline
  is advisory.
- Suppressions must be narrow, justified and reviewed.
- Direct `System.out` or `System.err` use is acceptable only at a deliberate CLI
  boundary; application diagnostics use `java.util.logging`.
- Test names should describe behaviour, not implementation mechanics.

## Current limitations

The POM contains a commented-out Maven Enforcer configuration; Java and Maven
minimum versions are documented but are not currently enforced by that plugin.
JaCoCo creates a report but no minimum coverage rule is configured. Javadoc
warnings do not fail the build. These are explicit baseline limitations, not
claims of strict quality gating.
