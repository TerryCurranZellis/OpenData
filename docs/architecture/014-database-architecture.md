# Database Architecture

**Document ID:** ARCH-014  
**Version:** 1.2  
**Status:** Implemented  
**Baseline date:** 26 July 2026  
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
- `openmeteo` contains locations and daily weather facts;
- later plugins should receive their own schemas rather than adding unrelated
  columns to `core` tables.

This implements ADR-0014. Framework operational metadata can therefore evolve
without forcing all plugin business models into one generic table.

## Java persistence boundary

- `DatabaseResourceManager` supplies pooled `Connection` objects and owns the
  physical resource lifecycle.
- `SQLServerResource` configures and owns an Apache DBCP generic object pool and
  pooling driver.
- current plugin repositories use `DatabaseResourceManager`;
- `DatabaseConnectionManager` and its older repositories remain compatibility
  code and are not the active plugin persistence path;
- repository interfaces express dataset persistence operations;
- SQL Server repository classes own SQL text, parameter binding and transaction
  handling;
- service classes coordinate parsing and repository calls without embedding SQL.

New code uses try-with-resources. Closing a borrowed connection returns it to the
pool; it does not normally close the physical SQL Server session.

## Transaction boundaries

One logical dataset replacement is atomic. For Ofgem, dataset lookup, domain
ingestion/source-file creation, current-period flag updates, period upsert, fact
replacement and domain-audit completion use one repository transaction. Failure
causes rollback and leaves the previously committed state intact.

OpenMeteo uses one transaction for its location update and daily staging/update/
insert sequence, protected by a location-scoped SQL Server application lock.

## Schema management

Numbered SQL scripts are designed for ordered, repeatable execution.
`core.schema_version` records the older Ofgem migration steps. The current
scripts are split between `sql/` and `sql/sqlserver/`; use the documented
combined order until one manifest replaces them. Live repeat-install acceptance
is outstanding.

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
![OpenData database schemas](../diagrams/generated/opendata-database.svg){width=16cm}
