# OpenData shared validation and persistence refactor — Batch 2

## Scope

Batch 2 migrates the OpenMeteo plugin to the shared infrastructure introduced
by Batch 0. It intentionally makes no changes to Ofgem or Octopus.

This batch was prepared against the repository `main` baseline at commit:

`d352a0015aa46f436512182523fbfffe628c22fa`

Batch 0 must already be installed before applying this archive. Batch 1 may
remain installed on the same local refactor branch; Batch 2 does not replace any
Ofgem file.

## Changes

### OpenMeteo configuration

`OpenMeteoConfiguration` now uses:

- `PluginPropertyValues` for required text, decimal numbers, integers, booleans,
  optional ISO dates, and timezone parsing;
- `ValidationRules` for text lengths, coordinate ranges, positive durations,
  integer ranges, and date ordering;
- `SqlIdentifiers` for SQL Server schema and table identifiers.

The previous private OpenMeteo-only helpers for required values, defaults,
integers, decimals, booleans, optional dates, text validation, and SQL identifier
validation have been removed.

The public `OpenMeteoConfiguration.sqlIdentifier(...)` procedure has been
retained temporarily for source compatibility. It delegates to
`SqlIdentifiers.requireSafe(...)` and is marked with both:

- `@Deprecated(since = "2.0.0", forRemoval = false)`;
- Javadoc `@deprecated` and `@since 2.0.0` tags.

### OpenMeteo persistence

`OpenMeteoRepository` now uses:

- `JdbcTransactionTemplate` for connection borrowing, commit, rollback, and
  restoration of the original auto-commit setting;
- `JdbcBatchExecutor` for staging daily weather records using the configured
  batch size;
- `SqlIdentifiers.qualify(...)` for schema-qualified SQL Server table names.

The OpenMeteo-specific persistence strategy remains unchanged:

1. remove any previous connection-local staging table;
2. enable `XACT_ABORT`;
3. acquire a transaction-owned SQL Server application lock;
4. upsert the configured location;
5. create and populate `#OpenMeteoDaily`;
6. update changed daily rows using a set-based statement;
7. insert missing daily rows using a set-based statement;
8. remove connection-scoped state before returning the pooled connection.

## Compatibility

The following public signatures remain unchanged:

- `OpenMeteoConfiguration.from(PluginDefinition)`
- `OpenMeteoConfiguration.resolveDateRange(LocalDate)`
- `OpenMeteoConfiguration.sqlIdentifier(String, String)` — now deprecated
- `OpenMeteoRepository(DatabaseResourceManager)`
- `OpenMeteoRepository.save(...)`

All amended public production code is documented with `@since 2.0.0`.

## Files

The archive replaces two production files and two focused tests:

- `src/main/java/com/towermarsh/opendata/plugin/openmeteo/initialise/OpenMeteoConfiguration.java`
- `src/main/java/com/towermarsh/opendata/plugin/openmeteo/load/OpenMeteoRepository.java`
- `src/test/java/com/towermarsh/opendata/plugin/openmeteo/initialise/OpenMeteoConfigurationTest.java`
- `src/test/java/com/towermarsh/opendata/plugin/openmeteo/load/OpenMeteoRepositoryTest.java`

## Installation

From the repository root while on the local refactor branch:

```powershell
Expand-Archive -Path .\OpenData-Refactor-Batch-2-OpenMeteo.zip `
    -DestinationPath . -Force

git status
git diff --check
mvn clean verify
```

Review the two replaced production files before committing.

Suggested commit command:

```powershell
git add --all
git commit -m "Refactor OpenMeteo validation and persistence"
```

## Validation performed while preparing the archive

- Java 17 isolated production compilation with all compiler lint checks: passed.
- OpenMeteo shared typed-configuration smoke test: passed.
- Default date-range and deprecated compatibility procedure checks: passed.
- OpenMeteo staging, set-based update/insert, and transaction smoke test: passed.
- Configured staging batch size test with multiple executions: passed.
- Pooled-session cleanup after transaction completion: passed.
- Java source line-length scan at 120 characters: passed.
- Archive path and duplicate-entry checks: passed.

The complete Maven build must still be run in the real repository so that the
project's Checkstyle, PMD, SpotBugs, JaCoCo, JUnit, and dependency analysis run
against the full source tree and the locally installed Batch 0 and Batch 1 code.
