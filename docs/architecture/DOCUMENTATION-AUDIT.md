# Documentation Audit

**Document ID:** REVIEW-DOC-AUDIT-001  
**Version:** 2.0  
**Status:** Current Version 2.0.0 implementation audit  
**Baseline date:** 3 August 2026

## Summary

The command-line and plugin lifecycle update supersedes the Batch 8 closure
baseline where those documents described a classpath-only registry, a standalone
registration command, per-invocation file layering or the former Octopus dry-run limitation.

## Current authoritative statements

| Area | Current implementation |
|---|---|
| Runtime registry | SQL Server `core.plugin_registry` via `JdbcPluginRegistry` |
| Packaged index | Registration catalogue only |
| Registration | Requires `--plugin`; supports named, repeated or `all` selection |
| External file | Complete definition for one named registration only |
| Status | Durable enable/disable operations |
| Unregister | Removes registry metadata and stored properties only |
| Run selection | Only enabled registered plugins |
| Dry run | Supported by Ofgem, OpenMeteo and Octopus; no plugin data writes |
| Short-option conflict | `-d` disable; `-n` dry-run |

## Remaining transitional items

- internal scheduling;
- multiple database engines;
- unified run/provenance identity;
- installed executable packaging and external writable bootstrap/key paths;
- process exit-code mapping;
- managed secret-provider integration;
- direct Octopus API/email acquisition;
- target-environment release acceptance.
