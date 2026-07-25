# OpenData test suite

This directory contains a replacement JUnit test suite for the current OpenData source tree.

## Test approach

- Unit tests do not require a running SQL Server instance.
- JDBC persistence tests use Mockito to simulate connections, statements, result sets, generated keys, batch results, commits, rollbacks, and SQL failures.
- HTTP-focused tests use local test servers or controlled collaborators rather than production endpoints.
- Workbook/parser tests create controlled test data in memory or temporary files.
- Plugin coordinator tests exercise success, dry-run, failure, audit, and parallel execution behaviour.

## Run

```powershell
mvn clean test
```

For a coverage report, add the JaCoCo Maven plugin or run coverage from NetBeans.

## Database integration tests

These tests verify the Java/JDBC behaviour without a database. Once the SQL Server database has been created, add a separate integration-test profile that runs against a disposable OpenData database. Do not mix live-database tests into the default unit-test phase.
