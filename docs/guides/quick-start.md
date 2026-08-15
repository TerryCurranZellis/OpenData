# OpenData 3.0.0 Quick Start

**Document ID:** GUIDE-QUICKSTART-001  
**Version:** 3.0.0  
**Status:** Current  
**Baseline date:** 15 August 2026  

---

## 1. Build

```powershell
mvn clean verify
mvn package
```

Use main class `com.towermarsh.opendata.OpenData` with the repository root as
working directory.

## 2. Install SQL Server

Run the numbered scripts in `sql/README.md`, including
`003a-create-plugin-registry.sql` and `007a-create-octopus-schema.sql`.

## 3. Prepare certificate and bootstrap

Replace the development certificate/private key. Before initial registration,
set `application.use-database-properties=false` and a temporary plain database
password in `src/main/resources/config/application.properties`. For a protected
PFX use `-Dopendata.config.keystore.password=<password>`.

## 4. Register and inspect

```text
opendata --plugin all --register
opendata --list-plugins
opendata --plugin ofgem --detail
```

Successful registration persists application configuration, plugin definitions
and registry metadata, encrypts the password and enables database-backed mode.

Use `--plugin <id> --detail` when you want to inspect the stored configuration
for one registered plugin. `--detail` accepts one named plugin only; it does not
use `--execute` and cannot be used with `all`.

## 5. Start the graphical interface

Run with no arguments, or explicitly request the GUI:

```text
opendata
opendata --gui
```

Use the plugin table and menu/toolbar actions for registration, administration,
details, execution, dry-run, logs, settings and Help.

## 6. Dry-run

Execution is explicit. `--plugin` selects what should run. A normal write run is
authorised with `--execute` (or `-x`); a dry-run is authorised independently with
`--dry-run` (or `-n`).

```text
opendata --plugin ofgem --dry-run
opendata --plugin openmeteo --dry-run
opendata --plugin octopus --dry-run
opendata --plugin all --dry-run --parallelism 3
```

Dry run still reads the SQL registry/configuration, but plugin execution creates
no provider writes, generic audit rows or archive movements.

## 7. Administration examples

```text
opendata --plugin octopus --detail
opendata --plugin octopus --disable
opendata --plugin octopus --enable
opendata --plugin octopus --unregister
opendata --plugin octopus --register
```

Administration commands and `--detail` do not use `--execute`. Use `-x` for
execution, `-n` for dry run, and remember that `-d` means disable.

## 8. First write run

Back up SQL Server, then explicitly authorise one plugin at a time:

```text
opendata --plugin ofgem --execute
opendata --plugin openmeteo --execute
opendata --plugin octopus --execute
```

Reconcile logs/audit/provider rows, then test repeated and parallel execution
before routine scheduling.

---
