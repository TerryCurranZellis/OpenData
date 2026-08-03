# OpenData 2.0.0

**Release status:** Development and release-candidate baseline
**Documentation baseline:** 2 August 2026
**Licence:** Apache License 2.0

OpenData 2.0.0 is a major architectural release. It replaces the broad
file-backed runtime configuration model with database-backed application and
plugin configuration, introduces certificate-protected bootstrap credentials,
standardises plugin processing into five explicit phases, and adds a complete
local-file workflow for Octopus Energy PDF statements.

## Release highlights

### Database-backed configuration

- `application.properties` is reduced to bootstrap values: application version,
  database-backed mode, database URL, database username and database password.
- `--register` copies application runtime properties and installed plugin
  definitions into SQL Server.
- Registration changes `application.use-database-properties` to `true` for later
  runs.
- Application and plugin overrides remain available through `--file`.

### Certificate-backed password protection

- The bootstrap database password is encrypted with the public X.509
  certificate.
- Startup decrypts an `{enc}` value with the matching private key in the PKCS#12
  file before opening the configuration database connection.
- Certificate resources can be loaded from the source tree or packaged
  classpath.
- The supplied development PFX password is `nopassword`. The current runtime
  accepts an override through `-Dopendata.config.keystore.password`.
- The previously documented `OPENDATA_CONFIG_KEYSTORE_PASSWORD` environment
  variable is not honoured by this source baseline because of an implementation
  constant mismatch.
- Production installations must replace the development certificate and PFX
  password and remove the tracked private key and plaintext bootstrap credential
  before release.

### Standard plugin lifecycle

Every plugin now follows:

```text
Initialise -> Extract -> Transform -> Load -> Finalise
```

The root plugin class remains a thin framework entry point. `Initialise` controls
flow, `Extract` acquires source data, `Transform` creates database-ready records,
`Load` persists them, and `Finalise` performs cleanup. Plugin-specific exception
packages are not part of the standard; failures are handled through the common
plugin exception boundary.

### Octopus Energy local statement ingestion

- Scans `C:\Attachments\octopus` by default.
- Accepts files named `octopus-energy-statement-YYYY-MM-DD.pdf`.
- Parses the ISO statement date from the filename.
- Calculates a SHA-256 fingerprint for each candidate file.
- Skips a completed file when both filename and fingerprint match the statement
  ledger.
- Treats changed content under an existing filename as a new input.
- Extracts all new statements and transforms them as one batch.
- Loads electricity, gas and statement-ledger records in one transaction.
- Marks a statement completed only after the transaction commits.
- Archives successfully processed files after a non-dry-run load.
- Supports electricity-only, gas-only and dual-fuel statements.

The current implementation processes user-supplied local PDFs. Octopus Energy API
integration and direct statement download are future work.

### Documentation platform

- The documentation engine remains manifest driven, with one manifest per
  generated guide.
- Version 2.0.0 foundation documents and public entry points have been refreshed.
- New project overview, repository structure, version evolution and documentation
  hierarchy diagrams are included.

## Upgrade considerations from 1.x

- Install the Version 2.0.0 SQL scripts, including configuration tables and the
  Octopus statement ledger.
- Back up the existing database and configuration before migration.
- Create or replace the configuration certificate and keep the private PFX
  protected.
- Run `--register` once with a working plain-text bootstrap password.
- Confirm the rewritten bootstrap password begins with `{enc}` and database mode
  is enabled.
- Review plugin properties in SQL Server rather than treating classpath property
  files as the active runtime store.
- Update custom plugins to the five-phase package model.

See [docs/migration/version-1-to-version-2.md](docs/migration/version-1-to-version-2.md).

## Requirements

- Java 17 or later.
- Maven 3.9 or later when building from source.
- Microsoft SQL Server for registration and persistence.
- PowerShell 5.1 or later for supplied Windows automation.
- Pandoc and PlantUML when rebuilding all documentation formats.

## Known release considerations

- The Maven JAR is not yet a self-contained executable with bundled dependencies
  and a `Main-Class` manifest entry.
- Database integration and live provider tests require environment-specific
  systems and credentials.
- The included certificate and PFX password are development defaults, not a
  production secret-management solution.
- Octopus parsing depends on the layout and text extractability of supplied PDF
  statements; representative statements should be retained outside source
  control for acceptance testing.
- A final public release date and tag should be assigned only after the release
  checklist has passed.
