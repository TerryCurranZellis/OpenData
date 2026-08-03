# 3. SQL Server Setup

**Document ID:** USER-003  
**Version:** 2.0  
**Status:** Version 2.0.0 procedure  
**Baseline date:** 2 August 2026

---

Use the scripts in `/sql` in numeric order. Test the full installation against a
non-production SQL Server instance before using live data.

## Installation order

1. `001-create-database-and-login.sql`
2. `002-create-core-schema.sql`
3. `003-create-configuration-store.sql`
4. `004-create-ofgem-schema.sql`
5. `005-seed-reference-data.sql`
6. `006-create-plugin-run-audit.sql`
7. `007-create-openmeteo-schema.sql`
8. `007a-create-octopus-schema.sql`
9. `008-grant-application-permissions.sql`
10. `009-grant-shared-schema-permissions.sql`
11. `010-verification-queries.sql` as a verification aid

The application expects database `OpenData` and, by default, login/user
`OpenData`. Supply the login password locally; do not place it in committed SQL,
documentation or shell-history files.

## Certificate preparation

The registration process needs:

```text
src/main/resources/config/security/opendata-config-public.cer
src/main/resources/config/security/opendata-config-private.pfx
```

The repository's development PFX uses `nopassword`. Replace the certificate pair
and password for a production installation and restrict the private file to the
application identity.

## Before registration

Confirm that:

- `core.application_property` and `core.plugin_property` exist;
- `core.PluginRun` exists;
- Ofgem, OpenMeteo and Octopus schemas and tables exist;
- `octopus.statement_file` exists;
- the application principal can read configuration and write only the required
  operational tables; and
- a recoverable database backup has been taken.

Then follow the [Version 2.0.0 quick start](../guides/quick-start.md).
