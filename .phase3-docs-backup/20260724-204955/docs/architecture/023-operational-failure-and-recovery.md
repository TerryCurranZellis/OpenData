# Operational Failure and Recovery

**Document ID:** ARCH-023  
**Version:** 1.0  
**Status:** Implemented foundation  
**Baseline date:** 24 July 2026

---

## Failure domains

| Domain | Examples | Recovery principle |
|---|---|---|
| configuration | missing password, invalid pool limits | fail before network/database work |
| discovery | no link, ambiguous links, page changed | preserve run error; no load |
| download | timeout, non-success status, size limit | remove partial file; retry safely |
| parsing | invalid workbook, missing labels | retain source metadata; no replace |
| validation | unexpected dimensions or counts | reject load before transaction |
| persistence | constraint, timeout, lost connection | rollback period replacement |
| shutdown | pool close or log flush problem | retain original run outcome |

## Retry model

Discovery, download, parse and validation are repeatable when the source file is
unchanged. SHA-256 helps identify the exact source used. Ofgem period replacement
is idempotent with respect to its natural period key: rerunning the same period
replaces its facts rather than appending duplicates.

## Partial work

A `.part` download is never treated as a valid source. A failed database batch is
rolled back. A run can remain `STARTED` after process termination; operations
should report and review such rows before retrying.

## Diagnostics

Operational diagnostics should include:

- dataset and run identifiers;
- failure stage and exception category;
- source URI and source-file identifier, not credentials;
- SQL state and vendor error code where safe;
- row/cell reference for data errors;
- active/idle pool snapshot for connection failures;
- elapsed duration and stage counters.

## Manual recovery sequence

1. identify the latest run and terminal/non-terminal status;
2. inspect `core.ingestion_error` and application logs;
3. confirm source-file hash and local file integrity;
4. verify SQL Server health and pool configuration;
5. correct configuration or mapping;
6. rerun the plugin;
7. verify one current period and expected fact count;
8. close or annotate any abandoned run according to an agreed operations policy.
