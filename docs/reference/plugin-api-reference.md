# Plugin API Reference

**Document ID:** REF-PLUGIN-API-001  
**Version:** 3.1.0  
**Status:** Version 3.1.0 implementation reference  
**Baseline date:** 18 September 2026  
**Minimum Java version:** 24

---

The plugin API is compiled from the `opendata-api` module. Runtime registries,
selection, factories, and execution orchestration remain in `opendata-core`.

## `OpenDataPlugin`

Every executable plugin implements:

```java
PluginMetrics execute(PluginExecutionContext context) throws Exception;
```

The instance is created for one resolved plugin execution and must not expose
shared mutable run state.
