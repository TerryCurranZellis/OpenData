# Batch 5 Version 3.1.0 GUI implementation review

**Document ID:** REVIEW-GUI-BATCH-005  
**Version:** 3.1.0  
**Date:** 14 August 2026  
**Status:** Ready for local integration testing

---

## Scope

Batch 5 implements the information and utility dialogs required before plugin
execution is connected to the GUI:

- Plugin Detail;
- Settings/Preferences;
- existing application Logfile viewer;
- built-in Help; and
- JavaFX About.

Execute and Dry-run remain Batch 6 work.

## Implementation summary

### Plugin Detail

Plugin Detail requires exactly one plugin checked in the main table. The GUI
loads the registered descriptor and stored `core.plugin_property` values on a
background JavaFX task and displays them in a two-column Property/Value table.
Values explicitly marked sensitive and conventional credential-bearing property
names are masked.

### Settings/Preferences

Settings displays the effective runtime configuration read-only. It includes the
configuration source, database/pool settings, execution settings and logging
settings. The database password is never displayed. The Save toolbar command
therefore reports that no editable settings are defined by the current
specification rather than inventing persistence behaviour.

### Log viewer

`LoggingManager` now exposes the active log directory and a safe handler flush.
The viewer flushes JUL, reads the current `opendata-0.log` file (or the newest
rotating OpenData log when required), and displays it in a scrollable,
non-editable, monospaced JavaFX text area.

### Help

A packaged JavaFX fallback help page provides a concise description of the GUI
and principal commands. Windows compiled Help integration remains a Batch 7
packaging/finalisation concern.

### About and Swing retirement

The About dialog is now JavaFX and displays the OpenData splash image,
application version, description, Java runtime, licence and copyright. The
standalone `--about` route launches the same JavaFX presentation. The deprecated
Swing About dialog, Swing image helper and compatibility GUI launcher are marked
for removal. The deprecated Swing execution splash remains only on the legacy CLI
run path.

## Test focus after overlay

1. Open Settings and verify the effective values appear while the password is
   shown only as masked text.
2. Check one plugin and open Plugin Detail; confirm database properties are
   present and long values are readable.
3. Try Plugin Detail with zero and then two checked plugins and verify the
   warning paths.
4. Open Logs and verify the displayed filename is the active OpenData log and
   the latest startup messages are visible.
5. Open Help from both menu and toolbar.
6. Open About from the main GUI and separately run `--about` to verify both use
   JavaFX.
7. Re-run the complete Maven test suite under JDK 24/25.

## Deferred

- Execute and Dry-run confirmations and execution;
- live execution logging and disabled Close button while execution runs;
- final compiled Windows Help launch/packaging;
- any future editable Settings model; and
- final GUI integration/packaging tests.

---
