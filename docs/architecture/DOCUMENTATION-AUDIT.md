# Documentation Audit — OpenData 4.0.0

**Document ID:** REVIEW-DOC-AUDIT-001  
**Application baseline:** 4.0.0  
**Review target:** 4.1.0  
**Updated:** 10 November 2026

## Current source of truth

The Maven reactor and Java sources govern implemented behaviour. The
[code inventory](CURRENT-CODE-INVENTORY.md) covers the active modules and
the [4.1.0 module-boundary review](031-module-boundaries-4.1-review.md)
records the architectural changes on the review branch.

The active documentation describes:
- Java 24+ and the JavaFX GUI/CLI application;
- `init=true` first-run database, credential and AES key configuration;
- application properties as the source of the AES key path;
- plugin registration, enable/disable, execution and non-writing dry-run;
- six independent Ofgem, OpenMeteo, Octopus, Octopus Adjustment, Electric
  Demand and Electric Supply plugins;
- separate Core, Common, API, plugin and executable launcher modules.

The Version 3.0.0 audit is historical and should not override the 4.0.0
first-run guide or current module-boundary documentation.

## Review status

| Review area | Finding / next action |
|---|---|
| Architecture documents | Core, Common, API and launcher boundaries documented for 4.1.0; run `ModuleBoundariesTest` on each change |
| GUI guide | First-run initialisation added; verify screens on Windows |
| Screenshot inventory | Twenty mandatory PNG captures and one conditional dry-run capture; all actual screenshots pending |
| PlantUML | Component and package diagrams updated; first-run sequence added; regenerate SVGs where required by publication |
| User, developer and operations manuals | Current entry-point descriptions updated to 4.0.0; verify individual plugin and SQL details against deployed builds |
| JavaDocs | Package inventory and author/version checks are automated; manual description/contract review is ongoing |
| Documentation build | Manifest, links and HTML manual generation validated by GitHub Actions after replacing the Chocolatey Pandoc step |
| Historical releases and ADRs | Preserve as archival records; do not rewrite older decisions as current behaviour |
| SQL Server acceptance | Clean installation, failed setup and retry, and live provider runs still require environment testing |
| Retired material | Remove only after checking links, manifests, release history and external references |

## High-priority acceptance evidence

1. Strict Maven `clean verify`, including JUnit, Checkstyle, SpotBugs and dependency checks.
2. Javadoc generation on the supported Java toolchain.
3. PlantUML SVG regeneration and source consistency.
4. Windows JavaFX first-run wizard screenshots and controls.
5. Disposable SQL Server provisioning, disabled plugin registration and retry.
6. Real execution and dry-run tests for each provider, including exit/log status.
7. Full current manual generation and review of source/data notices.

The documentation build is a structural validation, not a claim that
interactive screenshots or live database acceptance have been completed.
