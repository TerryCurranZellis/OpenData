# ADR-0033: Manage the schema with ordered idempotent SQL scripts

- **Status:** Accepted
- **Date:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

The project needs reproducible database creation without adding another runtime
framework. Developers may rerun installation after a partial setup.

## Decision

Maintain numbered SQL Server scripts in execution order. Scripts check whether
objects already exist, use `GO` batch boundaries and record completed logical
versions in `core.schema_version`. Bootstrap login/database creation remains
separate from application-schema changes.

## Consequences

### Positive

- database changes are reviewable with source code;
- local setup needs only SQL Server tooling;
- interrupted installation can normally be rerun;
- privileged bootstrap is separated from ordinary schema installation.

### Negative or limiting

- rollback scripts are not automatic;
- branching migrations require discipline;
- a migration framework may be needed as environments and contributors grow.

## Alternatives considered

Flyway and Liquibase were deferred, not rejected permanently. One monolithic SQL
file was rejected because ordering and version history would be unclear.
