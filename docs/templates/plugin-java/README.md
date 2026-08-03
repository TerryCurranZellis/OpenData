# Java Plugin Template

**Document ID:** TEMPLATE-PLUGIN-JAVA-001  
**Version:** 2.0  
**Status:** Version 2.0.0 structural template  
**Baseline date:** 3 August 2026

---

Copy the `example` package into
`src/main/java/com/towermarsh/opendata/plugin`, rename it to the new provider ID
and replace every `Example` symbol.

The template follows the current framework contract:

- `ExamplePlugin` is the thin `OpenDataPlugin` entry point;
- `initialise` owns typed configuration and orchestration;
- `download` is an optional acquisition helper used by `extract`;
- `extract` obtains the source representation;
- `transform` converts extracted values into immutable records;
- `transform.validate` enforces cross-record rules;
- `load` owns dry-run handling and the future SQL transaction;
- `finalise` owns cleanup and final reporting.

`ExampleLoad` deliberately throws in write mode until real SQL, rollback and
load counts are implemented. It is safe to use for structural development and
dry-run testing, but it cannot report false write success.

After copying:

1. add `<id>.properties` and the registry index entry;
2. add ordered SQL and least-privilege grants;
3. replace the placeholder source and transformation logic;
4. add unit, SQL Server integration and acceptance tests;
5. add plugin/user/reference documentation and source notices; and
6. run registration before ordinary database-backed execution.

See [Adding a plugin](../../guides/adding-a-plugin.md).
