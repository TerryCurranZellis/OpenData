# Configuration Architecture

**Document ID:** ARCH-011  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## Categories

Bootstrap properties hold application startup settings. Plugin properties hold
identity, endpoints, parser/target settings and credential references. `--file`
provides invocation overrides.

```text
application.properties -> BootstrapConfig
plugin properties + overrides -> PluginDefinition
BootstrapConfig + PluginDefinition + flags -> ApplicationConfig
```

Keys are lowercase and dotted. Named structures use `endpoint.<name>.*`,
`property.<name>.*` and `credential.<name>.*`. Secrets are referenced, never
embedded.

Earlier flat configuration classes may coexist during migration, but new code
uses the record-based route.

Normalised database tables and JSON output are shelved until Phase 1 is stable.
