# Final Release Checklist

Use this checklist before publishing the `v1.0.0` tag.

- [ ] Confirm the working tree contains only intended release changes.
- [ ] Run `mvn clean verify -Dquality.failOnViolation=true`.
- [ ] Run `./scripts/Validate-Documentation.ps1 -FailOnWarning`.
- [ ] Build all documentation formats.
- [ ] Test Ofgem, Open-Meteo and `--plugin all` in dry-run mode.
- [ ] Run applicable SQL Server integration tests in a release environment.
- [ ] Confirm `pom.xml`, release notes and tag all use version `1.0.0`.
- [ ] Review generated SHA-256 checksums.
- [ ] Create and push annotated tag `v1.0.0`.
- [ ] Confirm the GitHub release workflow publishes the expected artefacts.
