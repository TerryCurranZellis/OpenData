# Ingestion Audit and Provenance

**Document ID:** ARCH-020  
**Version:** 1.0  
**Status:** Implemented foundation  
**Baseline date:** 24 July 2026

---

## Objective

Every dataset execution must answer: what ran, which source file was used, when
it ran, what was loaded, what failed and whether a retry is safe.

## Core records

- `core.dataset` identifies the logical dataset and plugin;
- `core.ingestion_run` records start, finish, status, counters and duration;
- `core.source_file` records source URI, local name, size, media type and SHA-256;
- `core.ingestion_error` records stage, row, field and diagnostic details;
- `core.schema_version` records installed database changes.

## Status lifecycle

The terminal statuses are `SUCCEEDED`, `SUCCEEDED_WITH_REJECTIONS`, `FAILED`
and `CANCELLED`. `STARTED` is the only non-terminal persisted state.

A process that terminates unexpectedly can leave a run in `STARTED`. Recovery
reporting should classify old `STARTED` rows as abandoned, but must not rewrite
them automatically until a clear timeout policy is agreed.

## Provenance chain

```text
core.dataset
    -> core.ingestion_run
        -> core.source_file
        -> core.ingestion_error
        -> ofgem.price_cap_level
```

Each Ofgem fact row stores the ingestion run plus its workbook worksheet and cell.
The source file points to the run and carries its hash. This provides a trace from
a database value back to a specific downloaded file and spreadsheet location.

## Counters

The implemented run table stores `rows_extracted`, `rows_loaded` and
`rows_rejected`. For the Ofgem workbook, extracted means typed fact records
produced by the extractor, loaded means fact rows confirmed written, and rejected
means extracted records excluded under an explicit validation policy. Counters
must be supplied from stage results rather than inferred from log messages.

More granular discovery/read/accepted counters may be added later, but are not
part of the current Phase 3 schema.

## Retention

Database audit history is retained independently of staging-file retention.
Deleting a local source copy must not delete its metadata, hash or source URI.
Future retention policy must define how long full error messages and source files
are kept.
