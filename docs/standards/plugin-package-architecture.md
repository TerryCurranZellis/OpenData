# Plugin Package Architecture

**Document ID:** STD-PLUGIN-PACKAGE-001  
**Version:** 2.0  
**Status:** Version 2.0.0 target package standard  
**Baseline date:** 3 August 2026  
**Minimum Java version:** 17

---

Every provider plugin belongs below:

```text
com.towermarsh.opendata.plugin.<plugin-id>
```

![Plugin development lifecycle](../diagrams/generated/plugin-development-lifecycle.svg)

## Required ownership

| Location | Responsibility |
|---|---|
| plugin root | Thin `OpenDataPlugin` facade and stable plugin ID |
| `initialise` | Typed configuration and complete stage orchestration |
| `extract` | Obtain source files/responses and decode source representation |
| `transform` | Convert extracted values into provider domain records |
| `transform.model` | Immutable provider records |
| `transform.validate` | Response and cross-record validation |
| `load` | SQL, transaction ownership, idempotency and load counts |
| `finalise` | Cleanup, archive/post-processing and final reporting |

The root class exposes a public constructor accepting
`com.towermarsh.opendata.config.model.PluginDefinition` because
`ReflectionPluginFactory` tries that constructor first. A public no-argument
constructor is only appropriate for a genuinely configuration-free plugin.

## Stage rules

- `initialise` controls stage order and calls `finalise` from a `finally` block
  when cleanup must occur after both success and failure.
- `extract` may use shared download and parser infrastructure but owns
  provider-specific source interpretation.
- `transform` has no database side effects.
- `load` is the only stage that writes provider data.
- `finalise` must not conceal the primary failure. If archive movement follows a
  committed database transaction, the operational consequence must be
  documented.
- Dry run returns accurate read/skipped metrics without invoking load or any
  other persistent side effect.

## Dependencies

Provider packages may depend on shared framework packages. Shared framework
packages must not depend on a provider. One provider plugin must not directly
depend on another provider plugin.

## Exceptions

Plugins do not create an unrelated parallel exception hierarchy. Provider code
may throw meaningful Java or shared OpenData exceptions; the framework
`PluginExceptionHandler` adds plugin identity at the execution boundary.

## Current compatibility code

Provider-local helper packages such as `config` or `download` MAY be used when
they have one clear owner and are called from the five-stage lifecycle. Do not
maintain duplicate active implementations in both a helper package and a stage
package. The Version 2.0.0 source still contains some such compatibility
duplicates; new work must not extend that debt.
