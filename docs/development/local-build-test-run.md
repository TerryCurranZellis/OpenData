# Local Build, Test and Run

**Document ID:** DEV-BUILD-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

## Prerequisites

- JDK 17 or later, compiling with `release=17`;
- Maven 3.9 or later;
- Git;
- NetBeans or another Java IDE;
- SQL Server for write-mode integration tests;
- Windows PowerShell 5.1, Pandoc and PlantUML for the documentation toolchain;
- XeLaTeX plus `rsvg-convert` or Inkscape for PDF output.

## Build

```powershell
mvn clean test
mvn package
```

The unit suite covers CLI parsing, configuration, discovery, download/parsing,
pool setup, repositories and plugin coordination. JDBC mock tests do not prove
that SQL Server scripts, permissions or transactions work on a real server.

## Run

The current POM creates a non-executable library JAR. Configure the IDE to run:

```text
Main class: com.towermarsh.opendata.Main
Working directory: repository root
Arguments: --plugin all --dry-run --parallelism 2
```

Do not publish `java -jar` instructions until `Main-Class` and dependency
packaging are implemented and tested.

## Minimum verification

1. run the full unit suite;
2. list both registered plugins;
3. dry-run each plugin separately;
4. dry-run both with parallelism two;
5. validate and render documentation;
6. for persistence changes, run the SQL Server acceptance matrix.

## Generated and local files

Do not commit passwords, local override files, logs, working downloads, database
backups or generated manuals unless repository policy explicitly identifies an
output as maintained.

### NetBeans command-line arguments

In **Project Properties > Run > Arguments**, enter the arguments directly:

```text
--plugin all --dry-run
```

Do not wrap the complete line in an additional pair of quotes. The parser now tolerates wrappers that nevertheless deliver the whole line as one Java argument, including quoted file paths containing spaces.
