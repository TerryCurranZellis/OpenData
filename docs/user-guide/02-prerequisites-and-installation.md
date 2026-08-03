# 2. Prerequisites and Installation

**Document ID:** USER-002  
**Version:** 2.0  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 3 August 2026

---

## Required software

- JDK 17 or later;
- Maven 3.9 or later;
- Microsoft SQL Server;
- Apache NetBeans or another classpath-aware Java launcher;
- PowerShell 5.1 or later for the supplied support scripts; and
- outbound HTTPS access for Ofgem and OpenMeteo.

Pandoc and PlantUML are required only when rebuilding generated documentation.

## Build

From the repository root:

```powershell
mvn clean verify
mvn package
```

The build produces `target/opendata-2.0.0.jar`, but the JAR does not contain all
runtime dependencies and has no configured `Main-Class`. Configure the launcher
with:

```text
Main class: com.towermarsh.opendata.OpenData
Working directory: repository root
```

The working directory is significant. The current implementation reads and
writes bootstrap and certificate files beneath `src/main/resources/config` by
resolving them from `user.dir`.

## Local directories

Create writable directories for logs, working files and plugin inputs. For
Octopus, define separate input and archive directories. Do not leave
`archive.directory` blank: a blank value becomes the process working directory.
The configured `working.directory` is currently parsed but not used.

A typical local layout is:

```text
C:\OpenData\logs
C:\Attachments\octopus\incoming
C:\Attachments\octopus\archive
```

Restrict the Octopus directories because statements contain personal and
financial data.

## First checks

Before registration, run:

```text
opendata --help
opendata --list-plugins
```

`opendata` in this guide means the configured classpath-aware launcher. The list
should contain `ofgem`, `openmeteo` and `octopus`. Continue with
[SQL Server setup](03-sql-server-setup.md).
