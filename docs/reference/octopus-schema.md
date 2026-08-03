# Octopus Schema Reference

**Document ID:** REF-SCHEMA-OCTOPUS-001
**Version:** 2.0
**Status:** Implemented SQL Server schema
**Baseline date:** 3 August 2026

---

Created by `sql/007a-create-octopus-schema.sql`.

## `octopus.statement_file`

Tracks successfully processed source documents.

| Column group | Purpose |
|---|---|
| `statement_file_id` | Identity primary key |
| `file_name`, `sha256` | Unique source-content identity |
| `statement_date`, `size_bytes` | Source metadata |
| `status` | `COMPLETED` or `FAILED` constraint; current loader writes `COMPLETED` |
| `last_run_id` | Foreign key to `core.PluginRun` |
| `processed_at`, `failure_message` | Completion/failure metadata |

The extractor skips only `COMPLETED` rows matching both filename and hash.

## `octopus.electric_data`

Stores one electricity tariff/meter/reading-period record. The composite primary
key is:

```text
bill_date + tariff_period_start + tariff_period_end + tariff_name
+ mpan + meter_id + start_reading_date + end_reading_date
```

Measures include meter readings, energy used in kWh, unit-rate p/kWh, standing
charge p/day and GBP totals. `last_run_id` links to `core.PluginRun`.

## `octopus.gas_data`

Uses the corresponding composite key with MPRN and includes consumption in cubic
metres as well as energy used in kWh, rates and GBP totals.

## Write behavior

`OctopusPersistenceRepository` checks each natural key, inserts missing rows,
updates existing rows, marks all batch source files completed and commits once.
Any SQL/runtime failure rolls back the complete batch.

The archive move is outside this SQL transaction.

::: {.landscape}
![Octopus data model](../diagrams/generated/octopus-data-model.svg){width=22.5cm}
:::
