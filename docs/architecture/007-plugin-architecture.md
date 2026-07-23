# Plugin Architecture

**Document ID:** 007
**Version:** 1.0
**Status:** Draft

## Purpose

Explains dataset plugin model.

## Scope

This document forms part of the OpenData Framework Software Architecture Manual and should be read alongside the related architecture documents.

## Plugin Discovery and Registration

The initial implementation uses Java `ServiceLoader` to discover dataset
plugins. The framework core depends only upon the `OpenDataPlugin` interface and
does not contain direct references to Ofgem or any other dataset implementation.

Each plugin registers its implementation using:

```text
META-INF/services/com.towermarsh.opendata.plugin.OpenDataPlugin
```

The `ServiceLoaderPluginRegistry` validates discovered plugin identifiers and
rejects duplicates during application startup.

## Plugin Contract

Every plugin must provide:

- a stable lowercase identifier;
- a human-readable display name;
- plugin-specific configuration validation;
- an execution method returning `PluginExecutionResult`.

## Plugin Definition Contract

The Java plugin implementation provides specialised behaviour. Its endpoints,
formats, credentials, parser settings and target settings are represented by
the structured `PluginDefinition` record.

Each plugin receives an `ApplicationConfig` containing its parsed definition.
A plugin must not load its own properties file directly.

## Selection Flow

The `--plugin` command-line value is used only to select a registered plugin.
Configuration loading remains the responsibility of `ConfigurationService`.
The selected plugin receives a fully resolved immutable `ApplicationConfig`.

## Related New Document

Add this entry to Related Documents:

```text
- plugin-registry.md
```

## Key Concepts

| Topic | Description |
|-------|-------------|
| Architecture | Enterprise layered design |
| Documentation | Markdown source, Pandoc output |
| UML | PlantUML source diagrams |
| Testing | Unit testing and integration testing |

## Related Documents

- 001-project-vision.md
- 003-high-level-architecture.md
- 004-package-structure.md
- 007-plugin-architecture.md

## Future Enhancements

This document will be expanded as implementation progresses with UML diagrams, examples and detailed design decisions.

## Revision History

| Version | Date | Description |
|---------|------|-------------|
|1.0|2026-07-22|Initial draft|
|2.0|2026-07-23|Added plugin detail|
