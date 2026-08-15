# Octopus Energy Statement Plugin

**Document ID:** PLUGIN-OCTOPUS-INDEX-001
**Version:** 3.0.0  
**Status:** Runtime, dry-run and generic upsert integration implemented; live acceptance pending
**Baseline date:** 15 August 2026  

---

**Plugin id:** `octopus`
**Implementation:** `com.towermarsh.opendata.plugin.octopus.OctopusPlugin`
**Dataset id:** `octopus-energy-billing`

## Source boundary

The plugin imports electricity and gas billing data from local PDFs named:

```text
octopus-energy-statement-YYYY-MM-DD.pdf
```

Version 3.0.0 does not access an Octopus account, API or email mailbox.

## Configuration

`OctopusConfiguration` uses `PluginPropertyValues.requiredPath(...)` for the
input, working and archive directories and validates the `octopus` plugin id.
Missing and blank paths use the same error behaviour as other plugins.

## Persistence

One `JdbcTransactionTemplate` transaction contains:

1. typed electricity upserts;
2. typed gas upserts; and
3. statement-file ledger completion.

`JdbcUpsertExecutor` provides the common existence/insert/update loop.
`ElectricityRecordUpsertAdapter` and `GasRecordUpsertAdapter` retain their
separate natural keys, SQL and parameter bindings. Their counts are combined for
the plugin result.

This design removes duplicated flow without attempting to force electricity and
gas into an artificial common record model.

## Duplicate handling

`octopus.statement_file` identifies a completed source by lower-cased filename
and SHA-256. Same name plus changed content is processed again. Business rows and
completion ledger commit atomically.

## Dry run and archive

Dry run performs discovery, hashing, PDF extraction and parsing but skips the
processed-file ledger, business writes, audit write and archive. Archive movement
occurs after database commit and must be reconciled operationally if it fails.

See [architecture](../../architecture/027-octopus-energy-statement-architecture.md),
[plugin reference](../../reference/octopus-plugin.md) and
[schema reference](../../reference/octopus-schema.md).
