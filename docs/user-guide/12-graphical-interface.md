# Graphical Interface

**Document ID:** USER-GUI-001  
**Version:** 3.1.0  
**Status:** Implementation in progress  
**Baseline date:** 13 August 2026  
**Minimum Java version:** 24

---

## Overview

OpenData version 3 provides a JavaFX desktop interface while retaining the
command-line interface. Running OpenData without arguments or selecting the
`--gui`/`-g` option starts the graphical interface.

Version 3.1.0 establishes the JavaFX startup sequence and requires Java 24 or
later.

## Startup splash

Starting the graphical interface first displays the OpenData splash screen. The
splash is a JavaFX window and remains visible for **at least five seconds**. It
closes automatically as the maximised main window is displayed.
No action is required from the user.

Capture the documentation screenshot as `gui-splash-screen.png` according to the
[GUI screenshot plan](../development/gui-screenshot-plan.md).

## Main window

The application opens maximised and presents a conventional Windows layout:

1. menu bar;
2. toolbar;
3. plugin table; and
4. status bar.

The menu bar contains File, Register, Enable, Execute, Details and Help. The
toolbar provides quick access to Exit, Settings/Preferences, Save,
registration, enable/disable, Execute, Dry-run, Logfile, About and Help.

## Selecting plugins

Each plugin row has an explicit **Selected** checkbox. Check the plugins that a
later action should operate on. The status bar at the lower right displays the
number currently checked.

The table displays persistent plugin information from the OpenData database: the
plugin identifier and description, enabled state, most recent run status and date
of the last run. Status and date remain blank when the plugin has never been run.

Plugin run times are stored by OpenData in UTC and displayed in the local time
zone of the workstation running the GUI.

## Main-window status

The lower-left status area displays loading feedback while plugin information is
being prepared and **Ready** after the main page has been populated. Later GUI
batches also use this area for operation feedback.

## Persistent plugin loading

When the main window opens, the lower-left status shows `Loading plugin details...`
while the GUI reads the persistent plugin registry and latest run-audit data.
After a successful load it changes to `Ready`. If no plugins are registered, the
table remains empty and displays `No plugins registered`.

If the database/configuration read fails, the table reports that plugin details
could not be loaded and the lower-left status changes to `Unable to load plugin
details`. The failure is also written to the normal OpenData log.


## Plugin administration

### Register

Choose **Register** to scan the normal OpenData plugin configuration folders for
complete `*.properties` definitions that are not already registered. The GUI
checks `config/plugins` beneath the working directory and, when running from a
development checkout, also checks `src/main/resources/config/plugins`.
`index.properties` is ignored because it is a catalogue rather than a complete
plugin definition.

Every discovered definition is validated before it is offered. If new plugins
are found, OpenData displays their ids, names and source filenames in an
OK/Cancel confirmation dialog. Press **OK** to register all of the new
definitions or **Cancel** to make no changes. Existing registered plugin ids are
not overwritten by this command.

If no new definitions are found, OpenData displays an informational message and
returns to the main window. Duplicate configuration files declaring the same
`plugin.id` are reported as an error.

### Register from File

Choose **Register from File** to open a JavaFX file chooser and select one
complete plugin `.properties` file from any accessible folder. OpenData reads
`plugin.id` from the selected file, validates the complete definition and
registers it. This route can also replace the stored definition of an already
registered plugin, matching the existing registry registration behaviour.

### Enable, Disable and Unregister

Check one or more plugins in the **Selected** column and choose the required
action. If no plugin is checked, OpenData displays **No plugin selected**.

Enable, Disable and Unregister show an OK/Cancel confirmation listing the
selected plugin ids. Pressing **OK** performs the operation in a background task
and refreshes the main table when complete. Pressing **Cancel** makes no change.
The table and administration controls are temporarily disabled while an
administration operation is running.

## Current implementation limitations

Execute, Dry-run, Details, Logs, Settings, Save, Help and the main-window About
command are not yet connected to their final GUI production actions. Register,
Register from File, Enable, Disable and Unregister are implemented in Batch 4.

The older command-line About route and command-line run splash still use
deprecated Swing helpers for compatibility. The graphical startup path itself is
JavaFX-only.

## Screenshots

The maintained screenshot filenames and target manual locations are defined in
the [GUI screenshot plan](../development/gui-screenshot-plan.md).
