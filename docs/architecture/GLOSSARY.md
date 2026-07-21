# OpenData Architecture Glossary

| Term | Meaning |
|---|---|
| Acquisition | Retrieving source data from a provider or approved file |
| ADR | Architecture Decision Record |
| Archive | Retained copy of the original source material and metadata |
| Candidate record | Parsed record not yet accepted by validation |
| Configuration fingerprint | Hash representing safe effective configuration |
| Dataset plugin | Module containing source-specific behaviour |
| Effective configuration | Defaults after any explicit override has been applied |
| Framework core | Dataset-independent execution and infrastructure code |
| Idempotency | Repeating a load produces the same logical database state |
| Natural key | Business key that uniquely identifies a dataset record |
| Plugin descriptor | Plugin identity, version, description, and capabilities |
| Rejection | Candidate record not accepted by validation |
| Run | One invocation of one selected plugin |
| Run ID | Unique identifier for a run |
| Source artefact | Downloaded or supplied source file plus provenance |
| Staging table | Temporary database table used before publishing records |
