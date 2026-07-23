# ADR-0028: Introduce the OpenMeteo historical weather plugin

- Status: Pending
- Date: 2026-07-23
- Decision owners: OpenData maintainers

## Context

Existing Java code downloads daily historical weather observations from the
Open-Meteo archive API. It currently uses hard-coded location, endpoint,
timezone, and timeout settings and uses `org.json` outside the dependencies
already selected for OpenData.

The functionality should become a dataset plugin and follow the framework's
properties-based configuration model.

## Proposed decision

Introduce the plugin identifier `openmeteo` with implementation class:

```text
com.towermarsh.opendata.plugin.openmeteo.OpenMeteoPlugin
```

Move endpoint, location, timezone, date-range, and timeout values into
`config/plugins/openmeteo.properties`.

Use:

- the JDK HTTP client;
- Jackson Databind for JSON;
- immutable Java records;
- `java.util.logging`;
- WMO weather-code descriptions;
- an inclusive configurable date range.

No API credential is required for the public Open-Meteo archive endpoint.

## Consequences

### Positive

- Weather acquisition follows the same configuration model as other plugins.
- Locations and date ranges can be overridden without recompilation.
- No additional JSON library is introduced.
- Parsing and date resolution are unit-testable.
- The plugin can later feed the common ETL and repository layers.

### Negative or limiting

- Final registration depends on completion of the framework plugin contract.
- Historical data can be revised by the upstream provider.
- Very large date ranges may need batching in a future revision.
- Persistence design for weather records is not included in this decision.

## Implementation status

Pending integration.

The code package is prepared, but activation in the main runtime should wait
until the current application and plugin orchestration processes are complete.
