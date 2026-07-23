# Exception Strategy

**Document ID:** ARCH-013  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


Fail near the source, translate at boundaries, preserve causes, restore the
interrupt flag and omit secrets.

Focused exceptions include command-line, configuration/plugin definition,
download, import and validation failures. Parser/JDBC/HTTP exceptions do not
escape unchanged into the application layer.

Only the outer entry point maps failures to `ApplicationRunStatus` or process
exit behaviour. Lower layers do not call `System.exit`.

Messages identify plugin, endpoint or file without exposing credentials.
