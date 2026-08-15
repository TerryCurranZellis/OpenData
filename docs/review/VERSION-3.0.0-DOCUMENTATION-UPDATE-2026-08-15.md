# Version 3.0.0 Documentation Update

**Assessment date:** 15 August 2026  
**Version:** 3.0.0  
**Status:** Documentation baseline prepared; release evidence still required

## Scope completed

This update reconciles the merged `main` source with the Version 3.0.0
documentation baseline.

- Regenerated all 42 production `package-info.java` files from the current
  top-level Java types.
- Documented 215 top-level types: 129 classes, 53 records, 20 interfaces and
  13 enums.
- Grouped package inventories by type and linked every listed type with
  `{@link TypeName}` plus a short description derived from its class Javadoc.
- Normalised GUI-era Javadoc `@version 3.1.0` metadata to the final 3.0.0 release
  number; no production Java logic was changed by that normalisation.
- Aligned the Maven project version, bootstrap application version and
  documentation project version to 3.0.0.
- Updated current README, release notes, changelog, security, build/development,
  architecture, CLI, user, governance, release and manifest documentation.
- Normalised the embedded CLI help/example text to the canonical lower-case
  `--execute` spelling and independent `--dry-run` example; no parsing logic was changed.
- Reconciled third-party notices with the direct dependencies in `pom.xml`,
  including JavaFX 26.0.1, PDFBox 3.0.8 and JUnit 6.1.2.
- Recorded JDK 24 as the minimum supported Java version, with JDK 26 and Apache
  NetBeans 31 as the current development environment.
- Replaced the transitional Swing/JavaFX narrative with the merged JavaFX
  Version 3.0.0 architecture.
- Added the Version 3.0.0 GUI user guide, screenshot plan and final GUI
  acceptance checklist.
- Refreshed the principal architecture diagrams and added dedicated JavaFX
  application-flow and execution/live-logging diagrams.
- Added the Version 3.0.0 release record and release-readiness assessment.
- Restored the missing `Build-Documentation.ps1` wrapper expected by the GitHub
  documentation workflow and prevented the local direct-build block in
  `Invoke-Documentation.ps1` from running when the function is dot-sourced.

## Required GUI screenshots

The authoritative capture instructions are in
`docs/development/gui-screenshot-plan.md`. Thirteen source PNGs are required:

1. `gui-splash-screen.png`
2. `gui-main-window.png`
3. `gui-register-discovered.png`
4. `gui-register-from-file.png`
5. `gui-plugin-administration-confirm.png`
6. `gui-plugin-detail.png`
7. `gui-settings.png`
8. `gui-execute-confirm.png`
9. `gui-execution-log-running.png`
10. `gui-execution-log-complete.png`
11. `gui-log-viewer.png`
12. `gui-help.png`
13. `gui-about-dialog.png`

Capture them in `docs/diagrams/source` and copy the approved publication images
to `docs/diagrams/generated` before the final manual build. The guide deliberately
does not contain broken image links while the captures are outstanding.

## Diagram update

The canonical PlantUML sources now cover the merged GUI in the architecture,
system-context, component, package-dependency, operational-lifecycle, project
overview and version-evolution views. New sources are:

- `gui-application-flow.puml`
- `gui-execution-sequence.puml`

A matching SVG exists for every current `.puml` source. The modified diagrams
should still be re-rendered with the repository PlantUML toolchain and visually
inspected in the release environment before approval.

## Validation performed in this update

- All four documentation manifests parse as JSON and every referenced section
  exists.
- 455 local Markdown links/images were checked; the only unresolved paths are
  the intentional template placeholders such as `{{coverImage}}` and
  `{{diagramPath}}`.
- Every one of the 215 top-level production Java source files has exactly the
  expected package-inventory link and category heading.
- All 38 PlantUML sources have a matching generated SVG filename.
- `pom.xml`, `application.properties` and `config/documentation.json` all report
  Version 3.0.0.
- The duplicated root/shared third-party and data-source notice files are
  byte-identical.

## Validation still required on the release workstation/CI

This editing environment does not provide the project's required Java 24+
Maven/PowerShell toolchain, so it cannot substitute for release evidence. Before
final tagging, run the repository validation/build on the intended release
commit, including:

- `mvn clean verify` on Java 24;
- a JDK 26 development smoke test;
- Javadoc generation;
- PowerShell documentation validation and all manual formats;
- PlantUML re-rendering and visual inspection;
- CHM generation/Help verification;
- the GUI acceptance checklist and all 13 screenshots; and
- SQL Server/plugin integration acceptance described by the release checklist.

The Version 3.0.0 documentation therefore remains a release-candidate baseline
until the retained evidence is complete.
