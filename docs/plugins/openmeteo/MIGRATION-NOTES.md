# Migration from `WeatherApiClient`

## Changes

| Existing code | OpenMeto plugin |
|---|---|
| `com.towermarsh.energy.weather` | `com.towermarsh.opendata.plugin.openmeto` |
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
| No plugin facade | `OpenMetoPlugin` |

## Existing model

The supplied code referred to `DailyTemperatureRecord`, but that record was not
included in the request. The package therefore supplies `DailyWeatherRecord`
with all fields visible in the constructor call plus coordinates.

If the repository already contains a preferred weather record, either replace
`DailyWeatherRecord` or add a mapper at the plugin boundary.
