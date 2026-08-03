# OpenData 2.0.0 Quick Start

**Document ID:** GUIDE-QUICKSTART-001  
**Version:** 2.0  
**Status:** Current  
**Baseline date:** 2 August 2026

---

This guide provides the shortest safe path from a source checkout to a registered
configuration and first dry run.

## 1. Build the source

```powershell
mvn clean verify
mvn package
```

Configure your IDE or classpath launcher to run:

```text
com.towermarsh.opendata.OpenData
```

## 2. Install SQL Server objects

Run the numbered scripts in `/sql`, including
`007a-create-octopus-schema.sql`. Use the detailed
[SQL Server bootstrap guide](sql-server-bootstrap.md) for the complete order.

## 3. Prepare the certificate

Confirm that the public certificate and private PFX exist under:

```text
src/main/resources/config/security
```

The supplied development PFX password is `nopassword`. For another PFX password,
set one of:

```powershell
$env:OPENDATA_CONFIG_KEYSTORE_PASSWORD = '<pfx-password>'
```

```text
-Dopendata.config.keystore.password=<pfx-password>
```

## 4. Set the initial bootstrap properties

Before registration, `src/main/resources/config/application.properties` should
contain:

```properties
application.version=2.0.0
application.use-database-properties=false
database.url=jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true
database.user=OpenData
database.password=<plain-text-database-password>
```

Use the plain-text password only for the registration run. Protect the file from
other users and do not commit it.

## 5. Register configuration

Run:

```text
--register
```

A successful registration:

- saves application runtime properties in SQL Server;
- saves definitions for `ofgem`, `openmeteo` and `octopus`;
- stores the database password as an encrypted value;
- rewrites the local bootstrap password with an `{enc}` prefix; and
- changes `application.use-database-properties` to `true`.

## 6. Restart and verify

Restart the application so it must decrypt the bootstrap password and read its
configuration from SQL Server. Run:

```text
--list-plugins
```

Expected plugin IDs:

```text
ofgem
openmeteo
octopus
```

## 7. Dry-run plugins

```text
--plugin ofgem --dry-run
--plugin openmeteo --dry-run
--plugin octopus --dry-run
```

For Octopus, place test PDFs outside source control in
`C:\Attachments\octopus` using names such as:

```text
octopus-energy-statement-2026-07-31.pdf
```

A dry run must not create run-audit rows, write business tables, mark statements
completed or archive source PDFs.

## 8. First write run

Back up the database, review logs, and run one plugin at a time before using
`--plugin all`. Confirm that Octopus statement records and the
`octopus.statement_file` ledger commit together and that successful source PDFs
are archived only after the commit.
