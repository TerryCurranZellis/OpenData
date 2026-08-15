# Ofgem Plugin Reference

**Document ID:** REF-OFGEM-PLUGIN-001
**Version:** 3.0.0  
**Baseline date:** 15 August 2026  

## Public processing types

| Type | Responsibility |
|---|---|
| `OfgemConfiguration` | typed endpoint, file, timeout and archive settings |
| `OfgemPersistenceRepository` | transactional provenance and period replacement |
| `OfgemPersistenceResult` | inserted, updated and skipped counts |

## Shared dependencies

`OfgemConfiguration` uses `PluginPropertyValues` and `ValidationRules`.
`OfgemPersistenceRepository` uses `JdbcTransactionTemplate` and
`JdbcBatchExecutor` with a fixed level batch size of 500.

Provider SQL remains explicit. The shared layer does not determine the Ofgem
business key, period identity, provenance rows or current-period rule.

## Compatibility

These public signatures remain unchanged and are marked `@since 2.0.0`:

```java
OfgemConfiguration.from(PluginDefinition definition)
OfgemConfiguration.downloadPath()
new OfgemPersistenceRepository(DatabaseResourceManager database)
repository.persist(PluginDefinition definition,
                   ResolvedDownload download,
                   OfgemPriceCapWorkbookData data)
```

No public procedure is deprecated by the Ofgem refactor.

## Failure contract

Checked JDBC, file hashing and digest failures are translated by the shared
transaction boundary into `DatabaseAccessException` using the Ofgem failure
message. Runtime failures remain runtime failures. Rollback failure is attached
to the primary failure.
