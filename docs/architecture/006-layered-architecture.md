# Layered Architecture

**Document ID:** ARCH-006  
**Version:** 4.0.0  
**Status:** 4.1.0 module-boundary implementation  
**Baseline date:** 10 November 2026  
**Minimum Java version:** 24

---

## Logical application layers

1. **Assembly:** `opendata-launcher` places Core, Common and all provider
   JARs on the runtime classpath. Nothing in those modules depends on the launcher.
2. **Presentation:** JavaFX GUI, first-run wizard and CLI command processing,
   all owned by Core.
3. **Application services:** registration, configuration, plugin selection,
   execution coordination and SQL Server provisioning, owned by Core.
4. **Contracts:** API plugin, persistence, exception and configuration types.
   This layer never depends on implementation modules.
5. **Provider execution:** six independent ETL implementations and provider
   support modules, which depend on API/Common but never Core.
6. **Infrastructure:** SQL Server, HTTP, filesystem, encrypted bootstrap and
   JUL logging, accessed through bounded execution/resource boundaries.

A provider receives `PluginExecutionContext` and required configuration, not
the Core application instance. It must not call the CLI, JavaFX or setup
implementation. Mutable `Workbook`, `CSVParser`, `Connection` and
`ResultSet` objects stay inside short resource scopes.

Typed immutable records cross provider stage boundaries. Generic helpers are
used only where they make behaviour clearer and preserve type safety.
