# Update Instructions: `docs/architecture/010-component-interactions.md`

Add the following execution sequence.

1. `CommandLineArgumentsProcessor` parses user arguments.
2. `ServiceLoaderPluginRegistry` discovers installed plugins.
3. Informational requests such as `--list-plugins` are completed without loading
   dataset configuration.
4. For an execution request, `PluginApplicationService` resolves the selected
   plugin.
5. `ConfigurationService` loads and validates `ApplicationConfig`.
6. The plugin performs dataset-specific configuration validation.
7. The plugin starts its pipeline.
8. The application logs the `PluginExecutionResult`.
