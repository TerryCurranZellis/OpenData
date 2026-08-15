# Future Architecture

**Document ID:** ARCH-015  
**Version:** 3.0.0  
**Status:** Roadmap  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Near term

- remove plaintext credentials and private key material from tracked source;
- externalise writable bootstrap and certificate paths from the source tree;
- protect the PKCS#12 password through an operating-system or managed secret
  facility;
- unify `core.PluginRun` and the separate ingestion/provenance identities;
- remove or clearly deprecate duplicate compatibility classes;
- publish and verify one SQL installation order;
- prove live SQL Server writes, rollback, idempotency and least privilege;
- define executable packaging and process exit-code mapping;
- add bounded downloads and retry/backoff to active HTTP paths.

## Medium term

Typed shared data records, ZIP/XML/HTML-table adapters, managed credential
providers, universal source archiving, quality statistics, incremental imports,
architecture tests and stronger operational telemetry.

## Shelved or optional

Internal scheduling, graphical administration, browser automation, direct
Octopus account/API statement acquisition, IMAP attachment acquisition, dynamic
plugin installation and a plugin marketplace.

Database-backed application and plugin property storage is no longer future
work; it is implemented through registration and JDBC property sources. The
classpath index remains the installed-plugin registry.

Future changes preserve Java 24 unless a new ADR raises the baseline.
