# Logging Architecture

**Document ID:** ARCH-012  
**Version:** 3.0.0  
**Status:** Baseline  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---


OpenData uses `java.util.logging`; framework code does not require SLF4J or
Log4j.

One console handler and one rotating file handler are shared. The formatter
includes timestamp, level, worker thread, plugin id, run UUID, logger and
message. A `ThreadLocal` `PluginLogContext` is opened and closed by the
coordinator because executor threads are reused.

`INFO` records lifecycle milestones, `WARNING` recoverable anomalies, `SEVERE`
run failures and `FINE` diagnostics. `--verbose` enables `FINE`.

API keys, passwords, tokens, secret files and credential-bearing URLs are never
logged. Headers/query parameters are redacted before diagnostics.

The entry point logs final status and elapsed duration in `finally`. Each plugin
task has a UUID shared by contextual logs and `core.PluginRun`. The Ofgem domain
ingestion row currently uses a separate identity; this is a documented audit
model gap.
