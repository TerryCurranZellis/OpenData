# Documentation Audit

**Document ID:** REVIEW-DOC-AUDIT-001  
**Version:** 1.2  
**Status:** Superseded by the 26 July review  
**Baseline date:** 26 July 2026

## Summary

The 22 July audit correctly separated the original framework design from the
then-current implementation. The code has since advanced. The authoritative
gap list is now
[DOCUMENTATION-GAP-ANALYSIS-2026-07-26.md](../review/DOCUMENTATION-GAP-ANALYSIS-2026-07-26.md).

## Corrected statements

| Earlier statement | Current wording |
|---|---|
| Production ready | Active development; foundations implemented |
| Full plugin architecture | Registry, reflection factory and execution coordinator are implemented for Ofgem and OpenMeteo |
| Scheduling support | Internal scheduler deferred; use external scheduling |
| Database independent | Repository abstraction exists; SQL Server is the only current implementation |
| Complete ETL pipeline | Concrete plugin flows execute; generic ETL stage contracts are not the active runtime coordinator |
| Current version 0.1.0 | Maven version is 1.0.0; runtime reports the manifest version or `development` |
| Only original package list | Added current `cli` package and current configuration service design |

## Documents that should be treated as target-state material

Any document describing the following without an implementation qualifier must
still be marked future-state or transitional:

- plugin discovery without the explicit classpath index;
- internal scheduler;
- database-backed plugin settings;
- multiple database engines;
- a unified run/provenance audit model;
- executable/fat-JAR packaging;
- process exit-code mapping;
- production secret-provider integration.

## Recommended repository cleanup

1. Remove the unused legacy `src/main/resources/application.properties`.
2. Remove the classpath database password and require an external value.
3. Unify `core.PluginRun` and `core.ingestion_run`.
4. Configure executable packaging and explicit process exit codes.
5. Complete live SQL Server, rollback and permission verification.
