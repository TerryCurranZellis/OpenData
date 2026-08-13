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

The table also displays the plugin identifier and description, enabled state,
most recent run status and date of the last run. Status and date remain blank
when the plugin has never been run.

## Main-window status

The lower-left status area displays loading feedback while plugin information is
being prepared and **Ready** after the main page has been populated. Later GUI
batches also use this area for operation feedback.

## Current implementation limitations

The current rows are still sample presentation data. Register, enable, disable,
unregister, Execute, Dry-run, Details, Logs, Settings, Save, Help and the main
window About command are not yet connected to production services.

The older command-line About route and command-line run splash still use
deprecated Swing helpers for compatibility. The graphical startup path itself is
JavaFX-only.

## Screenshots

The maintained screenshot filenames and target manual locations are defined in
the [GUI screenshot plan](../development/gui-screenshot-plan.md).
