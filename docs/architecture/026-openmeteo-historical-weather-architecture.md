# OpenMeteo Historical Weather Architecture

**Document ID:** ARCH-026
**Version:** 3.0.0  
**Status:** Runtime and shared persistence integration implemented; live acceptance pending
**Baseline date:** 15 August 2026  

---

## Scope

OpenMeteo is the reference plugin for a parameterised public JSON API. One
plugin definition represents one stable location and loads daily historical
observations into a dedicated SQL Server schema.

## Active flow

1. resolve typed location, API, timeout, date and SQL settings;
2. build an archive-API request for configured coordinates and timezone;
3. parse and validate aligned daily JSON arrays;
4. transform arrays into immutable `DailyWeatherRecord` values;
5. return read/skipped metrics during dry run, or persist in one transaction;
6. report final metrics.

## Shared configuration processing

`OpenMeteoConfiguration` uses `PluginPropertyValues` for required text, decimals,
integers, booleans, optional ISO dates and `ZoneId` parsing. `ValidationRules`
validates text lengths, coordinate ranges, positive durations, integer ranges
and date ordering. `SqlIdentifiers` validates and qualifies configurable SQL
Server schema and table names.

The former public `OpenMeteoConfiguration.sqlIdentifier(...)` method remains as
a source-compatibility delegate. It is marked with Java
`@Deprecated(since = "2.0.0", forRemoval = false)`, Javadoc `@deprecated` and
`@since 2.0.0`. New code calls `SqlIdentifiers` directly.

## Date rule

Blank start resolves to `2000-01-01`; blank end resolves to yesterday in the
configured timezone. The retained `default-start-days-ago` and
`include-current-date` properties are not currently applied by
`resolveDateRange`.

## Shared transaction and staging batches

`OpenMeteoRepository` uses:

- `JdbcTransactionTemplate` for transaction ownership and checked-failure
  translation;
- `JdbcBatchExecutor` to populate `#OpenMeteoDaily` using the configured batch
  size; and
- `SqlIdentifiers.qualify(...)` for target table names.

The provider-specific persistence strategy remains:

1. remove a stale connection-local staging table;
2. enable SQL Server `XACT_ABORT`;
3. acquire a location-scoped transaction application lock;
4. upsert the location row;
5. create and batch-populate `#OpenMeteoDaily`;
6. update changed target rows with a set-based statement;
7. insert missing rows with a set-based statement; and
8. report unchanged rows as skipped.

The repository verifies that the number staged matches the number supplied.

## Pooled-session cleanup

Temporary tables and `SET` options can survive a logical pooled-connection
close. OpenMeteo therefore supplies a `JdbcConnectionCleanup` callback to the
transaction template. Before the connection is returned to the pool it:

- drops `#OpenMeteoDaily`;
- turns `XACT_ABORT` off; and
- completes any cleanup transaction state.

Cleanup failure is surfaced, or attached as a suppressed failure when a primary
transaction failure already exists. Auto-commit restoration remains the shared
transaction template's responsibility.

## Concurrency and idempotency

The application lock is scoped by `location-key`. The natural business key is
location plus observation date. Missing dates are inserted, changed dates are
updated and unchanged dates are skipped.

## Verification

Focused tests cover typed configuration, default date resolution, deprecated
compatibility behaviour, batch-size execution, commit, rollback and pooled
session cleanup. Live SQL Server lock, repeat-load and permission acceptance
remain release gates.

::: {.landscape}
![OpenMeteo pipeline and persistence](../diagrams/generated/openmeteo-persistence.svg){width=22.5cm}
:::

![OpenMeteo data model](../diagrams/generated/openmeteo-data-model.svg){width=16cm}
