# 3. SQL Server Setup

**Document ID:** USER-003  
**Version:** 1.0  
**Status:** Pre-production procedure  
**Baseline date:** 26 July 2026

---

Use the SQL scripts in their documented order. The repository currently has a
transitional split:

- `sql/001-core-plugin-run.sql` and `sql/002-openmeteo.sql` create the generic
  plugin-run and weather model;
- `sql/sqlserver/` creates the database, older ingestion provenance, Ofgem model,
  seed data and permissions.

Follow both SQL readmes and verify every script against a non-production SQL
Server first. Rerun idempotent scripts to prove repeatability.

The application expects database `OpenData`, login/user `OpenData` and the
least-privilege grants created by the SQL scripts. Use a real trusted certificate
outside local development.

Before the first write:

1. confirm both `core.PluginRun` and the Ofgem/OpenMeteo tables exist;
2. confirm Ofgem seed data exists;
3. test the application principal;
4. take a database backup;
5. run one plugin at a time.
