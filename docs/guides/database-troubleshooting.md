# Database Troubleshooting

**Document ID:** GUIDE-DB-TROUBLE-001  
**Version:** 2.0  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

::: {.docx-linear-table}

| Symptom | Likely cause | Checks |
|---|---|---|
| Login failed for `OpenData` | password/login/user mapping | test with `sqlcmd`; inspect login, user and role membership |
| Database not found | URL or script 001 failure | verify `databaseName=OpenData` and database state |
| Configuration decrypt fails | wrong/missing PFX or PFX password | certificate paths; JVM property; matching public/private pair |
| Configuration tables empty | registration not completed | inspect `core.application_property`, `core.plugin_property`, bootstrap switch |
| Plugin definition missing after registration | incomplete plugin rows | compare plugin index and `core.plugin_property`; rerun controlled registration |
| SQL certificate error | untrusted server certificate | use development trust only locally; install a trusted certificate |
| Pool timeout | unavailable SQL, blocking, long transaction or exhausted pool | `SELECT 1`; pool snapshot; waits; parallelism |
| Permission denied | missing script 008/009 grant or membership | inspect `opendata_app` and exact object permission |
| `core.PluginRun` remains `RUNNING` | process ended before completion | inspect logs and business tables; retain row and start a new run |
| Octopus dry run fails | processed-file ledger requires database | known defect; use isolated write-mode acceptance |

:::

## Useful SQL

```sql
SELECT DB_NAME() AS database_name,
       SUSER_SNAME() AS login_name,
       USER_NAME() AS user_name;

SELECT * FROM core.schema_version ORDER BY version;
SELECT * FROM core.PluginRun ORDER BY StartedAt DESC;
SELECT property_key, is_encrypted, updated_at
FROM core.application_property ORDER BY property_key;
SELECT plugin_id, property_key, updated_at
FROM core.plugin_property ORDER BY plugin_id, property_key;
```

Never grant `db_owner` merely to suppress an error. Identify the exact missing
permission, invalid object, credential or configuration value.
