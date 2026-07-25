# OpenMeteo Plugin Reference

**Document ID:** REF-PLUGIN-OPENMETEO-001  
**Version:** 2.0  
**Status:** Implemented  
**Baseline date:** 24 July 2026  
**Minimum Java version:** 17

The `openmeteo` plugin calls the Open-Meteo historical archive endpoint and requests daily minimum, maximum and mean 2 m temperature, sunrise, sunset, daylight duration and WMO weather code. Jackson maps the JSON response to immutable records.

The plugin resolves dates in the configured IANA timezone. When `end-date` is blank it uses yesterday unless `include-current-date=true`. When `start-date` is blank it uses `default-start-days-ago`.

A dry run performs HTTP acquisition, parsing and validation but performs no database access. A normal run upserts `openmeteo.Location` and merges `openmeteo.DailyWeather` in one repository-owned transaction.

![OpenMeteo Java data model](../diagrams/generated/openmeteo-data-model.svg)

![OpenMeteo persistence sequence](../diagrams/generated/openmeteo-persistence.svg)

See [OpenMeteo plugin guide](../plugins/openmeteo/README.md) and [OpenMeteo schema](openmeteo-schema.md).
