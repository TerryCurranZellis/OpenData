# Historical Migration from `WeatherApiClient`

**Document ID:** PLUGIN-OPENMETEO-MIGRATION-001  
**Version:** 3.0.0  
**Status:** Historical; migration completed  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 17

---

## Changes

| Earlier code | Current OpenMeteo plugin |
|---|---|
| `com.towermarsh.energy.weather` | `com.towermarsh.opendata.plugin.openmeteo` |
| Hard-coded endpoint | Typed endpoint property |
| Hard-coded coordinates | Plugin properties |
| Hard-coded timezone | Plugin property |
| Hard-coded 30-second timeout | Separate connection/request properties |
| `org.json.JSONObject` | Existing Jackson dependency |
| Incomplete weather-code mapping | Complete Open-Meteo WMO mapping |
| Mutable `ArrayList` returned | Immutable copied list |
| `IOException` and `InterruptedException` leak | Plugin-specific checked exception |
| No response-array validation | Array lengths checked |
| Request string concatenation | Encoded URI query |
| No plugin facade | `OpenMeteoPlugin` |

## Result

The implemented `DailyWeatherRecord` contains the observation date, configured
location name and coordinates, temperatures, sunrise/sunset, daylight duration,
WMO code and description. `OpenMeteoPlugin` is registered and persists those
records through `load.OpenMeteoRepository`.

The current implementation also separates raw download, JSON extraction,
cross-array validation, transformation/model and transactional load into
provider-local packages.

This file remains only to explain the origin of the current package; it is not
an implementation plan.
