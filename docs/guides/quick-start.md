# OpenData 2.0.0 Quick Start

**Document ID:** GUIDE-QUICKSTART-001  
**Version:** 2.1  
**Status:** Current  
**Baseline date:** 7 August 2026

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
```

Successful registration persists application configuration, plugin definitions
and registry metadata, encrypts the password and enables database-backed mode.

## 5. Dry-run

Execution is explicit. `--plugin` selects what should run; `--Execute` (or `-x`)
authorises the run.

```text
opendata --plugin ofgem --Execute --dry-run
opendata --plugin openmeteo --Execute --dry-run
opendata --plugin octopus --Execute --dry-run
opendata --plugin all --Execute --dry-run --parallelism 3
```

Dry run still reads the SQL registry/configuration, but plugin execution creates
no provider writes, generic audit rows or archive movements.

## 6. Administration examples

```text
opendata --plugin octopus --disable
opendata --plugin octopus --enable
opendata --plugin octopus --unregister
opendata --plugin octopus --register
```

Administration commands do not use `--Execute`. Use `-x` for execution,
`-n` for dry run, and remember that `-d` means disable.

## 7. First write run

Back up SQL Server, then explicitly authorise one plugin at a time:

```text
opendata --plugin ofgem --Execute
opendata --plugin openmeteo --Execute
opendata --plugin octopus --Execute
```

Reconcile logs/audit/provider rows, then test repeated and parallel execution
before routine scheduling.
