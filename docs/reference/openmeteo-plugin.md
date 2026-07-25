# OpenMeteo Plugin Reference

**Document ID:** REF-PLUGIN-OPENMETEO-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


The reference plugin uses the Historical Weather API `/v1/archive`. Typical
parameters are latitude, longitude, start/end dates, daily variables and timezone.
These values belong in configuration/overrides, not Java constants. Jackson
parses JSON; CSV may be selected. The public endpoint normally requires no key.
