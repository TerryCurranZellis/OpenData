# System Overview

**Document ID:** ARCH-002  
**Version:** 2.0  
**Status:** Version 2.0.0 implementation baseline  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

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
| Operator or scheduler | Runs control commands or selects one or more plugins |
| Bootstrap properties file | Provides database connection values and the database-backed-configuration switch |
| Optional override file | Supplies invocation-specific application or plugin values |
| Ofgem | Provides the Energy Price Cap publication page and workbook |
| Open-Meteo | Provides the historical weather JSON API |
| Octopus statement directory | Provides local PDF statements named using the supported convention |
| SQL Server | Stores configuration, run audit and plugin-owned business data |
| File system | Stores logs, working files, input statements and archives |
| Documentation toolchain | Renders PlantUML and publishes Markdown, DOCX or PDF outputs |

## Implemented capabilities

The Version 2.0.0 baseline implements:

- Apache Commons CLI parsing and control commands;
- an explicit classpath plugin index and reflection-based plugin construction;
- classpath or SQL Server property sources with invocation overrides;
- `--register`, which copies application and plugin properties into SQL Server
  and rewrites the bootstrap file for subsequent database-backed runs;
- RSA OAEP encryption and decryption for the database password;
- bounded parallel plugin execution with per-task log context;
- pooled SQL Server access and plugin-run audit;
- complete Ofgem and OpenMeteo pipelines and an implemented Octopus write-mode
  pipeline for local statements;
- side-effect-free dry-run infrastructure for plugin execution. The current
  Octopus extractor is an exception because it still queries its processed-file
  ledger during a dry run.

## Important limitations

- The plugin registry itself remains an explicit classpath index; only property
  values are database-backed.
- A dry run still needs SQL Server during startup when
  `application.use-database-properties=true`, because runtime and plugin
  definitions are read before the application swaps to an unavailable database
  resource for plugin execution.
- Octopus dry run is currently defective: `OctopusExtract` queries
  `octopus.statement_file` even when `context.dryRun()` is true, so it requests a
  connection from the unavailable dry-run resource and fails. This requires a
  Java fix; documentation must not present `--plugin octopus --dry-run` as
  operational.
- Internal scheduling is not implemented; use an external scheduler.
- The process logs an `ExecutionStatus` but does not call `System.exit`, so a
  non-success application status is not currently mapped to a non-zero process
  exit code.
- Source-tree bootstrap and certificate paths are writable implementation
  assumptions and are unsuitable for an installed, read-only application image.
- The uploaded baseline contains sensitive bootstrap material that must be
  removed or replaced before a public or production release.

## Design constraint

Dataset URLs, mapping rules, source paths and SQL statements belong in
configuration or plugin-owned components. They must not be hard-coded in the
`OpenData` entry point or generic application coordinator.

::: {.landscape}
![OpenData system context](../diagrams/generated/system-context.svg){width=22.5cm}
:::
