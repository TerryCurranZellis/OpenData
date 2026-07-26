# Configuration Architecture

**Document ID:** ARCH-011  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


## Categories

The classpath application resource holds database-pool, execution and logging
settings. Plugin resources hold identity, endpoints, typed properties and
credential references. `--file` provides invocation overrides.

```text
config/application.properties + application.<key> overrides
    -> ApplicationRuntimeConfiguration
config/plugins/index.properties
    -> PluginDescriptor list
config/plugins/<id>.properties + plugin overrides
    -> PluginDefinition
```

Application entries in an override file use `application.<key>`. A single-plugin
run may use unscoped plugin entries; a multi-plugin run must use
`plugin.<id>.<key>`. Unscoped plugin values in a multi-plugin file are rejected.

Keys are case-insensitive after normalisation. Named structures use
`endpoint.<name>.*`, `property.<name>.*` and `credential.<name>.*`.

The legacy `src/main/resources/application.properties` is not loaded by the
current runtime. Its removal and the classpath database password are tracked as
gaps. Database-backed plugin configuration and JSON exchange remain shelved.
