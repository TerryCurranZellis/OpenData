# 11. Logs, Audit and Troubleshooting

**Document ID:** USER-010  
**Version:** 3.0.0  
**Status:** Version 3.0.0 operational baseline  
**Baseline date:** 3 August 2026

---

OpenData uses `java.util.logging` with console output and rotating files named
`opendata-%g.log`. Use `--verbose` for `FINE` output.

Concurrent plugin records contain context similar to:

```text
[thread=opendata-plugin-1] [plugin=openmeteo] [run=<uuid>]
```

Use the UUID to correlate write-run logs with `core.PluginRun`. Dry runs do not
create `core.PluginRun` rows.

## Common failures

| Symptom | Check |
|---|---|
| Missing required `--plugin` | operational commands require `--plugin <id|all>` |
| Plugin is not registered | run `--list-plugins`, then register the definition |
| Plugin is disabled | use `--plugin <id> --enable` before running it |
| `--file` rejected | it is valid only with `--register` and one named plugin |
| `-d` starts disable | use `-n` or `--dry-run` for dry-run execution |
| Registry table missing | apply `003a-create-plugin-registry.sql` and grants |
| Configuration database fails | encrypted bootstrap password, certificate/PFX, SQL URL and grants |
| Pool timeout | SQL availability, long transactions, leaked connections and parallelism |
| Ofgem extraction fails | publisher page, link text and workbook layout |
| OpenMeteo fails | explicit dates, coordinates, timezone, HTTP response and array lengths |
| Octopus archive warning | commit succeeded but file move failed; reconcile ledger and directories |
| Stale `RUNNING` row | retain evidence and start a new run |

Never publish passwords, private keys, unredacted statement text or complete
configuration files. Do not rely on the shell exit code; confirm the final
application log line and every plugin summary.
