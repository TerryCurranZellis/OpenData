# OpenData Framework

OpenData is a Java 17 command-line framework for acquiring, validating,
transforming and loading public datasets into SQL Server. It is an actively
developed modular monolith with properties-based plugin registration.

## Current capabilities

- Apache Commons CLI supports one plugin, repeated or comma-separated plugin
  selections, `--plugin all`, bounded parallelism, dry runs and registry listing.
- A fixed executor isolates plugin failures while preserving an aggregate result.
- `java.util.logging` records thread, plugin and run identifiers for concurrent
  work.
- Apache DBCP supplies pooled SQL Server connections.
- Ofgem discovers and downloads the current price-cap workbook, extracts annual
  levelised cap values and persists them transactionally.
- OpenMeteo downloads historical daily weather and performs an idempotent,
  location-scoped SQL Server upsert.
- Provider implementations are isolated under `plugin.<id>` with distinct
  config, download, extract, transform/model/validate and load stages.
- Runtime plugin execution is recorded in `core.PluginRun`; the Ofgem domain
  loader also uses the older `core.ingestion_run` provenance model.
- JDK HTTP, Jsoup, Jackson, Apache Commons CSV and Apache POI provide shared
  acquisition and parsing support.

The application has been exercised through dry runs, but production acceptance
still requires a clean SQL Server installation, live write tests, rollback
tests, packaging work and resolution of the two audit models. See the
[current documentation gaps](docs/review/DOCUMENTATION-GAP-ANALYSIS-2026-07-26.md).
The concise hand-off list is the
[unresolved toolchain and specification summary](docs/review/UNRESOLVED-TOOLCHAIN-AND-SPECIFICATION-GAPS-2026-07-26.md).

## Documentation

- [Documentation index](docs/README.md)
- [User guide](docs/user-guide/README.md)
- [Architecture manual](docs/architecture/ARCHITECTURE.md)
- [Operations documentation](docs/operations/README.md)
- [Developer documentation](docs/development/README.md)
- [ADR register](docs/decisions/ADR-REGISTER.md)
- [Command-line reference](docs/reference/command-line-reference.md)
- [Adding a plugin](docs/guides/adding-a-plugin.md)
- [Java plugin template](docs/templates/plugin-java/README.md)

## Build and test

```powershell
mvn clean test
mvn package
```

Maven may run on a later JDK, but compilation targets Java 17 through
`maven.compiler.release=17`.

The current POM creates a library JAR without a `Main-Class` or bundled
dependencies. Run `com.towermarsh.opendata.Main` from NetBeans or another
classpath-aware launcher until executable packaging is configured. Do not use
`java -jar target/opendata-1.0.0.jar` as a documented production command yet.

## Configuration

The runtime reads:

```text
src/main/resources/config/application.properties
src/main/resources/config/plugins/index.properties
src/main/resources/config/plugins/<plugin-id>.properties
```

An external `--file` may override application and plugin values. Multi-plugin
files must scope plugin keys as `plugin.<id>.<key>`. The legacy
`src/main/resources/application.properties` file is not read by the current
runtime and should not be used as a configuration source.

Secrets must not be committed. The classpath database password is a known
hardening gap and must be replaced by an external secret before a real
deployment.

## Licence

OpenData is licensed under the [Apache License 2.0](LICENSE.md).
