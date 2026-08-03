# Documentation Standard

**Document ID:** STD-DOC-001  
**Version:** 2.0  
**Status:** Version 2.0.0 documentation baseline  
**Baseline date:** 3 August 2026

---

## Authority and structure

Maintained documentation is Markdown under `docs` or a root project policy file.
Generated DOCX, PDF and HTML outputs are products of the documentation engine,
not the authoritative source.

Section indexes must link each maintained page. Architecture pages use numbered
filenames; ADRs use `ADR-NNNN-title.md`; generated manuals use JSON manifests
under `docs/manifests`.

## Document header

A maintained document SHOULD include a level-one title, document ID, version,
status and baseline date. Java-specific documents state the minimum Java
version. Historical release records retain their original dates and version.

## Accuracy rules

- Current behaviour MUST be verified against source, resources, SQL, POM,
  workflows and tests.
- Implemented, planned, deferred, shelved and historical behaviour MUST be
  distinguished.
- Commands MUST reflect the actual main class and packaging model.
- A test claim MUST identify whether it is unit, component, integration or
  acceptance evidence.
- Security examples MUST use placeholders.
- Known defects and release blockers MUST remain visible until corrected and
  verified.
- Documentation examples that are not compiled by Maven MUST be temporarily
  compiled when changed.

## Links and diagrams

Canonical PlantUML source belongs in `docs/diagrams/source`; rendered SVG belongs
in `docs/diagrams/generated`. Reader-facing Markdown embeds target SVG, not
`.puml`. A diagram source and rendered output use the same basename.

Relative links must work from the source file and from its generated manual
context. Avoid machine-specific absolute paths.

## Manifests

Manifest sections are ordered deliberately. Use explicit paths for front matter
and high-level chapters, then narrow glob patterns. Avoid broad patterns that
silently include implementation notes, duplicate indexes or historical material
in a public manual.

## Validation before merge

Check:

1. JSON and Markdown syntax;
2. relative links and image targets;
3. diagram source/output pairs;
4. duplicate ADR numbers and statuses;
5. stale version, package and feature claims;
6. secret-like values and local paths;
7. DOCX/PDF output ordering and cover page; and
8. example code compatibility with the current framework API.
