# 2. Prerequisites and Installation

**Document ID:** USER-002  
**Version:** 3.0.0  
**Status:** Version 3.0.0 operational baseline  
**Baseline date:** 15 August 2026  

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

The working directory is significant because the current bootstrap and
certificate paths are resolved beneath `src/main/resources/config` from
`user.dir`.

## Local directories

Create writable directories for logs, working files and plugin inputs. For
Octopus, define separate input and archive directories and protect them as
personal financial data.

```text
C:\OpenData\logs
C:\Attachments\octopus\incoming
C:\Attachments\octopusrchive
```

## First checks

Help and About do not require SQL Server:

```text
opendata --help
opendata --about
```

`--list-plugins` reads the persistent registry and therefore requires the SQL
schema and valid bootstrap database connection. Continue with
[SQL Server setup](03-sql-server-setup.md) before listing or registering plugins.
