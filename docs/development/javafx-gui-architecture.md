# JavaFX GUI Architecture

**Document ID:** DEV-GUI-001  
**Version:** 3.1.0  
**Status:** Implementation in progress  
**Baseline date:** 13 August 2026  
**Minimum Java version:** 24

---

## Purpose

This document records the JavaFX implementation structure and the boundaries
that keep the graphical interface separate from OpenData processing logic.

## Implemented baseline

### Batch 1: main window

Batch 1 established the presentation contract:

- `OpenDataGuiApplication` for JavaFX startup and FXML loading;
- `OpenDataMainView.fxml` for the menu, toolbar, plugin table and status bar;
- `OpenDataMainController` for presentation event wiring;
- `PluginRow` as the table presentation model;
- explicit checkbox selection independent of ordinary table highlighting; and
- CSS and toolbar resources.

The sample plugin rows remain display fixtures until Batch 3 replaces them with
persistent registry data.

### Batch 2: startup and lifecycle

Batch 2 establishes the supported desktop startup path:

```text
OpenData.main
        |
com.towermarsh.opendata.gui.GuiLauncher
        |
Application.launch(OpenDataGuiApplication.class, ...)
        |
OpenDataSplashScreen
        |
OpenDataMainView.fxml
```

The splash is an undecorated JavaFX `Stage`. It uses `PauseTransition` to remain
visible for a minimum of five seconds without sleeping on the JavaFX application
thread. The main stage is configured while the splash is active and is shown
only after the splash closes.

`Application.launch(...)` blocks the calling thread until JavaFX exits. This is
intentional. `OpenData.main` initialises logging before launching JavaFX and its
existing `finally` block runs only after the GUI has closed, so logging remains
available throughout the complete GUI session.

The supported launcher now lives in `com.towermarsh.opendata.gui`. A deprecated
wrapper remains in `com.towermarsh.opendata.ui` so the prototype source location
can be retired without an abrupt compatibility break.

## Java runtime baseline

Version 3.1.0 requires **Java 24 or later**. Development may use a later JDK;
the current development environment uses JDK 25. JavaFX 26.x is retained.

GitHub build and release workflows use Java 24 so automated verification tests
the minimum supported runtime rather than a newer development JDK.

This decision supersedes the old Java 17 runtime minimum. See ADR-0052.

## Main-window contract

The main window is maximised on startup and contains these top-level menus:

| Menu | Commands |
|---|---|
| File | Settings, Exit |
| Register | Register, Register from File, Unregister |
| Enable | Enable, Disable |
| Execute | Execute, Dry-run |
| Details | Plugin Detail, Logs |
| Help | Help, About |

The toolbar exposes the specification commands using 24-pixel image resources.
Menu items and toolbar buttons that represent the same operation call the same
controller handler.

The plugin table contains:

| Column | Purpose |
|---|---|
| Selected | Explicit checkbox indicating inclusion in a later action |
| Plugin ID | Registered plugin identifier |
| Plugin Description | Human-readable plugin description |
| Enabled | Enabled/disabled state |
| Last Run Status | Status of the most recent execution; blank if never run |
| Date of Last Run | Most recent execution date/time; blank if never run |

The lower-left status label is reserved for loading/ready/operation feedback.
The lower-right label counts checked plugin rows.

## Presentation boundary

GUI code must not reproduce SQL, registry, configuration or ETL logic. The
intended dependency direction remains:

```text
JavaFX FXML / controls
        |
OpenDataMainController
        |
GUI application services / adapters
        |
existing OpenData services
        |
configuration, registry, execution, logging and plugins
```

The controller is still presentation-only after Batch 2. Backend integration
starts with the read-only registry view in Batch 3.

## Swing retirement

New GUI code must use JavaFX. The following legacy Swing helpers are deprecated
from version 3.1.0 with removal planned after JavaFX replacements exist:

- `com.towermarsh.opendata.ui.StartupSplashScreen`;
- `com.towermarsh.opendata.ui.AboutDialog`;
- `com.towermarsh.opendata.ui.OpenDataImageLoader`; and
- the compatibility `com.towermarsh.opendata.ui.GuiLauncher`.

The JavaFX GUI no longer uses the Swing splash. The deprecated Swing splash is
retained temporarily for the existing command-line run path, and the Swing About
dialog remains available for the command-line About route until Batch 5.

## Remaining implementation batches

| Batch | Scope | Main hurdle |
|---|---|---|
| 3 | Read-only plugin registry view and refresh | Convert persistent registry/audit data into GUI view models without making the controller a database client |
| 4 | Register, register-from-file, enable, disable and unregister actions | Reuse administration behaviour as services; add file chooser, selection validation and confirmations |
| 5 | Plugin Detail, Settings/Preferences, log viewer, Help and JavaFX About | Present configuration safely and finish Swing UI retirement |
| 6 | Execute and Dry-run with live log dialog | Background execution and a thread-safe JavaFX JUL handler |
| 7 | Integration tests, error handling, packaging and final documentation/screenshots | JavaFX test strategy, Windows packaging, help-file launch and release-quality documentation |

## Integration hurdles still ahead

### Service reuse

CLI administration is coordinated from command/application classes. The GUI
needs callable operations that return structured results instead of printing
command-oriented output. The refactor must preserve CLI behaviour while making
those operations reusable.

### Background execution and logging

Execute and Dry-run perform network and database I/O and must run away from the
JavaFX application thread. Live log display should be supplied by a focused JUL
handler that marshals UI changes using `Platform.runLater()`.

### Selection semantics

GUI operations must snapshot checked plugin IDs before starting a background
task so user interaction cannot mutate an in-flight command unexpectedly.

## Batch 2 non-goals

Batch 2 does not:

- query SQL Server for table contents;
- replace the sample plugin rows;
- register, enable, disable or unregister plugins;
- execute or dry-run plugins;
- replace the legacy Swing About dialog;
- display real plugin details or log files; or
- change plugin processing behaviour.
