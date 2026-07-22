# Update Instructions: `docs/architecture/011-configuration.md`

Add a section titled **Phase 1 Structured Configuration**.

Phase 1 stores each plugin definition in a classpath properties file. The file
is parsed immediately into immutable Java records. Plugin code consumes
`ApplicationConfig` and `PluginDefinition`, not raw properties.

`ApplicationConfig` now contains:

- `BootstrapConfig`;
- `PluginDefinition`;
- runtime overrides;
- the optional override file;
- dry-run and verbose execution flags.

This record-based boundary is deliberately storage independent. A future
database repository can return JSON and deserialize it into the same
`PluginDefinition` record.
