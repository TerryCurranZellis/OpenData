# ADR-0035: Replace one Ofgem period in a single transaction

- **Status:** Accepted
- **Date:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

A rerun may correct or replace all facts for an existing price-cap period. Row by
row commits could leave a mixture of old and new values after failure.

## Decision

For a resolved period identifier, delete all existing `price_cap_level` rows and
batch insert the complete validated replacement within one JDBC transaction.
Commit only after every batch result succeeds; otherwise roll back.

## Consequences

### Positive

- readers see either the previous complete period or the new complete period;
- reruns are idempotent by period and dimension key;
- failure recovery is straightforward.

### Negative or limiting

- the replacement holds locks for the transaction duration;
- the full validated period must be available before loading;
- period metadata upsert and fact replacement currently use separate transactions.

## Alternatives considered

Individual row commits and append-only duplicates were rejected. A staging-table
merge may replace delete/insert later if dataset volume or concurrency requires it.
