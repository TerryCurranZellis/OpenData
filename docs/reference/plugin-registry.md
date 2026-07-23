# Plugin Registry Reference

## Phase 1 Resources

```text
src/main/resources/config/plugins/
├── index.properties
├── ofgem.properties
└── openmeteo.properties
```

The index contains:

```properties
plugins=ofgem,openmeteo
```

Each id must have a corresponding `<id>.properties` file, and its `plugin.id`
must match the indexed id.

## Listing Plugins

```powershell
java -jar target/opendata-1.0.0.jar --list-plugins
```

Expected output includes both plugins:

```text
Installed OpenData plugins:
  ofgem              enabled    Ofgem Energy Price Cap
  openmeteo          enabled    OpenMeteo Historical Weather
```

## Adding a Plugin

1. Add `config/plugins/<plugin-id>.properties`.
2. Add the id to `config/plugins/index.properties`.
3. Add the Java plugin implementation.
4. Add a registry test assertion.
5. Update plugin documentation.

The explicit index will later be replaced by a database-backed registry.
