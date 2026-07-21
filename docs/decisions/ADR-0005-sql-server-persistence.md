# ADR-0005: Use Microsoft SQL Server as the first persistence target

- Status: Accepted
- Date: 2026-07-21

## Context

The initial OpenData implementation loads dataset records into SQL Server and existing project work already targets SQL Server.

## Decision

Build the first persistence adapter using the Microsoft JDBC driver and SQL Server data types, transactions, batching, and migration scripts.

## Consequences

- SQL Server-specific repository implementations are permitted.
- Framework contracts should avoid unnecessary vendor-specific leakage.
- A second database would require a new adapter and an ADR.
- SQL Server credentials and permissions require explicit operational controls.
