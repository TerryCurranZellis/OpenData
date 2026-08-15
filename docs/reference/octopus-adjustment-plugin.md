# Octopus Adjustment Plugin Reference

**Document ID:** REF-OCTOPUS-ADJUSTMENT-PLUGIN-001  
**Version:** 3.1.0  
**Baseline date:** 15 August 2026  

---

## Identity

| Item | Value |
|---|---|
| Plugin id | `octopus-adjustment` |
| Dataset id | `octopus-energy-adjustments` |
| Java root package | `com.towermarsh.opendata.plugin.octopusadjustment` |
| Source | Local Octopus adjustment PDF |
| Target | Adjustment-specific electricity/gas tables |

## Proposed processing types

| Type | Responsibility |
|---|---|
| `OctopusAdjustmentPlugin` | OpenData plugin entry point |
| `OctopusAdjustmentConfiguration` | Typed paths and account-number configuration |
| `OctopusAdjustmentInitialise` | Stage orchestration and setup |
| `OctopusAdjustmentExtract` | Account-prefix discovery, hashing and PDF extraction |
| `OctopusAdjustmentTransform` | Delegates compatible statement parsing |
| `OctopusAdjustmentLoad` | Dry-run/write load boundary |
| `OctopusAdjustmentPersistenceRepository` | Transactional adjustment persistence |
| `AdjustmentElectricityRecordUpsertAdapter` | Adjustment electricity SQL and bindings |
| `AdjustmentGasRecordUpsertAdapter` | Adjustment gas SQL and bindings |
| `OctopusAdjustmentPersistenceResult` | Insert/update/skip counts |
| `OctopusAdjustmentFinalise` | Post-commit archive and completion reporting |

Exact class decomposition may be reduced during implementation where direct
reuse makes a wrapper unnecessary.

## Reused Octopus types

Where visibility and dependency rules allow, Version 3.1.0 should reuse the
existing ordinary Octopus processing types rather than clone large parsing
implementations.

Primary candidates are:

```text
PdfTextExtractor
ExtractedOctopusStatement
OctopusStatementParser
OctopusParseResult
ElectricityRecord
GasRecord
```

`OctopusStatementParser` is particularly important because duplicating its
statement interpretation would create two independent parsing rule sets for the
same billing structure.

## Extraction contract

Input:

```text
OctopusAdjustmentConfiguration
```

Output:

```text
List<ExtractedOctopusStatement>
```

or an adjustment wrapper carrying equivalent metadata.

Candidate filenames begin with:

```text
<account-number>-
```

and end with `.pdf`.

## Transform contract

Input is extracted PDF text.

Output contains zero or more electricity and gas records.

The same validation expectations as ordinary Octopus billing records apply.

## Persistence contract

`OctopusAdjustmentPersistenceRepository` should execute all business and ledger
writes in one transaction.

The repository must never target:

```text
octopus.electric_data
octopus.gas_data
octopus.statement_file
```

It targets only the adjustment tables.

## Upsert result

The load result should expose combined counts suitable for the common plugin run
result:

```text
inserted
updated
skipped
```

It may additionally expose electricity/gas subtotals if useful for logging and
testing.

## Finalise contract

Write mode archives only sources whose adjustment data committed successfully.

Dry run performs no archive movement.

## Versioning

All new adjustment plugin classes and their Javadoc are introduced in:

```text
@since 3.1.0
```

The existing Version 3.0.0 ordinary Octopus API documentation remains unchanged.
