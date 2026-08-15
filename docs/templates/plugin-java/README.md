# Java Plugin Template

**Document ID:** TEMPLATE-PLUGIN-JAVA-001
**Version:** 3.0.0  
**Status:** Version 3.0.0 shared-processing template
**Baseline date:** 15 August 2026  

---

Copy the `example` package into
`src/main/java/com/towermarsh/opendata/plugin`, rename it to the provider ID and
replace every `Example` symbol.

## Required framework use

New plugins must begin with the shared Version 3.0.0 infrastructure:

- `PluginPropertyValues` for typed registered properties;
- `ValidationRules` for domain-independent rules;
- `SqlIdentifiers` when configuration controls SQL identifiers;
- `JdbcTransactionTemplate` for write transactions;
- `JdbcBatchExecutor` for parameterised batches; and
- `JdbcUpsertExecutor` only when record-by-record exists/insert/update is the
  correct provider strategy.

Do not copy private parsing or transaction helpers from an existing provider.
Keep provider defaults, business validation, SQL and natural keys in the new
plugin.

## Template stages

- `ExamplePlugin` is the thin framework entry point;
- `initialise` owns typed configuration and orchestration;
- `extract` obtains the source representation;
- `transform` creates immutable records;
- `transform.validate` enforces provider and cross-record rules;
- `load` applies dry-run policy and delegates to the repository;
- `finalise` owns cleanup/archive/reporting.

`ExampleConfiguration` demonstrates shared URI/path/duration/long parsing.
`ExampleLoader` demonstrates a transaction and insert batch. Replace the example
schema/table/columns and decide whether insert-only, set-based staging or typed
upsert is correct for the provider.

## API documentation rule

Every new or amended public Version 3.0.0 API must include Javadoc
`@since 2.0.0`. A retained obsolete public API must also include Java
`@Deprecated` and Javadoc `@deprecated`. Do not retain unused private methods as
deprecated wrappers.

## Completion checklist

1. add complete registration properties and registry index entry;
2. add ordered SQL, constraints, indexes and least-privilege grants;
3. replace placeholder source/transform/load logic;
4. add unit and live SQL Server tests;
5. add plugin/user/reference documentation, diagrams and source notices;
6. register the plugin before ordinary database-backed execution; and
7. run Maven and documentation validation.

See [Adding a plugin](../../guides/adding-a-plugin.md) and the
[shared validation/JDBC reference](../../reference/shared-validation-and-jdbc-reference.md).
