# ADR-0015: Use database interfaces with SQL Server as the first implementation

- Status: Accepted
- Date: 2026-07-22
- Decision owners: OpenData maintainers

## Context

SQL Server is the immediate persistence target, but the project vision describes a reusable open-data framework. Direct SQL Server dependencies throughout pipeline and plugin logic would make future portability difficult.

## Decision

Define persistence contracts at framework boundaries and provide SQL Server implementations first. SQL Server SQL, JDBC details, and data-source construction remain inside database adapter packages. Database independence is an architectural direction, not a claim that all SQL is currently portable.

## Consequences

### Positive

- Keeps pipeline and plugin orchestration independent of JDBC details.
- Allows focused SQL Server optimisation in the initial release.
- Creates test seams for repositories and transactions.
- Makes another database implementation possible later.

### Negative or limiting

- Portable interfaces cannot eliminate vendor-specific schema and SQL differences.
- Additional abstractions increase code compared with direct JDBC calls.
- Only SQL Server is supported and tested initially.

## Alternatives considered

### Direct JDBC in every plugin

Rejected because connection handling, transactions, and error translation would be duplicated.

### Use JPA/Hibernate

Not selected because the workload is batch-oriented, SQL-centric, and benefits from explicit control.

## Implementation notes

Use the Microsoft JDBC driver in the SQL Server adapter. Credentials should come from resolved configuration or a future secret provider, never source code. Integration tests should run against a real or containerised SQL Server where practical.
