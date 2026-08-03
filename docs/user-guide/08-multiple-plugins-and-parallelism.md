# 9. Multiple Plugins and Parallelism

**Document ID:** USER-008  
**Version:** 2.0  
**Status:** Version 2.0.0 operational baseline  
**Baseline date:** 3 August 2026

---

Select several plugins by repeating the option, using comma-separated ids, or
selecting all:

```text
opendata --plugin openmeteo --plugin ofgem --parallelism 2
opendata --plugin openmeteo,ofgem --parallelism 2
opendata --plugin all
```

The runtime creates one independent task per plugin on a fixed-size executor.
Actual worker count is the smaller of requested parallelism and selected plugin
count. The configured default is four and the command-line range is 1–64.

Each task has its own plugin instance, run UUID and database transaction. A
plugin failure does not cancel the other selected plugins, but the aggregate
application status becomes `PLUGIN_FAILURE`.

A multi-plugin override file must use `plugin.<id>.<key>` for every plugin value.
Unscoped plugin values are rejected.

Start with parallelism one. Increase it only after checking SQL Server
connections, lock waits, remote-service behaviour and per-plugin run duration.
The default database pool maximum is eight connections, but a plugin may borrow
more than one connection during its lifecycle, so do not equate pool size with a
safe plugin count.

`--plugin all --dry-run` is not a valid acceptance command while Octopus dry run
is defective. Dry-run only Ofgem and OpenMeteo together.
