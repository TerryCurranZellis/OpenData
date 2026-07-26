# 7. OpenMeteo Weather Plugin

**Document ID:** USER-007  
**Version:** 1.0  
**Status:** Runtime implemented; acceptance pending  
**Baseline date:** 26 July 2026

---

OpenMeteo downloads daily minimum, maximum and mean temperature, sunrise, sunset,
daylight duration and WMO weather code from the archive API.

Example override:

```properties
application.database.password=<secret>
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
opendata --plugin openmeteo --file C:\OpenData\weather.properties
```

`location-key` is the stable database identity. Renaming `location-name` does not
create another location. Repeating unchanged data should report zero inserted
and updated rows; changed values are updated and new dates inserted.

Rows are stored in `openmeteo.Location` and `openmeteo.DailyWeather`, linked to
the generic `core.PluginRun` UUID.
