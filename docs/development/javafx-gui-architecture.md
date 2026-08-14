# JavaFX GUI Architecture

**Document ID:** DEV-GUI-001
**Version:** 3.1.0
**Status:** Implementation in progress
**Baseline date:** 14 August 2026
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

Batch 1 originally used sample plugin rows to establish the presentation contract.
Batch 3 replaces those fixtures with persistent registry and run-audit data.

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

### Batch 3: persistent plugin table

Batch 3 introduces the first backend integration while preserving the GUI/service
boundary:

```text
OpenDataMainController
        |
        | JavaFX Task
        v
PluginTableDataLoader
        |
        +--> JdbcPluginRegistry
        |
        +--> PluginTableDataService --> core.PluginRun
        |
        v
PluginTableEntry
        |
        v
PluginRow
```

`PluginTableDataLoader` resolves the existing encrypted bootstrap configuration,
opens the SQL Server resource for one read operation and closes it afterwards.
`PluginTableDataService` obtains registered plugin metadata through the existing
`PluginRegistry` contract and performs one read-only query for the latest run
audit of each plugin.

The controller starts this work in a JavaFX `Task`; SQL Server and configuration
I/O do not execute on the JavaFX application thread. The task returns plain
`PluginTableEntry` records. Only the success handler converts those records to
JavaFX `PluginRow` properties. See ADR-0053.

The lower-left status is `Loading plugin details...` while the task runs and
`Ready` after successful population. A failed load is logged, leaves the table
disabled and reports `Unable to load plugin details`.

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

The controller remains free of SQL and processing logic. Batch 3 adds a read-only
backend service boundary; later state-changing batches must follow the same
dependency direction.

## Swing retirement

New GUI code must use JavaFX. The following legacy Swing helpers are deprecated
from version 3.1.0 with removal planned after JavaFX replacements exist:

- `com.towermarsh.opendata.ui.StartupSplashScreen`;
- `com.towermarsh.opendata.ui.AboutDialog`;
- `com.towermarsh.opendata.ui.OpenDataImageLoader`; and
- the compatibility `com.towermarsh.opendata.ui.GuiLauncher`.

The JavaFX GUI no longer uses the Swing splash or Swing About dialog. Batch 5
also moves the standalone `--about` route to JavaFX. The deprecated Swing
execution splash remains temporarily on the legacy command-line run path; the
old Swing About dialog, image helper and compatibility launcher are marked for
removal.

## Remaining implementation batches

| Batch | Scope | Main hurdle |
|---|---|---|
| 3 | Read-only plugin registry view and refresh | **Implemented:** persistent registry plus latest run audit loaded asynchronously behind a GUI service boundary |
| 4 | Register, register-from-file, enable, disable and unregister actions | **Implemented:** configuration-folder discovery, file chooser, selection validation, confirmations and asynchronous registry writes |
| 5 | Plugin Detail, Settings/Preferences, log viewer, Help and JavaFX About | **Implemented:** read-only asynchronous information services, sensitive-value masking and JavaFX About |
| 6 | Execute and Dry-run with live log dialog | **Implemented:** background execution, two-phase database lifecycle and scoped/batched JavaFX JUL streaming |
| 7 | Integration tests, error handling, packaging and final documentation/screenshots | JavaFX test strategy, Windows packaging, help-file launch and release-quality documentation |


### Batch 4 administration boundary

`PluginAdministrationGateway` owns short-lived bootstrap/database resources for
GUI administration. The controller snapshots selections or discovered files and
starts a JavaFX `Task`; it does not call JDBC directly. `PluginRegistrationResolver`
shares plugin-definition parsing and implementation-class validation with the
CLI while leaving the two user-interface workflows distinct.

The GUI Register action uses `PluginConfigurationDirectoryScanner` rather than
the packaged plugin index. It checks deployment-style `config/plugins` first and
the source-tree `src/main/resources/config/plugins` folder second. Validated
definitions already present in `JdbcPluginRegistry` are filtered out before the
confirmation dialog. Register from File bypasses discovery and validates the
file chosen by JavaFX `FileChooser`.

## Batch 5 information boundary

Batch 5 keeps the same controller/service direction established in Batch 3.
`PluginDetailGateway` and `ApplicationSettingsGateway` own short-lived bootstrap
and database resources and return immutable `ConfigurationDisplayEntry` values.
The controller starts those reads on JavaFX `Task`s and opens the dialogs only
after the values have returned to the JavaFX application thread.

Plugin Detail reads `JdbcConfigurationPropertiesSource`, matching the existing
CLI detail source. `ConfigurationDisplayMasker` hides explicitly sensitive
plugin values and conventional credential-bearing names before the data reaches
the dialog. Application Settings is intentionally read-only and never returns a
decrypted database password to the presentation layer.

`LogViewerService` reads the current JUL file rather than attaching a live UI
handler. `LoggingManager.flush()` makes buffered messages visible while leaving
all handlers open. Batch 6 keeps that existing-log viewer separate from the
scoped `JavaFxLogHandler` used only while Execute or Dry-run is active.

`OpenDataInformationDialogs` owns the reusable Property/Value table, text viewer
and JavaFX About presentation. `OpenDataAboutApplication` supplies the standalone
`--about` JavaFX lifecycle without requiring the main application window.

## Batch 6 execution boundary

Execute and Dry-run preserve the existing controller/service direction:

```text
OpenDataMainController
        |
        | snapshot checked ids + confirmation
        | JavaFX Task
        v
PluginExecutionGateway
        |
        +--> PluginSelectionResolver
        +--> PropertiesPluginDefinitionLoader
        +--> PluginExecutionCoordinator
        +--> runtime database / dry-run resource
```

The gateway follows the same two-phase SQL Server lifecycle as CLI execution.
It first opens the bootstrap database to resolve the persistent registry,
runtime configuration and plugin definitions. That pool is closed before a
normal run opens the runtime execution pool. Dry-run instead supplies
`UnavailableDatabaseResourceManager` and `NoOpPluginRunAudit`.

The controller snapshots checked plugin ids before confirmation so later table
interaction cannot mutate an in-flight request. Administration, information and
execution background operations are prevented from overlapping where they could
compete for the singleton SQL Server resource.

### Scoped live JUL handler

`JavaFxLogHandler` is attached temporarily to the
`com.towermarsh.opendata` application logger. `LoggingManager.configure(...)`
replaces only root handlers, so runtime log reconfiguration cannot detach the
live handler. The normal root file and console handlers still receive the same
records through JUL propagation.

Plugin threads may log concurrently. The live handler formats records with
`ContextualLogFormatter`, queues them safely and schedules batched drains via
`Platform.runLater()`. `OpenDataExecutionWindow` owns the modal scrollable text
area. Its Close button is disabled and close requests are consumed until the
background task reaches a terminal state.

After completion the controller detaches the handler and refreshes the plugin
table. Normal execution audit values then become visible; dry-run intentionally
leaves those persisted last-run columns unchanged.

## Integration hurdles still ahead

Batch 7 remains responsible for final JavaFX/integration test coverage, Windows
compiled Help launch/packaging and release-quality screenshots/documentation.

## Batch 3 non-goals

Batch 3 does not:

- register, enable, disable or unregister plugins;
- execute or dry-run plugins;
- replace the legacy Swing About dialog;
- display plugin configuration details or log files;
- add common warning/confirmation dialogs; or
- change CLI plugin processing behaviour.
