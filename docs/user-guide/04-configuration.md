# 4. Configuration

**Document ID:** USER-004  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 26 July 2026

---

Built-in settings are under `src/main/resources/config`. Do not edit packaged
defaults to store a secret. Create an external properties file:

```properties
application.database.password=<secret>
```

For one plugin, plugin properties may be unscoped:

```properties
property.start-date.value=2025-01-01
property.end-date.value=2025-12-31
```

For several plugins, scope every plugin setting:

```properties
application.database.password=<secret>
application.execution.max-parallel-plugins=2
plugin.openmeteo.property.start-date.value=2025-01-01
plugin.ofgem.property.download.request-timeout.value=PT180S
```

Use `--file C:\OpenData\run.properties` to load the file. Restrict its file
permissions and keep it out of Git.

The separate `src/main/resources/application.properties` is a legacy resource
and is not read by the current runtime.
