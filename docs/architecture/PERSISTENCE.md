# SQL Server Persistence Architecture

## 1. Purpose

The persistence layer loads validated plugin records and stores operational metadata.

## 2. Database separation

Recommended schemas:

```text
opendata       Published dataset tables
opendata_stage Optional staging tables
opendata_admin Run, source, and rejection metadata
```

The exact names may be configurable but should be consistent across plugins.

## 3. Shared metadata tables

### `opendata_admin.ingestion_run`

Suggested columns:

| Column | Type | Purpose |
|---|---|---|
| `run_id` | `uniqueidentifier` | Primary run identity |
| `plugin_id` | `varchar(100)` | Selected plugin |
| `plugin_version` | `varchar(50)` | Plugin version |
| `started_at_utc` | `datetime2(3)` | Run start |
| `finished_at_utc` | `datetime2(3)` | Run finish |
| `status` | `varchar(30)` | Final state |
| `configuration_hash` | `char(64)` | Safe effective configuration fingerprint |
| `records_read` | `bigint` | Input count |
| `records_accepted` | `bigint` | Valid count |
| `records_rejected` | `bigint` | Rejected count |
| `records_inserted` | `bigint` | Insert count |
| `records_updated` | `bigint` | Update count |
| `records_unchanged` | `bigint` | Unchanged count |
| `failure_category` | `varchar(50)` | Stable failure classification |
| `failure_message` | `nvarchar(2000)` | Safe diagnostic message |

### `opendata_admin.source_artifact`

Suggested columns:

| Column | Type | Purpose |
|---|---|---|
| `source_artifact_id` | `bigint identity` | Surrogate key |
| `run_id` | `uniqueidentifier` | Owning run |
| `source_uri` | `nvarchar(2048)` | Provider source |
| `retrieved_at_utc` | `datetime2(3)` | Retrieval time |
| `local_path` | `nvarchar(1024)` | Archived source |
| `sha256` | `char(64)` | Content checksum |
| `content_type` | `varchar(255)` | Response media type |
| `content_length` | `bigint` | Bytes stored |
| `etag` | `nvarchar(512)` | HTTP ETag where supplied |
| `last_modified` | `nvarchar(255)` | Provider value where supplied |

### `opendata_admin.record_rejection`

Suggested columns:

| Column | Type | Purpose |
|---|---|---|
| `rejection_id` | `bigint identity` | Surrogate key |
| `run_id` | `uniqueidentifier` | Owning run |
| `plugin_id` | `varchar(100)` | Dataset plugin |
| `source_row_number` | `bigint` | Source location |
| `error_code` | `varchar(100)` | Stable validation code |
| `field_name` | `varchar(255)` | Field in error |
| `safe_value` | `nvarchar(1000)` | Optional safe value |
| `message` | `nvarchar(2000)` | Diagnostic explanation |

## 4. Plugin tables

Each plugin owns its business tables and migration scripts.

For Ofgem, table design should include:

- A documented natural key.
- `run_id` or source lineage reference.
- Source effective dates.
- Source publication dates where available.
- Insert and update timestamps.
- Appropriate unique constraints.
- Indexes based on expected queries.
- Exact decimal types for monetary and tariff values.

## 5. Transaction boundaries

A normal load should use:

1. Create or update run row to `RUNNING`.
2. Load source metadata.
3. Start the dataset transaction.
4. Stage or batch records.
5. Apply inserts and updates.
6. Commit.
7. Mark run `SUCCEEDED`.

On failure:

1. Roll back dataset changes.
2. Record failure metadata using a separate safe transaction.
3. Return a failed run report.

## 6. Connection management

- Use the Microsoft JDBC driver.
- Create connections through `SqlServerConnectionFactory`.
- Use try-with-resources.
- Disable auto-commit for transactional work.
- Set transaction isolation explicitly where needed.
- Do not log passwords or complete credential-bearing connection strings.

## 7. Batch loading

- Use prepared statements.
- Use a configurable positive batch size.
- Flush at batch boundaries.
- Count affected rows.
- Map SQL exceptions to plugin-safe errors.
- Avoid holding all source records in memory for large datasets.

## 8. Idempotent loading

Recommended strategies, in order:

1. Unique natural key plus separate update/insert.
2. Staging table plus set-based comparison.
3. Stored procedure with explicit transaction.
4. Carefully reviewed `MERGE` only where its behaviour is acceptable.

## 9. Schema migration

Database DDL must be version controlled, for example:

```text
src/main/resources/db/migration/
├── V001__create_admin_schema.sql
├── V002__create_run_metadata.sql
└── V100__create_ofgem_tables.sql
```

A migration tool may be introduced later through a separate ADR.

## 10. Performance guidance

- Index natural keys.
- Avoid row-by-row existence queries.
- Prefer streaming and JDBC batching.
- Measure before increasing parallelism.
- Retain execution plans for important set-based statements.
- Use `datetime2` rather than legacy `datetime`.
- Use exact numerics for financial values.
