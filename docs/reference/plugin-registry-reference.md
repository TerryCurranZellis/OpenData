# Plugin Registry Reference

**Document ID:** REF-REGISTRY-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---

## Classpath resources

```text
src/main/resources/config/plugins/
    index.properties
    ofgem.properties
    openmeteo.properties
```

The index contains:

```properties
plugins=ofgem,openmeteo
```

Each id must have a corresponding `<id>.properties` file, and its `plugin.id`
must match the indexed id. The definition also identifies its implementation
class. `ClasspathPluginRegistry` orders the resulting descriptors by id.

## Listing plugins

```text
opendata --list-plugins
```

`opendata` represents a classpath-aware launcher for
`com.towermarsh.opendata.Main`; the current Maven artifact is not an executable
JAR. Output is tab-separated:

```text
ofgem       enabled    Ofgem Energy Price Cap
openmeteo   enabled    OpenMeteo Historical Weather
```

The command queries `PluginRegistry`; hard-coded listing text is prohibited.

## Adding a plugin

1. Add `config/plugins/<plugin-id>.properties`.
2. Add the id to `config/plugins/index.properties`.
3. Add the Java implementation named by `plugin.implementation-class`.
4. Add registry, selection and plugin tests.
5. Update the plugin, user and operations documentation.

The explicit classpath index is the current accepted mechanism. A
database-backed registry remains shelved.
