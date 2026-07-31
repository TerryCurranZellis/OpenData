# Unresolved Toolchain and Specification Gaps

**Document ID:** REVIEW-UNRESOLVED-20260726  
**Version:** 1.2  
**Status:** Current  
**Baseline date:** 26 July 2026  
**Reviewed source:** commit `c2adae5` plus the documentation update branch  
**Minimum Java version:** 17

---

## Purpose

This document is the concise hand-off list for work that remains unresolved
after the July 2026 documentation update. It separates:

- product or specification decisions that require implementation work; and
- toolchain checks that could not be completed in the documentation workspace.

The broader evidence and documentation corrections are recorded in the
[documentation gap analysis](DOCUMENTATION-GAP-ANALYSIS-2026-07-26.md).

## Unresolved specification and implementation decisions

::: {.landscape-table}

*Unresolved implementation decisions.*

| Priority | Decision or gap | Current position | Completion evidence required |
|---|---|---|---|
| Critical | Canonical execution audit identity | Framework execution uses `core.PluginRun`; Ofgem independently uses `core.ingestion_run` and `source_file` | ADR and schema change selecting or linking one run identity, migration, integration tests |
| Critical | Runtime database secret | `config/application.properties` contains a classpath password | Remove the value, rotate any used credential, document and test an external secret source |
| High | Legacy application properties | `src/main/resources/application.properties` is not loaded but contains obsolete paths, credentials and licence text | Confirm no external consumer, then remove or quarantine it |
| High | Executable distribution | Maven produces a library JAR without `Main-Class` or bundled dependencies | Select shade, assembly, jlink or launcher packaging and prove a clean invocation |
| High | Process exit-code ownership | `Main` logs `ExecutionStatus` but does not map it to the operating-system result | Decide launcher versus `System.exit`, then test success, partial failure and fatal failure |
| High | SQL Server acceptance | JDBC unit tests do not prove a clean live installation or write/rollback behaviour | Retained acceptance record for install, least privilege, plugin writes, idempotency and rollback |
| Medium | SQL installation ordering | Core scripts are split between `sql/` and `sql/sqlserver/` | One ordered manifest or migration mechanism with repeatable clean-install test |
| Medium | Parallel database APIs | Current runtime and older generic database configuration/repository abstractions coexist; the duplicate Ofgem repository stack is removed | Select the supported generic API and remove or explicitly retain compatibility classes |
| Medium | ADR-0030 pool lifecycle variance | The accepted decision rejects a registered-driver singleton; the runtime uses one | Restore the decision or supersede it explicitly |
| Medium | Generic ETL contracts | Stage interfaces exist but concrete plugins own their orchestration | ADR confirming extension-only contracts or implementation of a common pipeline |
| Medium | Ofgem component details | Component tables exist but the current loader writes annual cap levels only | Approved extraction rules, mappings, fixtures and persistence tests |
| Medium | TLS validation | Development URL uses `trustServerCertificate=true` | Trusted server certificate and `trustServerCertificate=false` outside local development |
| Medium | Active download bounds | Ofgem and OpenMeteo do not use the reusable bounded downloader path | Add configurable response-size limits and failure tests |
| Medium | Source licence headers | 14 of 169 main Java files lack the Apache-2.0 SPDX marker; four use the earlier header | Confirm the intended licence and normalise source headers |
| Low | Modelled but unsupported adapters | XML, ZIP, HTML-table and browser-automation values exist beyond the implemented parser/download paths | Preserve an explicit capability matrix or implement and test each adapter |
| Low | Automated dependency enforcement | Package rules rely on review; no ArchUnit-equivalent gate exists | Add automated architecture tests if these rules are release gates |
| Low | Typed shared parser model | Generic parsers return string maps | Decide whether to retain the extension boundary or introduce typed row/table records |
| Low | Internal scheduling | ADR-0020 remains Deferred | Continue with an external scheduler or supersede ADR-0020 |

:::

## Unresolved toolchain verification

| Check | Status in this documentation workspace | Follow-up |
|---|---|---|
| Maven build and Java tests | Full lifecycle not run because Maven is not installed. All main, test and template Java sources passed parser checks; the changed internal classes, plugin stage packages, templates and selected moved tests passed targeted Java 17 compilation with local API stubs where external dependencies were unavailable | Run `mvn clean test` and `mvn package` on Java 17 to resolve the real dependency graph and execute the complete suite |
| PowerShell 5.1 script execution | Not run because `powershell`/`pwsh` is not installed | Run `Invoke-Documentation.ps1 -Action Test -FailOnWarning` on Windows PowerShell 5.1 and build both manuals |
| Repository PlantUML tool | `tools/plantuml.jar` is intentionally not committed; SVGs were rendered with PlantUML 1.2026.1 from a temporary tool location | Place the approved JAR at `tools/plantuml.jar`, then dot-source `scripts/Convert-PlantUml.ps1`, run `Convert-PlantUml -ProjectRoot $PWD -Format svg -Clean`, and compare outputs |
| Pandoc DOCX SVG converter | `rsvg-convert` is not installed. Pandoc reported conversion warnings and embedded the SVG fallback; LibreOffice rendered all 12 technical-manual image occurrences successfully | Install `librsvg`/`rsvg-convert` on the documentation builder to remove the warnings and repeat the DOCX comparison |
| DOCX and PDF rendering | Direct Pandoc builds succeeded for both manuals. OOXML inspection found 13 A4 technical-manual sections, including six landscape sections and a final portrait section; all 12 images fit their configured section boxes. LibreOffice and direct-PDF renders had no blank-page candidates, and every landscape run returned to portrait | Repeat through the maintained PowerShell entry point on Windows and retain a final visual acceptance record |
| Live SQL Server | No reachable acceptance instance was supplied | Execute the database acceptance matrix with the application principal |
| Executable-package test | Blocked by the unresolved packaging decision | Test the selected distribution from a clean machine |
| Exit-code test | Blocked by the unresolved exit-code decision | Assert OS codes for successful, partially failed and fatal runs |

Pandoc, XeLaTeX, Inkscape and LibreOffice are present in the documentation
workspace. PlantUML syntax validation passed for all 21 canonical sources, all
21 SVGs were regenerated, and Markdown validation found no broken relative
links or direct `.puml` image links. Render-level verification confirmed A4
dimensions, SVG inclusion in DOCX, PDF vector intermediates and
portrait-to-landscape-to-portrait transitions. The maintained PowerShell entry
point must still be exercised on its supported platform.

## Required acceptance sequence

1. Resolve the classpath secret and legacy configuration files.
2. Decide audit identity, packaging and exit-code ownership through ADRs.
3. Run the Java and PowerShell validation commands on their supported
   toolchains.
4. Install the SQL scripts into a clean SQL Server database using the documented
   order.
5. Run Ofgem and OpenMeteo dry runs and database-writing runs.
6. Repeat inputs to prove idempotency or atomic replacement as applicable.
7. Induce a persistence failure and retain rollback and terminal-audit evidence.
8. Inspect the generated DOCX and PDF to confirm each landscape diagram is on an
   A4 landscape section and the following content returns to A4 portrait.

## Closure rule

A row may be removed only when its decision is recorded and the named evidence
is retained. Documentation wording alone does not close an implementation or
acceptance gap.
