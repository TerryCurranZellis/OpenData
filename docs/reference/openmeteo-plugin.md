# OpenMeteo Plugin Reference

**Document ID:** REF-PLUGIN-OPENMETEO-001
**Version:** 2.0
**Status:** Version 2.0.0 implementation reference
**Baseline date:** 3 August 2026

---

| Item | Value |
|---|---|
| Plugin id | `openmeteo` |
| Implementation class | `com.towermarsh.opendata.plugin.openmeteo.OpenMeteoPlugin` |
| Dataset id | `openmeteo-daily-weather` |
| Endpoint name | `archive` |
| Source format | JSON |
| Persistence | Location/date idempotent upsert |

## Active class flow

```text
OpenMeteoPlugin
 -> initialise.OpenMeteoInitialise
 -> extract.OpenMeteoExtract / OpenMeteoDownloader
 -> transform.OpenMeteoResponseExtractor
 -> transform.validate.OpenMeteoResponseValidator
 -> transform.OpenMeteoTransformer
 -> load.OpenMeteoLoad / OpenMeteoRepository
 -> finalise.OpenMeteoFinalise
```

## API query

The plugin sends latitude, longitude, inclusive start/end dates, timezone and the
following daily variables: maximum/minimum/mean temperature, sunrise, sunset,
daylight duration and weather code.

The public archive endpoint does not require an API key in the current design.

## Date behavior

Blank start resolves to `2000-01-01`; blank end resolves to yesterday in the
configured timezone. `default-start-days-ago` and `include-current-date` are
currently parsed but not applied by `resolveDateRange`.

## Persistence

A stable `location-key` identifies `openmeteo.Location`. Daily rows are keyed by
location/date and linked to `core.PluginRun`. The repository uses a SQL Server
application lock for the location, staging and one transaction. Inserted,
changed and unchanged rows become inserted, updated and skipped metrics.

## Dry run

The plugin performs the API request and all validation/transformation, then
returns read/skipped metrics without plugin database access. Database-backed
configuration can still require SQL Server during application startup.

See [plugin documentation](../plugins/openmeteo/README.md) and
[schema reference](openmeteo-schema.md).
