# System Overview

**Document ID:** ARCH-002  
**Version:** 3.0.0  
**Status:** Version 3.0.0 implementation baseline  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Boundary

OpenData is a Java command-line application. It acquires public data over HTTPS
or reads locally supplied source files, transforms each source into typed
records, optionally persists accepted records to SQL Server, and records
plugin-run audit information. It also writes logs, working files and archives to
the local file system.

## External participants

| Participant | Interaction |
|---|---|
| Operator or scheduler | Registers, lists, enables, disables, unregisters or runs plugins |
| Bootstrap properties file | Provides SQL Server connection values and database-backed switch |
| External plugin definition | Supplies one complete named plugin definition during registration only |
| Ofgem | Provides the Energy Price Cap page and workbook |
| Open-Meteo | Provides the historical weather JSON API |
| Octopus statement directory | Provides local PDF statements |
| SQL Server | Stores registry, configuration, run audit and plugin business data |
| File system | Stores logs, working files, input statements and archives |
| Documentation toolchain | Renders PlantUML and publishes manuals |

## Implemented capabilities

- Apache Commons CLI parsing with repeated plugin selection and lifecycle commands;
- packaged classpath registration catalogue and persistent JDBC runtime registry;
- registration of packaged definitions or one complete external definition;
- durable enable/disable/unregister state in `core.plugin_registry`;
- classpath or SQL Server property sources after registration;
- RSA OAEP database-password encryption/decryption;
- bounded parallel execution with per-task log context;
- pooled SQL Server access and plugin-run audit;
- complete Ofgem, OpenMeteo and local-file Octopus pipelines; and
- side-effect-free dry-run execution for all three plugins.

## Important limitations

- Dry run still connects to SQL Server for registry/configuration reads before
  plugin execution switches to an unavailable data-write resource.
- Internal scheduling is not implemented; use an external scheduler.
- `ExecutionStatus` is logged but is not mapped to a process exit code.
- source-tree bootstrap/certificate paths are unsuitable for a read-only install;
- tracked development secrets/private-key material require remediation.

::: {.landscape}
![OpenData system context](../diagrams/generated/system-context.svg){width=22.5cm}
:::
