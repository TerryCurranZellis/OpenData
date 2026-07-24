# Run and Verify an Ofgem Price-Cap Import

**Document ID:** GUIDE-OFGEM-RUN-001  
**Version:** 1.0  
**Status:** Integration procedure  
**Baseline date:** 24 July 2026

## Before running

- apply the Phase 2 and Phase 3 overlays;
- create and seed the `OpenData` database;
- configure the external database password;
- verify outbound access to the Ofgem landing page;
- run `mvn clean test`.

## Execution boundary

The Phase 3 package supplies the extractor, import service, repositories and audit
foundation. It does not yet add the final CLI plugin orchestration. After that
wiring is integrated, invoke the Ofgem plugin through the normal CLI/configuration
path and do not bypass audit run creation for a production import.

Before the runtime wiring exists, verify these components with unit/integration
tests or a temporary test harness rather than claiming a production plugin run.

## Database verification

```sql
SELECT TOP (10) *
FROM core.ingestion_run
ORDER BY ingestion_run_id DESC;

SELECT effective_from, effective_to, period_name, is_current
FROM ofgem.price_cap_period
ORDER BY effective_from DESC;

SELECT p.period_name, COUNT(*) AS fact_count
FROM ofgem.price_cap_level l
JOIN ofgem.price_cap_period p
  ON p.price_cap_period_id = l.price_cap_period_id
GROUP BY p.period_name
ORDER BY MAX(p.effective_from) DESC;
```

For the workbook structure used during Phase 3 validation, the primary extraction
produced 384 populated annual cap-level values. Treat that as a fixture-specific
check, not a permanent contractual count for every future workbook.

## Lineage check

Select representative values with `source_sheet` and `source_cell`, open the
recorded source workbook, and confirm the values and dimensional labels match.
