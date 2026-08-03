# ADR-0048: Persistent plugin registry and CLI lifecycle administration

- **Status:** Accepted and implemented
- **Date:** 2026-08-03
- **Decision owners:** OpenData maintainers

## Context

The packaged plugin index identified implementations available in the build but
could not represent operational installation or status. The command line also
lacked explicit register, unregister, enable and disable lifecycle operations for
one, several or all plugins.

The requested short option `-d` conflicted between disable and dry run.

## Decision

1. Keep `ClasspathPluginRegistry` as the packaged registration catalogue.
2. Add `JdbcPluginRegistry` backed by `core.plugin_registry` as the authoritative
   operational registry.
3. Store complete registered definitions in `core.plugin_property`.
4. Require `--plugin <id|all>` for register, unregister, enable, disable, run and
   dry run.
5. Permit repeated `--plugin` options and comma-separated compatibility input.
6. Permit `--file` only with `--register` and exactly one named plugin; the file
   is a complete UTF-8 plugin definition, not an invocation override.
7. Preserve existing enabled status when re-registering; use `plugin.enabled`
   only for a new registry row.
8. Make `--plugin all` select the packaged catalogue for registration and the
   persistent registry for lifecycle administration/execution.
9. Assign `-d` to `--disable` and `-n` to `--dry-run`; retain long
   `--dry-run` unchanged.
10. Keep `--parallelism` valid at 1-64 for all parsed commands but effective only
    during run/dry-run execution.

## Consequences

### Positive

- Installed and enabled state survives process restart.
- Operators can manage plugins without editing packaged resources.
- Named, repeated and all-plugin operations have one validation model.
- External definitions can be registered safely without becoming run-time
  overrides.
- Disabled plugins remain configured but cannot execute.

### Negative or limiting

- Registry administration and dry-run startup require SQL Server availability.
- Registering an implementation still requires its Java class in the runtime
  classpath.
- Existing installations must apply the `003a` migration and current grants.
- The `-n` short form differs from the originally requested duplicate `-d` form.

## Implementation evidence

- `cli.PluginCommand`, `CommandLineArguments` and processor;
- `plugin.JdbcPluginRegistry`;
- `config.PropertiesFileConfigurationPropertiesSource`;
- `config.ConfigurationRegistrationService`;
- `sql/003a-create-plugin-registry.sql` and grant/verification updates;
- CLI, registry and provider dry-run regression tests.

## Related decisions

This ADR extends ADR-0013 and ADR-0047. The classpath index remains accepted as a
catalogue; it is no longer the runtime system of record. It also updates the
implementation detail of ADR-0022 without changing the decision that CLI control
belongs at the application boundary.
