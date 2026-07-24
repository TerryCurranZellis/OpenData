# Architecture Decision Register

**Document ID:** ADR-REGISTER-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---

| ADR | Decision | Status |
|---|---|---|
| [ADR-0001](ADR-0001-plugin-architecture.md) | Use a plugin architecture | Accepted |
| [ADR-0002](ADR-0002-apache-commons-cli.md) | Use Apache Commons CLI | Accepted |
| [ADR-0003](ADR-0003-java-util-logging.md) | Use java.util.logging | Accepted |
| [ADR-0004](ADR-0004-parameter-file-layering.md) | Layer bootstrap, plugin and runtime configuration | Accepted |
| [ADR-0005](ADR-0005-sql-server-persistence.md) | Use SQL Server as the first persistence target | Accepted |
| [ADR-0006](ADR-0006-package-info-documentation.md) | Document every public package | Accepted |
| [ADR-0007](ADR-0007-modular-monolith.md) | Use a modular monolith | Accepted |
| [ADR-0008](ADR-0008-java-17-records-immutable-models.md) | Support Java 17 minimum and use records | Accepted |
| [ADR-0009](ADR-0009-common-staged-etl-pipeline.md) | Use a common staged ETL pipeline | Accepted |
| [ADR-0010](ADR-0010-constructor-injection-no-di-framework.md) | Use constructor injection without a DI framework | Accepted |
| [ADR-0011](ADR-0011-jdk-http-client-downloads.md) | Use JDK HTTP and streamed downloads | Accepted |
| [ADR-0012](ADR-0012-plugin-properties-before-database-configuration.md) | Use properties-based plugin definitions in Phase 1 | Accepted |
| [ADR-0013](ADR-0013-plugin-manifest-and-registry.md) | Use an explicit classpath plugin index | Accepted |
| [ADR-0014](ADR-0014-framework-metadata-and-plugin-business-tables.md) | Separate framework metadata and business tables | Proposed |
| [ADR-0015](ADR-0015-database-abstraction-sql-server-first.md) | Abstract database contracts, implement SQL Server first | Accepted |
| [ADR-0016](ADR-0016-exception-hierarchy-and-boundary-translation.md) | Translate exceptions at boundaries | Accepted |
| [ADR-0017](ADR-0017-documentation-as-code.md) | Maintain documentation as code | Accepted |
| [ADR-0018](ADR-0018-minimal-dependencies-standard-java-first.md) | Prefer standard Java but approve specialist libraries | Accepted |
| [ADR-0019](ADR-0019-ofgem-reference-plugin.md) | Use Ofgem as HTML-to-Excel reference | Accepted |
| [ADR-0020](ADR-0020-internal-scheduling-deferred.md) | Defer internal scheduling | Deferred |
| [ADR-0021](ADR-0021-apache-commons-csv.md) | Use Apache Commons CSV | Accepted |
| [ADR-0022](ADR-0022-jsoup-html-link-discovery.md) | Use JSoup for static HTML discovery | Accepted |
| [ADR-0023](ADR-0023-apache-poi-excel.md) | Use Apache POI for XLS and XLSX | Accepted |
| [ADR-0024](ADR-0024-credential-references-not-secrets.md) | Store credential references, not secrets | Accepted |
| [ADR-0025](ADR-0025-database-plugin-configuration-json.md) | Move plugin definitions to database JSON later | Shelved |
| [ADR-0026](ADR-0026-openmeteo-reference-plugin.md) | Use OpenMeteo as API reference plugin | Accepted |


ADR-0014 remains proposed, ADR-0020 deferred and ADR-0025 shelved. New ADRs
continue from ADR-0027; accepted records are not renumbered.
