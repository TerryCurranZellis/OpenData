# Plugin Architecture

**Document ID:** ARCH-007  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## Contract

A plugin represents one dataset family. It provides identity, configuration
validation and source-specific transformation while reusing framework download,
parse and persistence services.

## Phase 1 definition and registry

Each plugin uses `config/plugins/<id>.properties`, parsed into a storage-neutral
`PluginDefinition`. An explicit `index.properties` lists installed ids because
classpath directory scanning is unreliable inside JARs.

## Rules

Ids are lowercase, stable and unique. Filename, index id and `plugin.id` match.
Disabled plugins may be listed but cannot run. Plugins do not read raw
`Properties`, implement their own CLI, duplicate generic HTTP/CSV/Excel logic or
store secrets.

Ofgem is the HTML-to-XLSX reference; OpenMeteo is the parameterised API
reference.

A database registry may later construct the same records from JSON, but that
work is shelved.

See [plugin-registry.puml](../diagrams/plugin-registry.puml).
