# Update Instructions: `docs/architecture/011-configuration.md`

Add the following material to the existing configuration architecture document.

## New Section: Configuration and Plugin Integration

Configuration is resolved only after the command line has selected a plugin id.

The configuration loader then combines:

1. framework built-in defaults;
2. application classpath properties;
3. selected plugin classpath properties;
4. optional command-line override properties.

The resulting immutable `ApplicationConfig` is passed to the selected plugin.

Framework-wide validation runs before plugin-specific validation. This prevents
plugins from duplicating checks for general settings such as HTTP timeout
values, working directories and transaction configuration.

## New Section: Plugin-Specific Namespaces

Implementation-specific properties should use:

```text
plugin.<plugin-id>.<property-name>
```

For example:

```properties
plugin.ofgem.expected-columns=12
```

`ApplicationConfig.pluginValues()` returns the selected plugin's namespace with
the prefix removed.

## Related New Document

Add these entries to Related Documents:

```text
- ../reference/configuration-reference.md
- plugin-registry.md
```
