# Configuration Architecture

**Document ID:** ARCH-011  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


## Categories

The bootstrap application resource now holds only the application version,
database connection details, encrypted password, and the
`application.use-database-properties` flag. When that flag is `false`, packaged
plugin resources remain the source of truth. When it is `true`, application and
plugin properties are loaded from SQL Server instead. `--file` still provides
invocation overrides.

```text
config/application.properties (bootstrap only)
    -> bootstrap SQL Server access
    -> SQL Server [core].[application_property] + application.<key> overrides
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

The version 2 runtime reads a writable bootstrap file at
`src/main/resources/config/application.properties` before any database-backed
configuration lookup. `--register` copies packaged application and plugin
properties into the database and then switches the bootstrap file to
database-first mode for future runs.
