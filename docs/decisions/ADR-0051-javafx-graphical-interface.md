# ADR-0051: Use JavaFX for the OpenData graphical interface

**Status:** Accepted  
**Date:** 12 August 2026

## Context

OpenData version 2 is primarily operated through Apache Commons CLI. Version
3.0.0 introduces a Windows desktop interface while retaining the existing CLI.
The GUI must reuse the existing application, configuration, plugin registry,
execution and logging services rather than duplicate processing behaviour.

The GUI specification requires an FXML-defined main window, toolbar and menus,
a selectable plugin table, confirmation dialogs, background execution with live
logging, plugin detail and log viewers, a JavaFX splash screen and an About
window.

## Decision

Use JavaFX for the version 3 graphical interface and keep JavaFX code in the
`com.towermarsh.opendata.gui` package tree.

The main window and reusable dialogs are defined primarily in FXML. Controllers
own presentation logic and delegate application operations to existing OpenData
services through explicit GUI-facing service boundaries introduced in later
implementation batches.

The CLI remains supported. A no-argument invocation and the `--gui`/`-g` option
select the graphical interface.

Long-running plugin operations must not run on the JavaFX application thread.
The execution integration will use JavaFX background tasks and will adapt the
existing `java.util.logging` output for live display rather than adding
GUI-specific logging throughout plugin code.

## Consequences

- Version 3 adds JavaFX controls and FXML as runtime dependencies.
- GUI controllers remain thin and do not implement ETL or database behaviour.
- Existing CLI services become the basis of reusable application services for
  both command-line and graphical operation.
- GUI execution requires explicit background-thread and JavaFX-thread
  boundaries.
- Existing Swing UI helpers are migrated or retired incrementally rather than
  being mixed into the completed JavaFX interface.
- GUI screenshots become maintained documentation assets.

## Related documents

- [ADR register](ADR-REGISTER.md)
- [JavaFX GUI architecture](../development/javafx-gui-architecture.md)
- [GUI screenshot plan](../development/gui-screenshot-plan.md)
- [Graphical interface user guide](../user-guide/12-graphical-interface.md)
- [Version 3 GUI specification](../specifcations/OpenData%20Specifcation%20v3.md)
