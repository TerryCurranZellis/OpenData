# Plugin Registry Reference

**Document ID:** REF-REGISTRY-001
**Version:** 2.0
**Status:** Version 2.0.0 implementation reference
**Baseline date:** 3 August 2026
**Minimum Java version:** 17

---

## Classpath resources

```text
src/main/resources/config/plugins/
    index.properties
    ofgem.properties
    openmeteo.properties
    octopus.properties
```

The index contains:

```properties
plugins=ofgem,openmeteo,octopus
```

Each id must have a corresponding definition and matching `plugin.id`.
`ClasspathPluginRegistry` uses the index for installed implementation metadata
and orders descriptors by id.

## Configuration-source distinction

The installed implementation registry remains classpath-backed. Plugin property
values can be loaded from packaged files or `core.plugin_property` through
`JdbcConfigurationPropertiesSource` after registration. Database-backed
properties do not create a dynamic plugin marketplace or load arbitrary classes.

## Listing plugins

```text
opendata --list-plugins
```

`opendata` denotes a classpath-aware launcher for
`com.towermarsh.opendata.OpenData`; the current POM does not produce a complete
executable/fat JAR. Output is tab-separated and supplied by the registry:

```text
ofgem       enabled    Ofgem Energy Price Cap
openmeteo   enabled    OpenMeteo Historical Weather
octopus     enabled    Octopus Energy Billing
```

## Adding a plugin

1. Add `config/plugins/<plugin-id>.properties`.
2. Add the id to `index.properties`.
3. Add the implementation class named by `plugin.implementation-class`.
4. Follow the plugin-local initialise/extract/transform/load/finalise packages.
5. Add registry, selection, dry-run and provider tests.
6. Apply any provider schema and least-privilege grants.
7. Update plugin, operator, reference and data-source documentation.
8. Re-run `--register` when database-backed properties are in use.
