# Compact Example Plugin

**Document ID:** EXAMPLE-PLUGIN-001  
**Version:** 2.0  
**Status:** Copyable API example; not compiled by Maven  
**Baseline date:** 3 August 2026

---

This directory is a compact example of the Version 2.0.0 plugin API,
registration properties and safe dry-run behaviour. It deliberately keeps helper
classes in one package so the reflection/configuration contract is easy to see.

For a production plugin, use the
[structural Java template](../../templates/plugin-java/README.md), which shows
the required five-stage package architecture.

## Use

1. Copy the Java package below `src/main/java`.
2. Copy `example.properties` below
   `src/main/resources/config/plugins`.
3. add `example` to `config/plugins/index.properties`;
4. replace the placeholder endpoint and transformation;
5. implement a real transactional repository before write mode;
6. add tests and SQL; and
7. register the plugin before ordinary execution.

The example repository deliberately throws in write mode. Dry run can exercise
configuration, extraction and transformation without reporting false database
success.
