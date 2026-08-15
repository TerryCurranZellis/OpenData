# Octopus Adjustment Architecture

**Document ID:** ARCH-029  
**Version:** 3.1.0  
**Status:** Version 3.1.0 implementation specification  
**Baseline date:** 15 August 2026  

---

## Context

The ordinary Octopus plugin already provides a local-PDF pipeline for
electricity and gas billing statements.

Octopus adjustment bills contain the same class of electricity and gas billing
facts but represent recalculated charges. They must be stored separately from
ordinary billing data.

The Version 3.1.0 design therefore creates a new plugin while deliberately
reusing compatible extract and transform behaviour.

## Plugin identity

```text
plugin id: octopus-adjustment
dataset id: octopus-energy-adjustments
```

Proposed Java root package:

```text
com.towermarsh.opendata.plugin.octopusadjustment
```

## Registration boundary

The plugin definition is registered through the OpenData **GUI only**. New
plugins are not registered through the CLI. The GUI registration workflow reads
the new plugin definition and stores it in the existing plugin registry.

This is also an important isolation rule for Version 3.1.0: introducing
`octopus-adjustment` does not require an amendment to existing application main
code or CLI registration logic.

## Package structure

```text
com.towermarsh.opendata.plugin.octopusadjustment
├── initialise
├── extract
├── transform
├── load
└── finalise
```

This follows the existing plugin-local staged pipeline.

## Initialise

The initialise stage builds typed configuration and validates:

- plugin id is `octopus-adjustment`;
- account number is present;
- input directory is configured;
- working directory is configured;
- archive directory is configured; and
- execution context is valid.

The account number is used as the source-file prefix.

## Extract

Candidate example:

```text
A-5F191685-419015087-1.pdf
```

Candidate selection is based on:

```text
<account-number>-*.pdf
```

The extract stage should reuse the ordinary Octopus implementation where
practical for:

- directory scanning;
- file-size capture;
- SHA-256 calculation;
- PDF text extraction; and
- extracted-source metadata.

A copied class should only be retained where plugin-specific filename or ledger
behaviour prevents clean reuse. Shared behaviour should preferably delegate to
the established implementation rather than diverge.

## Transform

The transform stage should reuse the ordinary Octopus electricity/gas parsing
logic because adjustment billing records have the same target record structure.

The output remains logically equivalent to:

```text
OctopusParseResult
├── electricityRecords
└── gasRecords
```

The adjustment plugin may reuse the existing model records directly if their
visibility and dependency rules permit.

The transform stage must obtain bill dates from PDF content. The adjustment
filename is not a date source.

## Load

The load stage is intentionally unique.

Ordinary Octopus data writes to:

```text
octopus.electric_data
octopus.gas_data
octopus.statement_file
```

Adjustment processing instead writes to:

```text
octopus.adjustment_electric_data
octopus.adjustment_gas_data
octopus.adjustment_file
```

The business-table structure and natural keys match the ordinary equivalents,
but the physical separation preserves billing provenance.

### Transaction boundary

One `JdbcTransactionTemplate` transaction should contain:

1. adjustment electricity upserts;
2. adjustment gas upserts; and
3. adjustment-file completion.

A failure rolls back all three groups.

The generic `JdbcUpsertExecutor` infrastructure should continue to provide the
common existence/insert/update iteration.

Adjustment-specific adapters own the adjustment table SQL and bindings.

## Finalise

Finalise runs after a successful write commit.

It:

- moves successfully committed PDFs to the configured adjustment archive;
- leaves dry-run files untouched;
- logs archive failures separately from database failure; and
- reports final processing metrics.

As with the ordinary Octopus plugin, archive movement cannot participate in the
database transaction.

## Idempotency

Adjustment source identity is:

```text
lower(filename) + SHA-256
```

Write mode checks the adjustment ledger before loading.

The adjustment ledger is separate from `octopus.statement_file`, preventing an
ordinary statement and an adjustment bill from sharing processing state.

## Data provenance

The source filename, hash and run id preserve traceability from adjustment data
to its PDF source and OpenData execution.

## Dependency direction

The new plugin may depend on stable public/shared Octopus parsing types where
appropriate, but the ordinary `octopus` plugin must not depend on
`octopus-adjustment`.

The adjustment-specific load layer must not be reused by the ordinary plugin.

## Dry-run boundary

Dry run executes the non-persistent path through extraction and transformation.
It must not:

- check/update completion state in a way that changes data;
- write adjustment business tables;
- write adjustment-file completion state; or
- archive files.

## Failure model

| Failure | Required result |
|---|---|
| Invalid configuration | Fail before extraction |
| Unsupported/corrupt PDF | Fail source parsing; do not complete/archive source |
| Electricity load failure | Roll back transaction |
| Gas load failure | Roll back transaction |
| Ledger completion failure | Roll back transaction |
| Archive move failure after commit | Preserve committed database result and log reconciliation warning |

## Diagram

::: {.landscape}
![Octopus adjustment processing](../diagrams/generated/octopus-adjustment-processing.svg){width=22.5cm}

![Octopus adjustment data model](../diagrams/generated/octopus-adjustment-data-model.svg){width=22.5cm}
:::
