> **Version 4.0.0:** On a new graphical installation, the first-run wizard
> automatically applies the core schema resources and registers plugins disabled.
> Manual SQL scripts remain available for administrators and migrations. See
> [first-run guide](../docs/guides/first-run-initialisation.md).

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

Provider schemas for Ofgem, OpenMeteo, Octopus, Octopus Adjustment, Electric
Demand and Electric Supply are not created by top-level SQL scripts. Each
provider owns its SQL resource at
`opendata-plugins/opendata-plugin-<id>/src/main/resources/sql/plugins/<id>.sql`
and its `RegisterPlugin` class applies that resource at registration. Normal
plugin registration therefore requires suitable DDL privileges; the JavaFX
first-run wizard uses temporary administrator rights for initial schema creation
and grants the separate application account its configured role/permissions.

Supply the login password only through the SQLCMD variable. The application
connects to database `OpenData` as user `OpenData`. `010-verification-queries.sql`
is optional and read-only.
