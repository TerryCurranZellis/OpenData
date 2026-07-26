# Package Structure

**Document ID:** ARCH-004  
**Version:** 1.1  
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
| `plugin` | Provider-neutral contracts, registry, factory, coordinator and audit |
| `plugin.<id>` | Provider workflow facade only |
| `plugin.<id>.config` | Typed provider configuration |
| `plugin.<id>.download`, `.extract` | Provider acquisition and source decoding |
| `plugin.<id>.transform`, `.transform.model`, `.transform.validate` | Provider transformation, records and validation |
| `plugin.<id>.load` | Provider SQL, transactions and load metrics |
| `download`, `download.strategy` | Download contracts and implementations |
| `parser` | CSV, JSON and Excel parsers |
| `validation` | Validation contracts/results |
| `etl` | Extract, transform and load coordination |
| `database` | JDBC connection and repositories |
| `model` | Framework artefact/result values |
| `logging` | JUL setup |
| `exception` | Framework exceptions |

## Plugin-local structure

Ofgem and OpenMeteo now use the same package skeleton beneath their plugin id.
The root facade orders download, extract, transform/validate and load stages.
Provider classes must not be introduced in a parallel top-level package.

The canonical command-line model is under `cli`; the superseded `app` copy has
been removed. `ExecutionStatus` is the single application status model and
exposes operator-facing descriptions.

Every public package retains `package-info.java`.

::: {.landscape}
![OpenData package dependencies](../diagrams/generated/package-dependencies.svg){width=22.5cm}
:::
