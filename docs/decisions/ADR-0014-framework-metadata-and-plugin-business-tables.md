# ADR-0014: Separate framework metadata and plugin business tables

- **Status:** Accepted
- **Date:** 2026-07-23
- **Accepted:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

Execution metadata is common to every dataset, while business entities and keys
are dataset-specific. A single generic fact table would weaken constraints and
make plugin evolution dependent on unrelated datasets.

## Decision

Use framework-owned tables in the `core` SQL Server schema for datasets,
ingestion runs, source files, errors and schema versions. Give each plugin its
own business schema, beginning with `ofgem`.

Common tables may reference plugin identifiers, but `core` must not acquire
plugin-specific columns. Plugin facts may reference shared ingestion and source
records for provenance.

## Consequences

### Positive

- operational reporting is consistent across plugins;
- plugin tables retain strong domain constraints and meaningful names;
- plugins can evolve without changing unrelated business schemas;
- lineage joins business facts to shared run and source metadata.

### Negative or limiting

- cross-plugin reporting requires joins across schemas;
- every plugin must provide and maintain its own SQL scripts;
- shared concepts must be deliberately promoted rather than duplicated casually.

## Alternatives considered

### One generic key/value fact table

Rejected because SQL Server constraints, data types and reporting semantics would
be weak.

### Separate database for every plugin

Deferred because it adds operational and connection complexity before scale
requires that isolation.

## Implementation notes

Implemented by scripts `sql/002-create-core-schema.sql` and
`sql/004-create-ofgem-schema.sql`. The application role receives permissions on both
schemas without owning either schema.
