# OpenData Documentation Index

## Start here

- [Project README](../README.md)
- [Quick start](guides/quick-start.md)
- [Technical user guide](user-guide/README.md)
- [Administrator operations](operations/README.md)
- [Architecture](architecture/ARCHITECTURE.md)
- [Developer guide](development/README.md)
- [API and configuration reference](reference/README.md)
- [Governance and compliance](governance/README.md)
- [Release process](release/Release-Process.md)
- [Current release readiness](review/RELEASE-READINESS-STATUS-2.0.0.md)

## Generated manuals

| Manifest | Audience |
|---|---|
| `manifests/TechnicalUserGuide.json` | Operators and technical users |
| `manifests/AdministratorGuide.json` | Database/platform administrators |
| `manifests/DeveloperGuide.json` | Maintainers and plugin developers |
| `manifests/APIReference.json` | Integrators and support engineers |

## Version 3 GUI implementation

- [JavaFX GUI architecture](development/javafx-gui-architecture.md)
- [Graphical interface user guide](user-guide/12-graphical-interface.md)
- [GUI screenshot plan](development/gui-screenshot-plan.md)
- [ADR-0051: JavaFX graphical interface](decisions/ADR-0051-javafx-graphical-interface.md)
- [ADR-0052: Java 24, JavaFX lifecycle and Swing retirement](decisions/ADR-0052-java-24-javafx-lifecycle-and-swing-retirement.md)
- [ADR-0053: JavaFX controller/service boundary](decisions/ADR-0053-javafx-controller-service-boundary.md)
- [Batch 2 GUI implementation notes](reviews/Batch-2-Version-3.1.0-GUI.md)
- [Batch 3 GUI implementation notes](reviews/Batch-3-Version-3.1.0-GUI.md)

## Current processing-refactor baseline

- [Shared validation and JDBC architecture](architecture/028-shared-validation-and-jdbc-infrastructure.md)
- [Shared validation and JDBC API reference](reference/shared-validation-and-jdbc-reference.md)
- [Ofgem architecture](architecture/021-ofgem-price-cap-architecture.md)
- [OpenMeteo architecture](architecture/026-openmeteo-historical-weather-architecture.md)
- [Octopus architecture](architecture/027-octopus-energy-statement-architecture.md)
- [Final processing-refactor documentation audit](review/FINAL-DOCUMENTATION-AUDIT-2026-08-04.md)

## Evidence and history

- [Final release checklist](release/Final-Release-Checklist.md)
- [Release evidence index](release/Release-Evidence-Index.md)
- [ADR register](decisions/ADR-REGISTER.md)
- [Batch implementation notes](reviews/)
- [Historical Version 1.0.0 record](release/Release-1.0.0.md)

## Authority order

When documents conflict, use this order:

1. tested release evidence and current source;
2. current release/readiness and architecture documentation;
3. current user, operations, developer and reference guides;
4. accepted ADRs; and
5. dated reviews and historical release records.
