# Database Troubleshooting

**Document ID:** GUIDE-DB-TROUBLE-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

::: {.docx-linear-table}

| Symptom | Likely cause | Checks |
|---|---|---|
| Login failed for `OpenData` | login/password/user mapping | test with `sqlcmd`; inspect user/login |
| Database not found | bootstrap not run or URL wrong | verify `databaseName=OpenData` |
| Certificate error | untrusted certificate | use dev trust only locally; install trusted cert |
| Pool timeout | leaked connections or pool too small | inspect active/idle snapshot; verify try-with-resources |
| Validation query fails | SQL Server unavailable | run `SELECT 1` using same credentials |
| Permission denied | missing role grant | rerun/inspect script 090 |
| Duplicate current period | failed constraint/update logic | inspect `is_current` rows and transaction logs |
| Fact replacement rolled back | constraint or dimension seed mismatch | inspect SQL state and audit error |
| Run remains `STARTED` | process terminated before completion | inspect logs/source hash, then retry under policy |

:::

## Useful SQL

```sql
SELECT DB_NAME() AS database_name, SUSER_SNAME() AS login_name, USER_NAME() AS user_name;
SELECT * FROM core.schema_version ORDER BY version;
SELECT * FROM core.PluginRun ORDER BY StartedAt DESC;
SELECT * FROM core.ingestion_run ORDER BY ingestion_run_id DESC;
SELECT * FROM core.ingestion_error ORDER BY ingestion_error_id DESC;
SELECT LocationKey, MIN(ObservationDate), MAX(ObservationDate), COUNT(*)
FROM openmeteo.Location AS l
JOIN openmeteo.DailyWeather AS d ON d.LocationId = l.LocationId
GROUP BY LocationKey;
```

Never enable broad database-owner permissions merely to suppress an error.
Identify the specific missing permission or incorrect object instead.
