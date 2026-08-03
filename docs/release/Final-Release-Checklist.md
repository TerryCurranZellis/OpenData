# Final Release Checklist

**Target:** OpenData `v2.0.0`  
**Documentation baseline:** 3 August 2026

Every checked item must reference retained evidence in
[Release-Evidence-Index.md](Release-Evidence-Index.md). Do not check an item based
only on expectation or documentation review.

## Source, version and secrets

- [ ] Release commit identified and working tree clean.
- [ ] `pom.xml`, bootstrap version, changelog, notes and intended tag agree.
- [ ] No live passwords, customer PDFs/data, backups or private deployment keys
      in the release archive or repository history being published.
- [ ] Exposed development credentials rotated.
- [ ] Tracked development PFX/private key removed from distributable output.
- [ ] Environment-variable keystore password defect corrected and tested, or a
      documented release waiver restricts supported secret input.

## Build and quality

- [ ] `mvn clean verify` succeeds on Java 17.
- [ ] Checkstyle, PMD, SpotBugs, JaCoCo and dependency analysis results reviewed
      according to the actual POM configuration.
- [ ] Direct/transitive dependency and licence inventory retained.
- [ ] Preview SQL Server JDBC dependency replaced or explicitly approved.
- [ ] Packaged application launch method tested and documented accurately.

## Documentation

- [ ] Documentation validation completes without errors.
- [ ] All PlantUML sources render and generated SVG files are inspected.
- [ ] Technical, Administrator, Developer and API manuals build in required
      formats.
- [ ] README, release notes, migration, operations, notices and security policy
      agree with the tested release.
- [ ] No machine-specific or private paths/data appear unintentionally.

## Database and registration

- [ ] SQL scripts install into a clean database in numeric order.
- [ ] Idempotent scripts can be rerun safely where documented.
- [ ] Required configuration, audit and plugin objects exist.
- [ ] Least-privilege application login/role verified.
- [ ] SQL Server uses validated certificate trust with
      `trustServerCertificate=false` for the release environment.
- [ ] Registration encrypts the bootstrap password and restart loads database
      properties.
- [ ] Wrong/unavailable private key fails closed.

## Plugin acceptance

- [ ] Ofgem dry-run succeeds without database writes/audit rows.
- [ ] OpenMeteo dry-run succeeds without database writes/audit rows.
- [ ] Ofgem representative write run and rollback/reconciliation verified.
- [ ] OpenMeteo representative write run and period replacement verified.
- [ ] Octopus electricity-only, gas-only and dual-fuel write runs verified.
- [ ] Completed Octopus filename/hash pairs are skipped.
- [ ] Changed content under the same Octopus filename is processed as a new hash.
- [ ] Octopus source files archive only after successful commit.
- [ ] Octopus dry-run database-ledger defect corrected before claiming Octopus or
      `--plugin all --dry-run` acceptance.

## Distribution and release

- [ ] Source/binary/documentation archive contents reviewed.
- [ ] Required licences, notices and provider attributions included.
- [ ] SHA-256 checksums generated after final packaging and independently checked.
- [ ] Release evidence index complete; blockers closed or approved waivers signed.
- [ ] Annotated tag `v2.0.0` created only after release approval.
