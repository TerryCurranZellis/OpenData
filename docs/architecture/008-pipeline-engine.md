# Pipeline Engine

**Document ID:** ARCH-008  
**Version:** 1.0  
**Status:** Partial  
**Baseline date:** 23 July 2026  
**Minimum Java version:** 17

---


## Standard stages

Resolve configuration; resolve credentials; select endpoint; download; archive;
extract if needed; select parser; parse; validate; transform; load; verify; record
statistics and outcome.

`ExtractService`, `TransformService` and `LoadService` provide initial boundaries.
Complete step sequencing, context, rollback and execution history remain partial.

A future `PipelineContext` carries execution id, configuration, paths, endpoint,
artefacts, parser output, validation results, statistics and timings.

A failed stage stops later stages unless explicitly recoverable. Database loads
are transactional and raw artefacts remain available for diagnosis.

See [pipeline-sequence.puml](../diagrams/pipeline-sequence.puml).
