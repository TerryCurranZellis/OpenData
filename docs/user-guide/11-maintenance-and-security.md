# 11. Maintenance and Security

**Document ID:** USER-011  
**Version:** 1.0  
**Status:** Pre-production baseline  
**Baseline date:** 26 July 2026

---

## Routine maintenance

- review publisher/API changes before scheduled runs;
- monitor terminal audit status, metrics, log growth and database capacity;
- retain Ofgem archives and SQL backups according to policy;
- test repeated OpenMeteo and Ofgem loads;
- update dependencies only with regression and integration tests;
- rebuild documentation and diagrams with each feature change.

## Security

Keep passwords outside source control and restrict the override file. Use the
least-privilege SQL principal. Replace local
`trustServerCertificate=true` with certificate validation in a real deployment.
Treat every downloaded file as untrusted.

The repository currently contains a classpath development password and an
obsolete legacy resource with credentials. Remove them and rotate any value that
has been used before production.

## Recovery

After a failure, confirm rollback and retain the failed audit evidence. Start a
new run rather than changing a failed row to success. Restore SQL Server from its
normal backup process and verify one controlled write per plugin before resuming
scheduled execution.

The generated guide ends with Appendix A, containing the complete Apache License
2.0 from the repository root.
