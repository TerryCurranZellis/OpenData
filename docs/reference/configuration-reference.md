# Configuration Reference

**Document ID:** REF-CONFIG-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


Bootstrap keys cover application name/environment and work/archive/failure paths.
Plugin properties plus overrides build a `PluginDefinition`; bootstrap,
definition and flags build `ApplicationConfig`.

Precedence: built-in/bootstrap defaults, application properties, plugin
properties, `--file`, then any future `--set` values.

New code uses record-based configuration. Earlier flat loaders are transitional.
