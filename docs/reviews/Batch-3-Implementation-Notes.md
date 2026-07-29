# Batch 3 – Repository Standards Implementation Notes

**Status:** Implemented  
**Date:** 29 July 2026

## Scope

Batch 3 establishes conventional open-source repository standards without
changing application runtime behaviour.

## Added at repository root

- `CHANGELOG.md`
- `CONTRIBUTING.md`
- `CODE_OF_CONDUCT.md`
- `SECURITY.md`
- `NOTICE`

## Changed

- Reworked `README.md` around project status, requirements, build, configuration,
  command examples, documentation, contribution and licensing.
- Replaced duplicate files under `docs/` with compatibility pages that direct
  readers to the canonical root documents.
- Updated the documentation manifest exclusions and documentation index.

## Diagram assessment

No new architecture or process diagram is required for this batch. Repository
policy files are textual governance artefacts; adding a diagram would not improve
their clarity. Therefore no additional `.puml` source was created.

## Follow-on work

Batch 4 should standardise source-file copyright and Apache 2.0 headers, review
all remaining Towermarsh references, and verify third-party attribution data.
