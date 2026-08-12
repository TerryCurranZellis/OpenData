# Graphical Interface

**Document ID:** USER-GUI-001  
**Version:** 3.0.0  
**Status:** Implementation in progress  
**Baseline date:** 12 August 2026  
**Minimum Java version:** 17

---

## Overview

OpenData version 3.0.0 adds a JavaFX desktop interface while retaining the
command-line interface. Running OpenData without arguments or selecting the
`--gui`/`-g` option starts the graphical interface.

Batch 1 establishes the main page. Backend actions shown by its menu and toolbar
are placeholders until the later integration batches are completed.

## Main window

The application opens maximised and presents a conventional Windows layout:

1. menu bar;
2. toolbar;
3. plugin table; and
4. status bar.

The menu bar contains File, Register, Enable, Execute, Details and Help. The
Toolbar provides quick access to the principal commands including Exit,
Settings/Preferences, Save, registration, enable/disable, Execute, Dry-run,
Logfile, About and Help.

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

## Batch 1 limitations

The Batch 1 rows are sample presentation data. Register, enable, disable,
unregister, Execute, Dry-run, Details, Logs, Settings, Save, Help and About are
not connected to the production services in this batch. Selecting one of those
commands updates the status text only.

## Screenshots

The maintained screenshot filenames and target manual locations are defined in
the [GUI screenshot plan](../development/gui-screenshot-plan.md). The Batch 1
main-window screenshot should be captured as `gui-main-window.png` once the
layout has been reviewed on Windows.
