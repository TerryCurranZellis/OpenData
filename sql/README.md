# SQL Server deployment order

Run the scripts in numeric order against SQL Server:

1. `000-bootstrap-template.sql` only when the database/login/user do not already exist. Supply the password through the SQLCMD variable; do not commit it.
2. `001-core-plugin-run.sql`
3. `002-openmeteo.sql`
4. `003-permissions.sql`

`900-verification-queries.sql` is optional and read-only; use it after the first run to inspect deployed objects, audit rows and weather coverage.

The application must connect to database `OpenData` as user `OpenData`. The table scripts are idempotent for initial deployment. Later structural changes should use versioned migration scripts rather than editing a database already in service.
