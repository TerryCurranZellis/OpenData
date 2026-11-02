# ADR-0059: Split the modular monolith into API, common, plugins, and core Maven modules

**Version:** 3.1.0  
**Status:** Superseded by ADR-0062
**Date:** 2026-09-18  
**Decision owners:** OpenData maintainers

---

## Context

OpenData now contains shared plugin contracts, reusable framework helpers, and
application/plugin runtime code with different change rates and future
extraction goals.

Bundled plugins are expected to become separate projects later, but they are not
ready to leave the current repository or release train yet.

## Decision

Keep one modular-monolith release and one Maven reactor, but split the source
into three modules at that time:

- `opendata-api` for shared plugin contracts, immutable plugin/configuration
  records, and database resource interfaces;
- `opendata-common` for reusable validation, JDBC helpers, base exceptions, and
  support strategies;
- `opendata-core` for the executable application, SQL Server resources, GUI,
  CLI, runtime orchestration, and the bundled plugins and their packaged
  definitions.

Bundled provider plugins live in `opendata-core` as `plugin.<id>` packages with
one shared `config/plugins/index.properties`. An earlier revision built each
plugin as a separate `opendata-plugin-*` module; that was withdrawn because
the plugin classes were not on the core classpath when registering a plugin
from a properties file, and because each module needed its own index.

This decision describes the Version 3.1.0 baseline only. ADR-0062 supersedes the
provider-placement decision by assigning each provider its own Maven project
under the `opendata-plugins` aggregator.

## Consequences

### Positive

- module responsibilities are explicit;
- future plugin extraction has a cleaner starting point;
- shared contracts can evolve independently from runtime wiring; and
- reusable helper code is separated from executable entry points.

### Negative or limiting

- one logical Java package namespace is now distributed across multiple Maven
  modules and requires disciplined documentation updates;
- module dependency direction must be reviewed carefully; and
- bundled plugins are now a dedicated module but still ship in the same executable.

## Rejected alternatives

### Keep all code in one module until plugin extraction

Rejected because the shared API/common boundaries already exist in the source
and need build, test, and documentation ownership now.

### Split each plugin into its own project immediately

Rejected because the provider modules are not yet ready for independent build,
release, and support boundaries.
