# Current Code Inventory

**Document ID:** ARCH-INVENTORY-001  
**Version:** 2.0  
**Status:** Current uploaded baseline  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

Reviewed against the project archive uploaded on 3 August 2026.

| Area | Current classes/contracts | Status |
|---|---|---|
| Bootstrap | `OpenData`, `OpenDataApplication`, `ExecutionStatus` | Implemented |
| CLI | `CommandLineArguments`, `PluginCommand`, processor and selection resolver | Implemented: repeated/all selection, lifecycle administration, dry run, verbosity and bounded parallelism |
| Bootstrap security | bootstrap loader and RSA password cipher | Implemented; key/credential deployment remediation required |
| Configuration sources | classpath/JDBC sources, external single-plugin registration source and registration service | Implemented |
| Download/discovery | JDK HTTP, Jsoup discovery and strategies | Implemented |
| Parsing | shared CSV/JSON/Excel plus provider-specific PDF/Excel/JSON parsing | Implemented |
| Validation | shared contracts and provider validators | Implemented where required by providers |
| ETL contracts | `ExtractService`, `TransformService`, `LoadService` | Reusable contracts; providers own runtime flow |
| Execution | coordinator, fixed executor, summaries and task context | Implemented |
| Persistence | DBCP resource manager, JDBC configuration/audit and provider repositories | Implemented for SQL Server |
| Logging | manager, formatter and plugin log context | Implemented |
| Exceptions | framework hierarchy plus configuration/database exceptions | Present |
| Plugin registry | packaged classpath catalogue plus persistent `JdbcPluginRegistry` and reflection factory | Implemented |
| Ofgem | HTML discovery, workbook extract/validate, transactional load and archive | Implemented; live acceptance outstanding |
| OpenMeteo | date resolution, JSON API, validation/transform, idempotent load | Implemented; live acceptance outstanding |
| Octopus | local PDF discovery/hash, text extraction, parse, dry-run isolation, transactional load and archive | Implemented; live acceptance outstanding |
| Audit | `core.PluginRun` plus separate ingestion/provenance structures | Implemented but duplicated |
| Scheduling | external scheduling | Internal scheduler deferred |
| UI | startup splash and about dialog | Implemented auxiliary UI |

## Consistency notes

- Maven compiles for Java 17 and declares project version `2.0.0`.
- The runtime entry class is `com.towermarsh.opendata.OpenData`.
- `OpenData` deliberately does not call `System.exit`; logged status is not
  currently an operating-system exit code.
- Packaged definitions are registration sources; installed metadata/status come
  from `core.plugin_registry`, while implementation classes must still exist on
  the runtime classpath.
- Database-backed dry runs need configuration database access during startup,
  then use an unavailable database resource for plugin execution.
- Octopus dry run skips the processed-file ledger and parses all matching input
  PDFs without plugin data writes or archive movement.
- The active database path uses `DatabasePoolConfiguration`,
  `DatabaseResourceManager` and provider repositories. Older similarly named
  pool/connection/repository classes remain and should not be mistaken for the
  active composition.
- Ofgem and OpenMeteo contain some duplicate compatibility classes outside the
  classes imported by their current initialise pipelines.
- Ofgem creates both a generic `core.PluginRun` row and domain provenance rows;
  that identity model still needs consolidation.
- The project currently assumes writable configuration and security files below
  `src/main/resources`, which is not a production packaging design.
- The uploaded archive contains a plaintext bootstrap password and a private PFX
  file. They are release-blocking security findings and must not be copied into
  public or production archives.
- Production readiness must not be claimed until live SQL, permissions,
  rollback, packaging and secret-remediation gates are complete.
