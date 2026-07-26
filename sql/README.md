# SQL Server Deployment Order

The current scripts are split between this folder and `sql/sqlserver`. A clean
installation must use the combined order:

1. `sqlserver/001-create-database-and-login.sql`
2. `sqlserver/010-create-core-schema.sql`
3. `sqlserver/020-create-ofgem-schema.sql`
4. `sqlserver/030-seed-reference-data.sql`
5. `001-core-plugin-run.sql`
6. `002-openmeteo.sql`
7. `sqlserver/090-grant-application-permissions.sql`
8. `003-permissions.sql`

Supply the login password only through the SQLCMD variable. The application
connects to database `OpenData` as user `OpenData`. `900-verification-queries.sql`
is optional and read-only.

The split ordering and overlapping core/permission concerns are a documented
gap. Use the [bootstrap guide](../docs/guides/sql-server-bootstrap.md) until a
single migration manifest replaces it.
