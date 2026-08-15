# ADR-0057: Complete the JavaFX migration and define Windows packaging

**Status:** Accepted  
**Date:** 14 August 2026

## Context

The version 3 GUI migration introduced JavaFX equivalents for the startup
splash, About window, information dialogs, plugin administration, execution and
live logging. A small legacy `com.towermarsh.opendata.ui` package nevertheless
remained because `OpenData.main` still displayed a Swing startup splash for CLI
execution and application metadata was located beside those UI helpers.

`ApplicationInfo` contains application identity and runtime metadata and has no
GUI toolkit dependency. Keeping it in a legacy UI package makes application-wide
code depend on a package that is being retired.

Version 3.1 also needs a Windows delivery form that supports both the default
JavaFX desktop experience and the existing command-line interface.

## Decision

Move `ApplicationInfo` to `com.towermarsh.opendata.app` and treat it as shared
application metadata.

Remove the Swing splash from command-line execution. JavaFX owns the GUI startup
splash; CLI execution remains a console workflow and does not need a graphical
startup window.

Remove the remaining Swing compatibility helpers after their JavaFX replacements
are active and no callers remain. Remove the direct `java.desktop` module
requirement when no OpenData source directly uses AWT or Swing.

Use the compiled Windows HTML Help file from the JavaFX Help command when it is
available. Retain the built-in JavaFX text help as a portable and failure-safe
fallback.

Use JDK `jpackage` for Windows delivery. The primary `OpenData` launcher is the
normal desktop launcher. Provide an additional `OpenData-CLI` launcher with a
Windows console. Both use `com.towermarsh.opendata.OpenData` so command-line
parsing and application lifecycle remain centralised in one entry point.

Create and test an `app-image` before producing an EXE or MSI installer.

## Consequences

- `com.towermarsh.opendata.app` owns application metadata as well as application
  execution/status concerns.
- production OpenData source no longer needs the legacy Swing UI package after
  compatibility callers are removed.
- the JavaFX GUI is the only graphical toolkit in the active application.
- CLI runs no longer create a graphical splash window.
- Windows users can launch the application normally without a console or use a
  dedicated console launcher for CLI work.
- generated CHM help can be shipped with the Windows image without making it a
  hard runtime dependency.

## Supersedes

This decision completes the staged Swing-retirement portion of ADR-0052. The
Java 24 minimum and JavaFX lifecycle decisions in ADR-0052 remain in force.

## Related decisions

- ADR-0051: JavaFX graphical interface
- ADR-0052: Java 24, JavaFX lifecycle and Swing retirement
- ADR-0055: JavaFX information dialogs and sensitive display
- ADR-0056: JavaFX live execution logging
