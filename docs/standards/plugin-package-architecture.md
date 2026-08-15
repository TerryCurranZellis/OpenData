# Plugin Package Architecture

**Document ID:** STD-PLUGIN-PACKAGE-001
**Version:** 3.0.0  
**Status:** Version 3.0.0 implementation standard
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

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
| `transform.validate` | Provider response and cross-record validation |
| `load` | Provider SQL, natural keys, idempotency, locks and load counts |
| `finalise` | Cleanup, archive/post-processing and final reporting |

The root class exposes a public constructor accepting
`com.towermarsh.opendata.config.model.PluginDefinition` because
`ReflectionPluginFactory` tries that constructor first. A public no-argument
constructor is only appropriate for a genuinely configuration-free plugin.

## Shared framework ownership

Provider packages use, rather than duplicate, framework mechanics:

| Shared package | Permitted responsibility |
|---|---|
| `validation` | typed property conversion, reusable value rules and safe SQL identifiers |
| `database.jdbc` | transaction, cleanup, batching and typed upsert execution |
| `download` and parser packages | provider-neutral source acquisition and format decoding |
| `plugin` | registry, execution context, audit, metrics and lifecycle coordination |

A provider must not create local general-purpose string, number, boolean, date,
duration or path parsers when the shared validation package already provides the
behaviour. Provider-specific formats may use `ValueParser<T>`.

The `load` package owns provider SQL and policy but normally delegates connection
and transaction mechanics to `JdbcTransactionTemplate`. It may use
`JdbcBatchExecutor` or typed `JdbcUpsertAdapter` implementations when those
patterns match the data. Set-based and replacement repositories remain explicit.

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
- dry run returns accurate read/skipped metrics without invoking load or any
  other persistent side effect.

## Dependencies

Provider packages may depend on shared framework packages. Shared framework
packages must not depend on a provider. One provider plugin must not directly
depend on another provider plugin.

Shared JDBC classes must not infer provider table names, columns, natural keys or
mappings from reflection. Configured SQL identifiers must pass
`SqlIdentifiers`; data values use prepared-statement parameters.

## Public API lifecycle

Public types and methods introduced or materially adjusted for Version 3.0.0
include Javadoc `@since 2.0.0`.

A retained obsolete public procedure uses both Java `@Deprecated` and Javadoc
`@deprecated`, with a named replacement. Private helpers with no external
callers are removed rather than retained as deprecated dead code.

## Exceptions

Plugins do not create an unrelated parallel exception hierarchy. Provider code
may throw meaningful Java or shared OpenData exceptions; the framework
`PluginExceptionHandler` adds plugin identity at the execution boundary.

## Compatibility code

Provider-local helper packages such as `config` or `download` MAY be used when
they have one clear owner and are called from the lifecycle. Do not maintain
duplicate active implementations in both a helper package and a stage package.
New work must not extend compatibility debt.
