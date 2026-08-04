# ADR-0049: Share validation and JDBC execution mechanics

- **Status:** Accepted
- **Date:** 4 August 2026
- **Decision owners:** OpenData maintainers

## Context

Ofgem, OpenMeteo and Octopus independently parsed common configuration types
and implemented similar JDBC connection, transaction, batch and row-level
upsert control flow. The duplication produced inconsistent messages and made a
new plugin likely to copy another provider's private helpers.

The plugin repositories nevertheless use materially different SQL strategies:
Ofgem replaces one period with provenance, OpenMeteo stages records before
set-based statements, and Octopus applies natural-key electricity and gas
upserts. A single generic repository would obscure those differences and move
provider knowledge into framework code.

## Decision

Introduce provider-neutral shared packages for mechanics only:

- `com.towermarsh.opendata.validation` owns typed property conversion, common
  value rules and configured SQL identifier validation;
- `com.towermarsh.opendata.database.jdbc` owns transaction boundaries, optional
  pooled-session cleanup, prepared-statement batching and typed row-level upsert
  control flow.

Each provider retains:

- its typed configuration record and domain constraints;
- its schema, SQL and prepared-statement bindings;
- its natural keys and idempotency policy;
- its provenance, locking, staging and replacement rules;
- its load result semantics.

No ORM, reflection-generated SQL or universal repository abstraction is
introduced.

## Consequences

### Positive

- a new plugin starts with consistent typed property conversion;
- sensitive property values are not repeated in conversion errors;
- transaction rollback and connection-state restoration are implemented once;
- batch result counting is consistent;
- repeated electricity/gas-style upsert loops can use typed adapters;
- plugins remain readable because their SQL and business keys remain explicit;
- tests can exercise shared mechanics independently from provider SQL.

### Negative or limiting

- shared APIs add framework concepts that plugin authors must understand;
- record-by-record upsert remains less efficient than set-based loading for
  large datasets;
- a cleanup callback is required when a plugin changes connection-scoped SQL
  Server state;
- provider repositories still contain deliberate SQL duplication where column
  sets are genuinely different;
- the framework does not validate whether a declared property type matches the
  Java accessor selected by a plugin.

## Compatibility

Public plugin constructors and load entry points remain source-compatible.
Private duplicate helpers are removed. The public
`OpenMeteoConfiguration.sqlIdentifier` compatibility method remains temporarily
and is marked with Java `@Deprecated` and Javadoc `@deprecated` metadata.

All public APIs introduced or materially adjusted by this decision are marked
with Javadoc `@since 2.0.0`.

## Implementation notes

Implemented by the shared foundation and the subsequent Ofgem, OpenMeteo and
Octopus migration batches. See
[Shared Validation and JDBC Infrastructure](../architecture/028-shared-validation-and-jdbc-infrastructure.md)
and the
[Shared Validation and JDBC Reference](../reference/shared-validation-and-jdbc-reference.md).
