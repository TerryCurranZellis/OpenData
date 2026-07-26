# Testing Standard

**Document ID:** STD-TEST-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

## Required levels

| Level | Purpose |
|---|---|
| Unit | Value objects, parsing rules, selection, validation and error handling |
| Component | Plugin flow with controlled HTTP/files and repository substitutes |
| Integration | Real SQL Server schema, permissions, transactions and pooling |
| Acceptance | End-to-end dry and write runs using representative sources |

## Rules

- Every defect fix MUST have a focused regression test where practical.
- Concurrency tests MUST prove overlap, failure isolation, ordered results and
  interruption behaviour.
- Repository tests MUST cover commit, rollback, unchanged rows and cleanup of
  pooled-session state.
- Parser fixtures MUST include a representative publisher file and malformed
  boundary cases.
- Mock JDBC tests MUST NOT be described as SQL Server integration tests.
- Time-dependent code SHOULD use an injected `Clock`.
- Tests MUST NOT depend on production passwords or mutable external state.

## Acceptance evidence

Record Java/Maven versions, commit, SQL Server version, schema scripts, commands,
run ids, row counts and failures injected. A passing unit suite alone is
insufficient for a production release.
