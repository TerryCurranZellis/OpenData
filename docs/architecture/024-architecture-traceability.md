# Architecture Traceability

**Document ID:** ARCH-024  
**Version:** 1.2  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

::: {.landscape-table}

*Architecture decisions mapped to implementation and verification evidence.*

| Decision | Implementation evidence | Verification |
|---|---|---|
| ADR-0014 separate framework/business tables | `core` and `ofgem` schemas | inspect scripts 010/020 |
| ADR-0029 shared discovery/parsers | `discovery`, CSV and Excel parser packages | parser/discovery tests |
| ADR-0030 managed connection pool | `SQLServerResource`, `DatabasePoolConfiguration` | implementation variance recorded; live pool pending |
| ADR-0031 dimensional Ofgem fact | `ofgem.price_cap_*` tables and `plugin.ofgem.transform.model` records | schema, model and extractor tests |
| ADR-0032 explicit JDBC repositories | plugin-local `load` repositories | mocked JDBC tests; live integration pending |
| ADR-0033 ordered SQL migrations | split SQL script sets | fresh/repeat install pending |
| ADR-0034 audit and provenance | `core.ingestion_*`, `source_file` | mocked audit repository tests |
| ADR-0035 transactional replacement | `OfgemPersistenceRepository.persist` transaction | source review; live rollback pending |
| ADR-0036 least-privilege role | `opendata_app`, two grant scripts | live permission test pending |
| ADR-0037 source-cell lineage | `source_sheet`, `source_cell` | sample value trace test |
| ADR-0038 bounded parallel execution | `PluginExecutionCoordinator`, `PluginThreadFactory` | coordinator concurrency/failure tests |
| ADR-0039 database-scoped concurrency | repository transactions and OpenMeteo `sp_getapplock` | mocked repository tests; live contention pending |
| ADR-0040 OpenMeteo relational storage | `openmeteo.Location`, `openmeteo.DailyWeather` | mocked repository tests; live idempotency pending |
| ADR-0041 contextual JUL | `PluginLogContext`, `ContextualLogFormatter` | source review; focused formatter/context tests missing |
| ADR-0042 side-effect-free dry run | no-op audit and unavailable database resource | coordinator unit tests; plugin dry-run acceptance pending |
| ADR-0043 plugin-local pipeline packages | Ofgem/OpenMeteo package trees and Java template | package/import audit and stage tests |

:::

## Required integration tests

Final acceptance requires:

- `mvn clean test` with the real dependency graph;
- fresh SQL Server database installation;
- repeated SQL script execution;
- successful application health check;
- successful Annex 9 import;
- rollback test after an induced batch failure;
- permission test proving the application user cannot alter schemas;
- value-to-workbook cell trace for representative rows.
