# ADR-0047: Database-backed configuration registration

- Status: Accepted
- Date: 2026-08-01
- Decision owners: OpenData maintainers

## Context

OpenData originally read application and plugin properties only from packaged
resources plus an optional `--file` override. That model left the database
password outside the runtime configuration store, prevented centrally managed
plugin settings, and forced local property files to remain the system of
record.

Version 2 requires a registration step that persists both application and
plugin properties in SQL Server and then uses the database as the default
configuration source on future runs.

## Decision

OpenData adds selected registration through `--plugin <id|all> --register` that:

1. reads packaged application properties and the selected packaged plugin definitions, or one complete external definition with `--file`;
2. writes the resolved flat property sets into SQL Server tables
   `[core].[application_property]` and `[core].[plugin_property]`;
3. encrypts the database password before storing it; and
4. rewrites `src/main/resources/config/application.properties` as a minimal
   bootstrap file containing only the application version, database URL,
   database user, encrypted database password, and the
   `application.use-database-properties` flag.

Runtime configuration loading now uses a property-source interface. The default
source remains the packaged files until `application.use-database-properties`
is enabled, after which the application loads runtime and plugin properties
from the database and ignores the packaged property definitions during normal
execution.

## Consequences

### Positive

- Plugin and application configuration can be centrally managed in SQL Server.
- The database password is no longer persisted in plain text in the bootstrap
  properties file.
- File-backed and database-backed configuration loading share the same parsing
  and validation path.
- The selected registration command provides an explicit migration step into version 2.

### Negative or limiting

- The application now depends on a bootstrap password-decryption key pair being
  available locally.
- Configuration registration must run successfully before database-backed
  configuration can be used.
- Schema deployment must include the new configuration property tables and
  permissions.

## Alternatives considered

### Keep properties files as the permanent source of truth

Rejected because it does not support the version 2 requirement to default to
database-backed configuration after registration.

### Store configuration as JSON blobs

Rejected for now because the current plugin parser already operates on flat
properties and the relational key/value model minimises change while remaining
queryable.

## Amendment — persistent lifecycle registry

ADR-0048 adds `core.plugin_registry`, selected/repeated/all registration and lifecycle administration. `--file` is no longer an invocation override; it is one complete named registration source. This ADR remains authoritative for database-backed configuration and encrypted bootstrap behaviour.
