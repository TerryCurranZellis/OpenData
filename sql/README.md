# SQL Server Deployment Order

All SQL Server deployment scripts now live in this folder and execute in this
order:

1. `001-create-database-and-login.sql`
2. `002-create-core-schema.sql`
3. `003-create-configuration-store.sql`
4. `003a-create-plugin-registry.sql`
5. `006-create-plugin-run-audit.sql`
6. `008-grant-application-permissions.sql`
7. `009-grant-shared-schema-permissions.sql`

Plugin schemas (`ofgem`, `openmeteo`, `octopus`, `electricdemand`,
`electricsupply`) are no longer created by scripts in this folder. Each plugin
creates its own schema and tables when it is registered, from
`opendata-core/src/main/resources/sql/plugins/<id>.sql`. The login used by
OpenData therefore needs permission to create schemas and tables (for example
membership of `db_owner`, or `CREATE SCHEMA` and `CREATE TABLE`).

Supply the login password only through the SQLCMD variable. The application
connects to database `OpenData` as user `OpenData`. `010-verification-queries.sql`
is optional and read-only.
