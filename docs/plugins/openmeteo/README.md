# OpenMeteo Historical Weather Plugin

**Document ID:** PLUGIN-OPENMETEO-INDEX-001
**Version:** 2.1
**Status:** Runtime and shared processing integration implemented; live acceptance pending
**Baseline date:** 4 August 2026

---

**Plugin id:** `openmeteo`
**Implementation:** `com.towermarsh.opendata.plugin.openmeteo.OpenMeteoPlugin`
**Configuration version:** `2`
**Dataset id:** `openmeteo-daily-weather`

## Pipeline

1. `OpenMeteoConfiguration` resolves and validates typed properties.
2. `OpenMeteoDownloader` requests the archive API.
3. response extraction and validation verify aligned daily arrays.
4. transformation creates immutable `DailyWeatherRecord` values.
5. dry run returns read/skipped metrics; write mode calls `OpenMeteoRepository`.
6. finalisation reports metrics.

## Shared configuration and identifiers

The configuration uses `PluginPropertyValues`, `ValidationRules` and
`SqlIdentifiers`. Configurable schema/table names are validated before they are
placed into SQL text. The deprecated
`OpenMeteoConfiguration.sqlIdentifier(...)` delegate exists only for source
compatibility; new code must use `SqlIdentifiers`.

## Date resolution

- blank `start-date` -> `2000-01-01`;
- blank `end-date` -> yesterday in the configured timezone;
- explicit dates are ISO `yyyy-MM-dd` and must be ordered.

`default-start-days-ago` and `include-current-date` remain present but are not
used by the current date resolution.

## Persistence and connection cleanup

`OpenMeteoRepository` uses one `JdbcTransactionTemplate` transaction. It takes a
location-scoped SQL Server application lock, upserts the location, creates
`#OpenMeteoDaily`, stages records through `JdbcBatchExecutor`, updates changed
rows, inserts missing rows and reports unchanged rows as skipped.

A cleanup callback drops the temporary table and restores `XACT_ABORT` before
the pooled connection is returned. Shared transaction code restores the
original auto-commit value.

## Dry run

```text
opendata --plugin openmeteo --dry-run
```

The API call, parsing, validation and transformation still run. Provider data is
not written. Database-backed configuration may still require SQL Server during
application startup.

See [architecture](../../architecture/026-openmeteo-historical-weather-architecture.md),
[plugin reference](../../reference/openmeteo-plugin.md) and
[schema reference](../../reference/openmeteo-schema.md).
