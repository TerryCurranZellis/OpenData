# OpenData 3.0.0 Release Record

**Version:** 3.0.0  
**Status:** Release candidate  
**Documentation baseline:** 15 August 2026

## Scope

OpenData 3.0.0 is the first release baseline in which the JavaFX desktop
interface is integrated into `main` alongside the command-line interface. The
release retains the existing Ofgem, OpenMeteo and Octopus plugins and the shared
configuration, registry, execution, logging and SQL Server infrastructure.

## Platform baseline

- Minimum supported Java runtime: JDK 24.
- Current development/test JDK: JDK 26.
- Current development IDE: Apache NetBeans 31.
- JavaFX dependencies: 26.0.1.
- Maven Enforcer requires Maven 3.9 or later and Java 24 or later.

The development JDK and IDE identify the environment used for this release; they
do not raise the documented minimum runtime above Java 24.

## Principal Version 3 changes

- JavaFX splash screen and maximised main application window.
- Menu and toolbar actions for registration, administration, execution,
  dry-run, details, logs, settings, Help and About.
- GUI registration discovery and register-from-file workflows.
- Shared execution gateway used by GUI actions rather than duplicating plugin
  execution logic in controllers.
- Live scoped JUL execution logging with completion-gated Close behaviour.
- Windows compiled HTML Help integration with a built-in JavaFX Help fallback.
- Retirement of the obsolete Swing UI package from the current source tree.
- Package Javadocs regenerated so every package inventory groups and links
  classes, records, interfaces and enums with descriptions.
- Third-party notices reconciled with the release POM, including JavaFX.

## CLI compatibility

The CLI remains supported. Normal provider execution is authorised with
`--execute`/`-x`; `--dry-run`/`-n` is the non-writing execution authorisation and
does not require `--execute`. Administration and detail operations remain
separate from execution authorisation.

## Documentation and GUI evidence

The Technical User Guide now contains a dedicated graphical-interface chapter.
The screenshot plan defines 13 PNG captures required for the final illustrated
manual. Until those images are captured and the GUI acceptance checklist is
complete, this record remains a release candidate rather than a final release
approval.

## Release gates

Final approval requires completion of:

- [Final Release Checklist](Final-Release-Checklist.md)
- [Release Evidence Index](Release-Evidence-Index.md)
- [GUI 3.0 final acceptance checklist](../development/gui-v3.0-final-acceptance-checklist.md)
- [GUI screenshot plan](../development/gui-screenshot-plan.md)

Do not create the `v3.0.0` release tag merely because the documentation has been
updated; tag the tested candidate commit after the evidence gates are approved.
