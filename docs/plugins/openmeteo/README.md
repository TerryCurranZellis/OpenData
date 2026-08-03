# OpenMeteo Historical Weather Plugin

**Document ID:** PLUGIN-OPENMETEO-INDEX-001
**Version:** 2.0
**Status:** Runtime and persistence implemented; live acceptance pending
**Baseline date:** 3 August 2026

---

**Plugin id:** `openmeteo`
**Implementation:** `com.towermarsh.opendata.plugin.openmeteo.OpenMeteoPlugin`
**Configuration version:** `2`
**Dataset id:** `openmeteo-daily-weather`

## Active processing flow

1. `initialise.OpenMeteoConfiguration` validates the endpoint, location,
   coordinates, timezone, timeouts and SQL identifiers.
2. `extract.OpenMeteoDownloader` resolves the date range and calls the archive
   API with the configured latitude, longitude and IANA timezone.
3. `transform.OpenMeteoResponseExtractor` parses raw JSON.
4. `transform.validate.OpenMeteoResponseValidator` verifies that daily arrays
   are aligned and valid.
5. `transform.OpenMeteoTransformer` creates immutable
   `transform.model.DailyWeatherRecord` values.
6. `load.OpenMeteoLoad` returns read/skipped metrics during a dry run or invokes
   `load.OpenMeteoRepository` during a write run.
7. `finalise.OpenMeteoFinalise` reports final metrics.

The active path is the one imported by `initialise.OpenMeteoInitialise`. Older
similarly named classes in `config`, `download` and parts of `extract` are
compatibility code, not an additional runtime flow.

## Requested daily variables

The current downloader requests:

- maximum, minimum and mean temperature at 2 metres;
- sunrise and sunset;
- daylight duration;
- WMO weather code.

It does not currently request precipitation or apparent temperature.

## Date resolution

The effective range is inclusive:

- blank `start-date` -> `2000-01-01`;
- blank `end-date` -> current date in the configured timezone minus one day;
- explicit dates must use ISO `yyyy-MM-dd` and start must not be after end.

`default-start-days-ago` and `include-current-date` are still present in the
configuration record and properties file, but the current
`resolveDateRange` implementation does not use them. They must not be described
as active fallback controls until the Java implementation changes.

## Storage and idempotency

- `openmeteo.Location` stores the stable location key, display name,
  coordinates and timezone.
- `openmeteo.DailyWeather` is keyed by location and observation date.
- `LastRunId` links inserted or changed rows to `core.PluginRun`.

The repository uses one transaction and a location-scoped SQL Server application
lock. It stages the requested data, inserts missing dates, updates changed rows
and reports unchanged rows as skipped. It restores connection/session state
before returning the connection to the pool.

Changing `location-name` for the same `location-key` renames the location rather
than creating a second location. Changing coordinates for the same key updates
the same location identity.

## Dry run

```text
opendata --plugin openmeteo --dry-run
```

The plugin calls the API, parses, validates and transforms all records, then
returns them as read/skipped without requesting a plugin database connection.
When database-backed configuration is enabled, SQL Server is still needed at
application startup to read configuration.

## Configuration summary

| Property | Packaged value/default | Current meaning |
|---|---:|---|
| `location-key` | `home` | Stable database key |
| `location-name` | `Home` | Display name |
| `latitude` | `51.674304` | Decimal latitude |
| `longitude` | `-0.785602` | Decimal longitude |
| `timezone` | `Europe/London` | Query and date-resolution timezone |
| `start-date` | `2000-01-01` | Inclusive start; blank also resolves to 2000-01-01 |
| `end-date` | blank | Inclusive end; blank resolves to yesterday |
| `default-start-days-ago` | `365` | Present but not used by current date resolution |
| `include-current-date` | `false` | Present but not used by current date resolution |
| `connect-timeout-seconds` | `30` | HTTP connection timeout |
| `request-timeout-seconds` | `60` | Complete request timeout |
| `database.target-schema` | `openmeteo` | Validated target schema |
| `database.location-table` | `Location` | Validated location table |
| `database.daily-table` | `DailyWeather` | Validated daily table |
| `database.batch-size` | `500` | JDBC staging batch size, 1-10000 |
| `database.lock-timeout-seconds` | `30` | Location lock wait |

See [plugin reference](../../reference/openmeteo-plugin.md) and
[schema reference](../../reference/openmeteo-schema.md).
