# Configuration Reference

**Document ID:** REF-CONFIG-001  
**Version:** 1.0  
**Status:** Draft  
**Last Updated:** 22 July 2026

## Purpose

This document defines how OpenData Framework configuration is loaded, merged,
validated and exposed to plugins.

## Configuration Precedence

Configuration sources are applied in this order:

1. Framework built-in defaults.
2. `config/application.properties` from the application classpath.
3. `config/plugins/<plugin-id>.properties` from the classpath.
4. The optional file supplied using `--file`.

A later source overrides an earlier source.

Environment variables are intentionally excluded. This makes each execution
explicit, reproducible and easy to diagnose.

## File Locations

```text
src/main/resources/
├── config/
│   ├── application.properties
│   └── plugins/
│       └── ofgem.properties
└── META-INF/
    └── services/
        └── com.towermarsh.opendata.plugin.OpenDataPlugin
```

## ApplicationConfig

`ApplicationConfig` is immutable and represents the fully resolved
configuration for a single invocation.

It contains:

- the selected plugin id;
- all merged property values;
- the optional override file path;
- the dry-run flag;
- the verbose flag.

Typed accessors are provided for:

- strings;
- integers;
- long integers;
- booleans;
- ISO-8601 durations;
- file-system paths;
- URIs.

Missing required values and invalid conversions result in a
`ConfigurationException`.

## Property Naming

Use lowercase dotted property names:

```properties
application.working-directory=data/work
http.connect-timeout-seconds=30
database.batch-size=1000
```

Plugin implementation-specific values should be namespaced:

```properties
plugin.ofgem.release-type=current
plugin.ofgem.expected-columns=12
```

The selected plugin can obtain those values using:

```java
Map<String, String> ofgemValues = configuration.pluginValues();
```

The returned keys have the `plugin.<plugin-id>.` prefix removed.

## Validation

Framework-wide validation is performed by `StandardConfigurationValidator`.

Each plugin must then validate values specific to its dataset before starting
its pipeline.

The application validates configuration in this order:

1. Load and merge all sources.
2. Run framework validators.
3. Resolve the requested plugin.
4. Run plugin-specific validation.
5. Execute the plugin.

## Sensitive Values

Passwords and other secrets must not be committed to Git.

Where credentials are unavoidable, the override file should be stored outside
the repository and protected using operating-system file permissions.

## Related Documents

- `command-line-reference.md`
- `plugin-registry.md`
- `../architecture/011-configuration.md`
