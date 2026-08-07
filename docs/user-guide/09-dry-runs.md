# 10. Dry Runs

**Document ID:** USER-009  
**Version:** 2.1  
**Status:** Current  
**Baseline date:** 7 August 2026

---

Dry run is the recommended first execution for every enabled plugin. Because it
still executes plugin extraction and transformation logic, it requires the
explicit `--Execute` execution gate:

```text
opendata --plugin ofgem --Execute --dry-run
opendata --plugin openmeteo --Execute --dry-run
opendata --plugin octopus --Execute --dry-run
opendata --plugin all --Execute --dry-run --parallelism 3
```

The short forms are `-x` for `--Execute` and `-n` for `--dry-run`. The `-d`
option means `--disable`.

A dry run:

- reads the persistent registry and active configuration;
- performs remote discovery/API requests or reads local source files;
- downloads replaceable working files where required;
- parses and validates data;
- reports metrics; and
- creates no plugin provider-data writes or generic `core.PluginRun` rows.

Startup still connects to SQL Server to read `core.plugin_registry` and, when
database-backed configuration is enabled, application/plugin properties. Dry run
therefore means no plugin data load, not no database connection at all.

Octopus deliberately skips its processed-file ledger in dry-run mode. It parses
all matching PDFs in the input directory and does not move them.

Dry-run success never proves SQL DML, grants, transaction behaviour, audit
completion or archive movement. Complete a controlled write-mode acceptance run
with `--Execute` before release.
