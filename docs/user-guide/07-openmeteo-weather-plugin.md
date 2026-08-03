# 7. OpenMeteo Weather Plugin

**Document ID:** USER-007  
**Version:** 2.0  
**Status:** Runtime implemented; acceptance required  
**Baseline date:** 3 August 2026

---

OpenMeteo downloads daily minimum, maximum and mean temperature, sunrise,
sunset, daylight duration and WMO weather code from the archive API.

Register and test:

```text
opendata --plugin openmeteo --register
opendata --plugin openmeteo --dry-run
opendata --plugin openmeteo
```

To change location or dates, copy the complete packaged
`config/plugins/openmeteo.properties` definition, amend values such as these,
and re-register it:

```properties
property.location-key.value=home
property.location-name.value=Home
property.latitude.value=51.674304
property.longitude.value=-0.785602
property.timezone.value=Europe/London
property.start-date.value=2025-01-01
property.end-date.value=2025-12-31
```

```text
opendata --plugin openmeteo --register --file C:\OpenData\openmeteo.properties
```

`location-key` is the stable database identity. Repeating an unchanged date range
should report no inserts or updates; changed values are updated and new dates are
inserted. The active date resolver uses explicit `start-date` and `end-date`.
