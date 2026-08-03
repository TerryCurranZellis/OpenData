# Run and Verify an Ofgem Price-Cap Import

**Document ID:** GUIDE-OFGEM-RUN-001  
**Version:** 2.0  
**Status:** Current operating procedure  
**Baseline date:** 3 August 2026

## Before running

- install the current SQL schema and grants;
- register and enable `ofgem`;
- verify outbound access to the publication page;
- run `mvn clean test`.

```text
opendata --plugin ofgem --register
opendata --plugin ofgem --dry-run
opendata --plugin ofgem
```

To modify the definition, re-register a complete file:

```text
opendata --plugin ofgem --register --file C:\OpenData\ofgem.properties
```

A dry run performs discovery, download and workbook extraction without archive,
provider tables or audit writes. A write run persists and archives according to
configuration.

Verify `core.PluginRun`, `core.ingestion_run`, `ofgem.price_cap_period`,
`ofgem.price_cap_level`, source hashes and representative worksheet/cell lineage.
