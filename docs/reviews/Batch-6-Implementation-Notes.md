# Batch 6 Implementation Notes

## Scope

Batch 6 updates governance, licensing, third-party software, external data,
privacy, contribution and security documentation for OpenData 2.0.0. It does not
change Java, SQL, Maven, PowerShell or runtime configuration.

## Completed work

- Reconciled direct dependency versions with the reviewed `pom.xml`.
- Distinguished software licences from provider data/service terms.
- Added explicit Ofgem, Open-Meteo and Octopus rights/handling guidance.
- Recorded the preview SQL Server JDBC driver as a release decision.
- Consolidated known credential, private-key, TLS and secret-input blockers.
- Added governance, privacy, attribution and release-compliance chapters.
- Added a governance/compliance diagram and included governance chapters in the
  generated guide manifests.

## Evidence boundary

Official provider/upstream pages were reviewed on 3 August 2026. Provider terms
can change independently; release operators must recheck them at publication.
The batch does not constitute legal advice and does not certify a binary
redistribution whose resolved dependency contents were not available.
