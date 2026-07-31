# ADR-0045: Standardise the documentation delivery baseline

- Status: Accepted
- Date: 2026-07-31
- Decision owners: OpenData maintainers

## Context

The OpenData documentation subsystem accumulated several related improvements during the public-release preparation work. These included Markdown-first source control, PlantUML source and rendered-image separation, documentation validation, Pandoc publication to HTML, DOCX and PDF, a shared Word reference document, source inventories, licensing material, and machine-neutral build scripts.

ADR-0017 established the general decision to maintain documentation as code, but it did not record the delivery constraints introduced by the later documentation and release batches. A consolidated record is required so those constraints are not accidentally removed during future refactoring.

## Decision

Retain the following documentation delivery baseline:

- Markdown and PlantUML in the repository are the authoritative sources.
- PlantUML source files are stored only in `docs/diagrams/source`; reader-facing documents use rendered files from `docs/diagrams/generated`.
- Documentation is validated before publication.
- Pandoc is the supported publishing engine for HTML, DOCX and PDF.
- DOCX styling is supplied by a Pandoc reference DOCX and post-processing may adjust section layout and image bounds.
- Shared text is maintained once under `docs/shared`.
- Generated manuals, inventories and intermediate files are written below the configured build directory.
- Maintained scripts must not contain local workstation paths or unconditional local invocations.
- Release notes, licensing, migration information and architecture decisions are maintained with the source documentation.

ADR-0046 defines the subsequent decision to make document composition manifest-driven.

## Consequences

### Positive

- Documentation and release artefacts are reproducible from version-controlled sources.
- Validation and rendering conventions are explicit and reviewable.
- Local workstation details cannot silently alter CI or another developer's build.
- The baseline can evolve without losing the safeguards added during release preparation.

### Negative or limiting

- Contributors need Pandoc, Java and PlantUML for a complete local build.
- PDF generation also depends on the configured LaTeX and SVG conversion toolchain.
- The DOCX post-processing code remains a specialised part of the PowerShell engine.

## Alternatives considered

### Keep the recent changes undocumented

Rejected because later refactoring could remove important build and validation behaviour without recognising it as an architectural decision.

### Treat generated DOCX and PDF files as authoritative

Rejected because binary artefacts are difficult to review, merge and keep synchronised with source.

## Implementation notes

The baseline is implemented by `Invoke-Documentation.ps1`, `Convert-PlantUml.ps1`, the Lua filters under `docs/_filters`, the shared content under `docs/shared`, and the documentation configuration in `config/documentation.json`.
