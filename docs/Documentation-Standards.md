# OpenData Documentation Standards

## Purpose

These standards define how OpenData documentation is written, assembled, validated and published.

## Authoring rules

1. Author source documents in UTF-8 Markdown.
2. Give every standalone source document one level-one heading.
3. Use repository-relative links and verify them before release.
4. Store PlantUML sources only in `docs/diagrams/source` in the main repository.
5. Link reader-facing documentation to rendered files in `docs/diagrams/generated`.
6. Do not edit generated manuals directly.
7. Keep reusable content in `docs/shared`.
8. Keep manual composition in `docs/manifest.json` rather than hard-coding it in scripts.

## Templates

Templates use double-brace tokens such as `{{title}}`, `{{author}}`, `{{date}}`, `{{version}}` and `{{slogan}}`. The generator replaces supported tokens during assembly.

## Validation

Validation must detect missing manifest entries, broken relative links, PlantUML sources outside the canonical source directory and malformed manual definitions. Orphan reporting may be advisory until all legacy documents are migrated into the manifest.

## Generated output

Generated files belong under `docs/generated` or the configured build directory and should not be treated as primary source material.
