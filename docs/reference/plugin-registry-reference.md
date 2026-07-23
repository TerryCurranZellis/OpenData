# Plugin Registry Reference

**Document ID:** REF-REGISTRY-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


Phase 1 uses `config/plugins/index.properties`:

```properties
plugins=ofgem,openmeteo
```

Each id has a matching properties file and matching `plugin.id`. `--list-plugins`
queries `PluginRegistry`; hard-coded listing text is prohibited.
