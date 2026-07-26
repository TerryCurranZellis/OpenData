# Configuration Reference

**Document ID:** REF-CONFIG-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


## Runtime sources

| Source | Purpose |
|---|---|
| `config/application.properties` | Database pool, executor and logging defaults |
| `config/plugins/index.properties` | Ordered installed-plugin ids |
| `config/plugins/<id>.properties` | Descriptor, endpoints and typed plugin properties |
| `--file <path>` | Application and plugin overrides for one invocation |

The legacy classpath `application.properties` is not loaded by
`ApplicationRuntimeConfiguration`.

## Precedence

1. classpath application or plugin resource;
2. entries from `--file`.

There is no current `--set` option and no environment-variable configuration
layer.

## Override scopes

Application values always use `application.<key>`:

```properties
application.database.password=...
application.execution.max-parallel-plugins=2
```

A single-plugin file may use unscoped plugin keys:

```properties
property.start-date.value=2025-01-01
```

A multi-plugin file must scope them:

```properties
plugin.openmeteo.property.start-date.value=2025-01-01
plugin.ofgem.property.download.request-timeout.value=PT180S
```

Unscoped plugin keys in a multi-plugin run are rejected. Key matching is
case-insensitive after normalisation.

## Security

The current classpath resource contains a development password, which is a
recorded defect rather than the intended deployment pattern. Use an external
override with restricted file permissions and remove the packaged value before
deployment.
