# Ofgem Plugin Reference

**Document ID:** REF-PLUGIN-OFGEM-001  
**Version:** 1.1  
**Status:** Baseline  
**Baseline date:** 26 July 2026  
**Minimum Java version:** 17

---


Ofgem updates the price cap quarterly and publishes the final levelised cap rates
model as XLSX. The plugin downloads the stable publication page, discovers the
matching workbook link and extracts annual values from `1a Levelised DTC` with
Apache POI.

Dry run stops after extraction. Write mode optionally archives the file, creates
Ofgem provenance rows and transactionally replaces the facts for the extracted
period. The public publication does not require an API key. Detailed component
values and historical backfill are not part of the current loader.
