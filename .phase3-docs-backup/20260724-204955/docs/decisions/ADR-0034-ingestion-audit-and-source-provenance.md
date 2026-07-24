# ADR-0034: Audit every ingestion run and source file

- **Status:** Accepted
- **Date:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

Loaded values must be traceable to a run and source artefact. Logs alone are not
a durable queryable record and cannot reliably support retry analysis.

## Decision

Persist dataset registration, run status/timing/counters, source-file metadata,
SHA-256 and structured ingestion errors in the `core` schema. Plugin facts link
to the ingestion run, and plugin periods link to the source file.

## Consequences

### Positive

- operational status is queryable;
- values can be traced to a specific source file;
- retries and duplicate analysis have durable evidence;
- framework-wide metrics use one model.

### Negative or limiting

- audit tables require retention and privacy policies;
- failed runs can leave non-terminal `STARTED` records after process termination;
- counters must be maintained consistently by orchestration.

## Alternatives considered

Log-only auditing was rejected. Embedding all audit columns in every plugin fact
was rejected because it duplicates framework concerns.
