# Architecture Traceability

**Document ID:** ARCH-024  
**Version:** 2.0  
**Status:** Current Version 2.0.0 traceability  
**Baseline date:** 3 August 2026

---

| Decision/capability | Implementation evidence | Verification |
|---|---|---|
| Packaged plugin manifest | `ClasspathPluginRegistry`, plugin index/properties | catalogue tests |
| Persistent plugin lifecycle registry | `JdbcPluginRegistry`, `core.plugin_registry`, CLI lifecycle commands | registry/CLI tests; live SQL pending |
| Database-backed configuration | registration service and JDBC property source | configuration tests; live registration pending |
| Repeated/all plugin selection | command parser and `PluginSelectionResolver` | parser/selection tests |
| Bounded parallel execution | coordinator and thread factory | coordinator tests |
| Contextual JUL | log context/formatter | focused tests/source review |
| Side-effect-free dry run | no-op audit, unavailable data resource, provider dry-run branches | coordinator and Octopus dry-run tests |
| Local-file Octopus plugin | PDF extract/parser/repository/archive | extractor/parser tests; live persistence pending |
| Ordered SQL scripts | `/sql`, `core.schema_version`, registry migration | fresh/repeat live install pending |
| Manifest documentation engine | manifests/templates/scripts | documentation validation/build |

## Required integration evidence

- `mvn clean verify` with the declared dependency graph;
- fresh and repeat SQL installation including `003a`;
- register/list/disable/enable/unregister acceptance;
- repeated/multi/all run and dry-run acceptance;
- provider write, rollback, idempotency and permission tests;
- secret/private-key release scan.
