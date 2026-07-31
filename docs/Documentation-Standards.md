# OpenData Documentation Standards

## Purpose

These standards define how OpenData documentation is written, assembled, validated and published.

## Authoring rules

1. Author source documents in UTF-8 Markdown.
2. Give every standalone source document one level-one heading.
3. Use repository-relative links and verify them before release.
4. Store PlantUML sources only in `docs/diagrams/source`.
5. Link reader-facing documentation to rendered files in `docs/diagrams/generated`.
6. Do not edit generated manuals directly.
7. Keep reusable content in `docs/shared`.
8. Define each generated document in one JSON file under `docs/manifests`.
9. Keep global tool settings and common document defaults in `config/documentation.json`.
10. Do not add document-specific names, source lists or output names to PowerShell.

## Manifest rules

Every manifest must define a unique `id`, a `title`, an `output` filename and a non-empty ordered `sections` array. A manifest may override common defaults for the Word template, front matter, TOC generation, TOC depth, heading numbering and inventory generation.

Paths in `sections` are relative to `docs`. Exact paths and `*`, `?` and `**` patterns are supported. Patterns must match at least one Markdown file. Duplicate matches are removed while preserving the first occurrence.

Adding a document must require only a manifest and Markdown content. `Invoke-Documentation -Action All` must discover it without a PowerShell change.

## Front matter and TOC

Generated documents must use this order:

1. Cover page.
2. Copyright page.
3. Revision history.
4. Table of contents.
5. Document chapters.

The engine writes a `.document-toc` marker after the front matter. `document-toc.lua` expands the marker for the selected output format and suppresses automatic HTML/PDF title blocks. DOCX is configured to refresh its native TOC field when opened. Do not restore the global Pandoc `--toc` option because it places the TOC before body content.

## Templates and tokens

Shared front matter is stored in `docs/shared`. Templates use double-brace tokens such as `{{title}}`, `{{author}}`, `{{date}}`, `{{year}}`, `{{version}}`, `{{slogan}}`, `{{documentType}}` and `{{audience}}`. The generator fails when a supported content file contains an unresolved token.

The DOCX `template` setting is a Pandoc reference DOCX. Its content is ignored by Pandoc, while styles, page settings, headers and footers are inherited.

## Validation

Validation must detect invalid manifest JSON, duplicate ids or outputs, missing front matter, unmatched section paths, missing Word templates, broken relative links, PlantUML sources outside the canonical source directory and malformed TOC options.

## Generated output

Generated manuals, merged Markdown, source inventories and intermediate files belong under the configured build directory. They are build artefacts and must not be treated as authoritative source material.
