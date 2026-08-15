# Running Plugins

**Document ID:** GUIDE-PLUGIN-RUN-001  
**Version:** 3.0.0  
**Status:** Current  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Prerequisite

Plugins must be registered and enabled. Inspect status with:

```text
opendata --list-plugins
```

## Run selection

```text
opendata --plugin openmeteo
opendata --plugin openmeteo --plugin ofgem
opendata --plugin openmeteo,ofgem --parallelism 2
opendata --plugin all
opendata --plugin all --dry-run --parallelism 3
```

`--plugin` may repeat. `all` cannot be mixed with named ids. Named disabled or
unregistered plugins are rejected; `all` silently excludes disabled rows.

`--parallelism` accepts 1-64 and is effective only for runs/dry-runs.

## Configuration changes

A run does not accept `--file`. Copy a complete packaged plugin definition,
amend it and re-register one named plugin:

```text
opendata --plugin openmeteo --register --file C:\OpenData\openmeteo.properties
```

## Dry run

Ofgem, OpenMeteo and Octopus perform acquisition/parsing without provider data
writes, generic audit rows or archive movements. Startup still needs SQL Server
for registry/configuration reads. Octopus dry run skips its completion ledger and
parses every matching input PDF.

## Outcome

The application logs final `ExecutionStatus` and per-plugin summaries. It does
not currently call `System.exit`, so shell status alone is not authoritative.
