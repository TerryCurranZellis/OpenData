# JavaFX GUI Architecture

**Document ID:** DEV-GUI-001  
**Version:** 3.0.0  
**Status:** Implementation in progress  
**Baseline date:** 12 August 2026  
**Minimum Java version:** 17

---

## Purpose

This document records the version 3.0.0 JavaFX implementation structure and the
boundaries that keep the graphical interface separate from OpenData processing
logic.

## Batch 1 main-window baseline

Batch 1 establishes the main JavaFX page without integrating database or plugin
operations. The batch contains:

- `OpenDataGuiApplication` for JavaFX startup and FXML loading;
- `OpenDataMainView.fxml` for the main menu, toolbar, plugin table and status
  bar;
- `OpenDataMainController` for presentation-only event wiring;
- `PluginRow` as the main-table presentation model;
- `opendata-light.css` and toolbar image resources; and
- explicit checkbox selection independent of ordinary table row highlighting.

The sample plugin rows in Batch 1 are display fixtures only. They must be
replaced by registry data when the GUI application-service layer is introduced.

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

GUI code must not directly reproduce SQL, registry, configuration or ETL logic.
The intended dependency direction is:

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

Batch 1 stops at the controller boundary. Later batches introduce the adapter
and application-service layer only where the existing command-oriented classes
are not already suitable for direct reuse.

## Planned implementation batches

| Batch | Scope | Main hurdle |
|---|---|---|
| 1 | Main page, checkbox table, menus, toolbar, status bar, GUI documentation | Establish a stable visual contract without coupling to backend code |
| 2 | GUI bootstrap, Java/JavaFX runtime decision, JavaFX splash/preloader and migration of existing Swing UI helpers | Resolve the JavaFX 26/JDK minimum mismatch and make JavaFX lifecycle coexist cleanly with the current `OpenData.main` lifecycle and logging shutdown |
| 3 | Read-only plugin registry view and refresh | Convert persistent registry/audit data into GUI view models without making the controller a database client |
| 4 | Register, register-from-file, enable, disable and unregister actions | Reuse CLI administration behaviour as services; add file chooser, selection validation and confirmation dialogs |
| 5 | Plugin Detail, Settings/Preferences, existing log viewer, Help and About | Present configuration safely, avoid exposing secrets, and replace remaining Swing dialogs |
| 6 | Execute and Dry-run with live log dialog | Background execution, cancellation/lifecycle rules and a JavaFX `java.util.logging.Handler` must be thread-safe |
| 7 | Integration tests, error handling, packaging and final documentation/screenshots | JavaFX test strategy, Windows packaging, help-file launch and release-quality documentation |

## Java and JavaFX version compatibility

The GUI branch currently sets `maven.compiler.release` to 17 and Maven Enforcer
accepts Java 17 or later, while the POM uses JavaFX 26.0.1. This must be resolved
before the GUI becomes a supported runtime baseline because JavaFX 26 itself
requires JDK 24 or later. Batch 1 does not change either setting.

The version 3 implementation therefore needs one explicit decision before Batch
2: either retain the stated Java 17 minimum and select a JavaFX release that
supports that runtime, or retain JavaFX 26 and raise the application's effective
minimum runtime accordingly.

## Integration hurdles

### Application lifecycle

The current top-level application initialises logging and shuts it down when
`main` returns. A JavaFX application remains active until the primary stage is
closed, so version 3 must make ownership of logging and shared resources
explicit rather than allowing them to be closed while GUI work is still
running.

### Existing Swing UI

`StartupSplashScreen` and the current About implementation are Swing based.
Mixing Swing and JavaFX is unnecessary for the target design, so they should be
replaced in one controlled batch after the main page is stable. The JavaFX API
provides `javafx.application.Preloader` as its application preloader mechanism;
Batch 2 should use that mechanism, or a dedicated JavaFX splash `Stage`, to meet
the specification's five-second splash requirement.

The current `GuiLauncher.java` is also stored under the `ui` source directory
while declaring package `com.towermarsh.opendata`. Batch 2 should move/fix this
launcher as part of the GUI package cleanup rather than carrying the mismatch
forward.

### Service reuse

CLI administration is currently coordinated from application/command classes.
The GUI needs callable operations that return structured results instead of
printing command-oriented output. The refactor must preserve CLI behaviour while
making those operations reusable.

### Background execution and logging

Execute and Dry-run can perform network and database I/O and must run away from
the JavaFX application thread. Live log display should be supplied by a focused
JUL handler that marshals UI updates using `Platform.runLater()`.

### Selection semantics

Version 3 uses explicit row checkboxes. GUI operations must snapshot the checked
plugin IDs before starting a background task so user interaction cannot mutate
an in-flight command unexpectedly.

## Batch 1 non-goals

Batch 1 does not:

- query SQL Server;
- load the persistent plugin registry;
- register, enable, disable or unregister plugins;
- execute or dry-run plugins;
- display real plugin details or log files;
- replace the Swing splash/About components; or
- change CLI behaviour.
