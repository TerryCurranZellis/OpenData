# Ofgem Plugin Configuration Reference

**Document ID:** REF-CONFIG-OFGEM-001
**Version:** 3.0.0  
**Status:** Current active configuration
**Baseline date:** 15 August 2026  

---

## Endpoint

`endpoint.price-cap-publication.*` defines the official landing page, HTTP
headers and HTML-link discovery rules. The active configuration requires the
endpoint name `price-cap-publication`.

| Property | Packaged value | Used by active pipeline |
|---|---|---|
| `endpoint.price-cap-publication.url` | Ofgem Energy Price Cap page | yes |
| `endpoint.price-cap-publication.method` | `GET` | yes |
| `endpoint.price-cap-publication.strategy` | `html-link-discovery` | yes |
| link CSS selector | `a[href]` | yes |
| link href pattern | XLSX regex | yes |
| link text pattern | final levelised cap-rates model regex | yes |

## Typed properties

| Property | Default/packaged value | Meaning |
|---|---|---|
| `download.output-filename` | `ofgem-final-levelised-cap-rates.xlsx` | Local working filename |
| `download.connect-timeout` | `PT30S` | ISO-8601 connection timeout |
| `download.request-timeout` | `PT120S` | ISO-8601 request timeout |
| `archive.original-file` | `true` | Archive a successfully processed workbook |
| `download.working-directory` | `work/ofgem` | Active download directory |
| `archive.directory` | `archive/ofgem` | Archive root |

The properties `download.follow-redirects`, `excel.sheet-selection`,
`excel.evaluate-formulas`, `database.target-schema` and
`database.target-table` remain in the packaged definition, but the active
`initialise.OfgemConfiguration` does not read them. Do not describe them as
runtime controls until the implementation consumes them.

## Overrides

Single-plugin file:

```properties
property.download.working-directory.value=C:\OpenData\work\ofgem
property.archive.directory.value=C:\OpenData\archive\ofgem
property.download.request-timeout.value=PT180S
```

Multi-plugin file:

```properties
plugin.ofgem.property.download.working-directory.value=C:\OpenData\work\ofgem
plugin.ofgem.property.archive.directory.value=C:\OpenData\archive\ofgem
```

No API credential is required for the current public source.
