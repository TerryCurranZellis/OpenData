# Testing Standard

**Document ID:** STD-TEST-001  
**Version:** 3.0.0  
**Status:** Version 3.0.0 engineering baseline  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Test levels

| Level | Purpose | Typical environment |
|---|---|---|
| Unit | Value objects, parsing, selection, validation and failure paths | JUnit/Mockito, no network or database |
| Component | Complete plugin flow with controlled files, HTTP clients or repository substitutes | Local deterministic fixtures |
| Integration | Real SQL Server schema, permissions, transactions and pooling | Isolated database |
| Acceptance | End-to-end registration and plugin runs using representative sources | Release-like environment |

Mock JDBC tests are unit or component tests, not SQL Server integration tests.

## Required coverage by change type

### Plugin configuration

Test required properties, defaults, invalid values, endpoint selection and
case-normalised property names.

### Extract and parser code

Use representative source fixtures and malformed boundary cases. Cover HTTP
status errors, interruption, file-size limits, missing headings, unusual
encodings, formula cells, empty datasets and duplicate records where relevant.

### Load and repository code

Cover commit, rollback, repeat load, unchanged rows, partial failure,
idempotency, accurate metrics and restoration of pooled connection state.
Database constraints and transaction locks require real SQL Server tests.

### Parallel execution

Prove actual overlap, bounded worker count, result ordering, failure isolation,
interruption handling and no shared JDBC connection.

### Dry run

A dry run MUST prove absence of provider-table writes, run-audit rows, archive
moves and other persistent side effects. Registry and configuration reads are
permitted before execution. During plugin execution, OpenData supplies an
unavailable provider-data resource and no-op audit service. Octopus additionally
skips its processed-file ledger, and its regression test proves that dry-run
extract does not request a database connection.

## Determinism

Time-dependent code SHOULD use the `Clock` from
`PluginExecutionContext`. Tests MUST NOT depend on production passwords,
customer statements, mutable public endpoints or a developer's working
directory.

## Regression and evidence

Every defect fix MUST include a focused regression test where practical. Release
evidence records the commit, Java/Maven versions, SQL Server version, installed
scripts, commands, run IDs, row counts, injected failures and resulting
transaction state.

The current Maven build produces JaCoCo reports but does not enforce a coverage
threshold. Review changed-code coverage rather than treating report generation
as a quality gate.
