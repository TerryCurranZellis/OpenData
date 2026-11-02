# Compact Example Plugin

**Document ID:** EXAMPLE-PLUGIN-001  
**Version:** 3.0.0  
**Status:** Copyable API example; not compiled by Maven  
**Baseline date:** 15 August 2026  

---

This directory is a compact example of the Version 3.0.0 plugin API,
registration properties and safe dry-run behaviour. It deliberately keeps helper
classes in one package so the reflection/configuration contract is easy to see.

For a production plugin, use the
[structural Java template](../../templates/plugin-java/README.md), which shows
the required five-stage package architecture.

## Use

1. Create a provider project under `opendata-plugins` and copy the Java package
   below its `src/main/java`.
2. Copy `example.properties` below that project's
   `src/main/resources/config/plugins`.
3. Add an `index.properties` to that project listing only `example`.
4. Add the project to the `opendata-plugins` aggregator and to `opendata-core`
   dependencies if it should ship with the application.
5. replace the placeholder endpoint and transformation;
6. implement a real transactional repository before write mode;
7. add tests and SQL; and
8. register the plugin before ordinary execution.

The example repository deliberately throws in write mode. Dry run can exercise
configuration, extraction and transformation without reporting false database
success.
