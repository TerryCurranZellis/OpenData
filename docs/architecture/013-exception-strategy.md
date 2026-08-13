# Exception Strategy

**Document ID:** ARCH-013  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 24

---


Fail near the source, translate at boundaries, preserve causes, restore the
interrupt flag and omit secrets.

Focused exceptions include command-line, configuration/plugin definition,
download, import and validation failures. Parser/JDBC/HTTP exceptions do not
escape unchanged into the application layer.

The outer entry point maps failures to `ExecutionStatus`; the plugin coordinator
maps task outcomes to `PluginRunStatus`. Lower layers do not call `System.exit`.
The current `main` method logs the status but does not map it to an operating-
system exit code.

Messages identify plugin, endpoint or file without exposing credentials.
