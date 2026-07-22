# ADR-0014: Separate framework metadata from plugin business tables

- Status: Accepted
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

The database must store both operational information common to all imports and dataset-specific data whose shape differs between plugins. Combining these concerns would either produce generic low-value tables or tightly couple the core schema to individual datasets.

## Decision

Use common framework metadata tables for datasets, plugin registrations, execution runs, downloaded files, validation outcomes, and errors. Store domain records in plugin-owned business tables designed for each dataset. The Ofgem plugin provides the first concrete business schema.

## Consequences

### Positive

- Common operational reporting is consistent across plugins.
- Dataset tables retain useful types, keys, and constraints.
- Plugin schemas can evolve without repeatedly changing the core run tables.
- Supports auditability and restart analysis.

### Negative or limiting

- More tables and migrations must be managed.
- Cross-plugin queries may require views or reporting layers.
- Schema ownership and naming conventions must be enforced.

## Alternatives considered

### One generic entity-attribute-value table

Rejected because it weakens data types, constraints, discoverability, and query performance.

### Put all dataset columns into framework tables

Rejected because the core schema would change for every plugin.

## Implementation notes

Use stable run identifiers to relate framework metadata to plugin loads. Database scripts should be separated into framework and plugin areas and be safe to apply in controlled version order.
