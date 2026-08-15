# OpenMeteo Plugin Reference

**Document ID:** REF-OPENMETEO-PLUGIN-001
**Version:** 3.0.0  
**Baseline date:** 15 August 2026  

## Public processing types

| Type | Responsibility |
|---|---|
| `OpenMeteoConfiguration` | typed API, location, date and SQL settings |
| `OpenMeteoRepository` | locked, staged and set-based persistence |
| `OpenMeteoPersistenceResult` | inserted, updated and skipped counts |

## Shared dependencies

Configuration uses `PluginPropertyValues`, `ValidationRules` and
`SqlIdentifiers`. Persistence uses `JdbcTransactionTemplate`,
`JdbcBatchExecutor` and a `JdbcConnectionCleanup` callback.

## Deprecated compatibility procedure

```java
@Deprecated(since = "2.0.0", forRemoval = false)
OpenMeteoConfiguration.sqlIdentifier(String value, String name)
```

The method delegates to `SqlIdentifiers.requireSafe(...)`. It is retained for
source compatibility and also carries Javadoc `@deprecated` and
`@since 2.0.0`. New code must call `SqlIdentifiers` directly.

## Repository result rules

- an empty input list returns zero counts without borrowing a connection;
- staged row count must equal input record count;
- changed rows are updated;
- missing rows are inserted; and
- remaining rows are reported as skipped.

The cleanup callback removes connection-scoped temporary state before the
connection returns to the pool.
