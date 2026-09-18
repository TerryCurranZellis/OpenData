# Testing and Quality

**Document ID:** ARCH-018  
**Version:** 3.1.0  
**Status:** Version 3.1.0 modular build baseline  
**Baseline date:** 18 September 2026  
**Minimum Java version:** 24

---

## Automated baseline

The Maven reactor compiles with Java release 24 and runs JUnit through Surefire
in every module. `opendata-common` owns the shared JDBC, validation, and
strategy tests; `opendata-core` owns application, GUI, parser, registry, and
provider tests.

## Quality tooling

Each module declares the same build-environment, Checkstyle, SpotBugs, JaCoCo,
Javadoc, and dependency-analysis verification. The property
`quality.failOnViolation` defaults to `false`, so static and dependency findings
are advisory unless strict mode is selected:

```powershell
mvn clean verify -Dquality.failOnViolation=true
```
