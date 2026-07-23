# Future Architecture

**Document ID:** ARCH-015  
**Version:** 1.0  
**Status:** Roadmap  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## Near term

Complete registry integration; make record-based configuration canonical;
finish HTML/Excel integration; complete Ofgem and OpenMeteo pipelines; add run
history/checksums; remove duplicate legacy classes; select parsers by
`DatasetFormat`.

## Medium term

Typed `DataRecord`/`DataTable`, ZIP extraction, credential providers, HTTP
retry/backoff, quality statistics, incremental imports and ArchUnit tests.

## Shelved

Database plugin management/JSON, internal scheduling, graphical administration,
browser automation and plugin marketplace.

Future changes preserve Java 17 unless a new ADR raises the baseline.
