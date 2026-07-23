# High-Level Architecture

**Document ID:** ARCH-003  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## Style

OpenData is a modular monolith: one Maven build and JVM process with explicit
package boundaries.

## Components

Application entry point, CLI, plugin registry, configuration service, plugin
implementation, download strategies, parsers, validation, ETL services,
repository and logging.

## Control flow

```text
Main -> CLI -> PluginRegistry -> ConfigurationService -> Plugin
     -> DownloadStrategy -> Parser -> Validator/Transformer -> Repository
```

The application layer coordinates but does not parse formats or issue
source-specific SQL. Plugins define source rules but reuse generic HTTP and
format handling.

See [component-architecture.puml](../diagrams/component-architecture.puml).
