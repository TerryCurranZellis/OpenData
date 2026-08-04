# OpenData shared validation and persistence refactor — Batch 1

## Scope

Batch 1 migrates the Ofgem plugin to the shared infrastructure introduced by
Batch 0. It intentionally makes no changes to OpenMeteo or Octopus.

This batch was prepared against the repository `main` baseline at commit:

`d352a0015aa46f436512182523fbfffe628c22fa`

Batch 0 must already be installed before applying this archive.

## Changes

### Ofgem configuration

`OfgemConfiguration` now uses:

- `PluginPropertyValues` for text, ISO-8601 duration, and boolean parsing;
- `ValidationRules` for non-blank text and positive durations;
- consistent plugin/property error messages that do not expose property values.

The previous private Ofgem-only helpers (`value`, `duration`, `bool`, and
`requireText`) have been removed. They were private implementation details with
no external callers, so retaining deprecated wrappers would add dead code.

### Ofgem persistence

`OfgemPersistenceRepository` now uses:

- `JdbcTransactionTemplate` for connection borrowing, commit, rollback, and
  restoration of the original auto-commit setting;
- `JdbcBatchExecutor` for writing price-cap level records in batches of 500.

Ofgem-specific SQL, ingestion-run handling, source-file metadata, period
replacement, and generated-key processing remain explicit in the Ofgem
repository.

## Compatibility

The public constructors and method signatures remain unchanged:

- `OfgemConfiguration.from(PluginDefinition)`
- `OfgemConfiguration.downloadPath()`
- `OfgemPersistenceRepository(DatabaseResourceManager)`
- `OfgemPersistenceRepository.persist(...)`

All amended public code is documented with `@since 2.0.0`.

No procedure is deprecated by this batch. Future retained obsolete procedures
must use both the Java `@Deprecated` annotation and a Javadoc `@deprecated` tag.

## Files

The archive replaces two production files and adds two focused tests:

- `src/main/java/com/towermarsh/opendata/plugin/ofgem/initialise/OfgemConfiguration.java`
- `src/main/java/com/towermarsh/opendata/plugin/ofgem/load/OfgemPersistenceRepository.java`
- `src/test/java/com/towermarsh/opendata/plugin/ofgem/initialise/OfgemConfigurationTest.java`
- `src/test/java/com/towermarsh/opendata/plugin/ofgem/load/OfgemPersistenceRepositoryTest.java`

## Installation

From the repository root while on the local refactor branch:

```powershell
Expand-Archive -Path .\OpenData-Refactor-Batch-1-Ofgem.zip `
    -DestinationPath . -Force

git status
git diff --check
mvn clean verify
```

Review the two replaced production files before committing.

Suggested commit command:

```powershell
git add --all
git commit -m "Refactor Ofgem validation and persistence"
```

## Validation performed while preparing the archive

- Java 17 isolated production compilation: passed.
- Ofgem typed-configuration defaults and custom values smoke test: passed.
- Ofgem new-period persistence, transaction commit, and shared batch execution
  smoke test: passed.
- Long-line scan at 120 characters: passed.
- Archive path and duplicate-entry checks: passed.

The complete Maven build must still be run in the real repository so that the
project's Checkstyle, PMD, SpotBugs, JaCoCo, JUnit, and dependency analysis run
against the full source tree.
