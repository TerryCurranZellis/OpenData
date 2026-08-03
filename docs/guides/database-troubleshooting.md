# Database Troubleshooting

**Document ID:** GUIDE-DB-TROUBLE-001  
**Version:** 2.0  
**Status:** Current  
**Baseline date:** 3 August 2026

---

| Symptom | Likely cause | Action |
|---|---|---|
| `core.plugin_registry` not found | migration not applied | run `003a` then current grant scripts |
| `--list-plugins` fails | bootstrap/decryption/SQL/grant issue | verify connection, certificate and SELECT grant |
| named plugin not registered | absent registry row | register packaged or complete external definition |
| named plugin disabled | status row false | run `--plugin <id> --enable` |
| register cannot write properties | missing DML grants | apply `008`/`009`, inspect role membership |
| dry run fails before plugin starts | registry/configuration read failed | SQL startup is still required |
| pool timeout | unavailable SQL, blocking or exhausted pool | inspect server sessions, transactions and parallelism |
| Octopus archive warning | post-commit file move failed | reconcile ledger and directories before retry |

Use `sql/010-verification-queries.sql` and query
`core.schema_version`, `core.plugin_registry`, `core.plugin_property` and
`core.PluginRun`. Never repair registry/property rows during an active run.
