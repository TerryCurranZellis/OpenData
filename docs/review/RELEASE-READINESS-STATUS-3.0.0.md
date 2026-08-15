# OpenData 3.0.0 Release Readiness Status

**Assessment date:** 15 August 2026  
**Version:** 3.0.0  
**Status:** Release candidate; final evidence incomplete

## Current position

The merged `main` source is now documented as the Version 3.0.0 baseline. The
JavaFX migration is represented as implemented rather than as a future Swing
replacement, and the minimum runtime remains Java 24 while the current
development environment is JDK 26 with Apache NetBeans 31.

Documentation has been aligned with the merged GUI, current plugin set, current
POM dependency versions and current CLI dry-run semantics. Package inventories
are generated from the top-level Java types so package Javadocs distinguish and
link classes, records, interfaces and enums.

## Evidence still required before final release

- Complete `mvn clean verify` on the intended release commit and retain results.
- Confirm a minimum-Java-24 build/test run and a JDK-26 smoke test.
- Render and visually inspect all current PlantUML diagrams.
- Build the required generated manuals and compiled Windows Help output.
- Complete the JavaFX GUI acceptance checklist.
- Capture, review and add the 13 GUI PNG screenshots in the screenshot plan.
- Complete SQL Server/plugin integration acceptance and release evidence.
- Review the final archives for secrets/private data and produce checksums.

## Known documentation/source follow-up

The documentation uses the canonical lower-case `--execute`. A remaining source
message should be checked during the final source-quality pass for any legacy
capitalisation that is presentation-only; this documentation update does not
change implementation Java files other than `package-info.java`.

## Decision

**Not yet approved for final tagging.** The documentation baseline is suitable
for Version 3.0.0 release testing, but the final checklist and evidence index
must be completed before an annotated `v3.0.0` tag is created.
