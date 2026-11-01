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

## Failure records

When a plugin fails, the log receives a SEVERE entry containing the plugin id,
run id, full stack trace and a summary. `core.PluginRun.ErrorMessage` (up to
4000 characters) stores the same summary:

```text
Step: load (com.towermarsh.opendata.plugin.octopus.load.OctopusLoad.load)
Error: com.towermarsh.opendata.exception.PluginException: [octopus] ...
Caused by: java.sql.SQLException: Invalid object name 'octopus.statement_file'. [sqlState=S0002, errorCode=208]
  at com.towermarsh.opendata....
```

The `Step:` line names the pipeline step (initialise, extract, transform, load
or finalise) that failed, followed by every exception in the cause chain with
its type, SQL state and error code and the first OpenData stack frames of the
root cause.

## Common failures

| Symptom | Check |
|---|---|
| Missing required `--plugin` | operational commands require `--plugin <id|all>` |
| Plugin is not registered | run `--list-plugins`, then register the definition |
| Plugin is disabled | use `--plugin <id> --enable` before running it |
| `--file` rejected | it is valid only with `--register` and one named plugin |
| `-d` starts disable | use `-n` or `--dry-run` for dry-run execution |
| Registry table missing | apply `003a-create-plugin-registry.sql` and grants |
| Plugin tables missing | re-register the plugin; its `RegisterPlugin` creates the schema and tables (the SQL login needs `CREATE SCHEMA`/`CREATE TABLE`) |
| Registration fails with class not found | the `plugin.implementation-class` is not in `opendata-core`; check the spelling in the properties file |
| Octopus finds no files | the log lists the expected file name pattern; statements are `octopus-energy-statement-YYYY-MM-DD.pdf`, adjustments start with the account number followed by `-` |
| Configuration database fails | encrypted bootstrap password, certificate/PFX, SQL URL and grants |
| Pool timeout | SQL availability, long transactions, leaked connections and parallelism |
| Ofgem extraction fails | publisher page, link text and workbook layout |
| OpenMeteo fails | explicit dates, coordinates, timezone, HTTP response and array lengths |
| Octopus archive warning | commit succeeded but file move failed; reconcile ledger and directories |
| Stale `RUNNING` row | retain evidence and start a new run |

Never publish passwords, private keys, unredacted statement text or complete
configuration files. Do not rely on the shell exit code; confirm the final
application log line and every plugin summary.
