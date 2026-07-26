# PlantUML Source Directory

**Document ID:** DIAG-SOURCE-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

This is the only canonical location for OpenData PlantUML source. Keep sources
flat so every generated filename is stable and unique. Generated SVG or PNG
files are written to `../generated`.

Every source must:

- contain one `@startuml`/`@enduml` document;
- render without remote includes;
- use a unique filename;
- describe current or clearly labelled future behaviour;
- have a corresponding entry in the [diagram index](../README.md).
