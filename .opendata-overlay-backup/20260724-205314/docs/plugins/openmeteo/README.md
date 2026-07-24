# OpenMeto Plugin

## Purpose

The `openmeto` plugin downloads historical daily weather data from the
Open-Meteo archive API.

The project name requested for the plugin is **OpenMeto**. The external service
is correctly named **Open-Meteo**.

## Data returned

Each `DailyWeatherRecord` contains:

- observation date;
- configured location name;
- latitude and longitude;
- minimum temperature;
- maximum temperature;
- mean temperature;
- sunrise;
- sunset;
- daylight duration in minutes;
- WMO weather code;
- weather description.

## Configuration

The default plugin definition is:

```text
src/main/resources/config/plugins/openmeto.properties
```

It includes all values that were previously hard-coded in
`WeatherApiClient`, together with date-range and timeout behaviour.

### Date selection

When both date properties are blank:

- the end date defaults to yesterday;
- the start date defaults to 365 days before the end date.

Set explicit ISO dates through an override file:

```properties
property.start-date.value=2024-01-01
property.end-date.value=2024-12-31
```

### Location override

```properties
property.location-name.value=Weather Station 03658
property.latitude.value=51.6207
property.longitude.value=-1.1098
property.timezone.value=Europe/London
```

## Invocation

After the plugin registry/execution path is completed:

```bash
java -jar target/opendata-1.0.0.jar --plugin openmeto
```

With an override file:

```bash
java -jar target/opendata-1.0.0.jar \
  --plugin openmeto \
  --file config/openmeto-override.properties
```

## Integration status

The current OpenData bootstrap still has plugin execution wiring to complete.
`OpenMetoPlugin` is therefore supplied as a facade ready to be registered once
the final framework plugin interface and orchestration process are concluded.

## Dependency changes

No new JSON dependency is required. The plugin uses Jackson Databind, already
declared in the repository Maven build.
