# OpenData Plugins

This Maven aggregator contains one project for each provider plugin:

| Project | Plugin |
|---|---|
| `opendata-plugin-electric-demand` | Electric Demand |
| `opendata-plugin-electric-supply` | Electric Supply |
| `opendata-plugin-octopus` | Octopus |
| `opendata-plugin-octopus-adjustment` | Octopus Adjustment |
| `opendata-plugin-ofgem` | Ofgem |
| `opendata-plugin-openmeteo` | OpenMeteo |

`opendata-plugin-electricity-common` and `opendata-plugin-octopus-common`
contain code shared by their corresponding providers. Each provider project
owns its Java sources, tests, packaged definition, classpath index, and SQL
registration script. The application includes the six providers through
runtime dependencies declared by `opendata-core`.

Build all projects from the repository root with `mvn clean verify`. Build one
provider and its required reactor dependencies with, for example:

```shell
mvn -pl opendata-plugins/opendata-plugin-openmeteo -am verify
```
