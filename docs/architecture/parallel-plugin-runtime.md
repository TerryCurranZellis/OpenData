# Parallel Plugin Runtime

**Document ID:** ARCH-PARALLEL-RUNTIME-001  
**Version:** 2.0  
**Status:** Implemented  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 24

---

The runtime accepts one or more plugin ids. Each selected enabled registered
plugin becomes one independent task with a fresh implementation instance, unique
run id, immutable definition and access to the shared pool (or unavailable data
resource during dry run).

## Selection rules

- `--plugin openmeteo` selects one enabled registered plugin.
- repeated options or comma-separated values select several plugins.
- `--plugin all` selects every enabled row in `core.plugin_registry`.
- duplicate ids and `all` mixed with named ids are rejected.
- disabled or unregistered named plugins are rejected.

`--parallelism` accepts 1-64. The effective worker count is the lower of selected
plugin count and requested/configured parallelism. It is accepted but ignored for
administration commands.

A failure in one task does not cancel unrelated plugins. Results are reported in
selection order and the invocation succeeds only when every selected task is
successful or a successful dry run.

Java 24 uses a bounded platform-thread executor. Repositories own transactions
and connections; plugin instances must not share mutable execution state.
