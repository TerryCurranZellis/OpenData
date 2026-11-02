# ADR-0062: Build each provider plugin as an independent Maven project

**Status:** Accepted  
**Date:** 2 November 2026  
**Decision owners:** OpenData maintainers  
**Version:** 3.1.0

---

## Context

The Version 3.1.0 build placed provider implementations, definitions, and SQL
resources in `opendata-core`. This coupled provider changes and tests to the
application and prevented each provider from being built and maintained as a
separate project.

## Decision

Create an `opendata-plugins` Maven aggregator. Each provider has its own project
with its implementation, tests, packaged definition, individual classpath
index, and registration SQL:

- Electric Demand
- Electric Supply
- Octopus
- Octopus Adjustment
- Ofgem
- OpenMeteo

Shared electricity/NESO helpers and shared Octopus parsing/types live in
dedicated support projects beneath the same aggregator. Provider projects
depend on `opendata-api`, `opendata-common`, and only the support projects and
external libraries they require. `opendata-core` depends on the six providers
included in the application, preserving the existing classpath discovery and
registration behavior without owning provider implementation code.

## Consequences

### Positive

- Provider implementations and tests have independent Maven project boundaries.
- Provider-specific dependencies, metadata, and SQL are owned by the provider.
- Shared support code does not force provider-to-provider dependencies.
- Existing classpath discovery works across provider JARs through one index
  resource per provider.

### Negative or limiting

- The root reactor includes nested provider and support projects.
- The packaged application still has explicit dependencies on the providers it
  ships; adding a provider requires updating the core dependencies.
- Provider indexes must remain unique and list only their owning plugin.

## Alternatives considered

### Keep all providers in `opendata-core`

Rejected because implementation and test ownership would remain coupled to the
application project.

### Discover arbitrary provider JARs without application dependencies

Deferred. Dynamic installation and deployment are separate from defining
independently buildable projects; the current runtime uses its classpath.
