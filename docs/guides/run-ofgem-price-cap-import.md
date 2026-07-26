# Run and Verify an Ofgem Price-Cap Import

**Document ID:** GUIDE-OFGEM-RUN-001  
**Version:** 1.1  
**Status:** Current operating procedure  
**Baseline date:** 26 July 2026

## Before running

- create and seed the `OpenData` database;
- configure the external database password;
- verify outbound access to the Ofgem landing page;
- run `mvn clean test`.

## Dry run

```text
opendata --plugin ofgem --dry-run
```

The dry run performs discovery, download and workbook extraction. It does not
archive the workbook, initialise the database pool or create either audit model.

## Write run

Create an external file:

```properties
application.database.password=<secret>
```

Then run:

```text
opendata --plugin ofgem --file C:\OpenData\ofgem.properties
```

Use a classpath-aware launcher; executable JAR packaging is not yet configured.

## Database verification

```sql
SELECT TOP (10) *
FROM core.PluginRun
WHERE PluginId = N'ofgem'
ORDER BY StartedAt DESC;

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

Expect one `core.PluginRun` row and one `core.ingestion_run` row for the current
implementation. They are not linked; this is a known gap.

## Lineage check

Select representative values with `source_sheet` and `source_cell`, open the
recorded source workbook, and confirm the values and dimensional labels match.
