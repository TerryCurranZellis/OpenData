# Octopus Adjustment Schema and Persistence Reference

**Document ID:** REF-OCTOPUS-ADJUSTMENT-SCHEMA-001  
**Version:** 3.1.0  
**Baseline date:** 15 August 2026  

---

## Schema

The Version 3.1.0 plugin uses the existing SQL Server schema:

```text
octopus
```

but creates adjustment-specific tables.

## Tables

| Table | Purpose |
|---|---|
| `octopus.adjustment_electric_data` | Recalculated electricity billing facts |
| `octopus.adjustment_gas_data` | Recalculated gas billing facts |
| `octopus.adjustment_file` | Processed adjustment PDF identity and completion ledger |

## Business-table compatibility

`adjustment_electric_data` should have the same business structure as:

```text
octopus.electric_data
```

`adjustment_gas_data` should have the same business structure as:

```text
octopus.gas_data
```

This includes compatible:

- data types;
- period validation;
- meter/supply identifiers;
- natural keys;
- rate and charge columns;
- run provenance; and
- created/updated timestamps.

The tables remain physically separate because adjustment values are a distinct
billing provenance.

## Electricity natural key

The adjustment electricity primary/natural key should mirror ordinary
electricity:

```text
bill_date
tariff_period_start
tariff_period_end
tariff_name
mpan
meter_id
start_reading_date
end_reading_date
```

## Gas natural key

The adjustment gas primary/natural key should mirror ordinary gas:

```text
bill_date
tariff_period_start
tariff_period_end
tariff_name
mprn
meter_id
start_reading_date
end_reading_date
```

## Adjustment-file ledger

Recommended columns:

| Column | Type | Purpose |
|---|---|---|
| `adjustment_file_id` | `bigint IDENTITY` | Surrogate ledger key |
| `file_name` | `nvarchar(260)` | Source filename |
| `sha256` | `char(64)` | Source content hash |
| `size_bytes` | `bigint` | Source size |
| `status` | `varchar(20)` | Processing state |
| `last_run_id` | `uniqueidentifier` | OpenData run id |
| `processed_at` | `datetime2(3)` | Completion timestamp |
| `failure_message` | `nvarchar(2000)` | Optional failure details |

A statement date is not required for file discovery because adjustment
filenames do not contain a date. If a reliable document-level bill date is
available from parsed content, it may be added as metadata, but it must not be
derived from the filename.

## Ledger uniqueness

Recommended uniqueness:

```text
(file_name, sha256)
```

Filename comparison should be normalised consistently with the Java completion
lookup.

## Transaction model

For each load batch:

1. upsert adjustment electricity;
2. upsert adjustment gas;
3. mark adjustment sources completed; and
4. commit.

All operations share one SQL transaction.

A failure before commit rolls back the batch.

## Archive boundary

Source-file movement happens after database commit and is not part of the SQL
transaction.

An archive failure therefore requires operational reconciliation but must not
cause already committed adjustment data to be reported as rolled back.

## Ordinary tables

The adjustment plugin must not insert, update or complete rows in:

```text
octopus.electric_data
octopus.gas_data
octopus.statement_file
```
