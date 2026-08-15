# Batch 7 — Version 3.1.0 GUI Final Integration

**Version:** 3.1.0  
**Date:** 14 August 2026  
**Status:** Implementation package

---

## Scope

Batch 7 closes the staged JavaFX migration without replacing the tested Batch 6
Execute/Dry-run implementation. The installer operates as a transformation over
the current working tree so local Batch 6 execution and live-log changes remain
intact.

## Application metadata

`ApplicationInfo` is application-wide metadata. It has no JavaFX or Swing
responsibility and is therefore moved from:

```text
com.towermarsh.opendata.ui.ApplicationInfo
```

to:

```text
com.towermarsh.opendata.app.ApplicationInfo
```

Imports and the package-scoped test are updated by the Batch 7 installer while
preserving the local implementation and test assertions.

## Swing retirement

The JavaFX GUI already owns the startup splash through `OpenDataSplashScreen`.
The command-line route no longer displays the old Swing splash. Batch 7 therefore
removes the active `StartupSplashScreen` use from `OpenData.main` and retires the
unused Swing compatibility helpers when no current source still references them:

- `StartupSplashScreen`;
- `OpenDataImageLoader`;
- `AboutDialog`; and
- the compatibility `ui.GuiLauncher`.

If all Swing/AWT source references have gone, `requires java.desktop;` is also
removed from `module-info.java` when that descriptor is present.

## Help integration

The Help action now attempts to open the generated Windows HTML Help file:

```text
docs/build/help/TechnicalUserGuide/OpenData-Technical-User-Guide.chm
```

During packaged execution it also looks for:

```text
help/OpenData-Technical-User-Guide.chm
```

beside the packaged application JAR. When the CHM is absent, the platform is not
Windows, or Windows Help cannot be started, the existing built-in JavaFX help
viewer remains the fallback.

## Windows packaging

`scripts/Build-Windows-Package.ps1` creates a jpackage application image by
default. It provides:

- `OpenData` — the normal no-console launcher; running it without arguments opens
  the JavaFX GUI through the existing `OpenData.main` default;
- `OpenData-CLI` — an additional Windows console launcher for command-line use;
- runtime dependencies copied from Maven;
- the multisize OpenData icon when available; and
- the compiled CHM file when it has already been generated.

Use `-Type exe` or `-Type msi` only after the `app-image` has passed acceptance
testing and the Windows packaging prerequisites are installed.

## Verification

`scripts/Test-Gui-Batch7.ps1` checks the package move, retired Swing classes,
remaining Swing/AWT references, Maven tests and compiled Help availability.

The final manual acceptance list is in:

```text
docs/development/gui-v3.1-final-acceptance-checklist.md
```

## Installation safety

`apply-batch-7.ps1` backs up every existing file it changes or removes under:

```text
.opendata-batch7-backup/<timestamp>/
```

The installer does not copy an older `OpenDataMainController.java` into the
repository. It performs only a narrow replacement of the existing Help action,
which protects the Batch 6 execution and live-log implementation already tested
in the working tree.
