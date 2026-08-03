# 4. Configuration

**Document ID:** USER-004  
**Version:** 2.0  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 3 August 2026

---

## Configuration layers

OpenData resolves configuration from:

1. built-in runtime defaults;
2. the repository-local bootstrap file;
3. classpath properties before registration, or SQL Server properties after
   registration; and
4. optional `--file` overrides for the current invocation.

The writable bootstrap file is:

```text
src/main/resources/config/application.properties
```

After registration it contains only the version marker, database-backed switch,
database URL, database user and encrypted database password.

## Registration

Create a protected bootstrap override file outside the repository:

```properties
application.database.url=jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true
application.database.user=OpenData
application.database.password=<local-database-password>
```

Run:

```text
opendata --register --file C:\OpenData\bootstrap.properties
```

Registration:

- upserts application defaults and packaged application properties into
  `core.application_property`;
- replaces each installed plugin's rows in `core.plugin_property`;
- stores the database password encrypted in SQL Server; and
- rewrites the local bootstrap file with
  `application.use-database-properties=true` and an encrypted password.

The database writes and bootstrap-file rewrite are not one atomic transaction.
After an interrupted or failed registration, inspect both the database tables and
the local bootstrap file before retrying.

## Override scopes

Application overrides always use `application.<key>`:

```properties
application.execution.max-parallel-plugins=2
application.logging.directory=C:\OpenData\logs
```

A single-plugin run may use unscoped plugin keys:

```properties
property.start-date.value=2025-01-01
```

A multi-plugin run must scope all plugin values:

```properties
plugin.openmeteo.property.start-date.value=2025-01-01
plugin.ofgem.property.download.request-timeout.value=PT180S
```

Unknown properties may remain unused; the runtime validates only values that a
configuration class resolves. Keep override files minimal and review them after
upgrades.

## Security warning

The uploaded baseline contains a tracked plaintext bootstrap credential and a
tracked private PFX. Remove them from source control, replace the certificate
pair, and rotate any database password that has been exposed before release or
production use.
