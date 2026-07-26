# 2. Prerequisites and Installation

**Document ID:** USER-002  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

## Required software

- JDK 17 or later;
- Maven;
- SQL Server for database-writing runs;
- NetBeans or another classpath-aware Java launcher;
- outbound HTTPS access to Ofgem and Open-Meteo.

## Build

From the repository root:

```powershell
mvn clean test
mvn package
```

The current `opendata-1.0.0.jar` is not a self-contained executable. In NetBeans,
set the main class to `com.towermarsh.opendata.Main` and the working directory to
the repository root.

## First check

Run the application with `--list-plugins`. The output should show `ofgem` and
`openmeteo` as enabled. Then dry-run each plugin before configuring database
writes.
