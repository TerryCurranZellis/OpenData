# OpenMeteo Plugin Reference

**Document ID:** REF-PLUGIN-OPENMETEO-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


The plugin calls the Historical Weather API `/v1/archive` with configured
latitude, longitude, date range and IANA timezone. It requests the daily
temperature, apparent-temperature, precipitation, weather-code, daylight,
sunrise and sunset fields used by `DailyWeatherRecord`.

Jackson parses the JSON response. Dry run validates the response without
initialising the database. Write mode acquires a location-scoped SQL Server
application lock, upserts `openmeteo.Location` and inserts or updates
`openmeteo.DailyWeather` by location and observation date. Unchanged rows are
skipped. The public endpoint does not require a key.

Provider classes are under `plugin.openmeteo`: raw HTTP in `download`, JSON
mapping in `extract`, records and conversion in `transform`, validation in
`transform.validate`, and SQL in `load`.

Configuration keys and table definitions are documented in the
[configuration reference](../plugins/openmeteo/README.md) and
[schema reference](openmeteo-schema.md).
