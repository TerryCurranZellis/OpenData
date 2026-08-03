# 2. Prerequisites and Installation

**Document ID:** USER-002  
**Version:** 2.0  
**Status:** Version 2.0.0 baseline  
**Baseline date:** 2 August 2026

---

## Required software

- JDK 17 or later;
- Maven 3.9 or later;
- Microsoft SQL Server for registration and database-writing runs;
- Apache NetBeans or another classpath-aware Java launcher;
- PowerShell 5.1 or later for supplied scripts; and
- outbound HTTPS access for Ofgem and OpenMeteo.

Pandoc and PlantUML are required only when rebuilding generated documentation.

## Build

From the repository root:

```powershell
mvn clean verify
mvn package
```

The build creates `target/opendata-2.0.0.jar`. It does not currently include all
runtime dependencies or a `Main-Class` manifest entry. In NetBeans, configure:

```text
Main class: com.towermarsh.opendata.OpenData
Working directory: repository root
```

Keeping the repository root as the working directory allows the development run
to find bootstrap and security resources under `src/main/resources`.

## Local directories

Create writable directories required by the enabled plugins. For Octopus, the
Version 2.0.0 default input directory is:

```text
C:\Attachments\octopus
```

Restrict access because statement files can contain personal and financial data.
Do not place real statements under the repository directory.

## First application check

Before database registration, run `--help` and `--list-plugins` from the configured
launcher. The plugin list should include `ofgem`, `openmeteo` and `octopus`.
Continue with [SQL Server setup](03-sql-server-setup.md) and then the
[quick start](../guides/quick-start.md).
