# Documentation Standard

**Document ID:** STD-DOC-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

## Structure

Maintained documents live in the directory named by their purpose. Index files
must link every maintained document in that section. Architecture pages use
numbered filenames; ADRs use `ADR-NNNN-title.md`.

## Header

A maintained document SHOULD contain a level-one title, document id, version,
status and baseline date. Java-specific documents also state the minimum Java
version.

## Accuracy

- Current behaviour MUST be verified against source, resources, SQL and tests.
- Proposed, deferred, shelved and historical behaviour MUST be labelled.
- Commands MUST reflect the actual packaging model.
- A test claim MUST identify whether it is unit, integration or acceptance.
- Sensitive values MUST be placeholders.

## Diagrams

PlantUML source MUST live under `docs/diagrams/source`. Rendered SVG files belong
under `docs/diagrams/generated`. Markdown image embeds and reader-facing diagram
links MUST target the rendered SVG. The canonical `.puml` filename may be named
as code text for maintainers. Do not maintain duplicate `.puml` files.

## Validation

Before merging, check headings, relative links, diagram source/output mappings,
duplicate ADR numbers, stale feature claims and generated-document builds.
