# OpenData

OpenData is a Java 24+ plugin-oriented data-processing framework for acquiring,
validating, transforming and loading external data into SQL Server. Version
3.0.0 adds the supported JavaFX graphical interface while retaining the full
command-line interface.

## Version 3.0.0 baseline

- JavaFX desktop interface with startup splash, plugin table, registration,
  enable/disable/unregister, plugin details, settings, log viewing, Help and
  About.
- GUI Execute and Dry-run actions with confirmation, background execution and a
  live scrolling JUL log window.
- Command-line execution and administration using Apache Commons CLI.
- Persistent SQL Server plugin registry and database-backed configuration.
- Bounded parallel plugin execution with contextual `java.util.logging` output.
- Ofgem price-cap, Open-Meteo historical weather and Octopus Energy statement
  plugins.
- Manifest-driven technical, administrator, developer and API documentation.

## Requirements

- **Minimum supported JDK:** 24
- **Current development JDK:** 26
- **Current development IDE:** Apache NetBeans 31
- **Build tool:** Maven 3.9 or later
- **Database:** SQL Server
- **GUI toolkit:** JavaFX 26.0.1

A later JDK may be used for development, but release verification must continue
to test the minimum supported Java 24 baseline.

## Build

```powershell
mvn clean verify
mvn package
```

The Maven compiler is configured with `release=24`.

## Start the graphical interface

Running OpenData with no arguments starts the GUI:

```powershell
java -jar <opendata-artifact>.jar
```

The explicit GUI options are also supported:

```text
opendata --gui
opendata -g
```

The JavaFX startup path is:

```text
OpenData.main
    -> GuiLauncher
    -> OpenDataGuiApplication
    -> OpenDataSplashScreen
    -> OpenDataMainView.fxml / OpenDataMainController
```

## Command-line examples

```text
opendata --list-plugins
opendata --plugin ofgem --detail
opendata --plugin ofgem --execute
opendata --plugin openmeteo --dry-run
opendata --plugin all --dry-run --parallelism 3
```

`--plugin` selects a plugin; normal execution requires `--execute` (`-x`).
`--dry-run` (`-n`) performs the non-writing execution path.

## Included plugins

| Plugin | Purpose |
|---|---|
| `ofgem` | Imports UK energy price-cap workbook data |
| `openmeteo` | Imports historical weather data from Open-Meteo |
| `octopus` | Extracts and loads local Octopus Energy statement PDFs |

## Documentation

Start with the [documentation index](docs/DOCUMENTATION-INDEX.md).

- [Technical User Guide sources](docs/user-guide/README.md)
- [Graphical Interface User Guide](docs/user-guide/12-graphical-interface.md)
- [Administrator documentation](docs/operations/README.md)
- [Developer documentation](docs/development/README.md)
- [API and configuration reference](docs/reference/README.md)
- [Version 3.0.0 release record](docs/release/Release-3.0.0.md)
- [Third-party notices](THIRD-PARTY-NOTICES.md)

## Licensing

OpenData is licensed under the Apache License, Version 2.0. Third-party software
retains its own licences and notices; see `THIRD-PARTY-NOTICES.md` and
`DATA-SOURCE-NOTICES.md`.
