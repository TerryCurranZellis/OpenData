# Developer Documentation

**Document ID:** DEV-INDEX-001  
**Version:** 3.0.0  
**Status:** Current Version 3.0.0 merged baseline  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

## Development baseline

- Minimum supported JDK: 24.
- Current development JDK: 26.
- Current development IDE: Apache NetBeans 31.
- JavaFX dependency version: 26.0.1.
- Maven: 3.9 or later.

## Start here

- [Repository structure](repository-structure.md)
- [Local build, test and run](local-build-test-run.md)
- [JavaFX GUI architecture](javafx-gui-architecture.md)
- [GUI 3.0 final acceptance checklist](gui-v3.0-final-acceptance-checklist.md)
- [GUI screenshot plan](gui-screenshot-plan.md)
- [Java quality guide](Java-Quality-Guide.md)
- [Dependency management](dependency-management.md)
- [Build, CI and release guide](Build-CI-and-Release-Guide.md)
- [Release and versioning](release-and-versioning.md)

## Extending OpenData

- [Adding a plugin](../guides/adding-a-plugin.md)
- [Shared validation and JDBC reference](../reference/shared-validation-and-jdbc-reference.md)
- [Shared validation and JDBC architecture](../architecture/028-shared-validation-and-jdbc-infrastructure.md)
- [Adding a CSV plugin](../guides/adding-a-csv-plugin.md)
- [Adding a JSON plugin](../guides/adding-a-json-plugin.md)
- [Adding an Excel plugin](../guides/adding-an-excel-plugin.md)
- [Adding HTML link discovery](../guides/adding-html-link-discovery.md)
- [Plugin API reference](../reference/plugin-api-reference.md)
- [Plugin properties reference](../reference/plugin-properties-reference.md)
- [Java plugin template](../templates/plugin-java/README.md)
- [Compact example plugin](../examples/example-plugin/README.md)

## Engineering rules

- [Project standards](../standards/README.md)
- [Architecture decisions](../decisions/README.md)
- [Architecture manual](../architecture/ARCHITECTURE.md)

OpenData is a single Maven modular monolith. A plugin change normally spans Java,
registration properties, SQL, tests, operator documentation, data-source notices
and an ADR when it creates a durable architectural decision.

The executable plugin contract is `OpenDataPlugin.execute(PluginExecutionContext)`.
Provider implementations follow `initialise -> extract -> transform -> load ->
finalise`, with the root plugin class acting as a thin framework entry point.

Every maintained Java package has `package-info.java`. Version 3.0.0 package
pages group top-level classes, records, interfaces and enums and link each entry
to its Javadoc description.
