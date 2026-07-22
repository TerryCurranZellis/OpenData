# ADR-0012: Keep plugin configuration in properties files before database-backed configuration

- Status: Shelved
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

A future design could store plugin definitions and settings in database tables, indexed by plugin name, and return settings as JSON. This would centralise maintenance and allow databases with native JSON output to expose configuration. However, the application still needs bootstrap settings before a database connection can be established, and configuration import and maintenance workflows are not yet defined.

## Decision

For the current development phase, keep each plugin's settings in its own Java properties files, layered with application defaults and optional command-line override files. Shelve database-backed plugin configuration and JSON configuration export for a later architecture phase.

## Consequences

### Positive

- Allows Phase 1 development without first building a configuration administration subsystem.
- Configuration remains version-controllable and easy to inspect.
- Avoids making plugin startup dependent on database availability.
- Preserves a clear migration path to database-backed configuration.

### Negative or limiting

- Properties files require deployment and maintenance outside the database.
- Centralised querying and administration are deferred.
- A future migration will require an importer, schema, versioning, validation, and precedence rules.

## Alternatives considered

### Store all plugin settings in database tables now

Shelved because database bootstrap, initial loading, maintenance, and recovery procedures are not yet designed.

### Store settings only in external files permanently

Not chosen as a permanent restriction; database-backed storage remains a future option.

### Use environment variables as the primary store

Rejected because explicit files and command-line selection are preferred.

## Implementation notes

Retain a typed configuration API so the underlying source can change later. Future work should define plugin configuration tables, JSON schema/versioning, import/export commands, encryption or secret references, audit history, and fallback behaviour when the database is unavailable.
