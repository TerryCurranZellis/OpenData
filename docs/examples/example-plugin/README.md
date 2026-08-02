# Example plugin template

This folder contains copyable templates for a new properties-based OpenData plugin. The files are documentation examples and are not compiled as part of the application.

## Use

1. Copy the Java package into `src/main/java/com/towermarsh/opendata/plugin/example`.
2. Copy `example.properties` into `src/main/resources/config/plugins`.
3. Add `example` to the comma-separated `plugins` value in `src/main/resources/config/plugins/index.properties`.
4. Replace the placeholder endpoint, transformation, validation and repository logic.
5. Add unit tests for configuration, transformation, persistence and dry-run behaviour.
6. Verify registration with `--list-plugins`, then run `--plugin example --dry-run`.

`ReflectionPluginFactory` first looks for a public constructor accepting `PluginDefinition`; keep that constructor unless the plugin genuinely needs no configuration.
