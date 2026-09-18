# ADR-0060: Apply build-environment and quality verification in every module POM

**Version:** 3.1.0  
**Status:** Accepted for Version 3.1.0 implementation  
**Date:** 2026-09-18  
**Decision owners:** OpenData maintainers

---

## Context

Before the module split, the effective build verification lived only in the
single application POM. After the split, `opendata-api` and `opendata-common`
would otherwise compile without their own environment, test, coverage, and
quality checks.

## Decision

Require each module POM to declare the common verification plugins needed to
validate that module inside the shared reactor build.

The module-local verification baseline includes:

- Maven Enforcer for Java and Maven version checks;
- Surefire for module tests;
- Checkstyle, SpotBugs, JaCoCo, Javadoc, and dependency analysis; and
- consistent compiler and JAR metadata configuration.

## Consequences

### Positive

- every module is checked against the same Java and Maven baseline;
- split-out modules fail fast when they drift from the build standard; and
- quality and coverage reports remain available at module granularity.

### Negative or limiting

- module POM files become more repetitive;
- shared plugin-version properties must stay aligned in the parent POM; and
- advisory quality settings still require human review of reports.

## Rejected alternatives

### Keep verification only in `opendata-core`

Rejected because `opendata-api` and `opendata-common` would no longer prove that
they compile and test correctly under the supported toolchain.

### Rely only on parent-level aggregation

Rejected because the requirement is to verify each module through its own POM
and make that ownership visible at the module boundary.
