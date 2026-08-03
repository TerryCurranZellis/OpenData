# 11. Logs, Audit and Troubleshooting

**Document ID:** USER-010  
**Version:** 2.0  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 3 August 2026

---

OpenData uses `java.util.logging` with console output and rotating files named
`opendata-%g.log`. The default directory is `logs`, file limit 10 MiB, file count
10 and append enabled.

Concurrent plugin records contain context similar to:

```text
[thread=opendata-plugin-1] [plugin=openmeteo] [run=<uuid>]
```

Use the UUID to correlate logs with `core.PluginRun`. Dry runs do not create
`core.PluginRun` rows. Ofgem also creates a separate numeric ingestion identity.

## Common failures

| Symptom | Check |
|---|---|
| Missing required `--plugin` | launcher passed arguments correctly; use one argument array or the supported single-string form |
| Plugin not installed | `config/plugins/index.properties`, plugin file and implementation class |
| Configuration database fails at startup | encrypted bootstrap password, certificate/PFX, SQL URL and grants |
| Pool timeout | SQL availability, long transactions, leaked connections and parallelism |
| Ofgem extraction fails | publisher page, link text and workbook layout |
| OpenMeteo fails | explicit dates, coordinates, timezone, HTTP response and array lengths |
| Octopus dry run fails | known ledger/database limitation; use isolated write-mode acceptance |
| Octopus archive warning | database commit succeeded but file move failed; reconcile ledger and input/archive directories |
| Multi-plugin override rejected | every plugin key is `plugin.<id>.<key>` |
| Stale `RUNNING` row | process ended before audit completion; retain evidence and start a new run |

Run with `--verbose` for `FINE` output. Never publish passwords, private keys,
unredacted statement text or complete override files.

Do not rely on the shell exit code. Confirm the final application log line and
every plugin summary.
