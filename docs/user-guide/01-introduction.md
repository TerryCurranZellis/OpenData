# 1. Introduction

**Document ID:** USER-001  
**Version:** 2.0  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 3 August 2026

---

OpenData is a Java 17 command-line application that extracts data from external
or local sources, transforms it into validated records and loads it into
Microsoft SQL Server.

| Plugin ID | Data source | Input form |
|---|---|---|
| `ofgem` | Ofgem Energy Price Cap | Public HTML page and XLSX workbook |
| `openmeteo` | Open-Meteo archive API | JSON over HTTPS |
| `octopus` | Octopus Energy customer statements | User-supplied local PDF files |

Version 2.0.0 uses a persistent SQL Server plugin registry. Packaged plugin files
are registration sources; only plugins present and enabled in
`core.plugin_registry` can run.

A normal installation sequence is:

1. build and test the application;
2. install the numbered SQL Server scripts, including the plugin-registry migration;
3. replace the development certificate material and bootstrap credential;
4. register packaged plugins with `--plugin all --register`;
5. verify `--list-plugins` and enable/disable the required set;
6. run `--plugin all --dry-run` in a controlled environment;
7. perform write-mode acceptance against a test database; and
8. enable routine execution only after database, log and audit verification.

A custom definition can be registered with
`--plugin <id> --register --file <complete-plugin.properties>`. The `--file`
option is not a per-run override.

The Maven JAR is not currently a verified self-contained executable and does not
declare a `Main-Class`. Use Apache NetBeans or another classpath-aware launcher
with main class `com.towermarsh.opendata.OpenData` and the repository root as the
working directory.

The Java entry point logs an `ExecutionStatus` but does not call `System.exit`,
so numeric status codes are not returned to the operating system.
