# Phase 3 — SQL Server persistence and Ofgem import

## Objective

Phase 3 turns the download/parsing foundations into an auditable SQL Server
load for the first reference dataset. It introduces a managed connection pool,
shared ingestion metadata, typed Ofgem records, workbook extraction, and
transactional persistence.

## Connection lifecycle

`SQLServerResource` owns an Apache Commons DBCP `BasicDataSource`. Repositories
borrow a logical `Connection` and use try-with-resources. Calling
`Connection.close()` returns the connection to the pool. The application must
close `DatabaseConnectionManager` once at shutdown so the physical pool is
closed cleanly.

The pool is deliberately not a static singleton. A process normally creates
one manager, but later development can create separate pools for multiple SQL
Server databases, read/write separation, or parallel workloads without global
driver registration.

## Audit flow

1. Find the registered dataset in `core.dataset`.
2. Insert `core.ingestion_run` with status `STARTED`.
3. Download and hash the source file.
4. Insert `core.source_file`.
5. Extract typed Ofgem values.
6. Upsert `ofgem.price_cap_period`.
7. Replace all `ofgem.price_cap_level` rows for that period in one transaction.
8. Complete the run with counters and a terminal status.
9. Store row or stage errors in `core.ingestion_error` when required.

## Ofgem model

The current Annex 9 `1a Levelised DTC` output is an annual value in pounds per
customer. Its dimensions are:

- charge-restriction period;
- charge-restriction region or GB average;
- payment method;
- electricity metering arrangement, gas, or implied dual fuel;
- nil or benchmark annual consumption;
- VAT excluded or, for the explicit GB-average VAT row, VAT included.

The schema therefore stores an `amount_gbp` fact. It does not mislabel this
output as a unit rate or daily standing charge. Those values can be added later
from a source that explicitly publishes them.

## Initial scope boundary

`ofgem.price_cap_component_value` is defined now because the workbook contains
historical component tables, but Phase 3 only implements extraction of the
primary `1a Levelised DTC` output. Component extraction is the next incremental
Ofgem task.
