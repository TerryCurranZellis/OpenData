# 3. SQL Server Setup

**Document ID:** USER-003  
**Version:** 2.0  
**Status:** Version 2.0.0 procedure  
**Baseline date:** 3 August 2026

---

Run the scripts in `/sql` in numeric order against a non-production instance
first.

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
11. `010-verification-queries.sql` as a read-only verification aid

The application expects database `OpenData` and normally connects as login/user
`OpenData`. Supply the login password locally through the SQLCMD variable; do not
commit it or place it in documentation.

## Certificate preparation

The current implementation expects these paths relative to the repository root:

```text
src/main/resources/config/security/opendata-config-public.cer
src/main/resources/config/security/opendata-config-private.pfx
```

Create a replacement pair before production use. A password-protected PFX can be
opened with:

```text
-Dopendata.config.keystore.password=<password>
```

Do not rely on `OPENDATA_CONFIG_KEYSTORE_PASSWORD` in this baseline. The code
currently looks for an environment variable literally named `nopassword`.

## Before registration

Confirm that:

- `core.application_property` and `core.plugin_property` exist;
- `core.PluginRun` exists;
- the `ofgem`, `openmeteo` and `octopus` schemas exist;
- the application principal has the supplied grants but not schema-owner rights;
- the certificate private key is protected and not stored in a public source
  tree; and
- a recoverable database backup or snapshot exists.

Then follow [Configuration](04-configuration.md) and the
[SQL Server bootstrap guide](../guides/sql-server-bootstrap.md).
