# OpenData Roadmap

**Status:** Current priorities  
**Baseline:** 3 August 2026

## Current implementation

Ofgem, OpenMeteo and local-file Octopus ingestion execute through the common
persistent plugin registry/lifecycle. Database-backed configuration registration,
transactional plugin persistence, bounded parallelism and manifest-driven manuals
are implemented.

## Release-critical priorities

| Priority | Outcome | Acceptance evidence |
|---:|---|---|
| 1 | Remove/rotate tracked credentials and private keys | Clean archive/history decision and deployment-specific key test |
| 2 | Correct keystore environment-variable secret input | Automated and manual startup tests |
| 3 | Validate persistent plugin lifecycle commands on SQL Server | Register/list/enable/disable/unregister acceptance evidence |
| 4 | Validate SQL Server trust and least privilege | Trusted TLS connection and permission evidence |
| 5 | Resolve preview JDBC dependency | Stable dependency or approved risk decision |
| 6 | Verify executable packaging | Clean-machine launch with documented command/dependencies |
| 7 | Complete release evidence | Final checklist and evidence index approved |

## Post-release candidates

- Evaluate authorised Octopus API capabilities and terms; do not assume statement
  PDF download is available.
- Expand Ofgem historical/component coverage and reconciliation.
- Add stronger secret-management integration and certificate rotation.
- Improve source-provenance linkage and operational observability.
- Consider internal scheduling only if external orchestration is insufficient.
- Evaluate additional database engines without weakening SQL Server correctness.

## Boundary

Database-backed configuration and Octopus local-file processing are no longer
future work. Direct Octopus acquisition, internal scheduling and additional
database engines remain outside the Version 3.0.0 release scope.
