# PlantUML and Screenshot Source Directory

**Document ID:** DIAG-SOURCE-001  
**Version:** 3.0.0  
**Status:** Current  
**Baseline date:** 15 August 2026

---

This is the canonical source location for OpenData documentation visuals.
PlantUML sources are stored as flat `.puml` files so every generated filename is
stable and unique. Version 3.0.0 GUI screenshots are also captured here as PNG
files using the names defined in `../../development/gui-screenshot-plan.md`.

Generated/rendered diagram assets are written to `../generated`. When the
documentation pipeline copies or optimises screenshots for publication, the
source PNG in this directory remains the canonical capture.

Every PlantUML source must:

- contain one `@startuml`/`@enduml` document;
- render without remote includes;
- use a unique filename;
- describe current or clearly labelled future behaviour; and
- have a corresponding entry in the [diagram index](../README.md).

Every release GUI screenshot must:

- use the exact filename in the screenshot plan;
- contain no private credentials, customer data or machine-specific secrets;
- show the Version 3.0.0 interface state requested by the plan; and
- be reviewed at normal documentation scale before release approval.
