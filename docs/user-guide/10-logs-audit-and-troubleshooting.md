# 10. Logs, Audit and Troubleshooting

**Document ID:** USER-010  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

Logs are written through `java.util.logging`. Concurrent records include:

```text
[thread=opendata-plugin-1] [plugin=openmeteo] [run=<uuid>]
```

Use the UUID to correlate a task with `core.PluginRun`. Ofgem also creates a
separate numeric ingestion id for source provenance.

## Common failures

| Symptom | Check |
|---|---|
| Plugin not installed | `index.properties`, descriptor id and implementation class |
| Database password error | `application.database.password` in the external file |
| Pool/connection failure | URL, TLS, SQL Server, principal and grants |
| Ofgem extraction failure | Publisher page/link text and workbook layout |
| OpenMeteo failure | dates, coordinates, timezone, HTTP response and array lengths |
| Multi-plugin configuration error | all plugin keys are `plugin.<id>.<key>` |
| Stale audit row | process interruption, audit completion error and transaction state |

Run with `--verbose` for `FINE` output. Never paste a password or unredacted
override file into an incident report.

Do not rely on the current shell exit code; inspect final application and plugin
statuses.
