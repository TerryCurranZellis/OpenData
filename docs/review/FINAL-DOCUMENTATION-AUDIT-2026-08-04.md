# Final Documentation Audit — Shared Processing Refactor

**Document ID:** REVIEW-DOC-AUDIT-2026-08-04
**Status:** Source documentation complete; local generated-output validation required
**Audit date:** 4 August 2026
**Scope:** Code Batches 0–3 and Documentation Batches 1–2

## Audit conclusion

The source documentation required to explain the shared validation and JDBC
processing refactor is complete. The material now describes both the
provider-neutral framework and each provider's adopted processing strategy.

Completion of this audit does not constitute production release acceptance.

## Coverage matrix

| Refactor area | Primary documentation | Result |
|---|---|---|
| Shared typed properties and rules | ARCH-028, ADR-0049, shared API reference | Covered |
| Transaction template and cleanup | ARCH-019/028, shared API reference | Covered |
| Batch executor | ARCH-028, Ofgem/OpenMeteo architecture | Covered |
| Typed upsert executor | ARCH-028, Octopus architecture/reference | Covered |
| Ofgem migration | ARCH-021, plugin/reference pages, diagram | Covered |
| OpenMeteo migration | ARCH-026, plugin/schema/reference pages, diagram | Covered |
| Octopus migration | ARCH-027, plugin/schema/reference pages, diagram | Covered |
| Deprecation and `@since` policy | ADR-0049, coding/plugin guidance and references | Covered |
| New-plugin template | Java plugin template README/configuration/loader | Covered |
| Release and traceability | Release 2.0.0, ARCH-024, current inventory | Covered |

## Required local checks

Run from the merged local branch:

```powershell
git diff --check
mvn clean verify
.\scripts\Invoke-Documentation.ps1 -Action Validate
.\scripts\Invoke-Documentation.ps1 -Action All -RenderDiagrams
```

Then review:

- all generated manual inventories;
- provider diagrams in DOCX and PDF output;
- heading and cross-reference validation;
- absence of `.puml` image links in Markdown;
- Javadoc deprecation warnings and `@since 2.0.0` coverage; and
- release evidence/checklist status.

## Remaining non-documentation gates

Live SQL Server installation, permissions, commit/rollback, repeat-load,
application-lock, pooled-session, archive, security and packaging evidence
remain outside this documentation audit and must stay open until tested.
