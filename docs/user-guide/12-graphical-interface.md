# Graphical Interface User Guide

**Document ID:** USER-GUI-001  
**Version:** 3.0.0  
**Status:** Current merged GUI baseline  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## 1. Overview

OpenData 3.0.0 provides a JavaFX desktop interface for routine plugin operation
and administration. The command-line interface remains available for scripting,
automation and advanced use.

Running OpenData with no arguments starts the GUI. The same interface can be
requested explicitly with either:

```text
opendata --gui
opendata -g
```

The current development environment uses JDK 26 and Apache NetBeans 31. The
minimum supported OpenData runtime remains Java 24.

## 2. Starting OpenData

The GUI startup sequence is:

```text
OpenData.main
    -> GuiLauncher
    -> OpenDataGuiApplication
    -> OpenDataSplashScreen
    -> OpenDataMainView.fxml / OpenDataMainController
```

A JavaFX splash screen appears first and remains visible for at least five
seconds. The main window is prepared while the splash is displayed and then
opens maximised.

**Screenshot to capture:** `gui-splash-screen.png` — the complete startup splash,
centred on the desktop without cropping the OpenData artwork.

## 3. Main window

The main window contains:

1. a menu bar;
2. a toolbar with 24-pixel action icons;
3. the plugin table; and
4. a status bar.

The menus are:

| Menu | Commands |
|---|---|
| File | Settings, Exit |
| Register | Register, Register from File, Unregister |
| Enable | Enable, Disable |
| Execute | Execute, Dry-run |
| Details | Plugin Detail, Logs |
| Help | Help, About |

The toolbar provides quick access to Exit, Settings/Preferences, Save, Register,
Unregister, Enable, Disable, Execute, Dry-run, Logfile, About and Help. Menu and
toolbar actions for the same operation use the same controller action.

**Screenshot to capture:** `gui-main-window.png` — full maximised window after the
plugin table has loaded, with the status showing `Ready`, several real plugin
rows visible, and at least one plugin selected if practical.

## 4. Plugin table and selection

The table displays persistent data from the OpenData plugin registry and latest
plugin-run audit:

| Column | Meaning |
|---|---|
| Selected | Checkbox indicating whether the plugin is included in the next action |
| Plugin ID | Registered plugin identifier |
| Plugin Description | Human-readable description |
| Enabled | Whether normal execution is permitted |
| Last Run Status | Most recent persisted execution status; blank if never run |
| Date of Last Run | Most recent persisted run date/time; blank if never run |

Select plugins using the **Selected** checkbox. Ordinary row highlighting is not
the action-selection mechanism. The lower-right status label reports the number
of checked rows.

While plugin information is loading, the lower-left status reports
`Loading plugin details...`. After a successful load it reports `Ready`. When no
plugins are registered, the table displays `No plugins registered`.

If the registry/configuration read fails, the table reports that plugin details
could not be loaded, the status becomes `Unable to load plugin details`, and the
failure is also written to the normal OpenData log.

## 5. Registering plugins

### 5.1 Register discovered plugin definitions

Choose **Register > Register** or the Register toolbar button to scan the normal
OpenData plugin-configuration folders for complete `*.properties` definitions
that are not already registered. The GUI checks deployment-style
`config/plugins` first and, when running from a source checkout,
`src/main/resources/config/plugins`. `index.properties` is ignored because it is
a catalogue rather than a complete plugin definition.

Discovered definitions are validated before they are offered. If one or more new
plugins are found, OpenData shows their plugin ids, names and source filenames in
an OK/Cancel confirmation. **OK** registers them; **Cancel** makes no changes.
Already registered plugin ids are not overwritten by this discovery action.

Duplicate definitions declaring the same `plugin.id` are treated as an error.

**Screenshot to capture:** `gui-register-discovered.png` — confirmation containing
at least one discovered plugin id/name/source file and the OK/Cancel buttons.

### 5.2 Register from File

Choose **Register > Register from File** to open the JavaFX file chooser. Select
one complete plugin `.properties` file from an accessible folder. OpenData reads
and validates the definition before registration. This route can be used with a
file outside the normal configuration folders and follows the existing registry
replacement behaviour for an already registered id.

**Screenshot to capture:** `gui-register-from-file.png` — file chooser filtered to
plugin property files, with a representative `.properties` file selected but no
sensitive folder/account information exposed.

## 6. Enabling, disabling and unregistering plugins

Check one or more plugins, then choose **Enable**, **Disable** or **Unregister**.
If nothing is checked, OpenData displays **No plugin selected**.

Before changing registry state, OpenData shows an OK/Cancel confirmation listing
the selected plugin ids. **OK** performs the operation in a background JavaFX
task and refreshes the table when complete. **Cancel** makes no change. Relevant
administration controls are temporarily disabled while the operation is active.

**Screenshot to capture:** `gui-plugin-administration-confirm.png` — one
representative Enable, Disable or Unregister confirmation showing multiple
selected plugin ids.

## 7. Viewing plugin details

Check exactly one plugin and choose **Details > Plugin Detail**. OpenData loads
the registered descriptor and stored plugin properties in the background and
opens a read-only Property/Value table.

If no plugin is checked, the GUI displays **No plugin selected**. If more than
one is checked, it asks for exactly one selection.

Properties explicitly marked sensitive are masked. Common password, secret,
token and credential property names are also masked defensively.

**Screenshot to capture:** `gui-plugin-detail.png` — a real plugin detail dialog
with enough rows to show the two-column layout and at least one masked value if
the selected definition naturally contains one.

## 8. Settings / Preferences

Choose **File > Settings** or the Preferences toolbar button to inspect effective
application settings. Database/pool, execution and logging values are presented
read-only. The database password is not displayed.

The current GUI does not define editable settings. The Save toolbar button
therefore explains that no Save action is required by the current specification.

**Screenshot to capture:** `gui-settings.png` — representative configuration
rows, including database/execution/logging entries, with any sensitive value
masked.

## 9. Executing plugins

Check one or more enabled plugins and choose **Execute** from the Execute menu or
toolbar. OpenData snapshots the checked plugin ids before processing begins and
shows an OK/Cancel confirmation. Changing the table selection after that point
cannot alter the in-flight command.

A normal Execute run may insert or update provider data and writes the usual run
audit information. When several plugins are selected, OpenData uses the
configured `execution.max-parallel-plugins` limit.

**Screenshot to capture:** `gui-execute-confirm.png` — confirmation listing the
selected plugin ids immediately before a normal Execute run.

## 10. Dry-run

Choose **Dry-run** for the same selected enabled plugins when you want extraction
and transformation without provider database writes or generic run-audit rows.
A dry-run therefore does not replace the Last Run Status or Date of Last Run
shown in the main table.

The confirmation identifies the selected plugins before the run starts.

## 11. Live execution log

After Execute or Dry-run confirmation, OpenData opens a modal execution window
and performs the work on a background task. JUL messages from all selected
plugins are appended to the scrollable text area as they are produced. Normal
file and console logging remain active at the same time.

The centred **Close** button is disabled while processing is active and the
window cannot be closed through the window decoration during the run. After all
selected plugins complete, or execution setup fails, the outcome is shown and
**Close** becomes available.

When the window is closed, the main plugin table is refreshed. A normal Execute
therefore displays the latest persisted run status/date after refresh.

Capture two states because they document different behaviour:

- `gui-execution-log-running.png` — live log while work is active, with formatted
  plugin/run output visible and **Close** visibly disabled;
- `gui-execution-log-complete.png` — completed outcome with **Close** enabled.

A multi-plugin run is preferred for the running capture if it naturally produces
clear contextual log output. Do not expose passwords, tokens, account numbers or
customer statement content in screenshots.

## 12. Viewing the application log

Choose **Details > Logs** or the Logfile toolbar button. OpenData flushes active
JUL handlers and reads the current rotating application log without shutting the
logger down. The source path is shown above a non-editable scrollable text area.

This is different from the live execution window: the Log viewer displays the
existing current log file, while Execute/Dry-run streams new scoped log output.

**Screenshot to capture:** `gui-log-viewer.png` — source log path plus enough
formatted lines to demonstrate the scrollable viewer without exposing sensitive
data.

## 13. Help

Choose **Help > Help** or the Help toolbar button. On Windows, OpenData looks for
`OpenData-Technical-User-Guide.chm` in the supported help locations and opens it
with Windows HTML Help when available. If compiled Help cannot be found or
started, OpenData opens its built-in JavaFX help viewer instead.

**Screenshot to capture:** `gui-help.png` — preferably the compiled Windows Help
window launched from OpenData. If the release distribution intentionally uses
only the fallback, capture the JavaFX help viewer instead and record that choice
in release evidence.

## 14. About OpenData

Choose **Help > About** or the About toolbar button. The JavaFX About dialog shows
the OpenData image and application metadata including version, description, Java
runtime, licence and copyright information. Press **OK** to close it.

The standalone command-line `--about` route uses the same JavaFX presentation.

**Screenshot to capture:** `gui-about-dialog.png` — complete About dialog showing
OpenData version 3.0.0 and the current Java runtime.

## 15. Closing OpenData

Use **File > Exit**, the Exit toolbar button or the normal window close control.
JavaFX exits, control returns to `OpenData.main`, final application status and
duration are logged, and the logging system is shut down cleanly.

## 16. Screenshot publication

The authoritative capture filenames, content requirements and publication rules
are maintained in the
[GUI screenshot plan](../development/gui-screenshot-plan.md). Screenshots are
stored as PNG source assets under `docs/diagrams/source` and copied to the
publication/generated area only after review.
