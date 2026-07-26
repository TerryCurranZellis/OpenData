# 9. Dry Runs

**Document ID:** USER-009  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

Dry run is the recommended first execution:

```text
opendata --plugin all --dry-run --parallelism 2
```

A dry run:

- resolves configuration and plugin definitions;
- performs remote discovery/API requests;
- downloads working files;
- parses and validates source data;
- reports read/skipped metrics.

It does not:

- require a database password;
- initialise the database pool;
- create `core.PluginRun` or ingestion rows;
- archive the Ofgem workbook;
- write plugin tables.

A successful dry run proves acquisition and parsing only. It does not prove SQL
syntax, permissions, transactions, audit completion or rollback.
