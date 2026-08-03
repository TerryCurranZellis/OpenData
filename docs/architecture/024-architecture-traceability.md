# Architecture Traceability

**Document ID:** ARCH-024  
**Version:** 2.0  
**Status:** Version 2.0.0 traceability baseline  
**Baseline date:** 3 August 2026

---

::: {.landscape-table}

*Architecture decisions mapped to implementation and verification evidence.*

| Decision | Implementation evidence | Verification |
|---|---|---|
| ADR-0013 explicit classpath registry | `ClasspathPluginRegistry`, `config/plugins/index.properties` | registry tests and `--list-plugins` |
| ADR-0014 separate framework/business tables | `core`, `ofgem`, `openmeteo`, `octopus` schemas | schema review and fresh install |
| ADR-0029 shared discovery/parsers | `discovery`, CSV, JSON and Excel packages | parser/discovery tests |
| ADR-0030 managed connection pool | `SQLServerResource`, `DatabasePoolConfiguration` | pool tests; live pool acceptance pending |
| ADR-0031 dimensional Ofgem fact | Ofgem schema and typed records | schema/model/extractor tests |
| ADR-0032 explicit JDBC repositories | JDBC configuration/audit and plugin-local repositories | mocked tests; live integration pending |
| ADR-0033 ordered SQL scripts | ordered `/sql` scripts and `core.schema_version` | fresh/repeat install pending |
| ADR-0034 audit and provenance | `core.PluginRun`, ingestion/source-file tables | audit repository tests |
| ADR-0035 transactional Ofgem replacement | `OfgemPersistenceRepository` transaction | source review; live rollback pending |
| ADR-0036 least-privilege role | `opendata_app` and grant scripts | live permission test pending |
| ADR-0037 source-cell lineage | Ofgem worksheet/cell fields | extractor and representative trace test |
| ADR-0038 bounded parallel execution | `PluginExecutionCoordinator`, `PluginThreadFactory` | coordinator concurrency/failure tests |
| ADR-0039 database-owned concurrency | provider transactions and OpenMeteo application lock | repository tests; live contention pending |
| ADR-0040 OpenMeteo relational storage | `openmeteo.Location`, `openmeteo.DailyWeather` | repository/idempotency tests |
| ADR-0041 contextual JUL | `PluginLogContext`, `ContextualLogFormatter` | source review and focused tests |
| ADR-0042 side-effect-free dry run | no-op audit, unavailable plugin database, provider dry-run branches | coordinator tests; Octopus extract currently violates the rule |
| ADR-0043 plugin-local pipeline packages | Ofgem, OpenMeteo and Octopus stage packages | package/import review |
| ADR-0044 local-file Octopus plugin | PDF extract, parser, transactional repository and archive finaliser | extractor/parser tests; live persistence and dry-run correction pending |
| ADR-0046 manifest documentation engine | manifests, templates and documentation scripts | documentation validation/build |
| ADR-0047 database configuration registration | registration service, JDBC source, RSA cipher and configuration tables | loader/JDBC/RSA tests; live registration pending |

:::

## Required integration tests

Final acceptance requires:

- `mvn clean test` with the declared dependency graph;
- fresh and repeated SQL Server installation;
- successful configuration registration and subsequent database-backed run;
- successful write runs for Ofgem, OpenMeteo and Octopus;
- rollback tests after induced provider failures;
- duplicate/idempotency tests for repeated source input;
- permission tests proving the application user cannot alter schemas;
- representative source-to-database lineage checks;
- release scan proving no plaintext password or private key store is included.
