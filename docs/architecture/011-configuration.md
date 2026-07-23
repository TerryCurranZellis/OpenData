# Configuration

**Document ID:** 011
**Version:** 1.0
**Status:** Draft

## Purpose

Configuration architecture and strategy.

## Scope

This document forms part of the OpenData Framework Software Architecture Manual and should be read alongside the related architecture documents.

## Overview

The OpenData Framework is an enterprise-grade, plugin-based Java 17 framework for acquiring, validating, transforming and loading Open Data into relational databases. This document describes the architectural aspects related to **Configuration**.

## Configuration and Plugin Integration

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

## Plugin-Specific Namespaces

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

## Phase 1 Structured Configuration.

Phase 1 stores each plugin definition in a classpath properties file. The file
is parsed immediately into immutable Java records. Plugin code consumes
`ApplicationConfig` and `PluginDefinition`, not raw properties.

`ApplicationConfig` now contains:

- `BootstrapConfig`;
- `PluginDefinition`;
- runtime overrides;
- the optional override file;
- dry-run and verbose execution flags.

This record-based boundary is deliberately storage independent. A future
database repository can return JSON and deserialize it into the same
`PluginDefinition` record.


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
- 007-plugin-registry.md

## Future Enhancements

This document will be expanded as implementation progresses with UML diagrams, examples and detailed design decisions.

## Revision History

| Version | Date | Description |
|---------|------|-------------|
|1.0|2026-07-22|Initial draft|
|2.0|2026-07-23|Added pluging registry
