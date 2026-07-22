# Architecture Decision Record Register

This register summarises the OpenData architecture decisions currently identified from the repository, project documentation, and design discussions.

## Status meanings

- **Accepted** — the architectural decision is approved.
- **Proposed** — the decision is documented but still requires approval.
- **Shelved** — the option remains potentially useful but is intentionally deferred.
- **Rejected / Discarded** — the option is not to be pursued unless a later ADR supersedes the decision.
- **Superseded** — a later ADR has replaced the decision.

The **Delivery status** column records implementation progress separately from the architectural decision status.

## ADR register

| ADR | Decision | Decision status | Delivery status |
|---|---|---|---|
| ADR-0001 | Use a dataset plugin architecture | Accepted | Implemented |
| ADR-0002 | Use Apache Commons CLI | Accepted | Implemented |
| ADR-0003 | Use java.util.logging | Accepted | Implemented |
| ADR-0004 | Use layered parameter-file configuration | Accepted | Implemented / evolving |
| ADR-0005 | Use SQL Server persistence | Accepted | Partially implemented |
| ADR-0006 | Use package-info.java for package documentation | Accepted | Implemented / ongoing |
| ADR-0007 | Use a modular monolith | Accepted | Implemented |
| ADR-0008 | Use Java 17 records and immutable configuration models | Accepted | Implemented |
| ADR-0009 | Use a common staged ETL pipeline | Accepted | Partially implemented |
| ADR-0010 | Use constructor injection without a dependency-injection framework | Accepted | Implemented |
| ADR-0011 | Use the Java HTTP client for dataset downloads | Accepted | Implemented |
| ADR-0012 | Keep plugin configuration in properties files before database-backed configuration | Shelved | Shelved for future development |
| ADR-0013 | Describe plugins with manifests and resolve them through a registry | Accepted | Partially implemented |
| ADR-0014 | Separate framework metadata from plugin business tables | Accepted | Partially implemented |
| ADR-0015 | Use database interfaces with SQL Server as the first implementation | Accepted | Partially implemented |
| ADR-0016 | Use a framework exception hierarchy and translate errors at boundaries | Accepted | Partially implemented |
| ADR-0017 | Use Markdown, PlantUML, Javadoc, and Pandoc for documentation as code | Accepted | Implemented |
| ADR-0018 | Prefer standard Java APIs and a minimal dependency set | Accepted | Implemented |
| ADR-0019 | Use Ofgem as the first reference plugin | Accepted | In development |
| ADR-0020 | Defer the internal scheduler and advanced orchestration | Shelved | Shelved for future development |

## Shelved decisions

| ADR | Shelved item | Reason for deferral |
|---|---|---|
| ADR-0012 | Store plugin configuration in database tables and expose it as JSON | Phase 1 uses layered properties files; database bootstrap, administration, audit, import/export, and fallback behaviour need further design. |
| ADR-0020 | Add an internal scheduler or background service | Reliable CLI execution and idempotent processing are required first; external schedulers can invoke the application meanwhile. |

## Maintenance

Update this register whenever an ADR is added, accepted, rejected, shelved, superseded, or materially implemented. An implementation change must not silently alter an accepted decision; create a superseding ADR where the architecture changes.
