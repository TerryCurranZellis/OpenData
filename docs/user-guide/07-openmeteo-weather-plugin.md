# 7. OpenMeteo Weather Plugin

**Document ID:** USER-007  
**Version:** 2.0  
**Status:** Runtime implemented; acceptance required  
**Baseline date:** 3 August 2026

---

OpenMeteo downloads daily minimum, maximum and mean temperature, sunrise,
sunset, daylight duration and WMO weather code from the archive API.

Example override:

```properties
application.database.password=<database-password>
property.location-key.value=home
property.location-name.value=Home
property.latitude.value=51.674304
property.longitude.value=-0.785602
property.timezone.value=Europe/London
property.start-date.value=2025-01-01
property.end-date.value=2025-12-31
```

Run:

```text
opendata --plugin openmeteo --dry-run --file C:\OpenData\weather.properties
opendata --plugin openmeteo --file C:\OpenData\weather.properties
```

`location-key` is the stable database identity. Repeating an unchanged date range
should report no inserts or updates; changed values are updated and new dates are
inserted.

The active date resolver uses explicit `start-date` and `end-date`. The packaged
`default-start-days-ago` and `include-current-date` properties are currently not
used by the active implementation, so do not depend on them for scheduling.

Rows are stored in `openmeteo.Location` and `openmeteo.DailyWeather`, linked to
the generic `core.PluginRun` UUID.
