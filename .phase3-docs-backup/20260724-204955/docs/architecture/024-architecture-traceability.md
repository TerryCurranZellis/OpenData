# Architecture Traceability

**Document ID:** ARCH-024  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 24 July 2026

---

| Decision | Implementation evidence | Verification |
|---|---|---|
| ADR-0014 separate framework/business tables | `core` and `ofgem` schemas | inspect scripts 010/020 |
| ADR-0029 shared discovery/parsers | `discovery`, CSV and Excel parser packages | parser/discovery tests |
| ADR-0030 managed connection pool | `SQLServerResource`, `DatabasePoolConfig` | pool config/resource tests |
| ADR-0031 dimensional Ofgem fact | `ofgem.price_cap_*` tables and records | schema and extractor tests |
| ADR-0032 explicit JDBC repositories | `SqlServer*Repository` classes | repository integration tests |
| ADR-0033 ordered SQL migrations | scripts 001, 010, 020, 030, 090 | fresh/repeat install tests |
| ADR-0034 audit and provenance | `core.ingestion_*`, `source_file` | audit repository tests |
| ADR-0035 transactional replacement | `replaceLevels` transaction | rollback integration test |
| ADR-0036 least-privilege role | `opendata_app`, grant script 090 | permission test |
| ADR-0037 source-cell lineage | `source_sheet`, `source_cell` | sample value trace test |

## Required integration tests

The current generated source passed static Java 17 compilation, but final
acceptance requires:

- `mvn clean test` with the real dependency graph;
- fresh SQL Server database installation;
- repeated SQL script execution;
- successful application health check;
- successful Annex 9 import;
- rollback test after an induced batch failure;
- permission test proving the application user cannot alter schemas;
- value-to-workbook cell trace for representative rows.
