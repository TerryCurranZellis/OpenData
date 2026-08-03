# 10. Dry Runs

**Document ID:** USER-009  
**Version:** 2.0  
**Status:** Current with Octopus limitation  
**Baseline date:** 3 August 2026

---

Dry run is the recommended first execution for Ofgem and OpenMeteo:

```text
opendata --plugin ofgem --dry-run
opendata --plugin openmeteo --dry-run
opendata --plugin ofgem,openmeteo --dry-run --parallelism 2
```

A supported dry run:

- resolves configuration and plugin definitions;
- performs remote discovery or API requests;
- downloads working files where required;
- parses and validates data;
- reports metrics; and
- creates no plugin database writes or `core.PluginRun` rows.

When `application.use-database-properties=true`, startup still connects to SQL
Server to load application and plugin properties before the dry-run boundary.
Dry run therefore does not always mean “no database connection”.

## Octopus limitation

Do not use:

```text
opendata --plugin octopus --dry-run
opendata --plugin all --dry-run
```

`OctopusExtract` reads `octopus.statement_file` before parsing, while the dry-run
context deliberately supplies an unavailable database resource. The command
fails before useful validation. Use a controlled write-mode acceptance run with
disposable input copies and an isolated test database.

Dry-run success never proves SQL syntax, grants, transaction behaviour, audit
completion or archive movement.
