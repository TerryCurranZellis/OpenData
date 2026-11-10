# OpenData 4.0.0 — first-run provisioning

**Status:** Source implementation delivered; full reactor and live SQL Server
acceptance **not yet verified**.  
**Prepared:** 7 November 2026  
**Minimum supported JDK:** 24. The original 4.0.0 Core POM specified JavaFX 27, which was incompatible with JDK 24; the 4.1.0 review branch uses JavaFX 26.0.2.

## Included

- File-based `init=true` startup gate, independent of the CLI parser/database.
- Three-step JavaFX database/login/encryption setup wizard with progress and
  retry-safe failure behaviour.
- SQL Server database, application login, core schema/table and application role
  creation with resources packaged in `opendata-core`.
- All available packaged and file-based plugins discovered, validated, their
  tables initialised with temporary administrator rights, and registered disabled.
- AES-256-GCM key generation and external file-based key-location setting.
- Deployment `config/application.properties` created atomically; `init=false`
  becomes the success marker, never an early write.
- JPMS module descriptors for API and core and reactor POM version 4.0.0.
- Focused tests, administrator/user guidance, architecture and deprecation notes.

## Review-branch verification status

The 4.1.0 review branch has passed automated Maven tests and strict Checkstyle
and SpotBugs verification on JDK 24. Its documentation workflow also validates
the manifest, sources and links and builds HTML manuals. These results verify
the review branch, not the original tagged 4.0.0 release or any deployed SQL
Server environment.

## Validation outstanding

Run `mvn clean verify`, `mvn javadoc:javadoc` and the SpotBugs/Checkstyle/PMD
profile on the target JDK with dependency resolution. On a disposable SQL
Server, test new database, existing database retry, broken plugin DDL,
invalid credentials, TLS certificate validation, GUI cancellation and output
permissions. Confirm no plugin is enabled and no plugin data executes.

This record is not a claim of successful live SQL Server acceptance.
