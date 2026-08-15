# Octopus Schema and Persistence Reference

**Document ID:** REF-OCTOPUS-SCHEMA-001
**Version:** 3.0.0  
**Baseline date:** 15 August 2026  

## Tables

| Table | Purpose |
|---|---|
| `octopus.electric_data` | electricity tariff-period and meter-reading facts |
| `octopus.gas_data` | gas tariff-period, meter-reading and conversion facts |
| `octopus.statement_file` | source-file hash, status and processing ledger |

## Natural-key upserts

Electricity and gas each retain a provider-specific natural key composed from
bill date, tariff-period dates, tariff name, supply identifier, meter id and
reading dates. `JdbcUpsertExecutor` performs the common decision loop, while the
typed adapters own key queries and column bindings.

This is a record-by-record upsert strategy. It is suitable for statement-sized
batches and is intentionally distinct from OpenMeteo's set-based staging model.

## Atomic statement processing

Electricity, gas and statement-ledger operations use one
`JdbcTransactionTemplate` transaction. The `statement_file` row is marked
`COMPLETED` only after both record groups have been processed successfully in
the transaction.

## Source-file identity

The completion ledger uses filename plus SHA-256. A changed file with the same
name is eligible for processing. Archive movement follows commit and is not part
of the SQL transaction.
