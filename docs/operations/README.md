# Operations Documentation

**Document ID:** OPS-INDEX-001  
**Version:** 4.0.0  
**Status:** OpenData 4.0.0 baseline; 4.1.0 code refactor in review
**Baseline date:** 10 November 2026  

---

- [First-run SQL Server and AES configuration](../guides/first-run-initialisation.md)
- [Operations runbook](operations-runbook.md)
- [Monitoring and diagnostics](monitoring-and-diagnostics.md)
- [Backup and recovery](backup-and-recovery.md)
- [Logging and connection-pool operations](logging-and-pool.md)
- [Database troubleshooting](../guides/database-troubleshooting.md)

The operational lifecycle is summarised below.

![OpenData operational lifecycle](../diagrams/generated/operational-lifecycle.svg)

The initialisation wizard must run with a desktop session and stores the AES
key-file path through `config/application.properties`. Never commit AES key
material or SQL administrator credentials.

Internal scheduling is deferred. External scheduling should remain disabled for
production use until packaging and operating-system exit-code propagation are
implemented or the scheduler has a tested log-based success check.
