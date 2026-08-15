# OpenData 3.0.0 Release Notes

**Status:** Release candidate documentation baseline  
**Baseline date:** 15 August 2026  
**Minimum supported Java:** 24

## Overview

OpenData 3.0.0 completes the migration from the prototype desktop code to the
supported JavaFX graphical interface while retaining the existing command-line
interface and Version 2 database-backed plugin framework.

The GUI is now the default when OpenData starts with no arguments. Operators can
also request it explicitly with `--gui` or `-g`.

## Graphical interface

Version 3.0.0 provides:

- JavaFX startup splash and maximised main window;
- persistent plugin table populated from the SQL Server registry and latest run
  audit;
- explicit checkbox selection for one or more plugins;
- Register and Register from File workflows;
- Enable, Disable and Unregister operations with confirmation;
- Plugin Detail with sensitive configuration values masked;
- read-only Settings/Preferences;
- current application-log viewer;
- Execute and Dry-run commands running away from the JavaFX application thread;
- a modal live execution-log window whose Close action is disabled until work
  completes;
- JavaFX About presentation;
- Windows compiled HTML Help (`.chm`) when available, with built-in JavaFX help
  as fallback.

The previous Swing presentation package has been removed from the merged
Version 3.0.0 baseline.

## Command-line interface

The CLI remains fully supported. Normal execution requires `--execute` (`-x`);
`--dry-run` (`-n`) authorises the non-writing path. Plugin administration and
information commands do not use `--execute`.

Examples:

```text
opendata --plugin ofgem --execute
opendata --plugin openmeteo --dry-run
opendata --plugin all --dry-run --parallelism 3
opendata --plugin octopus --detail
opendata --list-plugins
```

## Runtime and development baseline

- Minimum supported runtime/build JDK: **24**.
- Current development JDK: **26**.
- Current development IDE: **Apache NetBeans 31**.
- JavaFX: **26.0.1** (`javafx-controls` and `javafx-fxml`).
- Maven Enforcer requires Java 24 or later and Maven 3.9 or later.

Release verification should continue to exercise the minimum Java 24 baseline,
even when day-to-day development uses JDK 26.

## Included plugins

Version 3.0.0 retains the current provider set:

- `ofgem` — UK energy price-cap data;
- `openmeteo` — historical weather data; and
- `octopus` — local Octopus Energy statement PDF processing.

Additional plugins planned after Version 3.0.0 are not part of this release.

## Documentation

The Version 3.0.0 documentation baseline includes the Technical User Guide,
Administrator Guide, Developer Guide and API/configuration reference. The GUI
user guide has been promoted from implementation notes to current operating
guidance.

GUI screenshots are intentionally tracked as release evidence rather than
embedded as broken image links. Capture the required PNG files listed in
`docs/development/gui-screenshot-plan.md`, then add the final publication copies
before release approval.

## Third-party dependency update

The third-party notice inventory has been reconciled with the Version 3.0.0
`pom.xml`, including JavaFX 26.0.1. The notice also corrects the PDFBox version
to 3.0.8 and JUnit Jupiter to 6.1.2 and records Apache NetBeans 31/JDK 26 as the
current development environment.

## Release readiness

Version 3.0.0 should not be tagged until the release checklist is complete. In
particular, retain evidence for:

- `mvn clean verify` on the Java 24 minimum baseline;
- GUI functional acceptance and final screenshot capture;
- compiled Windows Help/fallback behaviour;
- clean SQL installation and plugin acceptance;
- dependency/licence inventory;
- secret/privacy review; and
- final distribution/checksum verification.
