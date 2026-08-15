# Shared Validation and JDBC Infrastructure

**Document ID:** ARCH-028
**Version:** 3.0.0  
**Status:** Implemented in Version 3.0.0
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Purpose

OpenData plugins share configuration and JDBC execution mechanics without
sharing provider-specific business rules or SQL. The Version 2.0.0 refactor
removes repeated text, number, boolean, date, duration, path and SQL-identifier
parsing and centralises transaction, batch and row-level upsert control flow.

The design deliberately does **not** introduce a universal repository, an ORM,
reflection-generated SQL or provider-neutral business keys. Each plugin retains
its own schema knowledge, SQL statements, natural keys, provenance rules and
persistence strategy.

::: {.landscape}
![Shared validation and database persistence components](../diagrams/generated/database-persistence-components.svg){width=22.5cm}
:::

## Shared validation package

The package `com.towermarsh.opendata.validation` provides four complementary
components.

| Component | Responsibility |
|---|---|
| `PluginPropertyValues` | Typed access to values in a resolved `PluginDefinition` |
| `ValidationRules` | Reusable text, range, duration and date-order rules |
| `SqlIdentifiers` | Validation and SQL Server bracket quoting of configured identifiers |
| `ValueParser<T>` | Extension point for a plugin-specific value type |

`PluginPropertyValues` reports the plugin and property names when conversion
fails but does not echo the source value. This prevents a sensitive property
value being copied into logs or exception messages.

The declared `PluginPropertyType` remains useful registration metadata. Runtime
configuration records are still responsible for choosing the required Java
method and applying domain constraints after conversion.

## Shared JDBC package

The package `com.towermarsh.opendata.database.jdbc` contains execution
mechanics that do not know a plugin schema.

| Component | Responsibility |
|---|---|
| `JdbcTransactionTemplate` | Borrow connection, disable auto-commit, commit, roll back and restore state |
| `JdbcConnectionCleanup` | Remove connection-scoped state before a pooled session is returned |
| `JdbcTransaction<T>` | Typed transaction callback |
| `JdbcBatchExecutor` | Execute typed prepared-statement batches and count results consistently |
| `JdbcStatementBinder<T>` | Bind one record to a prepared statement |
| `JdbcUpsertAdapter<T,C>` | Define record-specific existence, insert and update SQL operations |
| `JdbcUpsertExecutor` | Execute the common exists/insert/update loop |
| `JdbcUpsertResult` | Combine inserted and updated counts safely |

Checked failures leaving `JdbcTransactionTemplate` are wrapped in
`DatabaseAccessException`. Runtime exceptions retain their original type.
Rollback and cleanup failures are attached as suppressed exceptions rather than
concealing the primary failure.

## Persistence strategy selection

A plugin selects shared mechanics according to its data and SQL strategy.

| Strategy | Recommended shared component | Plugin remains responsible for |
|---|---|---|
| Direct prepared-statement batch | `JdbcBatchExecutor` | SQL and parameter binding |
| Record-by-record natural-key upsert | `JdbcUpsertAdapter` and `JdbcUpsertExecutor` | natural key, existence SQL, insert SQL and update SQL |
| Temporary staging plus set-based SQL | `JdbcTransactionTemplate` and `JdbcBatchExecutor` | staging table, locks and set-based update/insert statements |
| Complex transactional replacement | `JdbcTransactionTemplate` with optional batch execution | provenance, replacement scope and generated-key processing |

## Current plugin use

### Ofgem

`OfgemConfiguration` uses shared property and validation components.
`OfgemPersistenceRepository` uses `JdbcTransactionTemplate` and
`JdbcBatchExecutor`, while retaining explicit ingestion-run, source-file,
period-replacement and generated-key SQL.

### OpenMeteo

`OpenMeteoConfiguration` uses shared typed conversion, coordinate/range rules
and SQL identifier validation. `OpenMeteoRepository` uses the transaction and
batch components while retaining its SQL Server application lock, temporary
staging table and set-based update/insert strategy. A transaction cleanup hook
removes the temporary table and resets `XACT_ABORT` before the pooled session is
returned.

The former public `OpenMeteoConfiguration.sqlIdentifier` method remains as a
deprecated delegating compatibility method. New code calls
`SqlIdentifiers.requireSafe` or `SqlIdentifiers.qualify` directly.

### Octopus

`OctopusConfiguration` uses shared path conversion. Electricity and gas SQL are
implemented by separate typed adapters because their columns differ. Both use
one `JdbcUpsertExecutor` control flow, and statement-file completion remains in
the same `JdbcTransactionTemplate` transaction.

## Dependency rules

- plugin code may depend on `validation` and `database.jdbc`;
- shared packages must not import a provider package;
- a plugin must not import another provider plugin;
- configuration parsing belongs in the typed configuration record, not in the
  root plugin facade;
- provider-specific SQL remains in the provider's `load` package;
- no shared helper may infer table names, natural keys or column mappings from
  reflection.

## API lifecycle rules

All public APIs introduced or materially adjusted by this Version 2.0.0 change
must carry Javadoc `@since 2.0.0`.

When an obsolete public method must remain for source compatibility, it must
carry both:

```java
@Deprecated(since = "2.0.0", forRemoval = false)
```

and a Javadoc `@deprecated` tag that names the replacement. Private helpers with
no external callers should normally be removed rather than retained as dead
deprecated wrappers.

## Testing requirements

New plugins must test:

- missing, blank, default and invalid typed properties;
- domain ranges and date ordering after conversion;
- safe configured SQL identifiers where identifiers are configurable;
- transaction commit, rollback and original auto-commit restoration;
- batch boundaries and `SUCCESS_NO_INFO` counting where batching is used;
- inserted and updated counts for typed upsert adapters;
- connection cleanup when temporary tables or SQL Server `SET` options are used;
- repeat-load behaviour against a real SQL Server instance.

Mock JDBC tests verify orchestration but do not replace SQL Server integration
and permission tests.
