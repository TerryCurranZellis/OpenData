# Coding Standard

**Document ID:** STD-CODE-001  
**Version:** 2.0  
**Status:** Version 2.0.0 engineering baseline  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 24

---

## Java language and structure

- Code MUST compile with Java release 17.
- Packages MUST remain below `com.towermarsh.opendata`.
- Immutable data SHOULD use records or final fields with defensive copies.
- Dependencies MUST be passed through constructors or explicit execution
  context; OpenData does not use a dependency-injection framework.
- Application logging MUST use `java.util.logging`.
- Lower layers MUST NOT call `System.exit`.
- Interrupt handling MUST restore the interrupted flag.
- Shared acquisition, parser and database packages MUST NOT depend on a provider
  plugin package.

## Plugin boundaries

The root plugin class implements `OpenDataPlugin` and remains a thin facade.
Provider orchestration belongs in `initialise`; acquisition in `extract`;
normalisation and validation in `transform`; SQL and transactions in `load`;
cleanup and post-processing in `finalise`.

A plugin execution object and its mutable working state are thread-confined.
Plugins MUST NOT share a JDBC connection or mutable run state across tasks.

## JDBC and files

- Connections, statements, result sets and streams MUST use deterministic
  cleanup.
- The repository or load component that borrows a connection owns commit,
  rollback and restoration of connection state.
- Data values MUST use prepared-statement parameters.
- SQL identifiers derived from configuration MUST be allow-list validated before
  interpolation.
- Downloads MUST use explicit timeouts, bounded sizes where practical and
  temporary files before atomic or replacement moves.

## Errors and metrics

Provider failures propagate to the framework boundary for contextual logging and
audit. Do not catch an exception merely to log and rethrow without adding useful
context.

`PluginMetrics` values MUST be non-negative and accurately distinguish records
read, inserted, updated and skipped. A dry run MUST avoid persistent side effects
and database access unless the contract explicitly provides a read-only
resource.

## Source documentation

Every production package containing classes MUST include `package-info.java`.
Public contracts and non-obvious concurrency, transaction, security or
filesystem behaviour require useful Javadoc. Do not copy obsolete version text
into new class comments.

## Formatting and automated checks

Use UTF-8, four spaces and no tab characters. Keep methods focused, use
descriptive names and prefer explicit control flow over hidden side effects.
Checkstyle, SpotBugs and PMD are configured in Maven; their findings are
advisory by default but must still be reviewed.
