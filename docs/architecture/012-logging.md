# Logging Architecture

**Document ID:** ARCH-012  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


OpenData uses `java.util.logging`; framework code does not require SLF4J or
Log4j.

`INFO` records lifecycle milestones, `WARNING` recoverable anomalies, `SEVERE`
run failures and `FINE` diagnostics. Startup, selected plugin/configuration
version, download counts, parser statistics, validation results, transaction
outcome, status and duration are logged.

API keys, passwords, tokens, secret files and credential-bearing URLs are never
logged. Headers/query parameters are redacted before diagnostics.

The entry point logs final status and elapsed duration in `finally`. A future run
identifier should correlate log and database records.
