# Supported Data Formats

**Document ID:** REF-FORMAT-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


| Format | Status | Implementation |
|---|---|---|
| CSV | Supported | Apache Commons CSV |
| JSON | Supported | Jackson; used by OpenMeteo |
| XLS/XLSX | Supported | Apache POI; XLSX used by Ofgem |
| HTML link discovery | Supported | JSoup |
| HTML table | Planned | JSoup |
| ZIP | Planned | extraction stage |
| XML | Modelled | parser pending |

A landing page format may differ from the resolved downloadable format.
