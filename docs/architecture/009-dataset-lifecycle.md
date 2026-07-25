# Dataset Lifecycle

**Document ID:** ARCH-009  
**Version:** 1.0  
**Status:** Baseline  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## States

`REQUESTED`, `CONFIGURED`, `SOURCE_RESOLVED`, `DOWNLOADING`, `DOWNLOADED`,
`ARCHIVED`, `PARSED`, `VALIDATED`, `TRANSFORMED`, `LOADED`, `VERIFIED`,
`COMPLETED` and `FAILED`.

Each stage produces an artefact or result: `ApplicationConfig`, resolved URI,
raw file, archive copy, parsed rows, validation result, target rows, import result
and final run summary.

Archived inputs support reprocessing without another network request. Each run
should record software and plugin configuration versions. Dataset-specific keys,
release dates or checksums support idempotency.

See [dataset-lifecycle.puml](../diagrams/dataset-lifecycle.puml).
