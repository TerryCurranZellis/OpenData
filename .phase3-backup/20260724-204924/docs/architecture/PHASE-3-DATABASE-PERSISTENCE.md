# Phase 3 — SQL Server Persistence and Ofgem Import Foundation

**Document ID:** ARCH-PHASE-3-001  
**Version:** 1.1  
**Status:** Component implementation complete; runtime integration pending  
**Baseline date:** 24 July 2026

## Delivered components

- managed Apache DBCP SQL Server connection pool;
- validated immutable pool configuration and pool snapshot;
- database health check and compatibility connection facade;
- `core` ingestion audit repositories and schema;
- `ofgem` dimensional price-cap schema and reference data;
- Annex 9 `1a Levelised DTC` workbook extractor;
- Ofgem period/fact repository and transactional replacement;
- SQL Server bootstrap, migration and least-privilege grant scripts;
- unit-test and static-compilation foundations.

## Architectural result

The application now has reusable infrastructure for auditable SQL Server loads.
Framework metadata is separated from plugin business data, borrowed JDBC
connections are returned to a managed pool, and Ofgem values retain source-file
and source-cell lineage.

## Remaining integration

The package does not yet provide the final Ofgem plugin orchestration that joins
Phase 2 discovery/download to Phase 3 audit/extract/load and reports one final
application run status. That should be implemented next using constructor
injection and the existing service/repository boundaries.

## Detailed documents

- [Database architecture](014-database-architecture.md)
- [Pooling](019-database-persistence-and-pooling.md)
- [Audit and provenance](020-ingestion-audit-and-provenance.md)
- [Ofgem architecture](021-ofgem-price-cap-architecture.md)
- [Failure and recovery](023-operational-failure-and-recovery.md)
- [Traceability](024-architecture-traceability.md)
