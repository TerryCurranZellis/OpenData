# Data and Privacy Governance

## Data classes

| Class | Examples | Minimum handling |
|---|---|---|
| Public provider data | Ofgem price-cap workbooks | Provenance, licence and integrity controls |
| Attributed API data | Open-Meteo responses and derived rows | CC BY attribution, request provenance, service-term review |
| Confidential customer documents | Octopus statement PDFs and extracted records | Access control, minimisation, retention, redaction and secure deletion |
| Security material | Passwords, PFX files, private keys | Never commit; least access; rotation and recovery |
| Operational evidence | Logs, audit rows, backups | Restrict access and redact sensitive values |

## Lifecycle controls

1. Confirm authority and provider terms before acquisition.
2. Record source, time, parameters, filename and hash.
3. Validate content before transformation.
4. Minimise personal data and avoid unnecessary logging.
5. Load transactionally and preserve run/source lineage.
6. Archive only where authorised and protect the archive.
7. Apply documented retention and deletion.
8. Redact or synthesise data before sharing evidence.

## Octopus-specific boundary

The plugin processes files supplied by an authorised operator. It does not create
a lawful basis to process another person's statements. Production deployments
must decide retention, access, backup and deletion rules before enabling the
plugin.

## Incident handling

If credentials or customer documents are committed, shared or logged, stop
further distribution, preserve minimal evidence, revoke/rotate affected secrets,
remove exposed data from release artifacts, and assess notification obligations.
Do not rely on deleting the latest file alone where repository history or already
published archives may still contain it.
