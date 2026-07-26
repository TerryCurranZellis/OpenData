# Coding Standard

**Document ID:** STD-CODE-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

## Java

- Code MUST compile with Java release 17.
- Packages MUST remain under `com.towermarsh.opendata`.
- Immutable data SHOULD use records and defensive copies.
- Dependencies MUST be passed through constructors or explicit method context;
  a dependency-injection framework is not used.
- Application logging MUST use `java.util.logging`.
- Lower layers MUST NOT call `System.exit`.
- Interrupt handling MUST restore the interrupt flag.
- JDBC connections, statements, result sets and streams MUST use deterministic
  cleanup.
- A plugin task MUST NOT share a JDBC connection or mutable plugin instance with
  another task.
- SQL identifiers derived from configuration MUST be validated before
  interpolation.

## Boundaries

The application layer owns startup and lifecycle. Plugins own dataset rules.
Repositories own SQL and transactions. Shared acquisition/parsing packages must
not depend on a plugin package.

## Source headers and Javadoc

Source headers MUST identify Apache-2.0 consistently. Public packages require
`package-info.java`; public APIs and non-obvious concurrency or transaction
behaviour require Javadoc.

## Formatting

Use four spaces, no tabs, UTF-8 and descriptive names. Keep methods focused and
prefer explicit control flow over hidden side effects.
