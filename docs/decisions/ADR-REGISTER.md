# Architecture Decision Register

**Document ID:** ADR-REGISTER-001
**Version:** 3.1.0
**Status:** Baseline
**Baseline date:** 13 August 2026
**Minimum Java version:** 24

---

::: {.landscape-table}

*Architecture decision register.*

| Record | Decision | Status | Note |
|---|---|---|---|
| [ADR-0001](ADR-0001-plugin-architecture.md) | Use a plugin architecture | Accepted | |
| [ADR-0002](ADR-0002-apache-commons-cli.md) | Use Apache Commons CLI | Accepted | |
| [ADR-0003](ADR-0003-java-util-logging.md) | Use `java.util.logging` | Accepted | |
| [ADR-0004](ADR-0004-parameter-file-layering.md) | Layer bootstrap, plugin and runtime configuration | Accepted | |
| [ADR-0005](ADR-0005-sql-server-persistence.md) | Use SQL Server as the first persistence target | Accepted | |
| [ADR-0006](ADR-0006-package-info-documentation.md) | Document every public package | Accepted | |
| [ADR-0007](ADR-0007-modular-monolith.md) | Use a modular monolith | Accepted | |
| [ADR-0008](ADR-0008-java-17-records-immutable-models.md) | Support Java 17 and immutable records | Accepted | Java 17 minimum superseded by ADR-0052; immutable-model guidance retained |
| [ADR-0009](ADR-0009-common-staged-etl-pipeline.md) | Use a common staged ETL pipeline | Accepted | |
| [ADR-0010](ADR-0010-constructor-injection-no-di-framework.md) | Use constructor injection without a DI framework | Accepted | |
| [ADR-0011](ADR-0011-jdk-http-client-downloads.md) | Use JDK HTTP and streamed downloads | Accepted | |
| [ADR-0012](ADR-0012-plugin-properties-before-database-configuration.md) | Use properties-based plugin definitions in Phase 1 | Accepted | |
| [ADR-0013](ADR-0013-plugin-manifest-and-registry.md) | Use an explicit classpath plugin index | Accepted with amended role | Packaged registration catalogue; ADR-0048 owns persistent runtime status |
| [ADR-0014](ADR-0014-framework-metadata-and-plugin-business-tables.md) | Separate framework metadata and plugin business tables | Accepted | Implemented in Phase 3 |
| [ADR-0015](ADR-0015-database-abstraction-sql-server-first.md) | Abstract database contracts, implement SQL Server first | Accepted | |
| [ADR-0016](ADR-0016-exception-hierarchy-and-boundary-translation.md) | Translate exceptions at boundaries | Accepted | |
| [ADR-0017](ADR-0017-documentation-as-code.md) | Maintain documentation as code | Accepted | |
| [ADR-0018](ADR-0018-minimal-dependencies-standard-java-first.md) | Prefer standard Java but approve specialist libraries | Accepted | |
| [ADR-0019](ADR-0019-ofgem-reference-plugin.md) | Use Ofgem as the HTML-to-Excel reference plugin | Accepted | |
| [ADR-0020](ADR-0020-internal-scheduling-deferred.md) | Defer internal scheduling | Deferred | External scheduling preferred |
| [ADR-0021 CSV](ADR-0021-apache-commons-csv.md) | Use Apache Commons CSV | Accepted | Legacy duplicate number |
| [ADR-0021 Config](ADR-0021-configuration-resolution-and-validation.md) | Separate configuration resolution and validation | Accepted | Legacy duplicate number |
| [ADR-0022 HTML](ADR-0022-jsoup-html-link-discovery.md) | Use Jsoup for static HTML discovery | Accepted | Legacy duplicate number |
| [ADR-0022 CLI](ADR-0022-cli-control-commands-and-exit-codes.md) | Keep CLI control commands at the application boundary | Accepted | Legacy duplicate number |
| [ADR-0023 Excel](ADR-0023-apache-poi-excel.md) | Use Apache POI for XLS and XLSX | Accepted | Legacy duplicate number |
| [ADR-0023 Parsers](ADR-0023-format-parser-adapters.md) | Provide common CSV and JSON parser adapters | Accepted | Legacy duplicate number |
| [ADR-0024 Credentials](ADR-0024-credential-references-not-secrets.md) | Store credential references, not secrets | Accepted | Legacy duplicate number |
| [ADR-0024 IMAP](ADR-0024-imap-email-attachment-source.md) | Use IMAP as a reusable attachment source | Shelved | Legacy duplicate number; future work |
| [ADR-0025 Config DB](ADR-0025-database-plugin-configuration-json.md) | Move plugin definitions to database JSON later | Shelved | Legacy duplicate number |
| [ADR-0025 Octopus](ADR-0025-octopus-email-bill-plugin.md) | Introduce the Octopus email bill plugin | Shelved | Legacy duplicate number; future work |
| [ADR-0026 OpenMeteo](ADR-0026-openmeteo-reference-plugin.md) | Use OpenMeteo as the API reference plugin | Accepted | Legacy duplicate number |
| [ADR-0026 Octopus TX](ADR-0026-transactional-octopus-record-persistence.md) | Persist one Octopus statement batch atomically | Accepted | Implemented; legacy duplicate number |
| [ADR-0027](ADR-0027-idempotent-email-attachment-processing.md) | Make email attachment processing idempotent | Accepted | Future work |
| [ADR-0028](ADR-0028-openmeteo-historical-weather-plugin.md) | Integrate historical OpenMeteo acquisition | Accepted | Runtime and persistence implemented |
| [ADR-0029](ADR-0029-web-file-discovery-and-tabular-parsing.md) | Use shared discovery and tabular parsing | Accepted | Implemented in Phase 2 |
| [ADR-0030](ADR-0030-managed-database-connection-pool.md) | Use a managed Apache DBCP connection pool | Accepted | Implemented in Phase 3 |
| [ADR-0031](ADR-0031-normalised-ofgem-price-cap-schema.md) | Store a dimensional annual Ofgem cap fact | Accepted | Implemented in Phase 3 |
| [ADR-0032](ADR-0032-explicit-jdbc-repositories.md) | Use explicit JDBC repositories rather than an ORM | Accepted | Implemented in Phase 3 |
| [ADR-0033](ADR-0033-ordered-idempotent-sql-scripts.md) | Manage schema with ordered idempotent SQL scripts | Accepted | Implemented in Phase 3 |
| [ADR-0034](ADR-0034-ingestion-audit-and-source-provenance.md) | Audit every ingestion run and source file | Accepted | Implemented foundation |
| [ADR-0035](ADR-0035-transactional-period-replacement.md) | Replace one Ofgem period in one transaction | Accepted | Implemented in Phase 3 |
| [ADR-0036](ADR-0036-least-privilege-application-role.md) | Use a least-privilege application role | Accepted | Implemented in SQL scripts |
| [ADR-0037](ADR-0037-preserve-source-cell-lineage.md) | Preserve workbook worksheet and cell lineage | Accepted | Implemented in Ofgem facts |
| [ADR-0038](ADR-0038-bounded-parallel-plugin-execution.md) | Use bounded parallel plugin execution | Accepted | Implemented |
| [ADR-0039](ADR-0039-database-concurrency-owned-by-transactions.md) | Coordinate database concurrency through transactions | Accepted | Implemented |
| [ADR-0040](ADR-0040-openmeteo-relational-storage.md) | Store OpenMeteo data in dedicated relational tables | Accepted | Implemented |
| [ADR-0041](ADR-0041-contextual-jul-for-concurrent-plugins.md) | Add task context to `java.util.logging` | Accepted | Implemented |
| [ADR-0042](ADR-0042-side-effect-free-dry-run.md) | Keep dry runs free of persistent side effects | Accepted | Implemented |
| [ADR-0043](ADR-0043-plugin-local-pipeline-packages.md) | Organise provider code as plugin-local pipeline packages | Accepted | Implemented |
| [ADR-0044](ADR-0044-octopus-energy.md) | Process local Octopus Energy statement PDFs | Accepted | Write and dry-run paths implemented; live acceptance pending |
| [ADR-0045](ADR-0045-documentation-delivery-baseline.md) | Standardise the documentation delivery baseline | Accepted | Implemented |
| [ADR-0046](ADR-0046-manifest-driven-documentation-engine.md) | Use a manifest-driven documentation engine | Accepted | Implemented |
| [ADR-0047](ADR-0047-database-backed-configuration-registration.md) | Register configuration in SQL Server and bootstrap from encrypted file settings | Accepted | Implemented for version 2 bootstrap |
| [ADR-0048](ADR-0048-persistent-plugin-registry-and-cli-lifecycle.md) | Persist plugin lifecycle state and administer it through the CLI | Accepted | Implemented; extends ADR-0013/0047 |
| [ADR-0049](ADR-0049-shared-validation-and-jdbc-execution.md) | Share validation and JDBC execution mechanics | Accepted | Implemented across Ofgem, OpenMeteo and Octopus |
| [ADR-0050](ADR-0050-application-metadata-validation-and-utilities.md) | Centralise application metadata, validation and focused utilities | Accepted | Implemented in version 2.1 |

| [ADR-0051](ADR-0051-javafx-graphical-interface.md) | Use JavaFX for the OpenData graphical interface | Accepted | Version 3 GUI implementation in progress |
| [ADR-0052](ADR-0052-java-24-javafx-lifecycle-and-swing-retirement.md) | Use Java 24 minimum and let JavaFX own the desktop lifecycle | Accepted | JavaFX 26.x baseline; Swing retirement staged |
| [ADR-0053](ADR-0053-javafx-controller-service-boundary.md) | Keep JavaFX controllers behind asynchronous application-service boundaries | Accepted | Batch 3 persistent plugin-table integration |

:::
