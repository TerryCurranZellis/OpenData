# OpenMeteo plugin

**Plugin id:** `openmeteo`  
**Implementation:** `com.towermarsh.opendata.plugin.openmeteo.OpenMeteoPlugin`  
**Configuration version:** 2

## Processing

1. Resolve the effective date range in the configured timezone.
2. Call the Open-Meteo archive API for daily minimum, maximum and mean 2 m temperature, sunrise, sunset, daylight duration and WMO weather code.
3. Parse and validate aligned daily arrays.
4. Convert the response into immutable `DailyWeatherRecord` values.
5. In a dry run, report every record as read/skipped and perform no database access.
6. In a normal run, persist all records in one SQL Server transaction.
7. Before returning the physical SQL Server session to the pool, remove the local staging table and restore session-level `SET` state.

## Storage

- `openmeteo.Location` stores the stable `location-key`, display name, coordinates and timezone.
- `openmeteo.DailyWeather` is keyed by location and observation date.
- `LastRunId` links each inserted or changed daily row to `core.PluginRun`.

Renaming `location-name` does not create a second location when `location-key` remains unchanged. Changing coordinates for the same key updates the location definition and future loads continue under the same key.

## Idempotency

Repeating an identical range produces zero inserted and zero updated rows. Rows whose weather values changed are updated; dates not already stored are inserted. Unchanged rows are reported as skipped.

## Configuration

| Property | Default | Meaning |
|---|---:|---|
| `location-key` | `home` | Stable database key |
| `location-name` | `Home` | Display name |
| `latitude` | `51.674304` | Decimal latitude |
| `longitude` | `-0.785602` | Decimal longitude |
| `timezone` | `Europe/London` | Open-Meteo timezone |
| `start-date` | `2000-01-01` | Inclusive ISO date |
| `end-date` | blank | Yesterday unless current date is enabled |
| `default-start-days-ago` | `365` | Relative fallback |
| `include-current-date` | `false` | Permit today as the end date |
| `connect-timeout-seconds` | `30` | HTTP connection timeout |
| `request-timeout-seconds` | `60` | Complete request timeout |
| `database.target-schema` | `openmeteo` | Target schema |
| `database.location-table` | `Location` | Location table |
| `database.daily-table` | `DailyWeather` | Daily table |
| `database.batch-size` | `500` | JDBC staging batch |
| `database.lock-timeout-seconds` | `30` | Same-location lock wait |

## Example

```text
opendata --plugin openmeteo --file C:\OpenData\local.properties
```

```properties
application.database.password=...
property.start-date.value=2025-01-01
property.end-date.value=2025-12-31
```

For a multi-plugin run, prefix the plugin values:

```properties
plugin.openmeteo.property.start-date.value=2025-01-01
```

## Source API

Open-Meteo historical weather API: https://open-meteo.com/en/docs/historical-weather-api
