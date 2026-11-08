# OpenData 4.0.0: first-run configuration wizard

**Document ID:** GUIDE-INIT-040  
**Release:** 4.0.0  
**Applies to:** SQL Server installations running the JavaFX desktop application

## When the wizard runs

A new installation has a writable `config/application.properties` containing:

```properties
init=true
application.version=4.0.0
application.use-database-properties=false
```

The shipped resource inside the core JAR also has `init=true`, so a first run
without a writable file opens the wizard. The path is relative to the process
working directory (the NetBeans project working directory can differ from the
repository root). The wizard runs **before** the normal GUI or CLI requests.
It is not a command-line option. A desktop session with JavaFX is required.
While `init=true`, even a command-line invocation opens the GUI wizard; a
headless session fails with an explanation instead of attempting SQL access.

## Before starting

- Install a reachable SQL Server instance with TCP/IP enabled (default port 1433).
- Have an existing SQL-authenticated administrator account with `CREATE DATABASE`,
  `CREATE LOGIN`, database object DDL, user/role management and permission-granting rights.
- Supply a **different** strong SQL application login/password. If the SQL login
  already exists, the wizard does not reset its password; provide its current one.
- Use a writable, private path **outside source control** for the 32-byte AES key.
- Prefer a server certificate trusted by the computer. The wizard has a local-
  development-only option to trust a self-signed server certificate.
- Make a backup before re-running the wizard against an existing database.

## Wizard steps

1. **SQL Server:** enter the hostname/IP, port, desired database name (default
   `OpenData`) and *temporary* administrator SQL credentials. Choose whether to
   trust a self-signed certificate. Names are limited to letters, digits and
   underscores, and must start with a letter.
2. **Application credentials:** enter the normal OpenData SQL login, its
   password twice, and the AES key file path. If the key does not exist, setup
   generates a cryptographically random 256-bit AES key. An existing valid key
   is reused without overwriting it.
3. **Review / initialise:** confirm the database name, user and key location.
   Setup creates the database (if absent), core schema, configuration tables,
   registry, run-audit table, and `opendata_app` role. It grants the application
   login database access, installs all discovered plugin schemas using the
   temporary administrator connection, then persists plugin definitions with
   both `core.plugin_registry.is_enabled=0` and
   `core.plugin_property.plugin.enabled=false`.
4. **Finish:** the wizard writes the encrypted database password and key file
   location to the deployment properties file, changes `init=false`, and opens
   the ordinary OpenData GUI. **No plugin runs automatically.** Enable plugins
   individually after verifying configuration.

Plugins are discovered both from the packaged classpath indexes and from
`config/plugins/*.properties`, including development source-tree fallback
paths. External files take precedence when the same ID is also packaged;
conflicting external files with the same plugin ID cause an error. An external-only installation with no packaged plugin index is supported;
plugins must still have their implementation classes on the runtime classpath.

## Completed configuration

The writable file (not the JAR resource) contains keys similar to:

```properties
init=false
application.version=4.0.0
application.use-database-properties=true
database.url=jdbc:sqlserver://localhost:1433;databaseName=OpenData;encrypt=true;trustServerCertificate=false;loginTimeout=15
database.user=OpenData
database.password={aes}<encrypted-payload>
security.aes-key-file=C:/OpenDataSecrets/opendata-aes.key
```

The exact output is Java `Properties`-escaped; it must be read through the normal
property loader rather than parsed as simple unescaped text. Passwords use
AES-256-GCM with a new random nonce for every encrypted value. The AES key file
is **not encrypted** and must be protected with NTFS permissions. Administrator
credentials are never stored. The encrypted application credential is also
written to `core.application_property` with `is_encrypted=1`.

## Safe failure and recovery

If setup fails while creating SQL tables or registering plugins, the wizard
reports the failed operation, **does not set `init=false`**, and does not launch
the main GUI. SQL Server DDL and registration cannot be rolled back as a single
cross-database transaction. The schema scripts and registry upserts are intended
to be safe to retry; inspect partial database objects, correct the problem, and
start the wizard again. Existing SQL logins and valid AES key files are not
recreated or overwritten. An existing database is not deleted.

If all SQL operations succeed but the final properties-file write fails, fix
file permissions and rerun. Do **not** edit `init=false` manually until the
registry and bootstrap values have been checked.

## Upgrade from OpenData 3.x

The old loader wrote to
`src/main/resources/config/application.properties` in the source tree. Version
4 uses the deployment path `config/application.properties`. For an existing,
working database, **copy the current bootstrap settings to the new location**,
add the existing AES key path as `security.aes-key-file`, and set `init=false`.
Do not use the first-run wizard to rebuild or re-register an already-running
production installation. Back up both the properties file and AES key before
upgrading. The previous JVM property `opendata.config.key-file` and environment
variable `OPENDATA_CONFIG_KEY_FILE` are deprecated fallbacks; their replacement
is `security.aes-key-file` in the properties file. The legacy `{enc}` RSA format
remains unsupported and must be migrated to `{aes}`.

## Security consideration

The current normal plugin-registration design runs database DDL via the
application connection, so the wizard adds the application login to SQL
Server's `db_ddladmin` database role to support future registrations.
This is more privileged than a pure data-processing account and is a deliberate
trade-off in this existing architecture. For hardened production deployments,
use separate administrative provisioning workflows and restrict normal runtime
permissions after setup; future plugin registrations will then need a suitably
privileged administrator session. TLS trust bypass should not be used for
remote or production databases.

## Verification queries

```sql
USE [OpenData];
SELECT [plugin_id], [is_enabled] FROM [core].[plugin_registry] ORDER BY [plugin_id];
SELECT [plugin_id], [property_value]
FROM [core].[plugin_property] WHERE [property_key] = 'plugin.enabled';
SELECT [property_key], [is_encrypted]
FROM [core].[application_property] WHERE [property_key] = 'database.password';
```

All plugin enabled values should be `0`/`false` until an operator enables them.
