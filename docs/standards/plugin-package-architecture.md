# Plugin Package Architecture

Every plugin must use the following top-level packages beneath `com.towermarsh.opendata.plugin.<plugin-id>`:

- `initialise` — creates typed local configuration and controls the plugin flow.
- `extract` — downloads or obtains source data only and passes the downloaded representation onward.
- `transform` — parses, validates, normalises, and converts downloaded data into records. It may contain supporting packages such as `model` and `validate`.
- `load` — persists transformed records into the target database tables.
- `finalise` — executes cleanup and final reporting after the pipeline completes or fails.

The plugin root class is a thin framework entry point and delegates execution to its `initialise` class. Plugin packages must not define plugin-specific exception classes. Pipeline failures are converted to the common `PluginException` by `PluginExceptionHandler` in the framework plugin package.
