# SQL Server Deployment Order

All SQL Server deployment scripts now live in this folder and execute in this
order:

1. `001-create-database-and-login.sql`
2. `002-create-core-schema.sql`
3. `003-create-configuration-store.sql`
4. `003a-create-plugin-registry.sql`
5. `004-create-ofgem-schema.sql`
6. `005-seed-reference-data.sql`
7. `006-create-plugin-run-audit.sql`
8. `007-create-openmeteo-schema.sql`
9. `007a-create-octopus-schema.sql`
10. `008-grant-application-permissions.sql`
11. `009-grant-shared-schema-permissions.sql`

Supply the login password only through the SQLCMD variable. The application
connects to database `OpenData` as user `OpenData`. `010-verification-queries.sql`
is optional and read-only.
