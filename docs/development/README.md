# Developer Documentation

**Document ID:** DEV-INDEX-001
**Version:** 3.1.0
**Status:** Version 3.1.0 GUI implementation baseline
**Baseline date:** 13 August 2026
**Minimum Java version:** 24

---

## Start here

- [Repository structure](repository-structure.md)
- [Local build, test and run](local-build-test-run.md)
- [JavaFX GUI architecture](javafx-gui-architecture.md)
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

OpenData is a single Maven modular monolith. A plugin change normally spans
Java, classpath registration properties, SQL, tests, operator documentation,
data-source notices and an ADR when the change creates a durable architectural
decision.

The executable plugin contract is
`OpenDataPlugin.execute(PluginExecutionContext)`. Version 2.0.0 provider code is
organised around `initialise`, `extract`, `transform`, `load` and `finalise`.
The root plugin class is a thin framework entry point and
`ReflectionPluginFactory` constructs it from the resolved `PluginDefinition`.

New typed configuration uses `PluginPropertyValues`, `ValidationRules` and
`SqlIdentifiers`. New repositories keep provider SQL explicit while using the
shared `database.jdbc` transaction, batch or typed upsert components where the
pattern applies.
