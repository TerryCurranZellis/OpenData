# Validation — OpenData Octopus Adjustment 3.1.0

The additive code pack was checked against the current `main` API structure before packaging.

## Checks completed

- Generated main Java sources compile in a local `javac` API-compatibility harness using signatures matched to current OpenData `main`.
- Generated test Java sources also compile in the same harness with JUnit/Mockito surface stubs.
- `OctopusStatementParser.parseAllFromFile(Path)` is used for adjustment PDF transformation; no parser copy is included.
- The existing `ElectricityRecord` and `GasRecord` model types are reused.
- JDBC transaction/upsert usage matches the current `JdbcTransactionTemplate`, `JdbcUpsertExecutor`, `JdbcUpsertAdapter`, and `JdbcUpsertResult` APIs.
- Production Java and SQL contain no reference to the ordinary persistence targets `octopus.electric_data`, `octopus.gas_data`, or `octopus.statement_file`.
- No `index.properties`, `module-info.java`, `OpenData.java`, CLI parser, or other existing `main` file is included.
- No CLI plugin-registration command is included; registration is documented as GUI-only.
- All Java `@version` declarations in the pack are `3.1.0`.

## Repository build

A full Maven build is intentionally left as the installation validation step after the pack is copied into the user's current working branch, because this execution environment cannot clone the repository directly over the network. Run the normal project test/build after installation.
