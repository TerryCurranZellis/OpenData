# OpenData refactor — Batch 3: Octopus

## Purpose

Batch 3 completes the shared validation and persistence refactor by migrating the
Octopus plugin to the Batch 0 infrastructure.

This archive is intended to be extracted into the root of the local OpenData
repository after Batches 0, 1, and 2 have been applied.

Repository baseline inspected: `TerryCurranZellis/OpenData` `main`, commit
`d352a0015aa46f436512182523fbfffe628c22fa`.

## Changes

### Configuration

`OctopusConfiguration` now uses `PluginPropertyValues.requiredPath(...)` for:

- `input.directory`
- `working.directory`
- `archive.directory`

This removes the plugin-local path-property lookup and adds consistent handling
of missing and blank values. The configuration factory now also confirms that
the supplied plugin id is `octopus`.

### Generic persistence

The previous repository contained separate electricity and gas loops, each
implementing the same sequence:

1. check whether the natural key exists;
2. insert when absent;
3. update when present;
4. increment inserted or updated counters.

Batch 3 replaces those duplicate loops with the Batch 0
`JdbcUpsertExecutor` and `JdbcUpsertAdapter` contracts.

The Octopus implementation is divided into:

- `AbstractOctopusUpsertAdapter<T>` — common prepared-statement and upsert
  mechanics;
- `ElectricityRecordUpsertAdapter` — electricity SQL and parameter bindings;
- `GasRecordUpsertAdapter` — gas SQL and parameter bindings;
- `OctopusPersistenceRepository` — transaction orchestration, combined counts,
  and statement-file completion.

Electricity and gas retain separate SQL because their database columns differ,
but their control flow is now implemented once.

### Transactions

`OctopusPersistenceRepository` now uses `JdbcTransactionTemplate`. This
centralises:

- connection acquisition;
- auto-commit handling;
- commit;
- rollback;
- auto-commit restoration;
- checked database-exception wrapping.

Statement-file ledger completion remains in the same transaction as the energy
records.

## Compatibility and deprecation

The public constructor and `save(...)` method remain source-compatible.

The removed `requirePath(...)` method was private and had no external callers,
so retaining it as a deprecated wrapper would add dead code without preserving
an API. No public procedure is deprecated in this batch.

All amended public production APIs and all new classes are documented with
`@since 2.0.0`.

## Included files

Production:

- `src/main/java/com/towermarsh/opendata/plugin/octopus/initialise/OctopusConfiguration.java`
- `src/main/java/com/towermarsh/opendata/plugin/octopus/load/AbstractOctopusUpsertAdapter.java`
- `src/main/java/com/towermarsh/opendata/plugin/octopus/load/ElectricityRecordUpsertAdapter.java`
- `src/main/java/com/towermarsh/opendata/plugin/octopus/load/GasRecordUpsertAdapter.java`
- `src/main/java/com/towermarsh/opendata/plugin/octopus/load/OctopusPersistenceRepository.java`

Tests:

- `src/test/java/com/towermarsh/opendata/plugin/octopus/initialise/OctopusConfigurationTest.java`
- `src/test/java/com/towermarsh/opendata/plugin/octopus/load/OctopusPersistenceRepositoryTest.java`

## Installation

From the OpenData repository root on the local refactor branch:

```powershell
Expand-Archive -Path .\OpenData-Refactor-Batch-3-Octopus.zip `
    -DestinationPath . -Force

git status
git diff --check
mvn clean verify
```

The archive must be applied after Batch 0 because it imports the shared
validation and JDBC packages introduced there.

## Local validation completed

- Java 17 production compilation with all `javac` lint checks.
- Javadoc doclint.
- Shared path-property parsing.
- Electricity insert path.
- Electricity update path.
- Gas insert path.
- Gas update path.
- Combined generic upsert result counts.
- Statement-file ledger completion in the same transaction.
- Commit and auto-commit restoration.
- Rollback and checked database-exception wrapping.
- JUnit source syntax compilation.
- Trailing-whitespace and tab checks.

The full Maven build must be run locally against the complete repository.

## Next phase

After Batch 3 is accepted, the code refactor series is complete. The next
recommended batch series is a documentation refresh covering:

- the shared validation package;
- the shared JDBC transaction, batch, and upsert framework;
- Ofgem persistence changes;
- OpenMeteo staging and pooled-session cleanup changes;
- Octopus typed electricity and gas adapters;
- architecture and sequence diagrams;
- release notes and `@since 2.0.0` API documentation.
