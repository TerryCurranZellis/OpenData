# 9. Dry Runs

**Document ID:** USER-009
**Version:** 2.0
**Status:** Current with Octopus limitation
**Baseline date:** 3 August 2026

---

Dry run is the recommended first plugin execution for Ofgem and OpenMeteo:

```text
opendata --plugin ofgem --dry-run
opendata --plugin openmeteo --dry-run
opendata --plugin ofgem,openmeteo --dry-run --parallelism 2
```

A supported dry run:

- resolves configuration and plugin definitions;
- performs remote discovery or API requests;
- downloads working files where the provider requires them;
- parses and validates source data;
- reports read/skipped metrics;
- supplies an unavailable database resource to plugin code;
- creates no `core.PluginRun` rows and writes no plugin tables.

When `application.use-database-properties=true`, application startup still needs
the encrypted bootstrap credential and SQL Server to load runtime/plugin
configuration before the dry-run plugin boundary is entered. Dry run therefore
does not universally mean that no database connection is required during
startup.

## Octopus limitation

Do not run:

```text
opendata --plugin octopus --dry-run
opendata --plugin all --dry-run
```

as acceptance commands in the current baseline. `OctopusExtract` reads the
completed-file ledger before parsing, but the dry-run context intentionally has
no usable plugin database manager. The command therefore fails. This is an
implementation defect; documentation or configuration cannot correct it.

A successful Ofgem/OpenMeteo dry run proves acquisition and parsing only. It
does not prove SQL syntax, permissions, transactions, audit completion or
rollback. Octopus currently requires disposable PDFs, an isolated test database,
an explicit archive directory and a controlled write-mode acceptance run.
