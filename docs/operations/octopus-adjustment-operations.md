# Octopus Adjustment Operations

**Document ID:** OPS-OCTOPUS-ADJUSTMENT-001  
**Version:** 3.1.0  
**Baseline date:** 15 August 2026  

---

## Operating objective

Process Octopus adjustment PDFs reproducibly while keeping recalculated billing
data separate from ordinary Octopus statement data.

## Registration prerequisite

Register `octopus-adjustment` through the OpenData **GUI only**. New-plugin
registration is not a CLI operation. Once the GUI has registered the plugin,
the CLI can be used for `--detail`, `--dry-run` and `--execute`.

## Pre-run checks

Before a write execution verify:

1. the plugin has been registered through the GUI and is enabled;
2. the account-number prefix is correct;
3. the input directory is readable;
4. the archive directory is writable;
5. the database schema update has been applied;
6. the relevant PDF is not open/locked by another program; and
7. a dry run parses the expected electricity/gas records.

## Recommended first-run sequence

```text
opendata --plugin octopus-adjustment --detail
opendata --plugin octopus-adjustment --dry-run
opendata --plugin octopus-adjustment --execute
```

## Expected source selection

For account:

```text
A-5F191685
```

a valid example is:

```text
A-5F191685-419015087-1.pdf
```

Unrelated PDF files in the same directory must be ignored.

## Post-run checks

After a successful write run verify:

- expected electricity/gas row counts;
- `last_run_id` links to the successful plugin run;
- source appears in `octopus.adjustment_file` as completed;
- source PDF moved to the adjustment archive; and
- ordinary `octopus.electric_data` and `octopus.gas_data` were not changed by
  the adjustment plugin.

## Duplicate source

If the same filename and SHA-256 are already complete, the source should be
skipped.

This is normal idempotent behaviour.

## Same filename, changed file

If the filename is reused but the contents change, the SHA-256 differs and the
source should be processed again.

Investigate why the source changed before accepting the resulting data as
authoritative.

## Parse failure

Symptoms can include:

- zero records where records are expected;
- missing bill dates;
- missing meter identifiers;
- invalid period ordering; or
- an unsupported Octopus PDF layout.

Action:

1. retain the source in input;
2. do not mark it complete manually;
3. inspect the dry-run log;
4. compare its layout with a previously supported adjustment;
5. amend transformation rules if required; and
6. rerun dry-run.

## Database failure

The transaction must roll back adjustment electricity, gas and completion
ledger changes together.

Do not manually mark the file complete after a failed transaction.

## Archive failure after commit

If the database committed but the file could not be archived:

1. treat the database load as successful;
2. inspect the adjustment ledger;
3. correct directory permissions/file locks;
4. move the committed file to the archive manually if appropriate; and
5. preserve the original filename.

A subsequent normal run should skip the completed name/hash pair rather than
loading the data again.

## Recovery query examples

Confirm file completion:

```sql
SELECT
    file_name,
    sha256,
    status,
    last_run_id,
    processed_at
FROM octopus.adjustment_file
ORDER BY processed_at DESC;
```

Review adjustment electricity:

```sql
SELECT *
FROM octopus.adjustment_electric_data
ORDER BY bill_date DESC, tariff_period_start;
```

Review adjustment gas:

```sql
SELECT *
FROM octopus.adjustment_gas_data
ORDER BY bill_date DESC, tariff_period_start;
```

## Backup and privacy

Include the adjustment tables in normal OpenData database backup policy.

Treat source and archived PDFs as personal financial data.
