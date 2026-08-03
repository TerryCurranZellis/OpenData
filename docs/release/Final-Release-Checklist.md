# Final Release Checklist

**Target:** OpenData `v2.0.0`  
**Baseline date:** 2 August 2026

Use this checklist before publishing Version 2.0.0.

## Source and version

- [ ] Confirm the working tree contains only intended release changes.
- [ ] Confirm `pom.xml`, bootstrap properties, release notes, changelog and tag
      all use `2.0.0`.
- [ ] Confirm Version 1.0.0 records are retained only as clearly historical
      documents.
- [ ] Review dependency and third-party licence inventories.

## Build and documentation

- [ ] Run `mvn clean verify -Dquality.failOnViolation=true`.
- [ ] Run `.\scripts\Validate-Documentation.ps1 -FailOnWarning`.
- [ ] Render all PlantUML sources and inspect every new SVG.
- [ ] Build all configured documentation formats.
- [ ] Check README, release notes, installation, quick start and migration links.

## Database and configuration

- [ ] Install every SQL script into a clean database in numeric order.
- [ ] Rerun idempotent scripts.
- [ ] Verify configuration, audit, Ofgem, OpenMeteo and Octopus objects.
- [ ] Run `--register` using a temporary plain-text bootstrap password.
- [ ] Confirm the rewritten password has the `{enc}` prefix.
- [ ] Restart and confirm configuration loads from SQL Server.
- [ ] Verify failure with the wrong or unavailable private key.
- [ ] Verify least-privilege permissions.

## Plugin acceptance

- [ ] Dry-run Ofgem, OpenMeteo, Octopus and `--plugin all`.
- [ ] Complete representative write-mode runs.
- [ ] Verify run auditing and transaction rollback.
- [ ] Verify Octopus electricity-only, gas-only and dual-fuel statements.
- [ ] Verify unchanged completed Octopus files are skipped.
- [ ] Verify changed content under the same filename is reprocessed.
- [ ] Verify source PDFs are archived only after a successful commit.

## Security and release artefacts

- [ ] Replace or explicitly accept the development certificate for the release
      environment.
- [ ] Use a strong non-default PFX password outside development.
- [ ] Confirm no credentials, private deployment keys, customer statements,
      database backups or unredacted logs are packaged.
- [ ] Review `SECURITY.md`, `DATA-SOURCE-NOTICES.md` and
      `THIRD-PARTY-NOTICES.md`.
- [ ] Generate and verify SHA-256 checksums.
- [ ] Create and push annotated tag `v2.0.0` only after every mandatory check is
      complete.
