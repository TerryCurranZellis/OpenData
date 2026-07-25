# Package Structure

**Document ID:** ARCH-004  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## Canonical ownership

| Package | Responsibility |
|---|---|
| root / `app` | Entry point, orchestration and run status |
| `cli` | Commons CLI and immutable arguments |
| `config`, `config.model` | Bootstrap/plugin loading and records |
| `plugin`, `plugin.ofgem`, `plugin.openmeteo` | Contracts, registry and source behaviour |
| `download`, `download.strategy` | Download contracts and implementations |
| `parser` | CSV, JSON and Excel parsers |
| `validation` | Validation contracts/results |
| `etl` | Extract, transform and load coordination |
| `database` | JDBC connection and repositories |
| `model` | Framework artefact/result values |
| `logging` | JUL setup |
| `exception` | Framework exceptions |

## Consolidation

The canonical command-line model is under `cli`; an older `app` copy should be
removed after reference checks. The record-based configuration route is the
target; earlier flat loader classes are transitional.

Every public package retains `package-info.java`.

See [package-dependencies.puml](../diagrams/package-dependencies.puml).
