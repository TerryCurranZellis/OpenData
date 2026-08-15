# Octopus Plugin Reference

**Document ID:** REF-OCTOPUS-PLUGIN-001
**Version:** 3.0.0  
**Baseline date:** 15 August 2026  

## Public processing types

| Type | Responsibility |
|---|---|
| `OctopusConfiguration` | required input, working and archive paths |
| `OctopusPersistenceRepository` | atomic energy-row and statement-ledger persistence |
| `OctopusPersistenceResult` | combined inserted, updated and skipped counts |

## Internal typed adapters

| Type | Responsibility |
|---|---|
| `AbstractOctopusUpsertAdapter<T>` | common prepared-statement adapter mechanics |
| `ElectricityRecordUpsertAdapter` | electricity key, SQL and bindings |
| `GasRecordUpsertAdapter` | gas key, SQL and bindings |

The adapters implement the shared `JdbcUpsertAdapter<T, UUID>` contract and are
executed by `JdbcUpsertExecutor`. They are provider implementation details, not
cross-plugin domain models.

## Transaction contract

`OctopusPersistenceRepository.save(...)` executes electricity records, gas
records and statement-file `MERGE` operations inside one
`JdbcTransactionTemplate` transaction. Any SQL or runtime failure prevents the
batch from committing.

## Compatibility

The public repository constructor and `save(OctopusParseResult, UUID)` signature
are unchanged and documented with `@since 2.0.0`. The removed path helper was
private, so no deprecated wrapper is provided.
