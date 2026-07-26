# Java plugin template

Copy the `example` package into `src/main/java/com/towermarsh/opendata/plugin`,
rename it to the new plugin identifier, and replace every `Example` symbol.

The template deliberately keeps provider-specific work below the plugin package:

- `download` acquires the source through shared download infrastructure;
- `config` converts the generic plugin definition into typed values;
- `extract` reads the source representation;
- `transform` converts extracted values into typed records;
- `transform.model` owns plugin domain records;
- `transform.validate` enforces cross-record rules;
- `load` owns transactional persistence and load counts;
- `ExamplePlugin` is the workflow facade used by the shared coordinator.

`ExampleLoader` intentionally throws until its transaction and SQL are
implemented. A new plugin should not silently report a successful write.

After copying the files, add the plugin properties resource, SQL migration,
tests, operator documentation, and registry configuration described in
[Adding a plugin](../../guides/adding-a-plugin.md).
