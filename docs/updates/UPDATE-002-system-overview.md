# Update Instructions: `docs/architecture/002-system-overview.md`

Add `Plugin Registry` as a major system component.

## Component Responsibility

The Plugin Registry discovers installed dataset plugins, validates unique plugin
identifiers, supports plugin listing and resolves the plugin requested by the
command line.

## Interaction Summary

```text
Command Line
    -> Plugin Registry
    -> Configuration Service
    -> Selected Plugin
    -> Pipeline Engine
```

The framework core remains independent of dataset-specific implementations.
