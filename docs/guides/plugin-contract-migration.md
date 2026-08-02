# Migrating an existing plugin to concurrent execution

Each enabled plugin must implement the thread-confined `OpenDataPlugin` contract:

```java
public final class ExamplePlugin implements OpenDataPlugin {
    private final ExampleConfiguration configuration;

    public ExamplePlugin(final PluginDefinition definition) {
        this.configuration = ExampleConfiguration.from(definition);
    }

    @Override
    public PluginMetrics execute(final PluginExecutionContext context) throws Exception {
        final var records = download(configuration);
        if (context.dryRun()) {
            return new PluginMetrics(records.size(), 0, 0, records.size());
        }
        final var result = new ExampleRepository(context.database()).save(
                records,
                context.runId());
        return new PluginMetrics(
                records.size(),
                result.inserted(),
                result.updated(),
                result.skipped());
    }
}
```

## Rules

1. Keep plugin state immutable and confined to one execution object.
2. Do not use static mutable fields for run state.
3. Borrow a pooled JDBC connection inside each repository operation; never retain it on the plugin.
4. Own and finish the transaction in the repository that borrowed the connection.
5. Use database constraints and transaction-scoped locks for shared rows rather than JVM-wide `synchronized` blocks.
6. Honour interruption and restore the interrupted flag when catching `InterruptedException`.
7. Return accurate read, insert, update and skip counts.
8. Do not log passwords, tokens, complete connection strings containing secrets, or sensitive plugin properties.

## Existing download-only entry points

A legacy method such as `execute()` or `download()` can remain for tests and library callers. The coordinator only invokes `execute(PluginExecutionContext)`.

## Database concurrency

Independent plugins normally require no mutual exclusion. Add a narrow SQL Server application lock only when two runs can alter the same logical dataset. Include the plugin id and stable dataset key in the lock resource, use `LockOwner='Transaction'`, and keep the transaction short.

## Registry

After adapting the class, ensure its plugin properties name the implementation and that the id appears in `src/main/resources/config/plugins/index.properties`. `--plugin all` includes only enabled descriptors.
