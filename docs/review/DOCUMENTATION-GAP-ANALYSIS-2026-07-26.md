# Documentation Gap Analysis

**Document ID:** REVIEW-GAP-20260726  
**Version:** 1.2  
**Status:** Current  
**Baseline date:** 26 July 2026  
**Reviewed source:** commit `c2adae5` plus the current documentation/refactoring branch  
**Minimum Java version:** 17

---

## Purpose

This review records gaps found before the documentation baseline was updated.
It distinguishes documentation defects that are corrected in this batch from
implementation and verification work that documentation cannot resolve.

## Gaps requiring implementation decisions

::: {.landscape-table}

*Outstanding implementation and specification gaps.*

| Priority | Gap | Evidence | Required resolution |
|---|---|---|---|
| Critical | Two audit models represent one Ofgem execution | `PluginExecutionCoordinator` writes `core.PluginRun`; `OfgemPersistenceRepository` separately writes `core.ingestion_run` | Select one canonical run identity and link source-file/domain provenance to it |
| Critical | A database password is present in classpath configuration | `config/application.properties` contains `database.password=OpenData` | Remove the value, require an external secret and rotate any password that has been used |
| High | A second, unused application configuration contains obsolete database names, paths, credentials and a different licence header | `src/main/resources/application.properties`; runtime loads `config/application.properties` | Remove or quarantine the legacy resource after confirming no external consumer uses it |
| High | Maven does not create an executable application JAR | No `Main-Class`, dependency copy or shade/assembly configuration in `pom.xml` | Choose and configure the packaging model before publishing `java -jar` commands |
| High | Logged application status is not mapped to the operating-system exit code | `Main` records `ExecutionStatus` but deliberately does not call `System.exit` | Decide whether a launcher or `System.exit` boundary owns exit-code mapping |
| High | Live SQL Server acceptance is not recorded | Unit tests mock JDBC; review files contain no clean-install/write/rollback evidence | Run the acceptance matrix and retain results |
| Medium | Ofgem maintains both a plugin-level audit and a domain ingestion audit | The two rows use different identifiers and status vocabularies | Resolve with the canonical audit decision rather than documenting both as permanent |
| Medium | SQL is split between `sql/` and `sql/sqlserver/` with overlapping core concerns | `001-core-plugin-run.sql` and `sqlserver/010-create-core-schema.sql` | Publish one ordered installation manifest or migration tool |
| Medium | Two database configuration generations remain | Runtime uses `DatabasePoolConfiguration` and `DatabaseResourceManager`; older `DatabasePoolConfig`, `DatabaseConnectionManager` and generic repository abstractions remain | Select the supported API, migrate callers and remove or label compatibility classes |
| Medium | The pool implementation conflicts with ADR-0030 | ADR-0030 rejects a registered-driver singleton; current `SQLServerResource` is a singleton using DBCP `PoolingDriver` | Change the implementation or record a superseding lifecycle decision |
| Medium | Generic ETL stage interfaces are not the runtime coordinator | Plugins implement their concrete sequence directly | Retain as extension contracts or introduce a common pipeline in a later ADR |
| Medium | Detailed Ofgem component-value tables are provisioned but not loaded | SQL creates component tables; current extractor loads annual cap levels | Keep clearly reserved until extraction rules and tests exist |
| Medium | The database URL trusts the server certificate | Classpath development configuration sets `trustServerCertificate=true` | Use a trusted certificate and `trustServerCertificate=false` outside local development |
| Medium | Active plugin download paths are not size-bounded | The reusable `HttpDataDownloader` has a limit, but Ofgem uses `DirectHttpDownloadStrategy` and OpenMeteo buffers a string response | Add configurable response limits and failure tests to both active plugins |
| Medium | Java source licence headers are inconsistent | 14 of 169 main-source files lack an Apache-2.0 SPDX marker; four retain the earlier “All rights reserved” header | Confirm licensing intent and normalise headers without altering attribution |
| Low | Several formats and strategies are modelled but not executable | Enums include XML, ZIP, HTML table and browser automation; the parser factory supports CSV, Excel and JSON | Keep the capability matrix explicit or implement the missing adapters |
| Low | Package rules are not automatically enforced | Dependency rules are documentation conventions; no ArchUnit gate exists | Add an architecture test suite if the rules become release gates |
| Low | Generic parser rows remain string maps | Parser adapters return `List<Map<String,String>>` | Define a typed row/table model before plugins depend on shared typed transformations |
| Low | Internal scheduling is absent | ADR-0020 is Deferred | Continue using an external scheduler until a new decision supersedes ADR-0020 |

:::

## Documentation defects corrected in this batch

- stale statements that plugin execution, Ofgem orchestration, run identifiers,
  OpenMeteo persistence and concurrent logging were still pending;
- incomplete command-line documentation for repeated/comma-separated plugins,
  `all`, `--parallelism` and dry-run behaviour;
- references to the Maven JAR as already executable;
- direct Markdown links to `.puml` files rather than rendered SVG images;
- duplicate PlantUML files outside `docs/diagrams/source`;
- duplicate ADR numbers for concurrency and OpenMeteo storage;
- missing development, standards, operations, roadmap and user-guide sections;
- incomplete guide/reference indexes;
- duplicate and stale plugin registry/properties references;
- testing claims that described mocked JDBC tests as database integration;
- security claims that overlooked tracked credentials and unbounded active
  download paths;
- a documentation build that merged only one technical manual and omitted
  operations, plugins and the user guide.
- provider code split across a top-level Ofgem package and flat plugin packages;
- the disconnected Ofgem import/repository generation;
- unused `app.CommandLineArguments` and `ApplicationRunStatus`;
- enum-name-only application status logging;
- the misspelled OpenMeteo HTTP user-agent identifier;
- absence of a maintained Java package template for new plugins.

## Verification still required

The documentation link, structure, PlantUML and rendered-output checks passed
in this workspace. Product acceptance additionally requires:

- `mvn clean test` on Java 17;
- clean and repeat SQL Server installation;
- Ofgem and OpenMeteo dry runs;
- successful database-writing runs for both plugins;
- repeated OpenMeteo load proving zero changes for identical input;
- repeated Ofgem period load proving atomic replacement;
- induced failure proving rollback and terminal audit status;
- permission testing with the application principal;
- executable-package and exit-code tests after those gaps are implemented.

Full Maven and PowerShell execution remain toolchain gaps rather than completed
acceptance evidence. See the separate
[unresolved toolchain and specification summary](UNRESOLVED-TOOLCHAIN-AND-SPECIFICATION-GAPS-2026-07-26.md)
for the exact checks completed and still required.

## Documentation authority

When a historical phase document conflicts with the current source, this review,
the [current code inventory](../architecture/CURRENT-CODE-INVENTORY.md), current
reference documents and source code take precedence. Historical phase files
remain for traceability.
