# Component Interactions

**Document ID:** ARCH-010  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## Listing

`--list-plugins` parses arguments, creates the registry and prints descriptors.
It does not load a dataset or create download clients.

## Execution

CLI creates arguments; registry verifies the plugin; bootstrap and plugin loaders
construct `ApplicationConfig`; the plugin validates source settings; the
pipeline executes; `Main` records status and duration.

## Source-specific flows

Ofgem downloads an HTML page, discovers the current workbook, downloads XLSX and
uses Apache POI. OpenMeteo constructs historical API query parameters and parses
JSON with Jackson (or CSV when selected).

See [plugin-execution-sequence.puml](../diagrams/plugin-execution-sequence.puml)
and [configuration-loading-sequence.puml](../diagrams/configuration-loading-sequence.puml).
