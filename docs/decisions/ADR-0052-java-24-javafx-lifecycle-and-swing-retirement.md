# ADR-0052: Use Java 24 minimum and let JavaFX own the desktop lifecycle

**Status:** Accepted  
**Date:** 13 August 2026

## Context

OpenData's first JavaFX GUI batch retained an older Java 17 minimum while the
GUI dependency was JavaFX 26.0.1. JavaFX 26 requires a newer Java runtime, so
that combination could not be treated as a supported deployment baseline.

The existing application also contained Swing-based splash and About helpers.
The version 3 GUI should have one desktop toolkit and a clear lifecycle: logging
is initialised by `OpenData.main`, JavaFX remains active while the GUI is open,
and shared resources are shut down only after the GUI closes.

## Decision

OpenData version 3.0.0 and later requires **Java 24 or later**. Development may
use a later supported JDK; the current development JDK is Java 26 and the current
development IDE is Apache NetBeans 31.

Retain JavaFX 26.x for the GUI. GitHub build and release workflows use Java 24
so CI verifies the actual minimum supported runtime rather than the developer's
newer JDK.

The JavaFX launch path is:

```text
OpenData.main
    |
com.towermarsh.opendata.gui.GuiLauncher
    |
Application.launch(OpenDataGuiApplication.class, ...)
    |
JavaFX splash Stage (minimum five seconds)
    |
maximised main Stage
```

`Application.launch(...)` is deliberately allowed to block the calling main
thread until JavaFX exits. `OpenData.main` therefore keeps logging and other
main-owned lifecycle resources alive for the complete GUI session and performs
normal shutdown only after the GUI has closed.

Use a dedicated undecorated JavaFX `Stage` for the startup splash. It displays
the OpenData splash image for at least five seconds without sleeping or blocking
the JavaFX application thread, then closes as the main stage is shown.

The merged Version 3.0.0 source removes the obsolete Swing UI package after the
equivalent JavaFX splash, About and information/help workflows were implemented.
New GUI code must not add Swing dependencies.

## Consequences

- Java 17 is no longer a supported runtime baseline for current OpenData.
- The Java 17 minimum portion of ADR-0008 is superseded by this decision; its
  immutable-model guidance remains historical architectural context.
- CI must use Java 24 or later.
- The GUI startup path contains no Swing code.
- `OpenData.main` retains ownership of logging shutdown and does not need a
  second GUI-specific shutdown manager.
- The JavaFX replacements for the former Swing About and splash helpers are part
  of the Version 3.0.0 baseline.
- The obsolete Swing UI package is no longer part of the current source tree.

## Related documents

- [ADR-0051: JavaFX graphical interface](ADR-0051-javafx-graphical-interface.md)
- [JavaFX GUI architecture](../development/javafx-gui-architecture.md)
- [Graphical interface user guide](../user-guide/12-graphical-interface.md)
- [GUI specification](../specifcations/OpenData%20Specifcation%20v3.md)
