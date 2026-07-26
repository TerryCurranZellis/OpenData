# Dependency Rules

**Document ID:** ARCH-005  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## Permitted direction

`app` may coordinate CLI, configuration and the provider-neutral plugin runtime.
Each `plugin.<id>` facade may call only its own `config`, `download`, `extract`,
`transform`, `transform.validate` and `load` stages plus shared framework
contracts. Plugin stage packages may use shared infrastructure but may not
depend on another provider.

## Prohibited dependencies

- `Main` must not depend on Ofgem or OpenMeteo implementation classes.
- provider classes must not live outside `plugin.<id>`;
- one provider must not import another provider;
- parsers must not depend on plugin packages;
- `load` packages must not initiate downloads or extraction;
- `download` and `extract` packages must not write the database;
- records must not open network/file/database resources;
- plugins must not parse CLI options or log secrets;
- validation must not silently mutate source rows.

## Approved specialist libraries

Commons CLI, Commons CSV, Jackson, JSoup, Apache POI and Microsoft JDBC.

The package rules are currently review conventions. Automated ArchUnit or
equivalent enforcement remains an open quality task.
