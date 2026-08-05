# Architecture Traceability

**Document ID:** ARCH-024
**Version:** 2.1
**Status:** Current Version 2.0.0 traceability
**Baseline date:** 4 August 2026

---

| Decision/capability | Implementation evidence | Verification |
|---|---|---|
| Packaged plugin manifest | `ClasspathPluginRegistry`, plugin index/properties | catalogue tests |
| Persistent plugin lifecycle registry | `JdbcPluginRegistry`, `core.plugin_registry`, CLI lifecycle commands | registry/CLI tests; live SQL pending |
| Database-backed configuration | registration service and JDBC property source | configuration tests; live registration pending |
| Shared typed property parsing | `PluginPropertyValues`, `ValueParser` | shared and provider configuration tests |
| Shared value validation | `ValidationRules`, `SqlIdentifiers` | boundary, date, duration and identifier tests |
| Shared transaction ownership | `JdbcTransactionTemplate`, `JdbcTransaction`, `JdbcConnectionCleanup` | commit, rollback, suppressed failure and state-restoration tests |
| Shared JDBC batches | `JdbcBatchExecutor`, `JdbcStatementBinder` | batch threshold and result-count tests |
| Shared typed upserts | `JdbcUpsertExecutor`, `JdbcUpsertAdapter`, `JdbcUpsertResult` | insert/update/count tests and Octopus repository tests |
| Ofgem period replacement | `OfgemPersistenceRepository` plus shared transaction/batch infrastructure | focused repository tests; live SQL pending |
| OpenMeteo staging and cleanup | `OpenMeteoRepository`, temporary table, application lock and cleanup callback | focused repository tests; live SQL pending |
| Octopus typed energy adapters | electricity/gas adapters plus generic upsert executor | insert/update/rollback tests; live SQL pending |
| Repeated/all plugin selection | command parser and `PluginSelectionResolver` | parser/selection tests |
| Bounded parallel execution | coordinator and thread factory | coordinator tests |
| Contextual JUL | log context/formatter | focused tests/source review |
| Side-effect-free dry run | no-op audit, unavailable data resource, provider dry-run branches | coordinator and provider dry-run tests |
| Ordered SQL scripts | `/sql`, `core.schema_version`, registry migration | fresh/repeat live install pending |
| Manifest documentation engine | manifests/templates/scripts | documentation validation/build |
| Processing refactor documentation | ARCH-028, ADR-0049, provider architecture/reference and final audit | Documentation Batches 1–2 |

## Required integration evidence

- `mvn clean verify` with the merged Batches 0–3;
- documentation validation and all manifest-driven document builds;
- fresh and repeat SQL installation;
- provider write, rollback, idempotency, archive and permission tests;
- register/list/disable/enable/unregister acceptance;
- repeated/multi/all run and dry-run acceptance; and
- secret/private-key release scan.
