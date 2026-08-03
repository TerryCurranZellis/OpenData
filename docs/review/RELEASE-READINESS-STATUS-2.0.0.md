# OpenData 2.0.0 Release Readiness Status

**Assessment date:** 3 August 2026  
**Assessment basis:** Uploaded source plus Documentation Batches 2–7  
**Decision:** Not yet production-ready

## Implemented and documented

- Database-backed registration and configuration loading.
- Certificate-based bootstrap password encryption/decryption.
- Common plugin lifecycle and bounded parallel execution.
- Ofgem and OpenMeteo extract/transform/load paths.
- Local Octopus discovery, duplicate completion ledger, transactional persistence
  and successful-file archiving.
- Manifest-driven technical, administrator, developer and API documentation.
- Architecture, operations, development, governance and release controls.

## Mandatory unresolved blockers

| Severity | Blocker | Required closure |
|---|---|---|
| Critical | Plain database password and private PFX are present in tracked runtime resources | Remove from distributable source, rotate exposed credentials and use deployment-specific keys |
| High | Keystore environment-variable constant is incorrect | Correct source and test the intended secret-input path |
| High | Octopus dry-run reads the completion ledger without a dry-run database resource | Make extract dry-run side-effect-free and database-independent or provide a safe explicit design |
| High | Development JDBC URL trusts the server certificate | Validate SQL Server certificate trust for release deployment |
| Medium | SQL Server JDBC dependency is a preview version | Replace with verified stable version or record explicit acceptance |
| Medium | Self-contained executable packaging is not proven | Define and test clean-machine launch/distribution |
| Evidence | Maven, SQL Server and PowerShell acceptance were not executable in the documentation processing environment | Run and retain release evidence on the target toolchain |

## Release decision rule

Do not assign a Version 2.0.0 release date or publish tag `v2.0.0` until all
mandatory blockers are closed or formally waived through the release evidence
process. Documentation completion alone is not release acceptance.
