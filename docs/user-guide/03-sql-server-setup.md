# 3. SQL Server Setup

**Document ID:** USER-003  
**Checked-in source:** OpenData Maven 4.2.0  
**First-run feature baseline:** 4.0.0

## Preferred setup: JavaFX wizard

For a new installation, use the [first-run initialisation guide](../guides/first-run-initialisation.md) rather than running SQL manually. With `init=true`, the wizard requests a temporary SQL administrator login, a distinct application login, a strong application password and the AES key file location.

The wizard creates or verifies the OpenData database, core schema, configuration and registry tables, plugin run audit and application role. Each discovered provider's `RegisterPlugin` hook installs its provider-specific schema under temporary administrator rights. The providers are registered **disabled**, and normal GUI use begins only after successful completion sets `init=false`.

The SQL Server must be reachable over TCP/IP. Use a valid server certificate where possible; bypassing validation is a development-only option.

## Manual provisioning and recovery

Administrators can execute the ordered scripts in [sql/README.md](../../sql/README.md); see the [SQLCMD bootstrap example](../guides/sql-server-bootstrap.md). The manual core sequence is:

1. `001-create-database-and-login.sql`
2. `002-create-core-schema.sql`
3. `003-create-configuration-store.sql`
4. `003a-create-plugin-registry.sql`
5. `006-create-plugin-run-audit.sql`
6. `008-grant-application-permissions.sql`
7. `009-grant-shared-schema-permissions.sql`
8. `010-verification-queries.sql` (read-only)

Provider SQL resources are owned by their provider modules at `opendata-plugins/opendata-plugin-*/src/main/resources/sql/plugins/`, not by the top-level SQL folder.

## Verify safely

Verify `core.application_property`, `core.plugin_registry`, `core.plugin_property` and the plugin-run audit tables. Check each registered provider's table/schema and that the registry's `is_enabled` values are false after initial setup. Do not run registration DDL with an unnecessarily privileged normal runtime account; use the provisioning/administrator access model documented for your deployment.

The old RSA `.cer`/`.pfx` certificate bootstrap is not part of the current AES-256-GCM installation. Protect the external AES key file and keep database passwords outside documentation. See [Configuration](04-configuration.md) and [Database access security](../guides/database-configuration-and-security.md).
