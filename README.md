# OpenData Framework

OpenData is a Java framework for acquiring, validating, transforming and loading
public datasets. It is under active development and uses a modular monolith with
properties-based plugin definitions.

## Consolidated Phase 1 baseline

- Java 17 is the minimum supported release and Maven API/bytecode target.
- Apache Commons CLI handles command-line processing.
- `java.util.logging` is the application logging framework.
- Immutable Java records represent structured plugin configuration.
- Each plugin has a properties file and is named in an explicit classpath index.
- Ofgem and OpenMeteo are the reference plugins.
- JDK HTTP supports direct downloads; JSoup resolves links from static HTML.
- Apache Commons CSV, Jackson and Apache POI parse CSV, JSON and Excel.
- Validation, ETL and SQL Server repository foundations are present.
- Markdown, ADRs and PlantUML are maintained with the source code.

Some components remain foundations rather than completed production workflows.
See [the architecture manual](docs/architecture/ARCHITECTURE.md).

## Documentation

- [Documentation index](docs/README.md)
- [Architecture manual](docs/architecture/ARCHITECTURE.md)
- [ADR register](docs/decisions/ADR-REGISTER.md)
- [Command-line reference](docs/reference/command-line-reference.md)
- [Adding a plugin](docs/guides/adding-a-plugin.md)

## Build

```powershell
mvn clean test
mvn package
java -jar target/opendata-1.0.0.jar --list-plugins
java -jar target/opendata-1.0.0.jar --plugin ofgem --dry-run
java -jar target/opendata-1.0.0.jar --plugin openmeteo --dry-run
```

Maven may run on a later JDK, but the compiler must use
`maven.compiler.release=17`.

## Configuration

```text
src/main/resources/config/application.properties
src/main/resources/config/plugins/index.properties
src/main/resources/config/plugins/<plugin-id>.properties
```

Secrets must not be committed. Plugin definitions contain credential references,
not resolved credentials.

## Licence

The repository licence is Apache License 2.0. Source-file headers should be made
consistent with the repository licence.
