# 8. Multiple Plugins and Parallelism

**Document ID:** USER-008  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

Select several plugins by repeating the option, using commas, or selecting all:

```text
opendata --plugin openmeteo --plugin ofgem --parallelism 2
opendata --plugin openmeteo,ofgem --parallelism 2
opendata --plugin all
```

The application creates one independent task per plugin on a fixed-size executor.
The worker count is the smaller of requested parallelism and selected plugins.

Each task has its own plugin instance, run UUID and JDBC transaction. One failure
does not prevent another plugin completing, but the aggregate status becomes
`PLUGIN_FAILURE`.

A multi-plugin override file must use `plugin.<id>.<key>`. Unscoped plugin values
are rejected to prevent cross-plugin configuration.

Start with parallelism one for database acceptance. Increase it only after
checking connection-pool capacity, SQL waits and remote-service behaviour.
