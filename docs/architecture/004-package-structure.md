# Package Structure

**Document ID:** ARCH-004  
**Version:** 3.1.0  
**Status:** Current implementation and target ownership  
**Baseline date:** 18 September 2026  
**Minimum Java version:** 24

---

## Canonical ownership

| Module | Package / area | Responsibility |
|---|---|---|
| `opendata-api` | `config.model` | Immutable plugin-definition, endpoint, and credential records |
| `opendata-api` | `database` | Shared database resource contracts and pool snapshots |
| `opendata-api` | `plugin` | Shared plugin contracts and immutable run metadata |
| `opendata-common` | `database`, `database.jdbc` | Shared database exception types and reusable JDBC execution helpers |
| `opendata-common` | `download.strategy` | Shared download-resolution strategies |
| `opendata-common` | `exception` | Shared exception types |
| `opendata-common` | `logging`, `util` | Shared plugin log context and reusable utility helpers |
| `opendata-common` | `validation` | Typed property parsing and reusable validation rules |
| `opendata-plugins` | `plugin.<id>` | Provider workflow facade and staged pipeline implementation |
| `opendata-core` | `plugin` | Provider-neutral registries, execution coordinator, audit, and factories |
| `opendata-core` | root / `app` | Entry point, orchestration and run status |
| `opendata-core` | `cli` | Commons CLI and immutable arguments |
| `opendata-core` | `config`, `config.model` | Bootstrap, registration, property sources, and runtime configuration |
| `opendata-core` | `download`, `discovery`, `parser`, `etl` | Shared acquisition, selection, parsing, and staged pipeline contracts |
| `opendata-core` | `database.audit` | Ingestion audit persistence |
| `opendata-core` | `gui` | JavaFX lifecycle, controllers, dialogs, and live log presentation |
| `opendata-core` | `logging`, `model` | Shared runtime support owned by the executable application |

## Package documentation rule

Every production package containing classes must have `package-info.java` in its
owning module. Package pages must list only the types compiled from that module;
module splits must therefore update both the source layout and the package
Javadocs together.
