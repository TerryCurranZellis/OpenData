# Ofgem Plugin Reference

**Document ID:** REF-PLUGIN-OFGEM-001  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


Ofgem updates the price cap quarterly and publishes the final levelised cap rates
model as XLSX. The plugin downloads the stable publication page, discovers the
matching workbook link, archives the file, parses with POI and loads transformed
records. The public publication currently needs no API key.
