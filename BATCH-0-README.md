# OpenData Refactoring — Batch 0

## Purpose

This batch adds the shared foundation for the later Ofgem, OpenMeteo, and
Octopus refactoring batches. It does not change any plugin implementation.

Base repository revision reviewed:
`d352a0015aa46f436512182523fbfffe628c22fa` (`main`, 4 August 2026).

## Included changes

### Shared validation

- `PluginPropertyValues` provides consistent parsing of strings, integers,
  long integers, decimal numbers, booleans, durations, dates, paths, and URIs.
- `ValidationRules` contains reusable text, range, duration, and date-order
  checks.
- `SqlIdentifiers` centralises safe SQL Server identifier validation and
  qualification.
- Validation errors identify the plugin and property without echoing the
  potentially sensitive value.

### Shared JDBC infrastructure

- `JdbcTransactionTemplate` centralises connection borrowing, transaction
  boundaries, rollback, auto-commit restoration, and optional pooled-session
  cleanup.
- `JdbcBatchExecutor` centralises prepared-statement batching and affected-row
  counting.
- `JdbcUpsertAdapter<T, C>` keeps record-specific SQL in typed adapters.
- `JdbcUpsertExecutor` supplies the common exists/insert/update control flow.
- `JdbcUpsertResult` provides shared insert/update statistics.

## Installation

1. Create or switch to the intended local Git branch.
2. Extract this archive into the root of the OpenData repository.
3. Allow the extracted `src` directory to merge with the existing `src`
   directory.
4. The existing validation `package-info.java` is intentionally replaced.
5. Run:

```powershell
mvn clean verify
```

## Expected later use

- Batch 1 will migrate Ofgem configuration parsing and JDBC transaction/batch
  mechanics.
- Batch 2 will migrate OpenMeteo while retaining its staging-table and pooled
  SQL Server session cleanup.
- Batch 3 will implement separate electricity and gas
  `JdbcUpsertAdapter` classes for Octopus and remove their duplicated control
  flow.

## Files

All Java files under `src/main` are production additions except
`validation/package-info.java`, which replaces the existing package document.
The files under `src/test` are new focused unit tests.
