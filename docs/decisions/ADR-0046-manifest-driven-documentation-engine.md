# ADR-0046: Use a manifest-driven documentation engine

- Status: Accepted
- Date: 2026-07-31
- Decision owners: OpenData maintainers

## Context

The documentation builder contained explicit knowledge of two document sets, Technical and User. Their titles, output names and source composition were stored together in one manifest, while PowerShell selected them through fixed validation sets and conditional branches. Adding another guide therefore required both configuration and code changes.

Pandoc's automatic table-of-contents option also placed the TOC before Markdown body content. Because the cover, copyright and revision history were body content, generated documents could begin with the TOC instead of the cover page.

The engine needs to publish any number of guides, place front matter before the TOC, avoid duplicated shared Markdown, and allow a new document to be added without changing PowerShell.

## Decision

Use one JSON manifest per generated document and discover those manifests from the directory and pattern configured in `config/documentation.json`.

Each normalised manifest supplies or inherits:

- a unique document id;
- title and output filename;
- Word reference template;
- cover, copyright and revision-history files;
- an ordered list of Markdown sections;
- document metadata; and
- generation options such as TOC depth and heading numbering.

The engine performs these stages for each selected manifest:

1. Resolve global defaults and document-specific overrides.
2. Validate front matter, section paths, output uniqueness and template availability.
3. Assemble YAML metadata, cover, copyright and revision history.
4. Insert a format-aware TOC marker after the front matter.
5. Append the ordered Markdown sections.
6. Invoke Pandoc with generic arguments and the manifest's options.
7. Publish the requested output formats and source inventory.

The `document-toc.lua` filter expands the marker at its exact location. DOCX receives a native Word TOC field and is configured to refresh fields when opened, PDF receives a LaTeX table of contents, and HTML receives a linked contents list. Writer-generated HTML and PDF title blocks are suppressed. Pandoc's global `--toc` option is not used.

`Invoke-Documentation -Action All` is an alias for building every discovered manifest. `-Document` accepts manifest ids or manifest/output base names and has no fixed validation set.

## Consequences

### Positive

- Adding a guide requires only a new manifest and its Markdown sources.
- `All` always reflects the manifests currently present.
- Cover, copyright and revision history precede the TOC in every format.
- Shared content and default settings are maintained once.
- Document selection, assembly and Pandoc invocation use one generic path.

### Negative or limiting

- Manifest validation becomes a critical build step.
- Word-compatible viewers calculate the native TOC field; simpler viewers may display the field without populated entries.
- Static HTML contents entries do not include page numbers.
- Existing commands that selected `Technical` or `User` must use the new manifest ids.

## Alternatives considered

### Extend the hard-coded switch statement

Rejected because each new guide would still require a PowerShell release.

### Keep one combined manifest

Rejected because it centralises unrelated document definitions and encourages document-specific processing logic.

### Use Pandoc `--toc` for every format

Rejected because it places the TOC before body-based front matter and cannot satisfy the required document order.

### Duplicate cover and legal pages in each guide directory

Rejected because shared content would drift and require repeated maintenance.

## Implementation notes

The initial manifests are `TechnicalUserGuide`, `AdministratorGuide`, `DeveloperGuide` and `APIReference`. Common defaults are under `defaultDocument` in `config/documentation.json`. Migration details are recorded in `docs/migration/MANIFEST-DRIVEN-DOCUMENTATION-ENGINE.md`.
