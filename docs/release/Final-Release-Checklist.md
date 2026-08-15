# Final Release Checklist

**Target:** OpenData `v3.0.0`  
**Documentation baseline:** 15 August 2026

Every checked item must reference retained evidence in
[Release-Evidence-Index.md](Release-Evidence-Index.md). Do not check an item based
only on expectation or documentation review.

## Source, version and secrets

- [ ] Release commit identified and working tree clean.
- [ ] `pom.xml`, bootstrap version, documentation configuration, changelog,
      release notes and intended tag all report `3.0.0`.
- [ ] No live passwords, customer PDFs/data, backups or private deployment keys
      are present in the release archive or repository history being published.
- [ ] Release certificate/private-key handling has been reviewed and deployment
      secrets are not included in distributable output.
- [ ] Environment-variable/system-property keystore password handling is tested
      for the supported deployment path.

## Build and quality

- [ ] `mvn clean verify` succeeds on the minimum supported Java 24 runtime.
- [ ] The same release is smoke-tested with the current development JDK 26.
- [ ] Checkstyle, PMD, SpotBugs, JaCoCo and dependency analysis results are
      reviewed according to the active POM configuration.
- [ ] Direct/transitive dependency and licence inventory is retained and agrees
      with `THIRD-PARTY-NOTICES.md`.
- [ ] Preview SQL Server JDBC dependency is replaced or explicitly approved.
- [ ] Packaged application launch method is tested and documented accurately.

## JavaFX GUI acceptance

- [ ] No-argument startup and `--gui` both open the JavaFX application.
- [ ] Splash screen, main window, plugin selection and menu/toolbar actions work.
- [ ] Registration, register-from-file, enable, disable and unregister workflows
      are accepted against the release database.
- [ ] Plugin detail and application log viewers show the expected information.
- [ ] Execute and Dry-run confirmations launch the correct operation.
- [ ] Live execution logging streams safely to JavaFX and Close remains disabled
      until the operation completes.
- [ ] Windows compiled Help opens when installed; built-in JavaFX Help is a
      working fallback.
- [ ] About dialog reports the final release version.
- [ ] [GUI 3.0 final acceptance checklist](../development/gui-v3.0-final-acceptance-checklist.md)
      is complete.
- [ ] All 13 release screenshots listed in the
      [GUI screenshot plan](../development/gui-screenshot-plan.md) are captured,
      reviewed and present in `docs/diagrams/source` (and copied/rendered as the
      documentation build expects).

## Documentation

- [ ] Documentation validation completes without errors.
- [ ] All PlantUML sources render and generated SVG files are inspected.
- [ ] Technical, Administrator, Developer and API manuals build in required
      formats.
- [ ] README, release notes, GUI guide, migration/operations material, notices
      and security policy agree with the tested release.
- [ ] Package Javadocs render all `package-info.java` type links correctly.
- [ ] No machine-specific or private paths/data appear unintentionally.

## Database and registration

- [ ] SQL scripts install into a clean database in numeric order.
- [ ] Idempotent scripts can be rerun safely where documented.
- [ ] Required configuration, audit and plugin objects exist.
- [ ] Least-privilege application login/role is verified.
- [ ] SQL Server uses validated certificate trust with
      `trustServerCertificate=false` for the release environment.
- [ ] `--plugin all --register` encrypts the bootstrap password and restart loads
      database properties.
- [ ] Registry list, named/repeated/all selection and enable/disable/unregister/
      re-register are accepted against SQL Server.
- [ ] External one-plugin registration file rules are accepted and invalid
      combinations rejected.
- [ ] Wrong/unavailable private key fails closed.

## CLI and plugin acceptance

- [ ] Normal runs require `--execute`/`-x`; `--dry-run`/`-n` is accepted as its
      own non-writing execution authorisation.
- [ ] Ofgem, OpenMeteo and Octopus dry-runs succeed without provider writes,
      audit rows or archive moves prohibited by dry-run semantics.
- [ ] `--plugin all --dry-run` succeeds with the expected enabled set and bounded
      parallelism.
- [ ] Ofgem representative write run and rollback/reconciliation are verified.
- [ ] OpenMeteo representative write run and period replacement are verified.
- [ ] Octopus electricity-only, gas-only and dual-fuel write runs are verified.
- [ ] Completed Octopus filename/hash pairs are skipped.
- [ ] Changed content under the same Octopus filename is processed as a new hash.
- [ ] Octopus source files archive only after successful commit.

## Distribution and release

- [ ] Source/binary/documentation archive contents are reviewed.
- [ ] Required licences, notices and provider attributions are included.
- [ ] SHA-256 checksums are generated after final packaging and independently
      checked.
- [ ] Release evidence index is complete; blockers are closed or approved
      waivers signed.
- [ ] Annotated tag `v3.0.0` is created only after release approval.
