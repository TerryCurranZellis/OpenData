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

## Plugin Detail

Check exactly one plugin and choose **Details > Plugin Detail**. OpenData loads the
registered descriptor and stored plugin properties in the background, then opens
a two-column **Property / Value** dialog. The dialog is read-only and the **OK**
button closes it.

If no plugin is checked, OpenData displays **No plugin selected**. If more than
one plugin is checked, OpenData asks you to select one plugin because Plugin
Detail displays one configuration at a time.

Values explicitly declared sensitive by a plugin definition are shown as masked
text. Conventional password, secret, token and credential property names are
also masked defensively.

## Settings / Preferences

Choose **File > Settings** or the Preferences toolbar button to inspect the
effective application configuration. The dialog shows database/pool, execution
and logging settings read-only. The database password is never displayed.

The current GUI specification does not define editable settings or Save
semantics, so the Batch 5 Settings dialog is intentionally read-only. Selecting
the Save toolbar button explains that no editable Settings action is currently
defined.

## Viewing the application log

Choose **Details > Logs** or the Logfile toolbar button. OpenData flushes the
active `java.util.logging` handlers and reads the current rotating application
log without closing the logger. The file path is shown above a non-editable,
scrollable text area. Long lines can be reviewed using horizontal scrolling.

This is the existing-log viewer. The live execution log used by Execute and
Dry-run is implemented separately in Batch 6 because its Close button must stay
disabled until processing completes.

## Help

Choose **Help > Help** or the Help toolbar button to open the built-in JavaFX
help overview. It describes the principal GUI actions and acts as a fallback
while Windows compiled Help integration is completed.

## About OpenData

Choose **Help > About** or the About toolbar button to display the JavaFX About
dialog. It contains the OpenData splash image together with the application
version, description, Java runtime, licence and copyright information. Press
**OK** to close the dialog.

The standalone command-line `--about` action now uses the JavaFX About
presentation as well; the previous Swing About implementation is retained only
as deprecated source compatibility code.

## Current implementation limitations

Execute and Dry-run are not yet connected to plugin execution; those actions are
Batch 6 work. Settings is intentionally read-only because the current
specification does not define an editable settings/save contract.

The JavaFX GUI and standalone `--about` command no longer use the deprecated
Swing About implementation. The deprecated Swing execution splash remains only
on the legacy command-line run path pending final migration review.

## Screenshots

The maintained screenshot filenames and target manual locations are defined in
the [GUI screenshot plan](../development/gui-screenshot-plan.md).
