# Future Architecture

**Document ID:** ARCH-015  
**Version:** 1.1  
**Status:** Roadmap  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


## Near term

Remove packaged credentials; unify plugin-run and Ofgem provenance identities;
publish one SQL migration order; prove live SQL Server writes, rollback and
least privilege; define executable packaging and process exit codes; bound
downloads on the active plugin paths.

## Medium term

Typed `DataRecord`/`DataTable`, ZIP/XML/HTML-table adapters, credential
providers, HTTP retry/backoff, universal source archiving, quality statistics,
incremental imports and ArchUnit tests.

## Shelved

Database plugin management/JSON, internal scheduling, graphical administration,
browser automation and plugin marketplace.

Future changes preserve Java 17 unless a new ADR raises the baseline.
