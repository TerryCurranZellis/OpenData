# SQL Server Database Installation

The target database is `OpenData`. The initial SQL login and database user are
both `OpenData`; the application password is intentionally absent from source
control. Normal runtime access is granted through role `opendata_app`.

## Installation order

1. `001-create-database-and-login.sql`
2. `010-create-core-schema.sql`
3. `020-create-ofgem-schema.sql`
4. `030-seed-reference-data.sql`
5. `090-grant-application-permissions.sql`

Run script 001 with a privileged server identity and a SQLCMD variable for the
local password. Run the remaining scripts against database `OpenData` in numeric
order. The scripts are designed to be safely rerunnable and record logical
versions in `core.schema_version`.

## Security

`trustServerCertificate=true` is for local development only. Production must use
a trusted SQL Server certificate and `trustServerCertificate=false`. The
application user must not be granted `db_owner` merely to resolve installation or
runtime errors.

## Documentation

- [Bootstrap guide](../../docs/guides/sql-server-bootstrap.md)
- [Configuration and security](../../docs/guides/database-configuration-and-security.md)
- [Schema reference](../../docs/reference/database-schema-reference.md)
- [Troubleshooting](../../docs/guides/database-troubleshooting.md)
