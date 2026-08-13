# Shared Validation and JDBC Reference

**Document ID:** REF-SHARED-VALIDATION-JDBC-001
**Version:** 2.0
**Status:** Version 2.0.0 implementation reference
**Baseline date:** 4 August 2026
**Minimum Java version:** 24

---

## `PluginPropertyValues`

Construct one reader from the resolved definition:

```java
final var properties = new PluginPropertyValues(definition);
```

| Method | Result |
|---|---|
| `requiredText(name)` | required trimmed text |
| `text(name, defaultValue)` | trimmed text or default |
| `integer(name, defaultValue)` | `int` |
| `longValue(name, defaultValue)` | `long` |
| `doubleValue(name, defaultValue)` | optional/defaulted `double` |
| `requiredDouble(name)` | required `double` |
| `decimal(name, defaultValue)` | `BigDecimal` |
| `booleanValue(name, defaultValue)` | accepts true/false, yes/no, 1/0 and on/off |
| `duration(name, defaultValue)` | ISO-8601 `Duration` |
| `requiredDate(name)` | ISO `LocalDate` |
| `optionalDate(name)` | optional ISO `LocalDate` |
| `requiredPath(name)` | `Path` |
| `uri(name, defaultValue)` | `URI` |
| `parseRequired(...)` | required caller-defined type |
| `parse(...)` | defaulted caller-defined type |

Conversion exceptions identify the plugin and property without including the
configured value.

Example:

```java
final var properties = new PluginPropertyValues(definition);
final int batchSize = ValidationRules.requireRange(
        properties.integer("database.batch-size", 500),
        1,
        10_000,
        "database.batch-size");
```

## `ValidationRules`

| Method | Rule |
|---|---|
| `requireText(value, name)` | non-null, non-blank, trimmed text |
| `requireText(value, name, maximumLength)` | text with maximum length |
| `requirePositive(duration, name)` | duration greater than zero |
| `requireNonNegative(value, name)` | integer zero or greater |
| `requireRange(int, min, max, name)` | inclusive integer range |
| `requireRange(double, min, max, name)` | finite inclusive decimal range |
| `requireDateOrder(start, end, name)` | start must not follow end |

Use these methods after conversion to express domain constraints. Do not add a
plugin-local copy of a rule already represented here.

## `SqlIdentifiers`

Configured schema and table names cannot be passed as prepared-statement
parameters. Validate them before SQL composition:

```java
final String table = SqlIdentifiers.qualify(schema, tableName);
```

| Method | Result |
|---|---|
| `requireSafe(value, name)` | validates one unquoted identifier |
| `quote(value, name)` | validates and returns `[identifier]` |
| `qualify(schema, table)` | returns `[schema].[table]` |

Only identifiers matching `[A-Za-z_][A-Za-z0-9_]*` and the SQL Server maximum
identifier length accepted by the implementation are permitted. These methods
are not substitutes for prepared-statement parameters for data values.

## `JdbcTransactionTemplate`

Construct the template from the supplied database resource manager:

```java
final var transactions = new JdbcTransactionTemplate(database);
```

Basic transaction:

```java
return transactions.execute(
        "Unable to persist example data",
        connection -> persist(connection, records));
```

Transaction with connection-scoped cleanup:

```java
return transactions.execute(
        "Unable to persist staged data",
        connection -> persistStaged(connection, records),
        connection -> {
            execute(connection, "DROP TABLE IF EXISTS #ExampleStage");
            execute(connection, "SET XACT_ABORT OFF");
        });
```

The template:

1. borrows one connection;
2. records and disables auto-commit;
3. runs the callback;
4. commits on success;
5. rolls back on failure;
6. runs optional cleanup after commit or rollback;
7. restores the original auto-commit value;
8. returns the connection through try-with-resources.

Do not commit or roll back inside the normal transaction callback. The callback
may execute savepoints only when the plugin has a documented need.

## `JdbcBatchExecutor`

```java
final int loaded = JdbcBatchExecutor.execute(
        connection,
        INSERT_SQL,
        records,
        configuration.databaseBatchSize(),
        (statement, record) -> bindRecord(statement, record));
```

The executor rejects blank SQL, null records and non-positive batch sizes. It
counts normal update results, treats `Statement.SUCCESS_NO_INFO` as one affected
record and fails if the driver reports `Statement.EXECUTE_FAILED`.

## `JdbcUpsertAdapter` and `JdbcUpsertExecutor`

Use the generic executor when each record follows the same natural-key flow:

```java
final JdbcUpsertResult result = JdbcUpsertExecutor.execute(
        connection,
        records,
        runId,
        adapter);
```

The adapter supplies:

```java
boolean exists(Connection connection, T record, C context);
void insert(Connection connection, T record, C context);
void update(Connection connection, T record, C context);
```

`JdbcUpsertResult.plus(...)` combines typed results from separate record sets.
Use a set-based staging strategy instead when volume or concurrency makes one
existence query per record inappropriate.

## Error handling

- conversion and validation errors use `IllegalArgumentException`;
- JDBC callbacks may throw checked exceptions;
- checked transaction failures are wrapped in `DatabaseAccessException`;
- runtime exceptions retain their original type;
- rollback and cleanup failures are suppressed on the primary exception;
- exception messages must not include credentials or sensitive property values.

## API metadata

Public APIs introduced or materially changed for Version 2.0.0 use Javadoc
`@since 2.0.0`. A retained obsolete public method uses both Java `@Deprecated`
and Javadoc `@deprecated`, including the supported replacement.
