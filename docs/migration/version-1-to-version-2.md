# Upgrade from OpenData 1.x to 2.0.0

**Document ID:** MIGRATION-2.0-001  
**Version:** 2.0  
**Status:** Current  
**Baseline date:** 2 August 2026

---

Version 2.0.0 changes the configuration model and plugin package contract. Treat
the upgrade as a controlled migration rather than replacing files in a live
installation without review.

## 1. Back up the Version 1.x installation

Retain:

- the complete source and configuration baseline;
- the SQL Server database and schema-version evidence;
- existing plugin property files;
- archived source files and audit records; and
- any deployment-specific certificates or credentials.

Do not include customer statements or credentials in a general source archive.

## 2. Install Version 2.0.0 SQL changes

Apply the numbered SQL scripts in order to a test database first. Confirm the
configuration tables, plugin-run audit objects and plugin schemas, including
`octopus.statement_file`.

## 3. Prepare certificate resources

Provide the matching public `.cer` and private `.pfx` files. The repository's
`nopassword` PFX password is for development only. Establish deployment-specific
file permissions, password storage, backup and rotation.

## 4. Prepare the bootstrap file

Set `application.use-database-properties=false` and supply a working plain-text
database password for the registration run. The file should contain no plugin or
runtime settings.

## 5. Register configuration

Run `--register`. Review the application and plugin rows created in SQL Server.
Confirm the bootstrap file is rewritten with database mode enabled and an `{enc}`
password.

## 6. Restart and verify

Restart the application, run `--list-plugins`, and confirm that decryption and
database-backed configuration loading succeed. A failed certificate lookup or
wrong PFX password must prevent database login rather than silently falling back
to an unrelated password.

## 7. Migrate custom plugins

Move source-specific responsibilities into:

```text
initialise
extract
transform
load
finalise
```

Keep the root plugin class thin. Remove plugin-local exception packages and use
the shared plugin exception handler. Preserve additional packages under
`transform` where required.

## 8. Validate plugins

Dry-run each plugin separately before a write run. For Octopus, use copies of
representative statements outside source control and test duplicate, changed-file,
transaction failure and archive behaviour.

## Rollback

If acceptance fails, restore the Version 1.x database and source baseline rather
than attempting to mix Version 1.x configuration files with Version 2.0.0
runtime code. Preserve logs and migration evidence with secrets and personal data
redacted.
