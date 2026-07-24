# ADR-0032: Use explicit JDBC repositories rather than an ORM

- **Status:** Accepted
- **Date:** 2026-07-24
- **Decision owners:** OpenData maintainers

## Context

OpenData is an ETL application with SQL Server-specific schemas, batch inserts,
controlled replacement transactions and relatively small domain aggregates. An
ORM would add mapping state and dependencies without removing the need for
explicit SQL and transaction control.

## Decision

Define repository interfaces at dataset boundaries and implement them with JDBC,
prepared statements and explicit SQL Server SQL. Service and parser layers must
not contain SQL. Repository constructors receive `DatabaseConnectionManager`.

## Consequences

### Positive

- SQL and transaction behaviour remain visible;
- batch operations and SQL Server features are straightforward;
- no persistence annotations leak into immutable domain records;
- repository integration tests can target exact statements and constraints.

### Negative or limiting

- SQL mapping code is handwritten;
- schema changes require coordinated SQL and Java edits;
- portability requires new implementations.

## Alternatives considered

JPA/Hibernate and Spring Data were rejected for the initial modular monolith.
They may be reconsidered only if domain complexity outweighs explicit JDBC.
