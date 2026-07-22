# Update Instructions: `docs/architecture/007-plugin-architecture.md`

Add a section titled **Plugin Definition Contract**.

The Java plugin implementation provides specialised behaviour. Its endpoints,
formats, credentials, parser settings and target settings are represented by
the structured `PluginDefinition` record.

Each plugin receives an `ApplicationConfig` containing its parsed definition.
A plugin must not load its own properties file directly.
