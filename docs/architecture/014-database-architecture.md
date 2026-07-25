# Database Architecture

**Document ID:** ARCH-014  
**Version:** 1.1  
**Status:** Implemented  
**Baseline date:** 24 July 2026  
**Minimum Java version:** 17

---

## Scope

SQL Server is the first persistence target. Database-neutral Java contracts
isolate connection acquisition and repository responsibilities, but do not
promise SQL dialect portability. A second database would require a new resource
manager and repository implementations.

## Logical schemas

The `OpenData` database is divided by responsibility:

- `core` contains framework-owned dataset registration, ingestion runs, source
  files, errors and schema-version history;
- `ofgem` contains Ofgem-owned dimensions, price-cap periods, annual price-cap
  facts and reserved component facts;
- later plugins receive their own schemas rather than adding unrelated columns
  to `core` tables.

This implements ADR-0014. Framework operational metadata can therefore evolve
without forcing all plugin business models into one generic table.

## Java persistence boundary

- `DatabaseResourceManager` supplies pooled `Connection` objects and owns the
  physical resource lifecycle.
- `SQLServerResource` configures and owns Apache DBCP `BasicDataSource`.
- `DatabaseConnectionManager` is the compatibility facade used by repositories.
- repository interfaces express dataset persistence operations;
- SQL Server repository classes own SQL text, parameter binding and transaction
  handling;
- service classes coordinate parsing and repository calls without embedding SQL.

New code uses try-with-resources. Closing a borrowed connection returns it to the
pool; it does not normally close the physical SQL Server session.

## Transaction boundaries

One logical dataset replacement is atomic. For Ofgem, all rows for one
price-cap period are deleted and reinserted within one transaction. Failure
causes rollback and leaves the previously committed period intact.

The period metadata upsert is also transactional. A future refinement may join
period upsert and fact replacement into one repository transaction if the
orchestration requires strict all-or-nothing behaviour across both operations.

## Schema management

Numbered SQL scripts are executed in order. Scripts check for existing schemas,
tables, roles or version rows so that an interrupted installation can be safely
re-run. `core.schema_version` records successful logical migration steps.

The initial approach deliberately avoids adding Flyway or Liquibase. A migration
tool can be adopted later if branching, rollback or multi-environment deployment
complexity justifies it.

## Security

The SQL login and database user are named `OpenData`. The user is a member of
`opendata_app`, which receives operational DML permissions rather than database
owner or schema-alter permissions. Administrative scripts are run separately by
a privileged operator.

See [database persistence and pooling](019-database-persistence-and-pooling.md),
[security and credentials](017-security-and-credentials.md) and
[the database ER diagram](../diagrams/database/opendata-database.puml).
