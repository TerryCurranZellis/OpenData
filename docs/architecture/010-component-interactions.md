# Component Interactions

**Document ID:** 010
**Version:** 1.0
**Status:** Draft

## Purpose

Describes subsystem collaboration.

## Scope

This document forms part of the OpenData Framework Software Architecture Manual and should be read alongside the related architecture documents.

## Overview

The OpenData Framework is an enterprise-grade, plugin-based Java 17 framework for acquiring, validating, transforming and loading Open Data into relational databases. This document describes the architectural aspects related to **Component Interactions**.

## Execution Sequence

1. `CommandLineArgumentsProcessor` parses user arguments.
2. `ServiceLoaderPluginRegistry` discovers installed plugins.
3. Informational requests such as `--list-plugins` are completed without loading
   dataset configuration.
4. For an execution request, `PluginApplicationService` resolves the selected
   plugin.
5. `ConfigurationService` loads and validates `ApplicationConfig`.
6. The plugin performs dataset-specific configuration validation.
7. The plugin starts its pipeline.
8. The application logs the `PluginExecutionResult`.

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

## Future Enhancements

This document will be expanded as implementation progresses with UML diagrams, examples and detailed design decisions.

## Revision History

| Version | Date | Description |
|---------|------|-------------|
|1.0|2026-07-22|Initial draft|
|2.0|2026-07-23|Added execution sequence|
