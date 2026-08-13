# Project Vision

**Document ID:** ARCH-001  
**Version:** 2.0  
**Status:** Version 2.0.0 baseline  
**Baseline date:** 2 August 2026  
**Minimum Java version:** 24

---

## Vision

OpenData provides a reusable, auditable foundation for acquiring data from
independent sources and loading consistent records into a local SQL Server
database. A new source should normally require a plugin and source-specific
transformation, not changes throughout the application.

![OpenData project overview](../diagrams/generated/project-overview.svg)

## Version 2.0.0 direction

Version 2.0.0 establishes the long-term configuration and plugin foundation:

- the local application property file contains only database bootstrap details;
- application and plugin configuration is registered and maintained in SQL
  Server;
- the bootstrap password is protected with a certificate-backed RSA mechanism;
- plugins use an explicit five-phase lifecycle;
- common infrastructure owns command-line processing, concurrency, logging,
  database access, auditing and exception boundaries; and
- plugin packages own source-specific acquisition, transformation, persistence
  and cleanup.

## Goals

- support APIs, direct files, publication pages and user-supplied documents;
- preserve source identity and processing provenance;
- prevent accidental duplicate processing;
- validate data before persistence;
- isolate provider rules from reusable infrastructure;
- support repeatable single-plugin and bounded multi-plugin execution;
- keep secrets and personal data out of logs and source control;
- maintain documentation beside the code; and
- remain understandable and maintainable by a small development team.

## Current plugins

| Plugin | Purpose |
|---|---|
| Ofgem | Acquire and persist Energy Price Cap workbook data |
| OpenMeteo | Acquire and persist historical daily weather data |
| Octopus | Process local customer statement PDFs into electricity and gas records |

Octopus is deliberately local-file based in Version 2.0.0. Direct account or API
integration remains a future option and is not part of the current release
baseline.

## Principles

Java 24 compatibility; immutable values where practical; configuration before
custom code; explicit infrastructure boundaries; transactional persistence;
least privilege; source and run provenance; side-effect-free dry runs; and
documentation as code.

## Success criteria

Version 2.0.0 succeeds when a clean installation can create the database,
register configuration, restart using the encrypted bootstrap password, list and
run all installed plugins, avoid duplicate Octopus statement processing, and
produce documentation that accurately describes the implemented system.
