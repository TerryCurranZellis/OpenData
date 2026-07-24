# SQL Server database installation

The target database is `OpenData`. The initial SQL login and database user are
both named `OpenData`; the application password is intentionally absent from
source control.

## Prerequisites

- A local or remote SQL Server instance.
- `sqlcmd`, SQL Server Management Studio, Azure Data Studio, or another client
  capable of executing SQLCMD variables and `GO` batch separators.
- An administrator account for the bootstrap scripts.

## Installation order

Run the scripts in this order:

1. `001-create-database-and-login.sql`
2. `010-create-core-schema.sql`
3. `020-create-ofgem-schema.sql`
4. `030-seed-reference-data.sql`
5. `090-grant-application-permissions.sql`

Example using Windows integrated authentication for the administrator:

```powershell
sqlcmd -S localhost -E `
  -i .\sql\sqlserver\001-create-database-and-login.sql `
  -v OpenDataPassword="YOUR_LOCAL_PASSWORD"

Get-ChildItem .\sql\sqlserver\0[1-9][0-9]-*.sql |
    Sort-Object Name |
    ForEach-Object {
        sqlcmd -S localhost -E -d OpenData -b -i $_.FullName
    }
```

The real password must never be added to Git. The JDBC configuration should use:

```properties
database.url=jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true
database.user=OpenData
database.password=<local value>
```

`trustServerCertificate=true` is suitable for a local development instance. A
production deployment should use a trusted server certificate and set it to
`false`.

## Schemas

- `core` contains dataset registration, ingestion runs, source files, errors,
  and schema-version history.
- `ofgem` contains price-cap periods, dimensions, annual levelised cap values,
  and a reserved component-value table for the historical/component outputs.

The `OpenData` user is a member of `opendata_app`, which receives operational
DML permissions but not database-owner or schema-change rights.
