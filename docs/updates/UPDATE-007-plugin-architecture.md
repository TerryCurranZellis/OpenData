# Update Instructions: `docs/architecture/007-plugin-architecture.md`

Add the following material to the existing plugin architecture document.

## New Section: Plugin Discovery and Registration

The initial implementation uses Java `ServiceLoader` to discover dataset
plugins. The framework core depends only upon the `OpenDataPlugin` interface and
does not contain direct references to Ofgem or any other dataset implementation.

Each plugin registers its implementation using:

```text
META-INF/services/com.towermarsh.opendata.plugin.OpenDataPlugin
```

The `ServiceLoaderPluginRegistry` validates discovered plugin identifiers and
rejects duplicates during application startup.

## New Section: Plugin Contract

Every plugin must provide:

- a stable lowercase identifier;
- a human-readable display name;
- plugin-specific configuration validation;
- an execution method returning `PluginExecutionResult`.

## New Section: Selection Flow

The `--plugin` command-line value is used only to select a registered plugin.
Configuration loading remains the responsibility of `ConfigurationService`.
The selected plugin receives a fully resolved immutable `ApplicationConfig`.

## Related New Document

Add this entry to Related Documents:

```text
- plugin-registry.md
```
