# Supported Data Formats

**Document ID:** REF-FORMAT-001  
**Version:** 3.0.0  
**Status:** Version 3.0.0 implementation matrix  
**Baseline date:** 15 August 2026  
**Minimum Java version:** 24

---

| Format/capability | Model value | Executable support |
|---|---|---|
| CSV | `CSV` | Generic Apache Commons CSV parser |
| JSON | `JSON` | Generic flat-array parser; provider-specific Jackson models also used |
| XLS/XLSX | `XLS`, `XLSX` | Generic Apache POI parser and provider-specific workbook extractors |
| PDF | not in `DatasetFormat` | Provider-specific PDFBox extraction in Octopus |
| Static HTML link discovery | `HTML` + `HTML_LINK_DISCOVERY` | Implemented with Jsoup and Java HTTP client |
| Direct file download | `DIRECT_HTTP` | Implemented |
| HTML table extraction | `HTML_TABLE` | Modelled, no shared executable strategy |
| Authenticated API | `AUTHENTICATED_API` | Modelled, secret-provider/application boundary absent |
| Browser automation | `BROWSER_AUTOMATION` | Modelled, not implemented |
| XML | `XML` | Modelled, no generic parser |
| ZIP | `ZIP` | Modelled, no generic extraction stage |
| Text/binary | `TEXT`, `BINARY` | Modelled; provider-specific handling required |

`DataParserFactory` detects only `.csv`, `.json`, `.xls` and `.xlsx`. It maps
both Excel extensions to `ExcelDataParser`. PDF is deliberately provider
specific in the current implementation.

An enum value indicates that configuration can represent a format or strategy;
it does not prove that the runtime can execute it.
