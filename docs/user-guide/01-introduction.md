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

Version 2.0.0 introduces database-backed application and plugin configuration.
A one-time `--register` command copies the packaged property definitions into SQL
Server and rewrites the local bootstrap file with an encrypted database
password.

A normal installation sequence is:

1. build and test the application;
2. install the numbered SQL Server scripts;
3. replace the development certificate material and bootstrap credential;
4. run `--register` using a protected local override file;
5. restart and verify `--list-plugins`;
6. dry-run Ofgem and OpenMeteo;
7. perform a controlled Octopus write-mode acceptance run against test data; and
8. enable routine execution only after database, log and audit verification.

The Maven JAR is not currently a self-contained executable and does not declare a
`Main-Class`. Use Apache NetBeans or another classpath-aware launcher with main
class `com.towermarsh.opendata.OpenData` and the repository root as the working
directory.

Two current limitations matter operationally:

- the Java entry point logs a status but does not call `System.exit`, so the
  `ExecutionStatus` numeric codes are not returned to the operating system; and
- Octopus dry run fails because its extraction phase reads the processed-file
  ledger through the unavailable dry-run database resource.
